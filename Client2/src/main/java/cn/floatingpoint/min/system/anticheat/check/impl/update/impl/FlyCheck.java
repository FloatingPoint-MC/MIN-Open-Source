package cn.floatingpoint.min.system.anticheat.check.impl.update.impl;

import cn.floatingpoint.min.system.anticheat.check.impl.update.UpdateWalkingCheck;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.irc.packet.impl.CPacketAntiCheatData;
import cn.floatingpoint.min.utils.client.Pair;
import net.minecraft.world.GameType;

import java.util.ArrayList;

public class FlyCheck extends UpdateWalkingCheck {

    @Override
    public void execute(Object... args) {
        if (mc.player.capabilities.isCreativeMode || mc.playerController.getCurrentGameType() == GameType.CREATIVE || mc.player.isSpectator()) {
            return;
        }
        if (lastReceivedCapabilityPacket.isAllowFlying()) return;
        if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -0.1, 0.0D)).isEmpty()) {
            if (mc.player.capabilities.isFlying) {
                boolean cheat = false;
                ArrayList<Pair<CPacketAntiCheatData.Type, Object>> data = new ArrayList<>();
                if (lastReceivedCapabilityPacket == null) {
                    data.add(new Pair<>(CPacketAntiCheatData.Type.INT, 0));
                    data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, mc.player.capabilities.allowFlying));
                    data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, true));
                    if (!mc.player.capabilities.allowFlying) {
                        mc.player.capabilities.isFlying = false;
                        cheat = true;
                    }
                } else {
                    if (lastReceivedCapabilityPacket.isAllowFlying()) return;
                    data.add(new Pair<>(CPacketAntiCheatData.Type.INT, 1));
                    data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, lastReceivedCapabilityPacket.isAllowFlying()));
                    data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, lastReceivedCapabilityPacket.isFlying()));
                    cheat = (mc.player.capabilities.isFlying != lastReceivedCapabilityPacket.isFlying());
                    mc.player.capabilities.isFlying = lastReceivedCapabilityPacket.isFlying();
                }
                data.add(new Pair<>(CPacketAntiCheatData.Type.BOOLEAN, cheat));
                IRCClient.getInstance().addToSendQueue(new CPacketAntiCheatData(3, data));
            }
        }
    }
}