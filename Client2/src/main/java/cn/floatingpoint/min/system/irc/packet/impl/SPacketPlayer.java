package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;
import java.util.UUID;

public class SPacketPlayer implements Packet<INetHandlerClient> {
    private UUID uniqueId;
    private int rank;

    public SPacketPlayer() {}

    public SPacketPlayer(UUID uniqueId, int rank) {
        this.uniqueId = uniqueId;
        this.rank = rank;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public int getRank() {
        return rank;
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf
     */
    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.uniqueId = buf.readUniqueId();
        this.rank = buf.readVarIntFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param buf
     */
    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeUniqueId(this.uniqueId);
        buf.writeVarIntToBuffer(this.rank);
    }

    @Override
    public void processPacket(INetHandlerClient handler) {
        handler.handlePlayer(this);
    }
}
