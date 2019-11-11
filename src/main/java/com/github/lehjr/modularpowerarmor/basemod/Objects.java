package com.github.lehjr.modularpowerarmor.basemod;

import com.github.lehjr.modularpowerarmor.block.BlockLuxCapacitor;
import com.github.lehjr.modularpowerarmor.block.BlockTinkerTable;
import com.github.lehjr.modularpowerarmor.fluid.BlockFluidLiquidNitrogen;
import com.github.lehjr.modularpowerarmor.fluid.LiquidNitrogen;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorBoots;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorChestplate;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorHelmet;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorLeggings;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import com.github.lehjr.modularpowerarmor.item.module.armor.DiamondPlatingModule;
import com.github.lehjr.modularpowerarmor.item.module.armor.EnergyShieldModule;
import com.github.lehjr.modularpowerarmor.item.module.armor.IronPlatingModule;
import com.github.lehjr.modularpowerarmor.item.module.armor.LeatherPlatingModule;
import com.github.lehjr.modularpowerarmor.item.module.cosmetic.TransparentArmorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy.generation.AdvancedSolarGenerator;
import com.github.lehjr.modularpowerarmor.item.module.energy.generation.KineticGeneratorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy.generation.SolarGeneratorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy.generation.ThermalGeneratorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy.storage.EnergyStorageModule;
import com.github.lehjr.modularpowerarmor.item.module.environmental.*;
import com.github.lehjr.modularpowerarmor.item.module.miningenhancement.AOEPickUpgradeModule;
import com.github.lehjr.modularpowerarmor.item.module.miningenhancement.AquaAffinityModule;
import com.github.lehjr.modularpowerarmor.item.module.miningenhancement.FortuneModule;
import com.github.lehjr.modularpowerarmor.item.module.miningenhancement.SilkTouchModule;
import com.github.lehjr.modularpowerarmor.item.module.movement.*;
import com.github.lehjr.modularpowerarmor.item.module.special.ClockModule;
import com.github.lehjr.modularpowerarmor.item.module.special.CompassModule;
import com.github.lehjr.modularpowerarmor.item.module.special.InvisibilityModule;
import com.github.lehjr.modularpowerarmor.item.module.special.MagnetModule;
import com.github.lehjr.modularpowerarmor.item.module.tool.*;
import com.github.lehjr.modularpowerarmor.item.module.vision.BinocularsModule;
import com.github.lehjr.modularpowerarmor.item.module.vision.NightVisionModule;
import com.github.lehjr.modularpowerarmor.item.module.weapon.*;
import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.github.lehjr.modularpowerarmor.basemod.RegistryNames.*;


/**
 * Object Holders for the mod
 */
public enum Objects {
    INSTANCE;

    /**
     * Armor --------------------------------------------------------------------------------------
     */
    @GameRegistry.ObjectHolder(ITEM__POWER_ARMOR_HELMET__REGNAME)
    public static final ItemPowerArmorHelmet powerArmorHead = null;

    @GameRegistry.ObjectHolder(ITEM__POWER_ARMOR_CHESTPLATE__REGNAME)
    public static final ItemPowerArmorChestplate powerArmorTorso = null;

    @GameRegistry.ObjectHolder(ITEM__POWER_ARMOR_LEGGINGS__REGNAME)
    public static final ItemPowerArmorLeggings powerArmorLegs = null;

    @GameRegistry.ObjectHolder(ITEM__POWER_ARMOR_BOOTS__REGNAME)
    public static final ItemPowerArmorBoots powerArmorFeet = null;

    /**
     * HandHeld -----------------------------------------------------------------------------------
     */
    @GameRegistry.ObjectHolder(ITEM__POWER_FIST__REGNAME)
    public static final ItemPowerFist powerFist = null;

    /**
     * Components ---------------------------------------------------------------------------------
     */
    @GameRegistry.ObjectHolder(COMPONENT__WIRING__REGNAME)
    public static final ItemComponent wiring = null;

    @GameRegistry.ObjectHolder(COMPONENT__SOLENOID__REGNAME)
    public static final ItemComponent solenoid = null;

    @GameRegistry.ObjectHolder(COMPONENT__SERVO__REGNAME)
    public static final ItemComponent servo = null;

    @GameRegistry.ObjectHolder(COMPONENT__GLIDER_WING__REGNAME)
    public static final ItemComponent glider_wing = null;

