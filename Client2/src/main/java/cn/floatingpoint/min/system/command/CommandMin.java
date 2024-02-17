package cn.floatingpoint.min.system.command;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.utils.client.ChatUtil;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;

/**
 * @projectName: MIN
 * @author: vlouboos
 * @date: 2023-07-20 11:55:51
 */
public class CommandMin {
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
                    if (args.length < 3) {
                        ChatUtil.printToChatWithPrefix("\247cCorrect Usage: /min replay record <Name>");
                    } else {
                        String replayName = ChatUtil.buildMessage(Arrays.copyOfRange(args, 2, args.length));
                        if (Managers.fileManager.getConfigFile("replay/" + replayName + ".replay", false).exists()) {
                            ChatUtil.printToChatWithPrefix("\247cReplay existed!");
                        } else {
                            Managers.replayManager.startRecording(replayName);
                        }
                    }
                    return true;
                } else if (args[1].equalsIgnoreCase("stop")) {
                    if (args.length < 3) {
                        ChatUtil.printToChatWithPrefix("\247cCorrect Usage: /min replay stop <Name>");
                    } else {
                        String replayName = ChatUtil.buildMessage(Arrays.copyOfRange(args, 2, args.length));
                        Managers.replayManager.stopRecording(replayName);
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
