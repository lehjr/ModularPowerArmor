package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.basemod.NuminaConfig;
import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
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
import net.machinemuse.powersuits.basemod.MPSRegistryNames;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.event.MovementManager;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
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

public class JetBootsModule extends AbstractPowerModule {
    ResourceLocation flightControl = new ResourceLocation(MPSRegistryNames.MODULE_FLIGHT_CONTROL__REGNAME);
    public JetBootsModule(String regName) {
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
            this.ticker = new Ticker(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.FEETONLY, CommonConfig.moduleConfig);
            this.ticker.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 0);
            this.ticker.addBasePropertyDouble(MPSConstants.JETBOOTS_THRUST, 0);
            this.ticker.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.ENERGY_CONSUMPTION, 750, "RF");
            this.ticker.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.JETBOOTS_THRUST, 0.08);
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
                if (player.isInWater())
                    return;

                ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
                boolean hasFlightControl = helmet.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(m->
                        m instanceof IModularItem && ((IModularItem) m).isModuleOnline(flightControl)).orElse(false);

                double jetEnergy = applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
                double thrust = applyPropertyModifiers(MPSConstants.JETBOOTS_THRUST);

                PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
                // if player has enough energy to fly
                if (jetEnergy < ElectricItemUtils.getPlayerEnergy(player)) {
                    if (hasFlightControl && thrust > 0) {
                        thrust = MovementManager.thrust(player, thrust, true);
                        if ((player.world.isRemote) && NuminaConfig.USE_SOUNDS.get()) {
                            Musique.playerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS, SoundCategory.PLAYERS, (float) (thrust * 12.5), 1.0f, true);
                        }
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (thrust * jetEnergy));
                    } else if (playerInput.jumpKey && player.getMotion().y < 0.5) {
                        thrust = MovementManager.thrust(player, thrust, false);
                        if ((player.world.isRemote) && NuminaConfig.USE_SOUNDS.get()) {
                            Musique.playerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS, SoundCategory.PLAYERS, (float) (thrust * 12.5), 1.0f, true);
                        }
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (thrust * jetEnergy));
                    } else {
                        if ((player.world.isRemote) && NuminaConfig.USE_SOUNDS.get()) {
                            Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS);
                        }
                    }
                } else {
                    if (player.world.isRemote && NuminaConfig.USE_SOUNDS.get()) {
                        Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS);
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, ItemStack item) {
                if (player.world.isRemote && NuminaConfig.USE_SOUNDS.get()) {
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS);
                }
            }
        }
    }
}
