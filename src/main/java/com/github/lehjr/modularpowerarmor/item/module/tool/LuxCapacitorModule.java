package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.util.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.util.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.util.heat.HeatUtils;
import com.github.lehjr.mpalib.util.math.Colour;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class LuxCapacitorModule extends AbstractPowerModule {
    public LuxCapacitorModule() {
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
            this.rightClick = new RightClickie(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, MPASettings::getModuleConfig);
            this.rightClick.addBaseProperty(MPAConstants.ENERGY_CONSUMPTION, 1000, "FE");
            this.rightClick.addTradeoffProperty(MPAConstants.RED, MPAConstants.RED_HUE, 1, "%");
            this.rightClick.addTradeoffProperty(MPAConstants.GREEN, MPAConstants.GREEN_HUE, 1, "%");
            this.rightClick.addTradeoffProperty(MPAConstants.BLUE, MPAConstants.BLUE_HUE, 1, "%");
            this.rightClick.addTradeoffProperty(MPAConstants.ALPHA, MPAConstants.OPACITY, 1, "%");
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

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                float energyConsumption = getEnergyUsage();
                if (ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption) {
                    if (!worldIn.isRemote) {
                        HeatUtils.heatPlayer(playerIn, energyConsumption / 500);

                        ElectricItemUtils.drainPlayerEnergy(playerIn, (int) energyConsumption);

                        float red = (float) applyPropertyModifiers(MPAConstants.RED_HUE);
                        float green = (float) applyPropertyModifiers(MPAConstants.GREEN_HUE);
                        float blue = (float) applyPropertyModifiers(MPAConstants.BLUE_HUE);
                        float alpha = (float) applyPropertyModifiers(MPAConstants.OPACITY);

                        LuxCapacitorEntity luxCapacitor = new LuxCapacitorEntity(worldIn, playerIn, new Colour(red, green, blue, alpha));
                        worldIn.addEntity(luxCapacitor);
                    }
                    return ActionResult.resultSuccess(itemStackIn);
                }
                return ActionResult.resultPass(itemStackIn);
            }

            @Override
            public int getEnergyUsage() {
                return (int) applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION);
            }
        }
    }
}