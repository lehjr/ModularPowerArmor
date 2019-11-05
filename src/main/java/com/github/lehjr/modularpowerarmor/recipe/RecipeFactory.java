package com.github.lehjr.modularpowerarmor.recipe;

import com.github.lehjr.mpalib.recipe.MPALibShapedRecipe;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class RecipeFactory implements IRecipeFactory {
    @Override
    public IRecipe parse(JsonContext context, JsonObject json) {
        return MPALibShapedRecipe.deserialize(context, json);
    }
}