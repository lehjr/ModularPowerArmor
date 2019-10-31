package net.machinemuse.powersuits.common;

import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import net.machinemuse.powersuits.common.config.MPSConfig;
import net.machinemuse.powersuits.powermodule.armor.DiamondPlatingModule;
import net.machinemuse.powersuits.powermodule.armor.EnergyShieldModule;
import net.machinemuse.powersuits.powermodule.armor.IronPlatingModule;
import net.machinemuse.powersuits.powermodule.armor.LeatherPlatingModule;
import net.machinemuse.powersuits.powermodule.cosmetic.TransparentArmorModule;
import net.machinemuse.powersuits.powermodule.energy_generation.AdvancedSolarGenerator;
import net.machinemuse.powersuits.powermodule.energy_generation.KineticGeneratorModule;
import net.machinemuse.powersuits.powermodule.energy_generation.SolarGeneratorModule;
import net.machinemuse.powersuits.powermodule.energy_generation.ThermalGeneratorModule;
import net.machinemuse.powersuits.powermodule.energy_storage.AdvancedBatteryModule;
import net.machinemuse.powersuits.powermodule.energy_storage.BasicBatteryModule;
import net.machinemuse.powersuits.powermodule.energy_storage.EliteBatteryModule;
import net.machinemuse.powersuits.powermodule.energy_storage.UltimateBatteryModule;
import net.machinemuse.powersuits.powermodule.environmental.*;
import net.machinemuse.powersuits.powermodule.mining_enhancement.*;
import net.machinemuse.powersuits.powermodule.movement.*;
import net.machinemuse.powersuits.powermodule.special.ClockModule;
import net.machinemuse.powersuits.powermodule.special.CompassModule;
import net.machinemuse.powersuits.powermodule.special.InvisibilityModule;
import net.machinemuse.powersuits.powermodule.special.MagnetModule;
import net.machinemuse.powersuits.powermodule.tool.*;
import net.machinemuse.powersuits.powermodule.vision.BinocularsModule;
import net.machinemuse.powersuits.powermodule.vision.NightVisionModule;
import net.machinemuse.powersuits.powermodule.vision.ThaumGogglesModule;
import net.machinemuse.powersuits.powermodule.weapon.*;

public class MPSModules {
    public static void addModule(IPowerModule module) {
        if (MPSConfig.INSTANCE.getModuleAllowedorDefault(module.getDataName(), true))
            ModuleManager.INSTANCE.addModule(module);
    }

