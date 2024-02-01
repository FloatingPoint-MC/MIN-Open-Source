package cn.floatingpoint.min;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.runnable.Runnable;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.ui.client.GuiError;
import cn.floatingpoint.min.threads.AsyncLoopThread;
import cn.floatingpoint.min.threads.MouseHandlerThread;
import me.konago.nativeobfuscator.Native;
import net.minecraft.client.Minecraft;

import java.net.URISyntaxException;

public class MIN {
    public static final String VERSION = "2.12.1";
    private static final AsyncLoopThread asyncLoopThread = new AsyncLoopThread();

    @Native
    public static void init() {
        Managers.init();
        asyncLoopThread.setName("Asynchronous Loop Thread");
        asyncLoopThread.setDaemon(true);
        asyncLoopThread.start();
        MouseHandlerThread mouseHandlerThread = new MouseHandlerThread();
        mouseHandlerThread.setName("Mouse Handler Thread");
        mouseHandlerThread.start();
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

    @Native
    public static void checkIfAsyncThreadAlive() {
        if (!asyncLoopThread.isAlive() || asyncLoopThread.isInterrupted() || asyncLoopThread.getState().equals(Thread.State.TERMINATED)) {
            Minecraft.getMinecraft().world.sendQuittingDisconnectingPacket();
            Minecraft.getMinecraft().loadWorld(null);
            Minecraft.getMinecraft().displayGuiScreen(new GuiError());
        }
    }
}
