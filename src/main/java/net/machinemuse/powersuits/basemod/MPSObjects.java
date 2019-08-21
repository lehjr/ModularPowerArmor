package net.machinemuse.powersuits.basemod;

import net.machinemuse.powersuits.block.BlockLuxCapacitor;
import net.machinemuse.powersuits.block.BlockTinkerTable;
import net.machinemuse.powersuits.containers.MPSCraftingContainer;
import net.machinemuse.powersuits.containers.ModeChangingContainer;
import net.machinemuse.powersuits.containers.ModularItemContainer;
import net.machinemuse.powersuits.containers.TinkerTableContainer;
import net.machinemuse.powersuits.entity.LuxCapacitorEntity;
import net.machinemuse.powersuits.entity.PlasmaBoltEntity;
import net.machinemuse.powersuits.entity.SpinningBladeEntity;
import net.machinemuse.powersuits.item.armor.ItemPowerArmorBoots;
import net.machinemuse.powersuits.item.armor.ItemPowerArmorChestplate;
import net.machinemuse.powersuits.item.armor.ItemPowerArmorHelmet;
import net.machinemuse.powersuits.item.armor.ItemPowerArmorLeggings;
import net.machinemuse.powersuits.item.component.ItemComponent;
import net.machinemuse.powersuits.item.module.armor.DiamondPlatingModule;
import net.machinemuse.powersuits.item.module.armor.EnergyShieldModule;
import net.machinemuse.powersuits.item.module.armor.IronPlatingModule;
import net.machinemuse.powersuits.item.module.armor.LeatherPlatingModule;
import net.machinemuse.powersuits.item.module.cosmetic.TransparentArmorModule;
import net.machinemuse.powersuits.item.module.energy.generation.AdvancedSolarGeneratorModule;
import net.machinemuse.powersuits.item.module.energy.generation.BasicSolarGeneratorModule;
import net.machinemuse.powersuits.item.module.energy.generation.KineticGeneratorModule;
import net.machinemuse.powersuits.item.module.energy.generation.ThermalGeneratorModule;
import net.machinemuse.powersuits.item.module.energy.storage.EnergyStorageModule;
import net.machinemuse.powersuits.item.module.environmental.*;
import net.machinemuse.powersuits.item.module.miningenhancement.AOEPickUpgradeModule;
import net.machinemuse.powersuits.item.module.miningenhancement.AquaAffinityModule;
import net.machinemuse.powersuits.item.module.miningenhancement.FortuneModule;
import net.machinemuse.powersuits.item.module.miningenhancement.ItemModuleSilkTouch;
import net.machinemuse.powersuits.item.module.movement.*;
import net.machinemuse.powersuits.item.module.special.ClockModule;
import net.machinemuse.powersuits.item.module.special.CompassModule;
import net.machinemuse.powersuits.item.module.special.InvisibilityModule;
import net.machinemuse.powersuits.item.module.special.MagnetModule;
import net.machinemuse.powersuits.item.module.tool.*;
import net.machinemuse.powersuits.item.module.vision.BinocularsModule;
import net.machinemuse.powersuits.item.module.vision.ItemModuleNightVision;
import net.machinemuse.powersuits.item.module.weapon.*;
import net.machinemuse.powersuits.item.tool.ItemPowerFist;
import net.machinemuse.powersuits.tileentity.TileEntityLuxCapacitor;
import net.machinemuse.powersuits.tileentity.TinkerTableTileEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

import static net.machinemuse.powersuits.basemod.MPSConstants.MODID;
import static net.machinemuse.powersuits.basemod.MPSRegistryNames.*;


/**
 * Object Holders for the mod
 */
public enum MPSObjects {
    INSTANCE;

    /**
     * Armor --------------------------------------------------------------------------------------
     */
    @ObjectHolder(ITEM__POWER_ARMOR_HELMET__REGNAME)
    public static final ItemPowerArmorHelmet powerArmorHead = null;

    @ObjectHolder(ITEM__POWER_ARMOR_CHESTPLATE__REGNAME)
    public static final ItemPowerArmorChestplate powerArmorTorso = null;

    @ObjectHolder(ITEM__POWER_ARMOR_LEGGINGS__REGNAME)
    public static final ItemPowerArmorLeggings powerArmorLegs = null;

    @ObjectHolder(ITEM__POWER_ARMOR_BOOTS__REGNAME)
    public static final ItemPowerArmorBoots powerArmorFeet = null;

    /**
     * HandHeld -----------------------------------------------------------------------------------
     */
    @ObjectHolder(ITEM__POWER_FIST__REGNAME)
    public static final ItemPowerFist powerFist = null;

    /**
     * Components ---------------------------------------------------------------------------------
     */
    @ObjectHolder(COMPONENT__WIRING__REGNAME)
    public static final ItemComponent wiring = null;

