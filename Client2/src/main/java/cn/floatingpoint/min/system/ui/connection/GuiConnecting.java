package cn.floatingpoint.min.system.ui.connection;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class GuiConnecting extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, width, height, new Color(0, 0, 0).getRGB());
        GL11.glPushMatrix();
        int size = 128;
        RenderUtil.drawImage(new ResourceLocation("min/logo.png"), (width - size) / 2, (height - size) / 2, size, size);
        GL11.glPopMatrix();
        int colorCode = 216;
        int alpha = 255;
        Managers.fontManager.comfortaa_25.drawCenteredString(Managers.i18NManager.getTranslation("irc.connecting"), width / 2, height / 2 + 86, new Color(colorCode, colorCode, colorCode, alpha).getRGB());
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }
}
