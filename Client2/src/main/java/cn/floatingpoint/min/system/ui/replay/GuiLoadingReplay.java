package cn.floatingpoint.min.system.ui.replay;

import cn.floatingpoint.min.management.Managers;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;

public class GuiLoadingReplay extends GuiScreen {
    public String title;
    public double percentage;

    @Override
    public void initGui() {
        title = "";
        percentage = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, new Color(0, 0, 0).getRGB());
        Managers.fontManager.sourceHansSansCN_Regular_26.drawCenteredStringWithShadow(title, width / 2, height / 2 - 7, -1);
        Gui.drawRect(width / 2 - 150, height / 2 + 5, width / 2 + 150, height / 2 + 7, new Color(110, 110, 110).getRGB());
        Gui.drawRect(width / 2 - 150, height / 2 + 5, width / 2 - 150 + (int) (percentage * 300), height / 2 + 7, new Color(216, 216, 216).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

    }
}
