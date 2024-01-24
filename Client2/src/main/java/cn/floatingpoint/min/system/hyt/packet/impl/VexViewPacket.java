package cn.floatingpoint.min.system.hyt.packet.impl;

import cn.floatingpoint.min.system.hyt.packet.CustomPacket;
import cn.floatingpoint.min.system.hyt.party.ButtonDecoder;
import cn.floatingpoint.min.system.hyt.party.Sender;
import io.netty.buffer.ByteBuf;

public class VexViewPacket implements CustomPacket {
    @Override
    public String getChannel() {
        return "VexView";
    }

    @Override
    public void process(ByteBuf byteBuf) {
        ButtonDecoder buttonDecoder = new ButtonDecoder(byteBuf);
        if (buttonDecoder.sign) {
            Sender.clickButton(buttonDecoder.getButton("sign").getId());
        }
    }
}
