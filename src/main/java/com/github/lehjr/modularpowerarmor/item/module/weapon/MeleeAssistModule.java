package com.github.lehjr.modularpowerarmor.item.module.weapon;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MeleeAssistModule extends AbstractPowerModule {
    public MeleeAssistModule(String regName) {
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
            this.moduleCap = new PowerModule(module, EnumModuleCategory.WEAPON, EnumModuleTarget.TOOLONLY, MPASettings.getModuleConfig());
            this.moduleCap.addBaseProperty(MPAConstants.PUNCH_ENERGY, 10, "RF");
            this.moduleCap.addBaseProperty(MPAConstants.PUNCH_DAMAGE, 2, "pt");
            this.moduleCap.addTradeoffProperty(MPAConstants.IMPACT, MPAConstants.PUNCH_ENERGY, 1000, "RF");
            this.moduleCap.addTradeoffProperty(MPAConstants.IMPACT, MPAConstants.PUNCH_DAMAGE, 8, "pt");
            this.moduleCap.addTradeoffProperty(MPAConstants.CARRY_THROUGH, MPAConstants.PUNCH_ENERGY, 200, "RF");
            this.moduleCap.addTradeoffProperty(MPAConstants.CARRY_THROUGH, MPAConstants.PUNCH_KNOCKBACK, 1, "P");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> moduleCap));
        }
    }
}