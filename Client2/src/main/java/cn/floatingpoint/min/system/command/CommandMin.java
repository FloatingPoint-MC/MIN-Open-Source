package cn.floatingpoint.min.system.command;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.utils.client.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

import java.io.File;

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
                        Managers.replayManager.startRecording(replayName);
                    }
                }
            }
        }
        return false;
    }
}
