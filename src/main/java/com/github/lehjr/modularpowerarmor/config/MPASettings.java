package com.github.lehjr.modularpowerarmor.config;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.Objects;
import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorBoots;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorChestplate;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorHelmet;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorLeggings;
import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
        @Config.LangKey(Constants.CONFIG_HUD_USE_24_HOUR_CLOCK) // fixme move to hud (client only)
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
//            put(ModuleConstants.MODULE_DEBUG, true);

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
            put(RegistryNames.MODULE_ADVANCED_COOLING_SYSTEM__REGNAME, true);
            put(RegistryNames.MODULE_HAZMAT__REGNAME, true);
            put(RegistryNames.MODULE_MOB_REPULSOR__REGNAME, true);
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
            put(RegistryNames.MODULE_SCANNER__REGNAME, true);
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
            put( "advancedcoolingsystem.coolingBonus.power.multiplier", 1.0D );
            put( "advancedcoolingsystem.coolingFactor.base", 2.1D );
            put( "advancedcoolingsystem.energyCon.power.multiplier", 40.0D );
            put( "aoe_pick_upgrade.energyCon.base", 500.0D );
            put( "aoe_pick_upgrade.energyCon.diameter.multiplier", 9500.0D );
            put( "apiarist_armor.armorEnergyPerDamage.base", 100.0D );
            put( "aqua_affinity.energyCon.base", 0.0D );
            put( "aqua_affinity.energyCon.power.multiplier", 1000.0D );
            put( "aqua_affinity.harvSpeed.base", 0.2D );
            put( "aqua_affinity.harvSpeed.power.multiplier", 0.8D );
            put( "auto_feeder.autoFeederEfficiency.base", 50.0D );
            put( "auto_feeder.autoFeederEfficiency.efficiency.multiplier", 50.0D );
            put( "auto_feeder.energyCon.base", 100.0D );
            put( "auto_feeder.energyCon.efficiency.multiplier", 1000.0D );
            put( "axe.energyCon.base", 500.0D );
            put( "axe.energyCon.overclock.multiplier", 9500.0D );
            put( "axe.harvSpeed.base", 8.0D );
            put( "axe.harvSpeed.overclock.multiplier", 22.0D );
            put( "basiccoolingsystem.coolingBonus.coolingPower.multiplier", 4.0D );
            put( "basiccoolingsystem.coolingFactor.base", 1.0D );
            put( "basiccoolingsystem.energyCon.coolingPower.multiplier", 100.0D );
            put( "binoculars.fieldOfView.base", 0.5D );
            put( "binoculars.fieldOfView.fOVMult.multiplier", 9.5D );
            put( "blade_launcher.spinBladeDam.base", 6.0D );
            put( "blade_launcher.spinBladeEnergyCon.base", 5000.0D );
            put( "blink_drive.blinkDriveRange.base", 5.0D );
            put( "blink_drive.blinkDriveRange.range.multiplier", 59.0D );
            put( "blink_drive.energyCon.base", 10000.0D );
            put( "blink_drive.energyCon.range.multiplier", 30000.0D );
            put( "diamond_pick_upgrade.energyCon.base", 500.0D );
            put( "diamond_pick_upgrade.energyCon.overclock.multiplier", 9500.0D );
            put( "diamond_pick_upgrade.harvSpeed.base", 8.0D );
            put( "diamond_pick_upgrade.harvSpeed.overclock.multiplier", 52.0D );
            put( "energy_shield.armorEnergy.fieldStrength.multiplier", 6.0D );
            put( "energy_shield.armorEnergyPerDamage.fieldStrength.multiplier", 5000.0D );
            put( "energy_shield.knockbackResistance.base", 0.25D );
            put( "energy_shield.maxHeat.fieldStrength.multiplier", 500.0D );
            put( "flight_control.yLookRatio.vertically.multiplier", 1.0D );
            put( "flint_and_steel.energyCon.base", 10000.0D );
            put( "fortune.fortuneEnCon.base", 500.0D );
            put( "fortune.fortuneEnCon.enchLevel.multiplier", 9500.0D );
            put( "generator_kinetic.energyPerBlock.base", 2000.0D );
            put( "generator_kinetic.energyPerBlock.energyGenerated.multiplier", 6000.0D );
            put( "generator_kinetic.movementResistance.base", 0.01D );
            put( "generator_kinetic.movementResistance.energyGenerated.multiplier", 0.49D );
            put( "generator_solar.daytimeEnergyGen.base", 15000.0D );
            put( "generator_solar.nightTimeEnergyGen.base", 1500.0D );
            put( "generator_solar_adv.daytimeEnergyGen.base", 45000.0D );
            put( "generator_solar_adv.daytimeHeatGen.base", 15.0D );
            put( "generator_solar_adv.nightTimeEnergyGen.base", 1500.0D );
            put( "generator_solar_adv.nightTimeHeatGen.base", 5.0D );
            put( "generator_thermal.energyPerBlock.base", 250.0D );
            put( "generator_thermal.energyPerBlock.energyGenerated.multiplier", 250.0D );
            put( "glider.energyCon.base", 0.0D );
            put( "glider.energyCon.thrust.multiplier", 750.0D );
            put( "glider.jetbootsThrust.base", 0.0D );
            put( "glider.jetbootsThrust.thrust.multiplier", 0.08D );
            put( "hoe.energyCon.base", 500.0D );
            put( "hoe.energyCon.radius.multiplier", 9500.0D );
            put( "hoe.radius.radius.multiplier", 8.0D );
            put( "jet_boots.energyCon.base", 0.0D );
            put( "jet_boots.energyCon.thrust.multiplier", 750.0D );
            put( "jet_boots.jetbootsThrust.base", 0.0D );
            put( "jet_boots.jetbootsThrust.thrust.multiplier", 0.08D );
            put( "jetpack.energyCon.base", 0.0D );
            put( "jetpack.energyCon.thrust.multiplier", 1500.0D );
            put( "jetpack.jetpackThrust.base", 0.0D );
            put( "jetpack.jetpackThrust.thrust.multiplier", 0.16D );
            put( "jump_assist.energyCon.base", 0.0D );
            put( "jump_assist.energyCon.compensation.multiplier", 50.0D );
            put( "jump_assist.energyCon.power.multiplier", 250.0D );
            put( "jump_assist.muultiplier.base", 1.0D );
            put( "jump_assist.muultiplier.power.multiplier", 4.0D );
            put( "jump_assist.sprintExComp.base", 0.0D );
            put( "jump_assist.sprintExComp.compensation.multiplier", 1.0D );
            put( "leaf_blower.energyCon.base", 500.0D );
            put( "leaf_blower.energyCon.radius.multiplier", 9500.0D );
            put( "leaf_blower.radius.base", 1.0D );
            put( "leaf_blower.radius.radius.multiplier", 15.0D );
            put( "lightning_summoner.energyCon.base", 4900000.0D );
            put( "lightning_summoner.heatEmission.base", 100.0D );
            put( "luxcapacitor_module.blueHue.blue.multiplier", 1.0D );
            put( "luxcapacitor_module.energyCon.base", 1000.0D );
            put( "luxcapacitor_module.greenHue.green.multiplier", 1.0D );
            put( "luxcapacitor_module.opacity.alpha.multiplier", 1.0D );
            put( "luxcapacitor_module.redHue.red.multiplier", 1.0D );
            put( "magnet.energyCon.base", 0.0D );
            put( "magnet.energyCon.power.multiplier", 2000.0D );
            put( "magnet.radius.base", 5.0D );
            put( "magnet.radius.power.multiplier", 10.0D );
            put( "melee_assist.meleeDamage.base", 2.0D );
            put( "melee_assist.meleeDamage.impact.multiplier", 8.0D );
            put( "melee_assist.meleeKnockback.carryThrough.multiplier", 1.0D );
            put( "melee_assist.punchEnergyCon.base", 10.0D );
            put( "melee_assist.punchEnergyCon.carryThrough.multiplier", 200.0D );
            put( "melee_assist.punchEnergyCon.impact.multiplier", 1000.0D );
            put( "mob_repulsor.energyCon.base", 2500.0D );
            put( "omniwrench.energyCon.base", 500.0D );
            put( "omniwrench.energyCon.radius.multiplier", 9500.0D );
            put( "omniwrench.radius.radius.multiplier", 8.0D );
            put( "parachute.energyCon.thrust.multiplier", 1000.0D );
            put( "parachute.underwaterMovBoost.thrust.multiplier", 1.0D );
            put( "pickaxe.energyCon.base", 500.0D );
            put( "pickaxe.energyCon.overclock.multiplier", 9500.0D );
            put( "pickaxe.harvSpeed.base", 8.0D );
            put( "pickaxe.harvSpeed.overclock.multiplier", 52.0D );
            put( "plasma_cannon.plasmaDamage.amperage.multiplier", 38.0D );
            put( "plasma_cannon.plasmaDamage.base", 2.0D );
            put( "plasma_cannon.plasmaEnergyPerTick.amperage.multiplier", 1500.0D );
            put( "plasma_cannon.plasmaEnergyPerTick.base", 100.0D );
            put( "plasma_cannon.plasmaEnergyPerTick.voltage.multiplier", 500.0D );
            put( "plasma_cannon.plasmaExplosiveness.voltage.multiplier", 0.5D );
            put( "plating_diamond.armorPhysical.base", 5.0D );
            put( "plating_diamond.knockbackResistance.base", 0.25D );
            put( "plating_diamond.maxHeat.base", 400.0D );
            put( "plating_iron.armorPhysical.base", 4.0D );
            put( "plating_iron.knockbackResistance.base", 0.25D );
            put( "plating_iron.maxHeat.base", 300.0D );
            put( "plating_leather.armorPhysical.base", 3.0D );
            put( "plating_leather.knockbackResistance.base", 0.25D );
            put( "plating_leather.maxHeat.base", 75.0D );
            put( "railgun.railgunEnergyCost.base", 5000.0D );
            put( "railgun.railgunEnergyCost.voltage.multiplier", 25000.0D );
            put( "railgun.railgunHeatEm.base", 2.0D );
            put( "railgun.railgunHeatEm.voltage.multiplier", 10.0D );
            put( "railgun.railgunTotalImpulse.base", 500.0D );
            put( "railgun.railgunTotalImpulse.voltage.multiplier", 2500.0D );
            put( "scoop.energyCon.base", 20000.0D );
            put( "scoop.harvSpeed.base", 5.0D );
            put( "shears.energyCon.base", 1000.0D );
            put( "shears.harvSpeed.base", 8.0D );
            put( "shock_absorber.energyCon.base", 0.0D );
            put( "shock_absorber.energyCon.power.multiplier", 100.0D );
            put( "shock_absorber.muultiplier.base", 0.0D );
            put( "shock_absorber.muultiplier.power.multiplier", 10.0D );
            put( "shovel.energyCon.base", 500.0D );
            put( "shovel.energyCon.overclock.multiplier", 9500.0D );
            put( "shovel.harvSpeed.base", 8.0D );
            put( "shovel.harvSpeed.overclock.multiplier", 22.0D );
            put( "silk_touch.silkTouchEnCon.base", 2500.0D );
            put( "sprint_assist.sprintEnergyCon.base", 0.0D );
            put( "sprint_assist.sprintEnergyCon.compensation.multiplier", 20.0D );
            put( "sprint_assist.sprintEnergyCon.sprintAssist.multiplier", 100.0D );
            put( "sprint_assist.sprintExComp.base", 0.0D );
            put( "sprint_assist.sprintExComp.compensation.multiplier", 1.0D );
            put( "sprint_assist.sprintSpeedMult.base", 0.01D );
            put( "sprint_assist.sprintSpeedMult.sprintAssist.multiplier", 2.49D );
            put( "sprint_assist.walkingEnergyCon.base", 0.0D );
            put( "sprint_assist.walkingEnergyCon.walkingAssist.multiplier", 100.0D );
            put( "sprint_assist.walkingSpeedMult.base", 0.01D );
            put( "sprint_assist.walkingSpeedMult.walkingAssist.multiplier", 1.99D );
            put( "swim_assist.energyCon.thrust.multiplier", 1000.0D );
            put( "swim_assist.underwaterMovBoost.thrust.multiplier", 1.0D );
            put( "treetap.energyCon.base", 1000.0D );
            put( "water_electrolyzer.energyCon.base", 10000.0D );
        }};

        @Config.LangKey(Constants.CONFIG_MODULE_PROPERTY_INTEGERS)
        @Config.Comment("Value of specified property")
        public Map<String, Integer> propertyInteger = new HashMap<String, Integer>() {{
            put( "advancedcoolingsystem.fluidTankSize.base", 20000);
            put( "aoe_pick_upgrade.aoeMiningDiameter.diameter.multiplier", 5);
            put( "basiccoolingsystem.fluidTankSize.base", 20000);
            put( "battery_advanced.maxEnergy.base", 5000000);
            put( "battery_advanced.maxTransfer.base", 5000000);
            put( "battery_basic.maxEnergy.base", 1000000);
            put( "battery_basic.maxTransfer.base", 1000000);
            put( "battery_elite.maxEnergy.base", 50000000);
            put( "battery_elite.maxTransfer.base", 50000000);
            put( "battery_ultimate.maxEnergy.base", 100000000);
            put( "battery_ultimate.maxTransfer.base", 100000000);
            put( "fortune.fortuneLevel.enchLevel.multiplier", 3);
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