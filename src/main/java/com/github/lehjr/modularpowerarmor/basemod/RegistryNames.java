package com.github.lehjr.modularpowerarmor.basemod;

import static com.github.lehjr.modularpowerarmor.basemod.Constants.MODID;

/**
 * With all the new object types that need to get registered, putting all the registry names here is the best way to go
 * Also, @ObjectHolders require strings and won't take a ResourceLocation, so there's that
 */
public class RegistryNames {
    /**
     * Armor --------------------------------------------------------------------------------------
     */
    public static final String ITEM__POWER_ARMOR_HELMET__REGNAME = MODID + ":powerarmor_head";
    public static final String ITEM__POWER_ARMOR_CHESTPLATE__REGNAME = MODID + ":powerarmor_torso";
    public static final String ITEM__POWER_ARMOR_LEGGINGS__REGNAME = MODID + ":powerarmor_legs";
    public static final String ITEM__POWER_ARMOR_BOOTS__REGNAME = MODID + ":powerarmor_feet";

    /**
     * HandHeld -----------------------------------------------------------------------------------
     */
    public static final String ITEM__POWER_FIST__REGNAME = MODID + ":powerfist";

    /**
     * Components ---------------------------------------------------------------------------------
     */
    public static final String COMPONENT__WIRING__REGNAME = MODID + ":component_wiring";
    public static final String COMPONENT__SOLENOID__REGNAME = MODID + ":component_solenoid";
    public static final String COMPONENT__SERVO__REGNAME = MODID + ":component_servo";
    public static final String COMPONENT__GLIDER_WING__REGNAME = MODID + ":component_glider_wing";
    public static final String COMPONENT__ION_THRUSTER__REGNAME = MODID + ":component_ion_thruster";
    public static final String COMPONENT__LV_CAPACITOR__REGNAME = MODID + ":component_capacitor_lv";
    public static final String COMPONENT__MV_CAPACITOR___REGNAME = MODID + ":component_capacitor_mv";
    public static final String COMPONENT__HV_CAPACITOR___REGNAME = MODID + ":component_capacitor_hv";
    public static final String COMPONENT__EV_CAPACITOR___REGNAME = MODID + ":component_capacitor_ev";
    public static final String COMPONENT__PARACHUTE__REGNAME = MODID + ":component_parachute";
    public static final String COMPONENT__LEATHER_PLATING__REGNAME = MODID + ":component_plating_leather";
    public static final String COMPONENT__IRON_PLATING__REGNAME = MODID + ":component_plating_iron";
    public static final String COMPONENT__DIAMOND_PLATING__REGNAME = MODID + ":component_plating_diamond";
    public static final String COMPONENT__FIELD_EMITTER__REGNAME = MODID + ":component_field_emitter";
    public static final String COMPONENT__LASER_EMITTER__REGNAME = MODID + ":component_laser_emitter";
    public static final String COMPONENT__CARBON_MYOFIBER__REGNAME = MODID + ":component_carbon_myofiber";
    public static final String COMPONENT__CONTROL_CIRCUIT__REGNAME = MODID + ":component_control_circuit";
    public static final String COMPONENT__MYOFIBER_GEL__REGNAME = MODID + ":component_myofiber_gel";
    public static final String COMPONENT__ARTIFICIAL_MUSCLE__REGNAME = MODID + ":component_artificial_muscle";
    public static final String COMPONENT__SOLAR_PANEL__REGNAME = MODID + ":component_solar_panel";
    public static final String COMPONENT__MAGNET__REGNAME = MODID + ":component_magnet";
    public static final String COMPONENT__COMPUTER_CHIP__REGNAME = MODID + ":component_computer_chip";
    public static final String COMPONENT__RUBBER_HOSE__REGNAME = MODID + ":component_rubber_hose";

    /**
     * Modules ------------------------------------------------------------------------------------
     */
    // Debug --------------------------------------------------------------------------------------
    public static final String MODULE_DEBUG__REGNAME = MODID + ":debug_module";

