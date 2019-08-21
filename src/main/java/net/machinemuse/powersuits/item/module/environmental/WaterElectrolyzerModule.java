package net.machinemuse.powersuits.item.module.environmental;

import net.machinemuse.numina.basemod.NuminaConfig;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
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

public class WaterElectrolyzerModule extends AbstractPowerModule {
    public WaterElectrolyzerModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities (ItemStack stack, @Nullable CompoundNBT nbt){
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;
        IModuleTick ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.ENVIRONMENTAL, EnumModuleTarget.HEADONLY, CommonConfig.moduleConfig);
            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 10000, "RF");
            this.ticker = new Ticker(moduleCap);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ModuleTickCapability.TICK)
                return ModuleTickCapability.TICK.orEmpty(cap, LazyOptional.of(() -> ticker));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class Ticker extends ModuleTick {
            IPowerModule moduleCap;

            public Ticker(IPowerModule moduleCapIn) {
                this.moduleCap = moduleCapIn;
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack item) {
                int energy = ElectricItemUtils.getPlayerEnergy(player);
                int energyConsumption = (int) Math.round(moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION));
                if (energy > energyConsumption && player.getAir() < 10) {
                    if ((player.world.isRemote()) && NuminaConfig.USE_SOUNDS.get()) {
                        Musique.playClientSound(SoundDictionary.SOUND_EVENT_ELECTROLYZER, 1.0f);
                    }
                    ElectricItemUtils.drainPlayerEnergy(player, energyConsumption);
                    player.setAir(300);
                }
            }
        }
    }
}