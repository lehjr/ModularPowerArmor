package com.github.machinemuse.powersuits.event;

import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.legacy.module.IBlockBreakingModule;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.tool.ItemPowerFist;
import com.github.machinemuse.powersuits.utils.MusePlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HarvestEventHandler {
    @SubscribeEvent
    public void handleHarvestCheck(PlayerEvent.HarvestCheck event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = player.inventory.getCurrentItem();

        if (!stack.isEmpty() && stack.getItem() instanceof ItemPowerFist) {
            IBlockState state = event.getTargetBlock();
            if (state == null)
                return;
            RayTraceResult rayTraceResult = MusePlayerUtils.raytraceBlocks(player.world, player, true, 10);
            if (rayTraceResult == null)
                return;

            BlockPos pos = rayTraceResult.getBlockPos();
            if (pos == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK)
                return;

            if (state.getBlock() == null)
                return;

            int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);
            event.setCanHarvest(((ItemPowerFist) stack.getItem()).canHarvestBlock(stack, state, player, pos, playerEnergy));
        }
    }

    @SubscribeEvent
    public void handleBreakSpeed(PlayerEvent.BreakSpeed event) {
        // Note: here we can actually get the position if needed. we can't om the harvest check.
        IBlockState state = event.getState();
        EntityPlayer player = event.getEntityPlayer();

        ItemStack stack = player.inventory.getCurrentItem();
        if (!stack.isEmpty() && stack.getItem() instanceof ItemPowerFist) {
            if (event.getNewSpeed() < event.getOriginalSpeed())
                event.setNewSpeed(event.getOriginalSpeed());

            // TODO: add a way to look for the actual tool required instead of looping through multiple checks.

            int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);
            for (IPowerModule module : ModuleManager.INSTANCE.getModulesOfType(IBlockBreakingModule.class)) {
                if (ModuleManager.INSTANCE.itemHasActiveModule(stack, module.getDataName()) && ((IBlockBreakingModule) module).canHarvestBlock(stack, state, player, event.getPos(), playerEnergy)) {
                    if (event.getNewSpeed() == 0)
                        event.setNewSpeed(1);
                    ((IBlockBreakingModule) module).handleBreakSpeed(event);
                }
            }
        }
    }
}