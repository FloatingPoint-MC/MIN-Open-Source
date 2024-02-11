package cn.floatingpoint.min.system.anticheat.check.impl.click.impl;

import cn.floatingpoint.min.system.anticheat.check.impl.click.ClickCheck;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.irc.packet.impl.CPacketAntiCheatData;
import cn.floatingpoint.min.utils.client.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameType;

import java.util.ArrayList;

/**
 * @projectName: MINPrivate
 * @author: vlouboos
 * @date: 2024-02-08 22:03:03
 */
public class HitBoxCheck extends ClickCheck {
    @Override
    public void execute(Object... args) {
        if (mc.playerController.getCurrentGameType() != GameType.SURVIVAL && mc.playerController.getCurrentGameType() != GameType.ADVENTURE)
            return;
        RayTraceResult.Type type = (RayTraceResult.Type) args[0];
        if (type == RayTraceResult.Type.ENTITY && args[1] instanceof EntityPlayer player) {
            boolean cheat = player.getCollisionBorderSize() > 1.45e-44f;
            ArrayList<Pair<CPacketAntiCheatData.Type, Object>> data = new ArrayList<>();
            data.add(new Pair<>(CPacketAntiCheatData.Type.FLOAT, player.getCollisionBorderSize()));
            data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, cheat));
            IRCClient.getInstance().addToSendQueue(new CPacketAntiCheatData(2, data));
        }
    }
}
