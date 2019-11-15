package com.github.lehjr.modularpowerarmor.basemod;

public class Constants {
    public static final String MODID = "modularpowerarmor";
    public static final String NAME = "MachineMuse's Modular modularpowerarmor";
    public static final String VERSION = "@VERSION@";
    public static final String RESOURCE_DOMAIN = MODID.toLowerCase();
    public static final String RESOURCE_PREFIX = RESOURCE_DOMAIN + ":";
    public static final String TEXTURE_PREFIX = RESOURCE_PREFIX + "textures/";

    // temporary locations until model spec system up and running
    public static final String SEBK_AMROR_PANTS = TEXTURE_PREFIX + "item/armor/sebkarmorpants.png";
    public static final String SEBK_AMROR = TEXTURE_PREFIX + "item/armor/sebkarmor.png";
    public static final String CITIZEN_JOE_ARMOR_PANTS = TEXTURE_PREFIX + "item/armor/joearmorpants.png";
    public static final String CITIZEN_JOE_ARMOR = TEXTURE_PREFIX + "item/armor/joearmor.png";

    public static final String COMPONENTS_PREFIX = RESOURCE_PREFIX + "component/";


    public static final String CONFIG_PREFIX = "config." + MODID + ".";
    public static final String CONFIG_FILE = "lehjr/" + MODID;

    /** HUD --------------------------------------------------------------------------------------- */
    public static final String CONFIG_PREFIX_HUD = CONFIG_PREFIX + "hud.";
    public static final String CONFIG_HUD_TOGGLE_MODULE_SPAM = CONFIG_PREFIX_HUD + "enableModuleSpam";
    public static final String CONFIG_HUD_DISPLAY_HUD = CONFIG_PREFIX_HUD + "DisplayHUD";
    public static final String CONFIG_HUD_KEYBIND_HUD_X = CONFIG_PREFIX_HUD + "Xposition";
    public static final String CONFIG_HUD_KEYBIND_HUD_Y = CONFIG_PREFIX_HUD + "Yposition";
    public static final String CONFIG_HUD_USE_GRAPHICAL_METERS = CONFIG_PREFIX_HUD + "useGraphicalMeters";
    public static final String CONFIG_HUD_USE_24_HOUR_CLOCK = CONFIG_PREFIX_HUD + "use24HrClock";

    /** Cosmetics --------------------------------------------------------------------------------- */
    public static final String CONFIG_PREFIX_COSMETIC = CONFIG_PREFIX + "cosmetic.";
    public static final String CONFIG_COSMETIC_USE_LEGACY_COSMETIC_SYSTEM = CONFIG_PREFIX_COSMETIC + "useLegacyCosmeticSystem";
    public static final String CONFIG_COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS = CONFIG_PREFIX_COSMETIC + "allowHighPollyArmorModuels";
    public static final String CONFIG_COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN = CONFIG_PREFIX_COSMETIC + "allowPowerFistCustomization";

    /** General ----------------------------------------------------------------------------------- */
    public static final String CONFIG_PREFIX_GENERAL = CONFIG_PREFIX + "general.";
    public static final String CONFIG_GENERAL_ALLOW_CONFLICTING_KEYBINDS = CONFIG_PREFIX_GENERAL + "allowConflictingKeybinds";
    public static final String CONFIG_GENERAL_MAX_FLYING_SPEED = CONFIG_PREFIX_GENERAL + "maxFlyingSpeed";
    public static final String CONFIG_GENERAL_USE_OLD_AUTOFEEDER = CONFIG_PREFIX_GENERAL + "useOldAutofeeder";
    public static final String CONFIG_GENERAL_GET_MAX_ARMOR_PER_PIECE = CONFIG_PREFIX_GENERAL + "getMaximumArmorPerPiece";
    public static final String CONFIG_GENERAL_BASE_MAX_HEAT_POWERFIST = CONFIG_PREFIX_GENERAL + "baseMaxHeatPowerfist";
    public static final String CONFIG_GENERAL_BASE_MAX_HEAT_HELMET = CONFIG_PREFIX_GENERAL + "baseMaxHeatHelmet";
    public static final String CONFIG_GENERAL_BASE_MAX_HEAT_CHEST = CONFIG_PREFIX_GENERAL + "baseMaxHeatChest";
    public static final String CONFIG_GENERAL_BASE_MAX_HEAT_LEGS = CONFIG_PREFIX_GENERAL + "baseMaxHeatLegs";
    public static final String CONFIG_GENERAL_BASE_MAX_HEAT_FEET = CONFIG_PREFIX_GENERAL + "baseMaxHeatFeet";

