package cn.floatingpoint.min.system.replay.recording;

import cn.floatingpoint.min.system.replay.packet.RecordedPacket;
import cn.floatingpoint.min.threads.ReplayRecordingThread;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;

public class Recording {
    private final ReplayRecordingThread recordingThread;
    public final ArrayDeque<RecordedPacket> packets;
    private final LinkedHashSet<Long> chunkLoaded;
    public int tick;
    private final String name;
    private BlockPos spawnPos;
    private String entityName, uuid;
    private int entityId;
    private State state;

    public Recording(String name) {
        recordingThread = new ReplayRecordingThread(this);
        chunkLoaded = new LinkedHashSet<>();
        packets = new ArrayDeque<>();
        this.name = name;
        state = State.IDLE;
        tick = 0;
    }

    public BlockPos getSpawnPos() {
        return spawnPos;
    }

    public void setSpawnPos(BlockPos spawnPos) {
        this.spawnPos = spawnPos;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (this.state != State.PLAYING && state == State.PLAYING) {
            recordingThread.start();
        }
        this.state = state;
    }

    public void addPacket(Packet<?> packet, EnumPacketDirection side) {
        if (packet instanceof SPacketChunkData data) {
            Long l = ChunkPos.asLong(data.getChunkX(), data.getChunkZ());
            if (chunkLoaded.contains(l)) {
                return;
            }
            chunkLoaded.add(l);
        }
        recordingThread.addPacket(packet, side);
    }

    public String getName() {
        return name;
    }

    public boolean hasChunk(long seed) {
        if (chunkLoaded.contains(seed)) {
            return true;
        } else {
            chunkLoaded.add(seed);
            return false;
        }
    }
}
