package cn.floatingpoint.min.threads;

import cn.floatingpoint.min.system.replay.packet.C2SPacket;
import cn.floatingpoint.min.system.replay.packet.S2CPacket;
import cn.floatingpoint.min.system.replay.recording.Recording;
import cn.floatingpoint.min.utils.client.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import java.util.LinkedHashSet;

public class ReplayRecordingThread extends Thread {
    public final LinkedHashSet<Pair<Packet<?>, EnumPacketDirection>> toAdd;
    public final LinkedHashSet<Pair<Packet<?>, EnumPacketDirection>> toAddTemp;
    private final Recording recording;
    private boolean lock;

    public ReplayRecordingThread(Recording recording) {
        super("Replay Recording Thread " + Integer.toHexString(recording.hashCode()));
        this.recording = recording;
        toAdd = new LinkedHashSet<>();
        toAddTemp = new LinkedHashSet<>();
        lock = false;
    }

    @SuppressWarnings("all")
    @Override
    public void run() {
        while (Minecraft.getMinecraft().running) {
            lock = true;
            toAdd.forEach(pair -> {
                try {
                    int packetId = EnumConnectionState.PLAY.getPacketId(pair.getValue(), pair.getKey());
                    PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
                    packetBuffer.writeVarInt(packetId);
                    pair.getKey().writePacketData(packetBuffer);
                    switch (pair.getValue()) {
                        case SERVERBOUND -> recording.packets.add(new C2SPacket(packetBuffer));
                        case CLIENTBOUND -> recording.packets.add(new S2CPacket(packetBuffer));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            toAdd.clear();
            lock = false;
        }
    }

    public void addPacket(Packet<?> packet, EnumPacketDirection side) {
        if (lock) {
            toAddTemp.add(new Pair<>(packet, side));
        } else {
            if (!toAddTemp.isEmpty()) {
                toAdd.addAll(toAddTemp);
            }
            toAdd.add(new Pair<>(packet, side));
        }
    }
}
