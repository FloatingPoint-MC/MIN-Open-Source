package cn.floatingpoint.min.system.ui.hyt.germ.component.impl;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.ui.hyt.germ.component.GermComponent;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

import java.awt.*;

public class GermPartyKick implements GermComponent {
    private final String playerName;
    private final GermModIconButton kick;

    public GermPartyKick(String playerName) {
        this.playerName = playerName;
        this.kick = new GermModIconButton("bt", "min/hyt/page/kick.png", 12, 12) {
            @Override
            protected void whenClick() {
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                        new PacketBuffer(Unpooled.buffer().writeInt(26))
                                .writeString("GUI$team_request_list@bt_kick")
                                .writeString("{\"player_name\":\"" + playerName + "\"}")
                ));
            }
        };
    }

    @Override
    public void drawComponent(String parentUuid, int x, int y, int mouseX, int mouseY) {
        Managers.fontManager.sourceHansSansCN_Regular_20.drawString(playerName, x - 80, y - 4, new Color(216, 216, 216).getRGB());
        this.kick.drawComponent(parentUuid, x + 68, y, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(String parentUuid) {
        this.kick.mouseClicked(parentUuid);
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
