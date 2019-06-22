package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.capabilities.module.toggleable.IModuleToggle;
import net.machinemuse.numina.capabilities.module.toggleable.Toggle;
import net.machinemuse.numina.capabilities.module.toggleable.ToggleCapability;
import net.machinemuse.numina.control.PlayerMovementInputWrapper;
import net.machinemuse.numina.player.NuminaPlayerUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.event.MovementManager;
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

public class JumpAssistModule extends AbstractPowerModule {
    public JumpAssistModule(String regName) {
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
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_MOVEMENT, EnumModuleTarget.LEGSONLY, MPSConfig.INSTANCE);

            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 0, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.POWER, MPSConstants.ENERGY_CONSUMPTION, 250);
            this.moduleCap.addBasePropertyDouble(MPSConstants.MULTIPLIER, 1, "%");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.POWER, MPSConstants.MULTIPLIER, 4);
//
            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 0, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.COMPENSATION, MPSConstants.ENERGY_CONSUMPTION, 50);
            this.moduleCap.addBasePropertyDouble(MPSConstants.FOOD_COMPENSATION, 0, "%");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.COMPENSATION, MPSConstants.FOOD_COMPENSATION, 1);

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
                PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
                if (playerInput.jumpKey) {
                    double multiplier = MovementManager.getPlayerJumpMultiplier(player);
                    if (multiplier > 0) {
                        player.setMotion(player.getMotion().add(0, 0.15 * Math.min(multiplier, 1), 0));
                        MovementManager.setPlayerJumpTicks(player, multiplier - 1);
                    }
                    player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                } else {
                    MovementManager.setPlayerJumpTicks(player, 0);
                }
                NuminaPlayerUtils.resetFloatKickTicks(player);
            }
        }
    }
}