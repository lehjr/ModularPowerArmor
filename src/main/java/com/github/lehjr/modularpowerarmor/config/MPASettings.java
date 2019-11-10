package com.github.lehjr.modularpowerarmor.config;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.Objects;
import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorBoots;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorChestplate;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorHelmet;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorLeggings;
import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;

import java.util.HashMap;
import java.util.Map;

@Config(modid = Constants.MODID, name = Constants.CONFIG_FILE)
public class MPASettings {
    /**
     * The are all client side settings
     */
    public static HUD hud = new HUD();
    public static class HUD {
        @Config.LangKey(Constants.CONFIG_HUD_USE_GRAPHICAL_METERS)
        @Config.Comment("Use Graphical Meters")
        public static boolean useGraphicalMeters = true;

        @Config.LangKey(Constants.CONFIG_HUD_TOGGLE_MODULE_SPAM)
        @Config.Comment("Chat message when toggling module")
        public boolean toggleModuleSpam = false;

        @Config.LangKey(Constants.CONFIG_HUD_DISPLAY_HUD)
        @Config.Comment("Display HUD")
        public boolean keybindHUDon = true;

        @Config.LangKey(Constants.CONFIG_HUD_KEYBIND_HUD_X)
        @Config.Comment("x position")
        @Config.RangeDouble(min = 0)
        public double keybindHUDx = 8.0;

        @Config.LangKey(Constants.CONFIG_HUD_KEYBIND_HUD_Y)
        @Config.Comment("y position")
        @Config.RangeDouble(min = 0)
        public double keybindHUDy = 32.0;
    }

    public static RecipesAllowed recipesAllowed = new RecipesAllowed();
    public static class RecipesAllowed {

        @Config.Comment("Use recipes for Thermal Expansion")
        public boolean useThermalExpansionRecipes = true;

        @Config.Comment("Use recipes for EnderIO")
        public boolean useEnderIORecipes = true;

        @Config.Comment("Use recipes for TechReborn")
        public boolean useTechRebornRecipes = true;

        @Config.Comment("Use recipes for IndustrialCraft 2")
        public boolean useIC2Recipes = true;
    }

    /**
     * A mixture of client and server side settings
     */
    public static General general = new General();
    public static class General {
        // Server side settings -----------------------------------------------
        @Config.LangKey(Constants.CONFIG_GENERAL_USE_OLD_AUTOFEEDER)
        @Config.Comment("Use Old Auto Feeder Method")
        public boolean useOldAutoFeeder = false;
        // Client side settings ------------------------------------------------------
        @Config.LangKey(Constants.CONFIG_GENERAL_USE_24_HOUR_CLOCK)
        @Config.Comment("Use a 24h clock instead of 12h")
        public boolean use24hClock = false;
        @Config.LangKey(Constants.CONFIG_GENERAL_ALLOW_CONFLICTING_KEYBINDS)
        @Config.Comment("Allow Conflicting Keybinds")
        public boolean allowConflictingKeybinds = true;
        @Config.LangKey(Constants.CONFIG_GENERAL_MAX_FLYING_SPEED)
        @Config.Comment("Maximum flight speed (in m/s)")
        public double getMaximumFlyingSpeedmps = 25.0;
        /**
         * The maximum amount of armor contribution allowed per armor piece. Total
         * armor when the full set is worn can never exceed 4 times this amount.
         */
        @Config.LangKey(Constants.CONFIG_GENERAL_GET_MAX_ARMOR_PER_PIECE)
        @Config.Comment("Maximum Armor per Piece")
        @Config.RangeDouble(min = 0, max = 8.0)
        @Config.RequiresWorldRestart
        public double getMaximumArmorPerPiece = 6.0;

        @Config.LangKey(Constants.CONFIG_GENERAL_SALVAGE_CHANCE)
        @Config.Comment("Chance of each item being returned when salvaged")
        @Config.RangeDouble(min = 0, max = 1.0)
        public double getSalvageChance = 0.9;

        @Config.LangKey(Constants.CONFIG_GENERAL_BASE_MAX_HEAT_POWERFIST)
        @Config.Comment("PowerFist Base Heat Cap")
        public double baseMaxHeatPowerFist = 5.0;

