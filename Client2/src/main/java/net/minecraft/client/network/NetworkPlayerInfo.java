package net.minecraft.client.network;

import cn.floatingpoint.min.MIN;
import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.management.impl.ClientManager;
import cn.floatingpoint.min.system.irc.IRCClient;
import cn.floatingpoint.min.system.irc.packet.impl.CPacketPlayer;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;

public class NetworkPlayerInfo {
    /**
     * The GameProfile for the player represented by this NetworkPlayerInfo instance
     */
    private final GameProfile gameProfile;
    public Map<Type, ResourceLocation> playerTextures = Maps.newEnumMap(Type.class);
    private GameType gameType;

    /**
     * Player response time to server in milliseconds
     */
    private int responseTime;
    private boolean playerTexturesLoaded;
    public String skinType;

    /**
     * When this is non-null, it is displayed instead of the player's real name
     */
    private ITextComponent displayName;
    private int lastHealth;
    private int displayHealth;
    private long lastHealthTime;
    private long healthBlinkTime;
    private long renderVisibilityId;

    public NetworkPlayerInfo(SPacketPlayerListItem.AddPlayerData entry) {
        this.gameProfile = entry.getProfile();
        this.gameType = entry.getGameMode();
        this.responseTime = entry.getPing();
        this.displayName = entry.getDisplayName();
    }

    /**
     * Returns the GameProfile for the player represented by this NetworkPlayerInfo instance
     */
    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    protected void setGameType(GameType gameMode) {
        this.gameType = gameMode;
    }

    public int getResponseTime() {
        return this.responseTime;
    }

    protected void setResponseTime(int latency) {
        this.responseTime = latency;
    }

    public String getSkinType() {
        return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.gameProfile.getId()) : this.skinType;
    }

    public ResourceLocation getLocationSkin() {
        this.loadPlayerTextures();
        return MoreObjects.firstNonNull(this.playerTextures.get(Type.SKIN), DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
    }

    @Nullable
    public ResourceLocation getLocationCape() {
        this.loadPlayerTextures();
        return this.playerTextures.get(Type.CAPE);
    }


    /**
     * Gets the special Elytra texture for the player.
     */
    @Nullable
    public ResourceLocation getLocationElytra() {
        this.loadPlayerTextures();
        return this.playerTextures.get(Type.ELYTRA);
    }

    @Nullable
    public ScorePlayerTeam getPlayerTeam() {
        return Minecraft.getMinecraft().world.getScoreboard().getPlayersTeam(this.getGameProfile().getName());
    }

    protected void loadPlayerTextures() {
        synchronized (this) {
            if (!this.playerTexturesLoaded) {
                this.playerTexturesLoaded = true;
                MIN.runAsync(() -> {
                    if (gameProfile.getName().contains("\247")) {
                        return;
                    }
                    GameProfile gameProfile = null;
                    ClientManager.ClientMate clientMate = Managers.clientManager.clientMateUuids.get(this.gameProfile.getId());
                    if (clientMate != null) {
                        if (!clientMate.skinName().isEmpty()) {
                            gameProfile = new GameProfile(clientMate.skinId(), clientMate.skinName());
                            Minecraft.getMinecraft().getSessionService().fillProfileProperties(gameProfile, true);
                        }
                    } else {
                        IRCClient.getInstance().addToSendQueue(new CPacketPlayer(this.gameProfile.getId()));
                    }
                    Minecraft.getMinecraft().getSkinManager().loadProfileTextures(gameProfile == null ? this.gameProfile : gameProfile, (typeIn, location, profileTexture) -> {
                        switch (typeIn) {
                            case SKIN -> {
                                NetworkPlayerInfo.this.playerTextures.put(Type.SKIN, location);
                                NetworkPlayerInfo.this.skinType = profileTexture.getMetadata("model");
                                if (NetworkPlayerInfo.this.skinType == null) {
                                    NetworkPlayerInfo.this.skinType = "default";
                                }
                            }
                            case CAPE -> NetworkPlayerInfo.this.playerTextures.put(Type.CAPE, location);
                            case ELYTRA -> NetworkPlayerInfo.this.playerTextures.put(Type.ELYTRA, location);
                        }
                    }, gameProfile == null);
                });
            }
        }
    }

    public void setDisplayName(@Nullable ITextComponent displayNameIn) {
        this.displayName = displayNameIn;
    }

    @Nullable
    public ITextComponent getDisplayName() {
        return this.displayName;
    }

    public int getLastHealth() {
        return this.lastHealth;
    }

    public void setLastHealth(int p_178836_1_) {
        this.lastHealth = p_178836_1_;
    }

    public int getDisplayHealth() {
        return this.displayHealth;
    }

    public void setDisplayHealth(int p_178857_1_) {
        this.displayHealth = p_178857_1_;
    }

    public long getLastHealthTime() {
        return this.lastHealthTime;
    }

    public void setLastHealthTime(long p_178846_1_) {
        this.lastHealthTime = p_178846_1_;
    }

    public long getHealthBlinkTime() {
        return this.healthBlinkTime;
    }

    public void setHealthBlinkTime(long p_178844_1_) {
        this.healthBlinkTime = p_178844_1_;
    }

    public long getRenderVisibilityId() {
        return this.renderVisibilityId;
    }

    public void setRenderVisibilityId(long p_178843_1_) {
        this.renderVisibilityId = p_178843_1_;
    }

    public boolean isPlayerTexturesNotLoaded() {
        return !playerTexturesLoaded;
    }

    public void setPlayerTexturesLoaded(boolean playerTexturesLoaded) {
        this.playerTexturesLoaded = playerTexturesLoaded;
    }
}