    /** Recipes ----------------------------------------------------------------------------------- */
    public static final String CONFIG_PREFIX_RECIPES = CONFIG_PREFIX + "recipes.";
    public static final String CONFIG_RECIPES_USE_VANILLA = CONFIG_PREFIX_RECIPES + "useVanilla";
    public static final String CONFIG_RECIPES_USE_THERMAL_EXPANSION = CONFIG_PREFIX_RECIPES + "useThermalExpansion";
    public static final String CONFIG_RECIPES_USE_ENDERIO = CONFIG_PREFIX_RECIPES + "useEnderIO";
    public static final String CONFIG_RECIPES_USE_TECH_REBORN = CONFIG_PREFIX_RECIPES + "useTechReborn";
    public static final String CONFIG_RECIPES_USE_IC2 = CONFIG_PREFIX_RECIPES + "useIC2";

    /** Modules ----------------------------------------------------------------------------------- */
    public static final String CONFIG_MODULES = CONFIG_PREFIX + "module";
    public static final String CONFIG_MODULE_PROPERTY_DOUBLES = CONFIG_PREFIX + "modulePropertyDoubles";
    public static final String CONFIG_MODULE_PROPERTY_INTEGERS = CONFIG_PREFIX + "modulePropertyIntegers";

    /**
     * Modules ------------------------------------------------------------------------------------
     */
    public static final String IS_MODULE_ALLOWED ="isModuleAllowed";

    // Armor --------------------------------------------------------------------------------------
    public static final String ARMOR_POINTS = "armorPoints";
    public static final String ARMOR_VALUE_PHYSICAL = "armorPhysical";
    public static final String ARMOR_VALUE_ENERGY = "armorEnergy";
    public static final String ARMOR_ENERGY_CONSUMPTION = "armorEnergyPerDamage";
    public static final String KNOCKBACK_RESISTANCE = "knockbackResistance";
    public static final String MODULE_FIELD_STRENGTH = "fieldStrength";
    public static final String MAXIMUM_HEAT = "maxHeat";

    // Energy Storage -----------------------------------------------------------------------------
    public static final String MAX_ENERGY = "maxEnergy";
    public static final String MAX_TRAMSFER = "maxTransfer";

    // Energy Generation --------------------------------------------------------------------------
    public static final String ENERGY_GENERATION = "energyPerBlock";
    public static final String MOVEMENT_RESISTANCE = "movementResistance";
    public static final String ENERGY_GENERATED = "energyGenerated";
    public static final String HEAT_GENERATION_DAY = "daytimeHeatGen";
    public static final String HEAT_GENERATION_NIGHT = "nightTimeHeatGen";
    public static final String ENERGY_GENERATION_DAY = "daytimeEnergyGen";
    public static final String ENERGY_GENERATION_NIGHT = "nightTimeEnergyGen";

    // Environmental ------------------------------------------------------------------------------
    public static final String EATING_EFFICIENCY = "autoFeederEfficiency";
    public static final String EFFICIENCY = "efficiency";
    public static final String FLUID_TANK_SIZE = "fluidTankSize";
    public static final String COOLING_BONUS = "coolingBonus";
    public static final String HEAT_ACTIVATION_PERCENT = "heatActivationPercent";

