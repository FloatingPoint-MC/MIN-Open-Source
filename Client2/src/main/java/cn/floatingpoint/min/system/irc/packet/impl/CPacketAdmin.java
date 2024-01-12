package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class CPacketAdmin implements Packet<INetHandlerServer> {
    private Action action;
    private String username;

    public CPacketAdmin() {
    }

    public CPacketAdmin(Action action, String username) {
        this.action = action;
        this.username = username;
    }

    public Action getAction() {
        return action;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.action);
        buf.writeString(this.username);
    }

    @Override
    public void processPacket(INetHandlerServer handler) {
        handler.handleAdmin(this);
    }

    public enum Action {
        BAN,
        UNBAN,
        MUTE,
        UNMUTE,
        KICK,
        LIST
    }
}
