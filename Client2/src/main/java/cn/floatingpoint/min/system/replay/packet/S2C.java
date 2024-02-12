package cn.floatingpoint.min.system.replay.packet;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.PacketBuffer;

public class S2C extends Packet {
    public S2C(int id, PacketBuffer packetBuffer) {
        super(EnumPacketDirection.SERVERBOUND, id, packetBuffer);
    }
}