    @ObjectHolder(COMPONENT__SOLENOID__REGNAME)
    public static final ItemComponent solenoid = null;

    @ObjectHolder(COMPONENT__SERVO__REGNAME)
    public static final ItemComponent servo = null;

    @ObjectHolder(COMPONENT__GLIDER_WING__REGNAME)
    public static final ItemComponent glider_wing = null;

    @ObjectHolder(COMPONENT__ION_THRUSTER__REGNAME)
    public static final ItemComponent ion_thruster = null;

    @ObjectHolder(COMPONENT__LV_CAPACITOR__REGNAME)
    public static final ItemComponent lv_capacitor = null;

    @ObjectHolder(COMPONENT__MV_CAPACITOR___REGNAME)
    public static final ItemComponent mv_capacitor = null;

    @ObjectHolder(COMPONENT__HV_CAPACITOR___REGNAME)
    public static final ItemComponent hv_capacitor = null;

    @ObjectHolder(COMPONENT__EV_CAPACITOR___REGNAME)
    public static final ItemComponent ev_capacitor = null;

    @ObjectHolder(COMPONENT__PARACHUTE__REGNAME)
    public static final ItemComponent component_parachute = null;

    @ObjectHolder(COMPONENT__LEATHER_PLATING__REGNAME)
    public static final ItemComponent leatherPlating = null;

    @ObjectHolder(COMPONENT__IRON_PLATING__REGNAME)
    public static final ItemComponent ironPlating = null;

    @ObjectHolder(COMPONENT__DIAMOND_PLATING__REGNAME)
    public static final ItemComponent diamondPlating = null;

    @ObjectHolder(COMPONENT__FIELD_EMITTER__REGNAME)
    public static final ItemComponent fieldEmitter = null;

    @ObjectHolder(COMPONENT__LASER_EMITTER__REGNAME)
    public static final ItemComponent laserEmitter = null;

    @ObjectHolder(COMPONENT__CARBON_MYOFIBER__REGNAME)
    public static final ItemComponent carbonMyofiber = null;

    @ObjectHolder(COMPONENT__CONTROL_CIRCUIT__REGNAME)
    public static final ItemComponent controlCircuit = null;

    @ObjectHolder(COMPONENT__MYOFIBER_GEL__REGNAME)
    public static final ItemComponent myofiberGel = null;

    @ObjectHolder(COMPONENT__ARTIFICIAL_MUSCLE__REGNAME)
    public static final ItemComponent artificialMuscle = null;

    @ObjectHolder(COMPONENT__SOLAR_PANEL__REGNAME)
    public static final ItemComponent solarPanel = null;

    @ObjectHolder(COMPONENT__MAGNET__REGNAME)
    public static final ItemComponent component_magnet = null;

    @ObjectHolder(COMPONENT__COMPUTER_CHIP__REGNAME)
    public static final ItemComponent computerChip = null;

    @ObjectHolder(COMPONENT__RUBBER_HOSE__REGNAME)
    public static final ItemComponent rubberHose = null;

    /**
     * Modules ------------------------------------------------------------------------------------
     */
    // Armor --------------------------------------------------------------------------------------
    @ObjectHolder(MODULE_LEATHER_PLATING__REGNAME)
    public static final LeatherPlatingModule moduleLeatherPlating = null;

    @ObjectHolder(MODULE_IRON_PLATING__REGNAME)
    public static final IronPlatingModule moduleIronPlating = null;

    @ObjectHolder(MODULE_DIAMOND_PLATING__REGNAME)
    public static final DiamondPlatingModule moduleDiamondPlating = null;

    @ObjectHolder(MODULE_ENERGY_SHIELD__REGNAME)
    public static final EnergyShieldModule moduleEnergyShield = null;

    // Cosmetic -----------------------------------------------------------------------------------

    @ObjectHolder(MODULE_TRANSPARENT_ARMOR__REGNAME)
    public static final TransparentArmorModule moduleTransparentArmor = null;

    // Energy Storage -----------------------------------------------------------------------------
    @ObjectHolder(MODULE_BATTERY_BASIC__REGNAME)
    public static final EnergyStorageModule moduleBatteryBasic = null;

    @ObjectHolder(MODULE_BATTERY_ADVANCED__REGNAME)
    public static final EnergyStorageModule moduleBatteryAdvanced = null;

    @ObjectHolder(MODULE_BATTERY_ELITE__REGNAME)
    public static final EnergyStorageModule moduleBatteryElite = null;

    @ObjectHolder(MODULE_BATTERY_ULTIMATE__REGNAME)
    public static final EnergyStorageModule moduleBatteryUltimate = null;

