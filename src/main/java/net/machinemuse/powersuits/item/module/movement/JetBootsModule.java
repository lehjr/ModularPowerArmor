package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.basemod.NuminaConfig;
import net.machinemuse.numina.capabilities.inventory.modularitem.ModularItemCapability;
import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.capabilities.module.toggleable.IModuleToggle;
import net.machinemuse.numina.capabilities.module.toggleable.Toggle;
import net.machinemuse.numina.capabilities.module.toggleable.ToggleCapability;
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.numina.control.PlayerMovementInputWrapper;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.MPSItems;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.constants.MPSModuleConstants;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JetBootsModule extends AbstractPowerModule {
    ResourceLocation flightControl = new ResourceLocation(MPSItems.INSTANCE.MODULE_FLIGHT_CONTROL__REGNAME);
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
        IPowerModule moduleCap;
        IModuleTick ticker;
        IModuleToggle toggle;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_MOVEMENT, EnumModuleTarget.FEETONLY, MPSConfig.INSTANCE);

            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 0);
            this.moduleCap.addBasePropertyDouble(MPSConstants.JETBOOTS_THRUST, 0);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.ENERGY_CONSUMPTION, 750, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.THRUST, MPSConstants.JETBOOTS_THRUST, 0.08);

            this.toggle = new Toggle(module);
            this.ticker = new Ticker();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ModuleTickCapability.TICK)
                return ModuleTickCapability.TICK.orEmpty(cap, LazyOptional.of(() -> ticker));
            if (cap == ToggleCapability.TOGGLEABLE_MODULE)
                return ToggleCapability.TOGGLEABLE_MODULE.orEmpty(cap, LazyOptional.of(() -> toggle));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class Ticker extends ModuleTick {
            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack item) {
                if (player.isInWater())
                    return;

                ItemStack helmet = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
                boolean hasFlightControl = helmet.getCapability(ModularItemCapability.MODULAR_ITEM).map(m->m.isModuleOnline(flightControl)).orElse(false);

                double jetEnergy = moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);
                double thrust = moduleCap.applyPropertyModifiers(MPSConstants.JETBOOTS_THRUST);

                PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
                // if player has enough energy to fly
                if (jetEnergy < ElectricItemUtils.getPlayerEnergy(player)) {
                    if (hasFlightControl && thrust > 0) {
                        thrust = MovementManager.thrust(player, thrust, true);
                        if ((player.world.isRemote) && NuminaConfig.INSTANCE.USE_SOUNDS.get()) {
                            Musique.playerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS, SoundCategory.PLAYERS, (float) (thrust * 12.5), 1.0f, true);
                        }
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (thrust * jetEnergy));
                    } else if (playerInput.jumpKey && player.getMotion().y < 0.5) {
                        thrust = MovementManager.thrust(player, thrust, false);
                        if ((player.world.isRemote) && NuminaConfig.INSTANCE.USE_SOUNDS.get()) {
                            Musique.playerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS, SoundCategory.PLAYERS, (float) (thrust * 12.5), 1.0f, true);
                        }
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (thrust * jetEnergy));
                    } else {
                        if ((player.world.isRemote) && NuminaConfig.INSTANCE.USE_SOUNDS.get()) {
                            Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS);
                        }
                    }
                } else {
                    if (player.world.isRemote && NuminaConfig.INSTANCE.USE_SOUNDS.get()) {
                        Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS);
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, ItemStack item) {
                if (player.world.isRemote && NuminaConfig.INSTANCE.USE_SOUNDS.get()) {
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS);
                }
            }
        }
    }
}
