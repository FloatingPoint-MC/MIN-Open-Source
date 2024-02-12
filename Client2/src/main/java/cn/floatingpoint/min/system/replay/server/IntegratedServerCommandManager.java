package cn.floatingpoint.min.system.replay.server;

import net.minecraft.command.ServerCommandManager;

public class IntegratedServerCommandManager extends ServerCommandManager {
    public IntegratedServerCommandManager(IntegratedServer server) {
        super(server);
    }
}
