package net.machinemuse.powersuits.basemod;

import net.machinemuse.numina.basemod.MuseLogger;
import net.machinemuse.powersuits.block.BlockLuxCapacitor;
import net.machinemuse.powersuits.block.BlockTinkerTable;
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
import net.machinemuse.powersuits.item.module.energy.generation.KineticGeneratorModule;
import net.machinemuse.powersuits.item.module.energy.generation.AdvancedSolarGeneratorModule;
import net.machinemuse.powersuits.item.module.energy.generation.BasicSolarGeneratorModule;
import net.machinemuse.powersuits.item.module.energy.generation.ThermalGeneratorModule;
import net.machinemuse.powersuits.item.module.energy.storage.EnergyStorageModule;
import net.machinemuse.powersuits.item.module.environmental.*;
import net.machinemuse.powersuits.item.module.miningenhancement.*;
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
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import static net.machinemuse.powersuits.basemod.ModularPowersuits.MODID;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD)
public enum MPSItems {
    INSTANCE;

    public static final MPSCreativeTab creativeTab = new MPSCreativeTab();
    /**
     * Armor --------------------------------------------------------------------------------------
     */
    public static final String powerArmorHelmetRegName = MODID + ":powerarmor.head";
    @ObjectHolder(powerArmorHelmetRegName)
    public static final ItemPowerArmorHelmet powerArmorHead = null;

    public static final String powerArmorChestPlateRegName = MODID + ":powerarmor.torso";
    @ObjectHolder(powerArmorChestPlateRegName)
    public static final ItemPowerArmorChestplate powerArmorTorso = null;

    public static final String powerArmorLeggingsRegName = MODID + ":powerarmor.legs";
    @ObjectHolder(powerArmorLeggingsRegName)
    public static final ItemPowerArmorLeggings powerArmorLegs = null;

    public static final String powerArmorBootsRegName = MODID + ":powerarmor.feet";
    @ObjectHolder(powerArmorBootsRegName)
    public static final ItemPowerArmorBoots powerArmorFeet = null;

    /**
     * HandHeld -----------------------------------------------------------------------------------
     */
    public static final String powerFistRegName = MODID + ":powerfist";
    @ObjectHolder(powerFistRegName)
    public static final ItemPowerFist powerFist = null;

    /**
     * Components ---------------------------------------------------------------------------------
     */
    public static final String componentWiringRegName = MODID + ":component.wiring";
    @ObjectHolder(componentWiringRegName)
    public static final ItemComponent wiring = null;

    public static final String componentSolenoidRegName = MODID + ":component.solenoid";
    @ObjectHolder(componentSolenoidRegName)
    public static final ItemComponent solenoid = null;

    public static final String componentServoRegName = MODID + ":component.servo";
    @ObjectHolder(componentServoRegName)
    public static final ItemComponent servo = null;

    public static final String componentGliderWingRegName = MODID + ":component.glider_wing";
    @ObjectHolder(componentGliderWingRegName)
    public static final ItemComponent glider_wing = null;

    public static final String componentIonThrusterRegName = MODID + ":component.ion_thruster";
    @ObjectHolder(componentIonThrusterRegName)
    public static final ItemComponent ion_thruster = null;

    public static final String component_LV_CapacitorRegName = MODID + ":component.capacitor.lv";
    @ObjectHolder(component_LV_CapacitorRegName)
    public static final ItemComponent lv_capacitor = null;

    public static final String component_MV_CapacitorRegName = MODID + ":component.capacitor.mv";
    @ObjectHolder(component_LV_CapacitorRegName)
    public static final ItemComponent mv_capacitor = null;

    public static final String component_HV_CapacitorRegName = MODID + ":component.capacitor.hv";
    @ObjectHolder(component_LV_CapacitorRegName)
    public static final ItemComponent hv_capacitor = null;

    public static final String component_EV_CapacitorRegName = MODID + ":component.capacitor.ev";
    @ObjectHolder(component_LV_CapacitorRegName)
    public static final ItemComponent ev_capacitor = null;

    public static final String componentParachuteRegName = MODID + ":component.parachute";
    @ObjectHolder(componentParachuteRegName)
    public static final ItemComponent component_parachute = null;

    public static final String componentLeatherPlatingRegName = MODID + ":component.plating.leather";
    @ObjectHolder(componentLeatherPlatingRegName)
    public static final ItemComponent leatherPlating = null;

    public static final String componentIronPlatingRegName = MODID + ":component.plating.iron";
    @ObjectHolder(componentIronPlatingRegName)
    public static final ItemComponent ironPlating = null;

    public static final String componentDiamondPlatingRegName = MODID + ":component.plating.diamond";
    @ObjectHolder(componentDiamondPlatingRegName)
    public static final ItemComponent diamondPlating = null;

    public static final String componentFieldEmitterRegName = MODID + ":component.field_emitter";
    @ObjectHolder(componentFieldEmitterRegName)
    public static final ItemComponent fieldEmitter = null;

