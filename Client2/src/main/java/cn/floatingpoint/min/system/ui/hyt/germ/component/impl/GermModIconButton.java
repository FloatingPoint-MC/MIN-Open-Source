package cn.floatingpoint.min.system.ui.hyt.germ.component.impl;

import cn.floatingpoint.min.utils.render.RenderUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.ResourceLocation;

public class GermModIconButton extends GermModButton {
    private final ResourceLocation image;
    private final int width, height;

    public GermModIconButton(String path, String imagePath, int width, int height) {
        super(path, "");
        this.image = new ResourceLocation(imagePath);
        this.width = width;
        this.height = height;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void drawComponent(String parentUuid, int x, int y, int mouseX, int mouseY) {
        if (Gui.isHovered(x - width / 2, y - height / 2, x + width / 2, y + height / 2, mouseX, mouseY)) {
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
        RenderUtil.drawImage(image, x - width / 2, y - height / 2, width, height);
    }
}
