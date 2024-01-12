package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class CPacketHandshake implements Packet<INetHandlerServer> {
    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {

    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
    }

    @Override
    public void processPacket(INetHandlerServer handler) {
        handler.handleHandshake(this);
    }
}