        @Config.LangKey(Constants.CONFIG_GENERAL_BASE_MAX_HEAT_HELMET)
        @Config.Comment("Power Armor Helmet Heat Cap")
        public double baseMaxHeatHelmet = 5.0;

        @Config.LangKey(Constants.CONFIG_GENERAL_BASE_MAX_HEAT_CHEST)
        @Config.Comment("Power Armor Chestplate Heat Cap")
        public double baseMaxHeatChest = 20.0;

        @Config.LangKey(Constants.CONFIG_GENERAL_BASE_MAX_HEAT_LEGS)
        @Config.Comment("Power Armor Leggings Heat Cap")
        public double baseMaxHeatLegs = 15.0;

        @Config.LangKey(Constants.CONFIG_GENERAL_BASE_MAX_HEAT_FEET)
        @Config.Comment("ItemModuleBase Heat Cap")
        public double baseMaxHeatFeet = 5.0;
    }

    /**
     * Currently maps need to be initialized and populated at runtime otherwise the values are not read from the config file
     * <p>
     * TODO: move to server config
     */
    public static Modules modules = new Modules();
    public static class Modules {
        @Config.LangKey(Constants.CONFIG_MODULES)
        @Config.Comment("Whether or not specified module is allowed")
        public Map<String, Boolean> allowedModules = new HashMap<String, Boolean>() {{
            // Debug ----------------------------------------------------------------------
            put(ModuleConstants.MODULE_DEBUG, true);

            // Armor ----------------------------------------------------------------------
            put(RegistryNames.MODULE_LEATHER_PLATING__REGNAME, true);
            put(RegistryNames.MODULE_IRON_PLATING__REGNAME, true);
            put(RegistryNames.MODULE_DIAMOND_PLATING__REGNAME, true);
            put(RegistryNames.MODULE_ENERGY_SHIELD__REGNAME, true);

            // Cosmetic -------------------------------------------------------------------
            put(RegistryNames.MODULE_TRANSPARENT_ARMOR__REGNAME, true);

            // Energy ---------------------------------------------------------------------
            put(RegistryNames.MODULE_BATTERY_BASIC__REGNAME, true);
            put(RegistryNames.MODULE_BATTERY_ADVANCED__REGNAME, true);
            put(RegistryNames.MODULE_BATTERY_ELITE__REGNAME, true);
            put(RegistryNames.MODULE_BATTERY_ULTIMATE__REGNAME, true);
            put(RegistryNames.MODULE_ADVANCED_SOLAR_GENERATOR__REGNAME, true);
            put(RegistryNames.MODULE_COAL_GEN__REGNAME, true);
            put(RegistryNames.MODULE_KINETIC_GENERATOR__REGNAME, true);
            put(RegistryNames.MODULE_SOLAR_GENERATOR__REGNAME, true);
            put(RegistryNames.MODULE_THERMAL_GENERATOR__REGNAME, true);

            // Environmental --------------------------------------------------------------
            put(RegistryNames.MODULE_AIRTIGHT_SEAL__REGNAME, true);
            put(RegistryNames.MODULE_APIARIST_ARMOR__REGNAME, true);
            put(RegistryNames.MODULE_AUTO_FEEDER__REGNAME, true);
            put(RegistryNames.MODULE_BASIC_COOLING_SYSTEM__REGNAME, true);
            put(RegistryNames.MODULE_HAZMAT__REGNAME, true);
            put(RegistryNames.MODULE_MOB_REPULSOR__REGNAME, true);
            put(RegistryNames.MODULE_ADVANCED_COOLING_SYSTEM__REGNAME, true);
            put(RegistryNames.MODULE_WATER_ELECTROLYZER__REGNAME, true);

            // Movement -------------------------------------------------------------------
            put(RegistryNames.MODULE_BLINK_DRIVE__REGNAME, true);
            put(RegistryNames.MODULE_CLIMB_ASSIST__REGNAME, true);
            put(RegistryNames.MODULE_FLIGHT_CONTROL__REGNAME, true);
            put(RegistryNames.MODULE_GLIDER__REGNAME, true);
            put(RegistryNames.MODULE_JETBOOTS__REGNAME, true);
            put(RegistryNames.MODULE_JETPACK__REGNAME, true);
            put(RegistryNames.MODULE_JUMP_ASSIST__REGNAME, true);
            put(RegistryNames.MODULE_PARACHUTE__REGNAME, true);
            put(RegistryNames.MODULE_SHOCK_ABSORBER__REGNAME, true);
            put(RegistryNames.MODULE_SPRINT_ASSIST__REGNAME, true);
            put(RegistryNames.MODULE_SWIM_BOOST__REGNAME, true);

            // Special --------------------------------------------------------------------
            put(RegistryNames.MODULE_CLOCK__REGNAME, true);
            put(RegistryNames.MODULE_COMPASS__REGNAME, true);
            put(RegistryNames.MODULE_ACTIVE_CAMOUFLAGE__REGNAME, true);
            put(RegistryNames.MODULE_MAGNET__REGNAME, true);

            // Vision ---------------------------------------------------------------------
            put(RegistryNames.BINOCULARS_MODULE__REGNAME, true);
            put(RegistryNames.MODULE_NIGHT_VISION__REGNAME, true);
            put(RegistryNames.MODULE_THAUM_GOGGLES__REGNAME, true);// done via mod compat

            // Tools --------------------------------------------------------------------------------------
            put(RegistryNames.MODULE_APPENG_EC_WIRELESS_FLUID__REGNAME, true);
            put(RegistryNames.MODULE_APPENG_WIRELESS__REGNAME, true);
            put(RegistryNames.MODULE_AXE__REGNAME, true);
            put(RegistryNames.MODULE_DIAMOND_PICK_UPGRADE__REGNAME, true);
            put(RegistryNames.MODULE_DIMENSIONAL_RIFT__REGNAME, true);
            put(RegistryNames.MODULE_FIELD_TINKER__REGNAME, true);
            put(RegistryNames.MODULE_FLINT_AND_STEEL__REGNAME, true);
            put(RegistryNames.MODULE_GRAFTER__REGNAME, true);
            put(RegistryNames.MODULE_HOE__REGNAME, true);
            put(RegistryNames.MODULE_LEAF_BLOWER__REGNAME, true);
            put(RegistryNames.MODULE_LUX_CAPACITOR__REGNAME, true);
            put(RegistryNames.MODULE_OMNIPROBE__REGNAME, true);
            put(RegistryNames.MODULE_OMNI_WRENCH__REGNAME, true);
            put(RegistryNames.MODULE_ORE_SCANNER__REGNAME, true);
            put(RegistryNames.MODULE_CM_PSD__REGNAME, true);
            put(RegistryNames.MODULE_PICKAXE__REGNAME, true);
            put(RegistryNames.MODULE_PORTABLE_CRAFTING__REGNAME, true);
            put(RegistryNames.MODULE_REF_STOR_WIRELESS__REGNAME, true);
            put(RegistryNames.MODULE_SCOOP__REGNAME, true);
            put(RegistryNames.MODULE_SHEARS__REGNAME, true);
            put(RegistryNames.MODULE_SHOVEL__REGNAME, true);
            put(RegistryNames.MODULE_TREETAP__REGNAME, true);

            // Mining Enhancements ------------------------------------------------------------------------
            put(RegistryNames.MODULE_AOE_PICK_UPGRADE__REGNAME, true);
            put(RegistryNames.MODULE_AQUA_AFFINITY__REGNAME, true);
            put(RegistryNames.MODULE_FORTUNE_REGNAME, false);
            put(RegistryNames.MODULE_MAD__REGNAME, true);
            put(RegistryNames.MODULE_SILK_TOUCH__REGNAME, true);

            // Weapons ------------------------------------------------------------------------------------
            put(RegistryNames.MODULE_BLADE_LAUNCHER__REGNAME, true);
            put(RegistryNames.MODULE_LIGHTNING__REGNAME, true);
            put(RegistryNames.MODULE_MELEE_ASSIST__REGNAME, true);
            put(RegistryNames.MODULE_PLASMA_CANNON__REGNAME, true);
            put(RegistryNames.MODULE_RAILGUN__REGNAME, true);
        }};

