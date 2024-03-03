package cn.floatingpoint.min.system.irc;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.irc.connection.NetworkManager;
import cn.floatingpoint.min.system.irc.handler.INetHandler;
import cn.floatingpoint.min.system.irc.handler.NetHandlerClient;
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
import io.netty.buffer.Unpooled;
import me.konago.nativeobfuscator.Native;
import net.minecraft.client.Minecraft;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Map;

public class IRCClient extends WebSocketClient {
    private static IRCClient theIRC;
    private int count;
    public NetworkManager netManager;
    public boolean connect;
    public boolean connectedUser;
    private boolean firstConnect;

    public IRCClient() throws URISyntaxException, IOException {
        //super(new URI("ws://irc.minclient.xyz"));
        super(new URI("wss://irc.minclient.xyz"));
        //super(new URI("ws://118.193.46.31:65535"));
        setTcpNoDelay(true);
        theIRC = this;
        firstConnect = true;
        count = 0;
        this.connect = this.startConnection();
    }

    @Native
    @SuppressWarnings("all")
    private boolean startConnection() throws IOException {
        try {
            System.out.println("Try connecting IRC server...");
            this.netManager = new NetworkManager(this);
            connect();
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
                            Minecraft.getMinecraft().displayGuiScreen(new GuiFailedConnect(Managers.i18NManager.getTranslation("irc.fail")));
                        }
                    } catch (Exception exception) {
                        break;
                    }
                }
            }).start();
            Minecraft.getMinecraft().setIngameNotInFocus();
            return false;
        }
        while (!this.isOpen() && firstConnect) {
            try {
                Thread.sleep(1000L);
                count++;
                if (count > 30) {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (count > 30) {
            System.out.println("[MIN] Failed in connecting!");
            if (Minecraft.getMinecraft().currentScreen == null || !(Minecraft.getMinecraft().currentScreen instanceof GuiFailedConnect)) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiFailedConnect(Managers.i18NManager.getTranslation("irc.fail")));
            }
            throw new IOException("Failed to connect server");
        }
        firstConnect = false;
        return true;
    }

    /**
     * Called after an opening handshake has been performed and the given websocket is ready to be
     * written on.
     *
     * @param handshakedata The handshake of the websocket instance
     */
    @Override
    @Native
    public void onOpen(ServerHandshake handshakedata) {
        Map<String, Key> map = RSAUtil.generateKeys();
        Encoder.hasKey = false;
        Encoder.key = null;
        Decoder.hasKey = true;
        Decoder.key = (PrivateKey) map.get("PRIVATE_KEY");
        netManager.packetListener = new NetHandlerClient(netManager);
        netManager.lock = false;
        netManager.sendPacket(new CPacketKey(map.get("PUBLIC_KEY").getEncoded()));
        System.out.println("[MIN] Successfully connected to the server!");
        if (Minecraft.getMinecraft().currentScreen instanceof GuiStatus guiStatus) {
            Client.setStatus("\247f" + Managers.i18NManager.getTranslation("irc.disconnect"));
            guiStatus.fail();
        }
    }

    /**
     * Callback for string messages received from the remote host
     *
     * @param message The UTF-8 decoded message that was received.
     * @see #onMessage(ByteBuffer)
     **/
    @Override
    public void onMessage(String message) {
        if (Minecraft.DEBUG_MODE()) {
            System.out.println(message);
        }
    }

    @SuppressWarnings("all")
    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            ((Packet<INetHandler>) Decoder.decode(Unpooled.wrappedBuffer(bytes))).processPacket(netManager.packetListener);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called after the websocket connection has been closed.
     *
     * @param code   The codes can be looked up here: {@link org.java_websocket.framing.CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether or not the closing of the connection was initiated by the remote
     *               host.
     **/
    @Override
    @Native
    public void onClose(int code, String reason, boolean remote) {
        startReconnection();
    }

    /**
     * Called when errors occurs. If an error causes the websocket connection to fail {@link
     * #onClose(int, String, boolean)} will be called additionally.<br> This method will be called
     * primarily because of IO or protocol errors.<br> If the given exception is an RuntimeException
     * that probably means that you encountered a bug.<br>
     *
     * @param ex The exception causing this error
     **/
    @Override
    public void onError(Exception ex) {
        if (firstConnect) {
            if (Minecraft.DEBUG_MODE()) {
                ex.printStackTrace();
            }
            count = 31;
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
        } catch (NoSuchAlgorithmException e) {
            this.connect = false;
            IRCClient.getInstance().connectedUser = Client.getUsername() != null;
            System.out.println("Failed in connecting!");
            new Thread(() -> {
                while (true) {
                    try {
                        if (Minecraft.getMinecraft().currentScreen == null || !(Minecraft.getMinecraft().currentScreen instanceof GuiFailedConnect)) {
                            Minecraft.getMinecraft().displayGuiScreen(new GuiFailedConnect(Managers.i18NManager.getTranslation("irc.fail")));
                        }
                    } catch (Exception ignored) {
                    }
                }
            }).start();
            Minecraft.getMinecraft().setIngameNotInFocus();
        }
    }

    public void startReconnection() {
        netManager.lock = true;
        Decoder.hasKey = Encoder.hasKey = false;
        Decoder.key = null;
        Encoder.key = null;
        new Thread(() -> {
            try {
                Thread.sleep(5000L);
                Minecraft.getMinecraft().addScheduledTask(this::reconnect);
            } catch (InterruptedException e) {
                while (true) {
                    try {
                        if (Minecraft.getMinecraft().currentScreen == null || !(Minecraft.getMinecraft().currentScreen instanceof GuiFailedConnect)) {
                            Minecraft.getMinecraft().displayGuiScreen(new GuiFailedConnect(Managers.i18NManager.getTranslation("irc.fail")));
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }).start();
    }
}
