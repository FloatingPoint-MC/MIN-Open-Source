package cn.floatingpoint.min.system.ui.hyt.germ;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.ui.hyt.germ.component.GermComponent;
import cn.floatingpoint.min.utils.render.RenderUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * @projectName: MIN
 * @author: vlouboos
 * @date: 2023-07-21 15:21:19
 */
public class GuiComponentPage extends GuiGermScreen {
    private final String uuid;
    private final LinkedHashSet<GermComponent> components = new LinkedHashSet<>();
    private String title;
    private int componentHeight;

    public GuiComponentPage(String uuid, ArrayList<GermComponent> components) {
        this.uuid = uuid;
        if (components.isEmpty()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }
        title = "花雨庭菜单";
        this.components.addAll(components);
    }

    @Override
    public void initGui() {
        ArrayList<GermComponent> components = new ArrayList<>(this.components);
        for (int i = 0; i < components.size(); ++i) {
            GermComponent component = components.get(i);
            componentHeight += component.getHeight();
            if (i != components.size()) {
                componentHeight += component.getSeparation();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        RenderUtil.drawImage(new ResourceLocation("min/hyt/page/header.png"), width / 2 - 100, height / 2 - 50 - componentHeight / 2 + 20, 200, 40);
        Managers.fontManager.sourceHansSansCN_Regular_20.drawCenteredString(this.title, width / 2, height / 2 - 40 - componentHeight / 2 + 20, new Color(216, 216, 216).getRGB());
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int y = height / 2 - componentHeight / 2 + 30;
        ArrayList<GermComponent> components = new ArrayList<>(this.components);
        for (int i = 0; i < components.size(); ++i) {
            GermComponent component = components.get(i);
            int backgroundHeight = component.getHeight() + (i == components.size() ? 0 : component.getSeparation());
            RenderUtil.drawImage(new ResourceLocation("min/hyt/page/body.png"), width / 2 - 100, y - 20, 200, backgroundHeight);
            GL11.glEnable(3042);
            component.drawComponent(uuid, width / 2, y, mouseX, mouseY);
            y += backgroundHeight;
        }
        RenderUtil.drawImage(new ResourceLocation("min/hyt/page/footer.png"), width / 2 - 100, y - 20, 200, 30);
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (GermComponent component : components) {
                component.mouseClicked(uuid);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
            mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11))
                    .writeString(uuid)
            ));
        }
    }

    public GuiComponentPage title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String getUUID() {
        return uuid;
    }
}
