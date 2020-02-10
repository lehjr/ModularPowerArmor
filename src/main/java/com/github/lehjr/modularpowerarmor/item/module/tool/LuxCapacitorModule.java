package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LuxCapacitorModule extends AbstractPowerModule {
    public LuxCapacitorModule(String regName) {
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
            this.rightClick.addBasePropertyDouble(MPAConstants.ENERGY_CONSUMPTION, 1000, "RF");
            this.rightClick.addTradeoffPropertyDouble(MPAConstants.RED, MPAConstants.RED_HUE, 1, "%");
            this.rightClick.addTradeoffPropertyDouble(MPAConstants.GREEN, MPAConstants.GREEN_HUE, 1, "%");
            this.rightClick.addTradeoffPropertyDouble(MPAConstants.BLUE, MPAConstants.BLUE_HUE, 1, "%");
            this.rightClick.addTradeoffPropertyDouble(MPAConstants.ALPHA, MPAConstants.OPACITY, 1, "%");
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
                playerIn.setActiveHand(hand);
                if (!worldIn.isRemote) {
                    double energyConsumption = getEnergyUsage();
                    HeatUtils.heatPlayer(playerIn, energyConsumption / 500);
                    if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption) {
                        ElectricItemUtils.drainPlayerEnergy(playerIn, (int) energyConsumption);

                        double red = applyPropertyModifiers(MPAConstants.RED_HUE);
                        double green = applyPropertyModifiers(MPAConstants.GREEN_HUE);
                        double blue = applyPropertyModifiers(MPAConstants.BLUE_HUE);
                        double alpha = applyPropertyModifiers(MPAConstants.OPACITY);

                        LuxCapacitorEntity luxCapacitor = new LuxCapacitorEntity(worldIn, playerIn, new Colour(red, green, blue, alpha));
                        worldIn.addEntity(luxCapacitor);
                    }
                    return ActionResult.newResult(ActionResultType.SUCCESS, itemStackIn);
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