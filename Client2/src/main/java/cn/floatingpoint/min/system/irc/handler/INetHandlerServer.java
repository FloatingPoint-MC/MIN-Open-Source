package cn.floatingpoint.min.system.irc.handler;

import cn.floatingpoint.min.system.irc.packet.impl.*;

public interface INetHandlerServer extends INetHandler {
    void handleAdmin(CPacketAdmin packetIn);

    void handleChat(CPacketChat packetIn);

    void handleDisconnect(CPacketDisconnect packetIn);

    void handleHandshake(CPacketHandshake packetIn);

    void handleLogin(CPacketLogin packetIn);

    void handleLogout(CPacketLogout packetIn);

    void handleRegister(CPacketRegister packetIn);

    void handleKey(CPacketKey packetIn);

    void handleTabComplete(CPacketTabComplete packetIn);

    void handlePlayer(CPacketPlayer packetIn);

    void handleJoinServer(CPacketJoinServer packetIn);

    void handleMuted(SPacketMuted packetIn);
}
