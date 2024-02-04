package cn.floatingpoint.min.system.hyt.packet.impl;

import cn.floatingpoint.min.system.hyt.packet.CustomPacket;
import cn.floatingpoint.min.system.ui.hyt.germ.component.GermComponent;
import cn.floatingpoint.min.system.ui.hyt.germ.GuiComponentPage;
import cn.floatingpoint.min.system.ui.hyt.germ.GuiGermScreen;
import cn.floatingpoint.min.system.ui.hyt.germ.component.impl.*;
import cn.floatingpoint.min.system.ui.hyt.party.GuiInput;
import cn.floatingpoint.min.utils.client.ChatUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import me.konago.nativeobfuscator.Native;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import org.apache.commons.codec.binary.Base32;
import org.json.JSONArray;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class GermModPacket implements CustomPacket {
    private boolean shouldSend = false;
    private final Base32 base32 = new Base32();
    private String lastScreen;
    private byte[] totalBytes = null;
    private int counter;
    private String prevGuiUuid;

    @Override
    public String getChannel() {
        return "germplugin-netease";
    }

    @Override
    public void process(ByteBuf byteBuf) {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        process(packetBuffer);
    }

    // 什么？你问我为什么取消警告？那你删掉试试。
    @SuppressWarnings("all")
    private void process(PacketBuffer packetBuffer) {
        int packetId = packetBuffer.readInt();
        if (packetId == 73) {
            // Gui
            PacketBuffer packetBuffer1 = new PacketBuffer(packetBuffer.copy());
            String identity = packetBuffer1.readString(Short.MAX_VALUE);
            if (identity.equalsIgnoreCase("gui")) {
                String guiUuid = packetBuffer1.readString(Short.MAX_VALUE);
                String yml = packetBuffer1.readString(9999999);
                Yaml yaml = new Yaml();
                Map<String, Object> objectMap = yaml.load(yml);
                if (objectMap == null) return;
                lastScreen = guiUuid;
                objectMap = (Map<String, Object>) objectMap.get(guiUuid);
                if (objectMap == null) return;
                if (guiUuid.equalsIgnoreCase("mainmenu")) {
                    for (String key : objectMap.keySet()) {
                        if (yml.contains("只有队长才可以加入游戏哦")) {
                            ArrayList<GermComponent> components = new ArrayList<>();
                            components.add(new GermText("只有队长才可以加入游戏捏"));
                            components.add(new GermModButton("自适应背景$确认", "确认"));
                            mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                                    new PacketBuffer(Unpooled.buffer()
                                            .writeInt(4)
                                            .writeInt(0)
                                            .writeInt(0))
                                            .writeString(guiUuid)
                                            .writeString(guiUuid)
                                            .writeString(guiUuid)
                            ));
                            mc.displayGuiScreen(new GuiComponentPage(guiUuid, components));
                        } else {
                            if (key.equalsIgnoreCase("options") || key.endsWith("_bg")) continue;
                            Map<String, Object> context = (Map<String, Object>) objectMap.get(key);
                            if (context.containsKey("relativeParts")) {
                                context = (Map<String, Object>) context.get("relativeParts");
                                if (context.containsKey("主分类")) {
                                    context = (Map<String, Object>) context.get("主分类");
                                    if (context.containsKey("relativeParts")) {
                                        context = (Map<String, Object>) context.get("relativeParts");
                                        ArrayList<GermComponent> buttons = openGui(context.keySet(), guiUuid);
                                        if (!buttons.isEmpty()) {
                                            mc.displayGuiScreen(new GuiComponentPage(guiUuid, buttons));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    prevGuiUuid = guiUuid;
                } else if (guiUuid.startsWith("team_") && mc.player.ticksExisted > 6) {
                    vTeam(guiUuid, objectMap);
                    prevGuiUuid = guiUuid;
                } else {
                    for (String key : objectMap.keySet()) {
                        if (key.equalsIgnoreCase("options") || key.endsWith("_bg")) continue;
                        Map<String, Object> context = (Map<String, Object>) objectMap.get(key);
                        openOldGui(context, key, guiUuid);
                    }
                    prevGuiUuid = guiUuid;
                    return;
                }
            }
            //} else if (packetId == 737) {
            //    // Damage Display
            //    //if (Managers.moduleManager.renderModules.get("DamageParticles").isEnabled()) {
            //    //    String damage = packetBuffer.readString(30000);
            //    //    System.out.println("Damage:" + damage);
            //    //}
            //} else if (packetId == -1) {
            //    //System.out.println(packetBuffer.readString(Short.MAX_VALUE));
            //} else if (packetId == 2141) { // 大喇叭
            //    //System.out.println(packetBuffer.readString(Short.MAX_VALUE));
            //} else if (packetId == 78) {
            //    //System.out.println(packetBuffer.readString(Short.MAX_VALUE));
            //    //System.out.println(packetBuffer.readInt());
            //    //System.out.println(packetBuffer.readString(Short.MAX_VALUE));
            //} else {
            //    //int size = packetBuffer.readableBytes();
            //    //if (size > 0) {
            //        //System.out.println("Unknown packet id: " + packetId + ", size=" + size);
            //    //}
        } else if (packetId == 731) {
            shouldSend = true;
        } else if (packetId == 72) {
            if (shouldSend) {
                PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
                buffer.writeInt(16);
                buffer.writeString("3.4.2");
                buffer.writeString(Base64.getEncoder().encodeToString(encode("花雨庭你为什么要获取我们的计算机信息？").getBytes(StandardCharsets.UTF_8)));
                mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", buffer));
                shouldSend = false;
            }
        } else if (packetId == -1) {
            // Game Menu
            // Divide a byte array to multiple byte-array-packets
            boolean start = packetBuffer.readBoolean();
            int length = packetBuffer.readInt();
            boolean end = packetBuffer.readBoolean();
            byte[] tempBytes = packetBuffer.readByteArray();
            if (start) {
                totalBytes = new byte[length];
            }
            try {
                System.arraycopy(tempBytes, 0, totalBytes, counter, tempBytes.length);
                counter += tempBytes.length;
                if (end) {
                    ByteBuf buf = Unpooled.wrappedBuffer(totalBytes);
                    process(buf);
                    counter = 0;
                    totalBytes = null;
                }
            } catch (Exception e) {
                counter = 0;
                totalBytes = null;
            }
        } else if (packetId == 79) {
            String type = packetBuffer.readString(Short.MAX_VALUE);
            int action = packetBuffer.readInt();
            String content = packetBuffer.readString(3276700);
            if (type.equals("GUI") && action == 0) {
                if (lastScreen.equals("mainmenu")) {
                    updateScreen(content);
                }
            }
        } else if (packetId == 737) {
            // Lobby HUD
        } else if (packetId == 714) {
            // Lobby HUD Position
        } else if (packetId == 723) {
            // Lobby HUD Key
        } else if (packetId == 2141) {
            // 蠢比大喇叭
        } else {
            if (Minecraft.DEBUG_MODE()) {
                int size = packetBuffer.readableBytes();
                if (size > 0) {
                    System.out.println("Unknown packet id: " + packetId + ", size=" + size);
                }
            }
        }
    }

    @SuppressWarnings("all")
    @Native
    private void vTeam(String guiUuid, Map<String, Object> objectMap) {
        switch (guiUuid) {
            case "team_create" -> {
                ArrayList<GermComponent> buttons = new ArrayList<>();
                buttons.add(
                        new GermModButton("create", "创建队伍") {
                            @Override
                            protected void whenClick() {
                                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                                        new PacketBuffer(Unpooled.buffer().writeInt(26))
                                                .writeString("GUI$team_create@create")
                                                .writeString("{\"null\":null}")));
                            }
                        }
                );
                buttons.add(new GermModButton("join", "加入队伍") {
                    @Override
                    protected void whenClick() {
                        Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                                new PacketBuffer(Unpooled.buffer().writeInt(26))
                                        .writeString("GUI$team_create@join")
                                        .writeString("{\"null\":null}")));
                    }
                });
                mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                        new PacketBuffer(Unpooled.buffer()
                                .writeInt(4)
                                .writeInt(0)
                                .writeInt(0))
                                .writeString(guiUuid)
                                .writeString(guiUuid)
                                .writeString(guiUuid)
                ));
                mc.displayGuiScreen(new GuiComponentPage(guiUuid, buttons).title("花雨庭组队系统"));
            }
            case "team_list" -> {
                Map<String, Object> context = (Map<String, Object>) objectMap.get("scroll");
                context = (Map<String, Object>) context.get("scrollableParts");
                ArrayList<GermComponent> components = new ArrayList<>();
                for (String key : context.keySet()) {
                    Map<String, Object> entry = (Map<String, Object>) ((Map<String, Object>) context.get(key)).get("relativeParts");
                    String playerName = ((ArrayList<String>) ((Map<String, Object>) entry.get("name")).get("texts")).get(0);
                    entry = (Map<String, Object>) entry.get("bt");
                    String type = ((ArrayList<String>) entry.get("tooltip")).get(0);
                    if (type.equals("接受邀请")) {
                        components.add(new GermPartyInvitation(playerName));
                    } else if (type.equals("申请加入")) {
                        components.add(new GermPartyApply(playerName));
                    }
                }
                components.add(new GermModButton("input", "手动输入") {
                    @Override
                    protected boolean doesCloseOnClickButton() {
                        return false;
                    }
                });
                mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                        new PacketBuffer(Unpooled.buffer()
                                .writeInt(4)
                                .writeInt(0)
                                .writeInt(0))
                                .writeString(guiUuid)
                                .writeString(guiUuid)
                                .writeString(guiUuid)
                ));
                mc.displayGuiScreen(new GuiComponentPage(guiUuid, components));
                // Old-fashioned handed input
                //mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                //        .writeString(guiUuid)
                //        .writeString("input")
                //        .writeInt(2))));
                //mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                //        .writeString(guiUuid)
                //        .writeString("input")
                //        .writeInt(0))));
                //Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                //        new PacketBuffer(Unpooled.buffer()
                //                .writeInt(26))
                //                .writeString("GUI$team_list@input")
                //                .writeString("{\"null\":null}")));
            }
            case "team_input" -> {
                mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                        new PacketBuffer(Unpooled.buffer()
                                .writeInt(4)
                                .writeInt(0)
                                .writeInt(0))
                                .writeString(mc.currentScreen == null ? prevGuiUuid : ((GuiGermScreen) mc.currentScreen).getUUID())
                                .writeString(prevGuiUuid)
                                .writeString(guiUuid)
                ));
                mc.displayGuiScreen(new GuiInput((GuiGermScreen) mc.currentScreen, guiUuid, prevGuiUuid));
            }
            case "team_main" -> {
                Map<String, Object> context = (Map<String, Object>) objectMap.get("buttons");
                context = (Map<String, Object>) context.get("relativeParts");
                ArrayList<GermComponent> buttons = new ArrayList<>();
                for (String key : context.keySet()) {
                    Map<String, Object> buttonMap = (Map<String, Object>) context.get(key);
                    String postRequest = buttonMap.get("clickScript").toString().trim();
                    postRequest = postRequest.substring(16, postRequest.length() - 3);
                    String postAction = postRequest.split(",\\{")[0];
                    postAction = postAction.substring(0, postAction.length() - 1);
                    String finalPostAction = postAction;
                    buttons.add(new GermModButton("buttons$" + key, ((ArrayList<String>) buttonMap.get("texts")).get(0)) {
                        @Override
                        protected void whenClick() {
                            Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                                    new PacketBuffer(Unpooled.buffer().writeInt(26))
                                            .writeString("GUI$team_main@" + finalPostAction)
                                            .writeString("{\"null\":null}")));
                        }

                        @Override
                        protected boolean doesCloseOnClickButton() {
                            return false;
                        }
                    });
                }
                mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                        new PacketBuffer(Unpooled.buffer()
                                .writeInt(4)
                                .writeInt(0)
                                .writeInt(0))
                                .writeString(guiUuid)
                                .writeString(guiUuid)
                                .writeString(guiUuid)
                ));
                mc.displayGuiScreen(new GuiComponentPage(guiUuid, buttons).title(((ArrayList<String>) ((Map<String, Object>) objectMap.get("title")).get("texts")).get(0)));
            }
            case "team_request_list" -> {
                objectMap = (Map<String, Object>) objectMap.get("scroll");
                objectMap = (Map<String, Object>) objectMap.get("scrollableParts");
                if (objectMap == null) {
                    ChatUtil.printToChatWithPrefix("\247a\247l没有人申请你的组队捏！");
                } else {
                    ArrayList<GermComponent> components = new ArrayList<>();
                    for (String keyEntry : objectMap.keySet()) {
                        Map<String, Object> childEntry = (Map<String, Object>) objectMap.get(keyEntry);
                        childEntry = (Map<String, Object>) childEntry.get("relativeParts");
                        String textName = ((ArrayList<String>) ((Map<String, Object>) childEntry.get("name")).get("texts")).get(0);
                        mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                                new PacketBuffer(Unpooled.buffer()
                                        .writeInt(4)
                                        .writeInt(0)
                                        .writeInt(0))
                                        .writeString(guiUuid)
                                        .writeString(guiUuid)
                                        .writeString(guiUuid)
                        ));
                        components.add(new GermPartyRequest(keyEntry.substring(6)));
                    }
                    mc.displayGuiScreen(new GuiComponentPage(guiUuid, components).title("申请列表"));
                }
            }
            case "team_invite_list" -> {
                Map<String, Object> context = (Map<String, Object>) objectMap.get("scroll");
                context = (Map<String, Object>) context.get("scrollableParts");
                ArrayList<GermComponent> components = new ArrayList<>();
                for (String key : context.keySet()) {
                    Map<String, Object> entry = (Map<String, Object>) ((Map<String, Object>) context.get(key)).get("relativeParts");
                    String playerName = ((ArrayList<String>) ((Map<String, Object>) entry.get("name")).get("texts")).get(0);
                    components.add(new GermPartyInvite(playerName));
                }
                components.add(new GermModButton("input", "手动输入") {
                    @Override
                    protected boolean doesCloseOnClickButton() {
                        return false;
                    }
                });
                mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                        new PacketBuffer(Unpooled.buffer()
                                .writeInt(4)
                                .writeInt(0)
                                .writeInt(0))
                                .writeString(guiUuid)
                                .writeString(guiUuid)
                                .writeString(guiUuid)
                ));
                mc.displayGuiScreen(new GuiComponentPage(guiUuid, components));

                // Old-fashioned handed input
                //mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                //        .writeString(guiUuid)
                //        .writeString("input")
                //        .writeInt(2))));
                //mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13))
                //        .writeString(guiUuid)
                //        .writeString("input")
                //        .writeInt(0))));
                //Minecraft.getMinecraft().player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                //        new PacketBuffer(Unpooled.buffer().writeInt(26))
                //                .writeString("GUI$team_invite_list@input")
                //                .writeString("{\"null\":null}")
                //));
            }
            case "team_kick_list" -> {
                objectMap = (Map<String, Object>) objectMap.get("scroll");
                objectMap = (Map<String, Object>) objectMap.get("scrollableParts");
                if (objectMap == null) {
                    ChatUtil.printToChatWithPrefix("\247a\247l你的队伍里就剩下你一个人了捏！");
                } else {
                    ArrayList<GermComponent> components = new ArrayList<>();
                    for (String keyEntry : objectMap.keySet()) {
                        Map<String, Object> childEntry = (Map<String, Object>) objectMap.get(keyEntry);
                        childEntry = (Map<String, Object>) childEntry.get("relativeParts");
                        String textName = ((ArrayList<String>) ((Map<String, Object>) childEntry.get("name")).get("texts")).get(0);
                        mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                                new PacketBuffer(Unpooled.buffer()
                                        .writeInt(4)
                                        .writeInt(0)
                                        .writeInt(0))
                                        .writeString(guiUuid)
                                        .writeString(guiUuid)
                                        .writeString(guiUuid)
                        ));
                        components.add(new GermPartyKick(keyEntry.substring(6)));
                    }
                    mc.displayGuiScreen(new GuiComponentPage(guiUuid, components).title("踢出队员"));
                }
            }
        }
    }

    @Native
    private void updateScreen(String content) {
        ArrayList<GermComponent> buttons = getButtons(content);
        if (!buttons.isEmpty()) {
            mc.displayGuiScreen(new GuiComponentPage("mainmenu", buttons));
        }
    }

    @SuppressWarnings("all")
    private void openOldGui(Map<String, Object> context, String key, String guiUuid) {
        ArrayList<GermComponent> buttons = new ArrayList<>();
        for (String k : context.keySet()) {
            if (!k.equalsIgnoreCase("scrollableParts")) continue;
            context = (Map<String, Object>) context.get("scrollableParts");
            for (String uuid : context.keySet()) {
                Map<String, Object> scrollableSubMap = (Map<String, Object>) context.get(uuid);
                if (scrollableSubMap.containsKey("relativeParts")) {
                    scrollableSubMap = (Map<String, Object>) scrollableSubMap.get("relativeParts");
                    for (String k1 : scrollableSubMap.keySet()) {
                        scrollableSubMap = (Map<String, Object>) scrollableSubMap.get(k1);
                        if (scrollableSubMap == null) return;
                        if (scrollableSubMap.containsKey("texts")) {
                            String buttonText = ((ArrayList<String>) scrollableSubMap.get("texts")).get(0);
                            buttons.add(new GermModButton(key + "$" + uuid + "$" + k1, buttonText));
                            break;
                        }
                    }
                }
            }
        }
        if (!buttons.isEmpty()) {
            mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                    new PacketBuffer(Unpooled.buffer()
                            .writeInt(4)
                            .writeInt(0)
                            .writeInt(0))
                            .writeString(guiUuid)
                            .writeString(guiUuid)
                            .writeString(guiUuid)
            ));
            mc.displayGuiScreen(new GuiComponentPage(guiUuid, buttons));
        }
    }

    private ArrayList<GermComponent> getButtons(String content) {
        StringTokenizer token = new StringTokenizer(content, "@");
        token.nextToken();
        if (!token.nextToken().equals("data")) return new ArrayList<>();
        String json = token.nextToken();
        JSONObject subs = new JSONObject(json);
        JSONArray jsonArray = subs.getJSONArray("subs");
        return analyzeButton(jsonArray);
    }

    private ArrayList<GermComponent> analyzeButton(JSONArray jsonArray) {
        ArrayList<GermComponent> buttons = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object o = jsonArray.get(i);
            if (o instanceof JSONObject jsonObject) {
                String sid = jsonObject.getString("sid");
                String name = jsonObject.getString("name").replace("&e&l", "").replace("\247m", "");
                buttons.add(new DetailedGameButton("自适应背景$细分分类$游戏" + i, name, i, sid));
            }
        }
        return buttons;
    }

    private ArrayList<GermComponent> openGui(Set<String> keys, String uuid) {
        ArrayList<GermComponent> buttons = new ArrayList<>();
        for (String k1 : keys) {
            String buttonText = getText(k1);
            if (buttonText.isEmpty()) continue;
            buttons.add(new GameButton("自适应背景$主分类$" + k1, buttonText, k1));
        }
        mc.player.connection.sendPacket(new CPacketCustomPayload("germmod-netease",
                new PacketBuffer(Unpooled.buffer()
                        .writeInt(4)
                        .writeInt(0)
                        .writeInt(0))
                        .writeString(uuid)
                        .writeString(uuid)
                        .writeString(uuid)
        ));
        return buttons;
    }

    private String getText(String key) {
        return switch (key) {
            case "subject_bedwar" -> "起床战争";
            case "subject_skywar" -> "空岛战争";
            case "subject_leisure" -> "休闲游戏";
            case "subject_fight" -> "竞技游戏";
            case "subject_survive" -> "生存";
            case "subject_fight_team" -> "战争";
            default -> "";
        };
    }

    public String encode(String string) {
        String s = encode("1qaz2wsx3edc4ds6g4f4g65a7ujm8ik,9ol.0p;/", string);
        return encodeAgain("!QAZ@WSX#EDC$RFV%TGB^YHN&UJM*IK<(OL>)P:?", s);
    }

    public String encodeAgain(String key, String s) {
        return base32.encodeAsString(getKeyBytes(key, s.getBytes())).replaceAll("=", "");
    }

    public static String encode(String key, String s) {
        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(getKeyBytes(key, s.getBytes()));
    }

    public static byte[] getKeyBytes(String string, byte[] byArray) {
        byte[] byArray2 = null;
        try {
            Key key = generateKey(string);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(1, key);
            byArray2 = cipher.doFinal(byArray);
            return byArray2;
        } catch (Exception exception) {
            return byArray2;
        }
    }

    private static Key generateKey(String string) {
        SecureRandom secureRandom;
        KeyGenerator keyGenerator = null;
        SecureRandom secureRandom2 = null;
        try {
            keyGenerator = KeyGenerator.getInstance("DES");
            secureRandom = secureRandom2 = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            secureRandom = secureRandom2;
            noSuchAlgorithmException.printStackTrace();
        }
        secureRandom.setSeed(string.getBytes());
        KeyGenerator keyGenerator2 = keyGenerator;
        keyGenerator2.init(secureRandom2);
        return keyGenerator2.generateKey();
    }
}
