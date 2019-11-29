/*
 * ModularPowersuits (Maintenance builds by lehjr)
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

package com.github.machinemuse.powersuits.powermodule.weapon;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IRightClickModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MPSIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import com.github.machinemuse.powersuits.utils.MusePlayerUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by User: Andrew2448
 * 5:56 PM 6/14/13
 */
public class LightningModule extends PowerModuleBase implements IRightClickModule {
    public LightningModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.hvcapacitor, 1));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.solenoid, 2));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.fieldEmitter, 2));

        addBasePropertyDouble(MPSModuleConstants.ENERGY_CONSUMPTION, 4900000, "RF");
        addBasePropertyDouble(MPSModuleConstants.HEAT_EMISSION, 100, "");
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.WEAPON;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_LIGHTNING__DATANAME;
    }

    @Override
    public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND) {
            try {
                double range = 64;
                int energyConsumption = getEnergyUsage(itemStackIn);
                if (energyConsumption < ElectricItemUtils.getPlayerEnergy(playerIn)) {
                    ElectricItemUtils.drainPlayerEnergy(playerIn, energyConsumption);
                    HeatUtils.heatPlayer(playerIn, ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStackIn, MPSModuleConstants.HEAT_EMISSION));
                    RayTraceResult raytraceResult = MusePlayerUtils.doCustomRayTrace(playerIn.world, playerIn, true, range);
                    worldIn.spawnEntity(new EntityLightningBolt(playerIn.world, raytraceResult.hitVec.x, raytraceResult.hitVec.y, raytraceResult.hitVec.z, false));
                }
            } catch (Exception ignored) {
                return ActionResult.newResult(EnumActionResult.FAIL, itemStackIn);
            }
            return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
        }
        return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.PASS;
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        return EnumActionResult.PASS;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {

    }

    @Override
    public int getEnergyUsage(@Nonnull ItemStack itemStack) {
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.ENERGY_CONSUMPTION);
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MPSIcon.lightning;
    }
}