package com.github.lehjr.modularpowerarmor.item.module.movement;


import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Eximius88 on 2/3/14.
 */
public class DimensionalRiftModule extends AbstractPowerModule {
    public DimensionalRiftModule(String regName) {
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
            this.rightClick = new RightClickie(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.TOOLONLY, CommonConfigX.moduleConfig);
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
                if (!playerIn.isPassenger() && !playerIn.isBeingRidden() && playerIn.isNonBoss() && !playerIn.world.isRemote()) {
                    BlockPos coords = playerIn.getBedPosition().isPresent() ? playerIn.getBedPosition().get() : playerIn.world.getSpawnPoint();

                    while (!worldIn.isAirBlock(coords) && !worldIn.isAirBlock(coords.up())) {
                        coords = coords.up();
                    }

                    playerIn.changeDimension(DimensionType.OVERWORLD);
                    int energyConsumption = (int) applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION);
                    int playerEnergy = ElectricItemUtils.getPlayerEnergy(playerIn);
                    if (playerEnergy >= energyConsumption) {
                        ElectricItemUtils.drainPlayerEnergy(playerIn, getEnergyUsage());
                        HeatUtils.heatPlayer(playerIn, applyPropertyModifiers(MPAConstants.HEAT_GENERATION));
                        return ActionResult.newResult(ActionResultType.SUCCESS, itemStackIn);
                    }
                }
                return ActionResult.newResult(ActionResultType.PASS, itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}
