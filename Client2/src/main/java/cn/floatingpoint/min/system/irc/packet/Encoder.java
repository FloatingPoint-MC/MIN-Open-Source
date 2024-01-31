package cn.floatingpoint.min.system.irc.packet;

import cn.floatingpoint.min.utils.math.DESUtil;
import cn.floatingpoint.min.utils.math.RSAUtil;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;

public class Encoder {
    public static boolean hasKey;
    public static PublicKey key;

    public static byte[] encode(Packet<?> packet) throws Exception {
        Integer integer = EnumConnectionState.getPacketId(EnumPacketDirection.SERVERBOUND, packet);
        if (integer == null) {
            throw new IOException("Can't serialize unregistered packet");
        } else {
            PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
            packetbuffer.writeVarIntToBuffer(integer);
            packet.writePacketData(packetbuffer);
            if (hasKey) {
                byte[] bytes = new byte[packetbuffer.readableBytes()];
                packetbuffer.readBytes(bytes);
                packetbuffer.clear();
                return Base64.getEncoder().encode(RSAUtil.encrypt(bytes, key));
            } else {
                byte[] bytes = new byte[packetbuffer.readableBytes()];
                packetbuffer.readBytes(bytes);
                return Base64.getEncoder().encode(DESUtil.encrypt(bytes));
            }
        }
    }
}
