package cn.floatingpoint.min.system.irc.packet;

import cn.floatingpoint.min.system.irc.connection.NetworkManager;
import cn.floatingpoint.min.utils.math.RSAUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.List;
import java.util.Objects;

public class Decoder extends ByteToMessageDecoder {
    public static boolean hasKey = false;
    public static PrivateKey key;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() != 0) {
            PacketBuffer packetbuffer;
            if (!hasKey) {
                packetbuffer = new PacketBuffer(byteBuf);
            } else {
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                packetbuffer = new PacketBuffer(Unpooled.wrappedBuffer(Objects.requireNonNull(RSAUtil.decrypt(bytes, key))));
            }
            int i = packetbuffer.readVarIntFromBuffer();
            Packet<?> packet = channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get().getPacket(EnumPacketDirection.CLIENTBOUND, i);

            if (packet == null) {
                throw new IOException("Bad packet id " + i);
            } else {
                packet.readPacketData(packetbuffer);

                if (packetbuffer.readableBytes() > 0) {
                    throw new IOException("Packet " + channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get().getId() + "/" + i + " (" + packet.getClass().getSimpleName() + ") was larger than expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + i);
                } else {
                    list.add(packet);
                }
            }
        }
    }
}
