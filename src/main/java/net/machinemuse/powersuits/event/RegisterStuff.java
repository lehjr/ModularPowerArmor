package net.machinemuse.powersuits.event;

import net.machinemuse.numina.basemod.MuseLogger;
import net.machinemuse.powersuits.basemod.MPSCreativeTab;
import net.machinemuse.powersuits.basemod.MPSObjects;
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
import net.machinemuse.powersuits.item.module.vision.NightVisionModule;
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
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.machinemuse.powersuits.basemod.MPSConstants.MODID;
import static net.machinemuse.powersuits.basemod.MPSRegistryNames.*;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public enum RegisterStuff {
    INSTANCE;


    public static final MPSCreativeTab creativeTab = new MPSCreativeTab();

    @SubscribeEvent
    public void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent) {
        MuseLogger.logger.info("Started registering MPS Modules");

        itemRegistryEvent.getRegistry().registerAll(
                // Armor --------------------------------------------------------------------------------------
                new ItemPowerArmorHelmet(ITEM__POWER_ARMOR_HELMET__REGNAME),
                new ItemPowerArmorChestplate(ITEM__POWER_ARMOR_CHESTPLATE__REGNAME),
                new ItemPowerArmorLeggings(ITEM__POWER_ARMOR_LEGGINGS__REGNAME),
                new ItemPowerArmorBoots(ITEM__POWER_ARMOR_BOOTS__REGNAME),

                // HandHeld -----------------------------------------------------------------------------------
                new ItemPowerFist(ITEM__POWER_FIST__REGNAME),

                // Components ---------------------------------------------------------------------------------
                new ItemComponent(COMPONENT__WIRING__REGNAME),
                new ItemComponent(COMPONENT__SOLENOID__REGNAME),
                new ItemComponent(COMPONENT__SERVO__REGNAME),
                new ItemComponent(COMPONENT__GLIDER_WING__REGNAME),
                new ItemComponent(COMPONENT__ION_THRUSTER__REGNAME),
                new ItemComponent(COMPONENT__LV_CAPACITOR__REGNAME),
                new ItemComponent(COMPONENT__MV_CAPACITOR___REGNAME),
                new ItemComponent(COMPONENT__HV_CAPACITOR___REGNAME),
                new ItemComponent(COMPONENT__EV_CAPACITOR___REGNAME),
                new ItemComponent(COMPONENT__PARACHUTE__REGNAME),
                new ItemComponent(COMPONENT__LEATHER_PLATING__REGNAME),
                new ItemComponent(COMPONENT__IRON_PLATING__REGNAME),
                new ItemComponent(COMPONENT__DIAMOND_PLATING__REGNAME),
                new ItemComponent(COMPONENT__FIELD_EMITTER__REGNAME),
                new ItemComponent(COMPONENT__LASER_EMITTER__REGNAME),
                new ItemComponent(COMPONENT__CARBON_MYOFIBER__REGNAME),
                new ItemComponent(COMPONENT__CONTROL_CIRCUIT__REGNAME),
                new ItemComponent(COMPONENT__MYOFIBER_GEL__REGNAME),
                new ItemComponent(COMPONENT__ARTIFICIAL_MUSCLE__REGNAME),
                new ItemComponent(COMPONENT__SOLAR_PANEL__REGNAME),
                new ItemComponent(COMPONENT__MAGNET__REGNAME),
                new ItemComponent(COMPONENT__COMPUTER_CHIP__REGNAME),
                new ItemComponent(COMPONENT__RUBBER_HOSE__REGNAME),

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
//                new FluidTankModule(MODULE_FLUID_TANK__REGNAME),
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
                new NightVisionModule(MODULE_NIGHT_VISION__REGNAME),

                // Mining Enhancements ------------------------------------------------------------------------
                new AOEPickUpgradeModule(MODULE_AOE_PICK_UPGRADE__REGNAME),
                new ItemModuleSilkTouch(MODULE_SILK_TOUCH__REGNAME),
                new FortuneModule(MODULE_FORTUNE_REGNAME),

                // Tools --------------------------------------------------------------------------
                new AquaAffinityModule(MODULE_AQUA_AFFINITY__REGNAME),
                new AxeModule(MODULE_AXE__REGNAME),
                new DiamondPickUpgradeModule(MODULE_DIAMOND_PICK_UPGRADE__REGNAME),
                new DimensionalRiftModule(MODULE_DIMENSIONAL_RIFT__REGNAME),
                new FieldTinkerModule(MODULE_FIELD_TINKER__REGNAME),
                new FlintAndSteelModule(MODULE_FLINT_AND_STEEL__REGNAME),
                new HoeModule(MODULE_HOE__REGNAME),
                new LeafBlowerModule(MODULE_LEAF_BLOWER__REGNAME),
                new LuxCapacitorModule(MODULE_LUX_CAPACITOR__REGNAME),
                new PortableCraftingModule(MODULE_PORTABLE_CRAFTING__REGNAME),
                new PickaxeModule(MODULE_PICKAXE__REGNAME),
                new ShearsModule(MODULE_SHEARS__REGNAME),
                new ShovelModule(MODULE_SHOVEL__REGNAME),

                // Debug --------------------------------------------------------------------------
                // todo

                // Weapons ------------------------------------------------------------------------
                new BladeLauncherModule(MODULE_BLADE_LAUNCHER__REGNAME),
                new LightningModule(MODULE_LIGHTNING__REGNAME),
                new MeleeAssistModule(MODULE_MELEE_ASSIST__REGNAME),
                new PlasmaCannonModule(MODULE_PLASMA_CANNON__REGNAME),
                new RailgunModule(MODULE_RAILGUN__REGNAME),

                // ItemBlocks ---------------------------------------------------------------------------------
                new BlockItem(MPSObjects.INSTANCE.tinkerTable,
                        new Item.Properties().group(creativeTab))
                        .setRegistryName(new ResourceLocation(TINKER_TABLE_REG_NAME)),
                new BlockItem(MPSObjects.INSTANCE.luxCapacitor,
                        new Item.Properties().group(creativeTab))
                        .setRegistryName(new ResourceLocation(LUX_CAPACITOR_REG_NAME))
        );
    }

    @SubscribeEvent
    public void registerBlocks(final RegistryEvent.Register<Block> blockRegistryEvent) {
        blockRegistryEvent.getRegistry().registerAll(new BlockLuxCapacitor(LUX_CAPACITOR_REG_NAME), new BlockTinkerTable(TINKER_TABLE_REG_NAME));
    }

    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(() ->
                        new TileEntityLuxCapacitor(), new BlockLuxCapacitor(LUX_CAPACITOR_REG_NAME)).build(null).setRegistryName(LUX_CAPACITOR_REG_NAME + "_tile"),

                TileEntityType.Builder.create(() ->
                        new TinkerTableTileEntity(), new BlockTinkerTable(TINKER_TABLE_REG_NAME)).build(null).setRegistryName(TINKER_TABLE_REG_NAME + "_tile")
        );
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityType<?>> event ){
        event.getRegistry().registerAll(
                EntityType.Builder.<LuxCapacitorEntity>create(LuxCapacitorEntity::new, EntityClassification.MISC)
                        .size(0.25F, 0.25F)
                        .setCustomClientFactory(((spawnEntity, world) -> MPSObjects.LUX_CAPACITOR_ENTITY_TYPE.create(world)))
                        .build(LUX_CAPACITOR_REG_NAME + "_entity").setRegistryName(LUX_CAPACITOR_REG_NAME + "_entity"),

                EntityType.Builder.<SpinningBladeEntity>create(SpinningBladeEntity::new, EntityClassification.MISC)
                        .setCustomClientFactory((spawnEntity, world) -> MPSObjects.SPINNING_BLADE_ENTITY_TYPE.create(world))
                        .build(MODID +":spinning_blade").setRegistryName(MODID +":spinning_blade"),

                EntityType.Builder.<PlasmaBoltEntity>create(PlasmaBoltEntity::new, EntityClassification.MISC)
                        .setCustomClientFactory((spawnEntity, world) -> MPSObjects.PLASMA_BOLT_ENTITY_TYPE.create(world))
                        .build(MODID +":plasma_bolt").setRegistryName(MODID +":plasma_bolt")
        );
    }

    @SubscribeEvent
    public void registerContainerTypes(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(

                // MODE CHANGING CONTAINER TYPE
                new ContainerType<>(ModeChangingContainer::new)
                        .setRegistryName(MODID + ":mode_changing_container_type"),

                // Modular Item Container
                new ContainerType<>(ModularItemContainer::new)
                        .setRegistryName(MODID + ":modular_item_container_type"),

                // the IForgeContainerType only needed for extra data
                // ModuleConfig
                IForgeContainerType.create((windowId, playerInventory, data) -> {
                    int typeIndex = data.readInt();
                    return new TinkerTableContainer(windowId, playerInventory, typeIndex);
                }).setRegistryName(MODID + ":module_config_container_type"),

                // Keybinding
                IForgeContainerType.create((windowId, playerInventory, data) -> {
                    int typeIndex = data.readInt();
                    return new TinkerTableContainer(windowId, playerInventory, typeIndex);
                }).setRegistryName(MODID + ":table_key_config_container_type"),

                // Cosmetic
                IForgeContainerType.create((windowId, playerInventory, data) -> {
                    int typeIndex = data.readInt();
                    return new TinkerTableContainer(windowId, playerInventory, typeIndex);
                }).setRegistryName(MODID + ":cosmetic_config_container_type"),

                // Crafting Gui
                new ContainerType<>(MPSCraftingContainer::new)
                        .setRegistryName(MODID + ":crafting_container")
        );
    }
}