    public static final String componentLaserEmitterRegName = MODID + ":component.laser_emitter";
    @ObjectHolder(componentLaserEmitterRegName)
    public static final ItemComponent laserEmitter = null;

    public static final String componentCarbonMyofiberRegName = MODID + ":component.carbon_myofiber";
    @ObjectHolder(componentCarbonMyofiberRegName)
    public static final ItemComponent carbonMyofiber = null;

    public static final String componentControlCircuitRegName = MODID + ":component.control_circuit";
    @ObjectHolder(componentControlCircuitRegName)
    public static final ItemComponent controlCircuit = null;

    public static final String componentMyofiberGelRegName = MODID + ":component.myofiber_gel";
    @ObjectHolder(componentMyofiberGelRegName)
    public static final ItemComponent myofiberGel = null;

    public static final String componentArtificialMuscleRegName = MODID + ":component.artificial_muscle";
    @ObjectHolder(componentArtificialMuscleRegName)
    public static final ItemComponent artificialMuscle = null;

    public static final String componentSolarPaneltRegName = MODID + ":component.solar_panel";
    @ObjectHolder(componentSolarPaneltRegName)
    public static final ItemComponent solarPanel = null;

    public static final String componentMagnetRegName = MODID + ":component.magnet";
    @ObjectHolder(componentMagnetRegName)
    public static final ItemComponent component_magnet = null;

    public static final String componentComputerChipRegName = MODID + ":component.computer_chip";
    @ObjectHolder(componentComputerChipRegName)
    public static final ItemComponent computerChip = null;

    public static final String componentRubberHoseRegName = MODID + ":component.rubber_hose";
    @ObjectHolder(componentRubberHoseRegName)
    public static final ItemComponent rubberHose = null;

    /**
     * Modules ------------------------------------------------------------------------------------
     */
    // Armor --------------------------------------------------------------------------------------
    public static final String MODULE_LEATHER_PLATING__REGNAME = MODID + ":module.armor.plating.leather";
    @ObjectHolder(MODULE_LEATHER_PLATING__REGNAME)
    public static final LeatherPlatingModule moduleLeatherPlating = null;

    public static final String MODULE_IRON_PLATING__REGNAME =  MODID + ":module.armor.plating.iron";
    @ObjectHolder(MODULE_IRON_PLATING__REGNAME)
    public static final IronPlatingModule moduleIronPlating = null;

    public static final String MODULE_DIAMOND_PLATING__REGNAME =  MODID + ":module.armor.plating.diamond";
    @ObjectHolder(MODULE_DIAMOND_PLATING__REGNAME)
    public static final DiamondPlatingModule moduleDiamondPlating = null;

    public static final String MODULE_ENERGY_SHIELD__REGNAME =  MODID + ":module.armor.energy_shield";
    @ObjectHolder(MODULE_ENERGY_SHIELD__REGNAME)
    public static final EnergyShieldModule moduleEnergyShield = null;

    // Cosmetic -----------------------------------------------------------------------------------
    public static final String MODULE_TRANSPARENT_ARMOR__REGNAME = MODID + ":module.cosmetic.transparent_armor";
    @ObjectHolder(MODULE_TRANSPARENT_ARMOR__REGNAME)
    public static final TransparentArmorModule moduleTransparentArmor = null;

    // Energy Storage -----------------------------------------------------------------------------
    public static final String MODULE_BATTERY_BASIC__REGNAME = MODID + ":battery.basic";
    @ObjectHolder(MODULE_BATTERY_BASIC__REGNAME)
    public static final EnergyStorageModule moduleBatteryBasic = null;

    public static final String MODULE_BATTERY_ADVANCED__REGNAME = MODID + ":battery.advanced";
    @ObjectHolder(MODULE_BATTERY_ADVANCED__REGNAME)
    public static final EnergyStorageModule moduleBatteryAdvanced = null;

    public static final String MODULE_BATTERY_ELITE__REGNAME = MODID + ":battery.elite";
    @ObjectHolder(MODULE_BATTERY_ELITE__REGNAME)
    public static final EnergyStorageModule moduleBatteryElite = null;

    public static final String MODULE_BATTERY_ULTIMATE__REGNAME = MODID + ":battery.ultimate";
    @ObjectHolder(MODULE_BATTERY_ULTIMATE__REGNAME)
    public static final EnergyStorageModule moduleBatteryUltimate = null;

    // Energy Generation -----------------------------------------------------------------------------
    public static final String MODULE_SOLAR_GENERATOR__REGNAME = MODID + ":generator.solar";
    @ObjectHolder(MODULE_SOLAR_GENERATOR__REGNAME)
    public static final BasicSolarGeneratorModule solarGenerator = null;

    public static final String MODULE_ADVANCED_SOLAR_GENERATOR__REGNAME = MODID + ":generator.solar.adv";
    @ObjectHolder(MODULE_ADVANCED_SOLAR_GENERATOR__REGNAME)
    public static final AdvancedSolarGeneratorModule advSolarGenerator = null;

