package net.machinemuse.powersuits.recipe;

import com.google.gson.JsonObject;
import net.machinemuse.numina.misc.ModCompatibility;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IConditionSerializer;

import java.util.function.BooleanSupplier;

public class MPSRecipeConditionFactory implements IConditionSerializer {
    ResourceLocation conditionName;

    public MPSRecipeConditionFactory(ResourceLocation conditionName) {
        this.conditionName = conditionName;
    }

    @Override
    public BooleanSupplier parse(JsonObject json) {
            switch (conditionName.toString()) {
                // Thermal Expansion
                case "powersuits:thermal_expansion_recipes_enabled":
                    return () -> CommonConfig.RECIPES_USE_THERMAL_EXPANSION.get();

                // EnderIO
                case "powersuits:enderio_recipes_enabled":
                    return () -> CommonConfig.RECIPES_USE_ENDERIO.get();

                // Original recipe loading code set priority for TechReborn recipes instead of Gregtech or Industrialcraft
                // Tech Reborn
                case "powersuits:tech_reborn_recipes_enabled":
                    return () -> CommonConfig.RECIPES_USE_TECH_REBORN.get();

                // IC2
                case "powersuits:ic2_recipes_enabled":
                    return () -> (ModCompatibility.isIndustrialCraftExpLoaded() &&
                            /*!ModCompatibility.isGregTechLoaded() &&*/
                            /* !ModCompatibility.isTechRebornLoaded()) */ CommonConfig.RECIPES_USE_IC2.get());
                // IC2 Classic
                case "powersuits:ic2_classic_recipes_enabled":
                    return () -> (ModCompatibility.isIndustrialCraftClassicLoaded()&&
                            /*!ModCompatibility.isGregTechLoaded() &&*/
                            /* !ModCompatibility.isTechRebornLoaded()) */ CommonConfig.RECIPES_USE_IC2.get());
                // Vanilla reciples only as fallback
                case "powersuits:vanilla_recipes_enabled":
                    return () -> {
                        return (CommonConfig.RECIPES_USE_VANILLA.get() ||
                                // or as a fallback
                                !((CommonConfig.RECIPES_USE_THERMAL_EXPANSION.get() && ModCompatibility.isThermalExpansionLoaded()) ||
                                        (CommonConfig.RECIPES_USE_ENDERIO.get() && ModCompatibility.isEnderIOLoaded()) ||
                                        (CommonConfig.RECIPES_USE_IC2.get() && ModCompatibility.isIndustrialCraftLoaded()) ||
                                        (CommonConfig.RECIPES_USE_TECH_REBORN.get() && ModCompatibility.isTechRebornLoaded())));
                        // Either enabled in config
                    };
            }
        return () -> false;
    }
}