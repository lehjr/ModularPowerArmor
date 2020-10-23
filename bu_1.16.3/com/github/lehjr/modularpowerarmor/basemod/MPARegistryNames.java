package com.github.lehjr.modularpowerarmor.basemod;

import static com.github.lehjr.modularpowerarmor.basemod.MPAConstants.MOD_ID;

/**
 * With all the new object types that need to get registered, putting all the registry names here is the best way to go
 * Also, @ObjectHolders require strings and won't take a ResourceLocation, so there's that
 */
public class MPARegistryNames {


    /**
     * HandHeld -----------------------------------------------------------------------------------
     */
    public static final String ITEM__POWER_FIST__REGNAME = MOD_ID + ":powerfist";

    /**
     * Components ---------------------------------------------------------------------------------
     */
    public static final String COMPONENT__WIRING__REGNAME = MOD_ID + ":component_wiring";
    public static final String COMPONENT__SOLENOID__REGNAME = MOD_ID + ":component_solenoid";
    public static final String COMPONENT__SERVO__REGNAME = MOD_ID + ":component_servo";
    public static final String COMPONENT__GLIDER_WING__REGNAME = MOD_ID + ":component_glider_wing";
    public static final String COMPONENT__ION_THRUSTER__REGNAME = MOD_ID + ":component_ion_thruster";
    public static final String COMPONENT__PARACHUTE__REGNAME = MOD_ID + ":component_parachute";
    public static final String COMPONENT__FIELD_EMITTER__REGNAME = MOD_ID + ":component_field_emitter";
    public static final String COMPONENT__LASER_EMITTER__REGNAME = MOD_ID + ":component_laser_emitter";
    public static final String COMPONENT__CARBON_MYOFIBER__REGNAME = MOD_ID + ":component_carbon_myofiber";
    public static final String COMPONENT__CONTROL_CIRCUIT__REGNAME = MOD_ID + ":component_control_circuit";
    public static final String COMPONENT__MYOFIBER_GEL__REGNAME = MOD_ID + ":component_myofiber_gel";
    public static final String COMPONENT__ARTIFICIAL_MUSCLE__REGNAME = MOD_ID + ":component_artificial_muscle";
    public static final String COMPONENT__SOLAR_PANEL__REGNAME = MOD_ID + ":component_solar_panel";
    public static final String COMPONENT__MAGNET__REGNAME = MOD_ID + ":component_magnet";
    public static final String COMPONENT__COMPUTER_CHIP__REGNAME = MOD_ID + ":component_computer_chip";
    public static final String COMPONENT__RUBBER_HOSE__REGNAME = MOD_ID + ":component_rubber_hose";

    /**
     * Modules ------------------------------------------------------------------------------------
     */
    // Armor --------------------------------------------------------------------------------------
    public static final String MODULE_LEATHER_PLATING__REGNAME = MOD_ID + ":plating_leather";
    public static final String MODULE_IRON_PLATING__REGNAME =  MOD_ID + ":plating_iron";
    public static final String MODULE_DIAMOND_PLATING__REGNAME =  MOD_ID + ":plating_diamond";
    public static final String MODULE_ENERGY_SHIELD__REGNAME =  MOD_ID + ":energy_shield";

    // Cosmetic -----------------------------------------------------------------------------------
    public static final String MODULE_TRANSPARENT_ARMOR__REGNAME = MOD_ID + ":transparent_armor";

    // Energy Storage -----------------------------------------------------------------------------
    public static final String MODULE_BATTERY_BASIC__REGNAME = MOD_ID + ":battery_basic";
    public static final String MODULE_BATTERY_ADVANCED__REGNAME = MOD_ID + ":battery_advanced";
    public static final String MODULE_BATTERY_ELITE__REGNAME = MOD_ID + ":battery_elite";
    public static final String MODULE_BATTERY_ULTIMATE__REGNAME = MOD_ID + ":battery_ultimate";

    // Energy Generation -----------------------------------------------------------------------------
    public static final String MODULE_SOLAR_GENERATOR__REGNAME = MOD_ID + ":generator_solar";
    public static final String MODULE_ADVANCED_SOLAR_GENERATOR__REGNAME = MOD_ID + ":generator_solar_adv";
    public static final String MODULE_KINETIC_GENERATOR__REGNAME = MOD_ID + ":generator_kinetic";
    public static final String MODULE_THERMAL_GENERATOR__REGNAME = MOD_ID + ":generator_thermal";

    // todo
    // Debug --------------------------------------------------------------------------------------
    public static final String MODULE_DEBUG = MOD_ID + ":debug_module";

    // Environmental ------------------------------------------------------------------------------
    public static final String MODULE_COOLING_SYSTEM__REGNAME = MOD_ID + ":cooling_system";
    public static final String MODULE_FLUID_TANK__REGNAME = MOD_ID + ":fluid_tank";
    public static final String MODULE_AUTO_FEEDER__REGNAME = MOD_ID + ":auto_feeder";
    public static final String MODULE_MOB_REPULSOR__REGNAME = MOD_ID + ":mob_repulsor";
    public static final String MODULE_WATER_ELECTROLYZER__REGNAME = MOD_ID + ":water_electrolyzer";

