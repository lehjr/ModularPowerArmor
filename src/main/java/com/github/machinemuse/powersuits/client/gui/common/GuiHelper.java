package com.github.machinemuse.powersuits.client.gui.common;

import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.client.render.modelspec.EnumSpecType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.Optional;

/**
 * @author lehjr
 */
public class GuiHelper {
    public static boolean isValidItem(ItemStack itemStack) {
        EntityEquipmentSlot slot = EntityMob.getSlotForItemStack(itemStack);
        return Optional.ofNullable(itemStack.getCapability(ModelSpecNBTCapability.RENDER, null)).map(iModelSpecNBT -> {
            EnumSpecType specType = iModelSpecNBT.getSpecType();

            if (iModelSpecNBT.getSpecType().equals(EnumSpecType.HANDHELD) && slot.getSlotType().equals(EntityEquipmentSlot.Type.HAND)) {
                return true;
            }
            if (specType.equals(EnumSpecType.ARMOR_MODEL) || specType.equals(EnumSpecType.ARMOR_MODEL)
                    && slot.getSlotType().equals(EntityEquipmentSlot.Type.ARMOR)) {
                return true;
            }
            return false;
        }).orElse(false);
    }
}