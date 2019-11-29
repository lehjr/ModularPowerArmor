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
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.legacy.module.IRightClickModule;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by User: Andrew2448
 * 7:45 PM 4/23/13
 * <p>
 * Updated by leon on 6/14/16.
 */

public class TreetapModule extends PowerModuleBase implements IRightClickModule {
    public static ItemStack resin;
    public static Block rubber_wood;
    public static ItemStack emulatedTool;
    public static ItemStack treetap;
    private Method attemptExtract;
    private boolean isIC2Classic;

    public TreetapModule(EnumModuleTarget moduleTarget) {
        // TODO: add support for tree taps from other mods?

        super(moduleTarget);
        if (ModCompatibility.isIndustrialCraftClassicLoaded()) {
            emulatedTool = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "itemTreetapElectric")), 1);
            treetap = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "itemTreetap")), 1);
            resin = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "misc_resource")), 1, 4);
            rubber_wood = Block.REGISTRY.getObject(new ResourceLocation("ic2", "blockRubWood"));
            try {
                attemptExtract = treetap.getItem().getClass().
                        getDeclaredMethod("attemptExtract", ItemStack.class, EntityPlayer.class, World.class, BlockPos.class, EnumFacing.class, List.class);
            } catch (Exception ignored) {

            }
            isIC2Classic = true;
        } else {
            emulatedTool = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "electric_treetap")), 1);
            treetap = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "treetap")), 1);
            resin = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("ic2", "misc_resource")), 1, 4);
            rubber_wood = Block.REGISTRY.getObject(new ResourceLocation("ic2", "rubber_wood"));
            try {
                attemptExtract = treetap.getItem().getClass().
                        getDeclaredMethod("attemptExtract", EntityPlayer.class, World.class, BlockPos.class, EnumFacing.class, IBlockState.class, List.class);
            } catch (Exception ignored) {

            }
            isIC2Classic = false;
        }
        ModuleManager.INSTANCE.addInstallCost(getDataName(), emulatedTool);

        addBasePropertyDouble(MPSModuleConstants.ENERGY_CONSUMPTION, 1000, "RF");
    }

    @Override
    public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        try {
            // IC2 Classic
            if (isIC2Classic) {
                if (block == rubber_wood && getEnergyUsage(itemStack) < ElectricItemUtils.getPlayerEnergy(player)) {
                    if (attemptExtract.invoke("attemptExtract", null, player, world, pos, facing, null).equals(true)) {
                        ElectricItemUtils.drainPlayerEnergy(player, (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.ENERGY_CONSUMPTION));
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
            // IC2 Experimental
            else {
                if (block == rubber_wood && getEnergyUsage(itemStack) < ElectricItemUtils.getPlayerEnergy(player)) {
                    if (attemptExtract.invoke("attemptExtract", player, world, pos, facing, state, null).equals(true)) {
                        ElectricItemUtils.drainPlayerEnergy(player, (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.ENERGY_CONSUMPTION));
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
            return EnumActionResult.PASS;
        } catch (Exception ignored) {

        }
        return EnumActionResult.FAIL;
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
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.TOOL;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_TREETAP__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(emulatedTool).getParticleTexture();
    }
}