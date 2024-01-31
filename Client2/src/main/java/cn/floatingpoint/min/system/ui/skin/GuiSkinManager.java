package cn.floatingpoint.min.system.ui.skin;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.irc.Client;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.irc.packet.impl.CPacketSkin;
import cn.floatingpoint.min.system.ui.components.InputField;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;

public class GuiSkinManager extends GuiScreen {
    private InputField input;

    @Override
    public void initGui() {
        input = new InputField(width / 2 - 110, height / 2 - 30, 220, 20);
        input.setMaxStringLength(256);
        Client.setStatus("\247e" + Managers.i18NManager.getTranslation("idle"));
        this.buttonList.add(new GuiButton(0, width / 2 - 110, height / 2 + 10, 100, 20, Managers.i18NManager.getTranslation("skin.buy")));
        this.buttonList.add(new GuiButton(1, width / 2 + 10, height / 2 + 10, 100, 20, Managers.i18NManager.getTranslation("skin.apply")));
        this.buttonList.add(new GuiButton(2, width / 2 - 110, height / 2 + 40, 220, 20, Managers.i18NManager.getTranslation("back")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, new Color(44, 44, 44).getRGB());
        Managers.fontManager.sourceHansSansCN_Regular_26.drawCenteredString(Client.getStatus().get(0), width / 2, height / 2 - 50, new Color(216, 216, 216).getRGB());
        input.drawTextBox();
        if (input.getText().isEmpty()) {
            Managers.fontManager.sourceHansSansCN_Regular_18.drawString(Managers.i18NManager.getTranslation("skin.tip"), width / 2 - 104, height / 2 - 24, new Color(192, 192, 192).getRGB());
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            if (input.getText().isEmpty()) {
                Client.setStatus("\247c" + Managers.i18NManager.getTranslation("skin.empty.code"));
            } else {
                Client.setStatus("\247e" + Managers.i18NManager.getTranslation("skin.uploading"));
                IRCClient.getInstance().addToSendQueue(new CPacketSkin(CPacketSkin.Action.BUY, input.getText()));
            }
        } else if (button.id == 1) {
            if (input.getText().isEmpty()) {
                Client.setStatus("\247c" + Managers.i18NManager.getTranslation("skin.empty.username"));
            } else {
                Client.setStatus("\247e" + Managers.i18NManager.getTranslation("skin.uploading"));
                IRCClient.getInstance().addToSendQueue(new CPacketSkin(CPacketSkin.Action.CHANGE, input.getText()));
            }
        } else if (button.id == 2) {
            mc.displayGuiScreen(mc.mainMenu);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        input.mouseClicked(mouseX, mouseY, mouseButton);
        Client.setStatus("\247e" + Managers.i18NManager.getTranslation("idle"));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        input.keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }
}
