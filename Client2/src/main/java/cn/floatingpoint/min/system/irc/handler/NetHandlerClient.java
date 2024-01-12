package cn.floatingpoint.min.system.irc.handler;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.irc.Client;
import cn.floatingpoint.min.system.irc.GuiStatus;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.irc.connection.NetworkManager;
import cn.floatingpoint.min.system.irc.packet.Decoder;
import cn.floatingpoint.min.system.irc.packet.Encoder;
import cn.floatingpoint.min.system.irc.packet.impl.*;
import cn.floatingpoint.min.utils.client.ChatUtil;
import cn.floatingpoint.min.utils.math.DHUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.text.TextComponentString;

public class NetHandlerClient implements INetHandlerClient {
    private final NetworkManager netManager;

    public NetHandlerClient(NetworkManager netManager) {
        this.netManager = netManager;
    }

    @Override
    public void handleChat(SPacketChat packetIn) {
        ChatUtil.printToChat(new TextComponentString(packetIn.getMessage()));
    }

    @Override
    public void handleDisconnect(SPacketDisconnect packetIn) {
        switch (packetIn.getType()) {
            case FORCE_BAN:
                ChatUtil.printToChatWithPrefix("\247cYour are gonna be kicked by an admin!");
                mc.addScheduledTask(() -> {
                    try {
                        Thread.sleep(1500L);
                    } catch (InterruptedException ignored) {

                    }
                    mc.shutdown();
                });
                break;
            case FORCE_KICK:
                ChatUtil.printToChatWithPrefix("\247cYou are gonna be kicked by an admin!");
                mc.addScheduledTask(() -> {
                    try {
                        Thread.sleep(1500L);
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
                    Client.setStatus("\247aLogged in(\247e" + packetIn.getUsername() + "\247a)!");
                    Client.setUsername(packetIn.getUsername());
                    IRCClient.getInstance().connectedUser = Client.getUsername() != null;
                    Client.setLoggedIn(true);
                    gui.pass();
                }
                break;
            case FAIL_HWID:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247cFailed to login(Wrong HWID)!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
            case FAIL_LOGIN:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247cFailed to login(Unknown username or password)!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
            case FAIL_EXIST:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247cFailed to login(You've already logged in IRC server)!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
            case FAIL_BANNED:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247cFailed to login(Account has been banned)!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
            case FAIL_EXPIRED:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247cSorry, this account is expired!");
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
            case FAIL_REGISTER_INVALID_CODE:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247cFailed to register(Invalid code)!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
            case PASS_RENEW:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247aAccount renewed!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.pass();
                }
                break;
            case FAIL_RENEW_BANNED:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247cFailed to renew(Account has been banned)!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
            case FAIL_RENEW_UNKNOWN:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247cFailed to renew(Account not found)!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
            case FAIL_RENEW_UNEXPIRED:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247cFailed to renew(Account is unexpired)!");
                    Client.setLoggedIn(false);
                    Client.setUsername(null);
                    Client.setPassword(null);
                    gui.fail();
                }
                break;
            case FAIL_RENEW_INVALID_CODE:
                if (this.mc.currentScreen instanceof GuiStatus gui) {
                    Client.setStatus("\247cFailed to renew(Invalid code)!");
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
        try {
            byte[] privateKey = Decoder.key;
            Decoder.key = DHUtil.getSecretKey(remotePublicKey, privateKey);
            Encoder.key = DHUtil.getSecretKey(remotePublicKey, privateKey);
        } catch (Exception e) {
            this.netManager.getChannel().close();
        }
        Encoder.hasKey = Decoder.hasKey = true;
        this.netManager.sendPacket(new CPacketHandshake());
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
}
