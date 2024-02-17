package cn.floatingpoint.min.threads;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.replay.packet.C2SPacket;
import cn.floatingpoint.min.system.replay.packet.ChunkPacket;
import cn.floatingpoint.min.system.replay.packet.RecordedPacket;
import cn.floatingpoint.min.system.replay.packet.S2CPacket;
import cn.floatingpoint.min.system.replay.recording.Recording;
import cn.floatingpoint.min.utils.client.ChatUtil;
import cn.floatingpoint.min.utils.client.IOUtil;
import cn.floatingpoint.min.utils.client.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReplayRecordingThread extends Thread {
    public final ConcurrentLinkedDeque<Pair<Integer, Pair<Packet<?>, EnumPacketDirection>>> toAdd;
    private final Recording recording;

    public ReplayRecordingThread(Recording recording) {
        super("Replay Recording Thread " + Integer.toHexString(recording.hashCode()));
        this.recording = recording;
        toAdd = new ConcurrentLinkedDeque<>();
    }

    @SuppressWarnings("all")
    @Override
    public void run() {
        while (Minecraft.getMinecraft().running) {
            try {
                if (recording.getState() == cn.floatingpoint.min.system.replay.recording.State.PLAYING) {
                    while (!toAdd.isEmpty()) {
                        Pair<Integer, Pair<Packet<?>, EnumPacketDirection>> toAdd = this.toAdd.poll();
                        Pair<Packet<?>, EnumPacketDirection> pair = toAdd.getValue();
                        Packet<?> packet = pair.getKey();
                        Integer packetId = EnumConnectionState.PLAY.getPacketId(pair.getValue(), packet);
                        if (packetId == null) {
                            System.err.println("Can't get packetId from " + pair.getKey().getClass().getName() + " of side " + pair.getValue().name());
                            continue;
                        }
                        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
                        packet.writePacketData(packetBuffer);
                        if (packet instanceof SPacketChunkData data) {
                            recording.packets.add(new ChunkPacket(packetBuffer));
                        } else {
                            switch (pair.getValue()) {
                                case SERVERBOUND ->
                                        recording.packets.add(new C2SPacket(toAdd.getKey(), packetId, packetBuffer));
                                case CLIENTBOUND ->
                                        recording.packets.add(new S2CPacket(toAdd.getKey(), packetId, packetBuffer));
                            }
                        }
                    }
                } else if (recording.getState() == cn.floatingpoint.min.system.replay.recording.State.END) {
                    ChatUtil.printToChatWithPrefix("Saving recording '" + recording.getName() + "'");
                    File zipFile = Managers.fileManager.getConfigFile("replay/" + recording.getName() + ".replay");
                    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
                        int packetNum = recording.packets.size();
                        JSONObject dataJson = new JSONObject()
                                .put("spawn_loc", new JSONArray()
                                        .put(recording.getSpawnPos().getX())
                                        .put(recording.getSpawnPos().getY())
                                        .put(recording.getSpawnPos().getZ())
                                )
                                .put("entity_name", recording.getEntityName())
                                .put("entity_uuid", recording.getUuid())
                                .put("entity_id", recording.getEntityId())
                                .put("packet_num", packetNum);
                        IOUtil.writeToZip(new ZipEntry("data"), out, dataJson);
                        for (int i = 0; i < packetNum; i++) {
                            JSONObject packetJson = new JSONObject();
                            RecordedPacket recordedPacket = recording.packets.poll();
                            PacketBuffer packetBuffer = recordedPacket.packetBuffer();
                            packetJson.put("id", recordedPacket.packetId());
                            packetJson.put("tick", recordedPacket.tick());
                            JSONArray dataArray = new JSONArray();
                            if (recordedPacket instanceof C2SPacket) {
                                packetJson.put("type", "C2S");
                                byte[] bytes = new byte[packetBuffer.readableBytes()];
                                packetBuffer.readBytes(bytes);
                                dataArray.put("O" + Arrays.toString(bytes));
                            } else if (recordedPacket instanceof S2CPacket) {
                                packetJson.put("type", "S2C");
                                switch (recordedPacket.packetId()) {
                                    case 1 -> { // SpawnExperienceOrb
                                        dataArray
                                                .put("VI" + packetBuffer.readVarInt())
                                                .put("D" + packetBuffer.readDouble())
                                                .put("D" + packetBuffer.readDouble())
                                                .put("D" + packetBuffer.readDouble())
                                                .put("S" + packetBuffer.readShort());
                                    }
                                    case 2 -> { // SpawnGlobalEntity
                                        dataArray
                                                .put("VI" + packetBuffer.readVarInt())
                                                .put("B" + packetBuffer.readByte())
                                                .put("D" + packetBuffer.readDouble())
                                                .put("D" + packetBuffer.readDouble())
                                                .put("D" + packetBuffer.readDouble());
                                    }
                                    case 6 -> { // Animation
                                        dataArray.put("VI" + packetBuffer.readVarInt()).put("B" + packetBuffer.readUnsignedByte());
                                    }
                                    case 7 -> { // Statistics
                                        int size = packetBuffer.readVarInt();
                                        dataArray.put("VI" + size);
                                        for (int j = 0; j < size; ++j) {
                                            dataArray
                                                    .put("Ljava/lang/String;" + packetBuffer.readString(32767))
                                                    .put("VI" + packetBuffer.readVarInt());
                                        }
                                    }
                                    case 13 -> dataArray.put("B" + packetBuffer.readUnsignedByte()); // ServerDifficulty
                                    case 14 -> { // Tab Complete
                                        int length = packetBuffer.readVarInt();
                                        dataArray.put("VI" + length);
                                        for (int j = 0; j < length; j++) {
                                            dataArray.put("Ljava/lang/String;" + packetBuffer.readString(32767));
                                        }
                                    }
                                    case 16 -> { // MultiBlockChange
                                        dataArray
                                                .put("I" + packetBuffer.readInt())
                                                .put("I" + packetBuffer.readInt());
                                        int length = packetBuffer.readVarInt();
                                        dataArray.put("VI" + length);
                                        for (int j = 0; j < length; j++) {
                                            dataArray.put("S" + packetBuffer.readShort())
                                                    .put("VI" + packetBuffer.readVarInt());
                                        }
                                    }
                                    case 27 -> { // EntityStatus
                                        dataArray
                                                .put("I" + packetBuffer.readInt())
                                                .put("B" + packetBuffer.readUnsignedByte());
                                    }
                                    case 28 -> { // Explosion
                                        dataArray
                                                .put("F" + packetBuffer.readFloat())
                                                .put("F" + packetBuffer.readFloat())
                                                .put("F" + packetBuffer.readFloat())
                                                .put("F" + packetBuffer.readFloat());
                                        int amount = packetBuffer.readInt();
                                        dataArray.put("I" + amount);
                                        for (int j = 0; j < amount; j++) {
                                            dataArray.put("B" + packetBuffer.readByte())
                                                    .put("B" + packetBuffer.readByte())
                                                    .put("B" + packetBuffer.readByte());
                                        }
                                        // Change motion to 0 to prevent the camera is knocked
                                        dataArray.put("F0")
                                                .put("F0")
                                                .put("F0");
                                    }
                                    case 30 -> { // ChangeGameState
                                        dataArray.put("B" + packetBuffer.readUnsignedByte())
                                                .put("F" + packetBuffer.readFloat());
                                    }
                                    case 37 -> { // Entity
                                        dataArray.put("VI" + packetBuffer.readVarInt());
                                    }
                                    case 38 -> {
                                        dataArray
                                                .put("VI" + packetBuffer.readVarInt())
                                                .put("S" + packetBuffer.readShort())
                                                .put("S" + packetBuffer.readShort())
                                                .put("S" + packetBuffer.readShort())
                                                .put("Z" + packetBuffer.readBoolean());
                                    }
                                    case 39 -> {
                                        dataArray
                                                .put("VI" + packetBuffer.readVarInt())
                                                .put("S" + packetBuffer.readShort())
                                                .put("S" + packetBuffer.readShort())
                                                .put("S" + packetBuffer.readShort())
                                                .put("B" + packetBuffer.readByte())
                                                .put("B" + packetBuffer.readByte())
                                                .put("Z" + packetBuffer.readBoolean());
                                    }
                                    case 40 -> {
                                        dataArray
                                                .put("VI" + packetBuffer.readVarInt())
                                                .put("B" + packetBuffer.readByte())
                                                .put("B" + packetBuffer.readByte())
                                                .put("Z" + packetBuffer.readBoolean());
                                    }
                                    default -> {
                                        byte[] bytes = new byte[packetBuffer.readableBytes()];
                                        packetBuffer.readBytes(bytes);
                                        dataArray.put("O" + Arrays.toString(bytes));
                                    }
                                }
                            } else if (recordedPacket instanceof ChunkPacket) {
                                packetJson.put("type", "Chunk");
                                byte[] bytes = new byte[packetBuffer.readableBytes()];
                                packetBuffer.readBytes(bytes);
                                dataArray.put("O" + Arrays.toString(bytes));
                            }
                            packetJson.put("data", dataArray);
                            IOUtil.writeToZip(new ZipEntry("Packet" + i), out, packetJson);
                        }
                    }
                    Managers.replayManager.getRecordings().remove(recording);
                    TextComponentString s = new TextComponentString("\247b[MIN] \247fSaved recording '" + recording.getName() + "' to " + zipFile.getCanonicalPath());
                    s.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, zipFile.getCanonicalPath()));
                    s.getStyle().setUnderlined(true);
                    ChatUtil.printToChat(s);
                    break;
                }
                Thread.sleep(100L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addPacket(Packet<?> packet, EnumPacketDirection side) {
        if (recording.getState() == cn.floatingpoint.min.system.replay.recording.State.PLAYING) {
            toAdd.add(new Pair<>(recording.tick, new Pair<>(packet, side)));
        }
    }
}
