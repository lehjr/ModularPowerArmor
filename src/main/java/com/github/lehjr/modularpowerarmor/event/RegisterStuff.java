package com.github.lehjr.modularpowerarmor.event;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.CreativeTab;
import com.github.lehjr.modularpowerarmor.basemod.Objects;
import com.github.lehjr.modularpowerarmor.block.BlockLuxCapacitor;
import com.github.lehjr.modularpowerarmor.block.BlockTinkerTable;
import com.github.lehjr.modularpowerarmor.fluid.BlockFluidLiquidNitrogen;
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
import com.github.lehjr.modularpowerarmor.item.module.vision.ThaumGogglesModule;
import com.github.lehjr.modularpowerarmor.item.module.weapon.*;
import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.github.lehjr.modularpowerarmor.basemod.RegistryNames.*;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class RegisterStuff {
    static {
        new RegisterStuff();
    }

    public static final CreativeTab creativeTab = new CreativeTab();

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent) {
        itemRegistryEvent.getRegistry().registerAll(
                // Armor --------------------------------------------------------------------------------------
                new ItemPowerArmorHelmet(ITEM__POWER_ARMOR_HELMET__REGNAME),
                new ItemPowerArmorChestplate(ITEM__POWER_ARMOR_CHESTPLATE__REGNAME),
                new ItemPowerArmorLeggings(ITEM__POWER_ARMOR_LEGGINGS__REGNAME),
                new ItemPowerArmorBoots(ITEM__POWER_ARMOR_BOOTS__REGNAME),

                // HandHeld -----------------------------------------------------------------------------------
                new ItemPowerFist(ITEM__POWER_FIST__REGNAME),

                // Components ---------------------------------------------------------------------------------
                new ItemComponent(COMPONENT__REGNAME),
//                new ItemComponent(COMPONENT__WIRING__REGNAME),
//                new ItemComponent(COMPONENT__SOLENOID__REGNAME),
//                new ItemComponent(COMPONENT__SERVO__REGNAME),
//                new ItemComponent(COMPONENT__GLIDER_WING__REGNAME),
//                new ItemComponent(COMPONENT__ION_THRUSTER__REGNAME),
//                new ItemComponent(COMPONENT__LV_CAPACITOR__REGNAME),
//                new ItemComponent(COMPONENT__MV_CAPACITOR___REGNAME),
//                new ItemComponent(COMPONENT__HV_CAPACITOR___REGNAME),
//                new ItemComponent(COMPONENT__EV_CAPACITOR___REGNAME),
//                new ItemComponent(COMPONENT__PARACHUTE__REGNAME),
//                new ItemComponent(COMPONENT__LEATHER_PLATING__REGNAME),
//                new ItemComponent(COMPONENT__IRON_PLATING__REGNAME),
//                new ItemComponent(COMPONENT__DIAMOND_PLATING__REGNAME),
//                new ItemComponent(COMPONENT__FIELD_EMITTER__REGNAME),
//                new ItemComponent(COMPONENT__LASER_EMITTER__REGNAME),
//                new ItemComponent(COMPONENT__CARBON_MYOFIBER__REGNAME),
//                new ItemComponent(COMPONENT__CONTROL_CIRCUIT__REGNAME),
//                new ItemComponent(COMPONENT__MYOFIBER_GEL__REGNAME),
//                new ItemComponent(COMPONENT__ARTIFICIAL_MUSCLE__REGNAME),
//                new ItemComponent(COMPONENT__SOLAR_PANEL__REGNAME),
//                new ItemComponent(COMPONENT__MAGNET__REGNAME),
//                new ItemComponent(COMPONENT__COMPUTER_CHIP__REGNAME),
//                new ItemComponent(COMPONENT__RUBBER_HOSE__REGNAME),

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
                new SolarGeneratorModule(MODULE_SOLAR_GENERATOR__REGNAME),
                new AdvancedSolarGenerator(MODULE_ADVANCED_SOLAR_GENERATOR__REGNAME),
                new KineticGeneratorModule(MODULE_KINETIC_GENERATOR__REGNAME),
                new ThermalGeneratorModule(MODULE_THERMAL_GENERATOR__REGNAME),

                // Environmental ------------------------------------------------------------------
                new BasicCoolingSystemModule(MODULE_BASIC_COOLING_SYSTEM__REGNAME),
                new AdvancedCoolingSystemModule(MODULE_ADVANCED_COOLING_SYSTEM__REGNAME),
                new AirtightSealModule(MODULE_AIRTIGHT_SEAL__REGNAME),
                new ApiaristArmorModule(MODULE_APIARIST_ARMOR__REGNAME),
                new HazmatModule(MODULE_HAZMAT__REGNAME),
                new AutoFeederModule(MODULE_AUTO_FEEDER__REGNAME),
                new MobRepulsorModule(MODULE_MOB_REPULSOR__REGNAME),
                new WaterElectrolyzerModule(MODULE_WATER_ELECTROLYZER__REGNAME),

                // Mining Enhancements ------------------------------------------------------------------------
                new AOEPickUpgradeModule(MODULE_AOE_PICK_UPGRADE__REGNAME),
                new AquaAffinityModule(MODULE_AQUA_AFFINITY__REGNAME),
                new SilkTouchModule(MODULE_SILK_TOUCH__REGNAME),
                new FortuneModule(MODULE_FORTUNE_REGNAME),

                // Movement -----------------------------------------------------------------------------------
                new BlinkDriveModule(MODULE_BLINK_DRIVE__REGNAME),
                new ClimbAssistModule(MODULE_CLIMB_ASSIST__REGNAME),
                new DimensionalRiftModule(MODULE_DIMENSIONAL_RIFT__REGNAME),
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
                new ThaumGogglesModule(MODULE_THAUM_GOGGLES__REGNAME),

                // Tools --------------------------------------------------------------------------
                new AppEngWirelessFluidModule(MODULE_APPENG_EC_WIRELESS_FLUID__REGNAME),
                new AppEngWirelessModule(MODULE_APPENG_WIRELESS__REGNAME),
                new AxeModule(MODULE_AXE__REGNAME),
                new DiamondPickUpgradeModule(MODULE_DIAMOND_PICK_UPGRADE__REGNAME),
                new FieldTinkerModule(MODULE_FIELD_TINKER__REGNAME),
                new FlintAndSteelModule(MODULE_FLINT_AND_STEEL__REGNAME),
                new GrafterModule(MODULE_GRAFTER__REGNAME),
                new HoeModule(MODULE_HOE__REGNAME),
                new LeafBlowerModule(MODULE_LEAF_BLOWER__REGNAME),
                new LuxCapacitorModule(MODULE_LUX_CAPACITOR__REGNAME),
                new OmniProbeModule(MODULE_OMNIPROBE__REGNAME),
                new OmniWrenchModule(MODULE_OMNI_WRENCH__REGNAME),
                new PersonalShrinkingModule(MODULE_CM_PSD__REGNAME),
                new PortableCraftingModule(MODULE_PORTABLE_CRAFTING__REGNAME),
                new PickaxeModule(MODULE_PICKAXE__REGNAME),
                new RefinedStorageWirelessModule(MODULE_REF_STOR_WIRELESS__REGNAME),
                new ScannerModule(MODULE_SCANNER__REGNAME),
                new ScoopModule(MODULE_SCOOP__REGNAME),
                new ShearsModule(MODULE_SHEARS__REGNAME),
                new ShovelModule(MODULE_SHOVEL__REGNAME),
                new TreetapModule(MODULE_TREETAP__REGNAME),
                // Debug --------------------------------------------------------------------------
                // todo

                // Weapons ------------------------------------------------------------------------
                new BladeLauncherModule(MODULE_BLADE_LAUNCHER__REGNAME),
                new LightningModule(MODULE_LIGHTNING__REGNAME),
                new MeleeAssistModule(MODULE_MELEE_ASSIST__REGNAME),
                new PlasmaCannonModule(MODULE_PLASMA_CANNON__REGNAME),
                new RailgunModule(MODULE_RAILGUN__REGNAME),

                // ItemBlocks ---------------------------------------------------------------------------------
                new ItemBlock(Objects.tinkerTable).setRegistryName(new ResourceLocation(TINKER_TABLE_REG_NAME)),
                new ItemBlock(Objects.luxCapacitor).setRegistryName(new ResourceLocation(LUX_CAPACITOR_REG_NAME)));

        ItemComponent temp = (ItemComponent) itemRegistryEvent.getRegistry().getValue(new ResourceLocation(COMPONENT__REGNAME));
        if (temp != null) {
            temp.registerOres();
        }
    }

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> blockRegistryEvent) {
        blockRegistryEvent.getRegistry().register(new BlockTinkerTable( new ResourceLocation(TINKER_TABLE_REG_NAME)));
        blockRegistryEvent.getRegistry().register(new BlockLuxCapacitor(new ResourceLocation(LUX_CAPACITOR_REG_NAME)));
        blockRegistryEvent.getRegistry().register(new BlockFluidLiquidNitrogen(new ResourceLocation(LIQUID_NITROGEN__REGNAME)));
    }

    static boolean alreadyRegistered = true;
    public static void initFluids() {
        if (!FluidRegistry.isFluidRegistered("liquidnitrogen") && !FluidRegistry.isFluidRegistered("liquid_nitrogen")) {
            FluidRegistry.registerFluid(Objects.liquidNitrogen);
            FluidRegistry.addBucketForFluid(Objects.liquidNitrogen);
            alreadyRegistered = false;
        }
    }
}
