package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.blockbreaking.IBlockBreakingModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
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


public class AxeModule extends AbstractPowerModule {
    public AxeModule(String regName) {
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
            this.blockBreaking = new BlockBreaker(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.blockBreaking.addBasePropertyFloat(MPAConstants.ENERGY_CONSUMPTION, 500, "RF");
            this.blockBreaking.addBasePropertyFloat(MPAConstants.HARVEST_SPEED, 8, "x");
            this.blockBreaking.addTradeoffPropertyFloat(MPAConstants.OVERCLOCK, MPAConstants.ENERGY_CONSUMPTION, 9500);
            this.blockBreaking.addTradeoffPropertyFloat(MPAConstants.OVERCLOCK, MPAConstants.HARVEST_SPEED, 22);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> blockBreaking));
        }

        class BlockBreaker extends PowerModule implements IBlockBreakingModule {
            public BlockBreaker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
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
                return new ItemStack(Items.IRON_AXE);
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