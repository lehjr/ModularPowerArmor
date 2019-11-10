package com.github.lehjr.modularpowerarmor.client.gui.tinker.cosmetic;

import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmor;
import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.network.packets.CosmeticPresetPacket;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableItem;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableLabel;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CosmeticPresetSelectionSubframe extends ScrollableLabel {
    public RelativeRect border;
    public boolean open;
    public ItemSelectionFrame itemSelector;
    String name;
    Minecraft minecraft;

    public CosmeticPresetSelectionSubframe(String name, Point2D Point2D, ItemSelectionFrame itemSelector, RelativeRect border) {
        super(new ClickableLabel(name, Point2D), border);
        this.name = name;
        this.itemSelector = itemSelector;
        this.border = border;
        this.open = true;
        this.setMode(0);
        minecraft = Minecraft.getMinecraft();
    }

    public boolean isValidItem(ClickableItem clickie, EntityEquipmentSlot slot) {
        if (clickie != null) {
            if (clickie.getStack().getItem() instanceof ItemArmor)
                return clickie.getStack().getItem().isValidArmor(clickie.getStack(), slot, minecraft.player);
            else if (clickie.getStack().getItem() instanceof ItemPowerFist && slot.getSlotType().equals(EntityEquipmentSlot.Type.HAND))
                return true;
        }
        return false;
    }

    public ClickableItem getSelectedItem() {
        return this.itemSelector.getSelectedItem();
    }

    /**
     * Get's the equipment itemSlot the item is for.
     */
    public EntityEquipmentSlot getEquipmentSlot() {
        ItemStack selectedItem = getSelectedItem().getStack();
        if (!selectedItem.isEmpty() && selectedItem.getItem() instanceof ItemPowerArmor) {
            return selectedItem.getItem().getEquipmentSlot(selectedItem);
        }
        EntityPlayer player = minecraft.player;
        ItemStack heldItem = player.getHeldItemOffhand();

        if (!heldItem.isEmpty() && ItemStack.areItemStacksEqual(selectedItem, heldItem)) {
            return EntityEquipmentSlot.OFFHAND;
        }
        return EntityEquipmentSlot.MAINHAND;
    }

    public String getName() {
        return name;
    }

    public NBTTagCompound getItemTag() {
        return NBTUtils.getMuseItemTag(this.getSelectedItem().getStack());
    }

    public Rect getBorder() {
        return this.border;
    }

    @Override
    public boolean hitbox(double x, double y) {
        // change the render tag to this ... keep in mind that the render tag for these are just a key to read from the config file
        if(super.hitbox(x, y) && this.getSelectedItem() != null) {
            if (isValidItem(getSelectedItem(), getEquipmentSlot())) {
                MPAPackets.sendToServer(new CosmeticPresetPacket(this.getSelectedItem().inventorySlot, this.name));
            }
            return true;
        }
        return false;
    }
}