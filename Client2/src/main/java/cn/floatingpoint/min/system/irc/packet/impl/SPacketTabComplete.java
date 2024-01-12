package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class SPacketTabComplete implements Packet<INetHandlerClient> {
    private String[] matches;

    public SPacketTabComplete()
    {
    }

    public SPacketTabComplete(String[] matchesIn)
    {
        this.matches = matchesIn;
    }


    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.matches = new String[buf.readVarIntFromBuffer()];

        for (int i = 0; i < this.matches.length; ++i)
        {
            this.matches[i] = buf.readStringFromBuffer(32767);
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
    }

    @Override
    public void processPacket(INetHandlerClient handler) {
        handler.handleTabComplete(this);
    }

    public String[] getMatches() {
        return matches;
    }
}
