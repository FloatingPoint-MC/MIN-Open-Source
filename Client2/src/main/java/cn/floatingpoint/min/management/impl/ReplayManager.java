package cn.floatingpoint.min.management.impl;

import cn.floatingpoint.min.management.Manager;
import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.replay.Replay;
import cn.floatingpoint.min.system.replay.packet.C2SPacket;
import cn.floatingpoint.min.system.replay.packet.S2CPacket;
import cn.floatingpoint.min.system.replay.recording.Recording;
import cn.floatingpoint.min.system.replay.recording.State;
import cn.floatingpoint.min.system.replay.server.ReplayServer;
import cn.floatingpoint.min.system.ui.replay.GuiLoadingReplay;
import cn.floatingpoint.min.system.replay.packet.ChunkPacket;
import cn.floatingpoint.min.utils.client.ChatUtil;
import cn.floatingpoint.min.utils.client.IOUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.zip.ZipFile;

public class ReplayManager implements Manager {
    private LinkedHashMap<String, Recording> recordings;
    private ReplayServer replayServer;
    private boolean playing;

    @Override
    public String getName() {
        return "Replay Manager";
    }

    @Override
    public void init() {
        recordings = new LinkedHashMap<>();
    }

    public LinkedHashMap<String, Recording> getRecordings() {
        return recordings;
    }

    public void startRecording(String name) {
        if (recordings.containsKey(name)) {
            Recording recording = recordings.get(name);
            if (recording.getState() == State.PAUSED) {
                ChatUtil.printToChatWithPrefix("\247aResumed Recording '" + name + "'!");
                recording.setState(State.PLAYING);
                return;
            }
            ChatUtil.printToChatWithPrefix("\247cRecording '" + name + "' is already started!");
            return;
        }
        ChatUtil.printToChatWithPrefix("\247aRecording will start after you join the game!");
        recordings.put(name, new Recording(name));
    }

    public void stopRecording(String name) {
        if (!recordings.containsKey(name)) {
            ChatUtil.printToChatWithPrefix("\247cRecording '" + name + "' is not started!");
            return;
        }
        Recording recording = recordings.get(name);
        if (recording.getState() == State.IDLE) {
            ChatUtil.printToChatWithPrefix("\247eRecording '" + name + "' is stopped, but there is nothing recorded!");
            recordings.remove(name);
        } else if (recording.getState() == State.PLAYING || recording.getState() == State.PAUSED) {
            ChatUtil.printToChatWithPrefix("\247aRecording '" + name + "' stopped!");
            recording.setState(State.END);
        } else {
            ChatUtil.printToChatWithPrefix("\247cRecording '" + name + "' is already stopped!");
        }
    }

    @SuppressWarnings("all")
    public void loadReplays() {
        File replayFolder = Managers.fileManager.getConfigFile("replay");
        if (!replayFolder.exists() || !replayFolder.isDirectory()) {
            replayFolder.delete();
            replayFolder.mkdir();
        }
    }

