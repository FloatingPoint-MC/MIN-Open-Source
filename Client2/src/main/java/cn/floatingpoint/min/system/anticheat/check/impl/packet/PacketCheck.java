package cn.floatingpoint.min.system.anticheat.check.impl.packet;

import cn.floatingpoint.min.system.anticheat.check.Check;

/**
 * @projectName: MINPrivate
 * @author: vlouboos
 * @date: 2024-02-08 21:17:28
 */
public abstract class PacketCheck implements Check {
    @Override
    public Type getType() {
        return Type.PACKET_SEND;
    }
}
