package cn.floatingpoint.min.management;

import net.minecraft.client.Minecraft;

public interface Manager {
    Minecraft mc = Minecraft.getMinecraft();

    String getName();

    void init();
}
