package cn.floatingpoint.min;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.runnable.Runnable;
import cn.floatingpoint.min.system.anticheat.check.Check;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.ui.client.GuiError;
import cn.floatingpoint.min.threads.AntiCheatThread;
import cn.floatingpoint.min.threads.AsyncLoopThread;
import cn.floatingpoint.min.threads.MouseHandlerThread;
import me.konago.nativeobfuscator.Native;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.net.URISyntaxException;

public class MIN {
    public static final String VERSION = "2.15";
    private static final AsyncLoopThread asyncLoopThread = new AsyncLoopThread();
    private static final AntiCheatThread antiCheatThread = new AntiCheatThread();

    @Native
    public static void init() throws IOException {
        Managers.init();
        asyncLoopThread.setName("Asynchronous Loop Thread");
        asyncLoopThread.setDaemon(true);
        asyncLoopThread.start();
        MouseHandlerThread mouseHandlerThread = new MouseHandlerThread();
        mouseHandlerThread.setName("Mouse Handler Thread");
        mouseHandlerThread.start();
        antiCheatThread.setName("Anti Cheat Thread");
        antiCheatThread.setDaemon(true);
        antiCheatThread.start();
        try {
            new IRCClient();
        } catch (URISyntaxException e) {
            Minecraft.getMinecraft().shutdown();
        }
    }

    public static void stop() {
        Managers.fileManager.saveConfig();
    }

    public static void runAsync(Runnable runnable) {
        AsyncLoopThread.runnableSet.add(runnable);
    }

    public static void runCheck(Check.Executable executable) {
        antiCheatThread.executables.add(executable);
    }

    @Native
    public static void checkIfAsyncThreadAlive(Minecraft mc) {
        if (!asyncLoopThread.isAlive() || asyncLoopThread.isInterrupted() || asyncLoopThread.getState().equals(Thread.State.TERMINATED) || !antiCheatThread.isAlive() || antiCheatThread.isInterrupted() || antiCheatThread.getState().equals(Thread.State.TERMINATED)) {
            if (mc.world != null) {
                mc.world.sendQuittingDisconnectingPacket();
                mc.loadWorld(null);
            }
            mc.displayGuiScreen(new GuiError());
        }
    }
}
