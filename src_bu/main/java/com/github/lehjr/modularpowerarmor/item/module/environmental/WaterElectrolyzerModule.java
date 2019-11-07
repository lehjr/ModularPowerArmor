package com.github.lehjr.modularpowerarmor.item.module.environmental;

import com.github.lehjr.mpalib.basemod.MPALibConfig;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.modularpowerarmor.basemod.Constants;

import com.github.lehjr.modularpowerarmor.client.sound.SoundDictionary;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WaterElectrolyzerModule extends AbstractPowerModule {
    public WaterElectrolyzerModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities (ItemStack stack, @Nullable NBTTagCompound nbt){
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.ENVIRONMENTAL, EnumModuleTarget.HEADONLY, MPAConfig.moduleConfig);
            this.ticker.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 10000, "RF");
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
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, false);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack item) {
                int energy = ElectricItemUtils.getPlayerEnergy(player);
                int energyConsumption = (int) Math.round(applyPropertyModifiers(Constants.ENERGY_CONSUMPTION));
                if (energy > energyConsumption && player.getAir() < 10) {
                    if ((player.world.isRemote()) && MPALibConfig.useSounds()) {
                        Musique.playClientSound(SoundDictionary.SOUND_EVENT_ELECTROLYZER, 1.0f);
                    }
                    ElectricItemUtils.drainPlayerEnergy(player, energyConsumption);
                    player.setAir(300);
                }
            }
        }
    }
}