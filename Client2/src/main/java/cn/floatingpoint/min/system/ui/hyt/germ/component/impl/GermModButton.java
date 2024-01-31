package cn.floatingpoint.min.system.ui.hyt.germ.component.impl;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.ui.hyt.germ.component.GermComponent;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import java.awt.*;

/**
 * @projectName: MIN
 * @author: vlouboos
 * @date: 2023-07-21 15:18:30
 */
public class GermModButton implements GermComponent {
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final String path;
    protected final String text;
    protected boolean hovered;

    public GermModButton(String path, String text) {
        this.path = path;
        this.text = text;
        hovered = false;
    }

    @Override
    public void drawComponent(String parentUuid, int x, int y, int mouseX, int mouseY) {
        if (Gui.isHovered(x - 50, y - 10, x + 50, y + 10, mouseX, mouseY)) {
            if (!hovered) {
                mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                        .writeString(parentUuid)
                        .writeString(path)
                        .writeInt(2))
                ));
                hovered = true;
            }
        } else if (hovered) {
            mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                    .writeString(parentUuid)
                    .writeString(path)
                    .writeInt(3))
            ));
            hovered = false;
        }
        int startX = x - 50;
        int endX = x + 50;
        int startY = y - 10;
        int endY = y + 10;
        Gui.drawRect(startX, startY, endX, endY, new Color(0, 0, 0, 102).getRGB());
        Gui.drawRect(startX, startY, startX + 0.5D, endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect(startX + 0.5D, endY - 0.5D, endX - 0.5D, endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect(endX - 0.5D, startY, endX, endY, new Color(44, 44, 44, 102).getRGB());
        Gui.drawRect(startX + 0.5D, startY, endX - 0.5D, startY + 0.5, new Color(44, 44, 44, 102).getRGB());
        Managers.fontManager.sourceHansSansCN_Regular_18.drawCenteredString(text.replace("\2478", ""), x, startY + 6, new Color(216, 216, 216).getRGB());
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getSeparation() {
        return 20;
    }

    @Override
    public void mouseClicked(String parentUuid) {
        if (hovered) {
            beforeClick();
            mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                    .writeString(parentUuid)
                    .writeString(path)
                    .writeInt(0))
            ));
            whenClick();
            if (doesCloseOnClickButton()) {
                mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11))
                        .writeString(parentUuid)
                ));
                mc.displayGuiScreen(null);
            }
        }
    }

    protected void beforeClick() {

    }

    protected void whenClick() {

    }

    protected boolean doesCloseOnClickButton() {
        return true;
    }
}