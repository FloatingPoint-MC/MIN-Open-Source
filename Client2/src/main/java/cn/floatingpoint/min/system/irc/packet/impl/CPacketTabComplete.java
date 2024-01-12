package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class CPacketTabComplete implements Packet<INetHandlerServer> {
    private String message;

    public CPacketTabComplete()
    {
    }

    public CPacketTabComplete(String msg)
    {
        this.message = msg;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {

    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(StringUtils.substring(this.message, 0, 32767));
    }

    @Override
    public void processPacket(INetHandlerServer handler) {
        handler.handleTabComplete(this);
    }

    public String getMessage() {
        return message;
    }
}
