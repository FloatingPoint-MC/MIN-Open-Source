package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class SPacketChat implements Packet<INetHandlerClient> {
    private String username;
    private int rank;
    private String message;

    public String getUsername() {
        return username;
    }

    public int getRank() {
        return rank;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.username = buf.readStringFromBuffer(16);
        this.rank = buf.readVarIntFromBuffer();
        this.message = buf.readStringFromBuffer(127);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
    }

    @Override
    public void processPacket(INetHandlerClient handler) {
        handler.handleChat(this);
    }
}
