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

package com.github.machinemuse.powersuits.powermodule.tool;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.legacy.module.IRightClickModule;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.entity.EntityLuxCapacitor;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class LuxCapacitor extends PowerModuleBase implements IRightClickModule {
    public LuxCapacitor(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), new ItemStack(Items.GLOWSTONE_DUST, 1));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), new ItemStack(Items.IRON_INGOT, 2));
        addBasePropertyDouble(MPSModuleConstants.LUX_CAPACITOR_ENERGY_CONSUMPTION, 1000, "RF");
        addTradeoffPropertyDouble(MPSModuleConstants.RED, MPSModuleConstants.LUX_CAPACITOR_RED_HUE, 1, "%");
        addTradeoffPropertyDouble(MPSModuleConstants.GREEN, MPSModuleConstants.LUX_CAPACITOR_GREEN_HUE, 1, "%");
        addTradeoffPropertyDouble(MPSModuleConstants.BLUE, MPSModuleConstants.LUX_CAPACITOR_BLUE_HUE, 1, "%");
//        addTradeoffPropertyDouble("alpha", )// TODO: add alpha for the lense
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.TOOL;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_LUX_CAPACITOR__DATANAME;
    }

    @Override
    public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        playerIn.setActiveHand(hand);
        if (!worldIn.isRemote) {
            double energyConsumption = getEnergyUsage(itemStackIn);
            HeatUtils.heatPlayer(playerIn, energyConsumption / 500);
            if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption) {
                ElectricItemUtils.drainPlayerEnergy(playerIn, (int) energyConsumption);

                double red = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStackIn, MPSModuleConstants.LUX_CAPACITOR_RED_HUE);
                double green = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStackIn, MPSModuleConstants.LUX_CAPACITOR_GREEN_HUE);
                double blue = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStackIn, MPSModuleConstants.LUX_CAPACITOR_BLUE_HUE);

                EntityLuxCapacitor luxCapacitor = new EntityLuxCapacitor(worldIn, playerIn, new Colour(red, green, blue));
                worldIn.spawnEntity(luxCapacitor);
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
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.LUX_CAPACITOR_ENERGY_CONSUMPTION);
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.luxCapacitor;
    }
}
