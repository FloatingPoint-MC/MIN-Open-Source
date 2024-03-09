package cn.floatingpoint.min.system.ui.connection;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.irc.Client;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.ui.components.ClickableButton;
import cn.floatingpoint.min.system.ui.components.InputField;
import cn.floatingpoint.min.utils.client.RegistryEditUtil;
import cn.floatingpoint.min.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjglx.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class GuiLogin extends GuiScreen {
    private static int startUp;
    private InputField username;
    private InputField password;
    private ClickableButton login;
    private boolean rememberPassword;
    private final GuiScreen nextScreen;

    public GuiLogin(GuiScreen... screens) {
        startUp = 255;
        if (screens.length > 0) {
            nextScreen = screens[0];
        } else {
            nextScreen = Minecraft.getMinecraft().mainMenu;
        }
    }

    @Override
    public void initGui() {
        username = new InputField(width / 2 - 90, 70, 180, 20);
        password = new InputField(width / 2 - 90, 100, 180, 20, '*');
        this.rememberPassword = RegistryEditUtil.getValue("FloatingPoint/MINClient", "remember").equalsIgnoreCase("1");
        this.username.setText(RegistryEditUtil.getValue("FloatingPoint/MINClient", "username"));
        if (this.rememberPassword) {
            this.password.setText(RegistryEditUtil.getValue("FloatingPoint/MINClient", "password"));
        }
        username.setMaxStringLength(16);
        password.setMaxStringLength(256);
        login = new ClickableButton(width / 2, 150, 100, 20, Managers.i18NManager.getTranslation("login.login")) {
            @Override
            public void clicked() {
                login();
            }
        };
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, new Color(50, 50, 50).getRGB());
        username.drawTextBox();
        password.drawTextBox();
        Managers.fontManager.sourceHansSansCN_Regular_34.drawCenteredString(Managers.i18NManager.getTranslation("login.title"), width / 2, 30, -1);
        Managers.fontManager.sourceHansSansCN_Regular_20.drawCenteredString(Client.getStatus().get(0), width / 2, 50, -1);
        int white = new Color(236, 236, 236).getRGB();
        int grey = new Color(158, 158, 158).getRGB();
        if (username.getText().isEmpty() && !username.isFocused()) {
            Managers.fontManager.sourceHansSansCN_Regular_20.drawString(Managers.i18NManager.getTranslation("login.username"), width / 2 - 84, 76, white);
        }
        if (password.getText().isEmpty() && !password.isFocused()) {
            Managers.fontManager.sourceHansSansCN_Regular_20.drawString(Managers.i18NManager.getTranslation("login.password"), width / 2 - 84, 106, white);
        }
        RenderUtil.drawRoundedRect(width / 2 - 88, 127, width / 2 - 80, 135, 4, white);
        Managers.fontManager.sourceHansSansCN_Regular_18.drawString(Managers.i18NManager.getTranslation("login.remember"), width / 2 - 77, 127, white);
        if (this.rememberPassword) {
            RenderUtil.drawRoundedRect(width / 2 - 86, 129, width / 2 - 82, 133, 2, grey);
        }
        login.drawScreen();
        String regText = Managers.i18NManager.getTranslation("login.register.to");
        int regLength = Managers.fontManager.sourceHansSansCN_Regular_18.getStringWidth(regText);
        if (!isHovered(width / 2 - 89, 164, width / 2 - 89 + regLength, 173, mouseX, mouseY)) {
            Managers.fontManager.sourceHansSansCN_Regular_18.drawString("\247n" + regText, width / 2 - 89, 164, grey);
        } else {
            Managers.fontManager.sourceHansSansCN_Regular_18.drawString("\247n" + regText, width / 2 - 89, 164, white);
        }
        if (startUp > 0) {
            drawRect(0, 0, width, height, new Color(0, 0, 0, startUp).getRGB());
            startUp -= 10;
        }
        if (startUp < 0) {
            startUp = 0;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.shutdown();
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
                this.login();
            }
        }
        username.keyTyped(typedChar, keyCode);
        password.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(mouseX, mouseY, mouseButton);
        this.password.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovered(width / 2 - 88, 127, width / 2 - 80, 135, mouseX, mouseY)) {
            this.rememberPassword = !this.rememberPassword;
        } else {
            String regText = Managers.i18NManager.getTranslation("login.register.to");
            int regLength = Managers.fontManager.sourceHansSansCN_Regular_18.getStringWidth(regText);
            if (isHovered(width / 2 - 89, 164, width / 2 - 89 + regLength, 173, mouseX, mouseY)) {
                mc.displayGuiScreen(new GuiRegister(this));
            } else {
                Client.setStatus("\247e" + Managers.i18NManager.getTranslation("idle"));
            }
        }
        login.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void login() {
        if (this.username.getText().isEmpty()) {
            Client.setStatus("\247c" + Managers.i18NManager.getTranslation("login.empty.username"));
        } else if (this.password.getText().isEmpty()) {
            Client.setStatus("\247c" + Managers.i18NManager.getTranslation("login.empty.password"));
        } else {
            if (Client.getUsername() == null || !Client.isLoggedIn()) {
                Client.setStatus("\247f" + Managers.i18NManager.getTranslation("login.logging"));
                Client.setUsername(username.getText());
                Client.setPassword(password.getText());
                RegistryEditUtil.writeValue("FloatingPoint/MINClient", "username", this.username.getText());
                RegistryEditUtil.writeValue("FloatingPoint/MINClient", "password", this.rememberPassword ? this.password.getText() : "None");
                RegistryEditUtil.writeValue("FloatingPoint/MINClient", "remember", this.rememberPassword ? "1" : "0");
                mc.displayGuiScreen(new GuiStatus(this, nextScreen, Managers.i18NManager.getTranslation("back"), Managers.i18NManager.getTranslation("back")).setTitle(""));
                IRCClient.getInstance().enableIRC();
            } else {
                mc.displayGuiScreen(nextScreen);
            }
        }
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
        Keyboard.enableRepeatEvents(true);
    }
}
