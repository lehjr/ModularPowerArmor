package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.client.sound.SoundDictionary;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.basemod.MPALibConfig;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.control.PlayerMovementInputWrapper;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SwimAssistModule extends AbstractPowerModule {
    public SwimAssistModule(String regName) {
        super(regName);
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
            this.ticker = new Ticker(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.LEGSONLY, CommonConfig.moduleConfig);
            this.ticker.addTradeoffPropertyDouble(MPAConstants.THRUST, MPAConstants.ENERGY_CONSUMPTION, 1000, "RF");
            this.ticker.addTradeoffPropertyDouble(MPAConstants.THRUST, MPAConstants.SWIM_BOOST_AMOUNT, 1, "m/s");
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
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, false);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack item) {
                if (player.isInWater() && !(player.isPassenger())) {
                    PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
                    if (playerInput.moveForward != 0 || playerInput.moveStrafe != 0 || playerInput.jumpKey || playerInput.sneakKey) {
                        double moveRatio = 0;
                        if (playerInput.moveForward != 0) {
                            moveRatio += playerInput.moveForward * playerInput.moveForward;
                        }
                        if (playerInput.moveStrafe != 0) {
                            moveRatio += playerInput.moveStrafe * playerInput.moveStrafe;
                        }
                        if (playerInput.jumpKey || playerInput.sneakKey) {
                            moveRatio += 0.2 * 0.2;
                        }
                        double swimAssistRate = applyPropertyModifiers(MPAConstants.SWIM_BOOST_AMOUNT) * 0.05 * moveRatio;
                        double swimEnergyConsumption = applyPropertyModifiers(MPAConstants.ENERGY_CONSUMPTION);
                        if (swimEnergyConsumption < ElectricItemUtils.getPlayerEnergy(player)) {
                            if (player.world.isRemote && MPALibConfig.USE_SOUNDS.get()) {
                                Musique.playerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST, SoundCategory.PLAYERS, 1.0f, 1.0f, true);
                            }
                            MovementManager.thrust(player, swimAssistRate, true);
                        } else {
                            if (player.world.isRemote && MPALibConfig.USE_SOUNDS.get()) {
                                Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
                            }
                        }
                    } else {
                        if (player.world.isRemote && MPALibConfig.USE_SOUNDS.get()) {
                            Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
                        }
                    }
                } else {
                    if (player.world.isRemote && MPALibConfig.USE_SOUNDS.get()) {
                        Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, ItemStack item) {
                if (player.world.isRemote && MPALibConfig.USE_SOUNDS.get()) {
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
                }
            }
        }
    }
}