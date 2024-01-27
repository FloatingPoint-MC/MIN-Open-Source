package cn.floatingpoint.min.system.irc;

import cn.floatingpoint.min.system.irc.connection.NetworkManager;
import cn.floatingpoint.min.system.irc.packet.Decoder;
import cn.floatingpoint.min.system.irc.packet.Encoder;
import cn.floatingpoint.min.system.irc.packet.Packet;
import cn.floatingpoint.min.system.irc.packet.impl.CPacketAdmin;
import cn.floatingpoint.min.system.irc.packet.impl.CPacketChat;
import cn.floatingpoint.min.system.irc.packet.impl.CPacketKey;
import cn.floatingpoint.min.system.irc.packet.impl.CPacketLogin;
import cn.floatingpoint.min.utils.client.ChatUtil;
import cn.floatingpoint.min.utils.client.HWIDUtil;
import cn.floatingpoint.min.utils.client.Pair;
import cn.floatingpoint.min.utils.math.RSAUtil;
import cn.floatingpoint.min.utils.math.TimeHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.konago.nativeobfuscator.Native;
import net.minecraft.client.Minecraft;

import java.net.InetSocketAddress;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Map;

public class IRCClient {
    private static IRCClient theIRC;
    public NetworkManager netManager;
    public boolean connect;
    public boolean connectedUser;

    public IRCClient() {
        theIRC = this;
        this.connect = this.connect();
    }

