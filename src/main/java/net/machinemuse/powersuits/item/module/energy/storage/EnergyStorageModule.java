package net.machinemuse.powersuits.item.module.energy.storage;

import net.machinemuse.numina.capabilities.energy.ForgeEnergyModuleWrapper;
import net.machinemuse.numina.capabilities.energy.IEnergyWrapper;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

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
            this.moduleCap = new PowerModule(module, EnumModuleCategory.ENERGY_STORAGE, EnumModuleTarget.ALLITEMS, CommonConfig.moduleConfig);
            this.moduleCap.addBasePropertyInteger(MPSConstants.MAX_ENERGY, maxEnergy, "RF");
            this.moduleCap.addBasePropertyInteger(MPSConstants.MAX_TRAMSFER, maxTransfer, "RF");
            this.energyStorage = new ForgeEnergyModuleWrapper(
                    module,
                    moduleCap.applyPropertyModifierBaseInt(MPSConstants.MAX_ENERGY),
                    moduleCap.applyPropertyModifierBaseInt(MPSConstants.MAX_TRAMSFER)
            );
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == PowerModuleCapability.POWER_MODULE)
                return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> moduleCap));
            if (cap == CapabilityEnergy.ENERGY) {
                energyStorage.updateFromNBT();
            }
            return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(()-> energyStorage));
        }
    }

    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> 1 - energyCap.getEnergyStored() / (double) energyCap.getMaxEnergyStored()).orElse(1D);
    }
}