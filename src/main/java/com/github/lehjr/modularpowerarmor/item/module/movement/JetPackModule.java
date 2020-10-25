package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.client.sound.MPASoundDictionary;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.util.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.util.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.util.capabilities.player.CapabilityPlayerKeyStates;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.config.MPALibSettings;
import com.github.lehjr.mpalib.control.PlayerMovementInputWrapper;
import com.github.lehjr.mpalib.util.energy.ElectricItemUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class JetPackModule extends AbstractPowerModule {
    ResourceLocation flightControl = new ResourceLocation(MPAConstants.MOD_ID, MPARegistryNames.FLIGHT_CONTROL_MODULE);

    public JetPackModule() {
        super();
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.TORSOONLY, MPASettings::getModuleConfig);

            this.ticker.addBaseProperty(MPAConstants.ENERGY_CONSUMPTION, 0, "RF/t");
            this.ticker.addBaseProperty(MPAConstants.JETPACK_THRUST, 0, "N");
            this.ticker.addTradeoffProperty(MPAConstants.THRUST, MPAConstants.ENERGY_CONSUMPTION, 1500);
            this.ticker.addTradeoffProperty(MPAConstants.THRUST, MPAConstants.JETPACK_THRUST, 0.16F);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IToggleableModule) {
                ((IToggleableModule) cap).updateFromNBT();
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> ticker));
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config, false);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack torso) {
                if (player.isInWater()) {
                    return;
                }

                PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);

                ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
                boolean hasFlightControl = helmet.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(m->
                        m instanceof IModularItem && ((IModularItem) m).isModuleOnline(flightControl)).orElse(false);
                double jetEnergy = 0;
                double thrust = 0;
                jetEnergy += applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION);
                thrust += applyPropertyModifiers(MPAConstants.JETPACK_THRUST);

                if (jetEnergy < ElectricItemUtils.getPlayerEnergy(player)) {
                    if (hasFlightControl && thrust > 0) {
                        thrust = MovementManager.INSTANCE.thrust(player, thrust, true);
                        if (player.world.isRemote && MPALibSettings.useSounds()) {
                            Musique.playerSound(player, MPASoundDictionary.JETPACK, SoundCategory.PLAYERS, (float) (thrust * 6.25), 1.0f, true);
                        }
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (thrust * jetEnergy));
                    } else if (playerInput.jumpKey) {//&& player.motionY < 0.5) {
                        thrust = MovementManager.INSTANCE.thrust(player, thrust, false);
                        if (player.world.isRemote && MPALibSettings.useSounds()) {
                            Musique.playerSound(player, MPASoundDictionary.JETPACK, SoundCategory.PLAYERS, (float) (thrust * 6.25), 1.0f, true);
                        }
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (thrust * jetEnergy));
                    } else {
                        if (player.world.isRemote && MPALibSettings.useSounds()) {
                            Musique.stopPlayerSound(player, MPASoundDictionary.JETPACK);
                        }
                    }
                } else {
                    if (player.world.isRemote && MPALibSettings.useSounds()) {
                        Musique.stopPlayerSound(player, MPASoundDictionary.JETPACK);
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, ItemStack item) {
                if (player.world.isRemote && MPALibSettings.useSounds()) {
                    Musique.stopPlayerSound(player, MPASoundDictionary.JETPACK);
                }
            }
        }
    }
}