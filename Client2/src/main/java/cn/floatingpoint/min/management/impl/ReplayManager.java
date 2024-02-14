package cn.floatingpoint.min.management.impl;

import cn.floatingpoint.min.management.Manager;
import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.replay.Replay;
import cn.floatingpoint.min.system.replay.packet.C2SPacket;
import cn.floatingpoint.min.system.replay.packet.S2CPacket;
import cn.floatingpoint.min.system.replay.recording.Recording;
import cn.floatingpoint.min.utils.client.ChatUtil;
import cn.floatingpoint.min.utils.client.IOUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;
import java.util.zip.ZipFile;

public class ReplayManager implements Manager {
    private LinkedHashSet<String> replays;
    private LinkedHashMap<String, Recording> recordings;

    @Override
    public String getName() {
        return "Replay Manager";
    }

    @Override
    public void init() {
        replays = new LinkedHashSet<>();
        recordings = new LinkedHashMap<>();
    }

    public LinkedHashSet<String> getReplays() {
        return replays;
    }

    public LinkedHashMap<String, Recording> getRecordings() {
        return recordings;
    }

    public void startRecording(String name) {
        if (recordings.containsKey(name)) {
            ChatUtil.printToChatWithPrefix("\247cRecording '" + name + "' is already started!");
            return;
        }
        ChatUtil.printToChatWithPrefix("\247aRecording will start after you join the game!");
        recordings.put(name, new Recording());
    }

    @SuppressWarnings("all")
    public void loadReplays() {
        File replayFolder = Managers.fileManager.getConfigFile("replays");
        if (!replayFolder.exists() || !replayFolder.isDirectory()) {
            replayFolder.delete();
            replayFolder.mkdir();
        }
        replays.clear();
        for (File file : replayFolder.listFiles(file -> file.getName().toLowerCase().endsWith(".replay"))) {
            replays.add(file.getName().substring(0, file.getName().length() - 7));
        }
    }

    public Replay loadReplay(String name) {
        File replayFile = Managers.fileManager.getConfigFile("replays/" + name + ".replay");
        if (!replayFile.exists()) {
            loadReplays();
            return null;
        }
        try {
            ZipFile zip = new ZipFile(replayFile);
            JSONObject data = new JSONObject(new String(IOUtil.readZipEntry(zip.getEntry("data"), zip), StandardCharsets.UTF_8));
            Replay replay = new Replay();
            for (int i = 0; i < data.getInt("packet_num"); i++) {
                JSONObject packet = new JSONObject(new String(IOUtil.readZipEntry(zip.getEntry("Packet" + i), zip), StandardCharsets.UTF_8));
                String type = packet.getString("type");
                int id = packet.getInt("id");
                PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
                packetBuffer.writeVarInt(id);
                for (Object o : packet.getJSONArray("data")) {
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
                        default -> throw new IllegalStateException("Unexpected value: " + packetData.charAt(0));
                    }
                }
                switch (type) {
                    case "C2S" -> replay.addPacket(new C2SPacket(packetBuffer));
                    case "S2C" -> replay.addPacket(new S2CPacket(packetBuffer));
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                }
            }
            return replay;
        } catch (Exception e) {
            loadReplays();
            return null;
        }
    }
}
