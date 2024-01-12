package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;
import java.util.UUID;

public class CPacketPlayer implements Packet<INetHandlerServer> {
    private UUID uniqueId;

    public CPacketPlayer() {}

    public CPacketPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf
     */
    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.uniqueId = buf.readUniqueId();
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param buf
     */
    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeUniqueId(this.uniqueId);
    }

    @Override
    public void processPacket(INetHandlerServer handler) {
        handler.handlePlayer(this);
    }
}
