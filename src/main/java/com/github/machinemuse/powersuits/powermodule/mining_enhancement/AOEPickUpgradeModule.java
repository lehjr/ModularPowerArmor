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

package com.github.machinemuse.powersuits.powermodule.mining_enhancement;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IBlockBreakingModule;
import com.github.lehjr.mpalib.legacy.module.IMiningEnhancementModule;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import com.github.machinemuse.powersuits.utils.MusePlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Created by Eximius88 on 1/29/14.
 */
public class AOEPickUpgradeModule extends PowerModuleBase implements IMiningEnhancementModule {
    public AOEPickUpgradeModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.solenoid, 1));
        //ModuleManager.INSTANCE.addInstallCost(getDataName(), new ItemStack(Item.diamond, 3));

        addBasePropertyDouble(MPSModuleConstants.AOE_ENERGY_CONSUMPTION, 500, "RF");
        addTradeoffPropertyDouble(MPSModuleConstants.DIAMETER, MPSModuleConstants.AOE_ENERGY_CONSUMPTION, 9500);
        addIntTradeoffProperty(MPSModuleConstants.DIAMETER, MPSModuleConstants.AOE_MINING_RADIUS, 5, "m", 2, 1);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemStack, BlockPos posIn, EntityPlayer player) {
        if (player.world.isRemote)
            return false; // fixme : check?

        RayTraceResult rayTraceResult = MusePlayerUtils.raytraceBlocks(player.world, player, true, 10);
        if (rayTraceResult == null)
            return false;

        int radius =  (int) (ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.AOE_MINING_RADIUS) -1)/2 ;

        if (radius == 0)
            return false;

        EnumFacing side = rayTraceResult.sideHit;
        Iterable<BlockPos> posList;
        switch(side) {
            case UP:
            case DOWN:
                posList = BlockPos.getAllInBox(posIn.north(radius).west(radius), posIn.south(radius).east(radius));
                break;

            case EAST:
            case WEST:
                posList = BlockPos.getAllInBox(posIn.up(radius).north(radius), posIn.down(radius).south(radius));
                break;

            case NORTH:
            case SOUTH:
                posList = BlockPos.getAllInBox(posIn.up(radius).west(radius), posIn.down(radius).east(radius));
                break;

            default:
                posList = new ArrayList<>();
        }
        int energyUsage = this.getEnergyUsage(itemStack);

        boolean harvested = false;
        for (BlockPos blockPos : posList) {
            IBlockState state = player.world.getBlockState(blockPos).getActualState(player.world, blockPos);
            Block block = state.getBlock();

            for (IPowerModule module : ModuleManager.INSTANCE.getModulesOfType(IBlockBreakingModule.class)) {
                int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);

                if (ModuleManager.INSTANCE.itemHasActiveModule(itemStack, module.getDataName()) && ((IBlockBreakingModule) module).canHarvestBlock(itemStack, state, player, blockPos, playerEnergy - energyUsage)) {
                    if (block.removedByPlayer(state, player.world, blockPos, player, true)) {
                        if (posIn == blockPos) // center block
                            harvested = true;

                        block.onPlayerDestroy(player.world, blockPos, state);
                        block.harvestBlock(player.world, player, blockPos, state, player.world.getTileEntity(blockPos), player.getHeldItemMainhand());


//                    block.onBlockHarvested(player.world, blockPos, state, player);
//                    player.world.playEvent(null, 2001, blockPos, Block.getStateId(state));
//                    player.world.setBlockToAir(blockPos);
//                    block.breakBlock(player.world, blockPos, state);
//                    block.dropBlockAsItem(player.world, blockPos, state, 0);

                        ElectricItemUtils.drainPlayerEnergy(player, ((IBlockBreakingModule) module).getEnergyUsage(itemStack) + energyUsage);
                        break;
                    }
                }
            }
        }
        return harvested;
    }

    @Override
    public int getEnergyUsage(@Nonnull ItemStack itemStack) {
        return (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.AOE_ENERGY_CONSUMPTION);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.MINING_ENHANCEMENT;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_AOE_PICK_UPGRADE__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.aoePickUpgrade;
    }
}