    // TODO:
//    public static final String MODULE_COAL_GEN__REGNAME = MODID + ":coalGenerator";
//    @ObjectHolder()

    public static final String MODULE_KINETIC_GENERATOR__REGNAME = MODID + ":generator.kinetic";
    @ObjectHolder(MODULE_KINETIC_GENERATOR__REGNAME)
    public static final KineticGeneratorModule kineticGenerator = null;

    public static final String MODULE_THERMAL_GENERATOR__REGNAME = MODID + ":generator.thermal";
    @ObjectHolder(MODULE_THERMAL_GENERATOR__REGNAME)
    public static final ThermalGeneratorModule thermalGenerator = null;

    // todo
    // Debug --------------------------------------------------------------------------------------
    public static final String MODULE_DEBUG = MODID + ":debug_module";

    // Environmental ------------------------------------------------------------------------------
    public static final String MODULE_COOLING_SYSTEM__REGNAME = MODID + ":cooling_system";
    @ObjectHolder(MODULE_COOLING_SYSTEM__REGNAME)
    public static CoolingSystemModule basicCoolingSystem = null;

    public static final String MODULE_FLUID_TANK__REGNAME = MODID + ":fluid_tank";
    @ObjectHolder(MODULE_FLUID_TANK__REGNAME)
    public static final FluidTankModule fluidTankModule = null;

//    public static final String MODULE_AIRTIGHT_SEAL__REGNAME = MODID + ":airtight_seal";
//    @ObjectHolder(MODULE_AIRTIGHT_SEAL__REGNAME)
//    public static final ItemModuleAirtightSeal airtightSeal = null;
//
//    public static final String MODULE_APIARIST_ARMOR__REGNAME = MODID + ":apiarist_armor";
//    @ObjectHolder(MODULE_APIARIST_ARMOR__REGNAME)
//    public static final ItemModuleApiaristArmor apiaristArmor = null;
//
//    public static final String MODULE_HAZMAT__REGNAME = MODID + ":hazmat";
//    @ObjectHolder(MODULE_HAZMAT__REGNAME)
//    public static final ItemModuleHazmat hazmat = null;

    public static final String MODULE_AUTO_FEEDER__REGNAME = MODID + ":auto_feeder";
    @ObjectHolder(MODULE_AUTO_FEEDER__REGNAME)
    public static final AutoFeederModule autoFeeder = null;

    public static final String MODULE_MOB_REPULSOR__REGNAME = MODID + ":mob_repulsor";
    @ObjectHolder(MODULE_MOB_REPULSOR__REGNAME)
    public static final MobRepulsorModule mobRepulsor = null;

    public static final String MODULE_WATER_ELECTROLYZER__REGNAME = MODID + ":water_electrolyzer";
    @ObjectHolder(MODULE_WATER_ELECTROLYZER__REGNAME)
    public static final WaterElectrolyzerModule waterElectrolyzer = null;

    // Movement -----------------------------------------------------------------------------------
    public static final String MODULE_BLINK_DRIVE__REGNAME = MODID + ":blink_drive";
    @ObjectHolder(MODULE_BLINK_DRIVE__REGNAME)
    public static final BlinkDriveModule blinkDrive = null;

    public static final String MODULE_CLIMB_ASSIST__REGNAME = MODID + ":climb_assist";
    @ObjectHolder(MODULE_CLIMB_ASSIST__REGNAME)
    public static final ClimbAssistModule climbAssist = null;

    public static final String MODULE_FLIGHT_CONTROL__REGNAME = MODID + ":flight_control";
    @ObjectHolder(MODULE_FLIGHT_CONTROL__REGNAME)
    public static final FlightControlModule flightControl = null;

    public static final String MODULE_GLIDER__REGNAME = MODID + ":glider";
    @ObjectHolder(MODULE_GLIDER__REGNAME)
    public static final GliderModule glider = null;

    public static final String MODULE_JETBOOTS__REGNAME = MODID + ":jet_boots";
    @ObjectHolder(MODULE_JETBOOTS__REGNAME)
    public static final JetBootsModule jetBoots = null;

    public static final String MODULE_JETPACK__REGNAME = MODID + ":jetpack";
    @ObjectHolder(MODULE_JETPACK__REGNAME)
    public static final JetPackModule jetpack = null;

    public static final String MODULE_JUMP_ASSIST__REGNAME = MODID + ":jump_assist";
    @ObjectHolder(MODULE_JUMP_ASSIST__REGNAME)
    public static final JumpAssistModule jumpAssist = null;

    public static final String MODULE_PARACHUTE__REGNAME = MODID + ":parachute";
    @ObjectHolder(MODULE_PARACHUTE__REGNAME)
    public static final ParachuteModule parachute = null;

    public static final String MODULE_SHOCK_ABSORBER__REGNAME = MODID + ":shock_absorber";
    @ObjectHolder(MODULE_SHOCK_ABSORBER__REGNAME)
    public static final ShockAbsorberModule shockAbsorber = null;

