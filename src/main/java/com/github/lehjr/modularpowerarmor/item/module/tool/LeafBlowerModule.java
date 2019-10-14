package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.helper.ToolHelpers;
import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by User: Andrew2448
 * 7:13 PM 4/21/13
 */
public class LeafBlowerModule extends AbstractPowerModule {

    public LeafBlowerModule(String regName) {
        super(regName);
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
            this.rightClick = new RightClickie(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
            this.rightClick.addBasePropertyDouble(MPAConstants.ENERGY_CONSUMPTION, 500, "RF");
            this.rightClick.addTradeoffPropertyDouble(MPAConstants.RADIUS, MPAConstants.ENERGY_CONSUMPTION, 9500);
            this.rightClick.addBasePropertyDouble(MPAConstants.RADIUS, 1, "m");
            this.rightClick.addTradeoffPropertyDouble(MPAConstants.RADIUS, MPAConstants.RADIUS, 15);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> rightClick));
        }

        class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                int radius = (int) applyPropertyModifiers(MPAConstants.RADIUS);
                if (useBlower(radius, itemStackIn, playerIn, worldIn, playerIn.getPosition()))
                    return ActionResult.newResult(ActionResultType.SUCCESS, itemStackIn);
                return ActionResult.newResult(ActionResultType.PASS, itemStackIn);
            }

            private boolean useBlower(int radius, ItemStack itemStack, PlayerEntity player, World world, BlockPos pos) {
                int totalEnergyDrain = 0;
                BlockPos newPos;
                for (int i = pos.getX() - radius; i < pos.getX() + radius; i++) {
                    for (int j = pos.getY() - radius; j < pos.getY() + radius; j++) {
                        for (int k = pos.getZ() - radius; k < pos.getZ() + radius; k++) {
                            newPos = new BlockPos(i, j, k);
                            if (ToolHelpers.blockCheckAndHarvest(player, world, newPos)) {
                                totalEnergyDrain += getEnergyUsage();
                            }
                        }
                    }
                }
                ElectricItemUtils.drainPlayerEnergy(player, totalEnergyDrain);
                return true;
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}