        @Config.LangKey(Constants.CONFIG_MODULE_PROPERTY_DOUBLES)
        @Config.Comment("Value of specified property")
        public Map<String, Double> propertyDouble = new HashMap<String, Double>() {{
            put( "advSolarGenerator.daytimeEnergyGen.base", 45000.0D );
            put( "advSolarGenerator.daytimeHeatGen.base", 15.0D );
            put( "advSolarGenerator.nightTimeEnergyGen.base", 1500.0D );
            put( "advSolarGenerator.nightTimeHeatGen.base", 5.0D );
            put( "advSolarGenerator.slotPoints.base", 1.0D );
            put( "advancedBattery.maxEnergy.base", 5000000.0D );
            put( "advancedBattery.slotPoints.base", 1.0D );
            put( "advancedCoolingSystem.advCoolSysEnergyCon.advancedCoolingPower.multiplier", 160.0D );
            put( "advancedCoolingSystem.coolingBonus.advancedCoolingPower.multiplier", 7.0D );
            put( "advancedCoolingSystem.slotPoints.base", 1.0D );
            put( "aoePickUpgrade.aoeEnergyCon.base", 500.0D );
            put( "aoePickUpgrade.aoeEnergyCon.diameter.multiplier", 9500.0D );
            put( "aoePickUpgrade.aoeMiningDiameter.diameter.multiplier", 5D);
            put( "aoePickUpgrade.slotPoints.base", 1.0D );
            put( "apiaristArmor.apiaristArmorEnergyCon.base", 100.0D );
            put( "apiaristArmor.slotPoints.base", 1.0D );
            put( "appengECWirelessFluid.slotPoints.base", 1.0D );
            put( "appengWireless.slotPoints.base", 1.0D );
            put( "aquaAffinity.slotPoints.base", 1.0D );
            put( "aquaAffinity.underWaterEnergyCon.base", 0.0D );
            put( "aquaAffinity.underWaterEnergyCon.power.multiplier", 1000.0D );
            put( "aquaAffinity.underWaterHarvSpeed.base", 0.2D );
            put( "aquaAffinity.underWaterHarvSpeed.power.multiplier", 0.8D );
            put( "aurameter.slotPoints.base", 1.0D );
            put( "autoFeeder.autoFeederEfficiency.base", 50.0D );
            put( "autoFeeder.autoFeederEfficiency.efficiency.multiplier", 50.0D );
            put( "autoFeeder.eatingEnergyCon.base", 100.0D );
            put( "autoFeeder.eatingEnergyCon.efficiency.multiplier", 1000.0D );
            put( "autoFeeder.slotPoints.base", 1.0D );
            put( "axe.axeEnergyCon.base", 500.0D );
            put( "axe.axeEnergyCon.overclock.multiplier", 9500.0D );
            put( "axe.axeHarvSpd.base", 8.0D );
            put( "axe.axeHarvSpd.overclock.multiplier", 22.0D );
            put( "axe.slotPoints.base", 1.0D );
            put( "basicBattery.maxEnergy.base", 1000000.0D );
            put( "basicBattery.slotPoints.base", 1.0D );
            put( "basicCoolingSystem.coolingBonus.basicCoolingPower.multiplier", 4.0D );
            put( "basicCoolingSystem.coolingSystemEnergyCon.basicCoolingPower.multiplier", 100.0D );
            put( "basicCoolingSystem.slotPoints.base", 1.0D );
            put( "binoculars.fieldOfView.base", 0.5D );
            put( "binoculars.fieldOfView.fOVMult.multiplier", 9.5D );
            put( "binoculars.slotPoints.base", 1.0D );
            put( "bladeLauncher.slotPoints.base", 1.0D );
            put( "bladeLauncher.spinBladeDam.base", 6.0D );
            put( "bladeLauncher.spinBladeEnergyCon.base", 5000.0D );
            put( "blinkDrive.blinkDriveEnergyCon.base", 10000.0D );
            put( "blinkDrive.blinkDriveEnergyCon.range.multiplier", 30000.0D );
            put( "blinkDrive.blinkDriveRange.base", 5.0D );
            put( "blinkDrive.blinkDriveRange.range.multiplier", 59.0D );
            put( "blinkDrive.slotPoints.base", 1.0D );
            put( "climbAssist.slotPoints.base", 1.0D );
            put( "clock.slotPoints.base", 1.0D );
            put( "compass.slotPoints.base", 1.0D );
            put( "diamondPickUpgrade.slotPoints.base", 1.0D );
            put( "diamondPlating.armorPhysical.base", 5.0D );
            put( "diamondPlating.maxHeat.base", 400.0D );
            put( "diamondPlating.slotPoints.base", 1.0D );
            put( "dimRiftGen.energyCon.base", 200000.0D );
            put( "dimRiftGen.heatGeneration.base", 55.0D );
            put( "dimRiftGen.slotPoints.base", 1.0D );
            put( "eliteBattery.maxEnergy.base", 5.0E7D );
            put( "eliteBattery.slotPoints.base", 1.0D );
            put( "energyShield.armorEnergy.fieldStrength.multiplier", 6.0D );
            put( "energyShield.armorEnergyPerDamage.fieldStrength.multiplier", 5000.0D );
            put( "energyShield.maxHeat.fieldStrength.multiplier", 500.0D );
            put( "energyShield.slotPoints.base", 1.0D );
            put( "fieldTinkerer.slotPoints.base", 1.0D );
            put( "flightControl.slotPoints.base", 1.0D );
            put( "flightControl.yLookRatio.vertically.multiplier", 1.0D );
            put( "flintAndSteel.ignitEnergyCon.base", 10000.0D );
            put( "flintAndSteel.slotPoints.base", 1.0D );
            put( "fortuneModule.fortuneEnCon.base", 500.0D );
            put( "fortuneModule.fortuneEnCon.enchLevel.multiplier", 9500.0D );
            put( "fortuneModule.fortuneLevel.enchLevel.multiplier", 3D);
            put( "fortuneModule.slotPoints.base", 1.0D );
            put( "glider.slotPoints.base", 1.0D );
            put( "grafter.grafterEnergyCon.base", 10000.0D );
            put( "grafter.grafterHeatGen.base", 20.0D );
            put( "grafter.slotPoints.base", 1.0D );
            put( "hazmat.slotPoints.base", 1.0D );
            put( "hoe.hoeEnergyCon.base", 500.0D );
            put( "hoe.hoeEnergyCon.radius.multiplier", 9500.0D );
            put( "hoe.hoeSearchRad.radius.multiplier", 8.0D );
            put( "hoe.slotPoints.base", 1.0D );
            put( "invisibility.slotPoints.base", 1.0D );
            put( "ironPlating.armorPhysical.base", 4.0D );
            put( "ironPlating.maxHeat.base", 300.0D );
            put( "ironPlating.slotPoints.base", 1.0D );
            put( "jetBoots.jetBootsEnergyCon.base", 0.0D );
            put( "jetBoots.jetBootsEnergyCon.thrust.multiplier", 750.0D );
            put( "jetBoots.jetbootsThrust.base", 0.0D );
            put( "jetBoots.jetbootsThrust.thrust.multiplier", 0.08D );
            put( "jetBoots.slotPoints.base", 1.0D );
            put( "jetpack.jetpackEnergyCon.base", 0.0D );
            put( "jetpack.jetpackEnergyCon.thrust.multiplier", 1500.0D );
            put( "jetpack.jetpackThrust.base", 0.0D );
            put( "jetpack.jetpackThrust.thrust.multiplier", 0.16D );
            put( "jetpack.slotPoints.base", 1.0D );
            put( "jumpAssist.jumpBoost.base", 1.0D );
            put( "jumpAssist.jumpBoost.power.multiplier", 4.0D );
            put( "jumpAssist.jumpEnergyCon.base", 0.0D );
            put( "jumpAssist.jumpEnergyCon.compensation.multiplier", 50.0D );
            put( "jumpAssist.jumpEnergyCon.power.multiplier", 250.0D );
            put( "jumpAssist.jumpExhaustComp.base", 0.0D );
            put( "jumpAssist.jumpExhaustComp.compensation.multiplier", 1.0D );
            put( "jumpAssist.slotPoints.base", 1.0D );
            put( "kineticGenerator.energyPerBlock.base", 2000.0D );
            put( "kineticGenerator.energyPerBlock.energyGenerated.multiplier", 6000.0D );
            put( "kineticGenerator.movementResistance.base", 0.0D );
            put( "kineticGenerator.movementResistance.energyGenerated.multiplier", 0.5D );
            put( "kineticGenerator.slotPoints.base", 1.0D );
            put( "leafBlower.energyCon.base", 500.0D );
            put( "leafBlower.energyCon.radius.multiplier", 9500.0D );
            put( "leafBlower.radius.base", 1.0D );
            put( "leafBlower.radius.radius.multiplier", 15.0D );
            put( "leafBlower.slotPoints.base", 1.0D );
            put( "leatherPlating.armorPhysical.base", 3.0D );
            put( "leatherPlating.maxHeat.base", 75.0D );
            put( "leatherPlating.slotPoints.base", 1.0D );
            put( "lightningSummoner.energyCon.base", 4900000.0D );
            put( "lightningSummoner.heatEmission.base", 100.0D );
            put( "lightningSummoner.slotPoints.base", 1.0D );
            put( "luxCapacitor.luxCapBlue.blue.multiplier", 1.0D );
            put( "luxCapacitor.luxCapEnergyCon.base", 1000.0D );
            put( "luxCapacitor.luxCapGreen.green.multiplier", 1.0D );
            put( "luxCapacitor.luxCapRed.red.multiplier", 1.0D );
            put( "luxCapacitor.slotPoints.base", 1.0D );
            put( "madModule.energyCon.base", 100.0D );
            put( "madModule.slotPoints.base", 1.0D );
            put( "magnet.energyCon.base", 0.0D );
            put( "magnet.energyCon.power.multiplier", 2000.0D );
            put( "magnet.magnetRadius.base", 5.0D );
            put( "magnet.magnetRadius.power.multiplier", 10.0D );
            put( "magnet.slotPoints.base", 1.0D );
            put( "meleeAssist.meleeDamage.base", 2.0D );
            put( "meleeAssist.meleeDamage.impact.multiplier", 8.0D );
            put( "meleeAssist.meleeKnockback.carryThrough.multiplier", 1.0D );
            put( "meleeAssist.punchEnergyCon.base", 10.0D );
            put( "meleeAssist.punchEnergyCon.carryThrough.multiplier", 200.0D );
            put( "meleeAssist.punchEnergyCon.impact.multiplier", 1000.0D );
            put( "meleeAssist.slotPoints.base", 1.0D );
            put( "mobRepulsor.repulsorEnergyCon.base", 2500.0D );
            put( "mobRepulsor.slotPoints.base", 1.0D );
            put( "nightVision.slotPoints.base", 1.0D );
            put( "omniwrench.slotPoints.base", 1.0D );
            put( "oreScanner.slotPoints.base", 1.0D );
            put( "parachute.slotPoints.base", 1.0D );
            put( "pickaxe.pickHarvSpd.base", 8.0D );
            put( "pickaxe.pickHarvSpd.overclock.multiplier", 52.0D );
            put( "pickaxe.pickaxeEnergyCon.base", 500.0D );
            put( "pickaxe.pickaxeEnergyCon.overclock.multiplier", 9500.0D );
            put( "pickaxe.slotPoints.base", 1.0D );
            put( "plasmaCannon.plasmaDamage.amperage.multiplier", 38.0D );
            put( "plasmaCannon.plasmaDamage.base", 2.0D );
            put( "plasmaCannon.plasmaEnergyPerTick.amperage.multiplier", 1500.0D );
            put( "plasmaCannon.plasmaEnergyPerTick.base", 100.0D );
            put( "plasmaCannon.plasmaEnergyPerTick.voltage.multiplier", 500.0D );
            put( "plasmaCannon.plasmaExplosiveness.voltage.multiplier", 0.5D );
            put( "plasmaCannon.slotPoints.base", 1.0D );
            put( "portableCraftingTable.slotPoints.base", 1.0D );
            put( "railgun.railgunEnergyCost;.base", 5000.0D );
            put( "railgun.railgunEnergyCost;.voltage.multiplier", 25000.0D );
            put( "railgun.railgunHeatEm.base", 2.0D );
            put( "railgun.railgunHeatEm.voltage.multiplier", 10.0D );
            put( "railgun.railgunTotalImpulse.base", 500.0D );
            put( "railgun.railgunTotalImpulse.voltage.multiplier", 2500.0D );
            put( "railgun.slotPoints.base", 1.0D );
            put( "refinedStorageWirelessGrid.slotPoints.base", 1.0D );
            put( "scoop.scoopEnergyCon.base", 20000.0D );
            put( "scoop.scoopHarSpd.base", 5.0D );
            put( "scoop.slotPoints.base", 1.0D );
            put( "shears.shearEnergyCon.base", 1000.0D );
            put( "shears.shearHarvSpd.base", 8.0D );
            put( "shears.slotPoints.base", 1.0D );
            put( "shockAbsorber.distanceRed.base", 0.0D );
            put( "shockAbsorber.distanceRed.power.multiplier", 10.0D );
            put( "shockAbsorber.impactEnergyCon.base", 0.0D );
            put( "shockAbsorber.impactEnergyCon.power.multiplier", 100.0D );
            put( "shockAbsorber.slotPoints.base", 1.0D );
            put( "shovel.shovelEnergyCon.base", 500.0D );
            put( "shovel.shovelEnergyCon.overclock.multiplier", 9500.0D );
            put( "shovel.shovelHarvSpd.base", 8.0D );
            put( "shovel.shovelHarvSpd.overclock.multiplier", 22.0D );
            put( "shovel.slotPoints.base", 1.0D );
            put( "silk_touch.silkTouchEnCon.base", 2500.0D );
            put( "silk_touch.slotPoints.base", 1.0D );
            put( "solarGenerator.daytimeEnergyGen.base", 15000.0D );
            put( "solarGenerator.nightTimeEnergyGen.base", 1500.0D );
            put( "solarGenerator.slotPoints.base", 1.0D );
            put( "sprintAssist.slotPoints.base", 1.0D );
            put( "sprintAssist.sprintEnergyCon.base", 0.0D );
            put( "sprintAssist.sprintEnergyCon.compensation.multiplier", 20.0D );
            put( "sprintAssist.sprintEnergyCon.sprintAssist.multiplier", 100.0D );
            put( "sprintAssist.sprintExComp.base", 0.0D );
            put( "sprintAssist.sprintExComp.compensation.multiplier", 1.0D );
            put( "sprintAssist.sprintSpeedMult.base", 0.01D );
            put( "sprintAssist.sprintSpeedMult.sprintAssist.multiplier", 2.49D );
            put( "sprintAssist.walkingEnergyCon.base", 0.0D );
            put( "sprintAssist.walkingEnergyCon.walkingAssist.multiplier", 100.0D );
            put( "sprintAssist.walkingSpeedMult.base", 0.01D );
            put( "sprintAssist.walkingSpeedMult.walkingAssist.multiplier", 1.99D );
            put( "swimAssist.slotPoints.base", 1.0D );
            put( "swimAssist.swimBoostEnergyCon.thrust.multiplier", 1000.0D );
            put( "swimAssist.underwaterMovBoost.thrust.multiplier", 1.0D );
            put( "thermalGenerator.slotPoints.base", 1.0D );
            put( "thermalGenerator.thermalEnergyGen.base", 250.0D );
            put( "thermalGenerator.thermalEnergyGen.energyGenerated.multiplier", 250.0D );
            put( "transparentArmor.slotPoints.base", 1.0D );
            put( "treetap.energyCon.base", 1000.0D );
            put( "treetap.slotPoints.base", 1.0D );
            put( "ultimateBattery.maxEnergy.base", 1.0E8D );
            put( "ultimateBattery.slotPoints.base", 1.0D );
            put( "waterElectrolyzer.joltEnergy.base", 10000.0D );
            put( "waterElectrolyzer.slotPoints.base", 1.0D );
        }};

