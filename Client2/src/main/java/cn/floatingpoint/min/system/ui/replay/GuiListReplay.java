package cn.floatingpoint.min.system.ui.replay;

import cn.floatingpoint.min.management.Managers;
import cn.floatingpoint.min.system.replay.Replay;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

public class GuiListReplay extends GuiListExtended {
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
        File dir = Managers.fileManager.getConfigFile("replay", false);
        if (!dir.exists() && !dir.mkdir()) {
            return;
        }
        for (File file : Objects.requireNonNull(dir.listFiles(file -> file.getName().toLowerCase().endsWith(".replay")))) {
            this.entries.add(new GuiListReplayEntry(this, new Replay(file.getName(), file)));
        }
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    @Nonnull
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
