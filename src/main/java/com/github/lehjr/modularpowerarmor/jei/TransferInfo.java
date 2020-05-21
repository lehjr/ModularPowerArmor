package com.github.lehjr.modularpowerarmor.jei;

import com.github.lehjr.modularpowerarmor.container.MPACraftingContainer;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import static mezz.jei.api.constants.VanillaRecipeCategoryUid.CRAFTING;

public class TransferInfo implements IRecipeTransferInfo<MPACraftingContainer> {
    @Override
    public Class<MPACraftingContainer> getContainerClass() {
        return MPACraftingContainer.class;
    }

    @Override
    public ResourceLocation getRecipeCategoryUid() {
        return CRAFTING;
    }

    @Override
    public boolean canHandle(MPACraftingContainer mtrmContainer) {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(MPACraftingContainer mtrmContainer) {
        return mtrmContainer.inventorySlots.subList(1, 10);
    }

    @Override
    public List<Slot> getInventorySlots(MPACraftingContainer mtrmContainer) {
        return mtrmContainer.inventorySlots.subList(10, mtrmContainer.inventorySlots.size() -1);
    }
}
