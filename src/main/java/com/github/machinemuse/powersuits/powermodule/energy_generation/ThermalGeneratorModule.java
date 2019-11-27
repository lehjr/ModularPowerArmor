package com.github.machinemuse.powersuits.powermodule.energy_generation;


import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by User: Andrew2448
 * 6:43 PM 4/23/13
 */
public class ThermalGeneratorModule extends PowerModuleBase implements IPlayerTickModule {
    public ThermalGeneratorModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        // Fixme: and maybe add options for a Magmatic Dynamo and maybe a stirling generator
//        if (ModCompatibility.isIndustrialCraftLoaded()) {
//            ModuleManager.INSTANCE.addInstallCost(getDataName(),ModCompatibility.getIC2Item("geothermalGenerator"));
        // <ic2:te:4> = geothermal generator

//            ModuleManager.INSTANCE.addInstallCost(getDataName(),ItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));
//        } else {
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.controlCircuit, 2));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.ironPlating, 1));
//        }

        addBasePropertyDouble(MPSModuleConstants.THERMAL_ENERGY_GENERATION, 250);
        addTradeoffPropertyDouble(MPSModuleConstants.ENERGY_GENERATED, MPSModuleConstants.THERMAL_ENERGY_GENERATION, 250, "RF");
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.ENERGY_GENERATION;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_THERMAL_GENERATOR__DATANAME;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        double currentHeat = HeatUtils.getPlayerHeat(player);
        double maxHeat = HeatUtils.getPlayerMaxHeat(player);
        if (player.world.getTotalWorldTime() % 20 == 0) {
            if (player.isBurning()) {
                ElectricItemUtils.givePlayerEnergy(player, (int) (4 * ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, MPSModuleConstants.THERMAL_ENERGY_GENERATION)));
            } else if (currentHeat >= 200) {
                ElectricItemUtils.givePlayerEnergy(player, (int) (2 * ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, MPSModuleConstants.THERMAL_ENERGY_GENERATION)));
            } else if ((currentHeat / maxHeat) >= 0.5) {
                ElectricItemUtils.givePlayerEnergy(player, (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, MPSModuleConstants.THERMAL_ENERGY_GENERATION));
            }
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.thermalGenerator;
    }
}