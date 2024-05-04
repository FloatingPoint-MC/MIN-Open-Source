package cn.floatingpoint.min.system.ui.connection;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.irc.Client;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class GuiBan extends GuiScreen {
    private GuiButton button;

    @Override
    public void initGui() {
        this.buttonList.add(this.button = new GuiButton(0, width / 2 - 60, Math.min(this.height / 2 + Client.getStatus().size() * this.fontRenderer.FONT_HEIGHT / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30), 120, 20, Managers.i18NManager.getTranslation("back")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawCenteredString(this.fontRenderer, Managers.i18NManager.getTranslation("irc.connection.lost"), this.width / 2, this.height / 2 - Client.getStatus().size() * this.fontRenderer.FONT_HEIGHT / 2 - this.fontRenderer.FONT_HEIGHT * 2, 11184810);
        this.showStatus();
        if (!this.button.visible) {
            this.button.visible = true;
        }
        this.button.y = Math.min(this.height / 2 + Client.getStatus().size() * this.fontRenderer.FONT_HEIGHT / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }



    private void showStatus() {
        int y = this.height / 2 - Client.getStatus().size() * this.fontRenderer.FONT_HEIGHT / 2;
        for (String status : Client.getStatus()) {
            mc.fontRenderer.drawString(status, width / 2 - mc.fontRenderer.getStringWidth(status) / 2, y, 16777215);
            y += mc.fontRenderer.FONT_HEIGHT;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.shutdown();
        }
    }

    @Override
    public void onGuiClosed() {
        mc.shutdown();
    }
}
