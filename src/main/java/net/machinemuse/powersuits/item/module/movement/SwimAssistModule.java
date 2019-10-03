package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.basemod.NuminaConfig;
import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.tickable.IPlayerTickModule;
import net.machinemuse.numina.capabilities.module.tickable.PlayerTickModule;
import net.machinemuse.numina.capabilities.module.toggleable.IToggleableModule;
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.numina.control.PlayerMovementInputWrapper;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.event.MovementManager;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
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
            this.ticker.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.ENERGY_CONSUMPTION, 1000, "RF");
            this.ticker.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.SWIM_BOOST_AMOUNT, 1, "m/s");
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
                        double swimAssistRate = applyPropertyModifiers(MPSConstants.SWIM_BOOST_AMOUNT) * 0.05 * moveRatio;
                        double swimEnergyConsumption = applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
                        if (swimEnergyConsumption < ElectricItemUtils.getPlayerEnergy(player)) {
                            if (player.world.isRemote && NuminaConfig.USE_SOUNDS.get()) {
                                Musique.playerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST, SoundCategory.PLAYERS, 1.0f, 1.0f, true);
                            }
                            MovementManager.thrust(player, swimAssistRate, true);
                        } else {
                            if (player.world.isRemote && NuminaConfig.USE_SOUNDS.get()) {
                                Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
                            }
                        }
                    } else {
                        if (player.world.isRemote && NuminaConfig.USE_SOUNDS.get()) {
                            Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
                        }
                    }
                } else {
                    if (player.world.isRemote && NuminaConfig.USE_SOUNDS.get()) {
                        Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, ItemStack item) {
                if (player.world.isRemote && NuminaConfig.USE_SOUNDS.get()) {
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
                }
            }
        }
    }
}