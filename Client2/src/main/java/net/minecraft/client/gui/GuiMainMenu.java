package net.minecraft.client.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class GuiMainMenu extends GuiScreen {
    protected static final Logger LOGGER = LogManager.getLogger();

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

    }
}
