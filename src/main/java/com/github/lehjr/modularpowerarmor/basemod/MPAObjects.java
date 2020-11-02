package com.github.lehjr.modularpowerarmor.basemod;


import com.github.lehjr.modularpowerarmor.block.LuxCapacitorBlock;
import com.github.lehjr.modularpowerarmor.block.WorkBenchBlock;
import com.github.lehjr.modularpowerarmor.container.MPAWorkbenchContainer;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.modularpowerarmor.entity.PlasmaBoltEntity;
import com.github.lehjr.modularpowerarmor.entity.SpinningBladeEntity;
import com.github.lehjr.modularpowerarmor.item.armor.PowerArmorBoots;
import com.github.lehjr.modularpowerarmor.item.armor.PowerArmorChestplate;
import com.github.lehjr.modularpowerarmor.item.armor.PowerArmorHelmet;
import com.github.lehjr.modularpowerarmor.item.armor.PowerArmorLeggings;
import com.github.lehjr.modularpowerarmor.item.module.armor.IronPlatingModule;
import com.github.lehjr.modularpowerarmor.item.module.armor.LeatherPlatingModule;
import com.github.lehjr.modularpowerarmor.item.module.cosmetic.TransparentArmorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy_generation.AdvancedSolarGeneratorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy_generation.BasicSolarGeneratorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy_generation.KineticGeneratorModule;
import com.github.lehjr.modularpowerarmor.item.module.energy_generation.ThermalGeneratorModule;
import com.github.lehjr.modularpowerarmor.item.module.environmental.*;
import com.github.lehjr.modularpowerarmor.item.module.miningenhancement.*;
import com.github.lehjr.modularpowerarmor.item.module.movement.*;
import com.github.lehjr.modularpowerarmor.item.module.special.ClockModule;
import com.github.lehjr.modularpowerarmor.item.module.special.CompassModule;
import com.github.lehjr.modularpowerarmor.item.module.special.InvisibilityModule;
import com.github.lehjr.modularpowerarmor.item.module.special.MagnetModule;
import com.github.lehjr.modularpowerarmor.item.module.tool.*;
import com.github.lehjr.modularpowerarmor.item.module.vision.BinocularsModule;
import com.github.lehjr.modularpowerarmor.item.module.vision.NightVisionModule;
import com.github.lehjr.modularpowerarmor.item.module.weapon.*;
import com.github.lehjr.modularpowerarmor.item.tool.PowerFist;
import com.github.lehjr.modularpowerarmor.tile_entity.LuxCapacitorTileEntity;
import com.github.lehjr.modularpowerarmor.tile_entity.WorkBenchTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MPAObjects {
    public static final MPACreativeTab creativeTab = new MPACreativeTab();
    public static final Item.Properties singleStack = new Item.Properties()
            .maxStackSize(1)
            .group(MPAObjects.creativeTab)
            .defaultMaxDamage(-1)
            .setNoRepair();

    public static final Item.Properties fullStack = new Item.Properties()
            .group(MPAObjects.creativeTab)
            .defaultMaxDamage(-1)
            .setNoRepair();
    /**
     * Blocks ------------------------------------------------------------------------------------
     */
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MPAConstants.MOD_ID);

    public static final RegistryObject<WorkBenchBlock> WORKBENCH_BLOCK = BLOCKS.register(MPARegistryNames.WORKBENCH,
            () -> new WorkBenchBlock());

    public static final RegistryObject<LuxCapacitorBlock> LUX_CAPACITOR_BLOCK = BLOCKS.register(MPARegistryNames.LUX_CAPACITOR,
            () -> new LuxCapacitorBlock());


    /**
     * Tile Entity Types -------------------------------------------------------------------------
     */
    public static final DeferredRegister<TileEntityType<?>> TILE_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MPAConstants.MOD_ID);

    public static final RegistryObject<TileEntityType<WorkBenchTileEntity>> WORKBENCH_TILE_TYPE = TILE_TYPES.register(MPARegistryNames.WORKBENCH,
            () -> TileEntityType.Builder.create(WorkBenchTileEntity::new, WORKBENCH_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<LuxCapacitorTileEntity>> LUX_CAP_TILE_TYPE = TILE_TYPES.register(MPARegistryNames.LUX_CAPACITOR,
            () -> TileEntityType.Builder.create(LuxCapacitorTileEntity::new, LUX_CAPACITOR_BLOCK.get()).build(null));

    /**
     * Entity Types ------------------------------------------------------------------------------
     */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MPAConstants.MOD_ID);

    public static final RegistryObject<EntityType<LuxCapacitorEntity>> LUX_CAPACITOR_ENTITY_TYPE = ENTITY_TYPES.register(MPARegistryNames.LUX_CAPACITOR,
            ()-> EntityType.Builder.<LuxCapacitorEntity>create(LuxCapacitorEntity::new, EntityClassification.MISC)
                    .size(0.25F, 0.25F)
                    .build(new ResourceLocation(MPAConstants.MOD_ID, MPARegistryNames.LUX_CAPACITOR).toString()));

    public static final RegistryObject<EntityType<SpinningBladeEntity>> SPINNING_BLADE_ENTITY_TYPE = ENTITY_TYPES.register(MPARegistryNames.SPINNING_BLADE,
            ()-> EntityType.Builder.<SpinningBladeEntity>create(SpinningBladeEntity::new, EntityClassification.MISC)
                    .size(0.25F, 0.25F) // FIXME! check size
                    .build(new ResourceLocation(MPAConstants.MOD_ID, MPARegistryNames.SPINNING_BLADE).toString()));

    public static final RegistryObject<EntityType<PlasmaBoltEntity>> PLASMA_BOLT_ENTITY_TYPE = ENTITY_TYPES.register(MPARegistryNames.PLASMA_BOLT,
            ()-> EntityType.Builder.<PlasmaBoltEntity>create(PlasmaBoltEntity::new, EntityClassification.MISC)
//                    .size(0.25F, 0.25F)
                    .build(new ResourceLocation(MPAConstants.MOD_ID, MPARegistryNames.PLASMA_BOLT).toString()));


    // FIXME: bolt protectile



    /**
     * Items -------------------------------------------------------------------------------------
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MPAConstants.MOD_ID);

    /* BlockItems --------------------------------------------------------------------------------- */
    public static final RegistryObject<Item> WORKBENCH_ITEM = ITEMS.register(MPARegistryNames.WORKBENCH,
            () -> new BlockItem(WORKBENCH_BLOCK.get(), fullStack));

    public static final RegistryObject<Item> LUX_CAPACITOR_ITEM = ITEMS.register(MPARegistryNames.LUX_CAPACITOR,
            () -> new BlockItem(LUX_CAPACITOR_BLOCK.get(), fullStack));

    /* Armor -------------------------------------------------------------------------------------- */
    public static final RegistryObject<Item> POWER_ARMOR_HELMET = ITEMS.register(MPARegistryNames.POWER_ARMOR_HELMET,
            () -> new PowerArmorHelmet());

    public static final RegistryObject<Item> POWER_ARMOR_CHESTPLATE = ITEMS.register(MPARegistryNames.POWER_ARMOR_CHESTPLATE,
            () -> new PowerArmorChestplate());

    public static final RegistryObject<Item> POWER_ARMOR_LEGGINGS = ITEMS.register(MPARegistryNames.POWER_ARMOR_LEGGINGS,
            () -> new PowerArmorLeggings());

    public static final RegistryObject<Item> POWER_ARMOR_BOOTS = ITEMS.register(MPARegistryNames.POWER_ARMOR_BOOTS,
            () -> new PowerArmorBoots());

    /* HandHeld ----------------------------------------------------------------------------------- */
    public static final RegistryObject<Item> POWER_FIST = ITEMS.register(MPARegistryNames.POWER_FIST,
            () -> new PowerFist());

    /* Modules ------------------------------------------------------------------------------------ */
    // Armor --------------------------------------------------------------------------------------
    public static final RegistryObject<Item> LEATHER_PLATING_MODULE = registerModule(MPARegistryNames.LEATHER_PLATING_MODULE, new LeatherPlatingModule());
    public static final RegistryObject<Item> IRON_PLATING_MODULE = registerModule(MPARegistryNames.IRON_PLATING_MODULE, new IronPlatingModule());
    public static final RegistryObject<Item> DIAMOND_PLATING_MODULE = registerModule(MPARegistryNames.DIAMOND_PLATING_MODULE, new IronPlatingModule());
    public static final RegistryObject<Item> ENERGY_SHIELD_MODULE = registerModule(MPARegistryNames.ENERGY_SHIELD_MODULE, new IronPlatingModule());

    // Cosmetic -----------------------------------------------------------------------------------
    public static final RegistryObject<Item> TRANSPARENT_ARMOR_MODULE = registerModule(MPARegistryNames.TRANSPARENT_ARMOR_MODULE, new TransparentArmorModule());

    // Energy Generation --------------------------------------------------------------------------
    public static final RegistryObject<Item> SOLAR_GENERATOR_MODULE = registerModule(MPARegistryNames.SOLAR_GENERATOR_MODULE, new BasicSolarGeneratorModule());
    public static final RegistryObject<Item> ADVANCED_SOLAR_GENERATOR_MODULE = registerModule(MPARegistryNames.ADVANCED_SOLAR_GENERATOR_MODULE, new AdvancedSolarGeneratorModule());
    public static final RegistryObject<Item> KINETIC_GENERATOR_MODULE = registerModule(MPARegistryNames.KINETIC_GENERATOR_MODULE, new KineticGeneratorModule());
    public static final RegistryObject<Item> THERMAL_GENERATOR_MODULE = registerModule(MPARegistryNames.THERMAL_GENERATOR_MODULE, new ThermalGeneratorModule());

    // Environmental ------------------------------------------------------------------------------
    public static final RegistryObject<Item> AUTO_FEEDER_MODULE = registerModule(MPARegistryNames.AUTO_FEEDER_MODULE, new AutoFeederModule());
    public static final RegistryObject<Item> COOLING_SYSTEM_MODULE = registerModule(MPARegistryNames.COOLING_SYSTEM_MODULE, new CoolingSystemModule());
    public static final RegistryObject<Item> FLUID_TANK_MODULE = registerModule(MPARegistryNames.FLUID_TANK_MODULE, new FluidTankModule());
    public static final RegistryObject<Item> MOB_REPULSOR_MODULE = registerModule(MPARegistryNames.MOB_REPULSOR_MODULE, new MobRepulsorModule());
    public static final RegistryObject<Item> WATER_ELECTROLYZER_MODULE = registerModule(MPARegistryNames.WATER_ELECTROLYZER_MODULE, new WaterElectrolyzerModule());

    // Mining Enhancements ------------------------------------------------------------------------
    public static final RegistryObject<Item> AOE_PICK_UPGRADE_MODULE = registerModule(MPARegistryNames.AOE_PICK_UPGRADE_MODULE, new AOEPickUpgradeModule());
    public static final RegistryObject<Item> AQUA_AFFINITY_MODULE = registerModule(MPARegistryNames.AQUA_AFFINITY_MODULE, new AquaAffinityModule());
    public static final RegistryObject<Item> SILK_TOUCH_MODULE = registerModule(MPARegistryNames.SILK_TOUCH_MODULE, new SilkTouchModule());
    public static final RegistryObject<Item> FORTUNE_MODULE = registerModule(MPARegistryNames.FORTUNE_MODULE, new FortuneModule());
    public static final RegistryObject<Item> VEIN_MINER_MODULE = registerModule(MPARegistryNames.VEIN_MINER_MODULE, new VeinMinerModule());

    // Movement -----------------------------------------------------------------------------------
    public static final RegistryObject<Item> BLINK_DRIVE_MODULE = registerModule(MPARegistryNames.BLINK_DRIVE_MODULE, new BlinkDriveModule());
    public static final RegistryObject<Item> CLIMB_ASSIST_MODULE = registerModule(MPARegistryNames.CLIMB_ASSIST_MODULE, new ClimbAssistModule());
    public static final RegistryObject<Item> DIMENSIONAL_RIFT_MODULE = registerModule(MPARegistryNames.DIMENSIONAL_RIFT_MODULE, new DimensionalRiftModule());
    public static final RegistryObject<Item> FLIGHT_CONTROL_MODULE = registerModule(MPARegistryNames.FLIGHT_CONTROL_MODULE, new FlightControlModule());
    public static final RegistryObject<Item> GLIDER_MODULE = registerModule(MPARegistryNames.GLIDER_MODULE, new GliderModule());
    public static final RegistryObject<Item> JETBOOTS_MODULE = registerModule(MPARegistryNames.JETBOOTS_MODULE, new JetBootsModule());
    public static final RegistryObject<Item> JETPACK_MODULE = registerModule(MPARegistryNames.JETPACK_MODULE, new JetPackModule());
    public static final RegistryObject<Item> JUMP_ASSIST_MODULE = registerModule(MPARegistryNames.JUMP_ASSIST_MODULE, new JumpAssistModule());
    public static final RegistryObject<Item> PARACHUTE_MODULE = registerModule(MPARegistryNames.PARACHUTE_MODULE, new ParachuteModule());
    public static final RegistryObject<Item> SHOCK_ABSORBER_MODULE = registerModule(MPARegistryNames.SHOCK_ABSORBER_MODULE, new ShockAbsorberModule());
    public static final RegistryObject<Item> SPRINT_ASSIST_MODULE = registerModule(MPARegistryNames.SPRINT_ASSIST_MODULE, new SprintAssistModule());
    public static final RegistryObject<Item> SWIM_BOOST_MODULE = registerModule(MPARegistryNames.SWIM_BOOST_MODULE, new SwimAssistModule());

    // Special ------------------------------------------------------------------------------------
//    public static final RegistryObject<Item> CLOCK_MODULE = registerModule(MPARegistryNames.CLOCK_MODULE, new ClockModule());
//    public static final RegistryObject<Item> COMPASS_MODULE = registerModule(MPARegistryNames.COMPASS_MODULE, new CompassModule());
    public static final RegistryObject<Item> ACTIVE_CAMOUFLAGE_MODULE = registerModule(MPARegistryNames.ACTIVE_CAMOUFLAGE_MODULE, new InvisibilityModule());
    public static final RegistryObject<Item> MAGNET_MODULE = registerModule(MPARegistryNames.MAGNET_MODULE, new MagnetModule());

    // Vision -------------------------------------------------------------------------------------
    public static final RegistryObject<Item> BINOCULARS_MODULE = registerModule(MPARegistryNames.BINOCULARS_MODULE, new BinocularsModule());
    public static final RegistryObject<Item> NIGHT_VISION_MODULE = registerModule(MPARegistryNames.NIGHT_VISION_MODULE, new NightVisionModule());

    // Tools --------------------------------------------------------------------------
    public static final RegistryObject<Item> AXE_MODULE = registerModule(MPARegistryNames.AXE_MODULE, new AxeModule());
    public static final RegistryObject<Item> DIAMOND_PICK_UPGRADE_MODULE = registerModule(MPARegistryNames.DIAMOND_PICK_UPGRADE_MODULE, new DiamondPickUpgradeModule());

    // Fixme !!! rename?
    public static final RegistryObject<Item> FIELD_TINKER_MODULE = registerModule(MPARegistryNames.PORTABLE_WORKBENCH_MODULE, new FieldTinkerModule());
    public static final RegistryObject<Item> FLINT_AND_STEEL_MODULE = registerModule(MPARegistryNames.FLINT_AND_STEEL_MODULE, new FlintAndSteelModule());
    public static final RegistryObject<Item> HOE_MODULE = registerModule(MPARegistryNames.HOE_MODULE, new HoeModule());
    public static final RegistryObject<Item> LEAF_BLOWER_MODULE = registerModule(MPARegistryNames.LEAF_BLOWER_MODULE, new LeafBlowerModule());
    public static final RegistryObject<Item> LUX_CAPACITOR_MODULE = registerModule(MPARegistryNames.LUX_CAPACITOR_MODULE, new LuxCapacitorModule());
    public static final RegistryObject<Item> PORTABLE_CRAFTING_MODULE = registerModule(MPARegistryNames.PORTABLE_CRAFTING_MODULE, new PortableCraftingModule());
    public static final RegistryObject<Item> PICKAXE_MODULE = registerModule(MPARegistryNames.PICKAXE_MODULE, new PickaxeModule());
    public static final RegistryObject<Item> SHEARS_MODULE = registerModule(MPARegistryNames.SHEARS_MODULE, new ShearsModule());
    public static final RegistryObject<Item> SHOVEL_MODULE = registerModule(MPARegistryNames.SHOVEL_MODULE, new ShockAbsorberModule());

    // Debug --------------------------------------------------------------------------------------
    // todo

    // Weapons ------------------------------------------------------------------------------------
    public static final RegistryObject<Item> BLADE_LAUNCHER_MODULE = registerModule(MPARegistryNames.BLADE_LAUNCHER_MODULE, new BladeLauncherModule());
    public static final RegistryObject<Item> LIGHTNING_MODULE = registerModule(MPARegistryNames.LIGHTNING_MODULE, new LightningModule());
    public static final RegistryObject<Item> MELEE_ASSIST_MODULE = registerModule(MPARegistryNames.MELEE_ASSIST_MODULE, new MeleeAssistModule());
    public static final RegistryObject<Item> PLASMA_CANNON_MODULE = registerModule(MPARegistryNames.PLASMA_CANNON_MODULE, new PlasmaCannonModule());
    public static final RegistryObject<Item> RAILGUN_MODULE = registerModule(MPARegistryNames.RAILGUN_MODULE, new RailgunModule());

    /**
     * Container Types ----------------------------------------------------------------------------
     */
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, MPAConstants.MOD_ID);

    public static final RegistryObject<ContainerType<MPAWorkbenchContainer>> MPA_WORKBENCH_CONTAINER_TYPE = CONTAINER_TYPES.register(MPARegistryNames.MPA_WORKBENCH_CONTAINER_TYPE,
            () -> IForgeContainerType.create((windowId, inv, data) -> new MPAWorkbenchContainer(windowId, inv)));


//                    new ContainerType<>(MPAWorkbenchContainer::new)
//            .setRegistryName(MPA_WORKBENCH_CONTAINER_TYPE),
//
//
//    // Crafting Gui
//                new ContainerType<>(MPACraftingContainer::new)
//            .setRegistryName(MPA_CRAFTING_CONTAINER_TYPE__REG_NAME)


////    @ObjectHolder(MOD_ID + ":bolt")
////    public static EntityType<BoltEntity> BOLT_ENTITY_TYPE;
//
//
//

//        @ObjectHolder(MPA_CRAFTING_CONTAINER_TYPE__REG_NAME)
//        public static final ContainerType<MPACraftingContainer> MPA_CRAFTING_CONTAINER_TYPE = null;
//
//        @ObjectHolder(MPA_TINKER_TABLE_CONTAINER_TYPE__REG_NAME)
//        public static final ContainerType<MPAWorkbenchContainer> TINKER_TABLE_CONTAINER_TYPE = null;
//    }




    static RegistryObject<Item> registerModule(String regName, Item item) {
        MPAModules.INSTANCE.addModule(MPARegistryNames.getRegName(regName));
        return ITEMS.register(regName, () -> item);
    }
}