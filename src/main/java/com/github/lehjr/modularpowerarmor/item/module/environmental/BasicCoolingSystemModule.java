package com.github.lehjr.modularpowerarmor.item.module.environmental;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.client.event.MuseIcon;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import com.github.lehjr.modularpowerarmor.utils.modulehelpers.FluidUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class BasicCoolingSystemModule extends CoolingSystemBase {
    public BasicCoolingSystemModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), new ItemStack(Items.ENDER_EYE, 4));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));

        addTradeoffPropertyDouble(ModuleConstants.BASIC_COOLING_POWER, ModuleConstants.COOLING_BONUS, 4, "%");
        addTradeoffPropertyDouble(ModuleConstants.BASIC_COOLING_POWER, ModuleConstants.BASIC_COOLING_SYSTEM_ENERGY_CONSUMPTION, 100, "RF/t");
    }

    @Override
    public String getDataName() {
        return ModuleConstants.MODULE_BASIC_COOLING_SYSTEM__DATANAME;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack itemStack) {
        if (player.world.isRemote)
            return;

        super.onPlayerTickActive(player, itemStack);
        FluidUtils fluidUtils = new FluidUtils(player, itemStack, this.getDataName());
        fluidUtils.fillWaterFromEnvironment();
    }

    @Override
    public double getCoolingFactor() {
        return 1;
    }

    @Override
    public double getCoolingBonus(@Nonnull ItemStack itemStack) {
        return ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.COOLING_BONUS);
    }

    @Override
    public double getEnergyConsumption(@Nonnull ItemStack itemStack) {
        return ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, ModuleConstants.BASIC_COOLING_SYSTEM_ENERGY_CONSUMPTION);
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.basicCoolingSystem;
    }
}