/*
 * ModularPowersuits (Maintenance builds by lehjr)
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.config;

import com.github.lehjr.mpalib.network.MPALibByteBufferUtils;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorBoots;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorChestplate;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorHelmet;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorLeggings;
import com.github.machinemuse.powersuits.item.tool.ItemPowerFist;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.TreeMap;

/**
 * Caution: make sure the order of the packets matches between Read and Write.
 */
public class MPSServerSettings {
    /**
     * General -----------------------------------------------------------------------------------
     */
    public final boolean useOldAutoFeeder;
    public final double maximumFlyingSpeedmps;
    public final double maximumArmorPerPiece;
    public final double getSalvageChance;

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
     * Max Modules of Type -----------------------------------------------------------------------
     */
    public final int maxArmorModules;
    public final int maxEnergyStorageModules;
    public final int maxEnergyGenModules;
    public final int maxToolModules;
    public final int maxWeaponModules;
    public final int maxMovementModules;
    public final int maxCosmeticModules;
    public final int maxVisionModules;
    public final int maxEnvironmentalModules;
    public final int maxSpecialModules;
    public final int maxMiningEnhancementModules;

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
    public MPSServerSettings() {
        /**
         *  General -------------------------------------------------------------------------------
         */
        useOldAutoFeeder = MPSSettings.general.useOldAutoFeeder;
        maximumFlyingSpeedmps = MPSSettings.general.getMaximumFlyingSpeedmps;
        maximumArmorPerPiece = MPSSettings.general.getMaximumArmorPerPiece;
        getSalvageChance = MPSSettings.general.getSalvageChance;

        /**
         *  Recipes -------------------------------------------------------------------------------
         */
        useThermalExpansionRecipes = MPSSettings.recipesAllowed.useThermalExpansionRecipes;
        useEnderIORecipes = MPSSettings.recipesAllowed.useEnderIORecipes;
        useTechRebornRecipes = MPSSettings.recipesAllowed.useTechRebornRecipes;
        useIC2Recipes = MPSSettings.recipesAllowed.useIC2Recipes;

        /**
         * Custom install costs -------------------------------------------------------------------
         */
        MPSSettings.loadCustomInstallCosts();

        /**
         * Max Base Heat Heat --------------------------------------------------------------------
         */
        baseMaxHeatPowerFist = MPSSettings.general.baseMaxHeatPowerFist;
        baseMaxHeatHelmet = MPSSettings.general.baseMaxHeatHelmet;
        baseMaxHeatChest = MPSSettings.general.baseMaxHeatChest;
        baseMaxHeatLegs = MPSSettings.general.baseMaxHeatLegs;
        baseMaxHeatFeet = MPSSettings.general.baseMaxHeatFeet;

        /**
         * Modules -------------------------------------------------------------------------------
         */
        allowedModules = new TreeMap<>(MPSSettings.modules.allowedModules);
        propertyDouble = new TreeMap<>(MPSSettings.modules.propertyDouble);
        propertyInteger = new TreeMap<>(MPSSettings.modules.propertyInteger);

        /**
         * Max Modules of Type -------------------------------------------------------------------
         */
        maxArmorModules = MPSSettings.limits.maxArmorModules;
        maxEnergyStorageModules = MPSSettings.limits.maxEnergyStorageModules;
        maxEnergyGenModules = MPSSettings.limits.maxEnergyGenModules;
        maxToolModules = MPSSettings.limits.maxToolModules;
        maxWeaponModules = MPSSettings.limits.maxWeaponModules;
        maxMovementModules = MPSSettings.limits.maxMovementModules;
        maxCosmeticModules = MPSSettings.limits.maxCosmeticModules;
        maxVisionModules = MPSSettings.limits.maxVisionModules;
        maxEnvironmentalModules = MPSSettings.limits.maxEnvironmentalModules;
        maxSpecialModules = MPSSettings.limits.maxSpecialModules;
        maxMiningEnhancementModules = MPSSettings.limits.maxMiningEnhancementModules;

        /**
         * Cosmetics ------------------------------------------------------------------------------
         */
        useLegacyCosmeticSystem = MPSSettings.cosmetics.useLegacyCosmeticSystem;
        allowHighPollyArmorModuels = MPSSettings.cosmetics.allowHighPollyArmorModuels;
        allowPowerFistCustomization = MPSSettings.cosmetics.allowPowerFistCustomization;

        cosmeticPresetsPowerFist = MPSSettings.cosmetics.getCosmeticPresetsPowerFist();
        cosmeticPresetsPowerArmorHelmet = MPSSettings.cosmetics.getCosmeticPresetsPowerArmorHelmet();
        cosmeticPresetsPowerArmorChestplate = MPSSettings.cosmetics.getCosmeticPresetsPowerArmorChestplate();
        cosmeticPresetsPowerArmorLeggings = MPSSettings.cosmetics.getCosmeticPresetsPowerArmorLeggings();
        cosmeticPresetsPowerArmorBoots = MPSSettings.cosmetics.getCosmeticPresetsPowerArmorBoots();
    }

