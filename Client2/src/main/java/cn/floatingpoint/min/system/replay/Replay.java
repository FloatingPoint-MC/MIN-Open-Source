package cn.floatingpoint.min.system.replay;

import cn.floatingpoint.min.system.replay.packet.RecordedPacket;

import java.util.LinkedHashSet;

public class Replay {
    private LinkedHashSet<RecordedPacket> packets = new LinkedHashSet<>();

    public void addPacket(RecordedPacket packet) {
        packets.add(packet);
    }
}
