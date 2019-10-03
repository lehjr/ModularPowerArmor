package net.machinemuse.powersuits.item.module.movement;


import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.heat.MuseHeatUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
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
            this.rightClick = new RightClickie(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.TOOLONLY, CommonConfig.moduleConfig);
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
                    int energyConsumption = (int) applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
                    int playerEnergy = ElectricItemUtils.getPlayerEnergy(playerIn);
                    if (playerEnergy >= energyConsumption) {
                        ElectricItemUtils.drainPlayerEnergy(playerIn, getEnergyUsage());
                        MuseHeatUtils.heatPlayer(playerIn, applyPropertyModifiers(MPSConstants.HEAT_GENERATION));
                        return ActionResult.newResult(ActionResultType.SUCCESS, itemStackIn);
                    }
                }
                return ActionResult.newResult(ActionResultType.PASS, itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}
