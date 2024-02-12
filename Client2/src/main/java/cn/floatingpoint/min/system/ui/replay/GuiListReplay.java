package cn.floatingpoint.min.system.ui.replay;

import cn.floatingpoint.min.system.replay.storage.SaveConverter;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiListReplay extends GuiListExtended {
    private static final Logger LOGGER = LogManager.getLogger();
    private final GuiManageReplay guiManageReplay;
    private final List<GuiListReplayEntry> entries = Lists.newArrayList();

    /**
     * Index to the currently selected world
     */
    private int selectedIdx = -1;

    public GuiListReplay(GuiManageReplay parentScreen, Minecraft clientIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(clientIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.guiManageReplay = parentScreen;
        this.refreshList();
    }

    public void refreshList() {
        ISaveFormat isaveformat = new SaveConverter(new File(mc.gameDir, "MIN2/replay"), mc.getDataFixer());
        List<WorldSummary> list;

        try {
            list = isaveformat.getSaveList();
        } catch (AnvilConverterException anvilconverterexception) {
            LOGGER.error("Couldn't load level list", anvilconverterexception);
            this.mc.displayGuiScreen(new GuiErrorScreen(I18n.format("selectWorld.unable_to_load"), anvilconverterexception.getMessage()));
            return;
        }

        Collections.sort(list);

        for (WorldSummary worldsummary : list) {
            this.entries.add(new GuiListReplayEntry(this, worldsummary, this.mc.getSaveLoader()));
        }
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListReplayEntry getListEntry(int index) {
        return this.entries.get(index);
    }

    protected int getSize() {
        return this.entries.size();
    }

    protected int getScrollBarX() {
        return super.getScrollBarX() + 20;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth() {
        return super.getListWidth() + 50;
    }

    public void selectWorld(int idx) {
        this.selectedIdx = idx;
        this.guiManageReplay.selectReplay(this.getSelectedReplay());
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex) {
        return slotIndex == this.selectedIdx;
    }

    @Nullable
    public GuiListReplayEntry getSelectedReplay() {
        return this.selectedIdx >= 0 && this.selectedIdx < this.getSize() ? this.getListEntry(this.selectedIdx) : null;
    }

    public GuiManageReplay getGuiReplaySelection() {
        return this.guiManageReplay;
    }
}
