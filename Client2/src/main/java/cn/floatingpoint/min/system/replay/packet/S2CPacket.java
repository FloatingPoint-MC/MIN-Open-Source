package cn.floatingpoint.min.system.replay.packet;

import net.minecraft.network.PacketBuffer;

public record S2CPacket(int tick, int packetId, PacketBuffer packetBuffer) implements RecordedPacket {
}
