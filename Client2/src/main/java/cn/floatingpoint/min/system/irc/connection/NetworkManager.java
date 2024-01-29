package cn.floatingpoint.min.system.irc.connection;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.irc.Client;
import cn.floatingpoint.min.system.irc.GuiStatus;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.irc.handler.INetHandler;
import cn.floatingpoint.min.system.irc.handler.NetHandlerClient;
import cn.floatingpoint.min.system.irc.packet.Decoder;
import cn.floatingpoint.min.system.irc.packet.Encoder;
import cn.floatingpoint.min.system.irc.packet.EnumConnectionState;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.impl.CPacketKey;
import cn.floatingpoint.min.utils.math.RSAUtil;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;

import java.security.Key;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NetworkManager extends SimpleChannelInboundHandler<Packet<?>> {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    public static final AttributeKey<EnumConnectionState> attrKeyConnectionState = AttributeKey.valueOf("float");
    private INetHandler packetListener;
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Queue<InboundHandlerTuplePacketListener> outboundPacketsQueue = new ConcurrentLinkedQueue<>();
    private Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        this.setConnectionState(EnumConnectionState.PROTOCOL);
        this.packetListener = new NetHandlerClient(this);
        Map<String, Key> map = RSAUtil.generateKeys();
        Encoder.hasKey = false;
        Encoder.key = null;
        Decoder.hasKey = true;
        Decoder.key = (PrivateKey) map.get("PRIVATE_KEY");
        this.sendPacket(new CPacketKey(map.get("PUBLIC_KEY").getEncoded()));
        System.out.println("[MIN] Successfully connected to the server!");
        if (Minecraft.getMinecraft().currentScreen instanceof GuiStatus guiStatus) {
            Client.setStatus("\247f" + Managers.i18NManager.getTranslation("irc.disconnect"));
            guiStatus.fail();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        IRCClient.getInstance().reconnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @SuppressWarnings("all")
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet) {
        if (this.channel.isOpen()) {
            ((Packet<INetHandler>) packet).processPacket(this.packetListener);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent ent) {
            if (ent.state() == IdleState.READER_IDLE) {
                ctx.writeAndFlush(simpleDateFormat.format(new Date()) + ": You have heart disease");
                ctx.close();
            }
        }
    }

    public void sendPacket(Packet<?> packetIn) {
        if (this.channel.isOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, null);
        } else {
            this.readWriteLock.writeLock().lock();

            try {
                this.outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packetIn));
            } finally {
                this.readWriteLock.writeLock().unlock();
            }
        }
    }

    public Channel getChannel() {
        return channel;
    }

    private void dispatchPacket(final Packet<?> inPacket, final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
        final EnumConnectionState enumconnectionstate = EnumConnectionState.getFromPacket(inPacket);
        final EnumConnectionState enumconnectionstate1 = this.channel.attr(attrKeyConnectionState).get();

        if (enumconnectionstate1 != enumconnectionstate) {
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop()) {
            if (enumconnectionstate != enumconnectionstate1) {
                this.setConnectionState(enumconnectionstate);
            }

            ChannelFuture channelfuture = this.channel.writeAndFlush(inPacket);

            if (futureListeners != null) {
                channelfuture.addListeners(futureListeners);
            }

            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(() -> {
                if (enumconnectionstate != enumconnectionstate1) {
                    NetworkManager.this.setConnectionState(enumconnectionstate);
                }

                ChannelFuture channelfuture1 = NetworkManager.this.channel.writeAndFlush(inPacket);

                if (futureListeners != null) {
                    channelfuture1.addListeners(futureListeners);
                }

                channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    public void setConnectionState(EnumConnectionState newState) {
        this.channel.attr(attrKeyConnectionState).set(newState);
        this.channel.config().setAutoRead(true);
    }

    private void flushOutboundQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            this.readWriteLock.readLock().lock();

            try {
                while (!this.outboundPacketsQueue.isEmpty()) {
                    InboundHandlerTuplePacketListener networkmanager$inboundhandlertuplepacketlistener = this.outboundPacketsQueue.poll();
                    this.dispatchPacket(networkmanager$inboundhandlertuplepacketlistener.packet, networkmanager$inboundhandlertuplepacketlistener.futureListeners);
                }
            } finally {
                this.readWriteLock.readLock().unlock();
            }
        }
    }

    static class InboundHandlerTuplePacketListener {
        private final Packet<?> packet;
        private final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

        @SafeVarargs
        public InboundHandlerTuplePacketListener(Packet<?> inPacket, GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
            this.packet = inPacket;
            this.futureListeners = inFutureListeners;
        }
    }
}
