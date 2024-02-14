package cn.floatingpoint.min.system.replay.packet;

import net.minecraft.network.PacketBuffer;

public class C2SPacket implements RecordedPacket {
    private final PacketBuffer packetBuffer;

    public C2SPacket(PacketBuffer packetBuffer) {
        this.packetBuffer = packetBuffer;
    }

    @Override
    public PacketBuffer getPacketBuffer() {
        return packetBuffer;
    }
}