    @Native
    private boolean connect() {
        try {
            System.out.println("Try connecting IRC server...");
            this.netManager = new NetworkManager();
            Bootstrap bootstrap = new Bootstrap();
            EventLoopGroup group = new NioEventLoopGroup();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel channel) {
                    channel.pipeline()
                            .addLast("decoder", new Decoder())
                            .addLast("encoder", new Encoder())
                            .addLast(netManager);
                }
            });
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.TCP_NODELAY, true);
            // 连接服务端
            bootstrap.connect(new InetSocketAddress("127.0.0.1", 65535)).sync().channel();
            //bootstrap.connect(new InetSocketAddress(MiscUtil.getRemoteIP(), 65535)).sync().channel();
            Map<String, Key> map = RSAUtil.generateKeys();
            Encoder.hasKey = false;
            Encoder.key = null;
            Decoder.hasKey = true;
            Decoder.key = (PrivateKey) map.get("PRIVATE_KEY");
            this.netManager.sendPacket(new CPacketKey(map.get("PUBLIC_KEY").getEncoded()));
            System.out.println("[MIN] Successfully connected to the server!");
            if (Minecraft.getMinecraft().currentScreen instanceof GuiStatus guiStatus) {
                guiStatus.fail();
            }
            return true;
        } catch (Exception e) {
            if (Minecraft.DEBUG_MODE()) {
                e.printStackTrace();
            }
            this.connect = false;
            System.out.println("[MIN] Failed in connecting!");
            new Thread(() -> {
                while (true) {
                    try {
                        if (Minecraft.getMinecraft().currentScreen == null || !(Minecraft.getMinecraft().currentScreen instanceof GuiFailedConnect)) {
                            Minecraft.getMinecraft().displayGuiScreen(new GuiFailedConnect("Unable to connect to the server!"));
                        }
                    } catch (Exception exception) {
                        break;
                    }
                }
            }).start();
            Minecraft.getMinecraft().setIngameNotInFocus();
            return false;
        }
    }

    public static IRCClient getInstance() {
        return theIRC;
    }

    @Native
    public static void processMessage(String msg) {
        String[] args = msg.trim().split(" ");
        if (msg.toLowerCase().startsWith("/irc")) {
            args = Arrays.copyOfRange(args, 1, args.length);
            if (args.length < 1) {
                ChatUtil.printToChatWithPrefix("\247cCorrect usage: /irc <Message>");
                return;
            } else {
                if (args[0].equalsIgnoreCase("kick")) {
                    if (args.length < 2) {
                        ChatUtil.printToChatWithPrefix("\247cCorrect usage: /irc kick <Player> [Reason]");
                    } else {
                        if (args[1].length() <= 16) {
                            String reason = "";
                            if (args.length > 2) {
                                reason = ChatUtil.buildMessage(Arrays.copyOfRange(args, 2, args.length));
                            }
                            IRCClient.getInstance().addToSendQueue(new CPacketAdmin(CPacketAdmin.Action.KICK, args[1], 0, reason));
                        } else {
                            ChatUtil.printToChatWithPrefix("\247cIllegal username!");
                        }
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("ban")) {
                    if (args.length < 2) {
                        ChatUtil.printToChatWithPrefix("\247cCorrect usage: /irc ban <Player> [Duration] [Reason]");
                    } else {
                        if (args[1].length() <= 16) {
                            if (args.length > 2) {
                                String combined = ChatUtil.buildMessage(Arrays.copyOfRange(args, 2, args.length));
                                Pair<String, Long> pair = TimeHelper.getDurationAndReasonFromString(combined);
                                IRCClient.getInstance().addToSendQueue(new CPacketAdmin(CPacketAdmin.Action.BAN, args[1], pair.getValue(), pair.getKey().isEmpty() ? "Banned by an operator." : pair.getKey()));
                            } else {
                                IRCClient.getInstance().addToSendQueue(new CPacketAdmin(CPacketAdmin.Action.BAN, args[1], 0, "Banned by an operator."));
                            }
                        } else {
                            ChatUtil.printToChatWithPrefix("\247cIllegal username!");
                        }
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("unban")) {
                    if (args.length < 2) {
                        ChatUtil.printToChatWithPrefix("\247cCorrect usage: /irc unban <Player>");
                    } else {
                        if (args[1].length() <= 16) {
                            IRCClient.getInstance().addToSendQueue(new CPacketAdmin(CPacketAdmin.Action.UNBAN, args[1], 0, ""));
                        } else {
                            ChatUtil.printToChatWithPrefix("\247cIllegal username!");
                        }
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("mute")) {
                    if (args.length < 2) {
                        ChatUtil.printToChatWithPrefix("\247cCorrect usage: /irc mute <Player> [Duration] [Reason]");
                    } else {
                        if (args[1].length() <= 16) {
                            if (args.length > 2) {
                                String combined = ChatUtil.buildMessage(Arrays.copyOfRange(args, 2, args.length));
                                Pair<String, Long> pair = TimeHelper.getDurationAndReasonFromString(combined);
                                IRCClient.getInstance().addToSendQueue(new CPacketAdmin(CPacketAdmin.Action.MUTE, args[1], pair.getValue(), pair.getKey().isEmpty() ? "Muted by an operator." : pair.getKey()));
                            } else {
                                IRCClient.getInstance().addToSendQueue(new CPacketAdmin(CPacketAdmin.Action.MUTE, args[1], 0, "Muted by an operator."));
                            }
                        } else {
                            ChatUtil.printToChatWithPrefix("\247cIllegal username!");
                        }
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("unmute")) {
                    if (args.length < 2) {
                        ChatUtil.printToChatWithPrefix("\247cCorrect usage: /irc unmute <Player>");
                    } else {
                        if (args[1].length() <= 16) {
                            IRCClient.getInstance().addToSendQueue(new CPacketAdmin(CPacketAdmin.Action.UNMUTE, args[1], 0, ""));
                        } else {
                            ChatUtil.printToChatWithPrefix("\247cIllegal username!");
                        }
                    }
                    return;
                } else if (args[0].equalsIgnoreCase("list")) {
                    IRCClient.getInstance().addToSendQueue(new CPacketAdmin(CPacketAdmin.Action.LIST, "", 0, ""));
                    return;
                }
            }
        }
        IRCClient.getInstance().addToSendQueue(new CPacketChat(ChatUtil.buildMessage(args)));
    }

    public void addToSendQueue(Packet<?> packet) {
        this.netManager.sendPacket(packet);
    }

    public void enableIRC() {
        try {
            if (Encoder.hasKey) {
                this.addToSendQueue(new CPacketLogin(Client.getUsername(), Client.getPassword(), HWIDUtil.getHWID()));
            }
            connectedUser = true;
        } catch (NoSuchAlgorithmException e) {
            this.connect = false;
            IRCClient.getInstance().connectedUser = Client.getUsername() != null;
            System.out.println("Failed in connecting!");
            new Thread(() -> {
                while (true) {
                    try {
                        if (Minecraft.getMinecraft().currentScreen == null || !(Minecraft.getMinecraft().currentScreen instanceof GuiFailedConnect)) {
                            Minecraft.getMinecraft().displayGuiScreen(new GuiFailedConnect("Unable to connect to the server!"));
                        }
                    } catch (Exception ignored) {
                    }
                }
            }).start();
            Minecraft.getMinecraft().setIngameNotInFocus();
        }
    }

    public void reconnect() {
        Decoder.hasKey = Encoder.hasKey = false;
        Decoder.key = null;
        Encoder.key = null;
        new Thread(() -> {
            try {
                Thread.sleep(5000L);
                Minecraft.getMinecraft().addScheduledTask(this::connect);
            } catch (InterruptedException e) {
                while (true) {
                    try {
                        if (Minecraft.getMinecraft().currentScreen == null || !(Minecraft.getMinecraft().currentScreen instanceof GuiFailedConnect)) {
                            Minecraft.getMinecraft().displayGuiScreen(new GuiFailedConnect("Unable to connect to the server!"));
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }).start();
    }
}
