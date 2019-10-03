package net.machinemuse.powersuits.item.module.armor;

import net.machinemuse.numina.basemod.NuminaConstants;
import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.tickable.IPlayerTickModule;
import net.machinemuse.numina.capabilities.module.tickable.PlayerTickModule;
import net.machinemuse.numina.capabilities.module.toggleable.IToggleableModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyShieldModule extends AbstractPowerModule {
    public EnergyShieldModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            if (CommonConfig.moduleConfig != null) {
                ticker = new Ticker(module, EnumModuleCategory.ARMOR, EnumModuleTarget.ARMORONLY, CommonConfig.moduleConfig, true);
                ticker.addTradeoffPropertyDouble(MPSConstants.MODULE_FIELD_STRENGTH, MPSConstants.ARMOR_VALUE_ENERGY, 6, NuminaConstants.MODULE_TRADEOFF_PREFIX + MPSConstants.ARMOR_POINTS);
                ticker.addTradeoffPropertyDouble(MPSConstants.MODULE_FIELD_STRENGTH, MPSConstants.ARMOR_ENERGY_CONSUMPTION, 5000, "RF");
                ticker.addTradeoffPropertyDouble(MPSConstants.MODULE_FIELD_STRENGTH, MPSConstants.MAXIMUM_HEAT, 500, "");
                ticker.addBasePropertyDouble(MPSConstants.KNOCKBACK_RESISTANCE, 0.25, "");
            }
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IToggleableModule) {
                ((IToggleableModule) cap).updateFromNBT();
            }
            if (ticker == null) {
                return LazyOptional.empty();
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> ticker));
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config, boolean defBool) {
                super(module, category, target, config, defBool);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack item) {
                int energy = ElectricItemUtils.getPlayerEnergy(player);
                int energyUsage = (int) applyPropertyModifiers(MPSConstants.ARMOR_ENERGY_CONSUMPTION);

                // turn off module if energy is too low. This will fire on both sides so no need to sync
                if (energy < energyUsage) {
                    this.toggleModule(false);
                }
            }
        }
    }
}