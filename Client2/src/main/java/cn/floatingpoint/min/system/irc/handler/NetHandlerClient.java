package cn.floatingpoint.min.system.irc.handler;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.irc.Client;
import cn.floatingpoint.min.system.irc.GuiStatus;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.irc.connection.NetworkManager;
import cn.floatingpoint.min.system.irc.packet.Encoder;
import cn.floatingpoint.min.system.irc.packet.impl.*;
import cn.floatingpoint.min.utils.client.ChatUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.TimeUnit;

public class NetHandlerClient implements INetHandlerClient {
    private final NetworkManager netManager;

    public NetHandlerClient(NetworkManager netManager) {
        this.netManager = netManager;
    }

    @Override
    public void handleChat(SPacketChat packetIn) {
        if (!packetIn.getUsername().isEmpty()) {
            String originMessage = packetIn.getMessage();
            String prefix =
                    switch (packetIn.getRank()) {
                        case 0 -> "\2473[" + Managers.i18NManager.getTranslation("irc.player") + "]\2477";
                        case 1 -> "\247c[" + Managers.i18NManager.getTranslation("irc.youtuber") + "]\247f";
                        case 2 -> "\2474[" + Managers.i18NManager.getTranslation("irc.admin") + "]";
                        case 3 -> "\2476[" + Managers.i18NManager.getTranslation("irc.developer") + "]";
                        case 4 -> "\2474" + Managers.i18NManager.getTranslation("irc.server");
                        default -> "";
                    } + (packetIn.getRank() < 4 ? packetIn.getUsername() : "") + switch (packetIn.getRank()) {
                        case 1, 2, 3, 4 -> "\2477: \247f";
                        default -> "\2477: ";
                    };
            TextComponentString text = new TextComponentString("\247b[MIN-IRC]" + prefix + originMessage);
            if (packetIn.getRank() != 4) {
                text.getStyle()
                        .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatUtil.removeColor("@" + packetIn.getUsername() + " ")))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("\247e@" + packetIn.getUsername())));
            }
            ChatUtil.printToChat(text);
        } else {
            TextComponentString text = new TextComponentString("\247b[MIN-IRC]" + packetIn.getMessage());
            ChatUtil.printToChat(text);
        }
    }

    @Override
    public void handleDisconnect(SPacketDisconnect packetIn) {
        switch (packetIn.getType()) {
            case FORCE_BAN:
                if (!packetIn.getReason().isEmpty()) {
                    ChatUtil.printToChatWithPrefix("\247cYour are gonna be kicked by an admin!\nReason: You're banned from this server: " + packetIn.getReason());
                } else {
                    ChatUtil.printToChatWithPrefix("\247cYour are gonna be kicked by an admin!\nReason: You're banned from this server.");
                }
                mc.addScheduledTask(() -> {
                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException ignored) {

                    }
                    mc.shutdown();
                });
                break;
            case FORCE_KICK:
                if (!packetIn.getReason().isEmpty()) {
                    ChatUtil.printToChatWithPrefix("\247cYour are gonna be kicked by an admin!\nReason: " + packetIn.getReason());
                } else {
                    ChatUtil.printToChatWithPrefix("\247cYour are gonna be kicked by an admin!");
                }
                mc.addScheduledTask(() -> {
                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException ignored) {

                    }
                    mc.shutdown();
                });
        }
    }

    @Override
    public void handleAccount(SPacketAccount packetIn) {
        switch (packetIn.getStatus()) {
            case LOG_OUT:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247aLogged out!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.pass();
                }
                break;
            case PASS_LOGIN:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    gui.setTitle(Managers.i18NManager.getTranslation("login.fail.login"));
                    Client.setStatus("\247a" + Managers.i18NManager.getTranslation("login.logged").replace("{0}", "\247e" + packetIn.getUsername() + "\247a"));
                    Client.setUsername(packetIn.getUsername());
                    IRCClient.getInstance().connectedUser = Client.getUsername() != null;
                    Client.setLoggedIn(true);
                    gui.pass();
                }
                break;
            case FAIL_LOGIN:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    gui.setTitle(Managers.i18NManager.getTranslation("login.fail.login"));
                    Client.setStatus("\247c" + Managers.i18NManager.getTranslation("login.fail.password"));
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
            case FAIL_EXIST:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    gui.setTitle(Managers.i18NManager.getTranslation("login.fail.login"));
                    Client.setStatus("\247c" + Managers.i18NManager.getTranslation("login.fail.logged"));
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
            case FAIL_BANNED:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    long duration = packetIn.getDuration();
                    String last = "";
                    if (duration != 0) {
                        long days = TimeUnit.MILLISECONDS.toDays(duration);
                        long hours = TimeUnit.MILLISECONDS.toHours(duration) % 24;
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
                        if (days != 0) {
                            last += days + Managers.i18NManager.getTranslation(days == 1 ? "time.day" : "time.days") + " ";
                        }
                        if (hours != 0) {
                            last += hours + Managers.i18NManager.getTranslation(hours == 1 ? "time.hour" : "time.hours") + " ";
                        }
                        if (minutes != 0) {
                            last += minutes + Managers.i18NManager.getTranslation(minutes == 1 ? "time.minute" : "time.minutes") + " ";
                        }
                        if (seconds != 0) {
                            last += seconds + Managers.i18NManager.getTranslation(seconds == 1 ? "time.second" : "time.seconds") + " ";
                        }
                        last = Managers.i18NManager.getTranslation("irc.expire.at").replace("{0}", "\247f" + last.trim() + "\247c");
                    }
                    gui.setTitle(Managers.i18NManager.getTranslation("login.fail.login"));
                    Client.setStatus(
                            "\247c" + Managers.i18NManager.getTranslation("irc.banned") + "\247c" + last,
                            "",
                            "\2477" + Managers.i18NManager.getTranslation("irc.reason") + "\2477: \247f" + packetIn.getReason(),
                            "\2477" + Managers.i18NManager.getTranslation("irc.more") + "\247: \247b\247nhttps://appeal.minclient.xyz/",
                            "",
                            "\2477" + Managers.i18NManager.getTranslation("irc.id") + "\2477: \247f#" + packetIn.getUsername(),
                            "\2477" + Managers.i18NManager.getTranslation("irc.tip")
                    );
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
            case PASS_REGISTER:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247aThanks for using MIN Client!", "\247aYour account has been registered. Hope you'll have a good time!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.pass();
                }
                break;
            case FAIL_REGISTER_EXIST:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247cFailed to register(User existed)!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
        }
    }

    @Override
    public void handleKey(SPacketKey packetIn) {
        byte[] remotePublicKey = packetIn.getKey();
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(remotePublicKey);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            Encoder.key = keyFactory.generatePublic(publicSpec);
            Encoder.hasKey = true;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.netManager.sendPacket(new CPacketHandshake());
        if (IRCClient.getInstance().connectedUser) {
            IRCClient.getInstance().enableIRC();
        }
    }

    @Override
    public void handleTabComplete(SPacketTabComplete packetIn) {
        if (this.mc.currentScreen instanceof GuiChat guichat) {
            guichat.setCompletions(packetIn.getMatches());
        }
    }

    @Override
    public void handlePlayer(SPacketPlayer packetIn) {
        Managers.clientManager.clientMateUuids.put(packetIn.getUniqueId(), packetIn.getRank());
    }

    @Override
    public void handleMuted(SPacketMuted packetIn) {
        String reason = packetIn.getReason();
        long duration = packetIn.getDuration();
        String last = "";
        if (duration != 0) {
            long days = TimeUnit.MILLISECONDS.toDays(duration);
            long hours = TimeUnit.MILLISECONDS.toHours(duration) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;
            if (days != 0) {
                last += days + Managers.i18NManager.getTranslation(days == 1 ? "time.day" : "time.days") + " ";
            }
            if (hours != 0) {
                last += hours + Managers.i18NManager.getTranslation(hours == 1 ? "time.hour" : "time.hours") + " ";
            }
            if (minutes != 0) {
                last += minutes + Managers.i18NManager.getTranslation(minutes == 1 ? "time.minute" : "time.minutes") + " ";
            }
            if (seconds != 0) {
                last += seconds + Managers.i18NManager.getTranslation(seconds == 1 ? "time.second" : "time.seconds") + " ";
            }
            last = Managers.i18NManager.getTranslation("irc.expire.at").replace("{0}", "\247f" + last.trim() + "\247c");
        }
        ChatUtil.printToChat(new TextComponentString("\247m--------------------------------------------"));
        ChatUtil.printToChat(new TextComponentString(""));
        TextComponentString textComponents = new TextComponentString("   \247b[MIN-IRC] \247c" + Managers.i18NManager.getTranslation("irc.muted") + "\247c" + last);
        ChatUtil.printToChat(textComponents);
        textComponents = new TextComponentString("\2477   " + Managers.i18NManager.getTranslation("irc.reason") + "\2477: \247f" + reason);
        ChatUtil.printToChat(textComponents);
        ChatUtil.printToChat(new TextComponentString(""));
        ChatUtil.printToChat(new TextComponentString("\247m--------------------------------------------"));
    }
}
