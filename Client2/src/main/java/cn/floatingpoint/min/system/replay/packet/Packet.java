package cn.floatingpoint.min.system.replay.packet;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.PacketBuffer;

public abstract class Packet {
    private final EnumPacketDirection side;
    private final int id;
    private final PacketBuffer packetBuffer;

    public Packet(EnumPacketDirection side, int id, PacketBuffer packetBuffer) {
        this.side = side;
        this.id = id;
        this.packetBuffer = new PacketBuffer(packetBuffer.copy());

    }

    public EnumPacketDirection getSide() {
        return side;
    }

    public int getId() {
        return id;
    }

    public PacketBuffer getPacketBuffer() {
        return packetBuffer;
    }
}
