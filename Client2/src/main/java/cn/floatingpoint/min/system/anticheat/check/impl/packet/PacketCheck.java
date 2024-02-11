package cn.floatingpoint.min.system.anticheat.check.impl.packet;

import cn.floatingpoint.min.system.anticheat.check.Check;
import net.minecraft.network.Packet;

/**
 * @projectName: MINPrivate
 * @author: vlouboos
 * @date: 2024-02-08 21:17:28
 */
public abstract class PacketCheck implements Check {
    @Override
    public Type getType() {
        return Type.PACKET;
    }

    @Override
    public void execute(Object... args) {
        if (args.length == 0) {
            return;
        } else {
            if (args[0] instanceof Packet<?> packet) {
                onPacket(packet);
            }
        }
    }

    protected abstract void onPacket(Packet<?> packetIn);
}
