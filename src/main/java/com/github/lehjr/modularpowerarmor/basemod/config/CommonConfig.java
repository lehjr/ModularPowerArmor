package com.github.lehjr.modularpowerarmor.basemod.config;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Common settings for Server and Client. Synced from server to client.
 */
public class CommonConfig {
//    static MPAObjects mpsi = MPAObjects.INSTANCE;
//
//    public static final ServerSettings COMMON_CONFIG;
//    public static final ForgeConfigSpec COMMON_SPEC;
//
//    static {
//        final Pair<ServerSettings, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(ServerSettings::new);
//        COMMON_SPEC = serverSpecPair.getRight();
//        COMMON_CONFIG = serverSpecPair.getLeft();
//    }
//
//    /** Cosmetics ----------------------------------------------------------------------------------------------------- */
//// Note: these are controlled by the server because the legacy settings can create a vast number
////      of NBT Tags for tracking the settings for each individual model part.
//    public static ForgeConfigSpec.BooleanValue
//            COSMETIC_USE_LEGACY_COSMETIC_SYSTEM,
//            COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS,
//            COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN;
//
//    public static ForgeConfigSpec.DoubleValue
//            GENERAL_MAX_FLYING_SPEED,
//            GENERAL_BASE_MAX_HEAT_POWERFIST,
//            GENERAL_BASE_MAX_HEAT_HELMET,
//            GENERAL_BASE_MAX_HEAT_CHEST,
//            GENERAL_BASE_MAX_HEAT_LEGS,
//            GENERAL_BASE_MAX_HEAT_FEET;
//
//    public static double baseMaxHeatHelmet() {
//        return Optional.ofNullable(commonConfig != null? GENERAL_BASE_MAX_HEAT_HELMET.get() : null).orElse(5.0D);
//    }
//
//    public static double baseMaxHeatChest() {
//        return Optional.ofNullable(commonConfig != null? GENERAL_BASE_MAX_HEAT_CHEST.get() : null).orElse(5.0D);
//    }
//
//    public static double baseMaxHeatLegs() {
//        return Optional.ofNullable(commonConfig != null? GENERAL_BASE_MAX_HEAT_LEGS.get() : null).orElse(5.0D);
//    }
//
//    public static double baseMaxHeatFeet() {
//        return Optional.ofNullable(commonConfig != null? GENERAL_BASE_MAX_HEAT_FEET.get() : null).orElse(5.0D);
//    }
//
//    public static double baseMaxHeatPowerFist() {
//        return Optional.ofNullable(commonConfig != null? GENERAL_BASE_MAX_HEAT_POWERFIST.get() : null).orElse(5.0D);
//    }
//
//
//    public static ForgeConfigSpec.BooleanValue
//            RECIPES_USE_VANILLA,
//            RECIPES_USE_THERMAL_EXPANSION,
//            RECIPES_USE_ENDERIO,
//            RECIPES_USE_TECH_REBORN,
//            RECIPES_USE_IC2;
//
//    /**
//     * Settings that are controlled by the server and synced to client
//     */
//    public static class ServerSettings {
//        public ServerSettings(ForgeConfigSpec.Builder builder) {
//            /** Cosmetics --------------------------------------------------------------------------------------------- */
//            builder.comment("Model cosmetic settings").push("Cosmetic");
//
//            COSMETIC_USE_LEGACY_COSMETIC_SYSTEM = builder
//                    .comment("Use legacy cosmetic configuration instead of cosmetic presets")
//                    .translation(Constants.CONFIG_COSMETIC_USE_LEGACY_COSMETIC_SYSTEM)
//                    .worldRestart()
//                    .define("useLegacyCosmeticSystem", true);
//
//            COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS = builder
//                    .comment("Allow high polly armor models instead of just skins")
//                    .translation(Constants.CONFIG_COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS)
//                    .define("allowHighPollyArmorModuels", true);
//
//            COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN = builder
//                    .comment("Allow PowerFist model to be customized")
//                    .translation(Constants.CONFIG_COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN)
//                    .define("allowPowerFistCustomization", true);
//            builder.pop();
//
//            /** General ----------------------------------------------------------------------------------------------- */
//            builder.comment("General settings").push("General");
//            GENERAL_MAX_FLYING_SPEED = builder.comment("Maximum flight speed (in m/s)")
//                    .translation(Constants.CONFIG_GENERAL_MAX_FLYING_SPEED)
//                    .defineInRange("maximumFlyingSpeedmps", 25.0, 0, Double.MAX_VALUE);
//
//            GENERAL_BASE_MAX_HEAT_POWERFIST = builder.comment("PowerFist Base Heat Cap")
//                    .translation(Constants.CONFIG_GENERAL_BASE_MAX_MODULES_POWERFIST)
//                    .defineInRange("baseMaxHeatPowerFist", 5.0, 0, 5000);
//
//            GENERAL_BASE_MAX_HEAT_HELMET = builder.comment("Power Armor Helmet Heat Cap")
//                    .translation(Constants.CONFIG_GENERAL_BASE_MAX_MODULES_HELMET)
//                    .defineInRange("baseMaxHeatHelmet", 5.0, 0, 5000);
//
//            GENERAL_BASE_MAX_HEAT_CHEST = builder.comment("Power Armor Chestplate Heat Cap")
//                    .translation(Constants.CONFIG_GENERAL_BASE_MAX_MODULES_CHESTPLATE)
//                    .defineInRange("baseMaxHeatChest", 20.0, 0, 5000);
//
//            GENERAL_BASE_MAX_HEAT_LEGS = builder.comment("Power Armor Leggings Heat Cap")
//                    .translation(Constants.CONFIG_GENERAL_BASE_MAX_MODULES_LEGGINGS)
//                    .defineInRange("baseMaxHeatLegs", 15.0, 0, 5000);
//
//            GENERAL_BASE_MAX_HEAT_FEET = builder.comment("Power Armor Boots Heat Cap")
//                    .translation(Constants.CONFIG_GENERAL_BASE_MAX_MODULES_FEET)
//                    .defineInRange("baseMaxHeatFeet", 5.0, 0, 5000);
//            builder.pop();
//
//            /** Recipes ----------------------------------------------------------------------------------------------- */
//            builder.comment("Recipe settings").push("Recipes");
//            RECIPES_USE_VANILLA = builder
//                    .comment("Use recipes for Vanilla")
//                    .translation(Constants.CONFIG_RECIPES_USE_VANILLA)
//                    .worldRestart()
//                    .define("useVanillaRecipes", true);
//
//
//            RECIPES_USE_THERMAL_EXPANSION = builder
//                    .comment("Use recipes for Thermal Expansion")
//                    .translation(Constants.CONFIG_RECIPES_USE_THERMAL_EXPANSION)
//                    .worldRestart()
//                    .define("useThermalExpansionRecipes", true);
//
//            RECIPES_USE_ENDERIO = builder
//                    .comment("Use recipes for EnderIO")
//                    .translation(Constants.CONFIG_RECIPES_USE_ENDERIO)
//                    .worldRestart()
//                    .define("useEnderIORecipes", true);
//
//            RECIPES_USE_TECH_REBORN = builder
//                    .comment("Use recipes for TechReborn")
//                    .translation(Constants.CONFIG_RECIPES_USE_TECH_REBORN)
//                    .worldRestart()
//                    .define("useTechRebornRecipes", true);
//
//            RECIPES_USE_IC2 = builder
//                    .comment("Use recipes for IndustrialCraft 2")
//                    .translation(Constants.CONFIG_RECIPES_USE_IC2)
//                    .worldRestart()
//                    .define("useIC2Recipes", true);
//            builder.pop();
//
//            /** Modules --------------------------------------------------------------------------------------------------- */
//            builder.push("Modules");
//            builder.push("Movement");
//            builder.push("jump_assist");
//            builder.defineInRange("base_energyCon", 0.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("energyCon_power_multiplier", 250.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_muultiplier", 1.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("muultiplier_power_multiplier", 4.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("energyCon_compensation_multiplier", 50.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_sprintExComp", 0.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("sprintExComp_compensation_multiplier", 1.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("parachute");
//            builder.defineInRange("energyCon_thrust_multiplier", 1000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("underwaterMovBoost_thrust_multiplier", 1.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("swim_assist");
//            builder.defineInRange("energyCon_thrust_multiplier", 1000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("underwaterMovBoost_thrust_multiplier", 1.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("sprint_assist");
//            builder.defineInRange("base_sprintEnergyCon", 0.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("sprintEnergyCon_sprintAssist_multiplier", 100.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_sprintSpeedMult", 0.01D, 0, 1.7976931348623157E308);
//            builder.defineInRange("sprintSpeedMult_sprintAssist_multiplier", 2.49D, 0, 1.7976931348623157E308);
//            builder.defineInRange("sprintEnergyCon_compensation_multiplier", 20.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_sprintExComp", 0.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("sprintExComp_compensation_multiplier", 1.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_walkingEnergyCon", 0.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("walkingEnergyCon_walkingAssist_multiplier", 100.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_walkingSpeedMult", 0.01D, 0, 1.7976931348623157E308);
//            builder.defineInRange("walkingSpeedMult_walkingAssist_multiplier", 1.99D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("jetpack");
//            builder.defineInRange("base_energyCon", 0.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_jetpackThrust", 0.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("energyCon_thrust_multiplier", 1500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("jetpackThrust_thrust_multiplier", 0.16D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("shock_absorber");
//            builder.defineInRange("base_energyCon", 0.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("energyCon_power_multiplier", 100.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_muultiplier", 0.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("muultiplier_power_multiplier", 10.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("flight_control");
//            builder.defineInRange("yLookRatio_vertically_multiplier", 1.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.pop();
//            builder.push("glider");
//            builder.defineInRange("base_energyCon", 0.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_jetbootsThrust", 0.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("energyCon_thrust_multiplier", 750.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("jetbootsThrust_thrust_multiplier", 0.08D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("jet_boots");
//            builder.defineInRange("base_energyCon", 0.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_jetbootsThrust", 0.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("energyCon_thrust_multiplier", 750.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("jetbootsThrust_thrust_multiplier", 0.08D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("blink_drive");
//            builder.defineInRange("base_energyCon", 10000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_blinkDriveRange", 5.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("energyCon_range_multiplier", 30000.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("blinkDriveRange_range_multiplier", 59.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.pop();
//            builder.push("Armor");
//            builder.push("plating_leather");
//            builder.defineInRange("base_armorPhysical", 3.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_maxHeat", 75.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("energy_shield");
//            builder.defineInRange("armorEnergy_fieldStrength_multiplier", 6.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("armorEnergyPerDamage_fieldStrength_multiplier", 5000.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("maxHeat_fieldStrength_multiplier", 500.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("plating_iron");
//            builder.defineInRange("base_armorPhysical", 4.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_maxHeat", 300.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("plating_diamond");
//            builder.defineInRange("base_armorPhysical", 5.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_maxHeat", 400.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.pop();
//            builder.push("Energy Storage");
//            builder.push("battery_elite");
//            builder.defineInRange("base_maxEnergy", 50000000, 0, 2147483647);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_maxTransfer", 50000000, 0, 2147483647);
//            builder.pop();
//            builder.push("battery_ultimate");
//            builder.defineInRange("base_maxEnergy", 100000000, 0, 2147483647);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_maxTransfer", 100000000, 0, 2147483647);
//            builder.pop();
//            builder.push("battery_basic");
//            builder.defineInRange("base_maxEnergy", 1000000, 0, 2147483647);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_maxTransfer", 1000000, 0, 2147483647);
//            builder.pop();
//            builder.push("battery_advanced");
//            builder.defineInRange("base_maxEnergy", 5000000, 0, 2147483647);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_maxTransfer", 5000000, 0, 2147483647);
//            builder.pop();
//            builder.pop();
//            builder.push("Vision");
//            builder.push("binoculars");
//            builder.defineInRange("base_fieldOfView", 0.5D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("fieldOfView_fOVMult_multiplier", 9.5D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.pop();
//            builder.push("Special");
//            builder.push("magnet");
//            builder.defineInRange("base_energyCon", 0.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("energyCon_power_multiplier", 2000.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_radius", 5.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("radius_power_multiplier", 10.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.pop();
//            builder.push("Environment");
//            builder.push("fluid_tank");
//            builder.defineInRange("base_fluidTankSize", 20000, 0, 2147483647);
//            builder.define("isAllowed", true);
//            builder.pop();
//            builder.push("auto_feeder");
//            builder.defineInRange("base_energyCon", 100.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_autoFeederEfficiency", 50.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("energyCon_efficiency_multiplier", 1000.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("autoFeederEfficiency_efficiency_multiplier", 50.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("water_electrolyzer");
//            builder.defineInRange("base_energyCon", 10000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.pop();
//            builder.push("mob_repulsor");
//            builder.defineInRange("base_energyCon", 2500.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.pop();
//            builder.pop();
//            builder.push("Energy Generation");
//            builder.push("generator_kinetic");
//            builder.defineInRange("base_energyPerBlock", 2000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("energyPerBlock_energyGenerated_multiplier", 6000.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_movementResistance", 0.01D, 0, 1.7976931348623157E308);
//            builder.defineInRange("movementResistance_energyGenerated_multiplier", 0.49D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("generator_solar");
//            builder.defineInRange("base_daytimeEnergyGen", 15000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_nightTimeEnergyGen", 1500.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("generator_solar_adv");
//            builder.defineInRange("base_daytimeEnergyGen", 45000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_nightTimeEnergyGen", 1500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_daytimeHeatGen", 15.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_nightTimeHeatGen", 5.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("generator_thermal");
//            builder.defineInRange("base_energyPerBlock", 250.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("energyPerBlock_energyGenerated_multiplier", 250.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.pop();
//            builder.push("Weapon");
//            builder.push("plasma_cannon");
//            builder.defineInRange("base_plasmaEnergyPerTick", 100.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_plasmaDamage", 2.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("plasmaEnergyPerTick_amperage_multiplier", 1500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("plasmaDamage_amperage_multiplier", 38.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("plasmaEnergyPerTick_voltage_multiplier", 500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("plasmaExplosiveness_voltage_multiplier", 0.5D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("melee_assist");
//            builder.defineInRange("base_punchEnergyCon", 10.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_meleeDamage", 2.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("punchEnergyCon_impact_multiplier", 1000.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("meleeDamage_impact_multiplier", 8.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("punchEnergyCon_carryThrough_multiplier", 200.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("meleeKnockback_carryThrough_multiplier", 1.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("blade_launcher");
//            builder.defineInRange("base_spinBladeEnergyCon", 5000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_spinBladeDam", 6.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("railgun");
//            builder.defineInRange("base_railgunTotalImpulse", 500.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_railgunEnergyCost", 5000.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_railgunHeatEm", 2.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("railgunTotalImpulse_voltage_multiplier", 2500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("railgunEnergyCost_voltage_multiplier", 25000.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("railgunHeatEm_voltage_multiplier", 10.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("lightning_summoner");
//            builder.defineInRange("base_energyCon", 4900000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_heatEmission", 100.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.pop();
//            builder.push("Mining Enhancement");
//            builder.push("aoe_pick_upgrade");
//            builder.defineInRange("base_energyCon", 500.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("energyCon_diameter_multiplier", 9500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("aoeMiningDiameter_diameter_multiplier", 5, 0, 2147483647);
//            builder.pop();
//            builder.push("fortune");
//            builder.defineInRange("base_fortuneEnCon", 500.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("fortuneEnCon_enchLevel_multiplier", 9500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("fortuneLevel_enchLevel_multiplier", 3, 0, 2147483647);
//            builder.pop();
//            builder.push("silk_touch");
//            builder.defineInRange("base_silkTouchEnCon", 2500.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.pop();
//            builder.push("aqua_affinity");
//            builder.defineInRange("base_energyCon", 0.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_harvSpeed", 0.2D, 0, 1.7976931348623157E308);
//            builder.defineInRange("energyCon_power_multiplier", 1000.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("harvSpeed_power_multiplier", 0.8D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.pop();
//            builder.push("Tool");
//            builder.push("luxcapacitor_module");
//            builder.defineInRange("base_energyCon", 1000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("redHue_red_multiplier", 1.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("greenHue_green_multiplier", 1.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("blueHue_blue_multiplier", 1.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("opacity_alpha_multiplier", 1.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("shears");
//            builder.defineInRange("base_energyCon", 1000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_harvSpeed", 8.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("diamond_pick_upgrade");
//            builder.defineInRange("base_energyCon", 500.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_harvSpeed", 8.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("energyCon_overclock_multiplier", 9500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("harvSpeed_overclock_multiplier", 52.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("pickaxe");
//            builder.defineInRange("base_energyCon", 500.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_harvSpeed", 8.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("energyCon_overclock_multiplier", 9500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("harvSpeed_overclock_multiplier", 52.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("shovel");
//            builder.defineInRange("base_energyCon", 500.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_harvSpeed", 8.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("energyCon_overclock_multiplier", 9500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("harvSpeed_overclock_multiplier", 22.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("flint_and_steel");
//            builder.defineInRange("base_energyCon", 10000.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.pop();
//            builder.push("leaf_blower");
//            builder.defineInRange("base_energyCon", 500.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("energyCon_radius_multiplier", 9500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("base_radius", 1.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("radius_radius_multiplier", 15.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("axe");
//            builder.defineInRange("base_energyCon", 500.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("base_harvSpeed", 8.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("energyCon_overclock_multiplier", 9500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("harvSpeed_overclock_multiplier", 22.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.push("hoe");
//            builder.defineInRange("base_energyCon", 500.0D, 0, 1.7976931348623157E308);
//            builder.define("isAllowed", true);
//            builder.defineInRange("energyCon_radius_multiplier", 9500.0D, 0, 1.7976931348623157E308);
//            builder.defineInRange("radius_radius_multiplier", 8.0D, 0, 1.7976931348623157E308);
//            builder.pop();
//            builder.pop();
//            builder.pop();
//
//
//        }
//    }
//
//    public static void finishBuilder() {
//        moduleConfig.finishBuilder();
//    }
//
//    public static void setLoadingDone() {
//        moduleConfig.isLoading = false;
//    }


