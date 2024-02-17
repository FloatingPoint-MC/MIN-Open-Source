package cn.floatingpoint.min.system.ui.replay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.List;

public class GuiTeleport extends GuiChest {
    public GuiTeleport(IInventory upperInv, IInventory lowerInv, List<EntityPlayer> players) {
        super(upperInv, lowerInv);
        int i = 0;
        for (EntityPlayer player : players) {
            if (player.getUniqueID() == Minecraft.getMinecraft().player.getUniqueID()) continue;
            ItemStack skull = new ItemStack(Items.SKULL, 1, 3);
            NBTTagCompound tagCompound = new NBTTagCompound();
            NBTTagList list = new NBTTagList();
            list.appendTag(new NBTTagString("\247e点击传送!"));
            tagCompound.setTag("Lore", list);
            skull.setTagInfo("display", tagCompound);
            skull.setStackDisplayName("\247f" + player.getName());
            this.inventorySlots.getSlot(i).putStack(skull);
            i++;
        }
    }
}
