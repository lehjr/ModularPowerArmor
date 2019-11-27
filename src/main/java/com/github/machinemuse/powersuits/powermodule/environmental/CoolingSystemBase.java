/*
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.powermodule.environmental;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import com.github.machinemuse.powersuits.utils.modulehelpers.FluidUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;

import javax.annotation.Nonnull;

public abstract class CoolingSystemBase extends PowerModuleBase implements IPlayerTickModule, IToggleableModule {

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
