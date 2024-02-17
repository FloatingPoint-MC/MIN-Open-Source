package cn.floatingpoint.min.system.replay.packet;

import net.minecraft.network.PacketBuffer;

public record ChunkPacket(PacketBuffer packetBuffer) implements RecordedPacket {

    @Override
    public int tick() {
        return -1;
    }

    @Override
    public int packetId() {
        return -1;
    }
}
