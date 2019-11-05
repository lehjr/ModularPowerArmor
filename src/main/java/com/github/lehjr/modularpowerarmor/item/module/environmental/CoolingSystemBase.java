package com.github.lehjr.modularpowerarmor.item.module.environmental;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.lehjr.modularpowerarmor.utils.modulehelpers.FluidUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;

import javax.annotation.Nonnull;

public abstract class CoolingSystemBase extends AbstractPowerModule implements IPlayerTickModule, IToggleableModule {

    public CoolingSystemBase(EnumModuleTarget moduleTargetIn) {
        super(moduleTargetIn);
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack itemStack) {
        if (!player.world.isRemote) {
            double currentHeat = HeatUtils.getPlayerHeat(player);
            if (currentHeat <= 0)
                return;

            double maxHeat = HeatUtils.getPlayerMaxHeat(player);
            FluidUtils fluidUtils = new FluidUtils(player, itemStack, this.getDataName());
            double fluidEfficiencyBoost = fluidUtils.getCoolingEfficiency();

            // if not overheating
            if (currentHeat < maxHeat) {
                double coolJoules = (fluidEfficiencyBoost + getCoolingBonus(itemStack)) * getCoolingFactor();
//                System.out.println("cool joules: " + coolJoules);

                if (ElectricItemUtils.getPlayerEnergy(player) > coolJoules) {

//                    System.out.println("cooling normally");

                    coolJoules = HeatUtils.coolPlayer(player, coolJoules);

                    ElectricItemUtils.drainPlayerEnergy(player,
                            (int) (coolJoules * getEnergyConsumption(itemStack)));
                }

                // sacrificial emergency cooling
            } else {
                // how much player is overheating
                double overheatAmount = currentHeat - maxHeat;

                int fluidLevel = fluidUtils.getFluidLevel();

                boolean usedEmergencyCooling = false;
                // if system has enough fluid using this "very special" formula
                if (fluidLevel >= (int) (fluidEfficiencyBoost * overheatAmount)) {
                    fluidUtils.drain((int) (fluidEfficiencyBoost * overheatAmount));
                    HeatUtils.coolPlayer(player, overheatAmount + 1);
                    usedEmergencyCooling = true;

                    // sacrifice whatever fluid is in the system
                } else if (fluidLevel > 0) {
                    fluidUtils.drain(fluidLevel);
                    HeatUtils.coolPlayer(player, fluidEfficiencyBoost * fluidLevel);
                    usedEmergencyCooling = true;
                }

                if (usedEmergencyCooling)
                    for (int i = 0; i < 4; i++) {
                        player.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, player.posX, player.posY + 0.5, player.posZ, 0.0D, 0.0D, 0.0D);
                    }
            }
        }
    }

    public abstract double getCoolingFactor();

    public abstract double getCoolingBonus(@Nonnull ItemStack itemStack);

    public abstract double getEnergyConsumption(@Nonnull ItemStack itemStack);

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
    }

    @Override
    public abstract TextureAtlasSprite getIcon(ItemStack item);

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.ENVIRONMENTAL;
    }

    @Override
    public abstract String getDataName();
}
