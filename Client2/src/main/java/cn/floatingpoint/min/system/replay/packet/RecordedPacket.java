package cn.floatingpoint.min.system.replay.packet;

import net.minecraft.network.PacketBuffer;

public interface RecordedPacket {
    int tick();

    int packetId();

    PacketBuffer packetBuffer();
}
