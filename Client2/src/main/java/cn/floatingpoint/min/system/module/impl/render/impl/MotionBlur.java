package cn.floatingpoint.min.system.module.impl.render.impl;

import cn.floatingpoint.min.system.module.impl.render.RenderModule;
import cn.floatingpoint.min.system.module.value.impl.IntegerValue;
import cn.floatingpoint.min.utils.client.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class MotionBlur extends RenderModule {
    public final IntegerValue amplifier = new IntegerValue(1, 9, 1, 5) {
        @Override
        public void setValue(Integer value) {
            if (value.intValue() != this.getValue().intValue()){
                if (MotionBlur.this.isEnabled()) {
                    Minecraft.getMinecraft().entityRenderer.stopUseShader();
                    Minecraft.getMinecraft().entityRenderer.loadShader(new ResourceLocation("min/shaders/post/motion_blur_" + amplifier.getValue() + "x.json"));
                }
                super.setValue(value);
            }
        }
    };

    public MotionBlur() {
        addValues(new Pair<>("Amplifier", amplifier));
    }

    @Override
    public void onEnable() {
        mc.entityRenderer.loadShader(new ResourceLocation("min/shaders/post/motion_blur_" + amplifier.getValue() + "x.json"));
    }

    @Override
    public void onDisable() {
        mc.entityRenderer.stopUseShader();
    }

    @Override
    public void onRender3D() {
    }
}