    @GameRegistry.ObjectHolder(COMPONENT__ION_THRUSTER__REGNAME)
    public static final ItemComponent ion_thruster = null;

    @GameRegistry.ObjectHolder(COMPONENT__LV_CAPACITOR__REGNAME)
    public static final ItemComponent lv_capacitor = null;

    @GameRegistry.ObjectHolder(COMPONENT__MV_CAPACITOR___REGNAME)
    public static final ItemComponent mv_capacitor = null;

    @GameRegistry.ObjectHolder(COMPONENT__HV_CAPACITOR___REGNAME)
    public static final ItemComponent hv_capacitor = null;

    @GameRegistry.ObjectHolder(COMPONENT__EV_CAPACITOR___REGNAME)
    public static final ItemComponent ev_capacitor = null;

    @GameRegistry.ObjectHolder(COMPONENT__PARACHUTE__REGNAME)
    public static final ItemComponent component_parachute = null;

    @GameRegistry.ObjectHolder(COMPONENT__LEATHER_PLATING__REGNAME)
    public static final ItemComponent leatherPlating = null;

    @GameRegistry.ObjectHolder(COMPONENT__IRON_PLATING__REGNAME)
    public static final ItemComponent ironPlating = null;

    @GameRegistry.ObjectHolder(COMPONENT__DIAMOND_PLATING__REGNAME)
    public static final ItemComponent diamondPlating = null;

    @GameRegistry.ObjectHolder(COMPONENT__FIELD_EMITTER__REGNAME)
    public static final ItemComponent fieldEmitter = null;

    @GameRegistry.ObjectHolder(COMPONENT__LASER_EMITTER__REGNAME)
    public static final ItemComponent laserEmitter = null;

    @GameRegistry.ObjectHolder(COMPONENT__CARBON_MYOFIBER__REGNAME)
    public static final ItemComponent carbonMyofiber = null;

    @GameRegistry.ObjectHolder(COMPONENT__CONTROL_CIRCUIT__REGNAME)
    public static final ItemComponent controlCircuit = null;

    @GameRegistry.ObjectHolder(COMPONENT__MYOFIBER_GEL__REGNAME)
    public static final ItemComponent myofiberGel = null;

    @GameRegistry.ObjectHolder(COMPONENT__ARTIFICIAL_MUSCLE__REGNAME)
    public static final ItemComponent artificialMuscle = null;

    @GameRegistry.ObjectHolder(COMPONENT__SOLAR_PANEL__REGNAME)
    public static final ItemComponent solarPanel = null;

    @GameRegistry.ObjectHolder(COMPONENT__MAGNET__REGNAME)
    public static final ItemComponent component_magnet = null;

    @GameRegistry.ObjectHolder(COMPONENT__COMPUTER_CHIP__REGNAME)
    public static final ItemComponent computerChip = null;

    @GameRegistry.ObjectHolder(COMPONENT__RUBBER_HOSE__REGNAME)
    public static final ItemComponent rubberHose = null;

    /**
     * Modules ------------------------------------------------------------------------------------
     */
    // Armor --------------------------------------------------------------------------------------
    @GameRegistry.ObjectHolder(MODULE_LEATHER_PLATING__REGNAME)
    public static final LeatherPlatingModule moduleLeatherPlating = null;

    @GameRegistry.ObjectHolder(MODULE_IRON_PLATING__REGNAME)
    public static final IronPlatingModule moduleIronPlating = null;

    @GameRegistry.ObjectHolder(MODULE_DIAMOND_PLATING__REGNAME)
    public static final DiamondPlatingModule moduleDiamondPlating = null;

    @GameRegistry.ObjectHolder(MODULE_ENERGY_SHIELD__REGNAME)
    public static final EnergyShieldModule moduleEnergyShield = null;

    // Cosmetic -----------------------------------------------------------------------------------

    @GameRegistry.ObjectHolder(MODULE_TRANSPARENT_ARMOR__REGNAME)
    public static final TransparentArmorModule moduleTransparentArmor = null;

    // Energy Storage -----------------------------------------------------------------------------
    @GameRegistry.ObjectHolder(MODULE_BATTERY_BASIC__REGNAME)
    public static final EnergyStorageModule moduleBatteryBasic = null;

    @GameRegistry.ObjectHolder(MODULE_BATTERY_ADVANCED__REGNAME)
    public static final EnergyStorageModule moduleBatteryAdvanced = null;

