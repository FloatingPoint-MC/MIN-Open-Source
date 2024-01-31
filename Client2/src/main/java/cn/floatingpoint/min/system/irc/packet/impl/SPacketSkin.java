package cn.floatingpoint.min.system.irc.packet.impl;

import cn.floatingpoint.min.system.irc.handler.INetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.PacketBuffer;

import java.io.IOException;

public class SPacketSkin implements Packet<INetHandlerClient> {
    private Action action;
    private String addition;

    public Action getAction() {
        return action;
    }

    public String getAddition() {
        return addition;
    }

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf
     */
    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        action = buf.readEnumValue(Action.class);
        if (action == Action.BOUGHT || action == Action.CHANGED) {
            addition = buf.readStringFromBuffer(32);
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {

    }

    @Override
    public void processPacket(INetHandlerClient handler) {
        handler.handleSkin(this);
    }

    public enum Action {
        FAIL,
        CHANGED,
        BOUGHT,
        RENEWED,
        INVALID_NAME,
        INVALID_CODE,
    }
}
