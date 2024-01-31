package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class CPacketSkin implements Packet<INetHandlerServer> {
    private Action action;
    private String context;

    public CPacketSkin() {
    }

    public CPacketSkin(Action action, String context) {
        this.action = action;
        this.context = context;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        action = buf.readEnumValue(Action.class);
        context = buf.readStringFromBuffer(256);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeEnumValue(action);
        buf.writeString(context);
    }

    @Override
    public void processPacket(INetHandlerServer handler) {

    }

    public enum Action {
        BUY,
        CHANGE,
    }
}
