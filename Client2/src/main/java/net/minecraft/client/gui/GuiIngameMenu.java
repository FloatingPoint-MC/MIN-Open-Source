package net.minecraft.client.gui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

import cn.floatingpoint.min.MIN;
import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.utils.client.WebUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.resources.I18n;

public class GuiIngameMenu extends GuiScreen {

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 - 16, I18n.format("menu.returnToMenu")));

        if (!this.mc.isIntegratedServerRunning()) {
            (this.buttonList.get(0)).displayString = I18n.format("menu.disconnect");
        }

        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 - 16, I18n.format("menu.returnToGame")));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 - 16, 98, 20, I18n.format("menu.options")));
        GuiButton guibutton = this.addButton(new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 - 16, 98, 20, I18n.format("menu.shareToLan")));
        guibutton.enabled = this.mc.isSingleplayer() && !Objects.requireNonNull(this.mc.getIntegratedServer()).getPublic();
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 - 16, 98, 20, I18n.format("gui.advancements")));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 - 16, 98, 20, I18n.format("gui.stats")));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;

            case 1:
                boolean flag = this.mc.isIntegratedServerRunning();
                button.enabled = false;
                String username = Minecraft.getMinecraft().player.getName();
                MIN.runAsync(() -> {
                    try {
                        WebUtil.getJSONFromPost("https://minserver.vlouboos.repl.co/online/deactivate?username=" + username);
                    } catch (URISyntaxException | IOException ignored) {
                    }
                });
                this.mc.world.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                Managers.replayManager.setPlaying(false);

                if (flag) {
                    this.mc.displayGuiScreen(mc.mainMenu);
                } else {
                    this.mc.displayGuiScreen(new GuiMultiplayer(mc.mainMenu));
                }

            case 2:
            case 3:
            default:
                break;

            case 4:
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                break;

            case 5:
                this.mc.displayGuiScreen(new GuiScreenAdvancements(this.mc.player.connection.getAdvancementManager()));
                break;

            case 6:
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.player.getStatFileWriter()));
                break;

            case 7:
                this.mc.displayGuiScreen(new GuiShareToLan(this));
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        super.updateScreen();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, I18n.format("menu.game"), this.width / 2, 40, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
