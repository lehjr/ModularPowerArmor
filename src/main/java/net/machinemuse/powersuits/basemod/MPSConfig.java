package net.machinemuse.powersuits.basemod;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.machinemuse.numina.capabilities.IConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public enum MPSConfig implements IConfig {
    INSTANCE;
    static MPSItems mpsi = MPSItems.INSTANCE;

    public static final ClientConfig CLIENT_CONFIG;
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ServerConfig SERVER_CONFIG;
    public static final ForgeConfigSpec SERVER_SPEC;

    static File clientFile;
    static File serverFile;
    static File configFolder = null;

    @Nullable
    public static File getConfigFolder() {
        return configFolder;
    }

    static {
        final Pair<ClientConfig, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT_CONFIG = clientSpecPair.getLeft();
        clientFile = setupConfigFile("powersuits-client-only.toml");
        CLIENT_SPEC.setConfig(CommentedFileConfig.of(clientFile));

        final Pair<ServerConfig, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = serverSpecPair.getRight();
        SERVER_CONFIG = serverSpecPair.getLeft();
        serverFile = setupConfigFile("powersuits-common.toml");
        SERVER_SPEC.setConfig(CommentedFileConfig.of(serverFile));
    }

    static File setupConfigFile(String fileName) {
        Path configFile = Paths.get("config/machinemuse").resolve(MPSConstants.MODID).resolve(fileName);
        File cfgFile = configFile.toFile();
        try {
            if (!cfgFile.getParentFile().exists())
                cfgFile.getParentFile().mkdirs();
            if (!cfgFile.exists())
                cfgFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (configFolder == null)
            configFolder = cfgFile.getParentFile();

        return cfgFile;
    }

    /**
     * [ CLIENT-ONLY ] -------------------------------------------------------------------------------------------
     */
    // HUD ----------------------------------------------------------------------------------------
    public static ForgeConfigSpec.BooleanValue
            HUD_USE_GRAPHICAL_METERS,
            HUD_TOGGLE_MODULE_SPAM,
            HUD_DISPLAY_HUD,
            HUD_USE_24_HOUR_CLOCK;

    // General ------------------------------------------------------------------------------------
    public static ForgeConfigSpec.BooleanValue
            GENERAL_ALLOW_CONFLICTING_KEYBINDS;

    public static ForgeConfigSpec.DoubleValue
            HUD_KEYBIND_HUD_X,
            HUD_KEYBIND_HUD_Y;

    /**
     * Client only settings
     */
    public static class ClientConfig {
        ClientConfig(ForgeConfigSpec.Builder builder) {
            // HUD ------------------------------------------------------------------------------------
            builder.comment("HUD settings").push("HUD");
            HUD_USE_GRAPHICAL_METERS = builder
                    .comment("Use Graphical Meters")
                    .translation(MPSConstants.CONFIG_HUD_USE_GRAPHICAL_METERS)
                    .define("useGraphicalMeters", true);

            HUD_TOGGLE_MODULE_SPAM = builder
                    .comment("Chat message when toggling module")
                    .translation(MPSConstants.CONFIG_HUD_TOGGLE_MODULE_SPAM)
                    .define("toggleModuleSpam", false);

            HUD_DISPLAY_HUD = builder
                    .comment("Display HUD")
                    .translation(MPSConstants.CONFIG_HUD_DISPLAY_HUD)
                    .define("keybind_HUD_on", true);

            HUD_KEYBIND_HUD_X = builder
                    .comment("x position")
                    .translation(MPSConstants.CONFIG_HUD_KEYBIND_HUD_X)
                    .defineInRange("keybindHUDx", 8.0, 0, Double.MAX_VALUE);

            HUD_KEYBIND_HUD_Y = builder
                    .comment("x position")
                    .translation(MPSConstants.CONFIG_HUD_KEYBIND_HUD_Y)
                    .defineInRange("keybindHUDy", 32.0, 0, Double.MAX_VALUE);

            HUD_USE_24_HOUR_CLOCK = builder
                    .comment("Use a 24h clock instead of 12h")
                    .translation(MPSConstants.CONFIG_HUD_USE_24_HOUR_CLOCK)
                    .define("use24hClock", false);
            builder.pop();

            builder.comment("General settings").push("General");
            GENERAL_ALLOW_CONFLICTING_KEYBINDS = builder
                    .comment("Allow Conflicting Keybinds")
                    .translation(MPSConstants.CONFIG_GENERAL_ALLOW_CONFLICTING_KEYBINDS)
                    .define("allowConflictingKeybinds", true);

            builder.build();
        }
    }

    /**
     * [ SEVER/CLIENT ]-------------------------------------------------------------------------------------------
     */
    // Cosmetics -------------------------------------------------------------------------------------------------
//           Note: these are controlled by the server because the legacy settings can create a vast number
//      of NBT Tags for tracking the settings for each individual model part.
    public static ForgeConfigSpec.BooleanValue
            COSMETIC_USE_LEGACY_COSMETIC_SYSTEM,
            COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS,
            COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN;

    public static ForgeConfigSpec.DoubleValue
            GENERAL_MAX_FLYING_SPEED,
            GENERAL_BASE_MAX_HEAT_POWERFIST,
            GENERAL_BASE_MAX_HEAT_HELMET,
            GENERAL_BASE_MAX_HEAT_CHEST,
            GENERAL_BASE_MAX_HEAT_LEGS,
            GENERAL_BASE_MAX_HEAT_FEET;

    static Map<String, ForgeConfigSpec.IntValue> intValueMap = new HashMap<>();
    static Map<String, ForgeConfigSpec.DoubleValue> doubleValueMap = new HashMap<>();
    static Map<String, ForgeConfigSpec.BooleanValue> allowedModules = new HashMap<>();

    public static class ModuleConfig {
        String translationPrefix;
        ForgeConfigSpec.Builder builder;

        public ModuleConfig(String regNameIn, boolean isAllowed, ForgeConfigSpec.Builder builderIn) {
            this(new ResourceLocation(regNameIn), isAllowed, builderIn);
        }

        public ModuleConfig(ResourceLocation regNameIn, boolean isAllowed, ForgeConfigSpec.Builder builderIn) {
            this.translationPrefix = Util.makeTranslationKey("item", regNameIn);

            System.out.println("translation prefix: " + translationPrefix);

            this.builder = builderIn;
            builder.push("Module Settings");
            builder.translation(translationPrefix);
            setIsModuleAllowed(isAllowed);
        }

        void setIsModuleAllowed(boolean value) {
            ForgeConfigSpec.BooleanValue allowed = builder
                    .comment("Is Module Allowed")
                    .translation(MPSConstants.IS_MODULE_ALLOWED)
                    .define("isAllowed", value);
//            allowedModules.put(translationPrefix, allowed);
        }

        public void addInt(String name, String translationKey, String comment, int defaultVal, Integer min, Integer max) {
            ForgeConfigSpec.IntValue intConfigVal =
                    builder.comment(comment)
                            .translation(translationKey)
                            .defineInRange(name, defaultVal, min != null ? min : 0, max != null ? max : Integer.MAX_VALUE);
            intValueMap.put(translationKey, intConfigVal);
        }

        public void addDouble(String name, String translationKey, String comment, double defaultVal, Double min, Double max) {
            ForgeConfigSpec.DoubleValue doubleValue =
                    builder.comment(comment)
                            .translation(translationKey)
                            .defineInRange(name, defaultVal, min != null ? min : 0, max != null ? max : Double.MAX_VALUE);
            doubleValueMap.put(translationKey, doubleValue);
        }

        public void done() {
            builder.pop();
        }
    }

    /**
     * Settings that are controlled by the server and synced to client
     */
    public static class ServerConfig {

        public ServerConfig(ForgeConfigSpec.Builder builder) {
            // Cosmetics ----------------------------------------------------------------------------------------------
            builder.comment("Model cosmetic settings").push("Cosmetic");

            COSMETIC_USE_LEGACY_COSMETIC_SYSTEM = builder
                    .comment("Use legacy cosmetic configuration instead of cosmetic presets")
                    .translation(MPSConstants.CONFIG_COSMETIC_USE_LEGACY_COSMETIC_SYSTEM)
                    .worldRestart()
                    .define("useLegacyCosmeticSystem", true);

            COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS = builder
                    .comment("Allow high polly armor models instead of just skins")
                    .translation(MPSConstants.CONFIG_COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS)
                    .define("allowHighPollyArmorModuels", true);

            COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN = builder
                    .comment("Allow PowerFist model to be customized")
                    .translation(MPSConstants.CONFIG_COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN)
                    .define("allowPowerFistCustomization", true);
            builder.pop();

            // General ------------------------------------------------------------------------------------------------
            builder.comment("General settings").push("General");
            GENERAL_MAX_FLYING_SPEED = builder.comment("Maximum flight speed (in m/s)")
                    .translation(MPSConstants.CONFIG_GENERAL_MAX_FLYING_SPEED)
                    .defineInRange("maximumFlyingSpeedmps", 25.0, 0, Double.MAX_VALUE);
            builder.pop();

            GENERAL_BASE_MAX_HEAT_POWERFIST = builder.comment("PowerFist Base Heat Cap")
                    .translation(MPSConstants.CONFIG_GENERAL_BASE_MAX_MODULES_POWERFIST)
                    .defineInRange("baseMaxHeatPowerFist", 5.0, 0, 5000);

            GENERAL_BASE_MAX_HEAT_HELMET = builder.comment("Power Armor Helmet Heat Cap")
                    .translation(MPSConstants.CONFIG_GENERAL_BASE_MAX_MODULES_HELMET)
                    .defineInRange("baseMaxHeatHelmet", 5.0, 0, 5000);

            GENERAL_BASE_MAX_HEAT_CHEST = builder.comment("Power Armor Chestplate Heat Cap")
                    .translation(MPSConstants.CONFIG_GENERAL_BASE_MAX_MODULES_CHESTPLATE)
                    .defineInRange("baseMaxHeatChest", 20.0, 0, 5000);

            GENERAL_BASE_MAX_HEAT_LEGS = builder.comment("Power Armor Leggings Heat Cap")
                    .translation(MPSConstants.CONFIG_GENERAL_BASE_MAX_MODULES_LEGGINGS)
                    .defineInRange("baseMaxHeatLegs", 15.0, 0, 5000);

            GENERAL_BASE_MAX_HEAT_FEET = builder.comment("Power Armor Boots Heat Cap")
                    .translation(MPSConstants.CONFIG_GENERAL_BASE_MAX_MODULES_FEET)
                    .defineInRange("baseMaxHeatFeet", 5.0, 0, 5000);

            /**
             * Modules ------------------------------------------------------------------------------------------------
             */
            builder.comment("Module Specific Settings").push("Module");
            // Armor --------------------------------------------------------------------------------------------------
            builder.push("Armor Moudles");

            // Leather Plating
            ModuleConfig leatherPlating = new ModuleConfig(mpsi.MODULE_LEATHER_PLATING__REGNAME, true, builder);
            leatherPlating.done();

            // Iron Plating
            ModuleConfig ironPlating = new ModuleConfig(mpsi.MODULE_IRON_PLATING__REGNAME, true, builder);
            leatherPlating.setIsModuleAllowed(true);
            ironPlating.done();

            // Diamond Plating
            ModuleConfig diamondPlating = new ModuleConfig(mpsi.MODULE_DIAMOND_PLATING__REGNAME, true, builder);
            diamondPlating.done();

            // Energy Shield
            ModuleConfig energyShield = new ModuleConfig(mpsi.MODULE_ENERGY_SHIELD__REGNAME, true, builder);
            energyShield.done();

            builder.pop();
//
//            // Cosmetic -----------------------------------------------------------------------------------------------
//            ModuleConfig transparency = new ModuleConfig(mpsi.MODULE_TRANSPARENT_ARMOR__REGNAME, true, builder);
//            transparency.done();
//
//            // Energy Storage -----------------------------------------------------------------------------------------
//            builder.push("Energy Storage");
//
//            // Basic Battery
//            ModuleConfig basicBattery = new ModuleConfig(mpsi.MODULE_BATTERY_BASIC__REGNAME, true, builder);
//            basicBattery.done();
//
//            // Advanced Battery
//            ModuleConfig advancedBattery = new ModuleConfig(mpsi.MODULE_BATTERY_ADVANCED__REGNAME, true, builder);
//            advancedBattery.done();
//
//            // Elite Battery
//            ModuleConfig eliteBattery = new ModuleConfig(mpsi.MODULE_BATTERY_ELITE__REGNAME, true, builder);
//            eliteBattery.done();
//
//            // Ultimate Battery
//            ModuleConfig ultimateBattery = new ModuleConfig(mpsi.MODULE_BATTERY_ULTIMATE__REGNAME, true, builder);
//            ultimateBattery.done();
//
//            builder.pop();
//
//            // Enery Generation ---------------------------------------------------------------------------------------
//
//
//
//
//
//
//
//
//
////            testMap.put(mpsi.MODULE_LEATHER_PLATING__REGNAME,
////                            builder
////                            .comment("Allow PowerFist model to be customized")
////                            .translation(MPSConstants.CONFIG_COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN)
////                            .define(mpsi.MODULE_LEATHER_PLATING__REGNAME, true)
////            );
//
//
//
//
//
//            // Energy ---------------------------------------------------------------------------------
//            builder.push("Energy Modules");
//
//            // Energy Storage  ------------------------------------------------------------------------
//            builder.push("Energy Storage Modules");
//
//
//
//
//
//
//
//
//
//            builder.pop();
//
//            // Energy Storage  ------------------------------------------------------------------------
//            builder.push("Energy Generation Modules");
//
//            builder.pop();
//
//            builder.push("Allowed Modules");
////            MODULES_ALLOWED = builder
////                    .comment("AllowdModules")
////                    .translation("I aint got no learnin")
////                    .defineInList();
//
//
//
//            builder.pop(); // end energy modules

            builder.build();
        }
    }


    @Override
    public double getPropertyDoubleOrDefault(String s, double v) {
        return 0;
    }

    @Override
    public int getPropertyIntegerOrDefault(String s, int i) {
        return 0;
    }

    @Override
    public boolean isModuleAllowed(String s) {
        return false;
    }

    @Override
    public boolean isModuleAllowed(ResourceLocation resourceLocation) {
        return false;
    }








//    public boolean getModuleAllowedorDefault(ResourceLocation regName, boolean defaultVal) {
//        return defaultVal;
//    }
//
//    public boolean getModuleAllowedorDefault(String regName, boolean defaultVal) {
//        return getModuleAllowedorDefault(new ResourceLocation(regName), defaultVal);
//    }
//
//    public double getPropertyDoubleOrDefault(String key, double multiplier) {
//        return multiplier;
//    }
//
//    public int getPropertyIntegerOrDefault(String key, int multiplier) {
//        return multiplier;
//    }
//
//
//    // fixme!!
//    public boolean isModuleAllowed(ResourceLocation regName) {
//
//
//
//        return true;
//    }

//    public static CompoundNBT getPresetNBTFor(@Nonnull ItemStack itemStack, String presetName) {
//        Map<String, CompoundNBT> map = getCosmeticPresets(itemStack);
//        return map.get(presetName);
//    }
//
//    public static BiMap<String, CompoundNBT> getCosmeticPresets(@Nonnull ItemStack itemStack) {
//        Item item  = itemStack.getItem();
////        if (item instanceof ItemPowerFist)
////            return getServerSettings() != null ? getServerSettings().cosmeticPresetsPowerFist : MPSSettings.cosmetics.getCosmeticPresetsPowerFist();
////        else if (item instanceof ItemPowerArmorHelmet)
////            return getServerSettings() != null ? getServerSettings().cosmeticPresetsPowerArmorHelmet : MPSSettings.cosmetics.getCosmeticPresetsPowerArmorHelmet();
////        else if (item instanceof ItemPowerArmorChestplate)
////            return getServerSettings() != null ? getServerSettings().cosmeticPresetsPowerArmorChestplate : MPSSettings.cosmetics.getCosmeticPresetsPowerArmorChestplate();
////        else if (item instanceof ItemPowerArmorLeggings)
////            return getServerSettings() != null ? getServerSettings().cosmeticPresetsPowerArmorLeggings : MPSSettings.cosmetics.getCosmeticPresetsPowerArmorLeggings();
////        else if (item instanceof ItemPowerArmorBoots)
////            return getServerSettings() != null ? getServerSettings().cosmeticPresetsPowerArmorBoots : MPSSettings.cosmetics.getCosmeticPresetsPowerArmorBoots();
//        return HashBiMap.create();
//    }
//
//    public void updateCosmeticInfo(ResourceLocation location, String name, CompoundNBT cosmeticInfo) {
//        Item item = ForgeRegistries.ITEMS.getValue(location);
//
//        if (item instanceof ItemPowerFist)
//            cosmeticPresetsPowerFist.put(name, cosmeticInfo);
//        else if (item instanceof ItemPowerArmorHelmet)
//            cosmeticPresetsPowerArmorHelmet.put(name, cosmeticInfo);
//        else if (item instanceof ItemPowerArmorChestplate)
//            cosmeticPresetsPowerArmorChestplate.put(name, cosmeticInfo);
//        else if (item instanceof ItemPowerArmorLeggings)
//            cosmeticPresetsPowerArmorLeggings.put(name, cosmeticInfo);
//        else if (item instanceof ItemPowerArmorBoots)
//            cosmeticPresetsPowerArmorBoots.put(name, cosmeticInfo);
//    }
//
//    private BiMap<String, CompoundNBT> cosmeticPresetsPowerFist = HashBiMap.create();
//    public BiMap<String, CompoundNBT> getCosmeticPresetsPowerFist() {
//        if (cosmeticPresetsPowerFist.isEmpty() && !COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN.get())
//            cosmeticPresetsPowerFist = CosmeticPresetSaveLoad.loadPresetsForItem(MPSItems.INSTANCE.powerFist, 0);
//        return cosmeticPresetsPowerFist;
//    }
//
//    private BiMap<String, CompoundNBT> cosmeticPresetsPowerArmorHelmet = HashBiMap.create();
//    public BiMap<String, CompoundNBT> getCosmeticPresetsPowerArmorHelmet() {
//        if (cosmeticPresetsPowerArmorHelmet.isEmpty() && !COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get())
//            cosmeticPresetsPowerArmorHelmet = CosmeticPresetSaveLoad.loadPresetsForItem(MPSItems.INSTANCE.powerArmorHead, 0);
//        return cosmeticPresetsPowerArmorHelmet;
//    }
//
//    private BiMap<String, CompoundNBT> cosmeticPresetsPowerArmorChestplate = HashBiMap.create();
//    public BiMap<String, CompoundNBT> getCosmeticPresetsPowerArmorChestplate() {
//        if(cosmeticPresetsPowerArmorChestplate.isEmpty() && !COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get())
//            cosmeticPresetsPowerArmorChestplate = CosmeticPresetSaveLoad.loadPresetsForItem(MPSItems.INSTANCE.powerArmorTorso, 0);
//        return cosmeticPresetsPowerArmorChestplate;
//    }
//
//    private BiMap<String, CompoundNBT> cosmeticPresetsPowerArmorLeggings = HashBiMap.create();
//    public BiMap<String, CompoundNBT> getCosmeticPresetsPowerArmorLeggings() {
//        if(cosmeticPresetsPowerArmorLeggings.isEmpty() && !COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get())
//            cosmeticPresetsPowerArmorLeggings = CosmeticPresetSaveLoad.loadPresetsForItem(MPSItems.INSTANCE.powerArmorLegs, 0);
//        return cosmeticPresetsPowerArmorLeggings;
//    }
//
//    private BiMap<String, CompoundNBT>  cosmeticPresetsPowerArmorBoots = HashBiMap.create();
//    public BiMap<String, CompoundNBT> getCosmeticPresetsPowerArmorBoots() {
//        if(cosmeticPresetsPowerArmorBoots.isEmpty() && !COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get())
//            cosmeticPresetsPowerArmorBoots = CosmeticPresetSaveLoad.loadPresetsForItem(MPSItems.INSTANCE.powerArmorFeet, 0);
//        return cosmeticPresetsPowerArmorBoots;
//    }
}