    @GameRegistry.ObjectHolder(MODULE_BATTERY_ELITE__REGNAME)
    public static final EnergyStorageModule moduleBatteryElite = null;

    @GameRegistry.ObjectHolder(MODULE_BATTERY_ULTIMATE__REGNAME)
    public static final EnergyStorageModule moduleBatteryUltimate = null;

    // Energy Generation -----------------------------------------------------------------------------
    @GameRegistry.ObjectHolder(MODULE_SOLAR_GENERATOR__REGNAME)
    public static final SolarGeneratorModule solarGenerator = null;

    @GameRegistry.ObjectHolder(MODULE_ADVANCED_SOLAR_GENERATOR__REGNAME)
    public static final AdvancedSolarGenerator advSolarGenerator = null;

    @GameRegistry.ObjectHolder(MODULE_KINETIC_GENERATOR__REGNAME)
    public static final KineticGeneratorModule kineticGenerator = null;

    @GameRegistry.ObjectHolder(MODULE_THERMAL_GENERATOR__REGNAME)
    public static final ThermalGeneratorModule thermalGenerator = null;

    // todo
    // Debug --------------------------------------------------------------------------------------


    // Environmental ------------------------------------------------------------------------------
    @GameRegistry.ObjectHolder(MODULE_COOLING_SYSTEM__REGNAME)
    public static CoolingSystemModule basicCoolingSystem = null;

    @GameRegistry.ObjectHolder(MODULE_FLUID_TANK__REGNAME)
    public static final FluidTankModule fluidTankModule = null;

    @GameRegistry.ObjectHolder(MODULE_AUTO_FEEDER__REGNAME)
    public static final AutoFeederModule autoFeeder = null;

    @GameRegistry.ObjectHolder(MODULE_MOB_REPULSOR__REGNAME)
    public static final MobRepulsorModule mobRepulsor = null;

    @GameRegistry.ObjectHolder(MODULE_WATER_ELECTROLYZER__REGNAME)
    public static final WaterElectrolyzerModule waterElectrolyzer = null;

    // Movement -----------------------------------------------------------------------------------
    @GameRegistry.ObjectHolder(MODULE_BLINK_DRIVE__REGNAME)
    public static final BlinkDriveModule blinkDrive = null;

    @GameRegistry.ObjectHolder(MODULE_CLIMB_ASSIST__REGNAME)
    public static final ClimbAssistModule climbAssist = null;

    @GameRegistry.ObjectHolder(MODULE_FLIGHT_CONTROL__REGNAME)
    public static final FlightControlModule flightControl = null;

    @GameRegistry.ObjectHolder(MODULE_GLIDER__REGNAME)
    public static final GliderModule glider = null;

    @GameRegistry.ObjectHolder(MODULE_JETBOOTS__REGNAME)
    public static final JetBootsModule jetBoots = null;

    @GameRegistry.ObjectHolder(MODULE_JETPACK__REGNAME)
    public static final JetPackModule jetpack = null;

    @GameRegistry.ObjectHolder(MODULE_JUMP_ASSIST__REGNAME)
    public static final JumpAssistModule jumpAssist = null;

    @GameRegistry.ObjectHolder(MODULE_PARACHUTE__REGNAME)
    public static final ParachuteModule parachute = null;

    @GameRegistry.ObjectHolder(MODULE_SHOCK_ABSORBER__REGNAME)
    public static final ShockAbsorberModule shockAbsorber = null;

    @GameRegistry.ObjectHolder(MODULE_SPRINT_ASSIST__REGNAME)
    public static final SprintAssistModule sprint_assist = null;

    @GameRegistry.ObjectHolder(MODULE_SWIM_BOOST__REGNAME)
    public static final SwimAssistModule swim_assist = null;

    // Special ------------------------------------------------------------------------------------
    @GameRegistry.ObjectHolder(MODULE_CLOCK__REGNAME)
    public static final ClockModule clock = null;

    @GameRegistry.ObjectHolder(MODULE_COMPASS__REGNAME)
    public static final CompassModule compass = null;

    @GameRegistry.ObjectHolder(MODULE_ACTIVE_CAMOUFLAGE__REGNAME)
    public static InvisibilityModule invisibility = null;

    @GameRegistry.ObjectHolder(MODULE_MAGNET__REGNAME)
    public static final MagnetModule magnet = null;

