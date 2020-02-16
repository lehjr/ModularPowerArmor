package com.github.lehjr.modularpowerarmor.containers;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.crafting.IRecipe;

public abstract class MPARecipeBookContainer<C> extends RecipeBookContainer {
    public MPARecipeBookContainer(ContainerType containerType, int windowId) {
        super(containerType, windowId);
    }

    @Override
    public void func_217056_a(boolean placeAll, IRecipe recipe, ServerPlayerEntity player) {
        (new MPSServerRecipePlacer(this)).place(player, recipe, placeAll);
    }
}