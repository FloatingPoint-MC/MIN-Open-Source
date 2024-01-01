package cn.floatingpoint.min.system.hyt.packet.impl;

import cn.floatingpoint.min.system.ui.hyt.germ.GermModButton;
import io.netty.buffer.Unpooled;
import me.konago.nativeobfuscator.Native;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

public class DetailedGameButton extends GermModButton {
    private final int index;
    private final String sid;

    public DetailedGameButton(String path, String text, int index, String sid) {
        super(path, text);
        this.index = index;
        this.sid = sid;
    }


    @Override
    @Native
    protected void whenClick() {
        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                new PacketBuffer(Unpooled.buffer()
                        .writeInt(26))
                        .writeString("GUI$mainmenu@entry/" + index)
                        .writeString("{\"entry\":" + index + ",\"sid\":\"" + sid + "\"}")
        ));
    }
}
