package cn.floatingpoint.min.system.ui.loading;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.utils.math.FunctionUtil;
import cn.floatingpoint.min.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class GuiLoading extends GuiScreen {
    private final GuiScreen nextScreen;
    private float animation;
    private int stage;

    public GuiLoading(GuiScreen nextScreen) {
        this.nextScreen = nextScreen;
    }

    @Override
    public void initGui() {
        animation = 1.0f;
        stage = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0, 0, width, height, new Color(0, 0, 0).getRGB());
        if (Managers.clientManager.firstStart) {
            GL11.glPushMatrix();
            int size = (int) (128 * animation);
            if (this.stage <= 1) {
                RenderUtil.drawImage(new ResourceLocation("min/logo.png"), (width - size) / 2, (height - size) / 2, size, size);
            } else {
                RenderUtil.drawImage(new ResourceLocation("min/square.png"), (width - size) / 2, (height - size) / 2, size, size);
            }
            GL11.glPopMatrix();
            int colorCode = 216;
            int alpha = 255;
            if (stage == 0) {
                colorCode = (int) (216 + (animation / 1.3f) * (255 - 216));
            } else if (stage == 1) {
                colorCode = 255;
                alpha = (int) (alpha * animation / 1.3f);
            } else if (stage == 2) {
                alpha = 0;
            }
            Managers.fontManager.comfortaa_25.drawCenteredString("Min Client, Max Performance", width / 2, height / 2 + 86, new Color(colorCode, colorCode, colorCode, alpha).getRGB());
            if (stage == 0) {
                animation = FunctionUtil.decreasedSpeed(animation, 1.0f, 1.25f, 0.03f * (animation - 0.25f));
            } else if (stage == 1) {
                animation = FunctionUtil.increasedSpeed(animation, 1.1f, 0.0f, 0.1f * animation);
            } else if (stage == 2) {
                animation += 0.35f;
            }
            if (stage == 0 && 1.25f - animation <= 0.0005f) {
                stage = 1;
            } else if (stage == 1 && animation <= 0.0001f) {
                animation = 0.0f;
                stage = 2;
            } else if (stage == 2) {
                if (animation > 10.0f) {
                    mc.displayGuiScreen(new GuiFirstStart(nextScreen));
                }
            }
            animation = Math.max(animation, 0.0f);
        } else {
            if (stage == 0) {
                if (animation > 0.05f) {
                    RenderUtil.drawImage(new ResourceLocation("min/logo.png"), (width - 128) / 2, (height - 128) / 2, 128, 128);
                    int alpha = (int) (255 * animation);
                    Managers.fontManager.comfortaa_25.drawCenteredString("Min Client, Max Performance", width / 2, height / 2 + 86, new Color(216, 216, 216, alpha).getRGB());
                    Gui.drawRect(width / 2 - 50, height / 2 - 20, width / 2 + 50, height / 2 + 20, new Color(0, 0, 0, 255 - alpha).getRGB());
                } else {
                    stage = 1;
                    animation = 1.0f;
                    RenderUtil.drawImage(new ResourceLocation("min/square.png"), (width - 128) / 2, (height - 128) / 2, 128, 128);
                }
                animation = FunctionUtil.decreasedSpeed(animation, 1.0f, 0.0f, 0.065f);
            } else if (stage == 1) {
                int size = Math.max((int) (128 * animation), 128);
                RenderUtil.drawImage(new ResourceLocation("min/square.png"), (width - size) / 2, (height - size) / 2, size, size);
                animation = FunctionUtil.decreasedSpeed(animation, 1.0f, 1.84375f, 0.15f);
                if (animation > 1.8425f) {
                    mc.displayGuiScreen(nextScreen);
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }
}