    /**
     * The way this will work when finished...
     *
     * The "commonConfig" will allow softcoded access to the config values commonConfig.getConfigData().get(path)
     * Until initialized, use  return Optional.ofNullable(commonConfigNotNull ? useEnergy.get() : null).orElse(true);
     *
     */
//    public static ModConfig commonConfig;
    public static ModuleConfig moduleConfig = new ModuleConfig();
    static class ModuleConfig implements IConfig {
        static boolean isDevMode = false;
        /**
         * FIXME: might be better with categories as keys with a value as a map of module name and ArrayList<String> of entries
         *  this way these don't get duplicate catagory entries in the output file
         */
        Map<String, Map<String, ArrayList<String>>> outputMap = new HashMap<>();
        void addtoMap(String category, String moduleName, String entry) {

            Map<String, ArrayList<String>> modulesForCategory;
            ArrayList<String> moduleSettings;


            // check if the category is already in the map
            if (outputMap.containsKey(category)) {
                modulesForCategory = outputMap.get(category);
                if(modulesForCategory.containsKey(moduleName)) {
                    moduleSettings = modulesForCategory.get(moduleName);
                    if (moduleSettings.contains(entry)) {
                        return;
                    }
                } else {
                    moduleSettings = new ArrayList<>();
                }
            } else {
                modulesForCategory = new HashMap<>();
                moduleSettings = new ArrayList<>();
            }

            moduleSettings.add(entry);
            modulesForCategory.put(moduleName, moduleSettings);

            outputMap.put(category, modulesForCategory);
        }

