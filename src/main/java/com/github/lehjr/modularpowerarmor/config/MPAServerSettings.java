package com.github.lehjr.modularpowerarmor.config;

import com.github.lehjr.mpalib.network.MuseByteBufferUtils;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.ByteBuf;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorBoots;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorChestplate;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorHelmet;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorLeggings;
import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.TreeMap;

/**
 * Caution: make sure the order of the packets matches between Read and Write.
 */
public class MPAServerSettings {
    /**
     * General -----------------------------------------------------------------------------------
     */
    public final boolean useOldAutoFeeder;
    public final double maximumFlyingSpeedmps;
    public final double maximumArmorPerPiece;

    /**
     * Recipes -----------------------------------------------------------------------------------
     */
    public final boolean useThermalExpansionRecipes;
    public final boolean useEnderIORecipes;
    public final boolean useTechRebornRecipes;
    public static boolean useIC2Recipes;

    /**
     * Heat ---------------------------------------------------------------------------------------
     */
    public final double baseMaxHeatPowerFist;
    public final double baseMaxHeatHelmet;
    public final double baseMaxHeatChest;
    public final double baseMaxHeatLegs;
    public final double baseMaxHeatFeet;

    /**
     * Modules -----------------------------------------------------------------------------------
     */
    public final Map<String, Boolean> allowedModules;
    public final Map<String, Double> propertyDouble;
    public final Map<String, Integer> propertyInteger;


    /**
     * Cosmetics ---------------------------------------------------------------------------------
     */
    public final boolean useLegacyCosmeticSystem;
    public final boolean allowHighPollyArmorModuels;
    public final boolean allowPowerFistCustomization;

    public final BiMap<String, NBTTagCompound> cosmeticPresetsPowerFist;
    public final BiMap<String, NBTTagCompound> cosmeticPresetsPowerArmorHelmet;
    public final BiMap<String, NBTTagCompound> cosmeticPresetsPowerArmorChestplate;
    public final BiMap<String, NBTTagCompound> cosmeticPresetsPowerArmorLeggings;
    public final BiMap<String, NBTTagCompound> cosmeticPresetsPowerArmorBoots;

    /**
     * Server side instance.
     */
    public MPAServerSettings() {
        /**
         *  General -------------------------------------------------------------------------------
         */
        useOldAutoFeeder = MPASettings.general.useOldAutoFeeder;
        maximumFlyingSpeedmps = MPASettings.general.getMaximumFlyingSpeedmps;
        maximumArmorPerPiece = MPASettings.general.getMaximumArmorPerPiece;

        /**
         *  Recipes -------------------------------------------------------------------------------
         */
        useThermalExpansionRecipes = MPASettings.recipesAllowed.useThermalExpansionRecipes;
        useEnderIORecipes = MPASettings.recipesAllowed.useEnderIORecipes;
        useTechRebornRecipes = MPASettings.recipesAllowed.useTechRebornRecipes;
        useIC2Recipes = MPASettings.recipesAllowed.useIC2Recipes;

        /**
         * Max Base Heat Heat --------------------------------------------------------------------
         */
        baseMaxHeatPowerFist = MPASettings.general.baseMaxHeatPowerFist;
        baseMaxHeatHelmet = MPASettings.general.baseMaxHeatHelmet;
        baseMaxHeatChest = MPASettings.general.baseMaxHeatChest;
        baseMaxHeatLegs = MPASettings.general.baseMaxHeatLegs;
        baseMaxHeatFeet = MPASettings.general.baseMaxHeatFeet;

        /**
         * Modules -------------------------------------------------------------------------------
         */
        allowedModules = new TreeMap<>(MPASettings.modules.allowedModules);
        propertyDouble = new TreeMap<>(MPASettings.modules.propertyDouble);
        propertyInteger = new TreeMap<>(MPASettings.modules.propertyInteger);

        /**
         * Cosmetics ------------------------------------------------------------------------------
         */
        useLegacyCosmeticSystem = MPASettings.cosmetics.useLegacyCosmeticSystem;
        allowHighPollyArmorModuels = MPASettings.cosmetics.allowHighPollyArmorModuels;
        allowPowerFistCustomization = MPASettings.cosmetics.allowPowerFistCustomization;

        cosmeticPresetsPowerFist = MPASettings.cosmetics.getCosmeticPresetsPowerFist();
        cosmeticPresetsPowerArmorHelmet = MPASettings.cosmetics.getCosmeticPresetsPowerArmorHelmet();
        cosmeticPresetsPowerArmorChestplate = MPASettings.cosmetics.getCosmeticPresetsPowerArmorChestplate();
        cosmeticPresetsPowerArmorLeggings = MPASettings.cosmetics.getCosmeticPresetsPowerArmorLeggings();
        cosmeticPresetsPowerArmorBoots = MPASettings.cosmetics.getCosmeticPresetsPowerArmorBoots();
    }

