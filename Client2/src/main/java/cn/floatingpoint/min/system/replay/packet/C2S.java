package cn.floatingpoint.min.system.replay.packet;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.PacketBuffer;

public class C2S extends Packet {
    public C2S(int id, PacketBuffer packetBuffer) {
        super(EnumPacketDirection.SERVERBOUND, id, packetBuffer);
    }
}
