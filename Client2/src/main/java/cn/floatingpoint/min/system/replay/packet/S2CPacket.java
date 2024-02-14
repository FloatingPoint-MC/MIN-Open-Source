package cn.floatingpoint.min.system.replay.packet;

import net.minecraft.network.PacketBuffer;

public class S2CPacket implements RecordedPacket {
    private final PacketBuffer packetBuffer;

    public S2CPacket(PacketBuffer packetBuffer) {
        this.packetBuffer = packetBuffer;
    }

    @Override
    public PacketBuffer getPacketBuffer() {
        return packetBuffer;
    }
}
