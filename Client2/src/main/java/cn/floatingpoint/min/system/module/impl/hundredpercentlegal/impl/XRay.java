package cn.floatingpoint.min.system.module.impl.hundredpercentlegal.impl;

import cn.floatingpoint.min.system.module.impl.hundredpercentlegal.HundredPercentLegalModule;
import cn.floatingpoint.min.utils.render.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class XRay extends HundredPercentLegalModule {
    private static XRay instance;

    public XRay() {
        instance = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public static void doXray() {
        instance.xray();
    }

    private void xray() {
        if (!instance.isEnabled()) return;
        int diamond = new Color(0x0BE3FF).getRGB();
        int gold = new Color(0xFFFF00).getRGB();
        int lapis = new Color(0x0066FF).getRGB();
        for (int px = (int) (mc.player.posX - 10); px <= mc.player.posX + 10; px++) {
            for (int py = (int) Math.max(0, mc.player.posY - 10); py <= mc.player.posY + 10; py++) {
                for (int pz = (int) (mc.player.posZ - 10); pz <= mc.player.posZ + 10; pz++) {
                    if (px == mc.player.posX && pz == mc.player.posZ && py - mc.player.posY <= 2) continue;
                    BlockPos pos = new BlockPos(px, py, pz);
                    IBlockState state = mc.world.getBlockState(pos);
                    double x = px - mc.getRenderManager().getRenderPosX();
                    double y = py - mc.getRenderManager().getRenderPosY();
                    double z = pz - mc.getRenderManager().getRenderPosZ();
                    int blockId = Block.getIdFromBlock(state.getBlock());
                    if (blockId != 56 && blockId != 14 && blockId != 21) continue;
                    int outlineColor = switch (blockId) {
                        case 56 -> diamond;
                        case 14 -> gold;
                        case 21 -> lapis;
                        default -> throw new IllegalStateException("Unexpected value: " + blockId);
                    };
                    GL11.glPushMatrix();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableBlend();
                    GL11.glBlendFunc(770, 771);
                    GL11.glDisable(3553);
                    GL11.glEnable(2848);
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                    AxisAlignedBB blockBoundingBox = state.getBoundingBox(mc.world, pos);
                    double minX = blockBoundingBox.minX;
                    double maxX = blockBoundingBox.maxX;
                    double minY = blockBoundingBox.minY;
                    double maxY = blockBoundingBox.maxY;
                    double minZ = blockBoundingBox.minZ;
                    double maxZ = blockBoundingBox.maxZ;
                    Color color = new Color(outlineColor, true);
                    GL11.glPushMatrix();
                    GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
                    GL11.glLineWidth(2.5f);
                    RenderUtil.drawBoundingBoxOutline(new AxisAlignedBB(x + minX - 0.005, y + minY - 0.005, z + minZ - 0.005, x + maxX + 0.005, y + maxY + 0.005, z + maxZ + 0.005));
                    GL11.glPopMatrix();
                    GL11.glDisable(2848);
                    GL11.glEnable(3553);
                    GL11.glEnable(2929);
                    GL11.glDepthMask(true);
                    GL11.glPopMatrix();
                    GL11.glLineWidth(1.0F);
                }
            }
        }
    }
}
