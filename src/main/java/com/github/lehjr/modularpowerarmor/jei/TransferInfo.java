package com.github.lehjr.modularpowerarmor.jei;

import com.github.lehjr.modularpowerarmor.container.MPSCraftingContainer;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import static mezz.jei.api.constants.VanillaRecipeCategoryUid.CRAFTING;

public class TransferInfo implements IRecipeTransferInfo<MPSCraftingContainer> {
    @Override
    public Class<MPSCraftingContainer> getContainerClass() {
        return MPSCraftingContainer.class;
    }

    @Override
    public ResourceLocation getRecipeCategoryUid() {
        return CRAFTING;
    }

    @Override
    public boolean canHandle(MPSCraftingContainer mtrmContainer) {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(MPSCraftingContainer mtrmContainer) {
        return mtrmContainer.inventorySlots.subList(1, 10);
    }

    @Override
    public List<Slot> getInventorySlots(MPSCraftingContainer mtrmContainer) {
        return mtrmContainer.inventorySlots.subList(10, mtrmContainer.inventorySlots.size() -1);
    }
}
