package cn.floatingpoint.min.system.module.impl.render.impl;

import cn.floatingpoint.min.system.module.impl.render.RenderModule;
import cn.floatingpoint.min.system.module.value.impl.IntegerValue;
import cn.floatingpoint.min.utils.client.Pair;

public class MotionBlur extends RenderModule {
    public static final IntegerValue amplifier = new IntegerValue(1, 9, 1, 5);

    public MotionBlur() {
        addValues(new Pair<>("Amplifier", amplifier));
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onRender3D() {
    }
}
