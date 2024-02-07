package net.optifine.gui;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiVideoSettings;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GuiChatOF extends GuiChat {

    public GuiChatOF(GuiChat guiChat) {
        super(GuiVideoSettings.getGuiChatText(guiChat));
    }

    /**
     * Used to add chat messages to the client's GuiChat.
     */
    public void sendChatMessage(@Nonnull String msg) {
        if (this.checkCustomCommand(msg)) {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        } else {
            super.sendChatMessage(msg);
        }
    }

    private boolean checkCustomCommand(String msg) {
        if (msg == null) {
            return false;
        } else {
            msg = msg.trim();

            if (msg.equals("/reloadShaders")) {
                if (Config.isShaders()) {
                    Shaders.uninit();
                    Shaders.loadShaderPack();
                }

                return true;
            } else if (msg.equals("/reloadChunks")) {
                this.mc.renderGlobal.loadRenderers();
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Sets the list of tab completions, as long as they were previously requested.
     */
    public void setCompletions(@Nonnull String... newCompletions) {
        String s = GuiVideoSettings.getGuiChatText(this);

        if ("/reloadShaders".startsWith(s)) {
            newCompletions = (String[]) Config.addObjectToArray(newCompletions, "/reloadShaders");
        }

        if ("/reloadChunks".startsWith(s)) {
            newCompletions = (String[]) Config.addObjectToArray(newCompletions, "/reloadChunks");
        }

        super.setCompletions(newCompletions);
    }
}
