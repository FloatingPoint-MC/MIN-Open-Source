package cn.floatingpoint.min.management.impl;

import cn.floatingpoint.min.management.Manager;
import cn.floatingpoint.min.system.anticheat.check.Check;
import cn.floatingpoint.min.system.anticheat.check.impl.click.ClickCheck;
import cn.floatingpoint.min.system.anticheat.check.impl.click.impl.HitBoxCheck;
import cn.floatingpoint.min.system.anticheat.check.impl.click.impl.ReachCheck;
import cn.floatingpoint.min.system.anticheat.check.impl.packet.PacketCheck;
import cn.floatingpoint.min.system.anticheat.check.impl.update.UpdateWalkingCheck;
import cn.floatingpoint.min.system.anticheat.check.impl.update.impl.FlyCheck;

import java.util.HashSet;

/**
 * @projectName: MINPrivate
 * @author: vlouboos
 * @date: 2024-02-08 20:49:10
 */
public class AntiCheatManager implements Manager {
    private HashSet<ClickCheck> clickChecks;
    private HashSet<PacketCheck> packetChecks;
    private HashSet<UpdateWalkingCheck> updateWalkingChecks;

    @Override
    public String getName() {
        return "Anti Cheat Manager";
    }

    @Override
    public void init() {
        clickChecks = new HashSet<>();
        packetChecks = new HashSet<>();
        updateWalkingChecks = new HashSet<>();

        clickChecks.add(new ReachCheck());
        clickChecks.add(new HitBoxCheck());

        updateWalkingChecks.add(new FlyCheck());
    }

    public void execute(Check.Executable executable) {
        switch (executable.type()) {
            case LEFT_CLICK -> clickChecks.forEach(c -> c.execute(executable.args()));
            case PACKET_SEND -> packetChecks.forEach(c -> c.execute(executable.args()));
            case UPDATE_WALKING -> updateWalkingChecks.forEach(c -> c.execute(executable.args()));
        }
    }
}
