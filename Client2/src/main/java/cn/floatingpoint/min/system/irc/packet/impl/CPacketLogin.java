package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;
import cn.floatingpoint.min.utils.math.Sha256Util;

import java.nio.charset.StandardCharsets;

public class CPacketLogin implements Packet<INetHandlerServer> {
    private String username;
    private String password;
    private String hwid;

    public CPacketLogin() {}

    public CPacketLogin(String username, String password, String hwid) {
        this.username = username;
        this.password = password;
        this.hwid = hwid;
    }

    @Override
    public void readPacketData(PacketBuffer buf) {
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
        buf.writeString(this.username);
        buf.writeString(Sha256Util.encode(this.password.getBytes(StandardCharsets.UTF_8)));
        buf.writeString(this.hwid);
    }

    @Override
    public void processPacket(INetHandlerServer handler) {
        handler.handleLogin(this);
    }
}
