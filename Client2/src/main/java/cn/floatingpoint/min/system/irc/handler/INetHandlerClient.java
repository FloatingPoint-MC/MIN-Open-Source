package cn.floatingpoint.min.system.irc.handler;

import cn.floatingpoint.min.system.irc.packet.impl.*;

public interface INetHandlerClient extends INetHandler {
    void handleChat(SPacketChat packetIn);

    void handleDisconnect(SPacketDisconnect packetIn);

    void handleAccount(SPacketAccount packetIn);

    void handleKey(SPacketKey packetIn);

    void handleTabComplete(SPacketTabComplete packetIn);

    void handlePlayer(SPacketPlayer packetIn);
}
