package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

public class SPacketAccount implements Packet<INetHandlerClient> {
    private String username;
    private Status status;

    @Override
    public void readPacketData(PacketBuffer buf) {
        this.username = buf.readStringFromBuffer(16);
        this.status = buf.readEnumValue(Status.class);
    }

    @Override
    public void writePacketData(PacketBuffer buf) {
    }

    @Override
    public void processPacket(INetHandlerClient handler) {
        handler.handleAccount(this);
    }

    public String getUsername() {
        return username;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        PASS_LOGIN,
        FAIL_LOGIN,
        FAIL_EXIST,
        FAIL_HWID,
        FAIL_BANNED,
        FAIL_EXPIRED,
        PASS_REGISTER,
        FAIL_REGISTER_EXIST,
        FAIL_REGISTER_INVALID_CODE,
        PASS_RENEW,
        FAIL_RENEW_UNKNOWN,
        FAIL_RENEW_BANNED,
        FAIL_RENEW_UNEXPIRED,
        FAIL_RENEW_INVALID_CODE,
        LOG_OUT
    }
}
