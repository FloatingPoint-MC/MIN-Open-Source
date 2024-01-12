package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

public class CPacketDisconnect implements Packet<INetHandlerServer> {
    @Override
    public void readPacketData(PacketBuffer buf) {

    }

    @Override
    public void writePacketData(PacketBuffer buf) {

    }

    @Override
    public void processPacket(INetHandlerServer handler) {
        handler.handleDisconnect(this);
    }
}
