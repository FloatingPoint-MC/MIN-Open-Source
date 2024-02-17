package cn.floatingpoint.min.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @projectName: MIN
 * @author: vlouboos
 * @date: 2023-07-20 11:16:33
 */
public class ChatUtil {
    private static final ArrayList<ITextComponent> storedMessages = new ArrayList<>();

    public static void refreshMessage() {
        storedMessages.forEach(Minecraft.getMinecraft().ingameGUI.getChatGUI()::printChatMessage);
        storedMessages.clear();
    }

    public static void printToChatWithPrefix(String message) {
        if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().ingameGUI != null) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("\247b[MIN] \247f" + message));
        } else {
            storedMessages.add(new TextComponentString("\247b[MIN] \247f" + message));
        }
    }

    public static void printToChat(ITextComponent message) {
        if (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().ingameGUI != null) {
            Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(message);
        } else {
            storedMessages.add(message);
        }
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
        return stringBuilder.toString().trim();
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

    @Nullable
    public static String[] stretchChatLines(String original) {
        if (original.isEmpty()) return null;
        StringBuilder[] lineBuilders = new StringBuilder[4]; // The 8x4-text of total string
        for (int i = 0; i < 4; i++) {
            lineBuilders[i] = new StringBuilder();
        }
        String[] lines = new String[4];
        for (char c : original.toCharArray()) { // Draw every char, graphics 2d sucks with whole string
            // Use Graphics 2D to draw the texts
            int width = 6 + (Minecraft.getMinecraft().fontRenderer.getStringWidth(String.valueOf(c)) - 6) * 2;
            BufferedImage image = new BufferedImage(width, 16, BufferedImage.TYPE_INT_RGB); // A 12x16-pixel char
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, 16);
            Font font = new Font("Courier", Font.PLAIN, 12);
            graphics.setFont(font);
            graphics.setColor(new Color(0, 0, 0));
            graphics.drawString(String.valueOf(c), 0, 12);
            int[][] hasPoint = new int[width][16]; // A fake canvas
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < 16; y++) {
                    hasPoint[x][y] = (image.getRGB(x, y) == Color.WHITE.getRGB()) ? 0 : 1;
                }
            }
            graphics.dispose();

            for (int line = 0; line < 4; line++) { // Convert 16-line matrix to a 4-line text
                for (int x = 0; x < width / 2; x++) { // Convert 16-width matrix to an 6-width text
                    lineBuilders[line].append((char) (10240 + getBrailleIndex(line, hasPoint, x * 2)));
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            lines[i] = lineBuilders[i].toString();
        }
        return lines;
    }

    private static int getBrailleIndex(int line, int[][] hasPoint, int x) {
        int y = line * 4;
        return hasPoint[x][y] +
                hasPoint[x][y + 1] * 2 +
                hasPoint[x][y + 2] * 4 +
                hasPoint[x][y + 3] * 64 +
                hasPoint[x + 1][y] * 8 +
                hasPoint[x + 1][y + 1] * 16 +
                hasPoint[x + 1][y + 2] * 32 +
                hasPoint[x + 1][y + 3] * 128;
    }
}
