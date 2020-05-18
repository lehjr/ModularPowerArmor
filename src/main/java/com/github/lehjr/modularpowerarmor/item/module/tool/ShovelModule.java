package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.*;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class ShovelModule extends AbstractPowerModule {//
    public ShovelModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IBlockBreakingModule blockBreaking;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.blockBreaking = new BlockBreaker(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, MPASettings.getModuleConfig());
            this.blockBreaking.addBasePropertyDouble(MPAConstants.ENERGY_CONSUMPTION, 500, "RF");
            this.blockBreaking.addBasePropertyDouble(MPAConstants.HARVEST_SPEED, 8, "x");
            this.blockBreaking.addTradeoffPropertyDouble(MPAConstants.OVERCLOCK, MPAConstants.ENERGY_CONSUMPTION, 9500);
            this.blockBreaking.addTradeoffPropertyDouble(MPAConstants.OVERCLOCK, MPAConstants.HARVEST_SPEED, 22);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> blockBreaking));
        }

        class BlockBreaker extends PowerModule implements IBlockBreakingModule {
            public BlockBreaker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config);
            }

            @Override
            public boolean onBlockDestroyed(ItemStack itemStack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving, int playerEnergy) {
                if (this.canHarvestBlock(itemStack, state, (PlayerEntity) entityLiving, pos, playerEnergy)) {
                    ElectricItemUtils.drainPlayerEnergy((PlayerEntity) entityLiving, getEnergyUsage());
                    return true;
                }
                return false;
            }

            @Override
            public ItemStack getEmulatedTool() {
                return new ItemStack(Items.IRON_SHOVEL);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION);
            }

            @Override
            public void handleBreakSpeed(PlayerEvent.BreakSpeed event) {
                event.setNewSpeed((float) (event.getNewSpeed() * applyPropertyModifiers(MPAConstants.HARVEST_SPEED)));
            }
        }
    }
}