package cn.floatingpoint.min.system.ui.connection;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.irc.Client;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.irc.packet.impl.CPacketRegister;
import cn.floatingpoint.min.system.ui.components.ClickableButton;
import cn.floatingpoint.min.system.ui.components.InputField;
import cn.floatingpoint.min.utils.client.HWIDUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjglx.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class GuiRegister extends GuiScreen {
    private InputField username;
    private InputField password;
    private InputField confirmPassword;
    private ClickableButton register;
    private final GuiScreen parent;

    public GuiRegister(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        username = new InputField(width / 2 - 90, 70, 180, 20);
        password = new InputField(width / 2 - 90, 100, 180, 20, '*');
        confirmPassword = new InputField(width / 2 - 90, 130, 180, 20, '*');
        username.setMaxStringLength(16);
        password.setMaxStringLength(256);
        confirmPassword.setMaxStringLength(256);
        register = new ClickableButton(width / 2, 170, 100, 20, Managers.i18NManager.getTranslation("login.register.do")) {
            @Override
            public void clicked() {
                register();
            }
        };
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, new Color(50, 50, 50).getRGB());
        username.drawTextBox();
        password.drawTextBox();
        confirmPassword.drawTextBox();
        Managers.fontManager.sourceHansSansCN_Regular_34.drawCenteredString(Managers.i18NManager.getTranslation("login.registration"), width / 2, 30, -1);
        Managers.fontManager.sourceHansSansCN_Regular_20.drawCenteredString(Client.getStatus().get(0), width / 2, 50, -1);
        int white = new Color(236, 236, 236).getRGB();
        if (username.getText().isEmpty() && !username.isFocused()) {
            Managers.fontManager.sourceHansSansCN_Regular_20.drawString(Managers.i18NManager.getTranslation("login.username"), width / 2 - 84, 76, white);
        }
        if (password.getText().isEmpty() && !password.isFocused()) {
            Managers.fontManager.sourceHansSansCN_Regular_20.drawString(Managers.i18NManager.getTranslation("login.password"), width / 2 - 84, 106, white);
        }
        if (confirmPassword.getText().isEmpty() && !confirmPassword.isFocused()) {
            Managers.fontManager.sourceHansSansCN_Regular_20.drawString(Managers.i18NManager.getTranslation("login.confirm"), width / 2 - 84, 136, white);
        }
        register.drawScreen();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(parent);
        }
        if (typedChar == '\t' && (username.isFocused() || password.isFocused())) {
            username.setFocused(!username.isFocused());
            password.setFocused(!password.isFocused());
        }
        if (typedChar == '\r') {
            if (username.getText().isEmpty()) {
                username.setFocused(true);
            } else if (username.isFocused() || password.getText().isEmpty()) {
                username.setFocused(false);
                password.setFocused(true);
            } else {
                register();
            }
        }
        username.keyTyped(typedChar, keyCode);
        password.keyTyped(typedChar, keyCode);
        confirmPassword.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
        username.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);
        confirmPassword.mouseClicked(mouseX, mouseY, mouseButton);
        Client.setStatus("\247e" + Managers.i18NManager.getTranslation("idle"));
        register.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void register() {
        if (username.getText().isEmpty()) {
            Client.setStatus("\247c" + Managers.i18NManager.getTranslation("login.empty.username"));
        } else if (password.getText().isEmpty()) {
            Client.setStatus("\247c" + Managers.i18NManager.getTranslation("login.empty.password"));
        } else if (confirmPassword.getText().isEmpty()) {
            Client.setStatus("\247c" + Managers.i18NManager.getTranslation("login.empty.confirm"));
        } else if (username.getText().contains("@") || username.getText().contains(":") || username.getText().length() <= 3 || username.getText().length() > 16 || !username.getText().equals(username.getText().trim())) {
            Client.setStatus("\247c" + Managers.i18NManager.getTranslation("login.illegal.username"));
        } else if (password.getText().length() <= 6 || password.getText().length() > 255) {
            Client.setStatus("\247c" + Managers.i18NManager.getTranslation("login.illegal.password"));
        } else if (!confirmPassword.getText().equals(password.getText())) {
            Client.setStatus("\247c" + Managers.i18NManager.getTranslation("login.confirm.fail"));
        } else {
            try {
                Client.setStatus("\247f" + Managers.i18NManager.getTranslation("login.registering"));
                IRCClient.getInstance().addToSendQueue(new CPacketRegister(username.getText(), password.getText(), HWIDUtil.getHWID()));
                mc.displayGuiScreen(new GuiStatus(GuiRegister.this, parent, Managers.i18NManager.getTranslation("login.register.success"), Managers.i18NManager.getTranslation("back")));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void updateScreen() {
        username.updateCursorCounter();
        password.updateCursorCounter();
        Keyboard.enableRepeatEvents(true);
    }
}
