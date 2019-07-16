package net.machinemuse.powersuits.containers;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.crafting.IRecipe;

public abstract class MPSRecipeBookContainer<C> extends RecipeBookContainer {
    public MPSRecipeBookContainer(ContainerType containerType, int p_i50067_2_) {
        super(containerType, p_i50067_2_);
    }

    @Override
    public void func_217056_a(boolean p_217056_1_, IRecipe recipe, ServerPlayerEntity player) {
            (new MPSServerRecipePlacer(this)).place(player, recipe, p_217056_1_);
    }

    @Override
    public boolean matches(IRecipe recipeIn) {
        return false;
    }
}
