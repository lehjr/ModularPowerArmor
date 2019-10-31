package net.machinemuse.powersuits.client.gui.tinker.cosmetic;

import com.github.lehjr.mpalib.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableLabel;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.machinemuse.powersuits.client.gui.clickable.ClickableItem;
import net.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import net.machinemuse.powersuits.item.armor.ItemPowerArmor;
import net.machinemuse.powersuits.item.tool.ItemPowerFist;
import net.machinemuse.powersuits.network.MPSPackets;
import net.machinemuse.powersuits.network.packets.MusePacketCosmeticPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public class CosmeticPresetSelectionSubframe extends ScrollableLabel {
    public RelativeRect border;
    public boolean open;
    public ItemSelectionFrame itemSelector;
    String name;
    public CosmeticPresetSelectionSubframe(String name, Point2D Point2D, ItemSelectionFrame itemSelector, RelativeRect border) {
        super(new ClickableLabel(name, Point2D), border);
        this.name = name;
        this.itemSelector = itemSelector;
        this.border = border;
        this.open = true;
        this.setMode(0);
    }

    public boolean isValidItem(ClickableItem clickie, EntityEquipmentSlot slot) {
        if (clickie != null) {
            if (clickie.getItem().getItem() instanceof ItemPowerArmor)
                return clickie.getItem().getItem().isValidArmor(clickie.getItem(), slot, Minecraft.getMinecraft().player);
            else if (clickie.getItem().getItem() instanceof ItemPowerFist && slot.getSlotType().equals(EntityEquipmentSlot.Type.HAND))
                return true;
        }
        return false;
    }

    public ClickableItem getSelectedItem() {
        return this.itemSelector.getSelectedItem();
    }

    /**
     * Get's the equipment slot the item is for.
     */
    EntityEquipmentSlot getEquipmentSlot() {
        ItemStack selectedItem = getSelectedItem().getItem();
        if (selectedItem != null && selectedItem.getItem() instanceof ItemPowerArmor) {
            return ((ItemPowerArmor) selectedItem.getItem()).armorType;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        ItemStack heldItem = player.getHeldItemOffhand();

        if (!heldItem.isEmpty() && Objects.equals(selectedItem, heldItem) /*ItemUtils.stackEqualExact(selectedItem, heldItem)*/) {
            return EntityEquipmentSlot.OFFHAND;
        }
        return EntityEquipmentSlot.MAINHAND;
    }

    public String getName() {
        return name;
    }

    public NBTTagCompound getItemTag() {
        return NBTUtils.getMuseItemTag(this.getSelectedItem().getItem());
    }


    public Rect getBorder() {
        return this.border;
    }

    @Override
    public boolean hitbox(double x, double y) {
        // change the render tag to this ... keep in mind that the render tag for these are just a key to read from the config file
        if(super.hitbox(x, y) && this.getSelectedItem() != null) {
            if (isValidItem(getSelectedItem(), getEquipmentSlot())) {
                MPSPackets.sendToServer(new MusePacketCosmeticPreset(Minecraft.getMinecraft().player, this.getSelectedItem().inventorySlot, this.name));
            }
            return true;
        }
        return false;
    }
}