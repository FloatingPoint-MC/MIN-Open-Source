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

    /**
     * Timer used to rotate the panorama, increases every tick.
     */
    private float panoramaTimer;

    /**
     * Draws the main menu panorama
     */
    private void drawPanorama() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        int j = 64;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if (custompanoramaproperties != null) {
            j = custompanoramaproperties.getBlur1();
        }

        for (int k = 0; k < j; ++k) {
            GlStateManager.pushMatrix();
            float f = ((float) (k % 8) / 8.0F - 0.5F) / 64.0F;
            float f1 = ((float) (k / 8) / 8.0F - 0.5F) / 64.0F;
            GlStateManager.translate(f, f1, 0.0F);
            GlStateManager.rotate(MathHelper.sin(this.panoramaTimer / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-this.panoramaTimer * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int l = 0; l < 6; ++l) {
                GlStateManager.pushMatrix();

                if (l == 1) {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 3) {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (l == 4) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (l == 5) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                ResourceLocation[] aresourcelocation = TITLE_PANORAMA_PATHS;

                if (custompanoramaproperties != null) {
                    aresourcelocation = custompanoramaproperties.getPanoramaLocations();
                }

                this.mc.getTextureManager().bindTexture(aresourcelocation[l]);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                int i1 = 255 / (k + 1);
                bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, i1).endVertex();
                bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, i1).endVertex();
                bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, i1).endVertex();
                bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, i1).endVertex();
                tessellator.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
    }



    /**
     * Rotate and blurs the skybox view in the main menu
     */
    private void rotateAndBlurSkybox() {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        GlStateManager.glTexParameteri(3553, 10241, 9729);
        GlStateManager.glTexParameteri(3553, 10240, 9729);
        GlStateManager.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.disableAlpha();
        int j = 3;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if (custompanoramaproperties != null) {
            j = custompanoramaproperties.getBlur2();
        }

        for (int k = 0; k < j; ++k) {
            float f = 1.0F / (float) (k + 1);
            int l = this.width;
            int i1 = this.height;
            float f1 = (float) (k - 1) / 256.0F;
            bufferbuilder.pos(l, i1, this.zLevel).tex(0.0F + f1, 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            bufferbuilder.pos(l, 0.0D, this.zLevel).tex(1.0F + f1, 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, this.zLevel).tex(1.0F + f1, 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
            bufferbuilder.pos(0.0D, i1, this.zLevel).tex(0.0F + f1, 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
        }

        tessellator.draw();
        GlStateManager.enableAlpha();
        GlStateManager.colorMask(true, true, true, true);
    }

    private void renderSkybox() {
        this.mc.getFramebuffer().unbindFramebuffer();
        GlStateManager.viewport(0, 0, 256, 256);
        this.drawPanorama();
        this.rotateAndBlurSkybox();
        int i = 3;
        CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

        if (custompanoramaproperties != null) {
            i = custompanoramaproperties.getBlur3();
        }

        for (int j = 0; j < i; ++j) {
            this.rotateAndBlurSkybox();
            this.rotateAndBlurSkybox();
        }

        this.mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        float f2 = 120.0F / (float) (Math.max(this.width, this.height));
        float f = (float) this.height * f2 / 256.0F;
        float f1 = (float) this.width * f2 / 256.0F;
        int k = this.width;
        int l = this.height;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0D, l, this.zLevel).tex(0.5F - f, 0.5F + f1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos(k, l, this.zLevel).tex(0.5F - f, 0.5F - f1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos(k, 0.0D, this.zLevel).tex(0.5F + f, 0.5F - f1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, this.zLevel).tex(0.5F + f, 0.5F + f1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        tessellator.draw();
    }

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
            this.panoramaTimer += partialTicks;
            GlStateManager.disableAlpha();
            this.renderSkybox();
            GlStateManager.enableAlpha();
            int l = -2130706433;
            int i1 = 16777215;
            int j1 = 0;
            int k1 = Integer.MIN_VALUE;
            CustomPanoramaProperties custompanoramaproperties = CustomPanorama.getCustomPanoramaProperties();

            if (custompanoramaproperties != null) {
                l = custompanoramaproperties.getOverlay1Top();
                i1 = custompanoramaproperties.getOverlay1Bottom();
                j1 = custompanoramaproperties.getOverlay2Top();
                k1 = custompanoramaproperties.getOverlay2Bottom();
            }

            if (l != 0 || i1 != 0) {
                this.drawGradientRect(0, 0, this.width, this.height, l, i1);
            }

            if (j1 != 0 || k1 != 0) {
                this.drawGradientRect(0, 0, this.width, this.height, j1, k1);
            }
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

