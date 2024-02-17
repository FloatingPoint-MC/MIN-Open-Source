package cn.floatingpoint.min.system.replay.server;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.replay.Replay;
import cn.floatingpoint.min.system.replay.packet.C2SPacket;
import cn.floatingpoint.min.system.replay.packet.ChunkPacket;
import cn.floatingpoint.min.system.replay.packet.RecordedPacket;
import cn.floatingpoint.min.system.replay.packet.S2CPacket;
import cn.floatingpoint.min.system.ui.replay.GuiManageTick;
import cn.floatingpoint.min.system.ui.replay.GuiTeleport;
import cn.floatingpoint.min.utils.client.ChatUtil;
import cn.floatingpoint.min.utils.client.PlayerUtil;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.*;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class ReplayServer {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Replay replay;
    private NetHandlerPlayClient netHandler;

    // Net Handler Replay Server
    private int slot = 0;
    private final ArrayList<Entity> entities = new ArrayList<>();
    public EntityOtherPlayerMP self;

    private boolean running;
    private State state;
    private int updateCounter;
    private long encodedPosX, encodedPosY, encodedPosZ;


    public ReplayServer(Replay replay) {
        this.replay = replay;
        mc.loadWorld(null);
        if (!Managers.moduleManager.boostModules.get("FastLoad").isEnabled()) {
            System.gc();
        }
        Managers.replayManager.loadReplay(this);
        running = true;
        state = State.PAUSED;
        Managers.replayManager.setPlaying(false);
        Thread replayServerThread = new Thread("Replay Server Thread") {
            @Override
            public void run() {
                try {
                    netHandler = new NetHandlerPlayClient(mc, null, new ReplayNetworkManager(), new GameProfile(UUID.nameUUIDFromBytes("Replay:Player".getBytes(StandardCharsets.UTF_8)), "ReplayPlayer"));
                    mc.addScheduledTask(ReplayServer.this::acceptPlayer);
                } catch (Exception ignored) {
                }
            }
        };
        replayServerThread.start();
    }

    private void acceptPlayer() {
        netHandler.handleJoinGame(new SPacketJoinGame(696, GameType.ADVENTURE, false, DimensionType.OVERWORLD.getId(), EnumDifficulty.EASY, 999, WorldType.DEFAULT, false));
        netHandler.handleCustomPayload(new SPacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("MIN-Replay-Server")));
        netHandler.handleServerDifficulty(new SPacketServerDifficulty(EnumDifficulty.EASY, true));
        PlayerCapabilities playerCapabilities = new PlayerCapabilities();
        playerCapabilities.isFlying = true;
        playerCapabilities.allowFlying = true;
        playerCapabilities.allowEdit = false;
        netHandler.handlePlayerAbilities(new SPacketPlayerAbilities(playerCapabilities));
        netHandler.handleHeldItemChange(new SPacketHeldItemChange(5));
        netHandler.handleEntityStatus(new SPacketEntityStatus(mc.player, (byte) 4));
        for (RecordedPacket packet : replay.getPackets().get(-1)) {
            SPacketChunkData data = new SPacketChunkData();
            try {
                data.readPacketData(new PacketBuffer(packet.packetBuffer().copy()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            netHandler.handleChunkData(data);
        }
        netHandler.handlePlayerPosLook(new SPacketPlayerPosLook(replay.getSpawnPos().getX(), replay.getSpawnPos().getY(), replay.getSpawnPos().getZ(), 0.0f, 0.0f, Collections.emptySet(), 0));
        self = new EntityOtherPlayerMP(mc.world, new GameProfile(PlayerUtil.formUUID(replay.getUuid()), replay.getEntityName())) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        };
        self.setEntityId(replay.getEntityId());
        self.setPositionAndRotation(replay.getSpawnPos().getX(), replay.getSpawnPos().getY(), replay.getSpawnPos().getZ(), 0.0f, 0.0f);
        mc.world.addEntityToWorld(replay.getEntityId(), self);
        replaceReplayItems();
    }

    private void replaceReplayItems() {
        mc.player.inventory.mainInventory.clear();
        NBTTagCompound tagCompound;
        NBTTagList lore = new NBTTagList();
        lore.appendTag(new NBTTagString("\247e右键使用"));

        ItemStack teleport = new ItemStack(Items.COMPASS);
        tagCompound = new NBTTagCompound();
        tagCompound.setTag("Lore", lore);
        teleport.setTagInfo("display", tagCompound);
        teleport.setStackDisplayName("\247a传送器");
        mc.player.inventory.mainInventory.set(0, teleport);

        ItemStack control = new ItemStack(Items.DYE, 1, state == State.PAUSED ? 10 : 1);
        tagCompound = new NBTTagCompound();
        tagCompound.setTag("Lore", lore);
        control.setTagInfo("display", tagCompound);
        control.setStackDisplayName(state == State.PAUSED ? "\247a播放" : "\247e暂停");
        mc.player.inventory.mainInventory.set(4, control);

        ItemStack time = new ItemStack(Items.CLOCK);
        tagCompound = new NBTTagCompound();
        tagCompound.setTag("Lore", lore);
        time.setTagInfo("display", tagCompound);
        time.setStackDisplayName("\247e时间");
        mc.player.inventory.mainInventory.set(8, time);
    }

    public Replay getReplay() {
        return replay;
    }

    @SuppressWarnings("unchecked")
    public void readTick() throws IOException {
        if (replay.tick == -2) {
            ArrayList<RecordedPacket> packets = replay.getPackets().get(-1);
            for (RecordedPacket recordedPacket : packets) {
                SPacketChunkData data = new SPacketChunkData();
                data.readPacketData(new PacketBuffer(recordedPacket.packetBuffer().copy()));
                SPacketUnloadChunk unload = new SPacketUnloadChunk(data.getChunkX(), data.getChunkZ());
                netHandler.processChunkUnload(unload);
            }
            mc.world.removeAllEntities();
        } else {
            ArrayList<RecordedPacket> packets = replay.getPackets().get(replay.tick);
            String totalTimeStamp = "";
            double currentTick = replay.tick * 0.05;
            int secs = (int) (currentTick / 60);
            int rest = (int) (currentTick % 60);
            if (secs < 10) {
                totalTimeStamp += "0" + secs + ":";
            } else {
                totalTimeStamp += secs + ":";
            }
            if (rest < 10) {
                totalTimeStamp += "0" + rest + "/";
            } else {
                totalTimeStamp += rest + "/";
            }
            double totalTime = replay.totalTicks * 0.05;
            secs = (int) (totalTime / 60);
            rest = (int) (totalTime % 60);
            if (secs < 10) {
                totalTimeStamp += "0" + secs + ":";
            } else {
                totalTimeStamp += secs + ":";
            }
            if (rest < 10) {
                totalTimeStamp += "0" + rest;
            } else {
                totalTimeStamp += rest;
            }
            mc.ingameGUI.setOverlayMessage("\247a" + totalTimeStamp, false);
            if (packets != null) {
                for (RecordedPacket recordedPacket : packets) {
                    if (recordedPacket instanceof C2SPacket) {
                        Packet<INetHandlerPlayClient> packet;
                        try {
                            packet = (Packet<INetHandlerPlayClient>) EnumConnectionState.PLAY.getPacket(EnumPacketDirection.SERVERBOUND, recordedPacket.packetId());
                        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                                 InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                        assert packet != null;
                        packet.readPacketData(new PacketBuffer(recordedPacket.packetBuffer().copy()));
                        mc.addScheduledTask(() -> handleCPacket(packet));
                    } else if (recordedPacket instanceof S2CPacket) {
                        Packet<INetHandlerPlayClient> packet;
                        try {
                            packet = (Packet<INetHandlerPlayClient>) EnumConnectionState.PLAY.getPacket(EnumPacketDirection.CLIENTBOUND, recordedPacket.packetId());
                        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                                 InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                        assert packet != null;
                        packet.readPacketData(new PacketBuffer(recordedPacket.packetBuffer().copy()));
                        handleSPacket(packet);
                    } else if (recordedPacket instanceof ChunkPacket) {
                        SPacketChunkData data = new SPacketChunkData();
                        data.readPacketData(new PacketBuffer(recordedPacket.packetBuffer().copy()));
                        netHandler.handleChunkData(data);
                    }
                }
            }
            if (replay.tick >= replay.totalTicks) {
                ChatUtil.printToChatWithPrefix("Replay stopped.");
                Managers.replayManager.setPlaying(false);
                state = State.PAUSED;
                replay.tick = -2;
                replaceReplayItems();
            }
        }
    }

    class ReplayNetworkManager extends NetworkManager {

        @Override
        public void dispatchPacket(@Nonnull Packet<?> inPacket, @Nullable GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
            mc.addScheduledTask(() -> {
                if (inPacket instanceof CPacketPlayer) {
                } else if (inPacket instanceof CPacketPlayerTryUseItem) {
                    switch (slot) {
                        case 0 ->
                                mc.displayGuiScreen(new GuiTeleport(new InventoryPlayer(mc.player), new InventoryBasic(new TextComponentString("传送器"), (int) Math.ceil(mc.world.playerEntities.size() / 9.0) * 9), mc.world.playerEntities));
                        case 4 -> {
                            if (state == State.PAUSED) {
                                state = State.PLAYING;
                                Managers.replayManager.setPlaying(true);
                            } else if (state == State.PLAYING) {
                                state = State.PAUSED;
                                Managers.replayManager.setPlaying(false);
                            }
                            replaceReplayItems();
                        }
                        case 9 ->
                                mc.displayGuiScreen(new GuiManageTick(new InventoryPlayer(mc.player), new InventoryBasic(new TextComponentString("时间管理"), 9)));
                    }
                } else if (inPacket instanceof CPacketClickWindow packet) {
                    if (mc.currentScreen instanceof GuiTeleport) {
                        if (packet.getClickedItem() != ItemStack.EMPTY) {
                            String playerName = packet.getClickedItem().getDisplayName().replace("\247f", "");
                            EntityPlayer player = mc.world.getPlayerEntityByName(playerName);
                            if (player == null) {
                                ChatUtil.printToChatWithPrefix("\247c玩家不存在！");
                            } else {
                                mc.player.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                                mc.renderGlobal.loadRenderers();
                            }
                            mc.displayGuiScreen(null);
                        }
                    } else if (mc.currentScreen instanceof GuiManageTick) {
                        if (packet.getClickedItem() != ItemStack.EMPTY) {
                            Item item = packet.getClickedItem().getItem();
                            if (item == Items.DYE) {
                                boolean isPlaying = Managers.replayManager.isPlaying();
                                Managers.replayManager.setPlaying(false);
                                try {
                                    replay.tick = -2;
                                    readTick();
                                    replay.tick = -1;
                                    readTick();
                                    replay.tick = 0;
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                Managers.replayManager.setPlaying(isPlaying);
                            } else if (item == Items.SUGAR) {
                                boolean isPlaying = Managers.replayManager.isPlaying();
                                Managers.replayManager.setPlaying(false);
                                int dest = Math.max(0, replay.tick - 200);
                                try {
                                    for (int i = -2; i < dest; i++) {
                                        replay.tick = i;
                                        readTick();
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                Managers.replayManager.setPlaying(isPlaying);
                            } else if (item == Items.GLOWSTONE_DUST) {
                                boolean isPlaying = Managers.replayManager.isPlaying();
                                Managers.replayManager.setPlaying(false);
                                int currentTick = replay.tick;
                                int dest = Math.max(0, currentTick + 200);
                                try {
                                    for (int i = currentTick; i < dest; i++) {
                                        replay.tick = i;
                                        readTick();
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                Managers.replayManager.setPlaying(isPlaying);
                            }
                            mc.displayGuiScreen(null);
                        }
                    } else {
                        mc.displayGuiScreen(null);
                    }
                    replaceReplayItems();
                } else if (inPacket instanceof CPacketHeldItemChange packet) {
                    slot = packet.getSlotId();
                }
            });
        }

        @Override
        public void closeChannel(@Nonnull ITextComponent message) {

        }

        @Override
        public boolean isChannelOpen() {
            return true;
        }
    }

    public void handleCPacket(Packet<?> inPacket) {
        try {
            if (inPacket instanceof CPacketAnimation) {
                handleSPacket(new SPacketAnimation(self, 0));
            } else if (inPacket instanceof CPacketPlayer packet) {
                double x = packet.isMoving() ? packet.getX() : self.posX;
                double y = packet.isMoving() ? packet.getY() : self.posY;
                double z = packet.isMoving() ? packet.getZ() : self.posZ;

                float yaw = packet.isRotating() ? packet.getYaw(0.0f) : self.rotationYaw;
                float pitch = packet.isRotating() ? packet.getPitch(0.0f) : self.rotationPitch;
                self.setPositionAndRotationDirect(x, y, z, yaw, pitch, 3, false);
                self.onGround = packet.isOnGround();
            } else if (inPacket instanceof CPacketHeldItemChange packet) {
                self.inventory.currentItem = packet.getSlotId();
            }
        } catch (Exception ignore) {
        }
    }

    public void handleSPacket(Packet<INetHandlerPlayClient> inPacket) {
        if (inPacket instanceof SPacketUnloadChunk) {
            return;
        }
        try {
            inPacket.processPacket(netHandler);
        } catch (Exception ignore) {

        }
    }
}