        public boolean isLoading = true;
        void startNewBuilder() {

        }

        // once the builder has been built, it cannot be changed.
        public void finishBuilder() {
            if (isLoading)
                return;
            System.out.println("MODULE MAP SET SIZE: " + outputMap.size());
            StringBuilder outString = new StringBuilder("builder.push(\"Modules\");\n");

            for (Map.Entry<String, Map<String, ArrayList<String>>> categoryMapEntry : outputMap.entrySet()) {
                String moduleCategory = categoryMapEntry.getKey();
                outString.append("builder.push(\"").append(moduleCategory).append("\");\n");

                Map<String, ArrayList<String>> moduleMapEntry = categoryMapEntry.getValue();

                for (Map.Entry<String, ArrayList<String>> entry: moduleMapEntry.entrySet()) {
                    String moduleName = entry.getKey();
                    ArrayList<String> moduleSettings = entry.getValue();

                    outString.append("builder.push(\"").append(moduleName).append("\");\n");
                    for (String moduleLine : moduleSettings) {
                        outString.append(moduleLine);
                    }
                    outString.append("builder.pop();\n");
                }
                outString.append("builder.pop();\n");

            }
            outString.append("builder.pop();\n");
            try {
                FileUtils.writeStringToFile(ConfigHelper.setupConfigFile("missingConfigs.txt"), outString.toString(), Charset.defaultCharset(), false);
            } catch (Exception e) {

            }
        }