    // Energy Generation -----------------------------------------------------------------------------
    @ObjectHolder(MODULE_SOLAR_GENERATOR__REGNAME)
    public static final BasicSolarGeneratorModule solarGenerator = null;

    @ObjectHolder(MODULE_ADVANCED_SOLAR_GENERATOR__REGNAME)
    public static final AdvancedSolarGeneratorModule advSolarGenerator = null;

    @ObjectHolder(MODULE_KINETIC_GENERATOR__REGNAME)
    public static final KineticGeneratorModule kineticGenerator = null;

    @ObjectHolder(MODULE_THERMAL_GENERATOR__REGNAME)
    public static final ThermalGeneratorModule thermalGenerator = null;

    // todo
    // Debug --------------------------------------------------------------------------------------


    // Environmental ------------------------------------------------------------------------------
    @ObjectHolder(MODULE_COOLING_SYSTEM__REGNAME)
    public static CoolingSystemModule basicCoolingSystem = null;

    @ObjectHolder(MODULE_FLUID_TANK__REGNAME)
    public static final FluidTankModule fluidTankModule = null;

    @ObjectHolder(MODULE_AUTO_FEEDER__REGNAME)
    public static final AutoFeederModule autoFeeder = null;

    @ObjectHolder(MODULE_MOB_REPULSOR__REGNAME)
    public static final MobRepulsorModule mobRepulsor = null;

    @ObjectHolder(MODULE_WATER_ELECTROLYZER__REGNAME)
    public static final WaterElectrolyzerModule waterElectrolyzer = null;

    // Movement -----------------------------------------------------------------------------------
    @ObjectHolder(MODULE_BLINK_DRIVE__REGNAME)
    public static final BlinkDriveModule blinkDrive = null;

    @ObjectHolder(MODULE_CLIMB_ASSIST__REGNAME)
    public static final ClimbAssistModule climbAssist = null;

    @ObjectHolder(MODULE_FLIGHT_CONTROL__REGNAME)
    public static final FlightControlModule flightControl = null;

    @ObjectHolder(MODULE_GLIDER__REGNAME)
    public static final GliderModule glider = null;

    @ObjectHolder(MODULE_JETBOOTS__REGNAME)
    public static final JetBootsModule jetBoots = null;

    @ObjectHolder(MODULE_JETPACK__REGNAME)
    public static final JetPackModule jetpack = null;

    @ObjectHolder(MODULE_JUMP_ASSIST__REGNAME)
    public static final JumpAssistModule jumpAssist = null;

    @ObjectHolder(MODULE_PARACHUTE__REGNAME)
    public static final ParachuteModule parachute = null;

    @ObjectHolder(MODULE_SHOCK_ABSORBER__REGNAME)
    public static final ShockAbsorberModule shockAbsorber = null;

    @ObjectHolder(MODULE_SPRINT_ASSIST__REGNAME)
    public static final SprintAssistModule sprint_assist = null;

    @ObjectHolder(MODULE_SWIM_BOOST__REGNAME)
    public static final SwimAssistModule swim_assist = null;

    // Special ------------------------------------------------------------------------------------
    @ObjectHolder(MODULE_CLOCK__REGNAME)
    public static final ClockModule clock = null;

    @ObjectHolder(MODULE_COMPASS__REGNAME)
    public static final CompassModule compass = null;

    @ObjectHolder(MODULE_ACTIVE_CAMOUFLAGE__REGNAME)
    public static InvisibilityModule invisibility = null;

    @ObjectHolder(MODULE_MAGNET__REGNAME)
    public static final MagnetModule magnet = null;

    // Vision -------------------------------------------------------------------------------------
    @ObjectHolder(BINOCULARS_MODULE__REGNAME)
    public static final BinocularsModule binoculars = null;

    @ObjectHolder(MODULE_NIGHT_VISION__REGNAME)
    public static final ItemModuleNightVision night_vision = null;

    // Mining Enhancements ------------------------------------------------------------------------
    @ObjectHolder(MODULE_AOE_PICK_UPGRADE__REGNAME)
    public static final AOEPickUpgradeModule aoePickUpgrade = null;

    @ObjectHolder(MODULE_SILK_TOUCH__REGNAME)
    public static final ItemModuleSilkTouch silk_touch = null;

    @ObjectHolder(MODULE_FORTUNE_REGNAME)
    public static final FortuneModule fortuneModule = null;

    // Tools --------------------------------------------------------------------------------------
    @ObjectHolder(MODULE_AQUA_AFFINITY__REGNAME)
    public static final AquaAffinityModule aquaAffinity = null;

    @ObjectHolder(MODULE_AXE__REGNAME)
    public static final ItemModuleAxe axe = null;

