package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

public class SPacketDisconnect implements Packet<INetHandlerClient> {
    private EnumDisconnectType type;
    private String reason;

    @Override
    public void readPacketData(PacketBuffer buf) {
        this.type = buf.readEnumValue(EnumDisconnectType.class);
        this.reason = buf.readStringFromBuffer(127);
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
    }

    @Override
    public void processPacket(INetHandlerClient handler) {
        handler.handleDisconnect(this);
    }

    public EnumDisconnectType getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public enum EnumDisconnectType {
        FORCE_KICK,
        FORCE_BAN
    }
}
