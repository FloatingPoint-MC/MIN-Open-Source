package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

public class SPacketAccount implements Packet<INetHandlerClient> {
    private String username;
    private Status status;
    private String reason;
    private long duration;

    @Override
    public void readPacketData(PacketBuffer buf) {
        this.username = buf.readStringFromBuffer(16);
        this.status = buf.readEnumValue(Status.class);
        this.reason = buf.readStringFromBuffer(32767);
        this.duration = buf.readLong();
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

    public String getReason() {
        return reason;
    }

    public long getDuration() {
        return duration;
    }

    public enum Status {
        PASS_LOGIN,
        FAIL_LOGIN,
        FAIL_EXIST,
        FAIL_BANNED,
        PASS_REGISTER,
        FAIL_REGISTER_EXIST,
        LOG_OUT
    }
}
