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

import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

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
            Encoder.key =  keyFactory.generatePublic(publicSpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        Encoder.hasKey = true;
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
