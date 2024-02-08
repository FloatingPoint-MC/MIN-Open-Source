package cn.floatingpoint.min.system.anticheat.check.impl.click;

import cn.floatingpoint.min.system.anticheat.check.Check;

/**
 * @projectName: MINPrivate
 * @author: vlouboos
 * @date: 2024-02-08 20:58:36
 */
public abstract class ClickCheck implements Check {
    @Override
    public Type getType() {
        return Type.LEFT_CLICK;
    }
}