    public static final String MODULE_SPRINT_ASSIST__REGNAME = MODID + ":sprint_assist";
    @ObjectHolder(MODULE_SPRINT_ASSIST__REGNAME)
    public static final SprintAssistModule sprint_assist = null;

    public static final String MODULE_SWIM_BOOST__REGNAME = MODID + ":swim_assist";
    @ObjectHolder(MODULE_SWIM_BOOST__REGNAME)
    public static final SwimAssistModule swim_assist = null;

    // Special ------------------------------------------------------------------------------------
    public static final String MODULE_CLOCK__REGNAME = MODID + ":clock";
    @ObjectHolder(MODULE_CLOCK__REGNAME)
    public static final ClockModule clock = null;

    public static final String MODULE_COMPASS__REGNAME = MODID + ":compass";
    @ObjectHolder(MODULE_COMPASS__REGNAME)
    public static final CompassModule compass = null;

    public static final String MODULE_ACTIVE_CAMOUFLAGE__REGNAME = MODID + ":invisibility";
    @ObjectHolder(MODULE_ACTIVE_CAMOUFLAGE__REGNAME)
    public static InvisibilityModule invisibility = null;

    public static final String MODULE_MAGNET__REGNAME = MODID + ":magnet";
    @ObjectHolder(MODULE_MAGNET__REGNAME)
    public static final MagnetModule magnet = null;

    // Vision -------------------------------------------------------------------------------------
    public static final String BINOCULARS_MODULE__REGNAME = MODID + ":binoculars";
    @ObjectHolder(BINOCULARS_MODULE__REGNAME)
    public static final BinocularsModule binoculars = null;

    public static final String MODULE_NIGHT_VISION__REGNAME = MODID + ":night_vision";
    @ObjectHolder(MODULE_NIGHT_VISION__REGNAME)
    public static final ItemModuleNightVision night_vision = null;

//    public static final String MODULE_THAUM_GOGGLES__REGNAME = MODID + ":aurameter"; // no icon!!
//    @ObjectHolder(MODULE_THAUM_GOGGLES__REGNAME)
//    public static final ItemModuleThaumicGoggles aurameter = null;

    // Mining Enhancements ------------------------------------------------------------------------
    public static final String MODULE_AOE_PICK_UPGRADE__REGNAME = MODID + ":aoe_pick_upgrade"; // no icon
    @ObjectHolder(MODULE_AOE_PICK_UPGRADE__REGNAME)
    public static final AOEPickUpgradeModule aoePickUpgrade = null;

    public static final String MODULE_SILK_TOUCH__REGNAME = MODID + ":silk_touch";
    @ObjectHolder(MODULE_SILK_TOUCH__REGNAME)
    public static final ItemModuleSilkTouch silk_touch = null;

//    public static final String MODULE_MAD__REGNAME = MODID + ":mad";
//    @ObjectHolder(MODULE_MAD__REGNAME)
//    public static final ItemModuleMAD madModule = null;

    public static final String MODULE_FORTUNE_REGNAME = MODID + ":fortune";
    @ObjectHolder(MODULE_FORTUNE_REGNAME)
    public static final FortuneModule fortuneModule = null;

    // Tools --------------------------------------------------------------------------------------
//    public static final String MODULE_APPENG_EC_WIRELESS_FLUID__REGNAME = MODID + ":appeng_ec_wireless_fluid";
//    @ObjectHolder(MODULE_APPENG_EC_WIRELESS_FLUID__REGNAME)
//    public static ItemModuleAppEngWirelessFluid appengECWirelessFluid = null;
//
//    public static final String MODULE_APPENG_WIRELESS__REGNAME = MODID + ":appeng_wireless";
//    @ObjectHolder(MODULE_APPENG_WIRELESS__REGNAME)
//    public static ItemModuleAppEngWireless appengWireless = null;

    public static final String MODULE_AQUA_AFFINITY__REGNAME = MODID + ":aqua_affinity";
    @ObjectHolder(MODULE_AQUA_AFFINITY__REGNAME)
    public static final AquaAffinityModule aquaAffinity = null;

    public static final String MODULE_AXE__REGNAME = MODID + ":axe";
    @ObjectHolder(MODULE_AXE__REGNAME)
    public static final ItemModuleAxe axe = null;
//
//    public static final String MODULE_CHISEL__REGNAME = MODID + ":chisel";
//    @ObjectHolder(MODULE_CHISEL__REGNAME)
//    public static final ItemModuleChisel chisel = null;

    public static final String MODULE_DIAMOND_PICK_UPGRADE__REGNAME = MODID + ":diamond_pick_upgrade";
    @ObjectHolder(MODULE_DIAMOND_PICK_UPGRADE__REGNAME)
    public static final DiamondPickUpgradeModule diamondPickUpgrade = null;