    // Movement -----------------------------------------------------------------------------------
    public static final String MODULE_BLINK_DRIVE__REGNAME = MOD_ID + ":blink_drive";
    public static final String MODULE_CLIMB_ASSIST__REGNAME = MOD_ID + ":climb_assist";
    public static final String MODULE_DIMENSIONAL_RIFT__REGNAME = MOD_ID + ":dim_rift_gen";
    public static final String MODULE_FLIGHT_CONTROL__REGNAME = MOD_ID + ":flight_control";
    public static final String MODULE_GLIDER__REGNAME = MOD_ID + ":glider";
    public static final String MODULE_JETBOOTS__REGNAME = MOD_ID + ":jet_boots";
    public static final String MODULE_JETPACK__REGNAME = MOD_ID + ":jetpack";
    public static final String MODULE_JUMP_ASSIST__REGNAME = MOD_ID + ":jump_assist";
    public static final String MODULE_PARACHUTE__REGNAME = MOD_ID + ":parachute";
    public static final String MODULE_SHOCK_ABSORBER__REGNAME = MOD_ID + ":shock_absorber";
    public static final String MODULE_SPRINT_ASSIST__REGNAME = MOD_ID + ":sprint_assist";
    public static final String MODULE_SWIM_BOOST__REGNAME = MOD_ID + ":swim_assist";

    // Special ------------------------------------------------------------------------------------
    public static final String MODULE_CLOCK__REGNAME = MOD_ID + ":clock";
    public static final String MODULE_COMPASS__REGNAME = MOD_ID + ":compass";
    public static final String MODULE_ACTIVE_CAMOUFLAGE__REGNAME = MOD_ID + ":invisibility";
    public static final String MODULE_MAGNET__REGNAME = MOD_ID + ":magnet";

    // Vision -------------------------------------------------------------------------------------
    public static final String BINOCULARS_MODULE__REGNAME = MOD_ID + ":binoculars";
    public static final String MODULE_NIGHT_VISION__REGNAME = MOD_ID + ":night_vision";

    // Mining Enhancements ------------------------------------------------------------------------
    public static final String MODULE_AOE_PICK_UPGRADE__REGNAME = MOD_ID + ":aoe_pick_upgrade"; // no icon
    public static final String MODULE_SILK_TOUCH__REGNAME = MOD_ID + ":silk_touch";
    public static final String MODULE_FORTUNE_REGNAME = MOD_ID + ":fortune";
    public static final String MODULE_VEIN_MINER_REGNAME = MOD_ID + ":vein_miner";

    // Tools --------------------------------------------------------------------------------------
    public static final String MODULE_AQUA_AFFINITY__REGNAME = MOD_ID + ":aqua_affinity";
    public static final String MODULE_AXE__REGNAME = MOD_ID + ":axe";
    public static final String MODULE_DIAMOND_PICK_UPGRADE__REGNAME = MOD_ID + ":diamond_pick_upgrade";
    public static final String MODULE_FIELD_TINKER__REGNAME = MOD_ID + ":field_tinkerer";
    public static final String MODULE_FLINT_AND_STEEL__REGNAME = MOD_ID + ":flint_and_steel";
    public static final String MODULE_HOE__REGNAME = MOD_ID + ":hoe";
    public static final String MODULE_LEAF_BLOWER__REGNAME = MOD_ID + ":leaf_blower";
    public static final String MODULE_LUX_CAPACITOR__REGNAME = MOD_ID + ":luxcapacitor_module";
    public static final String MODULE_PORTABLE_CRAFTING__REGNAME = MOD_ID + ":portable_crafting_table";
    public static final String MODULE_PICKAXE__REGNAME = MOD_ID + ":pickaxe";
    public static final String MODULE_SHEARS__REGNAME = MOD_ID + ":shears";
    public static final String MODULE_SHOVEL__REGNAME = MOD_ID + ":shovel";

    // Weapons ------------------------------------------------------------------------------------
    public static final String MODULE_BLADE_LAUNCHER__REGNAME = MOD_ID + ":blade_launcher";
    public static final String MODULE_LIGHTNING__REGNAME = MOD_ID + ":lightning_summoner";
    public static final String MODULE_MELEE_ASSIST__REGNAME = MOD_ID + ":melee_assist";
    public static final String MODULE_PLASMA_CANNON__REGNAME = MOD_ID + ":plasma_cannon";
    public static final String MODULE_RAILGUN__REGNAME = MOD_ID + ":railgun";
    public static final String MODULE_SONIC_WEAPON__REGNAME = MOD_ID + "sonic_weapon";



    /**
     * Container
     */
    public static final String MPA_CRAFTING_CONTAINER_TYPE__REG_NAME = MOD_ID + ":crafting_container";
    public static final String MPA_TINKER_TABLE_CONTAINER_TYPE__REG_NAME = MOD_ID + ":tinker_table_container_type";

}