        @Override
        public double getBasePropertyDoubleOrDefault(
                EnumModuleCategory category,
                @Nonnull ItemStack module,
                String propertyName, double baseVal) {

            String moduleName = itemTranslationKeyToConfigKey(module.getTranslationKey());
            String entry = "base_" + propertyName;
            if (isDevMode) {
                addtoMap(category.getName(),
                        moduleName,
                        new StringBuilder("builder.defineInRange(\"")
                                .append(entry).append("\", ")
                                .append(baseVal).append("D, ")
                                .append(0).append(", ")
                                .append(Double.MAX_VALUE)
                                .append(");\n").toString());
                boolean isAllowed = isModuleAllowed(category, module);
            } else {
                ArrayList<String> key = new ArrayList<String>() {{
                    add("Modules");
                    add(category.getName());
                    add(moduleName);
                    add(entry);
                }};

                if (commonConfig != null && commonConfig.getConfigData().contains(key)) {
//                    System.out.println("common config value: " + commonConfig.getConfigData().get(key));
                    return commonConfig.getConfigData().get(key);
                }
            }
            return baseVal;
        }

        @Override
        public double getTradeoffPropertyDoubleOrDefault(
                EnumModuleCategory category,
                @Nonnull ItemStack module,
                String tradeoffName,
                String propertyName,
                double multiplier) {

            String moduleName = itemTranslationKeyToConfigKey(module.getTranslationKey());
            String entry = propertyName + "_" + tradeoffName + "_multiplier";

            if (isDevMode) {
                addtoMap(category.getName(),
                        moduleName,
                        new StringBuilder("builder.defineInRange(\"")
                                .append(entry).append("\", ")
                                .append(multiplier).append("D, ")
                                .append(0).append(", ")
                                .append(Double.MAX_VALUE)
                                .append(");\n").toString());
                boolean isAllowed = isModuleAllowed(category, module);
            } else {
                ArrayList<String> key = new ArrayList<String>() {{
                    add("Modules");
                    add(category.getName().replace(" ", "_"));
                    add(moduleName);
                    add(entry);
                }};
                if (commonConfig != null && commonConfig.getConfigData().contains(key)) {
//                    System.out.println("common config value: " + commonConfig.getConfigData().get(key));
                    return commonConfig.getConfigData().get(key);
                }
            }

            return multiplier;
        }

