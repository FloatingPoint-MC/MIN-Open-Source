package cn.floatingpoint.min.system.irc.packet;

import cn.floatingpoint.min.utils.math.DESUtil;
import cn.floatingpoint.min.utils.math.RSAUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Objects;

public class Decoder {
    public static boolean hasKey = false;
    public static PrivateKey key;

    public static Packet<?> decode(ByteBuf byteBuf) throws Exception {
        if (byteBuf.readableBytes() != 0) {
            PacketBuffer packetbuffer;
            if (!hasKey) {
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(DESUtil.decrypt(Base64.getDecoder().decode(bytes))));
            } else {
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(Objects.requireNonNull(RSAUtil.decrypt(Base64.getDecoder().decode(bytes), key))));
            }
            int i = packetbuffer.readVarIntFromBuffer();
            Packet<?> packet = EnumConnectionState.getPacket(EnumPacketDirection.CLIENTBOUND, i);

            if (packet == null) {
                throw new IOException("Bad packet id " + i);
            } else {
                packet.readPacketData(packetbuffer);

                if (packetbuffer.readableBytes() > 0) {
                    throw new IOException("Packet " + i + " (" + packet.getClass().getSimpleName() + ") was larger than expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + i);
                } else {
                    return packet;
                }
            }
        }
        throw new NullPointerException("Empty packet");
    }
}
