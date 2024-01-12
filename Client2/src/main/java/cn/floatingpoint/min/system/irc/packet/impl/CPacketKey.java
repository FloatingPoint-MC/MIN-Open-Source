package cn.floatingpoint.min.system.irc.packet.impl;
import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class CPacketKey implements Packet<INetHandlerServer> {
    private byte[] key;

    public CPacketKey() {
    }

    public CPacketKey(byte[] key) {
        this.key = key;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeByteArray(this.key);
    }

    @Override
    public void processPacket(INetHandlerServer handler) {
        handler.handleKey(this);
    }
}
