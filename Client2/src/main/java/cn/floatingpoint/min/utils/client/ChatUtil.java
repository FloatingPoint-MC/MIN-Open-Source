package cn.floatingpoint.min.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * @projectName: MIN
 * @author: vlouboos
 * @date: 2023-07-20 11:16:33
 */
public class ChatUtil {
    public static void printToChatWithPrefix(String message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("\247b[MIN] \247f" + message));
    }

    public static void printToChat(ITextComponent message) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(message);
    }

    public static String buildMessage(String... args) {
        StringBuilder t = new StringBuilder();
        for (String s : args) {
            t.append(s).append(" ");
        }
        boolean isNextColor = false;
        StringBuilder stringBuilder = new StringBuilder();
        for (Character c : t.toString().toCharArray()) {
            if (c.equals('&')) {
                if (!isNextColor) {
                    isNextColor = true;
                } else {
                    stringBuilder.append("&");
                    isNextColor = false;
                }
                continue;
            }
            if (isNextColor) {
                stringBuilder.append("\247");
            }
            isNextColor = false;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static String removeColor(String original) {
        original = original
                .replace("\247a", "")
                .replace("\247b", "")
                .replace("\247c", "")
                .replace("\247d", "")
                .replace("\247e", "")
                .replace("\247f", "")
                .replace("\2471", "")
                .replace("\2472", "")
                .replace("\2473", "")
                .replace("\2474", "")
                .replace("\2475", "")
                .replace("\2476", "")
                .replace("\2477", "")
                .replace("\2478", "")
                .replace("\2479", "")
                .replace("\2470", "")
                .replace("\247k", "")
                .replace("\247l", "")
                .replace("\247m", "")
                .replace("\247n", "")
                .replace("\247o", "")
                .replace("\247r", "");
        return original;
    }
}
