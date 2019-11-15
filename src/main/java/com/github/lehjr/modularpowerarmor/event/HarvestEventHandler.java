package com.github.lehjr.modularpowerarmor.event;

import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.Optional;

public class HarvestEventHandler {
    @SubscribeEvent
    public void handleHarvestCheck(PlayerEvent.HarvestCheck event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null) {
            return;
        }

        IBlockState state = event.getTargetBlock();
        if (state == null ||state.getBlock() == null) {
            return;
        }

        ItemStack stack = player.inventory.getCurrentItem();
        Optional.ofNullable(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(iItemHandler -> {
            if(iItemHandler instanceof IModeChangingItem) {
                if (state.getMaterial().isToolNotRequired()) {
                    event.setCanHarvest(true);
                    return;
                }

                RayTraceResult rayTraceResult = rayTrace(player.world, player, false);
                if (rayTraceResult == null || rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK)
                    return;

                BlockPos pos = new BlockPos(rayTraceResult.hitVec);
                if (pos == null)
                    return;

                if (state.getMaterial().isToolNotRequired()) {
                    event.setCanHarvest(true);
                    return;
                }

                int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);

                for (ItemStack module : ((IModeChangingItem) iItemHandler).getInstalledModulesOfType(com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule.class)) {
                    if (Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null))
                            .map(pm->pm instanceof com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule && ((com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule) pm).canHarvestBlock(stack, state, player, pos, playerEnergy)).orElse(false)) {
                        event.setCanHarvest(true);
                        return;
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void handleBreakSpeed(PlayerEvent.BreakSpeed event) {
        // Note: here we can actually get the position if needed. we can't easily om the harvest check.
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = player.inventory.getCurrentItem();
        Optional.ofNullable(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(iItemHandler -> {
            IBlockState state = event.getState();

            // wait... what is this again?
            if (event.getNewSpeed() < event.getOriginalSpeed())
                event.setNewSpeed(event.getOriginalSpeed());
            int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);
            for (ItemStack module : ((IModeChangingItem) iItemHandler).getInstalledModulesOfType(com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule.class)) {
                Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).ifPresent(pm -> {
                    if(pm instanceof com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule && ((com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule) pm).canHarvestBlock(stack, state, player, event.getPos(), playerEnergy)) {
                        System.out.println("module: " + pm.getModuleStack());

                        if (event.getNewSpeed() == 0)
                            event.setNewSpeed(1);
                        ((com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule) pm).handleBreakSpeed(event);
                    }
                });
            }
        });
    }

    // copied from vanilla item
    protected RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
        float pitch = playerIn.rotationPitch;
        float yaw = playerIn.rotationYaw;
        double posX = playerIn.posX;
        double posY = playerIn.posY + (double)playerIn.getEyeHeight();
        double posZ = playerIn.posZ;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        float f2 = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f3 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f4 = -MathHelper.cos(-pitch * 0.017453292F);
        float f5 = MathHelper.sin(-pitch * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = playerIn.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        Vec3d vec3d1 = vec3d.add((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
        return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
    }






}