package net.machinemuse.powersuits.item.module.tool;

import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.helper.ToolHelpers;
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
            this.rightClick.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 500, "RF");
            this.rightClick.addTradeoffPropertyDouble(MPSConstants.RADIUS, MPSConstants.ENERGY_CONSUMPTION, 9500);
            this.rightClick.addBasePropertyDouble(MPSConstants.RADIUS, 1, "m");
            this.rightClick.addTradeoffPropertyDouble(MPSConstants.RADIUS, MPSConstants.RADIUS, 15);
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
                int radius = (int) applyPropertyModifiers(MPSConstants.RADIUS);
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
                return (int) applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}