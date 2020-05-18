package com.github.lehjr.modularpowerarmor.item.module.energy.generation;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * Created by User: Andrew2448
 * 6:43 PM 4/23/13
 */
public class ThermalGeneratorModule extends AbstractPowerModule {
    public ThermalGeneratorModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities (ItemStack stack, @Nullable CompoundNBT nbt){
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.ENERGY_GENERATION, EnumModuleTarget.TORSOONLY, MPASettings.getModuleConfig());
            this.ticker.addBasePropertyDouble(MPAConstants.ENERGY_GENERATION, 250);
            this.ticker.addTradeoffPropertyDouble(MPAConstants.ENERGY_GENERATED, MPAConstants.ENERGY_GENERATION, 250, "RF");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IToggleableModule) {
                ((IToggleableModule) cap).updateFromNBT();
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> ticker));
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack item) {
                double currentHeat = HeatUtils.getPlayerHeat(player);
                double maxHeat = HeatUtils.getPlayerMaxHeat(player);
                if (player.world.getGameTime() % 20 == 0) {
                    if (player.isBurning()) {
                        ElectricItemUtils.givePlayerEnergy(player, (int) (4 * applyPropertyModifiers(MPAConstants.ENERGY_GENERATION)));
                    } else if (currentHeat >= 200) {
                        ElectricItemUtils.givePlayerEnergy(player, (int) (2 * applyPropertyModifiers(MPAConstants.ENERGY_GENERATION)));
                    } else if ((currentHeat / maxHeat) >= 0.5) {
                        ElectricItemUtils.givePlayerEnergy(player, (int) applyPropertyModifiers(MPAConstants.ENERGY_GENERATION));
                    }
                }
            }
        }
    }
}