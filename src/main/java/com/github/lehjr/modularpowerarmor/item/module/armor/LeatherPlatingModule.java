package com.github.lehjr.modularpowerarmor.item.module.armor;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.module.powermodule.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LeatherPlatingModule extends AbstractPowerModule {
    public LeatherPlatingModule(String regName) {
        super(regName);
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
            moduleCap = new PowerModule(module, EnumModuleCategory.ARMOR, EnumModuleTarget.ARMORONLY, CommonConfigX.moduleConfig);
            moduleCap.addBasePropertyDouble(MPAConstants.ARMOR_VALUE_PHYSICAL, 3, MPALIbConstants.MODULE_TRADEOFF_PREFIX + MPAConstants.ARMOR_POINTS);
            moduleCap.addBasePropertyDouble(MPAConstants.MAXIMUM_HEAT, 75);
            moduleCap.addBasePropertyDouble(MPAConstants.KNOCKBACK_RESISTANCE, 0.25, "");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> moduleCap));
        }
    }
}