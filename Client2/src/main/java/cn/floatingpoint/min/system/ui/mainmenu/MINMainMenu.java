package cn.floatingpoint.min.system.ui.mainmenu;

import cn.floatingpoint.min.MIN;
import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.ui.skin.GuiSkinManager;
import cn.floatingpoint.min.utils.math.FunctionUtil;
import cn.floatingpoint.min.utils.render.RenderUtil;
import com.google.common.util.concurrent.Runnables;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.optifine.CustomPanorama;
import net.optifine.CustomPanoramaProperties;
import org.lwjglx.input.Mouse;
import org.lwjglx.util.glu.Project;

import java.awt.*;
import java.io.IOException;

/**
 * @projectName: MIN
 * @author: vlouboos
 * @date: 2023-12-23 20:52:33
 */
public class MINMainMenu extends GuiMainMenu {
    private int widthCopyright;
    private int widthCopyrightRest;
    private int multiplayerAlpha, optionsAlpha, exitAlpha, skinAlpha;

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        DynamicTexture viewportTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);
        widthCopyright = Managers.fontManager.sourceHansSansCN_Regular_20.getStringWidth("Copyright Mojang AB. Addons by FloatingPoint-MC!");
        widthCopyrightRest = width - widthCopyright - 2;
        multiplayerAlpha = 0;
        optionsAlpha = 0;
        exitAlpha = 0;
        if (init) {
            stage = 0;
            alpha = 250;
            init = false;
        }
    }

    public static boolean init = true;
    private static int alpha = 0;
    private static int stage;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (stage == 0) {
            if (alpha > 0) {
                drawRect(0, 0, width, height, new Color(50, 50, 50, 255 - alpha).getRGB());
                RenderUtil.drawImage(new ResourceLocation("min/square.png"), width / 2 - 118, height / 2 - 118, 236, 236);
                alpha -= 25;
            } else {
                drawRect(0, 0, width, height, new Color(50, 50, 50, 255).getRGB());
                RenderUtil.drawImage(new ResourceLocation("min/square.png"), width / 2 - 118, height / 2 - 118, 236, 236);
                alpha = 236;
                stage = 1;
            }
        } else if (stage == 1) {
            drawRect(0, 0, width, height, new Color(50, 50, 50, 255).getRGB());
            RenderUtil.drawImage(new ResourceLocation("min/square.png"), (width - alpha) / 2, (height - alpha) / 2, alpha, alpha);
            alpha = FunctionUtil.decreasedSpeed(alpha, 236, 200, 4.0f);
            if (alpha == 200) {
                stage = 2;
                alpha = 250;
            }
        } else {
            this.renderBackground(this.width, this.height, partialTicks);
            RenderUtil.drawImage(new ResourceLocation("min/uis/mainmenu/multiplayer.png"), width / 2 - 60, height / 2 - 12, 24, 24);
            RenderUtil.drawImage(new ResourceLocation("min/uis/mainmenu/options.png"), width / 2 - 12, height / 2 - 12, 24, 24);
            RenderUtil.drawImage(new ResourceLocation("min/uis/mainmenu/exit.png"), width / 2 + 36, height / 2 - 12, 24, 24);

            RenderUtil.drawImage(new ResourceLocation("min/uis/mainmenu/skin.png"), width - 24, 0, 24, 24);
            String s = "MIN Client(Minecraft 1.12.2)";
            s = s + ("release".equalsIgnoreCase(mc.getVersionType()) ? "" : "/" + mc.getVersionType());

            Managers.fontManager.sourceHansSansCN_Regular_20.drawString("Version: " + MIN.VERSION + "(Released on 2024/3/17)", 2, 2, -1);

            Managers.fontManager.sourceHansSansCN_Regular_20.drawString(s, 2, height - 10, -1);
            Managers.fontManager.sourceHansSansCN_Regular_20.drawString("Copyright Mojang AB. Addons by FloatingPoint-MC!", widthCopyrightRest, height - 10, -1);

            if (mouseX > widthCopyrightRest && mouseX < widthCopyrightRest + widthCopyright && mouseY > height - 10 && mouseY < height && Mouse.isInsideWindow()) {
                drawRect(widthCopyrightRest, height - 1, widthCopyrightRest + widthCopyright, height, -1);
            }

            if (isHovered(width / 2 - 60, height / 2 - 12, width / 2 - 36, height / 2 + 12, mouseX, mouseY)) {
                if (multiplayerAlpha < 250) {
                    multiplayerAlpha += 50;
                }
            } else if (multiplayerAlpha > 0) {
                multiplayerAlpha -= 50;
            }

            if (multiplayerAlpha > 0) {
                Managers.fontManager.sourceHansSansCN_Regular_18.drawCenteredString(I18n.format("menu.multiplayer"), width / 2 - 48, height / 2 + 14, new Color(216, 216, 216, multiplayerAlpha).getRGB());
            }

            if (isHovered(width / 2 - 12, height / 2 - 12, width / 2 + 12, height / 2 + 12, mouseX, mouseY)) {
                if (optionsAlpha < 250) {
                    optionsAlpha += 50;
                }
            } else if (optionsAlpha > 0) {
                optionsAlpha -= 50;
            }

            if (optionsAlpha > 0) {
                Managers.fontManager.sourceHansSansCN_Regular_18.drawCenteredString(I18n.format("menu.options"), width / 2, height / 2 + 14, new Color(216, 216, 216, optionsAlpha).getRGB());
            }

            if (isHovered(width / 2 + 36, height / 2 - 12, width / 2 + 60, height / 2 + 12, mouseX, mouseY)) {
                if (exitAlpha < 250) {
                    exitAlpha += 50;
                }
            } else if (exitAlpha > 0) {
                exitAlpha -= 50;
            }

            if (exitAlpha > 0) {
                Managers.fontManager.sourceHansSansCN_Regular_18.drawCenteredString(I18n.format("menu.quit"), width / 2 + 48, height / 2 + 14, new Color(216, 216, 216, exitAlpha).getRGB());
            }

            if (isHovered(width - 24, 0, width, 24, mouseX, mouseY)) {
                if (skinAlpha < 250) {
                    skinAlpha += 50;
                }
            } else if (skinAlpha > 0) {
                skinAlpha -= 50;
            }

            if (skinAlpha > 0) {
                String text = Managers.i18NManager.getTranslation("menu.skin");
                Managers.fontManager.sourceHansSansCN_Regular_18.drawString(text, width - Managers.fontManager.sourceHansSansCN_Regular_18.getStringWidth(text), 24, new Color(216, 216, 216, skinAlpha).getRGB());
            }

            if (alpha > 0) {
                drawRect(0, 0, width, height, new Color(50, 50, 50, alpha).getRGB());
                alpha -= 15;
            }
            RenderUtil.drawImage(new ResourceLocation("min/square.png"), width / 2 - 100, height / 2 - 100, 200, 200);
        }
    }

    @Override
    @SuppressWarnings("all")
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (stage != 2) return;
        if (isHovered(width / 2 - 60, height / 2 - 12, width / 2 - 36, height / 2 + 12, mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (isHovered(width / 2 - 12, height / 2 - 12, width / 2 + 12, height / 2 + 12, mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }
        if (isHovered(width / 2 + 36, height / 2 - 12, width / 2 + 60, height / 2 + 12, mouseX, mouseY)) {
            mc.shutdown();
        }
        if (isHovered(width - 24, 0, width, 24, mouseX, mouseY)) {
            mc.displayGuiScreen(new GuiSkinManager());
        }
        if (mouseX > widthCopyrightRest && mouseX < widthCopyrightRest + widthCopyright && mouseY > height - 10 && mouseY < height) {
            mc.displayGuiScreen(new GuiWinGame(false, Runnables.doNothing()));
        }
    }
}

