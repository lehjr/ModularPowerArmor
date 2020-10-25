package com.github.lehjr.modularpowerarmor.client.gui.modding.cosmetic;

import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.modularpowerarmor.client.gui.obsolete.ScrollableLabel;
import com.github.lehjr.modularpowerarmor.item.armor.AbstractElectricItemArmor;
import com.github.lehjr.mpalib.util.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.util.capabilities.render.modelspec.EnumSpecType;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableItem;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.util.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.util.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.util.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.util.nbt.NBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class CosmeticPresetSelectionSubframe extends ScrollableLabel {
    public RelativeRect border;
    public boolean open;
    public ItemSelectionFrame itemSelector;
    String name;
    Minecraft minecraft;

    public CosmeticPresetSelectionSubframe(String name, Point2D point, ItemSelectionFrame itemSelector, RelativeRect border) {
        super(new ClickableLabel(name, point), border);
        this.name = name;
        this.itemSelector = itemSelector;
        this.border = border;
        this.open = true;
        this.setMode(ClickableLabel.JustifyMode.LEFT);
        minecraft = Minecraft.getInstance();
    }

    public boolean isValidItem(ClickableItem clickie, EquipmentSlotType slot) {
        if (clickie != null) {
            clickie.getStack().getCapability(ModelSpecNBTCapability.RENDER).map(iModelSpecNBT -> {
                EnumSpecType specType = iModelSpecNBT.getSpecType();

                if (iModelSpecNBT.getSpecType().equals(EnumSpecType.HANDHELD) && slot.getSlotType().equals(EquipmentSlotType.Group.HAND)) {
                    return true;
                }
                if (specType.equals(EnumSpecType.ARMOR_MODEL) || specType.equals(EnumSpecType.ARMOR_MODEL)
                        && slot.getSlotType().equals(EquipmentSlotType.Group.ARMOR)) {
                    return true;
                }
                return false;
            }).orElse(false);
        }
        return false;
    }

    public ClickableItem getSelectedItem() {
        return this.itemSelector.getSelectedItem();
    }

    /**
     * Get's the equipment itemSlot the item is for.
     */
    public EquipmentSlotType getEquipmentSlot() {
        ItemStack selectedItem = getSelectedItem().getStack();
        if (!selectedItem.isEmpty() && selectedItem.getItem() instanceof AbstractElectricItemArmor)
            return selectedItem.getEquipmentSlot();
        PlayerEntity player = minecraft.player;
        ItemStack heldItem = player.getHeldItemOffhand();

        if (!heldItem.isEmpty() && ItemStack.areItemStacksEqual(selectedItem, heldItem))
            return EquipmentSlotType.OFFHAND;
        return EquipmentSlotType.MAINHAND;
    }

    public String getName() {
        return name;
    }

    public CompoundNBT getItemTag() {
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
//                MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketCosmeticPreset(this.getSelectedItem().getSlotIndex(), this.name));
            }
            return true;
        }
        return false;
    }
}