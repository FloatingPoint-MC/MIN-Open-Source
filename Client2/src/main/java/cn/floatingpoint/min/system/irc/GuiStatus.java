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
    private String title;
    private int pass;

    public GuiStatus(GuiScreen previousScreen, GuiScreen nextScreen, String passText, String failText) {
        this.pass = 0;
        this.previousScreen = previousScreen;
        this.nextScreen = nextScreen;
        this.passText = passText;
        this.failText = failText;
        this.title = "";
    }

    @Override
    public void initGui() {
        this.button = new GuiButton(0, width / 2 - 100, Math.min(this.height / 2 + Client.getStatus().size() * this.fontRenderer.FONT_HEIGHT / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30), "");
        this.button.visible = false;
        this.buttonList.add(this.button);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, new Color(0, 0, 0).getRGB());
        if (!title.isEmpty()) {
            this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, this.height / 2 - Client.getStatus().size() * this.fontRenderer.FONT_HEIGHT / 2 - this.fontRenderer.FONT_HEIGHT * 2, 11184810);
        }
        if (this.pass != 0) {
            this.showStatus();
            if (!this.button.visible) {
                this.button.visible = true;
            }
            this.button.displayString = this.pass == 1 ? this.passText : this.failText;
            this.button.y = Math.min(this.height / 2 + Client.getStatus().size() * this.fontRenderer.FONT_HEIGHT / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30);
        } else {
            this.showStatus();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void showStatus() {
        int y = this.height / 2 - Client.getStatus().size() * this.fontRenderer.FONT_HEIGHT / 2;
        for (String status : Client.getStatus()) {
            mc.fontRenderer.drawString(status, width / 2 - mc.fontRenderer.getStringWidth(status) / 2, y, 16777215);
            y += mc.fontRenderer.FONT_HEIGHT;
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

    public String getTitle() {
        return title;
    }

    public GuiStatus setTitle(String title) {
        this.title = title;
        return this;
    }
}
