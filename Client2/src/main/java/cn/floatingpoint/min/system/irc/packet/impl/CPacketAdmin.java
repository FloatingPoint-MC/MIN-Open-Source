package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class CPacketAdmin implements Packet<INetHandlerServer> {
    private Action action;
    private String username;
    private long duration;
    private String reason;

    public CPacketAdmin() {
    }

    public CPacketAdmin(Action action, String username, long duration, String reason) {
        this.action = action;
        this.username = username;
        this.duration = duration;
        this.reason = reason;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.action);
        buf.writeString(this.username);
        buf.writeLong(this.duration);
        buf.writeString(this.reason);
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