    // Mining Enhancement -------------------------------------------------------------------------
    public static final String AOE_MINING_RADIUS = "aoeMiningDiameter";
    public static final String DIAMETER ="diameter";
    public static final String HARVEST_SPEED = "harvSpeed";
    public static final String FORTUNE_ENERGY_CONSUMPTION = "fortuneEnCon";
    public static final String ENCHANTMENT_LEVEL ="enchLevel";
    public static final String FORTUNE_ENCHANTMENT_LEVEL ="fortuneLevel";
    public static final String SILK_TOUCH_ENERGY_CONSUMPTION = "silkTouchEnCon";


    // Movement ----------------------------------------------------------------------------------
    public static final String THRUST = "thrust";
    public static final String SWIM_BOOST_AMOUNT = "underwaterMovBoost";
    public static final String RANGE = "range";
    public static final String BLINK_DRIVE_RANGE = "blinkDriveRange";
    public static final String SPRINT_ENERGY_CONSUMPTION = "sprintEnergyCon";
    public static final String SPRINT_SPEED_MULTIPLIER = "sprintSpeedMult";
    public static final String FOOD_COMPENSATION = "sprintExComp";
    public static final String WALKING_ENERGY_CONSUMPTION = "walkingEnergyCon";
    public static final String WALKING_SPEED_MULTIPLIER = "walkingSpeedMult";
    public static final String WALKING_ASSISTANCE = "walkingAssist";
    public static final String SPRINT_ASSIST = "sprintAssist";
    public static final String COMPENSATION = "compensation";
    public static final String MULTIPLIER = "muultiplier";
    public static final String JETPACK_THRUST = "jetpackThrust";
    public static final String JETBOOTS_THRUST = "jetbootsThrust";
    public static final String VERTICALITY = "vertically";
    public static final String FLIGHT_VERTICALITY = "yLookRatio";
    public static final String HEAT_GENERATION = "heatGen";


    // Tool ---------------------------------------------------------------------------------------
    public static final String OVERCLOCK = "overclock";
    public static final String RED_HUE = "redHue";
    public static final String BLUE_HUE = "blueHue";
    public static final String GREEN_HUE = "greenHue";
    public static final String OPACITY = "opacity";
    public static final String RED = "red";
    public static final String GREEN = "green";
    public static final String BLUE = "blue";
    public static final String ALPHA = "alpha";
    public static final String RADIUS = "radius";


    // Vision -------------------------------------------------------------------------------------
    public static final String FOV = "fieldOfView";
    public static final String FIELD_OF_VIEW = "fOVMult";

    // Weapon --------------------------------------------------------------------------------------
    public static final String BLADE_ENERGY = "spinBladeEnergyCon";
    public static final String BLADE_DAMAGE = "spinBladeDam";
    public static final String ENERGY_CONSUMPTION = "energyCon";
    public static final String HEAT_EMISSION = "heatEmission";
    public static final String PUNCH_ENERGY = "punchEnergyCon";
    public static final String PUNCH_DAMAGE = "meleeDamage";
    public static final String PUNCH_KNOCKBACK = "meleeKnockback";
    public static final String IMPACT = "impact";
    public static final String CARRY_THROUGH = "carryThrough";
    public static final String PLASMA_CANNON_ENERGY_PER_TICK = "plasmaEnergyPerTick";
    public static final String PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE = "plasmaDamage";
    public static final String PLASMA_CANNON_EXPLOSIVENESS = "plasmaExplosiveness";
    public static final String AMPERAGE = "amperage";
    public static final String CREEPER = "creeper";
    public static final String VOLTAGE = "voltage";
    public static final String RAILGUN_TOTAL_IMPULSE = "railgunTotalImpulse";
    public static final String RAILGUN_ENERGY_COST = "railgunEnergyCost";
    public static final String RAILGUN_HEAT_EMISSION = "railgunHeatEm";
    public static final String TIMER = "cooldown";
    public static final String POWER = "power";
}
