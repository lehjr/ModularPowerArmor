/*
 * ModularPowersuits (Maintenance builds by lehjr)
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.client.gui.tinker.cosmetic;

import com.github.lehjr.mpalib.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableLabel;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.github.lehjr.mpalib.network.MPALibPackets;
import com.github.lehjr.mpalib.network.packets.CosmeticPresetPacket;
import com.github.machinemuse.powersuits.client.gui.clickable.ClickableItem;
import com.github.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmor;
import com.github.machinemuse.powersuits.item.tool.ItemPowerFist;
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
            if (clickie.getStack().getItem() instanceof ItemPowerArmor)
                return clickie.getStack().getItem().isValidArmor(clickie.getStack(), slot, Minecraft.getMinecraft().player);
            else if (clickie.getStack().getItem() instanceof ItemPowerFist && slot.getSlotType().equals(EntityEquipmentSlot.Type.HAND))
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
        ItemStack selectedItem = getSelectedItem().getStack();
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
        return NBTUtils.getItemTag(this.getSelectedItem().getStack());
    }


    public Rect getBorder() {
        return this.border;
    }

    @Override
    public boolean hitbox(double x, double y) {
        // change the render tag to this ... keep in mind that the render tag for these are just a key to read from the config file
        if(super.hitbox(x, y) && this.getSelectedItem() != null) {
            if (isValidItem(getSelectedItem(), getEquipmentSlot())) {
                MPALibPackets.INSTANCE.sendToServer(new CosmeticPresetPacket(this.getSelectedItem().inventorySlot, this.name));
            }
            return true;
        }
        return false;
    }
}