    @ObjectHolder(MODULE_DIAMOND_PICK_UPGRADE__REGNAME)
    public static final DiamondPickUpgradeModule diamondPickUpgrade = null;

    @ObjectHolder(MODULE_DIMENSIONAL_RIFT__REGNAME)
    public static DimensionalRiftModule dimRiftGen = null;

    @ObjectHolder(MODULE_FIELD_TINKER__REGNAME)
    public static FieldTinkerModule fieldTinkerer = null;

    @ObjectHolder(MODULE_FLINT_AND_STEEL__REGNAME)
    public static FlintAndSteelModule flintAndSteel = null;

    @ObjectHolder(MODULE_HOE__REGNAME)
    public static final HoeModule hoe = null;

    @ObjectHolder(MODULE_LEAF_BLOWER__REGNAME)
    public static final LeafBlowerModule leafBlower = null;

    @ObjectHolder(MODULE_LUX_CAPACITOR__REGNAME)
    public static final LuxCapacitorModule luxcapacitor_module = null;

    @ObjectHolder(MODULE_PORTABLE_CRAFTING__REGNAME)
    public static final PortableCraftingModule portableCraftingTable = null;

    @ObjectHolder(MODULE_PICKAXE__REGNAME)
    public static final PickaxeModule pickaxe = null;

    @ObjectHolder(MODULE_SHEARS__REGNAME)
    public static final ShearsModule shears = null;

    @ObjectHolder(MODULE_SHOVEL__REGNAME)
    public static final ShovelModule shovel = null;

    // Weapons ------------------------------------------------------------------------------------
    @ObjectHolder(MODULE_BLADE_LAUNCHER__REGNAME)
    public static final BladeLauncherModule bladeLauncher = null;

    @ObjectHolder(MODULE_LIGHTNING__REGNAME)
    public static final LightningModule lightningSummoner = null;

    @ObjectHolder(MODULE_MELEE_ASSIST__REGNAME)
    public static final MeleeAssistModule meleeAssist = null;

    @ObjectHolder(MODULE_PLASMA_CANNON__REGNAME)
    public static final PlasmaCannonModule plasmaCannon = null;

    @ObjectHolder(MODULE_RAILGUN__REGNAME)
    public static final RailgunModule rainGun = null;

    /**
     * Blocks -------------------------------------------------------------------------------------
     */
    @ObjectHolder(TINKER_TABLE_REG_NAME)
    public static final BlockTinkerTable tinkerTable = null;

    @ObjectHolder(LUX_CAPACITOR_REG_NAME)
    public static final BlockLuxCapacitor luxCapacitor = null;

    /**
     * TileEntities -------------------------------------------------------------------------------
     */
    @ObjectHolder(TINKER_TABLE_REG_NAME + "_tile")
    public static TileEntityType<TinkerTableTileEntity> tinkerTableTileEntityType = null;

    @ObjectHolder(LUX_CAPACITOR_REG_NAME + "_tile")
    public static TileEntityType<TileEntityLuxCapacitor> capacitorTileEntityType = null;

    // Entities -----------------------------------------------------------------------------------
    @ObjectHolder(MODID + ":luxcapacitor_entity")
    public static EntityType<LuxCapacitorEntity> LUX_CAPACITOR_ENTITY_TYPE;

    @ObjectHolder(MODID + ":plasma_bolt")
    public static EntityType<PlasmaBoltEntity> PLASMA_BOLT_ENTITY_TYPE;

    @ObjectHolder(MODID + ":spinning_blade")
    public static EntityType<SpinningBladeEntity> SPINNING_BLADE_ENTITY_TYPE;

    /**
     * Container Types ----------------------------------------------------------------------------
     */
    @ObjectHolder(MODID + ":mode_changing_container_type")
    public static final ContainerType<ModeChangingContainer> MODE_CHANGING_CONTAINER_TYPE = null;

    @ObjectHolder(MODID + ":module_config_container_type")
    public static final ContainerType<TinkerTableContainer> MODULE_CONFIG_CONTAINER_TYPE = null;

    @ObjectHolder(MODID + ":cosmetic_config_container_type")
    public static final ContainerType<TinkerTableContainer> COSMETIC_CONFIG_CONTAINER_TYPE = null;

    @ObjectHolder(MODID + ":table_key_config_container_type")
    public static final ContainerType<TinkerTableContainer> KEY_CONFIG_CONTAINER_TYPE = null;

    @ObjectHolder(MODID + ":crafting_container")
    public static final ContainerType<MPSCraftingContainer> MPS_CRAFTING_CONTAINER_TYPE = null;

    @ObjectHolder(MODID + ":modular_item_container_type")
    public static final ContainerType<ModularItemContainer> MODULAR_ITEM_CONTAINER_CONTAINER_TYPE = null;
}
