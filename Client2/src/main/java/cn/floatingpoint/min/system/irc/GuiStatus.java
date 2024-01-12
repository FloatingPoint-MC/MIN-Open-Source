package cn.floatingpoint.min.system.irc;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;

public class GuiStatus extends GuiScreen {
    private final String passText, failText;
    public GuiScreen nextScreen;
    public GuiScreen previousScreen;
    private GuiButton button;
    private int pass;

    public GuiStatus(GuiScreen previousScreen, GuiScreen nextScreen, String passText, String failText) {
        this.pass = 0;
        this.previousScreen = previousScreen;
        this.nextScreen = nextScreen;
        this.passText = passText;
        this.failText = failText;
    }

    @Override
    public void initGui() {
        this.button = new GuiButton(0, width / 2 - 100, height / 2 + 15, "");
        this.button.visible = false;
        this.buttonList.add(this.button);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, new Color(0, 0, 0).getRGB());
        if (this.pass != 0) {
            int y = -25;
            this.showStatus(y);
            if (!this.button.visible) {
                this.button.visible = true;
            }
            this.button.displayString = this.pass == 1 ? this.passText : this.failText;
        } else {
            int y = -5;
            this.showStatus(y);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void showStatus(int y) {
        for (int i = Client.getStatus().size() - 1; i >= 0; i--) {
            String status = Client.getStatus().get(i);
            mc.fontRenderer.drawString(status, width / 2 - mc.fontRenderer.getStringWidth(status) / 2, height / 2 + y, 0);
            y -= mc.fontRenderer.FONT_HEIGHT + 2;
        }
    }

    public void pass() {
        this.pass = 1;
    }

    public void fail() {
        this.pass = 2;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(this.pass == 1 ? this.nextScreen : this.previousScreen);
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

    }
}
