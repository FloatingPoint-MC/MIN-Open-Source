package cn.floatingpoint.min.system.anticheat.check.impl.update;

import cn.floatingpoint.min.system.anticheat.check.Check;
import net.minecraft.network.play.server.SPacketPlayerAbilities;

/**
 * @projectName: MINPrivate
 * @author: vlouboos
 * @date: 2024-02-08 21:19:12
 */
public abstract class UpdateWalkingCheck implements Check {
    public static SPacketPlayerAbilities lastReceivedCapabilityPacket = null;

    @Override
    public Type getType() {
        return Type.UPDATE_WALKING;
    }


}
