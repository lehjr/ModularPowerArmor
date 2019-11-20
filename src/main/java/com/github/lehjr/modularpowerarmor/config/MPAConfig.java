package com.github.lehjr.modularpowerarmor.config;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.CreativeTab;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorBoots;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorChestplate;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorHelmet;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorLeggings;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
import com.github.lehjr.mpalib.basemod.MPALib;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public enum MPAConfig {
    INSTANCE;

    /**
     * Creative tab ------------------------------------------------------------------------------
     */
    public static CreativeTabs mpsCreativeTab = new CreativeTab();
    /**
     * Server side settings setup ----------------------------------------------------------------
     */
    private static MPAServerSettings serverSettings;
    /**
     * The annotation based config system lacks the ability to handle entries not set at runtime.
     * Writes the missing values to a file
     */
    Map<String, Double> missingModuleDoubles = new HashMap<>();
    /**
     * The annotation based config system lacks the ability to handle entries not set at runtime.
     * Writes the missing values to a file
     */
    Map<String, Integer> missingModuleIntegers = new HashMap<>();

    /**
     * Config folder -----------------------------------------------------------------------------
     */
    @Nullable
    public static File getConfigFolder() {
        return MPALib.configDir;
    }

    @Nullable
    public static final MPAServerSettings getServerSettings() {
        return serverSettings;
    }

    public static void setServerSettings(@Nullable final MPAServerSettings serverSettingsIn) {
        serverSettings = serverSettingsIn;
    }

    // Server side settings
    public static double getMaximumFlyingSpeedmps() {
        return getServerSettings() != null ? getServerSettings().maximumFlyingSpeedmps : MPASettings.general.getMaximumFlyingSpeedmps;
    }

    public boolean useOldAutoFeeder() {
        return getServerSettings() != null ? getServerSettings().useOldAutoFeeder : MPASettings.general.useOldAutoFeeder;
    }

    public static double getMaximumArmorPerPiece() {
        return getServerSettings() != null ? getServerSettings().maximumArmorPerPiece : MPASettings.general.getMaximumArmorPerPiece;
    }

    public static int rfValueOfComponent(@Nonnull ItemStack stackInCost) {
        if (!stackInCost.isEmpty() && stackInCost.getItem() instanceof ItemComponent) {
            switch (stackInCost.getItemDamage() - ItemComponent.lvcapacitor.getItemDamage()) {
                case 0:
                    return 200000 * stackInCost.getCount();
                case 1:
                    return 1000000 * stackInCost.getCount();
                case 2:
                    return 7500000 * stackInCost.getCount();
                case 3:
                    return 10000000 * stackInCost.getCount();
                default:
                    return 0;
            }
        }
        return 0;
    }

    /**
     * HUD Settings ------------------------------------------------------------------------------
     */
    public boolean toggleModuleSpam() {
        return MPASettings.hud.toggleModuleSpam;
    }

    public boolean keybindHUDon() {
        return MPASettings.hud.keybindHUDon;
    }

    public double keybindHUDx() {
        return MPASettings.hud.keybindHUDx;
    }

    public double keybindHUDy() {
        return MPASettings.hud.keybindHUDy;
    }

    public boolean useGraphicalMeters() {
        return MPASettings.HUD.useGraphicalMeters;
    }

    /**
     * General -----------------------------------------------------------------------------------
     */
    // Client side settings
    public boolean use24hClock() {
        return MPASettings.general.use24hClock;
    }

    public boolean allowConflictingKeybinds() {
        return MPASettings.general.allowConflictingKeybinds;
    }

    public double getBaseMaxHeatPowerFist() {
        return getServerSettings() != null ? getServerSettings().baseMaxHeatPowerFist : MPASettings.general.baseMaxHeatPowerFist;
    }

    public double getBaseMaxHeatHelmet() {
        return getServerSettings() != null ? getServerSettings().baseMaxHeatHelmet : MPASettings.general.baseMaxHeatHelmet;
    }

    public double getBaseMaxHeatChest() {
        return getServerSettings() != null ? getServerSettings().baseMaxHeatChest : MPASettings.general.baseMaxHeatChest;
    }

    public double getBaseMaxHeatLegs() {
        return getServerSettings() != null ? getServerSettings().baseMaxHeatLegs : MPASettings.general.baseMaxHeatLegs;
    }

    public double getBaseMaxHeatFeet() {
        return getServerSettings() != null ? getServerSettings().baseMaxHeatFeet : MPASettings.general.baseMaxHeatFeet;
    }

    /**
     *  Recipes -----------------------------------------------------------------------------------
     */
    public boolean useThermalExpansionRecipes() {
        return ModCompatibility.isThermalExpansionLoaded() &&
                ( getServerSettings() != null ? getServerSettings().useThermalExpansionRecipes : MPASettings.recipesAllowed.useThermalExpansionRecipes );
    }

    public boolean useEnderIORecipes() {
        return ModCompatibility.isEnderIOLoaded() &&
                ( getServerSettings() != null ? getServerSettings().useEnderIORecipes : MPASettings.recipesAllowed.useEnderIORecipes );
    }

    public boolean useTechRebornRecipes() {
        return ModCompatibility.isTechRebornLoaded() &&
                ( getServerSettings() != null ? getServerSettings().useTechRebornRecipes : MPASettings.recipesAllowed.useTechRebornRecipes );
    }

    public boolean useIC2Recipes() {
        // IC2 classic and IC2 experimental, no check here because they have to be checked separately
        return getServerSettings() != null ? getServerSettings().useIC2Recipes :  MPASettings.recipesAllowed.useIC2Recipes;
    }

    /**
     * Modules -----------------------------------------------------------------------------------
     */
    public static ModuleConfig moduleConfig = new ModuleConfig();
    static class ModuleConfig implements IConfig {

        @Override
        public double getBasePropertyDoubleOrDefault(
                EnumModuleCategory category,
                @Nonnull ItemStack module,
                String propertyName, double baseVal) {
            if (module.isEmpty()) {
                return baseVal;
            }

            String moduleName = module.getItem().getRegistryName().getPath();
            String key = new StringBuilder(moduleName).append('.').append(propertyName).append(".base").toString();

            return getPropertyDoubleOrDefault(key, baseVal);
        }


        @Override
        public double getTradeoffPropertyDoubleOrDefault(
                EnumModuleCategory category,
                @Nonnull ItemStack module,
                String tradeoffName,
                String propertyName,
                double multiplier) {

            if (module.isEmpty()) {
                return multiplier;
            }

            String moduleName = module.getItem().getRegistryName().getPath();
            String key = new StringBuilder(moduleName).append('.').append(propertyName).append('.').append(tradeoffName).append(".multiplier").toString();
            return getPropertyDoubleOrDefault(key, multiplier);
        }

        @Override
        public int getBasePropertIntegerOrDefault(EnumModuleCategory category, @Nonnull ItemStack module, String propertyName, int baseVal) {
            if (module.isEmpty()) {
                return baseVal;
            }

            String moduleName = module.getItem().getRegistryName().getPath();
            String key = new StringBuilder(moduleName).append('.').append(propertyName).append(".base").toString();
            return getPropertyIntegerOrDefault(key, baseVal);
        }

        @Override
        public int getTradeoffPropertyIntegerOrDefault(EnumModuleCategory category, @Nonnull ItemStack module, String tradeoffName, String propertyName, int multiplier) {
            if (module.isEmpty()) {
                return multiplier;
            }

            String moduleName = module.getItem().getRegistryName().getPath();
            String key = new StringBuilder(moduleName).append('.').append(propertyName).append('.').append(tradeoffName).append(".multiplier").toString();
            return getPropertyIntegerOrDefault(key, multiplier);
        }

        @Override
        public boolean isModuleAllowed(EnumModuleCategory category, @Nonnull ItemStack module) {
            if (module.isEmpty()) {
                return false;
            }
            boolean allowed = true;
            String moduleName = module.getItem().getRegistryName().getPath();
            return getServerSettings() != null ? getServerSettings().allowedModules.getOrDefault(moduleName, allowed) : MPASettings.modules.allowedModules.getOrDefault(moduleName, allowed);
        }

        // drop the prefix for MPS modules and replace "dots" with underscores
        final String itemPrefix = "item." + Constants.MODID + ".";
        final String itemModulePrefix = "item.module." + Constants.MODID + ".";
        String itemTranslationKeyToConfigKey(String translationKey) {
            if (translationKey.startsWith(itemPrefix )){
                translationKey = translationKey.substring(itemPrefix .length());
            }
            if (translationKey.startsWith(itemModulePrefix)) {
                translationKey = translationKey.substring(itemModulePrefix .length());
            }
            return translationKey;//.replace(".", "_");
        }

        public double getPropertyDoubleOrDefault(String name, double value) {
            //TODO: use this after porting finished
            //return getServerSettings() != null ? getServerSettings().propertyDouble.getOrDefault(id, getValue) : MPASettings.modules.propertyDouble.getOrDefault(id, getValue);
            if (getServerSettings() != null) {
                if (getServerSettings().propertyDouble.isEmpty() || !getServerSettings().propertyDouble.containsKey(name)) {
                    System.out.println("Property config values missing: ");
                    System.out.println("property: " + name);
                    System.out.println("getValue: " + value);
//                getServerSettings().propertyDouble.put(id, getValue);
                    MPAConfig.INSTANCE.missingModuleDoubles.put(name, value);

                }
                return getServerSettings().propertyDouble.getOrDefault(name, value);
            } else {
                if (MPASettings.modules.propertyDouble.isEmpty() || !MPASettings.modules.propertyDouble.containsKey(name)) {
                    System.out.println("Property config values missing: ");
                    System.out.println("property: " + name);
                    System.out.println("getValue: " + value);
//                MPASettings.modules.propertyDouble.put(id, getValue);
                    MPAConfig.INSTANCE.missingModuleDoubles.put(name, value);
                }
                return MPASettings.modules.propertyDouble.getOrDefault(name, value);
            }
        }

        public int getPropertyIntegerOrDefault(String name, int value) {
            //TODO: use this after porting finished
            //return getServerSettings() != null ? getServerSettings().propertyDouble.getOrDefault(id, getValue) : MPASettings.modules.propertyDouble.getOrDefault(id, getValue);
            if (getServerSettings() != null) {
                if (getServerSettings().propertyInteger.isEmpty() || !getServerSettings().propertyInteger.containsKey(name)) {
                    System.out.println("Property config values missing: ");
                    System.out.println("property: " + name);
                    System.out.println("getValue: " + value);
//                getServerSettings().propertyInteger.put(id, getValue);
                    MPAConfig.INSTANCE.missingModuleIntegers.put(name, value);
                }
                return getServerSettings().propertyInteger.getOrDefault(name, value);
            } else {
                if (MPASettings.modules.propertyInteger.isEmpty() || !MPASettings.modules.propertyInteger.containsKey(name)) {
                    System.out.println("Property config values missing: ");
                    System.out.println("property: " + name);
                    System.out.println("getValue: " + value);
//                MPASettings.modules.propertyInteger.put(id, getValue);
                    MPAConfig.INSTANCE.missingModuleIntegers.put(name, value);
                }
                return MPASettings.modules.propertyInteger.getOrDefault(name, value);
            }
        }
    }

    public void configDoubleKVGen() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, Double> line : new TreeMap<>(MPAConfig.INSTANCE.missingModuleDoubles).entrySet()) { // treemap sorts the keys
            stringBuilder.append("put( \"").append(line.getKey()).append("\", ").append(line.getValue()).append("D );\n");
        }
        try {
            String output = stringBuilder.toString();
            if (output != null && !output.isEmpty())
                FileUtils.writeStringToFile(new File(getConfigFolder(), "missingConfigDoubles.txt"), output, Charset.defaultCharset(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public void configIntegerKVGen() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, Integer> line : new TreeMap<>(MPAConfig.INSTANCE.missingModuleIntegers).entrySet()) { // treemap sorts the keys
            stringBuilder.append("put( \"").append(line.getKey()).append("\", ").append(line.getValue()).append(");\n");
        }

        try {
            FileUtils.writeStringToFile(new File(getConfigFolder(), "missingConfigIntegers.txt"), stringBuilder.toString(), Charset.defaultCharset(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * Models ------------------------------------------------------------------------------------
     */
    public static boolean useLegacyCosmeticSystem() {
        return getServerSettings() != null ? getServerSettings().useLegacyCosmeticSystem : MPASettings.cosmetics.useLegacyCosmeticSystem;
    }

    public static boolean allowHighPollyArmorModels() {
        return getServerSettings() != null ? getServerSettings().allowHighPollyArmorModuels : MPASettings.cosmetics.allowHighPollyArmorModuels;
    }

    public static boolean allowPowerFistCustomization() {
        return getServerSettings() != null ? getServerSettings().allowPowerFistCustomization : MPASettings.cosmetics.allowPowerFistCustomization;
    }

    @Nullable
    public static NBTTagCompound getPresetNBTFor(@Nonnull ItemStack itemStack, String presetName) {
        Map<String, NBTTagCompound> map = getCosmeticPresets(itemStack);
        return map.get(presetName);
    }

    public static BiMap<String, NBTTagCompound> getCosmeticPresets(@Nonnull ItemStack itemStack) {
        Item item  = itemStack.getItem();
        if (item instanceof ItemPowerFist)
            return getServerSettings() != null ? getServerSettings().cosmeticPresetsPowerFist : MPASettings.cosmetics.getCosmeticPresetsPowerFist();
        else if (item instanceof ItemPowerArmorHelmet)
            return getServerSettings() != null ? getServerSettings().cosmeticPresetsPowerArmorHelmet : MPASettings.cosmetics.getCosmeticPresetsPowerArmorHelmet();
        else if (item instanceof ItemPowerArmorChestplate)
            return getServerSettings() != null ? getServerSettings().cosmeticPresetsPowerArmorChestplate : MPASettings.cosmetics.getCosmeticPresetsPowerArmorChestplate();
        else if (item instanceof ItemPowerArmorLeggings)
            return getServerSettings() != null ? getServerSettings().cosmeticPresetsPowerArmorLeggings : MPASettings.cosmetics.getCosmeticPresetsPowerArmorLeggings();
        else if (item instanceof ItemPowerArmorBoots)
            return getServerSettings() != null ? getServerSettings().cosmeticPresetsPowerArmorBoots : MPASettings.cosmetics.getCosmeticPresetsPowerArmorBoots();
        return HashBiMap.create();
    }
}