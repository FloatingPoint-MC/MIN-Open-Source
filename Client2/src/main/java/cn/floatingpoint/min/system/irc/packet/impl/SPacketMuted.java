package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class SPacketMuted implements Packet<INetHandlerClient> {
    private String reason;
    private long duration;

    public String getReason() {
        return reason;
    }

    public long getDuration() {
        return duration;
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf
     */
    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        reason = buf.readStringFromBuffer(127);
        duration = buf.readLong();
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param buf
     */
    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {

    }

    @Override
    public void processPacket(INetHandlerClient handler) {
        handler.handleMuted(this);
    }
}