    // Vision -------------------------------------------------------------------------------------
    @GameRegistry.ObjectHolder(BINOCULARS_MODULE__REGNAME)
    public static final BinocularsModule binoculars = null;

    @GameRegistry.ObjectHolder(MODULE_NIGHT_VISION__REGNAME)
    public static final NightVisionModule night_vision = null;

    // Mining Enhancements ------------------------------------------------------------------------
    @GameRegistry.ObjectHolder(MODULE_AOE_PICK_UPGRADE__REGNAME)
    public static final AOEPickUpgradeModule aoePickUpgrade = null;

    @GameRegistry.ObjectHolder(MODULE_SILK_TOUCH__REGNAME)
    public static final SilkTouchModule silk_touch = null;

    @GameRegistry.ObjectHolder(MODULE_FORTUNE_REGNAME)
    public static final FortuneModule fortuneModule = null;

    // Tools --------------------------------------------------------------------------------------
    @GameRegistry.ObjectHolder(MODULE_AQUA_AFFINITY__REGNAME)
    public static final AquaAffinityModule aquaAffinity = null;

    @GameRegistry.ObjectHolder(MODULE_AXE__REGNAME)
    public static final AxeModule axe = null;

    @GameRegistry.ObjectHolder(MODULE_DIAMOND_PICK_UPGRADE__REGNAME)
    public static final DiamondPickUpgradeModule diamondPickUpgrade = null;

    @GameRegistry.ObjectHolder(MODULE_DIMENSIONAL_RIFT__REGNAME)
    public static DimensionalRiftModule dimRiftGen = null;

    @GameRegistry.ObjectHolder(MODULE_FIELD_TINKER__REGNAME)
    public static FieldTinkerModule fieldTinkerer = null;

    @GameRegistry.ObjectHolder(MODULE_FLINT_AND_STEEL__REGNAME)
    public static FlintAndSteelModule flintAndSteel = null;

    @GameRegistry.ObjectHolder(MODULE_HOE__REGNAME)
    public static final HoeModule hoe = null;

    @GameRegistry.ObjectHolder(MODULE_LEAF_BLOWER__REGNAME)
    public static final LeafBlowerModule leafBlower = null;

    @GameRegistry.ObjectHolder(MODULE_LUX_CAPACITOR__REGNAME)
    public static final LuxCapacitorModule luxcapacitor_module = null;

    @GameRegistry.ObjectHolder(MODULE_PORTABLE_CRAFTING__REGNAME)
    public static final PortableCraftingModule portableCraftingTable = null;

    @GameRegistry.ObjectHolder(MODULE_PICKAXE__REGNAME)
    public static final PickaxeModule pickaxe = null;

    @GameRegistry.ObjectHolder(MODULE_SHEARS__REGNAME)
    public static final ShearsModule shears = null;

    @GameRegistry.ObjectHolder(MODULE_SHOVEL__REGNAME)
    public static final ShovelModule shovel = null;

    // Weapons ------------------------------------------------------------------------------------
    @GameRegistry.ObjectHolder(MODULE_BLADE_LAUNCHER__REGNAME)
    public static final BladeLauncherModule bladeLauncher = null;

    @GameRegistry.ObjectHolder(MODULE_LIGHTNING__REGNAME)
    public static final LightningModule lightningSummoner = null;

    @GameRegistry.ObjectHolder(MODULE_MELEE_ASSIST__REGNAME)
    public static final MeleeAssistModule meleeAssist = null;

    @GameRegistry.ObjectHolder(MODULE_PLASMA_CANNON__REGNAME)
    public static final PlasmaCannonModule plasmaCannon = null;

    @GameRegistry.ObjectHolder(MODULE_RAILGUN__REGNAME)
    public static final RailgunModule rainGun = null;

    /**
     * Blocks -------------------------------------------------------------------------------------
     */
    @GameRegistry.ObjectHolder(TINKER_TABLE_REG_NAME)
    public static final BlockTinkerTable tinkerTable = null;

    @GameRegistry.ObjectHolder(LUX_CAPACITOR_REG_NAME)
    public static final BlockLuxCapacitor luxCapacitor = null;

    /**
     * Fluid --------------------------------------------------------------------------------------
     */
    public static final LiquidNitrogen liquidNitrogen = new LiquidNitrogen();
    @GameRegistry.ObjectHolder(LIQUID_NITROGEN__REGNAME)
    public static final BlockFluidLiquidNitrogen blockLiquidNitrogen = null;
}
