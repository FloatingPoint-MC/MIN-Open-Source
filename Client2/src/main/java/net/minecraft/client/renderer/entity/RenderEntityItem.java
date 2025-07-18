package net.minecraft.client.renderer.entity;

import java.util.Objects;
import java.util.Random;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.boost.EntityCulling;
import cn.floatingpoint.min.system.module.impl.render.impl.ItemPhysics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderEntityItem extends Render<EntityItem> {
    private final RenderItem itemRenderer;
    private final Random random = new Random();

    public RenderEntityItem(RenderManager renderManagerIn, RenderItem p_i46167_2_) {
        super(renderManagerIn);
        this.itemRenderer = p_i46167_2_;
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    private int transformModelCount(EntityItem itemIn, double x, double y, double z, float partialTicks, IBakedModel model) {
        ItemStack itemstack = itemIn.getItem();
        Item item = itemstack.getItem();

        if (item == Item.getItemFromBlock(Blocks.AIR)) {
            return 0;
        } else {
            boolean flag = model.isGui3d();
            int i = this.getModelCount(itemstack);
            float f1 = MathHelper.sin(((float) itemIn.getAge() + partialTicks) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
            float f2 = model.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
            GlStateManager.translate((float) x, (float) y + f1 + 0.25F * f2, (float) z);

            if (Managers.moduleManager.renderModules.get("ItemPhysics").isEnabled()) {
                GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            } else {
                if (flag || this.renderManager.options != null) {
                    float f3 = (((float) itemIn.getAge() + partialTicks) / 20.0F + itemIn.hoverStart) * (180F / (float) Math.PI);
                    GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
                }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return i;
        }
    }

    private int getModelCount(ItemStack stack) {
        int i = 1;

        if (stack.getCount() > 48) {
            i = 5;
        } else if (stack.getCount() > 32) {
            i = 4;
        } else if (stack.getCount() > 16) {
            i = 3;
        } else if (stack.getCount() > 1) {
            i = 2;
        }

        return i;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (EntityCulling.shouldCancelRenderItem(entity)) return;
        ItemStack itemstack = entity.getItem();
        int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata();
        this.random.setSeed(i);
        boolean flag = false;

        if (this.bindEntityTexture(entity)) {
            Objects.requireNonNull(this.renderManager.renderEngine.getTexture(Objects.requireNonNull(this.getEntityTexture(entity)))).setBlurMipmap(false, false);
            flag = true;
        }

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, entity.world, null);
        int j;
        if (Managers.moduleManager.renderModules.get("ItemPhysics").isEnabled() && !ItemPhysics.twoD.getValue()) {
            j = getModelCount(itemstack);
        } else {
            j = this.transformModelCount(entity, x, y, z, partialTicks, ibakedmodel);
        }
        float f = ibakedmodel.getItemCameraTransforms().ground.scale.x;
        float f1 = ibakedmodel.getItemCameraTransforms().ground.scale.y;
        float f2 = ibakedmodel.getItemCameraTransforms().ground.scale.z;
        boolean flag1 = ibakedmodel.isGui3d();

        if (Managers.moduleManager.renderModules.get("ItemPhysics").isEnabled() && !ItemPhysics.twoD.getValue()) {
            GlStateManager.translate((float) x, (float) y, (float) z);
            GlStateManager.scale(f, f1, f2);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(entity.rotationYaw, 0.0F, 0.0F, 1.0F);
            if (flag1) {
                GlStateManager.translate(0.0D, 0.0D, -0.08D);
            } else {
                GlStateManager.translate(0.0D, 0.0D, -0.04D);
            }
            if (flag1 || Minecraft.getMinecraft().getRenderManager().options != null) {
                double rotation;
                if (flag1) {
                    if (!entity.onGround) {
                        rotation = 1.1D;
                        entity.rotationPitch = (float) ((double) entity.rotationPitch + rotation);
                    }
                } else if (!Double.isNaN(entity.posX) && !Double.isNaN(entity.posY) && !Double.isNaN(entity.posZ) && entity.world != null) {
                    if (entity.onGround) {
                        entity.rotationPitch = 0.0F;
                    } else {
                        rotation = 1.1D;
                        entity.rotationPitch = (float) ((double) entity.rotationPitch + rotation);
                    }
                }
                GlStateManager.rotate(entity.rotationPitch, 1.0F, 0.0F, 0.0F);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            for (int k = 0; k < j; ++k) {
                GlStateManager.pushMatrix();
                if (flag1) {
                    if (k > 0) {
                        float f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f9 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        GlStateManager.translate(f7, f9, f6);
                    }

                    Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                } else {
                    Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                    GlStateManager.translate(0.0F, 0.0F, 0.05375F);
                }
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Objects.requireNonNull(this.getRenderManager().renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)).restoreLastBlurMipmap();
            return;
        }

        if (!flag1) {
            float f3 = -0.0F * (float) (j - 1) * 0.5F * f;
            float f4 = -0.0F * (float) (j - 1) * 0.5F * f1;
            float f5 = -0.09375F * (float) (j - 1) * 0.5F * f2;
            GlStateManager.translate(f3, f4, f5);
        }

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        for (int k = 0; k < j; ++k) {
            GlStateManager.pushMatrix();
            if (flag1) {

                if (k > 0) {
                    float f7 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f9 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    GlStateManager.translate(f7, f9, f6);
                }

                ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                this.itemRenderer.renderItem(itemstack, ibakedmodel);
                GlStateManager.popMatrix();
            } else {

                if (k > 0) {
                    float f8 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    GlStateManager.translate(f8, f10, 0.0F);
                }

                ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                this.itemRenderer.renderItem(itemstack, ibakedmodel);
                GlStateManager.popMatrix();
                GlStateManager.translate(0.0F * f, 0.0F * f1, 0.09375F * f2);
            }
        }

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        this.bindEntityTexture(entity);

        if (flag) {
            Objects.requireNonNull(this.renderManager.renderEngine.getTexture(Objects.requireNonNull(this.getEntityTexture(entity)))).restoreLastBlurMipmap();
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityItem entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
