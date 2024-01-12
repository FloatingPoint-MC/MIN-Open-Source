package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerServer;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;
import cn.floatingpoint.min.utils.math.Sha256Util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CPacketRegister implements Packet<INetHandlerServer> {
    private String username;
    private String password;
    private String code;

    public CPacketRegister() {
    }

    public CPacketRegister(String username, String password, String hwid) {
        this.username = username;
        this.password = password;
        this.code = hwid;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(this.username);
        buf.writeString(Sha256Util.encode(this.password.getBytes(StandardCharsets.UTF_8)));
        buf.writeString(this.code);
    }

    @Override
    public void processPacket(INetHandlerServer handler) {
        handler.handleRegister(this);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCode() {
        return code;
    }
}
