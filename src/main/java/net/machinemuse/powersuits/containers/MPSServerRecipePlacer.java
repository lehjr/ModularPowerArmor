package net.machinemuse.powersuits.containers;

import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.crafting.ServerRecipePlacer;

public class MPSServerRecipePlacer extends ServerRecipePlacer {
    public MPSServerRecipePlacer(RecipeBookContainer recipeBookContainer) {
        super(recipeBookContainer);
    }

    @Override
    protected void clear() {
        for(int index = 0; index < this.recipeBookContainer.getWidth() * this.recipeBookContainer.getHeight() + 1; ++index) {
            if (index != this.recipeBookContainer.getOutputSlot() || !(this.recipeBookContainer instanceof MPSCraftingContainer) && !(this.recipeBookContainer instanceof PlayerContainer)) {
                this.giveToPlayer(index);
            }
        }
        this.recipeBookContainer.clear();
    }
}