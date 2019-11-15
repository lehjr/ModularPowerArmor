package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.capabilities.module.toggleable.ToggleableModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShockAbsorberModule extends AbstractPowerModule {

    public ShockAbsorberModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IToggleableModule moduleToggle;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleToggle = new ToggleableModule(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.FEETONLY, MPAConfig.moduleConfig, true);
            this.moduleToggle.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 0, "RF/m");
            this.moduleToggle.addTradeoffPropertyDouble(Constants.POWER, Constants.ENERGY_CONSUMPTION, 100);
            this.moduleToggle.addBasePropertyDouble(Constants.MULTIPLIER, 0, "%");
            this.moduleToggle.addTradeoffPropertyDouble(Constants.POWER, Constants.MULTIPLIER, 10);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                moduleToggle.updateFromNBT();
                return (T) moduleToggle;
            }
            return null;
        }
    }
}