    /**
     * Sets all settings from a packet received client side in a new instance held in MPASettings.
     * @param datain
     */
    public MPAServerSettings(final ByteBuf datain) {
        /**
         * General -------------------------------------------------------------------------------
         */
        useOldAutoFeeder = datain.readBoolean();
        maximumFlyingSpeedmps = datain.readDouble();
        maximumArmorPerPiece = datain.readDouble();
        baseMaxHeatPowerFist = datain.readDouble();
        baseMaxHeatHelmet = datain.readDouble();
        baseMaxHeatChest = datain.readDouble();
        baseMaxHeatLegs = datain.readDouble();
        baseMaxHeatFeet = datain.readDouble();

        /**
         *  Recipes -------------------------------------------------------------------------------
         */
        useThermalExpansionRecipes = datain.readBoolean();
        useEnderIORecipes = datain.readBoolean();
        useTechRebornRecipes = datain.readBoolean();
        useIC2Recipes = datain.readBoolean();

        /**
         * Modules -------------------------------------------------------------------------------
         */
        allowedModules = MuseByteBufferUtils.readMap(datain, String.class, Boolean.class);
        propertyDouble =MuseByteBufferUtils.readMap(datain, String.class, Double.class);
        propertyInteger = MuseByteBufferUtils.readMap(datain, String.class, Integer.class);

        /**
         * Cosmetics ------------------------------------------------------------------------------
         */
        useLegacyCosmeticSystem = datain.readBoolean();
        allowHighPollyArmorModuels = datain.readBoolean();
        allowPowerFistCustomization = datain.readBoolean();
        cosmeticPresetsPowerFist = HashBiMap.create(MuseByteBufferUtils.readMap(datain, String.class, NBTTagCompound.class));
        cosmeticPresetsPowerArmorHelmet = HashBiMap.create(MuseByteBufferUtils.readMap(datain, String.class, NBTTagCompound.class));
        cosmeticPresetsPowerArmorChestplate = HashBiMap.create(MuseByteBufferUtils.readMap(datain, String.class, NBTTagCompound.class));
        cosmeticPresetsPowerArmorLeggings = HashBiMap.create(MuseByteBufferUtils.readMap(datain, String.class, NBTTagCompound.class));
        cosmeticPresetsPowerArmorBoots = HashBiMap.create(MuseByteBufferUtils.readMap(datain, String.class, NBTTagCompound.class));
    }

    /**
     * This is a server side operation that gets the values and writes them to the packet.
     * This packet is then sent to a new client on login to sync config values. This allows
     * the server to be able to control these settings.
     * @param packet
     */
    public void writeToBuffer(final ByteBuf packet) {
        /**
         * General -------------------------------------------------------------------------------
         */
        packet.writeBoolean(useOldAutoFeeder);
        packet.writeDouble(maximumFlyingSpeedmps);
        packet.writeDouble(maximumArmorPerPiece);
        packet.writeDouble(baseMaxHeatPowerFist);
        packet.writeDouble(baseMaxHeatHelmet);
        packet.writeDouble(baseMaxHeatChest);
        packet.writeDouble(baseMaxHeatLegs);
        packet.writeDouble(baseMaxHeatFeet);

        /**
         *  Recipes -------------------------------------------------------------------------------
         */
        packet.writeBoolean(useThermalExpansionRecipes);
        packet.writeBoolean(useEnderIORecipes);
        packet.writeBoolean(useTechRebornRecipes);
        packet.writeBoolean(useIC2Recipes);

        /**
         * Modules -------------------------------------------------------------------------------
         */
        MuseByteBufferUtils.writeMap(packet,allowedModules, true);
        MuseByteBufferUtils.writeMap(packet,propertyDouble, true);
        MuseByteBufferUtils.writeMap(packet,propertyInteger, true);

        /**
         * Cosmetics ------------------------------------------------------------------------------
         */
        packet.writeBoolean(useLegacyCosmeticSystem);
        packet.writeBoolean(allowHighPollyArmorModuels);
        packet.writeBoolean(allowPowerFistCustomization);

        MuseByteBufferUtils.writeMap(packet,cosmeticPresetsPowerFist, true);
        MuseByteBufferUtils.writeMap(packet,cosmeticPresetsPowerArmorHelmet, true);
        MuseByteBufferUtils.writeMap(packet,cosmeticPresetsPowerArmorChestplate, true);
        MuseByteBufferUtils.writeMap(packet,cosmeticPresetsPowerArmorLeggings, true);
        MuseByteBufferUtils.writeMap(packet,cosmeticPresetsPowerArmorBoots, true);
    }

    public void updateCosmeticInfo(ResourceLocation location, String name, NBTTagCompound cosmeticInfo) {
        Item item = Item.REGISTRY.getObject(location);

        if (item instanceof ItemPowerFist)
            cosmeticPresetsPowerFist.put(name, cosmeticInfo);
        else if (item instanceof ItemPowerArmorHelmet)
            cosmeticPresetsPowerArmorHelmet.put(name, cosmeticInfo);
        else if (item instanceof ItemPowerArmorChestplate)
            cosmeticPresetsPowerArmorChestplate.put(name, cosmeticInfo);
        else if (item instanceof ItemPowerArmorLeggings)
            cosmeticPresetsPowerArmorLeggings.put(name, cosmeticInfo);
        else if (item instanceof ItemPowerArmorBoots)
            cosmeticPresetsPowerArmorBoots.put(name, cosmeticInfo);
    }
}