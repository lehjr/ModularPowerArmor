package com.github.lehjr.modularpowerarmor.recipe;

import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

@SuppressWarnings("unused")
public class RecipeConditionFactory implements IConditionFactory {
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        if (JsonUtils.hasField(json, "type")) {
            String key = JsonUtils.getString(json, "type");
            switch (key) {
                // Thermal Expansion
                case "modularpowerarmor:thermal_expansion_recipes_enabled":
                    return () -> MPAConfig.INSTANCE.useThermalExpansionRecipes();

                // EnderIO
                case "modularpowerarmor:enderio_recipes_enabled":
                    return () -> MPAConfig.INSTANCE.useEnderIORecipes();

                // Original recipe loading code set priority for TechReborn recipes instead of Gregtech or Industrialcraft
                // Tech Reborn
                case "modularpowerarmor:tech_reborn_recipes_enabled":
                    return () -> MPAConfig.INSTANCE.useTechRebornRecipes();

                // IC2
                case "modularpowerarmor:ic2_recipes_enabled":
                    return () -> (ModCompatibility.isIndustrialCraftExpLoaded() &&
                            /*!ModCompatibility.isGregTechLoaded() &&*/
                                    /* !ModCompatibility.isTechRebornLoaded()) */ MPAConfig.INSTANCE.useIC2Recipes());
                // IC2 Classic
                case "modularpowerarmor:ic2_classic_recipes_enabled":
                    return () -> (ModCompatibility.isIndustrialCraftClassicLoaded()&&
                            /*!ModCompatibility.isGregTechLoaded() &&*/
                            /* !ModCompatibility.isTechRebornLoaded()) */ MPAConfig.INSTANCE.useIC2Recipes());
                // Vanilla reciples only as fallback
                case "modularpowerarmor:vanilla_recipes_enabled":
                    return () -> (!(
                                    MPAConfig.INSTANCE.useThermalExpansionRecipes() ||
                                    MPAConfig.INSTANCE.useEnderIORecipes() ||
                                    (MPAConfig.INSTANCE.useIC2Recipes() && ModCompatibility.isIndustrialCraftLoaded()) ||
                                    MPAConfig.INSTANCE.useTechRebornRecipes()
                    ));
            }
        }
        return () -> false;
    }
}
