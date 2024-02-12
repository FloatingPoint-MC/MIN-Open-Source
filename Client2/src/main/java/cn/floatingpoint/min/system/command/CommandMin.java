package cn.floatingpoint.min.system.command;

import cn.floatingpoint.min.system.replay.storage.SaveConverter;
import cn.floatingpoint.min.utils.client.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.WorldInfo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @projectName: MIN
 * @author: vlouboos
 * @date: 2023-07-20 11:55:51
 */
public class CommandMin {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean execute(String[] args) {
        if (args[0].equalsIgnoreCase("help")) {
            ChatUtil.printToChatWithPrefix("/min replay \2477 - Replay System");
            return true;
        } else if (args[0].equalsIgnoreCase("replay")) {
            if (args.length == 1) {
                ChatUtil.printToChatWithPrefix("--------------- Replay System ---------------");
                ChatUtil.printToChat(new TextComponentString("\247f/min replay record <Name>\2477 - Start or resume recording"));
                ChatUtil.printToChat(new TextComponentString("\247f/min replay stop <Name>\2477 - Stop recording"));
                ChatUtil.printToChat(new TextComponentString("\247f/min replay pause <Name>\2477 - Pause recording"));
                return true;
            } else {
                if (args[1].equalsIgnoreCase("record")) {
                    if (args.length != 3) {
                        ChatUtil.printToChatWithPrefix("\247cCorrect Usage: /min replay record <Name>");
                        return true;
                    } else {
                        String replayName = args[2];
                        if (new File(mc.gameDir, "MIN2/replay/" + replayName).exists()) {
                            ChatUtil.printToChatWithPrefix("\247cReplay existed!");
                            return true;
                        }
                        WorldInfo worldInfo = mc.world.getWorldInfo();
                        worldInfo.setWorldName(replayName);
                        worldInfo.setSpawn(mc.player.getPosition());
                        SaveConverter saveConverter = new SaveConverter(new File(mc.gameDir, "MIN2/replay"), mc.getDataFixer());
                        saveConverter.convertMapFormat(replayName, worldInfo, new IProgressUpdate() {
                            @Override
                            public void displaySavingString(String message) {
                            }

                            @Override
                            public void resetProgressAndMessage(String message) {

                            }

                            @Override
                            public void displayLoadingString(String message) {
                            }

                            @Override
                            public void setLoadingProgress(int progress) {
                            }

                            @Override
                            public void setDoneWorking() {
                            }
                        });
                        IChunkLoader iChunkLoader = saveConverter.getSaveLoader(replayName, false).getChunkLoader(mc.world.provider);
                        for (Map.Entry<Long, Chunk> entry : mc.world.getChunkProvider().loadedChunks.entrySet()) {
                            Chunk value = entry.getValue();
                            try {
                                iChunkLoader.saveChunk(mc.world, value);
                            } catch (MinecraftException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
