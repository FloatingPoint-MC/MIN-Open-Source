package cn.floatingpoint.min.utils.client;

import cn.floatingpoint.min.system.irc.handler.INetHandler;
import cn.floatingpoint.min.system.irc.packet.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {

    public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packetIn, final T processor, IThreadListener scheduler) throws ThreadQuickExitException {
        if (!scheduler.isCallingFromMinecraftThread()) {
            scheduler.addScheduledTask(() -> packetIn.processPacket(processor));
            throw ThreadQuickExitException.INSTANCE;
        }
    }
}
