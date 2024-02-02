package cn.floatingpoint.min.system.ui.hyt.germ.component.impl;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.ui.hyt.germ.component.GermComponent;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import java.awt.*;

public class GermPartyInvitation implements GermComponent {
    private final String playerName;
    private final GermModIconButton accept;

    public GermPartyInvitation(String playerName) {
        this.playerName = playerName;
        accept = new GermModIconButton("bt", "min/hyt/page/apply.png", 12, 12) {
            @Override
            protected void whenClick() {
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                        new PacketBuffer(Unpooled.buffer().writeInt(26))
                                .writeString("GUI$team_list@bt_accept_invite")
                                .writeString("{\"player_name\":\"" + playerName + "\"}")
                ));
            }
        };
    }

    @Override
    public void drawComponent(String parentUuid, int x, int y, int mouseX, int mouseY) {
        int startX = x - 90;
        int endX = x + 90;
        int startY = y - 10;
        int endY = y + 10;
        Gui.drawRect(startX, startY, endX, endY, new Color(0, 0, 0, 102).getRGB());
        Gui.drawRect(startX, startY, startX + 0.5D, endY, new Color(216, 216, 216, 102).getRGB());
        Gui.drawRect(startX + 0.5D, endY - 0.5D, endX - 0.5D, endY, new Color(216, 216, 216, 102).getRGB());
        Gui.drawRect(endX - 0.5D, startY, endX, endY, new Color(216, 216, 216, 102).getRGB());
        Gui.drawRect(startX + 0.5D, startY, endX - 0.5D, startY + 0.5, new Color(216, 216, 216, 102).getRGB());
        Managers.fontManager.sourceHansSansCN_Regular_20.drawString(playerName, x - 80, y - 4, new Color(216, 216, 216).getRGB());
        accept.drawComponent(parentUuid, x + 68, y, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(String parentUuid) {
        accept.mouseClicked(parentUuid);
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getSeparation() {
        return 12;
    }
}
