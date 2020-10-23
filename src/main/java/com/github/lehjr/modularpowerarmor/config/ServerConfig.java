package com.github.lehjr.modularpowerarmor.config;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    protected ForgeConfigSpec.BooleanValue RECIPES_USE_VANILLA;





    protected ServerConfig(ForgeConfigSpec.Builder builder) {

        /** Recipes ----------------------------------------------------------------------------------------------- */
        builder.comment("Recipe settings").push("Recipes");
        RECIPES_USE_VANILLA = builder
                .comment("Use recipes for Vanilla")
                .translation(MPAConstants.CONFIG_RECIPES_USE_VANILLA)
                .worldRestart()
                .define("useVanillaRecipes", true);
        builder.pop();
    }
}
