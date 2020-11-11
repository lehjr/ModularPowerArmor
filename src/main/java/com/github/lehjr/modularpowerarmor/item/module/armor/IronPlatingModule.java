package com.github.lehjr.modularpowerarmor.item.module.armor;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.basemod.MPALibConstants;
import com.github.lehjr.mpalib.util.capabilities.heat.HeatCapability;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IronPlatingModule extends AbstractPowerModule {
    public IronPlatingModule() {
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
                moduleCap = new PowerModule(module, EnumModuleCategory.ARMOR, EnumModuleTarget.ARMORONLY, MPASettings::getModuleConfig);
                moduleCap.addBaseProperty(MPAConstants.ARMOR_VALUE_PHYSICAL, 4, MPALibConstants.MODULE_TRADEOFF_PREFIX + MPAConstants.ARMOR_POINTS);
                moduleCap.addBaseProperty(HeatCapability.MAXIMUM_HEAT, 300);
                moduleCap.addBaseProperty(MPAConstants.KNOCKBACK_RESISTANCE, 0.25F);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> moduleCap));
        }
    }
}