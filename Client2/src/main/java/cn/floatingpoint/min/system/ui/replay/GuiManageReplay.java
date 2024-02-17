package cn.floatingpoint.min.system.ui.replay;

import java.io.IOException;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiManageReplay extends GuiScreen {

    /**
     * The screen to return to when this closes (always Main Menu).
     */
    protected GuiScreen prevScreen;
    protected String title = "Select Replay";
    private GuiButton deleteButton;
    private GuiButton selectButton;
    private GuiListReplay selectionList;

    public GuiManageReplay(GuiScreen screenIn) {
        this.prevScreen = screenIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.title = "Select Replay";
        this.selectionList = new GuiListReplay(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
        this.postInit();
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.selectionList.handleMouseInput();
    }

    public void postInit() {
        this.selectButton = this.addButton(new GuiButton(1, this.width / 2 - 154, this.height - 52, 307, 20, I18n.format("selectWorld.select")));
        this.deleteButton = this.addButton(new GuiButton(2, this.width / 2 - 154, this.height - 28, 150, 20, I18n.format("selectWorld.delete")));
        this.addButton(new GuiButton(0, this.width / 2 + 3, this.height - 28, 150, 20, I18n.format("gui.cancel")));
        this.selectButton.enabled = false;
        this.deleteButton.enabled = false;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            GuiListReplayEntry replay = this.selectionList.getSelectedReplay();

            if (button.id == 2) {
                if (replay != null) {
                    replay.deleteWorld();
                }
            } else if (button.id == 1) {
                if (replay != null) {
                    replay.joinWorld();
                }
            }  else if (button.id == 0) {
                this.mc.displayGuiScreen(this.prevScreen);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.selectionList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.selectionList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.selectionList.mouseReleased(mouseX, mouseY, state);
    }

    public void selectReplay(@Nullable GuiListReplayEntry entry) {
        boolean flag = entry != null;
        this.selectButton.enabled = flag;
        this.deleteButton.enabled = flag;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == -1) {
            mc.displayGuiScreen(prevScreen);
        }
    }
}