    public static final String MODULE_DIMENSIONAL_RIFT__REGNAME = MODID + ":dim_rift_gen";
    @ObjectHolder(MODULE_DIMENSIONAL_RIFT__REGNAME)
    public static DimensionalRiftModule dimRiftGen = null;

    public static final String MODULE_FIELD_TINKER__REGNAME = MODID + ":field_tinkerer";
    @ObjectHolder(MODULE_FIELD_TINKER__REGNAME)
    public static FieldTinkerModule fieldTinkerer = null;

    public static final String MODULE_FLINT_AND_STEEL__REGNAME = MODID + ":flint_and_steel";
    @ObjectHolder(MODULE_FLINT_AND_STEEL__REGNAME)
    public static FlintAndSteelModule flintAndSteel = null;

//    public static final String MODULE_GRAFTER__REGNAME = MODID + ":grafter";
//    @ObjectHolder(MODULE_GRAFTER__REGNAME)
//    public static ItemModuleGrafter grafter = null;

    public static final String MODULE_HOE__REGNAME = MODID + ":hoe";
    @ObjectHolder(MODULE_HOE__REGNAME)
    public static final HoeModule hoe = null;

    public static final String MODULE_LEAF_BLOWER__REGNAME = MODID + ":leaf_blower";
    @ObjectHolder(MODULE_LEAF_BLOWER__REGNAME)
    public static final LeafBlowerModule leafBlower = null;

    public static final String MODULE_LUX_CAPACITOR__REGNAME = MODID + ":luxcapacitor_module.json";
    @ObjectHolder(MODULE_LUX_CAPACITOR__REGNAME)
    public static final LuxCapacitorModule luxcapacitor_module = null;

//    public static final String MODULE_FIELD_TELEPORTER__REGNAME = MODID + ":mffs_field_teleporter";
//    @ObjectHolder(MODULE_FIELD_TELEPORTER__REGNAME)
//    public static final ItemModuleMFFSFieldTeleporter mffs_field_teleporter = null;
//
//    public static final String MODULE_OC_TERMINAL__REGNAME = MODID + ":ocTerminal";
//    @ObjectHolder(MODULE_OC_TERMINAL__REGNAME)
//    public static final ItemModuleOCTerminal ocTerminal = null;

    public static final String MODULE_PORTABLE_CRAFTING__REGNAME = MODID + ":portable_crafting_table";
    @ObjectHolder(MODULE_PORTABLE_CRAFTING__REGNAME)
    public static final PortableCraftingModule portableCraftingTable = null;

//    public static final String MODULE_OMNIPROBE__REGNAME = MODID + ":omni_probe";
//    @ObjectHolder(MODULE_OMNIPROBE__REGNAME)
//    public static final ItemModuleOmniProbe omniProbe = null;
//
//    public static final String MODULE_OMNI_WRENCH__REGNAME = MODID + ":omniwrench";
//    @ObjectHolder(MODULE_OMNI_WRENCH__REGNAME)
//    public static final ItemModuleOmniWrench omniwrench = null;
//
//    public static final String MODULE_ORE_SCANNER__REGNAME = MODID + ":ore_scanner";
//    @ObjectHolder(MODULE_ORE_SCANNER__REGNAME)
//    public static final ItemModuleOreScanner oreScanner = null;
//
//    public static final String MODULE_CM_PSD__REGNAME = MODID + ":cmpsd";//"Personal Shrinking Device";
//    @ObjectHolder(MODULE_CM_PSD__REGNAME)
//    public static final ItemModulePersonalShrinking cmPSD = null;

    public static final String MODULE_PICKAXE__REGNAME = MODID + ":pickaxe";
    @ObjectHolder(MODULE_PICKAXE__REGNAME)
    public static final PickaxeModule pickaxe = null;
//
//    public static final String MODULE_REF_STOR_WIRELESS__REGNAME = MODID + ":refined_storage_wireless_grid";//"Refined Storage Wireless Grid";
//    @ObjectHolder(MODULE_REF_STOR_WIRELESS__REGNAME)
//    public static final ItemModuleRefinedStorageWireless refinedStorageWirelessGrid = null;
//
//    public static final String MODULE_SCOOP__REGNAME = MODID + ":scoop";
//    @ObjectHolder(MODULE_SCOOP__REGNAME)
//    public static final ItemModuleScoop scoop = null;

    public static final String MODULE_SHEARS__REGNAME = MODID + ":shears";
    @ObjectHolder(MODULE_SHEARS__REGNAME)
    public static final ShearsModule shears = null;

    public static final String MODULE_SHOVEL__REGNAME = MODID + ":shovel";
    @ObjectHolder(MODULE_SHOVEL__REGNAME)
    public static final ShovelModule shovel = null;
//
//    public static final String MODULE_TREETAP__REGNAME = MODID + ":treetap";
//    @ObjectHolder(MODULE_TREETAP__REGNAME)
//    public static final ItemModuleTreetap treetap = null;

