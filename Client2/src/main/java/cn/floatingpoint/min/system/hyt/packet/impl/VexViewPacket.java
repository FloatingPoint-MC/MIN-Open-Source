package cn.floatingpoint.min.system.hyt.packet.impl;

import cn.floatingpoint.min.system.hyt.packet.CustomPacket;
import cn.floatingpoint.min.system.hyt.party.VexViewDecoder;
import cn.floatingpoint.min.system.hyt.party.Sender;
import cn.floatingpoint.min.system.irc.Client;
import io.netty.buffer.ByteBuf;
import me.konago.nativeobfuscator.Native;
import net.minecraft.client.gui.ScaledResolution;
import org.json.JSONObject;

public class VexViewPacket implements CustomPacket {
    @Override
    public String getChannel() {
        return "VexView";
    }

    @Override
    @Native
    public void process(ByteBuf byteBuf) {
        VexViewDecoder vexViewDecoder = new VexViewDecoder(byteBuf);
        if (vexViewDecoder.sign) {
            Sender.clickButton(vexViewDecoder.getButton("sign").getId());
        } else {
            JSONObject json = new JSONObject(vexViewDecoder.result);
            if (json.getString("packet_type").equals("ver")) {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                Sender.sendJson(new JSONObject()
                        .put("packet_sub_type", scaledResolution.getScaledWidth() + ":" + scaledResolution.getScaledHeight())
                        .put("packet_data", Client.getVexViewVersion())
                        .put("packet_type", "ver"));
            }
        }
    }
}