        @Override
        public int getBasePropertIntegerOrDefault(EnumModuleCategory category, @Nonnull ItemStack module, String propertyName, int baseVal) {
            String moduleName = itemTranslationKeyToConfigKey(module.getTranslationKey());
            String entry = "base_" + propertyName;

            if (isDevMode) {
                addtoMap(category.getName(),
                        moduleName,
                        new StringBuilder("builder.defineInRange(\"")
                                .append(entry).append("\", ")
                                .append(baseVal).append(", ")
                                .append(0).append(", ")
                                .append(Integer.MAX_VALUE)
                                .append(");\n").toString());
                boolean isAllowed = isModuleAllowed(category, module);
            } else {
                ArrayList<String> key = new ArrayList<String>() {{
                    add("Modules");
                    add(category.getName());
                    add(moduleName);
                    add(entry);
                }};
                if (commonConfig != null && commonConfig.getConfigData().contains(key)) {
//                    System.out.println("common config value: " + commonConfig.getConfigData().get(key));
                    return commonConfig.getConfigData().get(key);
                }
            }
            return baseVal;
        }

        @Override
        public int getTradeoffPropertyIntegerOrDefault(EnumModuleCategory category, @Nonnull ItemStack module, String tradeoffName, String propertyName, int multiplier) {
            String moduleName = itemTranslationKeyToConfigKey(module.getTranslationKey());
            String entry = propertyName + "_" + tradeoffName + "_multiplier";


            if (isDevMode) {
                addtoMap(category.getName(),
                        moduleName,
                        new StringBuilder("builder.defineInRange(\"")
                                .append(entry).append("\", ")
                                .append(multiplier).append(", ")
                                .append(0).append(", ")
                                .append(Integer.MAX_VALUE)
                                .append(");\n").toString());
                boolean isAllowed = isModuleAllowed(category, module);
            } else {
                ArrayList<String> key = new ArrayList<String>() {{
                    add("Modules");
                    add(category.getName());
                    add(moduleName);
                    add(entry);
                }};
                if (commonConfig != null && commonConfig.getConfigData().contains(key)) {
//                    System.out.println("common config value: " + commonConfig.getConfigData().get(key));
                    return commonConfig.getConfigData().get(key);
                }
            }
            return multiplier;
        }

