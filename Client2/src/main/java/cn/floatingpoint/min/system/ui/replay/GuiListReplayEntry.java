package cn.floatingpoint.min.system.ui.replay;

import java.io.File;
import java.util.UUID;

import cn.floatingpoint.min.system.replay.Replay;
import cn.floatingpoint.min.system.replay.server.ReplayServer;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public class GuiListReplayEntry implements GuiListExtended.IGuiListEntry {
    private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
    private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
    private final Minecraft mc;
    private final Replay replay;
    private final GuiManageReplay worldSelScreen;
    private final GuiListReplay containingListSel;
    private long lastClickTime;

    public GuiListReplayEntry(GuiListReplay listWorldSelIn, Replay replay) {
        this.containingListSel = listWorldSelIn;
        this.worldSelScreen = listWorldSelIn.getGuiReplaySelection();
        mc = Minecraft.getMinecraft();
        this.replay = replay;
    }

    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
        String s = this.replay.getName();

        if (StringUtils.isEmpty(s)) {
            s = "Replay " + (slotIndex + 1);
        }

        mc.fontRenderer.drawString(s, x + 32 + 3, y + 1, 16777215);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(ICON_MISSING);
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();

        if (mc.gameSettings.touchscreen || isSelected) {
            mc.getTextureManager().bindTexture(ICON_OVERLAY_LOCATION);
            Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int j = mouseX - x;
            int i = j < 32 ? 32 : 0;
            Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, (float) i, 32, 32, 256.0F, 256.0F);
        }
    }

    /**
     * Called when the mouse is clicked within this entry. Returning true means that something within this entry was
     * clicked and the list should not be dragged.
     */
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        this.containingListSel.selectWorld(slotIndex);

        if (relativeX < 32) {
            this.joinWorld();
            return true;
        } else if (Minecraft.getSystemTime() - this.lastClickTime < 250L) {
            this.joinWorld();
            return true;
        } else {
            this.lastClickTime = Minecraft.getSystemTime();
            return false;
        }
    }

    public void joinWorld() {
        this.loadWorld();
    }

    public void deleteWorld() {
        mc.displayGuiScreen(new GuiYesNo((result, id) -> {
            if (result) {
                mc.displayGuiScreen(new GuiScreenWorking());
                replay.delete();
                GuiListReplayEntry.this.containingListSel.refreshList();
            }

            mc.displayGuiScreen(GuiListReplayEntry.this.worldSelScreen);
        }, I18n.format("selectWorld.deleteQuestion"), "'" + this.replay.getName() + "' " + I18n.format("selectWorld.deleteWarning"), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel"), 0));
    }

    private void loadWorld() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        mc.displayGuiScreen(new GuiLoadingReplay());

        // Launch Server
        YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(mc.getProxy(), UUID.randomUUID().toString());
        MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
        GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
        PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(mc.gameDir, MinecraftServer.USER_CACHE_FILE.getName()));
        TileEntitySkull.setProfileCache(playerprofilecache);
        TileEntitySkull.setSessionService(minecraftsessionservice);
        PlayerProfileCache.setOnlineMode(false);
        new ReplayServer(replay);
    }

    /**
     * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
     */
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
    }

    public void updatePosition(int slotIndex, int x, int y, float partialTicks) {
    }
}
