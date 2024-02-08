package cn.floatingpoint.min.system.anticheat.check.impl.update;

import cn.floatingpoint.min.system.anticheat.check.Check;

/**
 * @projectName: MINPrivate
 * @author: vlouboos
 * @date: 2024-02-08 21:19:12
 */
public abstract class UpdateWalkingCheck implements Check {
    @Override
    public Type getType() {
        return Type.UPDATE_WALKING;
    }
}
