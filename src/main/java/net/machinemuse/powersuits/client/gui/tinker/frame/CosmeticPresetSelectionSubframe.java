package net.machinemuse.powersuits.client.gui.tinker.frame;

import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.machinemuse.numina.client.gui.clickable.ClickableLabel;
import net.machinemuse.numina.client.gui.scrollable.ScrollableLabel;
import net.machinemuse.numina.item.MuseItemUtils;
import net.machinemuse.numina.math.geometry.MusePoint2D;
import net.machinemuse.numina.math.geometry.MuseRect;
import net.machinemuse.numina.math.geometry.MuseRelativeRect;
import net.machinemuse.numina.nbt.MuseNBTUtils;
import net.machinemuse.powersuits.item.armor.ItemPowerArmor;
import net.machinemuse.powersuits.item.tool.ItemPowerFist;
import net.machinemuse.powersuits.network.MPSPackets;
import net.machinemuse.powersuits.network.packets.MusePacketCosmeticPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class CosmeticPresetSelectionSubframe extends ScrollableLabel {
    public MuseRelativeRect border;
    public boolean open;
    public ItemSelectionFrame itemSelector;
    String name;
    Minecraft minecraft;

    public CosmeticPresetSelectionSubframe(String name, MusePoint2D musePoint2D, ItemSelectionFrame itemSelector, MuseRelativeRect border) {
        super(new ClickableLabel(name, musePoint2D), border);
        this.name = name;
        this.itemSelector = itemSelector;
        this.border = border;
        this.open = true;
        this.setMode(0);
        minecraft = Minecraft.getInstance();
    }

    public boolean isValidItem(ClickableItem clickie, EquipmentSlotType slot) {
        if (clickie != null) {
            if (clickie.getItem().getItem() instanceof ItemPowerArmor)
                return clickie.getItem().getItem().canEquip(clickie.getItem(), slot, minecraft.player);
            else if (clickie.getItem().getItem() instanceof ItemPowerFist && slot.getSlotType().equals(EquipmentSlotType.Group.HAND))
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
    public EquipmentSlotType getEquipmentSlot() {
        ItemStack selectedItem = getSelectedItem().getItem();
        if (!selectedItem.isEmpty() && selectedItem.getItem() instanceof ItemPowerArmor)
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
        return MuseNBTUtils.getMuseItemTag(this.getSelectedItem().getItem());
    }


    public MuseRect getBorder() {
        return this.border;
    }

    @Override
    public boolean hitbox(double x, double y) {
        // change the render tag to this ... keep in mind that the render tag for these are just a key to read from the config file
        if(super.hitbox(x, y) && this.getSelectedItem() != null) {
            if (isValidItem(getSelectedItem(), getEquipmentSlot())) {
                MPSPackets.sendToServer(new MusePacketCosmeticPreset(minecraft.player.getEntityId(), this.getSelectedItem().inventorySlot, this.name));
            }
            return true;
        }
        return false;
    }
}