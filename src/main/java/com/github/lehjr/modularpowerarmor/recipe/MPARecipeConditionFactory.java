package com.github.lehjr.modularpowerarmor.recipe;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import com.google.gson.JsonObject;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class MPARecipeConditionFactory implements ICondition {
    static final ResourceLocation NAME = new ResourceLocation(MPAConstants.MOD_ID, "conditional");

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
                // Thermal Expansion
                case "thermal_expansion_recipes_enabled":
                    return CommonConfig.RECIPES_USE_THERMAL_EXPANSION.get();

                // EnderIO
                case "enderio_recipes_enabled":
                    return CommonConfig.RECIPES_USE_ENDERIO.get();

                // IC2 - Development appears to be dead
//                case "ic2_recipes_enabled":
//                    return (ModCompatibility.isIndustrialCraftExpLoaded() &&
//                            /*!ModCompatibility.isGregTechLoaded() &&*/
//                            /* !ModCompatibility.isTechRebornLoaded()) */ CommonConfig.RECIPES_USE_IC2.get());

                // IC2 Classic
                case "ic2_classic_recipes_enabled":
                    return (ModCompatibility.isIndustrialCraftClassicLoaded()&&
                            /*!ModCompatibility.isGregTechLoaded() &&*/
                            /* !ModCompatibility.isTechRebornLoaded()) */ CommonConfig.RECIPES_USE_IC2.get());
                // Vanilla reciples only as fallback
                case "vanilla_recipes_enabled":
                    return (CommonConfig.RECIPES_USE_VANILLA.get() ||
                                // or as a fallback
                                !((CommonConfig.RECIPES_USE_THERMAL_EXPANSION.get() && ModCompatibility.isThermalExpansionLoaded()) ||
                                        (CommonConfig.RECIPES_USE_ENDERIO.get() && ModCompatibility.isEnderIOLoaded()) ||
                                        (CommonConfig.RECIPES_USE_IC2.get() && ModCompatibility.isIndustrialCraftLoaded()) ||
                                        (CommonConfig.RECIPES_USE_TECH_REBORN.get() && ModCompatibility.isTechRebornLoaded())));
                        // Either enabled in config
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
            return new MPARecipeConditionFactory(JSONUtils.getString(json, "condition"));
        }

        @Override
        public ResourceLocation getID() {
            return MPARecipeConditionFactory.NAME;
        }
    }
}