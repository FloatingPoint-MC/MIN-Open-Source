package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class SPacketVersion implements Packet<INetHandlerClient> {
    private String version;
    public String getVersion() {
        return version;
    }
    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        version = buf.readStringFromBuffer(8);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(version);
    }

    @Override
    public void processPacket(INetHandlerClient handler) {
        handler.handleVersion(this);
    }
}
