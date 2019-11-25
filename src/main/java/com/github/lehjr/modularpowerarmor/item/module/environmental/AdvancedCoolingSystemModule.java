package com.github.lehjr.modularpowerarmor.item.module.environmental;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AdvancedCoolingSystemModule extends CoolingSystemBase {
    public AdvancedCoolingSystemModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProviderThis(stack);
    }

    public static class CapProviderThis extends CapProvider {
        public CapProviderThis(@Nonnull ItemStack module) {
            super(module);
            this.ticker = new Ticker(module, EnumModuleCategory.ENVIRONMENTAL, EnumModuleTarget.TORSOONLY, MPAConfig.INSTANCE.moduleConfig, true);
            this.ticker.addBasePropertyInteger(Constants.FLUID_TANK_SIZE, 20000);
            this.ticker.addBasePropertyDouble(Constants.COOLING_FACTOR, 2.1);
            this.ticker.addTradeoffPropertyDouble(Constants.POWER, Constants.COOLING_BONUS, 1, "%");
            this.ticker.addTradeoffPropertyDouble(Constants.POWER, Constants.ENERGY_CONSUMPTION, 40, "RF/t");
            this.fluidHandler = new ThisModuleTank(ticker.applyPropertyModifierBaseInt(Constants.FLUID_TANK_SIZE));
        }

        public class ThisModuleTank extends ModuleTank {
            public ThisModuleTank(int tankSize) {
                super(tankSize);
            }

            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                if (fluid != null) {
                    return fluid.getFluid() == FluidRegistry.WATER;
                }
                return false;
            }
        }
    }
}