    // Weapons ------------------------------------------------------------------------------------
    public static final String MODULE_BLADE_LAUNCHER__REGNAME = MODID + ":blade_launcher";
    @ObjectHolder(MODULE_BLADE_LAUNCHER__REGNAME)
    public static final BladeLauncherModule bladeLauncher = null;

    public static final String MODULE_LIGHTNING__REGNAME = MODID + ":lightning_summoner";
    @ObjectHolder(MODULE_LIGHTNING__REGNAME)
    public static final LightningModule lightningSummoner = null;

    public static final String MODULE_MELEE_ASSIST__REGNAME = MODID + ":melee_assist";
    @ObjectHolder(MODULE_MELEE_ASSIST__REGNAME)
    public static final MeleeAssistModule meleeAssist = null;

    public static final String MODULE_PLASMA_CANNON__REGNAME = MODID + ":plasma_cannon";
    @ObjectHolder(MODULE_PLASMA_CANNON__REGNAME)
    public static final PlasmaCannonModule plasmaCannon = null;

    public static final String MODULE_RAILGUN__REGNAME = MODID + ":railgun";
    @ObjectHolder(MODULE_RAILGUN__REGNAME)
    public static final RailgunModule rainGun = null;

    /**
     * Blocks -------------------------------------------------------------------------------------
     */
    public static final String tinkerTableRegName = MODID + ":tinkertable";
    public static final String luxCapaRegName = MODID + ":luxcapacitor";

    @ObjectHolder(tinkerTableRegName)
    public static final BlockTinkerTable tinkerTable = null;

    @ObjectHolder(luxCapaRegName)
    public static final BlockLuxCapacitor luxCapacitor = null;

    // TileEntities -------------------------------------------------------------------------------
    @ObjectHolder(tinkerTableRegName + "_tile")
    public static TileEntityType<TinkerTableTileEntity> tinkerTableTileEntityType = null;

    @ObjectHolder(luxCapaRegName + "_tile")
    public static TileEntityType<TileEntityLuxCapacitor> capacitorTileEntityType = null;

//    @ObjectHolder(MODID + ":portal_tile")
//    public static TileEntityType<TileEntityPortal> portalTileEntityType = null;

    // Entities -----------------------------------------------------------------------------------
    @ObjectHolder(MODID + ":luxcapacitor_entity")
    public static EntityType<LuxCapacitorEntity> LUX_CAPACITOR_ENTITY_TYPE;

    @ObjectHolder(MODID + ":plasma_bolt")
    public static EntityType<PlasmaBoltEntity> PLASMA_BOLT_ENTITY_TYPE;

    @ObjectHolder(MODID + ":spinning_blade")
    public static EntityType<SpinningBladeEntity> SPINNING_BLADE_ENTITY_TYPE;


    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(() ->
                        new TileEntityLuxCapacitor(), luxCapacitor).build(null).setRegistryName(luxCapaRegName + "_tile"),

