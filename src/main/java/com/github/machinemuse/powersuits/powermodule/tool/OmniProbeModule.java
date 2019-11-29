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

package com.github.machinemuse.powersuits.powermodule.tool;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IRightClickModule;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MPSIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import com.github.machinemuse.powersuits.utils.modulehelpers.OmniProbeHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by User: Korynkai
 * 6:30 PM 2014-11-17
 * <p>
 * TODO: Fix ProjectRed (may require PR to ProjectRed)
 */
public class OmniProbeModule extends PowerModuleBase implements IRightClickModule, IPlayerTickModule {
    private ItemStack rcMeter = ItemStack.EMPTY;

    private ItemStack conduitProbe = ItemStack.EMPTY;

    private ItemStack teMultimeter = ItemStack.EMPTY;

//    private ItemStack rednetMeter = ItemStack.EMPTY;

//    private ItemStack euMeter = ItemStack.EMPTY;

    public OmniProbeModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.controlCircuit, 4));
        ItemStack tHighest = new ItemStack(Items.COMPARATOR);

        // Does not exist
//        if (ModCompatibility.isMFRLoaded()) {
//            rednetMeter = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("MineFactoryReloaded", "rednet.meter")), 1);
//            tHighest = rednetMeter;
//        }

        if (ModCompatibility.isRailcraftLoaded()) {
            rcMeter = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("railcraft", "tool_charge_meter")), 1);
            tHighest = rcMeter;
        }

        /* untested */
        if (ModCompatibility.isThermalExpansionLoaded()) {
            teMultimeter = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("thermalexpansion", "multimeter")), 1);
            tHighest = teMultimeter;
        }

        if (ModCompatibility.isEnderIOLoaded()) {
            conduitProbe = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("enderio", "item_conduit_probe")), 1);
            tHighest = conduitProbe;
        }
        ModuleManager.INSTANCE.addInstallCost(getDataName(), tHighest);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.TOOL;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_OMNIPROBE__DATANAME;
    }

    @Override
    public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.PASS;
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        Block block = world.getBlockState(pos).getBlock();

        if (block == null || block.isAir(world.getBlockState(pos), world, pos))
            return EnumActionResult.PASS;

        try {
            if (ModCompatibility.isEnderIOLoaded()) {
                if (conduitProbe.getItem().onItemUse(player, world, pos, EnumHand.MAIN_HAND, side, hitX, hitY, hitZ) == EnumActionResult.SUCCESS)
                    return EnumActionResult.SUCCESS;
            }

//            if (ModCompatibility.isMFRLoaded()) {
//                if (block == Block.REGISTRY.getObject(new ResourceLocation("minefactoryreloaded", "cable.redstone")))
//                    return rednetMeter.getItem().onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, EnumHand.MAIN_HAND);
//            }

            if (ModCompatibility.isRailcraftLoaded()) {
                if (rcMeter.getItem().onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS)
                    return EnumActionResult.SUCCESS;
            }

            if(ModCompatibility.isThermalExpansionLoaded()) {
                if (teMultimeter.getItem().onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, EnumHand.MAIN_HAND) == EnumActionResult.SUCCESS)
                    return EnumActionResult.SUCCESS;
            }
        } catch (Exception ignored) {

        }
        return EnumActionResult.PASS;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        if (!OmniProbeHelper.getEIOFacadeTransparency(item)) {
            OmniProbeHelper.setEIONoCompete(item, MPSModuleConstants.MODULE_OMNIPROBE__DATANAME);
            OmniProbeHelper.setEIOFacadeTransparency(item, true);
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
        if (!(OmniProbeHelper.getEIONoCompete(item).isEmpty()) && (!OmniProbeHelper.getEIONoCompete(item).isEmpty())) {
            if (OmniProbeHelper.getEIONoCompete(item).equals(MPSModuleConstants.MODULE_OMNIPROBE__DATANAME)) {
                OmniProbeHelper.setEIONoCompete(item, "");
                if (OmniProbeHelper.getEIOFacadeTransparency(item)) {
                    OmniProbeHelper.setEIOFacadeTransparency(item, false);
                }
            }
        } else {
            if (OmniProbeHelper.getEIOFacadeTransparency(item)) {
                OmniProbeHelper.setEIOFacadeTransparency(item, false);
            }
        }
    }

    public float minF(float a, float b) {
        return a < b ? a : b;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {

    }

    @Override
    public int getEnergyUsage(@Nonnull ItemStack itemStack) {
        return 0;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MPSIcon.omniProbe;
    }
}
