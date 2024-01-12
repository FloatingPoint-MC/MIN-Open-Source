package cn.floatingpoint.min.system.irc.packet;

import cn.floatingpoint.min.system.irc.connection.NetworkManager;
import cn.floatingpoint.min.utils.math.RSAUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;
import java.security.PublicKey;

public class Encoder extends MessageToByteEncoder<Packet<?>> {
    public static boolean hasKey;
    public static PublicKey key;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet<?> packet, ByteBuf byteBuf) throws Exception {
        Integer integer = channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacketId(EnumPacketDirection.SERVERBOUND, packet);

        if (integer == null) {
            throw new IOException("Can't serialize unregistered packet");
        } else {
            PacketBuffer packetbuffer = new PacketBuffer(byteBuf);
            packetbuffer.writeVarIntToBuffer(integer);

            try {
                packet.writePacketData(packetbuffer);
                if (hasKey) {
                    byte[] bytes = new byte[packetbuffer.readableBytes()];
                    packetbuffer.readBytes(bytes);
                    packetbuffer.clear();
                    packetbuffer.writeBytes(RSAUtil.encrypt(bytes, key));
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
