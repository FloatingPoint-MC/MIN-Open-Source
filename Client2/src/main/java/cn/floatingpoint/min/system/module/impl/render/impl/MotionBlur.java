package cn.floatingpoint.min.system.module.impl.render.impl;

import cn.floatingpoint.min.system.module.impl.render.RenderModule;
import cn.floatingpoint.min.system.module.value.impl.IntegerValue;
import cn.floatingpoint.min.utils.client.Pair;
import net.minecraft.util.ResourceLocation;

public class MotionBlur extends RenderModule {
    public final IntegerValue amplifier = new IntegerValue(1, 9, 1, 5);
    private int currentAmplifier = 0;

    public MotionBlur() {
        addValues(new Pair<>("Amplifier", amplifier));
    }

    @Override
    public void onEnable() {
        currentAmplifier = amplifier.getValue();
    }

    @Override
    public void onDisable() {
        currentAmplifier = 0;
    }

    @Override
    public void onRender3D() {
    }

    public boolean setMotionBlurShader() {
        int amplifier = this.amplifier.getValue();
        if (!isEnabled()) {
            mc.entityRenderer.stopUseShader();
            return false;
        } else if (mc.world == null) {
            return false;
        } else if (mc.entityRenderer.getShaderGroup() != null && currentAmplifier == amplifier) {
            return true;
        } else {
            mc.entityRenderer.loadShader(new ResourceLocation("min/shaders/post/motion_blur_" + amplifier + "x.json"));
            if (mc.entityRenderer.useShader) {
                currentAmplifier = amplifier;
                return true;
            } else {
                return false;
            }
        }
    }
}
