package cn.floatingpoint.min.system.replay.recording;

import cn.floatingpoint.min.system.replay.packet.RecordedPacket;
import cn.floatingpoint.min.threads.ReplayRecordingThread;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedHashSet;

public class Recording {
    private final ReplayRecordingThread recordingThread;
    public LinkedHashSet<RecordedPacket> packets;
    private BlockPos spawnPos;
    private State state;

    public Recording() {
        recordingThread = new ReplayRecordingThread(this);
        packets = new LinkedHashSet<>();
        state = State.IDLE;
    }

    public BlockPos getSpawnPos() {
        return spawnPos;
    }

    public void setSpawnPos(BlockPos spawnPos) {
        this.spawnPos = spawnPos;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (this.state != State.PLAY && state == State.PLAY) {
            recordingThread.start();
        }
        this.state = state;
    }

    public void addPacket(Packet<?> packet, EnumPacketDirection side) {
        recordingThread.addPacket(packet, side);
    }
}
