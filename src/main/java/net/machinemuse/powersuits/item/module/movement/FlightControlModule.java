package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.toggleable.IModuleToggle;
import net.machinemuse.numina.capabilities.module.toggleable.Toggle;
import net.machinemuse.numina.capabilities.module.toggleable.ToggleCapability;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FlightControlModule extends AbstractPowerModule {
    public FlightControlModule(String regName) {
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
        IModuleToggle moduleToggle;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.HEADONLY, CommonConfig.moduleConfig);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.VERTICALITY, MPSConstants.FLIGHT_VERTICALITY, 1.0, "%");
            this.moduleToggle = new Toggle(module);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ToggleCapability.TOGGLEABLE_MODULE)
                return ToggleCapability.TOGGLEABLE_MODULE.orEmpty(cap, LazyOptional.of(()-> moduleToggle));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> moduleCap));
        }
    }
}