    /**
     * Load all the modules in the config file into memory. Eventually. For now,
     * they are hardcoded.
     */
    public static void loadPowerModules() {
        // FIXME: these need to be sorted
        /* Armor -------------------------------- */
        addModule(new LeatherPlatingModule(EnumModuleTarget.ARMORONLY));
        addModule(new IronPlatingModule(EnumModuleTarget.ARMORONLY));
        addModule(new DiamondPlatingModule(EnumModuleTarget.ARMORONLY));
        addModule(new EnergyShieldModule(EnumModuleTarget.ARMORONLY));

        /* Cosmetic ----------------------------- */
        addModule(new TransparentArmorModule(EnumModuleTarget.ARMORONLY));


        /* Energy ------------------------------- */
        addModule(new BasicBatteryModule(EnumModuleTarget.ALLITEMS));
        addModule(new AdvancedBatteryModule(EnumModuleTarget.ALLITEMS));
        addModule(new EliteBatteryModule(EnumModuleTarget.ALLITEMS));
        addModule(new UltimateBatteryModule(EnumModuleTarget.ALLITEMS));


        /* Power Fist --------------------------- */
        addModule(new AxeModule(EnumModuleTarget.TOOLONLY));
        addModule(new PickaxeModule(EnumModuleTarget.TOOLONLY));
        addModule(new DiamondPickUpgradeModule(EnumModuleTarget.TOOLONLY));
        addModule(new ShovelModule(EnumModuleTarget.TOOLONLY));
        addModule(new ShearsModule(EnumModuleTarget.TOOLONLY));
        addModule(new HoeModule(EnumModuleTarget.TOOLONLY));
        addModule(new LuxCapacitor(EnumModuleTarget.TOOLONLY));
        addModule(new FieldTinkerModule(EnumModuleTarget.TOOLONLY));
        addModule(new MeleeAssistModule(EnumModuleTarget.TOOLONLY));
        addModule(new PlasmaCannonModule(EnumModuleTarget.TOOLONLY));
        addModule(new RailgunModule(EnumModuleTarget.TOOLONLY));
        addModule(new BladeLauncherModule(EnumModuleTarget.TOOLONLY));
        addModule(new BlinkDriveModule(EnumModuleTarget.TOOLONLY));
        addModule(new InPlaceAssemblerModule(EnumModuleTarget.TOOLONLY));
        addModule(new LeafBlowerModule(EnumModuleTarget.TOOLONLY));
        addModule(new FlintAndSteelModule(EnumModuleTarget.TOOLONLY));
        addModule(new LightningModule(EnumModuleTarget.TOOLONLY));
        addModule(new DimensionalRiftModule(EnumModuleTarget.TOOLONLY));
        // Mining Enhancements
        addModule(new AOEPickUpgradeModule(EnumModuleTarget.TOOLONLY));
        addModule(new AquaAffinityModule(EnumModuleTarget.TOOLONLY));
        addModule(new FortuneModule(EnumModuleTarget.TOOLONLY));
        addModule(new SilkTouchModule(EnumModuleTarget.TOOLONLY));


        /* Helmet ------------------------------- */
        addModule(new WaterElectrolyzerModule(EnumModuleTarget.HEADONLY));
        addModule(new NightVisionModule(EnumModuleTarget.HEADONLY));
        addModule(new BinocularsModule(EnumModuleTarget.HEADONLY));
        addModule(new FlightControlModule(EnumModuleTarget.HEADONLY));
        addModule(new SolarGeneratorModule(EnumModuleTarget.HEADONLY));
        addModule(new AutoFeederModule(EnumModuleTarget.HEADONLY));
        addModule(new ClockModule(EnumModuleTarget.HEADONLY));
        addModule(new CompassModule(EnumModuleTarget.HEADONLY));
        addModule(new AdvancedSolarGenerator(EnumModuleTarget.HEADONLY));


        /* Chestplate --------------------------- */
        addModule(new ParachuteModule(EnumModuleTarget.TORSOONLY));
        addModule(new GliderModule(EnumModuleTarget.TORSOONLY));
        addModule(new JetPackModule(EnumModuleTarget.TORSOONLY));
        addModule(new InvisibilityModule(EnumModuleTarget.TORSOONLY));
        addModule(new BasicCoolingSystemModule(EnumModuleTarget.TORSOONLY));
        addModule(new MagnetModule(EnumModuleTarget.TORSOONLY));
        addModule(new ThermalGeneratorModule(EnumModuleTarget.TORSOONLY));
        addModule(new MobRepulsorModule(EnumModuleTarget.TORSOONLY));
        addModule(new AdvancedCoolingSystem(EnumModuleTarget.TORSOONLY));
        //addModule(new CoalGenerator(TORSOONLY)); //TODO: Finish


        /* Legs --------------------------------- */
        addModule(new SprintAssistModule(EnumModuleTarget.LEGSONLY));
        addModule(new JumpAssistModule(EnumModuleTarget.LEGSONLY));
        addModule(new SwimAssistModule(EnumModuleTarget.LEGSONLY));
        addModule(new KineticGeneratorModule(EnumModuleTarget.LEGSONLY));
        addModule(new ClimbAssistModule(EnumModuleTarget.LEGSONLY));


        /* Feet --------------------------------- */
        addModule(new JetBootsModule(EnumModuleTarget.FEETONLY));
        addModule(new ShockAbsorberModule(EnumModuleTarget.FEETONLY));


        /** Conditional loading ------------------------------------------------------------------- */
        // Thaumcraft
        if (ModCompatibility.isThaumCraftLoaded())
            addModule(new ThaumGogglesModule(EnumModuleTarget.HEADONLY));

        // CoFHCore
        if (ModCompatibility.isCOFHCoreLoaded())
            addModule(new OmniWrenchModule(EnumModuleTarget.TOOLONLY));

        // Mekanism
        if (ModCompatibility.isMekanismLoaded())
            addModule(new MADModule(EnumModuleTarget.TOOLONLY));

        // Industrialcraft
        if (ModCompatibility.isIndustrialCraftLoaded()) {
            addModule(new HazmatModule(EnumModuleTarget.ARMORONLY));
            addModule(new TreetapModule(EnumModuleTarget.TOOLONLY));
        }

        // Galacticraft
        if (ModCompatibility.isGalacticraftLoaded())
            addModule(new AirtightSealModule(EnumModuleTarget.HEADONLY));

        // Forestry
        if (ModCompatibility.isForestryLoaded()) {
            addModule(new GrafterModule(EnumModuleTarget.TOOLONLY));
            addModule(new ScoopModule(EnumModuleTarget.TOOLONLY));
            addModule(new ApiaristArmorModule(EnumModuleTarget.ARMORONLY));
        }

        // Chisel
        if (ModCompatibility.isChiselLoaded())
            try {
                addModule(new ChiselModule(EnumModuleTarget.TOOLONLY));
            } catch (Exception e) {
                MPALibLogger.logException("Couldn't add Chisel module", e);
            }

        // Applied Energistics
        if (ModCompatibility.isAppengLoaded()) {
            addModule(new AppEngWirelessModule(EnumModuleTarget.TOOLONLY));

            // Extra Cells 2
            if (ModCompatibility.isExtraCellsLoaded())
                addModule(new AppEngWirelessFluidModule(EnumModuleTarget.TOOLONLY));
        }

        // Multi-Mod Compatible OmniProbe
        if (ModCompatibility.isEnderIOLoaded() || ModCompatibility.isMFRLoaded() || ModCompatibility.isRailcraftLoaded())
            addModule(new OmniProbeModule(EnumModuleTarget.TOOLONLY));

// TODO: on hold for now. Needs a conditional fiuld tank and handler. May not be worth it.
        // Compact Machines
        if (ModCompatibility.isCompactMachinesLoaded())
            addModule(new PersonalShrinkingModule(EnumModuleTarget.TOOLONLY));

        // Refined Storage
        if (ModCompatibility.isRefinedStorageLoaded())
            addModule(new RefinedStorageWirelessModule(EnumModuleTarget.TOOLONLY));

        // Scannable
        if (ModCompatibility.isScannableLoaded())
            addModule(new OreScannerModule(EnumModuleTarget.TOOLONLY));
    }
}