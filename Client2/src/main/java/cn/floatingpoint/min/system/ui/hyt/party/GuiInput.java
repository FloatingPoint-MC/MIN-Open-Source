package cn.floatingpoint.min.system.ui.hyt.party;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.ui.components.InputField;
import cn.floatingpoint.min.system.ui.hyt.germ.GermModButton;
import cn.floatingpoint.min.system.ui.hyt.germ.GuiGermScreen;
import cn.floatingpoint.min.utils.render.RenderUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;

public class GuiInput extends GuiScreen {
    private InputField inputField;
    private GermModButton confirm;
    @Nullable
    private final GuiGermScreen parent;
    private final String guiUuid, parentUuid;

    public GuiInput(@Nullable GuiGermScreen parent, String guiUuid, String parentUuid) {
        this.parent = parent;
        this.guiUuid = guiUuid;
        this.parentUuid = parentUuid;
    }

    @Override
    public void initGui() {
        inputField = new InputField(width / 2 - 50, height / 2 - 30, 100, 20);
        confirm = new GermModButton("submit", "提交") {
            @Override
            protected void beforeClick() {
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                        new PacketBuffer(new PacketBuffer(Unpooled.buffer()
                                .writeInt(10))
                                .writeString(guiUuid)
                                .writeString("input")
                                .writeString("")
                                .writeInt(1))
                ));
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                        new PacketBuffer(new PacketBuffer(Unpooled.buffer()
                                .writeInt(10))
                                .writeString(guiUuid)
                                .writeString("input")
                                .writeString(inputField.getText().trim())
                                .writeInt(0))
                ));
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                        new PacketBuffer(new PacketBuffer(Unpooled.buffer()
                                .writeInt(10))
                                .writeString(guiUuid)
                                .writeString("input")
                                .writeString(inputField.getText().trim())
                                .writeInt(3))
                ));
            }

            @Override
            protected void whenClick() {
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                        new PacketBuffer(Unpooled.buffer()
                                .writeInt(26))
                                .writeString("GUI$" + guiUuid + "@input")
                                .writeString("{\"input\":\"" + inputField.getText() + "\"}")));
            }
        };
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableBlend();
        RenderUtil.drawImage(new ResourceLocation("min/hyt/background.png"), width / 2 - 100, height / 2 - 81, 200, 162);
        Managers.fontManager.sourceHansSansCN_Regular_20.drawCenteredString("花雨庭组队系统", width / 2, height / 2 - 72, new Color(216, 216, 216).getRGB());
        inputField.drawTextBox();
        confirm.drawButton(guiUuid, width / 2, height / 2 + 30, mouseX, mouseY);
        GlStateManager.disableBlend();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        inputField.mouseClicked(mouseX, mouseY, mouseButton);
        if (inputField.isFocused()) {
            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                    new PacketBuffer(new PacketBuffer(Unpooled.buffer()
                            .writeInt(10))
                            .writeString(guiUuid)
                            .writeString("input")
                            .writeString(inputField.getText().trim())
                            .writeInt(2))
            ));
        }
        confirm.mouseClicked(guiUuid);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        inputField.keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        inputField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11))
                .writeString(parentUuid)
        ));
        if (parent != null) {
            mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11))
                    .writeString(parent.getUUID())
            ));
        }
    }
}
