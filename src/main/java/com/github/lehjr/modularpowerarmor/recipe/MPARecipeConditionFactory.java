package com.github.lehjr.modularpowerarmor.recipe;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class MPARecipeConditionFactory implements ICondition {
    static final ResourceLocation NAME = new ResourceLocation(MPAConstants.MOD_ID, "flag");

    String conditionName;

    public MPARecipeConditionFactory(String conditionName) {
        this.conditionName = conditionName;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
        switch (conditionName) {
            // Vanilla reciples only as fallback
            case "vanilla_recipes_enabled": {
                return (MPASettings.useVanillaRecipes());
            }
        }
        return false;
    }

    public static class Serializer implements IConditionSerializer<MPARecipeConditionFactory> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, MPARecipeConditionFactory value) {
            // Don't think anything else needs to be added here, as this is now working

//            System.out.println("json: " + json.toString());
//            System.out.println("value: " + value.conditionName);
//            json.addProperty("condition", value.conditionName);
        }

        @Override
        public MPARecipeConditionFactory read(JsonObject json) {
            return new MPARecipeConditionFactory(JSONUtils.getString(json, "flag"));
        }

        @Override
        public ResourceLocation getID() {
            return MPARecipeConditionFactory.NAME;
        }
    }
}