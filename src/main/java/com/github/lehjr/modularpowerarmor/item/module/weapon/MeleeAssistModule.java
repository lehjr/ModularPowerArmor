package com.github.lehjr.modularpowerarmor.item.module.weapon;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.module.powermodule.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MeleeAssistModule extends AbstractPowerModule {
    public MeleeAssistModule(String regName) {
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
            this.moduleCap = new PowerModule(module, EnumModuleCategory.WEAPON, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
            this.moduleCap.addBasePropertyDouble(Constants.PUNCH_ENERGY, 10, "RF");
            this.moduleCap.addBasePropertyDouble(Constants.PUNCH_DAMAGE, 2, "pt");
            this.moduleCap.addTradeoffPropertyDouble(Constants.IMPACT, Constants.PUNCH_ENERGY, 1000, "RF");
            this.moduleCap.addTradeoffPropertyDouble(Constants.IMPACT, Constants.PUNCH_DAMAGE, 8, "pt");
            this.moduleCap.addTradeoffPropertyDouble(Constants.CARRY_THROUGH, Constants.PUNCH_ENERGY, 200, "RF");
            this.moduleCap.addTradeoffPropertyDouble(Constants.CARRY_THROUGH, Constants.PUNCH_KNOCKBACK, 1, "P");
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