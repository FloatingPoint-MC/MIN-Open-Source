package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class SPacketHandshake implements Packet<INetHandlerClient> {
    private String vexViewData;
    private byte[] modList;

    public String getVexViewData() {
        return vexViewData;
    }

    public byte[] getModList() {
        return modList;
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf
     */
    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        vexViewData = buf.readStringFromBuffer(32767);
        modList = buf.readByteArray();
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
        handler.handleHandshake(this);
    }
}
