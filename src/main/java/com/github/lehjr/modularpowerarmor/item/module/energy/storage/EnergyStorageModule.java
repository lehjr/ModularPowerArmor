package com.github.lehjr.modularpowerarmor.item.module.energy.storage;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.energy.ForgeEnergyModuleWrapper;
import com.github.lehjr.mpalib.capabilities.energy.IEnergyWrapper;
import com.github.lehjr.mpalib.capabilities.module.powermodule.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;
        IEnergyWrapper energyStorage;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.ENERGY_STORAGE, EnumModuleTarget.ALLITEMS, CommonConfigX.moduleConfig);
            this.moduleCap.addBasePropertyInteger(MPAConstants.MAX_ENERGY, maxEnergy, "RF");
            this.moduleCap.addBasePropertyInteger(MPAConstants.MAX_TRAMSFER, maxTransfer, "RF");
            this.energyStorage = new ForgeEnergyModuleWrapper(
                    module,
                    moduleCap.applyPropertyModifierBaseInt(MPAConstants.MAX_ENERGY),
                    moduleCap.applyPropertyModifierBaseInt(MPAConstants.MAX_TRAMSFER)
            );
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == PowerModuleCapability.POWER_MODULE) {
                return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
            }
            if (cap == CapabilityEnergy.ENERGY) {
                energyStorage.updateFromNBT();
            }
            return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(()-> energyStorage));
        }
    }

    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> energyCap.getMaxEnergyStored() > 0).orElse(false);
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> 1 - energyCap.getEnergyStored() / (double) energyCap.getMaxEnergyStored()).orElse(1D);
    }
}