package cn.floatingpoint.min.system.replay.packet;

import net.minecraft.network.PacketBuffer;

public record C2SPacket(int tick, int packetId, PacketBuffer packetBuffer) implements RecordedPacket {
}
