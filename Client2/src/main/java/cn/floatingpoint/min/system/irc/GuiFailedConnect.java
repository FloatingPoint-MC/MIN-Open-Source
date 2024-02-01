package cn.floatingpoint.min.system.irc;

import cn.floatingpoint.min.management.Managers;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;

public class GuiFailedConnect extends GuiScreen {
    private final String text;

    public GuiFailedConnect(String text) {
        this.text = text;
    }

    @Override
    public void initGui() {
        mc.setIngameNotInFocus();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 48, Managers.i18NManager.getTranslation("irc.fail.exit")));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        Managers.fontManager.sourceHansSansCN_Regular_20.drawCenteredString(this.text, this.width / 2, this.height / 2 - 22, new Color(223, 74, 74).getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.shutdown();
        }
        super.actionPerformed(button);
    }

    @Override
    public void onGuiClosed() {
        mc.shutdown();
        super.onGuiClosed();
    }
}
