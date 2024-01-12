package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class SPacketKey implements Packet<INetHandlerClient> {
    private byte[] key;

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.key = buf.readByteArray();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
    }

    @Override
    public void processPacket(INetHandlerClient handler) {
        handler.handleKey(this);
    }

    public byte[] getKey() {
        return key;
    }
}
