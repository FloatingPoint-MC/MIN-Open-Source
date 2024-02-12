package cn.floatingpoint.min.system.ui.hyt.germ.component.impl;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

public class GameButton extends GermModButton {
    private final String key;

    public GameButton(String path, String text, String key) {
        super(path, text);
        this.key = key;
    }

    @Override
    protected void whenClick() {
        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                new PacketBuffer(Unpooled.buffer()
                        .writeInt(26))
                        .writeString("GUI$mainmenu@subject/" + key.substring(8).replace("fight_team", "team_fight"))
                        .writeString("{\"click\":\"1\"}")));
    }

    @Override
    protected boolean doesCloseOnClickButton() {
        return false;
    }
}