        @Override
        public boolean isModuleAllowed(EnumModuleCategory category, @Nonnull ItemStack module) {
            String moduleName = itemTranslationKeyToConfigKey(module.getTranslationKey());
            String entry = "isAllowed";

            if (isDevMode) {
                addtoMap(category.getName(), moduleName, new StringBuilder("builder.define(\"").append(entry).append("\", true);\n").toString());
            } else {
                ArrayList<String> key = new ArrayList<String>() {{
                    add("Modules");
                    add(category.getName());
                    add(moduleName);
                    add(entry);
                }};

                if (commonConfig != null && commonConfig.getConfigData().contains(key)) {
//                    System.out.println("common config value: " + commonConfig.getConfigData().get(key));
                    return commonConfig.getConfigData().get(key);
                }
            }

            return true;
        }

        // drop the prefix for MPS modules and replace "dots" with underscores
        final String itemPrefix = "item." + Constants.MODID + ".";
        String itemTranslationKeyToConfigKey(String translationKey) {
            if (translationKey.startsWith(itemPrefix )){
                translationKey = translationKey.substring(itemPrefix .length());
            }
            return translationKey.replace(".", "_");
        }
    }

//    public static NBTTagCompound getPresetNBTFor(@Nonnull ItemStack itemStack, String presetName) {
//        Map<String, NBTTagCompound> map = getCosmeticPresets(itemStack);
//        return map.get(presetName);
//    }
//
//    public static BiMap<String, NBTTagCompound> getCosmeticPresets(@Nonnull ItemStack itemStack) {
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
//    public void updateCosmeticInfo(ResourceLocation location, String name, NBTTagCompound cosmeticInfo) {
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
//    private BiMap<String, NBTTagCompound> cosmeticPresetsPowerFist = HashBiMap.create();
//    public BiMap<String, NBTTagCompound> getCosmeticPresetsPowerFist() {
//        if (cosmeticPresetsPowerFist.isEmpty() && !COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN.get())
//            cosmeticPresetsPowerFist = CosmeticPresetSaveLoad.loadPresetsForItem(MPSObjects.INSTANCE.powerFist, 0);
//        return cosmeticPresetsPowerFist;
//    }
//
//    private BiMap<String, NBTTagCompound> cosmeticPresetsPowerArmorHelmet = HashBiMap.create();
//    public BiMap<String, NBTTagCompound> getCosmeticPresetsPowerArmorHelmet() {
//        if (cosmeticPresetsPowerArmorHelmet.isEmpty() && !COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get())
//            cosmeticPresetsPowerArmorHelmet = CosmeticPresetSaveLoad.loadPresetsForItem(MPSObjects.INSTANCE.powerArmorHead, 0);
//        return cosmeticPresetsPowerArmorHelmet;
//    }
//
//    private BiMap<String, NBTTagCompound> cosmeticPresetsPowerArmorChestplate = HashBiMap.create();
//    public BiMap<String, NBTTagCompound> getCosmeticPresetsPowerArmorChestplate() {
//        if(cosmeticPresetsPowerArmorChestplate.isEmpty() && !COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get())
//            cosmeticPresetsPowerArmorChestplate = CosmeticPresetSaveLoad.loadPresetsForItem(MPSObjects.INSTANCE.powerArmorTorso, 0);
//        return cosmeticPresetsPowerArmorChestplate;
//    }
//
//    private BiMap<String, NBTTagCompound> cosmeticPresetsPowerArmorLeggings = HashBiMap.create();
//    public BiMap<String, NBTTagCompound> getCosmeticPresetsPowerArmorLeggings() {
//        if(cosmeticPresetsPowerArmorLeggings.isEmpty() && !COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get())
//            cosmeticPresetsPowerArmorLeggings = CosmeticPresetSaveLoad.loadPresetsForItem(MPSObjects.INSTANCE.powerArmorLegs, 0);
//        return cosmeticPresetsPowerArmorLeggings;
//    }
//
//    private BiMap<String, NBTTagCompound>  cosmeticPresetsPowerArmorBoots = HashBiMap.create();
//    public BiMap<String, NBTTagCompound> getCosmeticPresetsPowerArmorBoots() {
//        if(cosmeticPresetsPowerArmorBoots.isEmpty() && !COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get())
//            cosmeticPresetsPowerArmorBoots = CosmeticPresetSaveLoad.loadPresetsForItem(MPSObjects.INSTANCE.powerArmorFeet, 0);
//        return cosmeticPresetsPowerArmorBoots;
//    }
}