        @Config.LangKey(Constants.CONFIG_MODULE_PROPERTY_INTEGERS)
        @Config.Comment("Value of specified property")
        public Map<String, Integer> propertyInteger = new HashMap<String, Integer>() {{

        }};
    }

    public static Cosmetics cosmetics = new Cosmetics();
    public static class Cosmetics {
        @Config.Comment("Use legacy cosmetic configuration instead of cosmetic presets")
        public boolean useLegacyCosmeticSystem=true;

        //        @Config.LangKey(MPSConstants.CONFIG_GENERAL_ALLOW_CONFLICTING_KEYBINDS)
        @Config.Comment("Allow high polly armor models instead of just skins")
        public boolean allowHighPollyArmorModuels = true;

        @Config.Comment("Allow PowerFist model to be customized")
        public boolean allowPowerFistCustomization=false;

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

        @Config.Ignore
        private BiMap<String, NBTTagCompound> cosmeticPresetsPowerFist = HashBiMap.create();
        public BiMap<String, NBTTagCompound> getCosmeticPresetsPowerFist() {
            if (cosmeticPresetsPowerFist.isEmpty() && !allowPowerFistCustomization)
                cosmeticPresetsPowerFist = CosmeticPresetSaveLoad.loadPresetsForItem(Objects.INSTANCE.powerFist, 0);
            return cosmeticPresetsPowerFist;
        }

