package cn.floatingpoint.min.system.ui.replay;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class GuiManageTick extends GuiChest {
    public GuiManageTick(IInventory upperInv, IInventory lowerInv) {
        super(upperInv, lowerInv);
        ItemStack head = new ItemStack(Items.DYE);
        head.setStackDisplayName("\247a从头开始");
        inventorySlots.getSlot(0).putStack(head);
        ItemStack back = new ItemStack(Items.SUGAR);
        back.setStackDisplayName("\247a往前10s");
        inventorySlots.getSlot(4).putStack(back);
        ItemStack go = new ItemStack(Items.GLOWSTONE_DUST);
        go.setStackDisplayName("\247a往后10s");
        inventorySlots.getSlot(8).putStack(go);
    }
}