    public void loadReplay(ReplayServer replayServer) {
        this.replayServer = replayServer;
        Replay replay = replayServer.getReplay();
        if (!replay.getFile().exists()) {
            loadReplays();
            return;
        }
        try (ZipFile zip = new ZipFile(replay.getFile())) {
            JSONObject data = new JSONObject(new String(IOUtil.readZipEntry(zip.getEntry("data"), zip), StandardCharsets.UTF_8));
            int length = data.getInt("packet_num");
            JSONArray spawnPos = data.getJSONArray("spawn_loc");
            replay.setSpawnPos(new BlockPos(spawnPos.getInt(0), spawnPos.getInt(1), spawnPos.getInt(2)));
            replay.setEntityId(data.getInt("entity_id"));
            replay.setEntityName(data.getString("entity_name"));
            replay.setUuid(data.getString("entity_uuid"));
            for (int i = 0; i < length; i++) {
                if (mc.currentScreen instanceof GuiLoadingReplay guiLoadingReplay) {
                    guiLoadingReplay.title = "Loading Replay '" + replay.getName() + "'";
                    guiLoadingReplay.percentage = (double) i / (double) length;
                }
                JSONObject packet = new JSONObject(new String(IOUtil.readZipEntry(zip.getEntry("Packet" + i), zip), StandardCharsets.UTF_8));
                String type = packet.getString("type");
                PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
                JSONArray dataArray = packet.getJSONArray("data");
                if (dataArray.isEmpty()) continue;
                for (Object o : dataArray) {
                    String packetData = (String) o;
                    String toWrite = packetData.substring(1);
                    switch (packetData.charAt(0)) {
                        case 'B' -> packetBuffer.writeByte(Byte.parseByte(toWrite));
                        case 'C' -> throw new IllegalStateException("Unexpected value: C(characters)");
                        case 'D' -> packetBuffer.writeDouble(Double.parseDouble(toWrite));
                        case 'F' -> packetBuffer.writeFloat(Float.parseFloat(toWrite));
                        case 'I' -> packetBuffer.writeInt(Integer.parseInt(toWrite));
                        case 'J' -> packetBuffer.writeLong(Long.parseLong(toWrite));
                        case 'S' -> packetBuffer.writeShort(Short.parseShort(toWrite));
                        case 'Z' -> packetBuffer.writeBoolean(Boolean.getBoolean(toWrite));
                        case 'V' -> {
                            char dataType = toWrite.charAt(0);
                            toWrite = toWrite.substring(1);
                            switch (dataType) {
                                case 'I' -> packetBuffer.writeVarInt(Integer.parseInt(toWrite));
                                case 'J' -> packetBuffer.writeVarLong(Long.parseLong(toWrite));
                            }
                        }
                        case '[' -> {
                            char dataType = toWrite.charAt(0);
                            toWrite = toWrite.substring(1);
                            switch (dataType) {
                                case 'B' -> {
                                    StringTokenizer tokenizer = new StringTokenizer(toWrite.replace("[", "").replace("]", ""), ", ");
                                    int size = tokenizer.countTokens();
                                    byte[] byteArray = new byte[size];
                                    for (int j = 0; j < size; j++) {
                                        byteArray[j] = Byte.parseByte(tokenizer.nextToken());
                                    }
                                    packetBuffer.writeByteArray(byteArray);
                                }
                                case 'I' -> {
                                    StringTokenizer tokenizer = new StringTokenizer(toWrite.replace("[", "").replace("]", ""), ", ");
                                    int size = tokenizer.countTokens();
                                    int[] intArray = new int[size];
                                    for (int j = 0; j < size; j++) {
                                        intArray[j] = Integer.parseInt(tokenizer.nextToken());
                                    }
                                    packetBuffer.writeVarIntArray(intArray);
                                }
                                case 'J' -> {
                                    StringTokenizer tokenizer = new StringTokenizer(toWrite.replace("[", "").replace("]", ""), ", ");
                                    int size = tokenizer.countTokens();
                                    long[] longArray = new long[size];
                                    for (int j = 0; j < size; j++) {
                                        longArray[j] = Integer.parseInt(tokenizer.nextToken());
                                    }
                                    packetBuffer.writeLongArray(longArray);
                                }
                            }
                        }
                        case 'L' -> {
                            if (toWrite.startsWith("java/lang/String;")) {
                                toWrite = toWrite.substring(17);
                                packetBuffer.writeString(toWrite);
                            } else if (toWrite.startsWith("net/minecraft/item/ItemStack;")) {
                                throw new IllegalStateException("Unexpected value: Lnet/minecraft/item/ItemStack;");
                            } else if (toWrite.startsWith("net/minecraft/nbt/NBTTagCompound;")) {
                                throw new IllegalStateException("Unexpected value: Lnet/minecraft/nbt/NBTTagCompound;");
                            }
                        }
                        case 'O' -> {
                            StringTokenizer tokenizer = new StringTokenizer(toWrite.replace("[", "").replace("]", ""), ", ");
                            int size = tokenizer.countTokens();
                            byte[] bytes = new byte[size];
                            for (int j = 0; j < size; j++) {
                                bytes[j] = Byte.parseByte(tokenizer.nextToken());
                            }
                            packetBuffer.writeBytes(bytes);
                        }
                        default -> throw new IllegalStateException("Unexpected value: " + packetData.charAt(0));
                    }
                }
                switch (type) {
                    case "C2S" -> {
                        int id = packet.getInt("id");
                        int tick = packet.getInt("tick");
                        replay.addPacket(new C2SPacket(tick, id, packetBuffer));
                    }
                    case "S2C" -> {
                        int id = packet.getInt("id");
                        int tick = packet.getInt("tick");
                        replay.addPacket(new S2CPacket(tick, id, packetBuffer));
                    }
                    case "Chunk" -> replay.addPacket(new ChunkPacket(packetBuffer));
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                }
            }
        } catch (Exception e) {
            loadReplays();
            if (Minecraft.DEBUG_MODE()) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public ReplayServer getReplayServer() {
        return replayServer;
    }
}
