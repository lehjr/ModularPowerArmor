package net.machinemuse.powersuits.item.module.weapon;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
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
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_WEAPON, EnumModuleTarget.TOOLONLY, MPSConfig.INSTANCE);
            this.moduleCap.addBasePropertyDouble(MPSConstants.PUNCH_ENERGY, 10, "RF");
            this.moduleCap.addBasePropertyDouble(MPSConstants.PUNCH_DAMAGE, 2, "pt");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.IMPACT, MPSConstants.PUNCH_ENERGY, 1000, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.IMPACT, MPSConstants.PUNCH_DAMAGE, 8, "pt");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.CARRY_THROUGH, MPSConstants.PUNCH_ENERGY, 200, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.CARRY_THROUGH, MPSConstants.PUNCH_KNOCKBACK, 1, "P");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> moduleCap));
        }
    }
}