    // Armor --------------------------------------------------------------------------------------
    public static final String MODULE_LEATHER_PLATING__REGNAME = MODID + ":plating_leather";
    public static final String MODULE_IRON_PLATING__REGNAME =  MODID + ":plating_iron";
    public static final String MODULE_DIAMOND_PLATING__REGNAME =  MODID + ":plating_diamond";
    public static final String MODULE_ENERGY_SHIELD__REGNAME =  MODID + ":energy_shield";

    // Cosmetic -----------------------------------------------------------------------------------
    public static final String MODULE_TRANSPARENT_ARMOR__REGNAME = MODID + ":transparent_armor";

    // Energy Storage -----------------------------------------------------------------------------
    public static final String MODULE_BATTERY_BASIC__REGNAME = MODID + ":battery_basic";
    public static final String MODULE_BATTERY_ADVANCED__REGNAME = MODID + ":battery_advanced";
    public static final String MODULE_BATTERY_ELITE__REGNAME = MODID + ":battery_elite";
    public static final String MODULE_BATTERY_ULTIMATE__REGNAME = MODID + ":battery_ultimate";

    // Energy Generation -----------------------------------------------------------------------------
    public static final String MODULE_SOLAR_GENERATOR__REGNAME = MODID + ":generator_solar";
    public static final String MODULE_ADVANCED_SOLAR_GENERATOR__REGNAME = MODID + ":generator_solar_adv";
    public static final String MODULE_KINETIC_GENERATOR__REGNAME = MODID + ":generator_kinetic";
    public static final String MODULE_THERMAL_GENERATOR__REGNAME = MODID + ":generator_thermal";
    public static final String MODULE_COAL_GEN__REGNAME = MODID + ":coalGenerator";

    // Environmental ------------------------------------------------------------------------------
    public static final String MODULE_COOLING_SYSTEM__REGNAME = MODID + ":cooling_system";
    public static final String MODULE_FLUID_TANK__REGNAME = MODID + ":fluid_tank";
    public static final String MODULE_AUTO_FEEDER__REGNAME = MODID + ":auto_feeder";
    public static final String MODULE_MOB_REPULSOR__REGNAME = MODID + ":mob_repulsor";
    public static final String MODULE_WATER_ELECTROLYZER__REGNAME = MODID + ":water_electrolyzer";
    public static final String MODULE_AIRTIGHT_SEAL__REGNAME = MODID + ":airtight_seal";
    public static final String MODULE_APIARIST_ARMOR__REGNAME = MODID + ":apiarist_armor";
    public static final String MODULE_HAZMAT__REGNAME = MODID + ":hazmat";

    // Movement -----------------------------------------------------------------------------------
    public static final String MODULE_BLINK_DRIVE__REGNAME = MODID + ":blink_drive";
    public static final String MODULE_CLIMB_ASSIST__REGNAME = MODID + ":climb_assist";
    public static final String MODULE_DIMENSIONAL_RIFT__REGNAME = MODID + ":dim_rift_gen";
    public static final String MODULE_FLIGHT_CONTROL__REGNAME = MODID + ":flight_control";
    public static final String MODULE_GLIDER__REGNAME = MODID + ":glider";
    public static final String MODULE_JETBOOTS__REGNAME = MODID + ":jet_boots";
    public static final String MODULE_JETPACK__REGNAME = MODID + ":jetpack";
    public static final String MODULE_JUMP_ASSIST__REGNAME = MODID + ":jump_assist";
    public static final String MODULE_PARACHUTE__REGNAME = MODID + ":parachute";
    public static final String MODULE_SHOCK_ABSORBER__REGNAME = MODID + ":shock_absorber";
    public static final String MODULE_SPRINT_ASSIST__REGNAME = MODID + ":sprint_assist";
    public static final String MODULE_SWIM_BOOST__REGNAME = MODID + ":swim_assist";

    // Special ------------------------------------------------------------------------------------
    public static final String MODULE_CLOCK__REGNAME = MODID + ":clock";
    public static final String MODULE_COMPASS__REGNAME = MODID + ":compass";
    public static final String MODULE_ACTIVE_CAMOUFLAGE__REGNAME = MODID + ":invisibility";
    public static final String MODULE_MAGNET__REGNAME = MODID + ":magnet";

