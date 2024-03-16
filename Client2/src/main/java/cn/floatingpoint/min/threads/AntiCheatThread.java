package cn.floatingpoint.min.threads;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.anticheat.check.Check;
import net.minecraft.client.Minecraft;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * @projectName: MINPrivate
 * @author: vlouboos
 * @date: 2024-02-08 20:50:26
 */
public class AntiCheatThread extends Thread {
    public final HashSet<Check.Executable> executables = new LinkedHashSet<>();

    @SuppressWarnings("all")
    @Override
    public void run() {
        while (Minecraft.getMinecraft().running) {
            try {
                synchronized (executables) {
                    if (executables.isEmpty()) {
                        Thread.sleep(5000L);
                        continue;
                    }
                    Check.Executable executable = executables.stream().findAny().get();
                    executables.remove(executable);
                    Managers.antiCheatManager.execute(executable);
                }
            } catch (Exception ignored) {
            }
        }
    }
}
