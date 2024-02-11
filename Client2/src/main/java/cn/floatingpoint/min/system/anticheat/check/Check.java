package cn.floatingpoint.min.system.anticheat.check;

import net.minecraft.client.Minecraft;

/**
 * @projectName: MINPrivate
 * @author: vlouboos
 * @date: 2024-02-08 20:55:50
 */
public interface Check {
    Minecraft mc = Minecraft.getMinecraft();

    void execute(Object... args);

    Type getType();

    enum Type {
        UPDATE_WALKING,
        LEFT_CLICK,
        PACKET
    }

    record Executable(Type type, Object... args) {
    }

}
