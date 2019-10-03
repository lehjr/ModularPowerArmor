package net.machinemuse.powersuits.event;

import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.capabilities.module.blockbreaking.IBlockBreakingModule;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;
import java.util.Objects;

public class HarvestEventHandler {
    @SubscribeEvent
    public static void handleBlockBreak(BlockEvent.BreakEvent event) {
        System.out.println("handleHarvestCheck");

    }



    @SubscribeEvent
    public static void handleHarvestCheck(PlayerEvent.HarvestCheck event) {
        PlayerEntity player = event.getEntityPlayer();
        if (player == null) {
            return;
        }

        BlockState state = event.getTargetBlock();
        if (state == null ||state.getBlock() == null) {
            return;
        }

        ItemStack stack = player.inventory.getCurrentItem();
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
            if(iItemHandler instanceof IModeChangingItem) {
                if (state.getMaterial().isToolNotRequired()) {
                    event.setCanHarvest(true);
                    return;
                }

                RayTraceResult rayTraceResult = rayTrace(player.world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
                if (rayTraceResult == null || rayTraceResult.getType() != RayTraceResult.Type.BLOCK)
                    return;

                BlockPos pos = new BlockPos(rayTraceResult.getHitVec());
                if (pos == null)
                    return;

                if (state.getMaterial().isToolNotRequired()) {
                    event.setCanHarvest(true);
                    return;
                }

                int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);

                for (ItemStack module : ((IModeChangingItem) iItemHandler).getInstalledModulesOfType(IBlockBreakingModule.class)) {
                    if (module.getCapability(PowerModuleCapability.POWER_MODULE)
                            .map(pm->pm instanceof IBlockBreakingModule && ((IBlockBreakingModule) pm).canHarvestBlock(stack, state, player, pos, playerEnergy)).orElse(false)) {
                        event.setCanHarvest(true);
                        return;
                    }
                }
            }
        });
    }

    // copied from vanilla item
    protected static RayTraceResult rayTrace(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        float pitch = player.rotationPitch;
        float yaw = player.rotationYaw;
        Vec3d vec3d = player.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-yaw * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-pitch * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-pitch * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();;
        Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
    }

    @SubscribeEvent
    public static void handleBreakSpeed(PlayerEvent.BreakSpeed event) {
        // Note: here we can actually get the position if needed. we can't easily om the harvest check.
        PlayerEntity player = event.getEntityPlayer();
        ItemStack stack = player.inventory.getCurrentItem();
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
            BlockState state = event.getState();

            // wait... what is this again?
            if (event.getNewSpeed() < event.getOriginalSpeed())
                event.setNewSpeed(event.getOriginalSpeed());
            int playerEnergy = ElectricItemUtils.getPlayerEnergy(player);
            for (ItemStack module : ((IModeChangingItem) iItemHandler).getInstalledModulesOfType(IBlockBreakingModule.class)) {
                module.getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(pm -> {
                    if(pm instanceof IBlockBreakingModule && ((IBlockBreakingModule) pm).canHarvestBlock(stack, state, player, event.getPos(), playerEnergy)) {
                        System.out.println("module: " + pm.getModuleStack());

                        if (event.getNewSpeed() == 0)
                            event.setNewSpeed(1);
                        ((IBlockBreakingModule) pm).handleBreakSpeed(event);
                    }
                });
            }
        });
    }

//    @SubscribeEvent
//    public void onBlockBreak(BlockEvent.BreakEvent e) {
//
//    }

    @SubscribeEvent
    public static void handHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        System.out.println("doing something here");


        if (event.getHarvester() != null) {
            PlayerEntity player = event.getHarvester();
            player.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                if(iItemHandler instanceof IModeChangingItem) {
                    List<ItemStack> originalDrops = event.getDrops();
                    for (ItemStack item:  originalDrops) {
                        System.out.println(item.serializeNBT());
                    }

                    NonNullList<ItemStack> test = NonNullList.create();
                    test.addAll(event.getDrops());

                    event.setResult(new BlockEvent.HarvestDropsEvent(
                            (World)event.getWorld(),
                            event.getPos(),
                            event.getState(),
                            event.getFortuneLevel(),
                            event.getDropChance(),
                            test,
                            event.getHarvester(),
                            true).getResult());

                    List<ItemStack> newList = event.getDrops();

                    for (ItemStack item:  newList) {
                        System.out.println(item.serializeNBT());
                    }

                    System.out.println("new drops size: " + newList.size());

                    System.out.println("new list same as old list? : " + Objects.equals(originalDrops, newList));
                }
            });
        }
    }
}