    /**
     * Sets all settings from a packet received client side in a new instance held in MPSSettings.
     * @param datain
     */
    public MPSServerSettings(final ByteBuf datain) {
        /**
         * General -------------------------------------------------------------------------------
         */
        useOldAutoFeeder = datain.readBoolean();
        maximumFlyingSpeedmps = datain.readDouble();
        maximumArmorPerPiece = datain.readDouble();
        getSalvageChance = datain.readDouble();
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
         * Custom install costs -------------------------------------------------------------------
         */
        ModuleManager.INSTANCE.setCustomInstallCosts(MPALibByteBufferUtils.readMap(datain, String.class, ItemStack[].class));

        /**
         * Modules -------------------------------------------------------------------------------
         */
        allowedModules = MPALibByteBufferUtils.readMap(datain, String.class, Boolean.class);
        propertyDouble =MPALibByteBufferUtils.readMap(datain, String.class, Double.class);
        propertyInteger = MPALibByteBufferUtils.readMap(datain, String.class, Integer.class);

        /**
         * Max Modules of Type -------------------------------------------------------------------
         */
        maxArmorModules = datain.readInt();
        maxEnergyStorageModules = datain.readInt();
        maxEnergyGenModules = datain.readInt();
        maxToolModules = datain.readInt();
        maxWeaponModules = datain.readInt();
        maxMovementModules = datain.readInt();
        maxCosmeticModules = datain.readInt();
        maxVisionModules = datain.readInt();
        maxEnvironmentalModules = datain.readInt();
        maxSpecialModules = datain.readInt();
        maxMiningEnhancementModules = datain.readInt();

        /**
         * Cosmetics ------------------------------------------------------------------------------
         */
        useLegacyCosmeticSystem = datain.readBoolean();
        allowHighPollyArmorModuels = datain.readBoolean();
        allowPowerFistCustomization = datain.readBoolean();
        cosmeticPresetsPowerFist = HashBiMap.create(MPALibByteBufferUtils.readMap(datain, String.class, NBTTagCompound.class));
        cosmeticPresetsPowerArmorHelmet = HashBiMap.create(MPALibByteBufferUtils.readMap(datain, String.class, NBTTagCompound.class));
        cosmeticPresetsPowerArmorChestplate = HashBiMap.create(MPALibByteBufferUtils.readMap(datain, String.class, NBTTagCompound.class));
        cosmeticPresetsPowerArmorLeggings = HashBiMap.create(MPALibByteBufferUtils.readMap(datain, String.class, NBTTagCompound.class));
        cosmeticPresetsPowerArmorBoots = HashBiMap.create(MPALibByteBufferUtils.readMap(datain, String.class, NBTTagCompound.class));
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
        packet.writeDouble(getSalvageChance);
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
         *  Custom Install Costs -------------------------------------------------------------------
         */
        MPALibByteBufferUtils.writeMap(packet, ModuleManager.INSTANCE.getCustomInstallCostsForServerToClientConfig(), true);

        /**
         * Modules -------------------------------------------------------------------------------
         */
        MPALibByteBufferUtils.writeMap(packet,allowedModules, true);
        MPALibByteBufferUtils.writeMap(packet,propertyDouble, true);
        MPALibByteBufferUtils.writeMap(packet,propertyInteger, true);

        /**
         * Max Modules of Type -------------------------------------------------------------------
         */
        packet.writeInt(maxArmorModules);
        packet.writeInt(maxEnergyStorageModules);
        packet.writeInt(maxEnergyGenModules);
        packet.writeInt(maxToolModules);
        packet.writeInt(maxWeaponModules);
        packet.writeInt(maxMovementModules);
        packet.writeInt(maxCosmeticModules);
        packet.writeInt(maxVisionModules);
        packet.writeInt(maxEnvironmentalModules);
        packet.writeInt(maxSpecialModules);
        packet.writeInt(maxMiningEnhancementModules);

        /**
         * Cosmetics ------------------------------------------------------------------------------
         */
        packet.writeBoolean(useLegacyCosmeticSystem);
        packet.writeBoolean(allowHighPollyArmorModuels);
        packet.writeBoolean(allowPowerFistCustomization);

        MPALibByteBufferUtils.writeMap(packet,cosmeticPresetsPowerFist, true);
        MPALibByteBufferUtils.writeMap(packet,cosmeticPresetsPowerArmorHelmet, true);
        MPALibByteBufferUtils.writeMap(packet,cosmeticPresetsPowerArmorChestplate, true);
        MPALibByteBufferUtils.writeMap(packet,cosmeticPresetsPowerArmorLeggings, true);
        MPALibByteBufferUtils.writeMap(packet,cosmeticPresetsPowerArmorBoots, true);
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