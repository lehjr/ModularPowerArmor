package com.github.lehjr.modularpowerarmor.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.mpalib.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.config.MPALibSettings;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MPASettings {
    public static final ClientConfig CLIENT_CONFIG;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static File clientFile;

    public static final CommonConfig COMMON_CONFIG;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        // Client
        {
            final Pair<ClientConfig, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
            CLIENT_SPEC = clientSpecPair.getRight();
            CLIENT_CONFIG = clientSpecPair.getLeft();
            clientFile = MPALibSettings.setupConfigFile("modularpowerarmor-client-only.toml", MPAConstants.MOD_ID);

            final CommentedFileConfig configData = CommentedFileConfig.builder(clientFile)
                    .sync()
                    .autosave()
                    .writingMode(WritingMode.REPLACE)
                    .build();

            configData.load();
            CLIENT_SPEC.setConfig(configData);
        }
        // Common
        {
            final Pair<CommonConfig, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
            COMMON_SPEC = commonSpecPair.getRight();
            COMMON_CONFIG = commonSpecPair.getLeft();
        }
    }

    /** Client ------------------------------------------------------------------------------------ */
    // HUD ---------------------------------------------------------------------------------------
    public static boolean useGraphicalMeters() {
        return CLIENT_CONFIG != null ? CLIENT_CONFIG.HUD_USE_GRAPHICAL_METERS.get() : false;
    }

    public static boolean displayHud() {
        return CLIENT_CONFIG != null ? CLIENT_CONFIG.HUD_DISPLAY_HUD.get() : false;
    }

    public static boolean use24HourClock() {
        return CLIENT_CONFIG != null ? CLIENT_CONFIG.HUD_USE_24_HOUR_CLOCK.get() : false;
    }

    public static float getHudKeybindX() {
        return CLIENT_CONFIG != null ? toFloat(CLIENT_CONFIG.HUD_KEYBIND_X.get()) : 8.0F;
    }

    public static float getHudKeybindY() {
        return CLIENT_CONFIG != null ? toFloat(CLIENT_CONFIG.HUD_KEYBIND_Y.get()) : 32.0F;
    }

    // General -----------------------------------------------------------------------------------
    public static boolean allowConfictingKeyBinds() {
        return CLIENT_CONFIG != null ? CLIENT_CONFIG.GENERAL_ALLOW_CONFLICTING_KEYBINDS.get() : true;
    }

    /** Common/Server ----------------------------------------------------------------------------- */
    @Nullable
    static CommonConfig getCommonConfig() {
        return COMMON_CONFIG;
    }

    // General ------------------------------------------------------------------------------------
    public static double getMaxFlyingSpeed() {
        return COMMON_CONFIG != null ? COMMON_CONFIG.GENERAL_MAX_FLYING_SPEED.get() : 25.0;
    }

    public static double getMaxHeatPowerFist() {
        return COMMON_CONFIG != null ? COMMON_CONFIG.GENERAL_BASE_MAX_HEAT_POWERFIST.get() : 5.0D;
    }

    public static double getMaxHeatHelmet() {
        return COMMON_CONFIG != null ? COMMON_CONFIG.GENERAL_BASE_MAX_HEAT_HELMET.get() : 5.0D;
    }

    public static double getMaxHeatChestplate() {
        return COMMON_CONFIG != null ? COMMON_CONFIG.GENERAL_BASE_MAX_HEAT_CHEST.get() : 20.0D;
    }

    public static double getMaxHeatLegs() {
        return COMMON_CONFIG != null ? COMMON_CONFIG.GENERAL_BASE_MAX_HEAT_LEGS.get() : 15.0D;
    }

    public static double getMaxHeatBoots() {
        return COMMON_CONFIG != null ? COMMON_CONFIG.GENERAL_BASE_MAX_HEAT_FEET.get() : 15.0D;
    }

    public static List<ResourceLocation> getOreList() {
        List<String> ores = COMMON_CONFIG != null ?
                (List<String>) COMMON_CONFIG.GENERAL_VEIN_MINER_ORE_LIST.get() : new ArrayList<>();
        List<ResourceLocation> retList = new ArrayList<>();
        ores.forEach(ore-> {
            retList.add(new ResourceLocation(ore));;
        });
        return retList;
    }

    public static List<ResourceLocation> getBlockList() {
        List<String> blocks = COMMON_CONFIG != null ?
                (List<String>) COMMON_CONFIG.GENERAL_VEIN_MINER_BLOCK_LIST.get() : new ArrayList<>();
        List<ResourceLocation> retList = new ArrayList<>();
        blocks.forEach(block-> {
            retList.add(new ResourceLocation(block));;
        });
        return retList;
    }

    // Cosmetic -----------------------------------------------------------------------------------
    public static boolean useLegacyCosmeticSystem() {
        return COMMON_CONFIG != null ? COMMON_CONFIG.COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get() : false;
    }

    public static boolean allowHighPollyArmor() {
        return COMMON_CONFIG != null ? COMMON_CONFIG.COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS.get() : true;
    }

    public static boolean allowPowerFistCustomization() {
        return COMMON_CONFIG != null ? COMMON_CONFIG.COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN.get() : true;
    }

    // Recipes ------------------------------------------------------------------------------------
    public static boolean useVanillaRecipes() {
        return COMMON_CONFIG != null ? COMMON_CONFIG.RECIPES_USE_VANILLA.get() : false;
    }

    // Modules ------------------------------------------------------------------------------------
    static ModuleConfig moduleConfig = null;
    public static void setModConfig(ModuleConfig modConfigIn) {
        moduleConfig = modConfigIn;
    }

    public static Callable<IConfig> getModuleConfig() {
        if (moduleConfig == null) {
            moduleConfig = new ModuleConfig(null);
        }
        return new Callable<IConfig>() {
            @Override
            public IConfig call() throws Exception {
                return (IConfig) moduleConfig;
            }
        };
    }

    static float toFloat(double val) {
        return (float)val;
    }
}