package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;
import java.util.UUID;

public class SPacketPlayer implements Packet<INetHandlerClient> {
    private UUID uniqueId;
    private String skinName;
    private UUID skinId;
    private int rank;
    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getSkinName() {
        return skinName;
    }

    public UUID getSkinId() {
        return skinId;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.uniqueId = buf.readUniqueId();
        this.skinName = buf.readStringFromBuffer(16);
        this.skinId = buf.readUniqueId();
        this.rank = buf.readVarIntFromBuffer();
    }
    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
    }

    @Override
    public void processPacket(INetHandlerClient handler) {
        handler.handlePlayer(this);
    }
}
