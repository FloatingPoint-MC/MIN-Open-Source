package cn.floatingpoint.min.system.replay;

import cn.floatingpoint.min.system.replay.packet.RecordedPacket;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Replay {
    private final HashMap<Integer, ArrayList<RecordedPacket>> packets;
    private final String name;
    private final File file;
    private BlockPos spawnPos;
    private String entityName, uuid;
    private int entityId;
    public int tick, totalTicks;

    public Replay(String name, File file) {
        this.packets = new HashMap<>();
        this.name = name.substring(0, name.length() - 7);
        this.file = file;
        this.tick = 0;
        this.totalTicks = 0;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
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

    public void addPacket(RecordedPacket packet) {
        packets.putIfAbsent(packet.tick(), new ArrayList<>());
        packets.get(packet.tick()).add(packet);
        totalTicks = Math.max(totalTicks, packet.tick());
    }

    public void delete() {
        if (!file.delete()) {
            throw new RuntimeException();
        }
    }

    public HashMap<Integer, ArrayList<RecordedPacket>> getPackets() {
        return packets;
    }
}
