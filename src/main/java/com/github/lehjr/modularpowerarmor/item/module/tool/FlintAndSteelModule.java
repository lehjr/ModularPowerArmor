package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * Created by User: Andrew2448
 * 10:48 PM 6/11/13
 */
public class FlintAndSteelModule extends AbstractPowerModule {
    public FlintAndSteelModule(String regName) {
        super(regName);//
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IRightClickModule rightClick;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClick = new RightClickie(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, MPASettings.getModuleConfig());
            this.rightClick.addBasePropertyDouble(MPAConstants.ENERGY_CONSUMPTION, 10000, "RF");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> rightClick));
        }

        class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config);
            }

            /**
             * Called when this item is used when targetting a Block
             */
            @Override
            public ActionResultType onItemUse(ItemUseContext context) {
                int energyConsumption = getEnergyUsage();
                PlayerEntity player = context.getPlayer();
                if (ElectricItemUtils.getPlayerEnergy(player) < energyConsumption )
                    return ActionResultType.FAIL;

                IWorld world = context.getWorld();
                BlockPos pos1 = context.getPos();
                BlockPos pos2 = pos1.offset(context.getFace());

                if (canIgnite(world.getBlockState(pos2), world, pos2)) {
                    world.playSound(player, pos2, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
                    BlockState blockstate1 = ((FireBlock) Blocks.FIRE).getStateForPlacement(world, pos2);
                    world.setBlockState(pos2, blockstate1, 11);
                    ItemStack itemstack = context.getItem();
                    if (player instanceof ServerPlayerEntity) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos2, itemstack);
                        ElectricItemUtils.drainPlayerEnergy(player, energyConsumption);
                    }

                    return ActionResultType.SUCCESS;
                } else {
                    BlockState blockstate = world.getBlockState(pos1);
                    if (func_219997_a(blockstate)) {
                        world.playSound(player, pos1, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
                        world.setBlockState(pos1, blockstate.with(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
                        if (player != null) {
                            ElectricItemUtils.drainPlayerEnergy(player, energyConsumption);
                        }

                        return ActionResultType.SUCCESS;
                    } else {
                        return ActionResultType.FAIL;
                    }
                }
            }

            public boolean func_219997_a(BlockState state) {
                return state.getBlock() == Blocks.CAMPFIRE && !state.get(BlockStateProperties.WATERLOGGED) && !state.get(BlockStateProperties.LIT);
            }

            public boolean canIgnite(BlockState state, IWorld world, BlockPos pos) {
                BlockState blockstate = ((FireBlock)Blocks.FIRE).getStateForPlacement(world, pos);
                boolean flag = false;

                for(Direction direction : Direction.Plane.HORIZONTAL) {
                    if (world.getBlockState(pos.offset(direction)).getBlock() == Blocks.OBSIDIAN && ((NetherPortalBlock)Blocks.NETHER_PORTAL).isPortal(world, pos) != null) {
                        flag = true;
                    }
                }
                return state.isAir() && (blockstate.isValidPosition(world, pos) || flag);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}