    // Vision -------------------------------------------------------------------------------------
    public static final String BINOCULARS_MODULE__REGNAME = MODID + ":binoculars";
    public static final String MODULE_NIGHT_VISION__REGNAME = MODID + ":night_vision";
    public static final String MODULE_THAUM_GOGGLES__REGNAME = MODID + ":aurameter";

    // Mining Enhancements ------------------------------------------------------------------------
    public static final String MODULE_AOE_PICK_UPGRADE__REGNAME = MODID + ":aoe_pick_upgrade"; // no icon
    public static final String MODULE_SILK_TOUCH__REGNAME = MODID + ":silk_touch";
    public static final String MODULE_FORTUNE_REGNAME = MODID + ":fortune";
    public static final String MODULE_MAD__REGNAME = MODID + ":madModule";

    // Tools --------------------------------------------------------------------------------------
    public static final String MODULE_APPENG_EC_WIRELESS_FLUID__REGNAME = MODID + ":appengECWirelessFluid";
    public static final String MODULE_APPENG_WIRELESS__REGNAME = MODID + ":appengWireless";
    public static final String MODULE_AQUA_AFFINITY__REGNAME = MODID + ":aqua_affinity";
    public static final String MODULE_AXE__REGNAME = MODID + ":axe";
    public static final String MODULE_CM_PSD__REGNAME = MODID + ":cmpsd";//"Personal Shrinking Device";
    public static final String MODULE_DIAMOND_PICK_UPGRADE__REGNAME = MODID + ":diamond_pick_upgrade";
    public static final String MODULE_FIELD_TINKER__REGNAME = MODID + ":field_tinkerer";
    public static final String MODULE_FLINT_AND_STEEL__REGNAME = MODID + ":flint_and_steel";
    public static final String MODULE_GRAFTER__REGNAME = "grafter";
    public static final String MODULE_HOE__REGNAME = MODID + ":hoe";
    public static final String MODULE_LEAF_BLOWER__REGNAME = MODID + ":leaf_blower";
    public static final String MODULE_LUX_CAPACITOR__REGNAME = MODID + ":luxcapacitor_module";
    public static final String MODULE_PORTABLE_CRAFTING__REGNAME = MODID + ":portable_crafting_table";
    public static final String MODULE_OMNIPROBE__REGNAME = MODID + ":omni_probe";
    public static final String MODULE_OMNI_WRENCH__REGNAME = MODID + ":omniwrench";
    public static final String MODULE_ORE_SCANNER__REGNAME = MODID + ":ore_scanner";
    public static final String MODULE_PICKAXE__REGNAME = MODID + ":pickaxe";
    public static final String MODULE_REF_STOR_WIRELESS__REGNAME = MODID + ":refined_storage_wireless_grid";//"Refined Storage Wireless Grid";
    public static final String MODULE_SCOOP__REGNAME = MODID + ":scoop";
    public static final String MODULE_SHEARS__REGNAME = MODID + ":shears";
    public static final String MODULE_SHOVEL__REGNAME = MODID + ":shovel";
    public static final String MODULE_TREETAP__REGNAME = MODID + ":treetap";

    // Weapons ------------------------------------------------------------------------------------
    public static final String MODULE_BLADE_LAUNCHER__REGNAME = MODID + ":blade_launcher";
    public static final String MODULE_LIGHTNING__REGNAME = MODID + ":lightning_summoner";
    public static final String MODULE_MELEE_ASSIST__REGNAME = MODID + ":melee_assist";
    public static final String MODULE_PLASMA_CANNON__REGNAME = MODID + ":plasma_cannon";
    public static final String MODULE_RAILGUN__REGNAME = MODID + ":railgun";
    public static final String MODULE_SONIC_WEAPON__REGNAME = MODID + "sonic_weapon";

    /**
     * Blocks -------------------------------------------------------------------------------------
     */
    public static final String TINKER_TABLE_REG_NAME = MODID + ":tinkertable";
    public static final String LUX_CAPACITOR_REG_NAME = MODID + ":luxcapacitor";
}