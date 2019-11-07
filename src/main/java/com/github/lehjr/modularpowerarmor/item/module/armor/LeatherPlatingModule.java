package com.github.lehjr.modularpowerarmor.item.module.armor;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.module.powermodule.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LeatherPlatingModule extends AbstractPowerModule {
    public LeatherPlatingModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            moduleCap = new PowerModule(module, EnumModuleCategory.ARMOR, EnumModuleTarget.ARMORONLY, MPAConfig.moduleConfig);
            moduleCap.addBasePropertyDouble(Constants.ARMOR_VALUE_PHYSICAL, 3, MPALIbConstants.MODULE_TRADEOFF_PREFIX + Constants.ARMOR_POINTS);
            moduleCap.addBasePropertyDouble(Constants.MAXIMUM_HEAT, 75);
            moduleCap.addBasePropertyDouble(Constants.KNOCKBACK_RESISTANCE, 0.25, "");
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) moduleCap;
            }
            return null;
        }
    }
}