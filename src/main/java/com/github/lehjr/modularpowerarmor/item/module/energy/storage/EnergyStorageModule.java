package com.github.lehjr.modularpowerarmor.item.module.energy.storage;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.energy.ForgeEnergyModuleWrapper;
import com.github.lehjr.mpalib.capabilities.energy.IEnergyWrapper;
import com.github.lehjr.mpalib.capabilities.module.powermodule.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class EnergyStorageModule extends AbstractPowerModule {
    protected final int maxEnergy;
    protected final int maxTransfer;

    public EnergyStorageModule(String regName, int maxEnergy, int maxTransfer) {
        super(regName);
        this.maxEnergy = maxEnergy;
        this.maxTransfer = maxTransfer;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;
        IEnergyWrapper energyStorage;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.ENERGY_STORAGE, EnumModuleTarget.ALLITEMS, CommonConfig.moduleConfig);
            this.moduleCap.addBasePropertyInteger(Constants.MAX_ENERGY, maxEnergy, "RF");
            this.moduleCap.addBasePropertyInteger(Constants.MAX_TRAMSFER, maxTransfer, "RF");
            this.energyStorage = new ForgeEnergyModuleWrapper(
                    module,
                    moduleCap.applyPropertyModifierBaseInt(Constants.MAX_ENERGY),
                    moduleCap.applyPropertyModifierBaseInt(Constants.MAX_TRAMSFER)
            );
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return true;
            }
            if (capability == CapabilityEnergy.ENERGY) {
                return true;
            }
            // todo: more capabilities


            return false;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE)
                return (T) moduleCap;
            if (capability == CapabilityEnergy.ENERGY) {
                energyStorage.updateFromNBT();
                return (T) energyStorage;
            }
            return null;
        }
    }

    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(CapabilityEnergy.ENERGY, null))
                .map( energyCap-> energyCap.getMaxEnergyStored() > 0).orElse(false);
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(CapabilityEnergy.ENERGY, null))
                .map( energyCap-> 1 - energyCap.getEnergyStored() / (double) energyCap.getMaxEnergyStored()).orElse(1D);
    }
}