                TileEntityType.Builder.create(() ->
                        new TinkerTableTileEntity(), tinkerTable).build(null).setRegistryName(tinkerTableRegName + "_tile")
        );
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityType<?>> event ){
        event.getRegistry().registerAll(
                EntityType.Builder.<LuxCapacitorEntity>create(LuxCapacitorEntity::new, EntityClassification.MISC)
                        .size(0.25F, 0.25F)
                        .build(MODID + ":luxcapacitor_entity").setRegistryName(MODID + ":luxcapacitor_entity"),

                EntityType.Builder.<SpinningBladeEntity>create(SpinningBladeEntity::new, EntityClassification.MISC)
//                       .size(0.25F, 0.25F)
                        .build(MODID +":spinning_blade").setRegistryName(MODID +":spinning_blade"),

                EntityType.Builder.<PlasmaBoltEntity>create(PlasmaBoltEntity::new, EntityClassification.MISC)
                        .build(MODID +":plasma_bolt").setRegistryName(MODID +":plasma_bolt")
        );
    }

    @SubscribeEvent
    public void registerContainerTypes(final RegistryEvent.Register<ContainerType<?>> event) {
        // see example: https://github.com/Yamahari/ILikeWood/blob/26767c4f60966edd2ba47a84ab28a4f4c0038039/src/main/java/yamahari/ilikewood/Main.java

//        event.getRegistry().registerAll(
//                new ContainerType<>(WoodenWorkbenchContainer::new).setRegistryName("wooden_workbench_container"),
//                new ContainerType<>((p_221504_0_, p_221504_1_) -> new WoodenLecternContainer(p_221504_0_)).setRegistryName("wooden_lectern_container")
//        );
    }



    @SubscribeEvent
    public void registerBlocks(final RegistryEvent.Register<Block> blockRegistryEvent) {
        blockRegistryEvent.getRegistry().registerAll(new BlockLuxCapacitor(luxCapaRegName), new BlockTinkerTable(tinkerTableRegName));
    }

    @SubscribeEvent
    public void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent) {
        MuseLogger.logger.info("Started registering MPS Modules");

        itemRegistryEvent.getRegistry().registerAll(
                // Armor --------------------------------------------------------------------------------------
                new ItemPowerArmorHelmet(powerArmorHelmetRegName),
                new ItemPowerArmorChestplate(powerArmorChestPlateRegName),
                new ItemPowerArmorLeggings(powerArmorLeggingsRegName),
                new ItemPowerArmorBoots(powerArmorBootsRegName),

                // HandHeld -----------------------------------------------------------------------------------
                new ItemPowerFist(powerFistRegName),

                // Components ---------------------------------------------------------------------------------
                new ItemComponent(componentWiringRegName),
                new ItemComponent(componentSolenoidRegName),
                new ItemComponent(componentServoRegName),
                new ItemComponent(componentGliderWingRegName),
                new ItemComponent(componentIonThrusterRegName),
                new ItemComponent(component_LV_CapacitorRegName),
                new ItemComponent(component_MV_CapacitorRegName),
                new ItemComponent(component_HV_CapacitorRegName),
                new ItemComponent(component_EV_CapacitorRegName),
                new ItemComponent(componentParachuteRegName),
                new ItemComponent(componentLeatherPlatingRegName),
                new ItemComponent(componentIronPlatingRegName),
                new ItemComponent(componentDiamondPlatingRegName),
                new ItemComponent(componentFieldEmitterRegName),
                new ItemComponent(componentLaserEmitterRegName),
                new ItemComponent(componentCarbonMyofiberRegName),
                new ItemComponent(componentControlCircuitRegName),
                new ItemComponent(componentMyofiberGelRegName),
                new ItemComponent(componentArtificialMuscleRegName),
                new ItemComponent(componentSolarPaneltRegName),
                new ItemComponent(componentMagnetRegName),
                new ItemComponent(componentComputerChipRegName),
                new ItemComponent(componentRubberHoseRegName),

                // Modules ------------------------------------------------------------------------
                // Armor
                new LeatherPlatingModule(MODULE_LEATHER_PLATING__REGNAME),
                new IronPlatingModule(MODULE_IRON_PLATING__REGNAME),
                new DiamondPlatingModule(MODULE_DIAMOND_PLATING__REGNAME),
                new EnergyShieldModule(MODULE_ENERGY_SHIELD__REGNAME),

                // Cosmetic -----------------------------------------------------------------------
                new TransparentArmorModule(MODULE_TRANSPARENT_ARMOR__REGNAME),

                // Energy Storage -----------------------------------------------------------------
                new EnergyStorageModule(MODULE_BATTERY_BASIC__REGNAME,
                        1000000,
                        1000000),

                new EnergyStorageModule(MODULE_BATTERY_ADVANCED__REGNAME,
                        5000000,
                        5000000),

                new EnergyStorageModule(MODULE_BATTERY_ELITE__REGNAME,
                        50000000,
                        50000000),

                new EnergyStorageModule(MODULE_BATTERY_ULTIMATE__REGNAME,
                        100000000,
                        100000000),

                // Energy Generation --------------------------------------------------------------
                new BasicSolarGeneratorModule(MODULE_SOLAR_GENERATOR__REGNAME),
                new AdvancedSolarGeneratorModule(MODULE_ADVANCED_SOLAR_GENERATOR__REGNAME),
                new KineticGeneratorModule(MODULE_KINETIC_GENERATOR__REGNAME),
                new ThermalGeneratorModule(MODULE_THERMAL_GENERATOR__REGNAME),

                // Environmental ------------------------------------------------------------------
                new CoolingSystemModule(MODULE_COOLING_SYSTEM__REGNAME),
                new FluidTankModule(MODULE_FLUID_TANK__REGNAME),
//                new ItemModuleAirtightSeal(MODULE_AIRTIGHT_SEAL__REGNAME), // fixme contiditional register
//                new ItemModuleApiaristArmor(MODULE_APIARIST_ARMOR__REGNAME),// fixme contiditional register
//                new ItemModuleHazmat(MODULE_HAZMAT__REGNAME),// fixme contiditional register
                new AutoFeederModule(MODULE_AUTO_FEEDER__REGNAME),
                new MobRepulsorModule(MODULE_MOB_REPULSOR__REGNAME),
                new WaterElectrolyzerModule(MODULE_WATER_ELECTROLYZER__REGNAME),

                // Movement -----------------------------------------------------------------------------------
                new BlinkDriveModule(MODULE_BLINK_DRIVE__REGNAME),
                new ClimbAssistModule(MODULE_CLIMB_ASSIST__REGNAME),
                new FlightControlModule(MODULE_FLIGHT_CONTROL__REGNAME),
                new GliderModule(MODULE_GLIDER__REGNAME),
                new JetBootsModule(MODULE_JETBOOTS__REGNAME),
                new JetPackModule(MODULE_JETPACK__REGNAME),
                new JumpAssistModule(MODULE_JUMP_ASSIST__REGNAME),
                new ParachuteModule(MODULE_PARACHUTE__REGNAME),
                new ShockAbsorberModule(MODULE_SHOCK_ABSORBER__REGNAME),
                new SprintAssistModule(MODULE_SPRINT_ASSIST__REGNAME),
                new SwimAssistModule(MODULE_SWIM_BOOST__REGNAME),

                // Special ------------------------------------------------------------------------------------
                new ClockModule(MODULE_CLOCK__REGNAME),
                new CompassModule(MODULE_COMPASS__REGNAME),
                new InvisibilityModule(MODULE_ACTIVE_CAMOUFLAGE__REGNAME),
                new MagnetModule(MODULE_MAGNET__REGNAME),

                // Vision -------------------------------------------------------------------------------------
                new BinocularsModule(BINOCULARS_MODULE__REGNAME),
                new ItemModuleNightVision(MODULE_NIGHT_VISION__REGNAME),
//                new ItemModuleThaumicGoggles(MODULE_THAUM_GOGGLES__REGNAME), // fixme contiditional register

                // Mining Enhancements ------------------------------------------------------------------------
                new AOEPickUpgradeModule(MODULE_AOE_PICK_UPGRADE__REGNAME),
                new ItemModuleSilkTouch(MODULE_SILK_TOUCH__REGNAME),
//                new ItemModuleMAD(MODULE_MAD__REGNAME),// fixme contiditional register
                new FortuneModule(MODULE_FORTUNE_REGNAME),

                // Tools --------------------------------------------------------------------------
//                new ItemModuleAppEngWirelessFluid(MODULE_APPENG_EC_WIRELESS_FLUID__REGNAME), // fixme contiditional register
//                new ItemModuleAppEngWireless(MODULE_APPENG_WIRELESS__REGNAME), // fixme contiditional register
                new AquaAffinityModule(MODULE_AQUA_AFFINITY__REGNAME),
                new ItemModuleAxe(MODULE_AXE__REGNAME),
//                new ItemModuleChisel(MODULE_CHISEL__REGNAME), // fixme contiditional register
                new DiamondPickUpgradeModule(MODULE_DIAMOND_PICK_UPGRADE__REGNAME),
                new DimensionalRiftModule(MODULE_DIMENSIONAL_RIFT__REGNAME),
                new FieldTinkerModule(MODULE_FIELD_TINKER__REGNAME),
                new FlintAndSteelModule(MODULE_FLINT_AND_STEEL__REGNAME),
//                new ItemModuleGrafter(MODULE_GRAFTER__REGNAME), // fixme contiditional register
                new HoeModule(MODULE_HOE__REGNAME),
                new LeafBlowerModule(MODULE_LEAF_BLOWER__REGNAME),
                new LuxCapacitorModule(MODULE_LUX_CAPACITOR__REGNAME),
//                new ItemModuleMFFSFieldTeleporter(MODULE_FIELD_TELEPORTER__REGNAME),// fixme contiditional register
//                new ItemModuleOCTerminal(MODULE_OC_TERMINAL__REGNAME) // TODO???
                new PortableCraftingModule(MODULE_PORTABLE_CRAFTING__REGNAME),
//                new ItemModuleOmniProbe(MODULE_OMNIPROBE__REGNAME),
//                new ItemModuleOmniWrench(MODULE_OMNI_WRENCH__REGNAME), // TODO???
//                new ItemModuleOreScanner(MODULE_ORE_SCANNER__REGNAME),
//                new ItemModulePersonalShrinking(MODULE_CM_PSD__REGNAME),
                new PickaxeModule(MODULE_PICKAXE__REGNAME),
//                new ItemModuleRefinedStorageWireless(MODULE_REF_STOR_WIRELESS__REGNAME),// fixme contiditional register
//                new ItemModuleScoop(MODULE_SCOOP__REGNAME),
                new ShearsModule(MODULE_SHEARS__REGNAME),
                new ShovelModule(MODULE_SHOVEL__REGNAME),
//                new ItemModuleTreetap(MODULE_TREETAP__REGNAME),// fixme contiditional register

                // Debug --------------------------------------------------------------------------
                // todo

                // Weapons ------------------------------------------------------------------------
                new BladeLauncherModule(MODULE_BLADE_LAUNCHER__REGNAME),
                new LightningModule(MODULE_LIGHTNING__REGNAME),
                new MeleeAssistModule(MODULE_MELEE_ASSIST__REGNAME),
                new PlasmaCannonModule(MODULE_PLASMA_CANNON__REGNAME),
                new RailgunModule(MODULE_RAILGUN__REGNAME),

                // ItemBlocks ---------------------------------------------------------------------------------
                new BlockItem(tinkerTable,
                        new Item.Properties().group(MPSItems.INSTANCE.creativeTab))
                        .setRegistryName(new ResourceLocation(tinkerTableRegName)),
                new BlockItem(luxCapacitor,
                        new Item.Properties().group(MPSItems.INSTANCE.creativeTab))
                        .setRegistryName(new ResourceLocation(luxCapaRegName))
        );
    }
}
