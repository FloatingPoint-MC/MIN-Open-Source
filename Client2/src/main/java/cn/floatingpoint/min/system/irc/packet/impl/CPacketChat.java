package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class CPacketChat implements Packet<INetHandlerServer> {
    private String message;

    public CPacketChat() {
    }

    public CPacketChat(String message) {
        this.message = message;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(this.message);
    }

    @Override
    public void processPacket(INetHandlerServer handler) {
        handler.handleChat(this);
    }

    public String getMessage() {
        return message;
    }
}
