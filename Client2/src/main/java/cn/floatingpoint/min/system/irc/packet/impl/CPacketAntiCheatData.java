package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;
import cn.floatingpoint.min.utils.client.Pair;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @projectName: MINPrivate
 * @author: vlouboos
 * @date: 2024-02-08 21:21:46
 */
public class CPacketAntiCheatData implements Packet<INetHandlerServer> {
    private int checkId;
    private ArrayList<Pair<Type, Object>> data;

    public CPacketAntiCheatData() {
    }

    public CPacketAntiCheatData(int checkId, ArrayList<Pair<Type, Object>> data) {
        this.checkId = checkId;
        this.data = data;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {

    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(checkId);
        buf.writeVarIntToBuffer(data.size());
        for (Pair<Type, Object> data : this.data) {
            Type type = data.getKey();
            buf.writeEnumValue(type);
            switch (type) {
                case INT -> buf.writeInt((int) data.getValue());
                case FLOAT -> buf.writeFloat((float) data.getValue());
                case DOUBLE -> buf.writeDouble((double) data.getValue());
                case BOOLEAN -> buf.writeBoolean((boolean) data.getValue());
                case STRING -> buf.writeString((String) data.getValue());
            }
        }
    }

    @Override
    public void processPacket(INetHandlerServer handler) {
        handler.handleAntiCheatData(this);
    }

    public enum Type {
        INT,
        FLOAT,
        DOUBLE,
        BOOLEAN,
        STRING
    }
}