        @Config.Ignore
        private BiMap<String, NBTTagCompound> cosmeticPresetsPowerArmorHelmet = HashBiMap.create();
        public BiMap<String, NBTTagCompound> getCosmeticPresetsPowerArmorHelmet() {
            if (cosmeticPresetsPowerArmorHelmet.isEmpty() && !useLegacyCosmeticSystem)
                cosmeticPresetsPowerArmorHelmet = CosmeticPresetSaveLoad.loadPresetsForItem(Objects.INSTANCE.powerArmorHead, 0);
            return cosmeticPresetsPowerArmorHelmet;
        }

        @Config.Ignore
        private BiMap<String, NBTTagCompound> cosmeticPresetsPowerArmorChestplate = HashBiMap.create();
        public BiMap<String, NBTTagCompound> getCosmeticPresetsPowerArmorChestplate() {
            if(cosmeticPresetsPowerArmorChestplate.isEmpty() && !useLegacyCosmeticSystem)
                cosmeticPresetsPowerArmorChestplate = CosmeticPresetSaveLoad.loadPresetsForItem(Objects.INSTANCE.powerArmorTorso, 0);
            return cosmeticPresetsPowerArmorChestplate;
        }

        @Config.Ignore
        private BiMap<String, NBTTagCompound> cosmeticPresetsPowerArmorLeggings = HashBiMap.create();
        public BiMap<String, NBTTagCompound> getCosmeticPresetsPowerArmorLeggings() {
            if(cosmeticPresetsPowerArmorLeggings.isEmpty() && !useLegacyCosmeticSystem)
                cosmeticPresetsPowerArmorLeggings = CosmeticPresetSaveLoad.loadPresetsForItem(Objects.INSTANCE.powerArmorLegs, 0);
            return cosmeticPresetsPowerArmorLeggings;
        }

        @Config.Ignore
        private BiMap<String, NBTTagCompound>  cosmeticPresetsPowerArmorBoots = HashBiMap.create();
        public BiMap<String, NBTTagCompound> getCosmeticPresetsPowerArmorBoots() {
            if(cosmeticPresetsPowerArmorBoots.isEmpty() && !useLegacyCosmeticSystem)
                cosmeticPresetsPowerArmorBoots = CosmeticPresetSaveLoad.loadPresetsForItem(Objects.INSTANCE.powerArmorFeet, 0);
            return cosmeticPresetsPowerArmorBoots;
        }
    }
}