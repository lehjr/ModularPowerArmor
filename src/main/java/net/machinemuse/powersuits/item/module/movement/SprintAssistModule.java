package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.capabilities.module.toggleable.IModuleToggle;
import net.machinemuse.numina.capabilities.module.toggleable.Toggle;
import net.machinemuse.numina.capabilities.module.toggleable.ToggleCapability;
import net.machinemuse.numina.energy.ElectricItemUtils;
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

/**
 * Ported by leon on 10/18/16.
 */
public class SprintAssistModule extends AbstractPowerModule {
    public SprintAssistModule(String regName) {
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

            this.moduleCap.addBasePropertyDouble(MPSConstants.SPRINT_ENERGY_CONSUMPTION, 0, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.SPRINT_ASSIST, MPSConstants.SPRINT_ENERGY_CONSUMPTION, 100);
            this.moduleCap.addBasePropertyDouble(MPSConstants.SPRINT_SPEED_MULTIPLIER, .01, "%");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.SPRINT_ASSIST, MPSConstants.SPRINT_SPEED_MULTIPLIER, 2.49);

            this.moduleCap.addBasePropertyDouble(MPSConstants.SPRINT_ENERGY_CONSUMPTION, 0, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.COMPENSATION, MPSConstants.SPRINT_ENERGY_CONSUMPTION, 20);
            this.moduleCap.addBasePropertyDouble(MPSConstants.SPRINT_FOOD_COMPENSATION, 0, "%");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.COMPENSATION, MPSConstants.SPRINT_FOOD_COMPENSATION, 1);

            this.moduleCap.addBasePropertyDouble(MPSConstants.WALKING_ENERGY_CONSUMPTION, 0, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.WALKING_ASSISTANCE, MPSConstants.WALKING_ENERGY_CONSUMPTION, 100);
            this.moduleCap.addBasePropertyDouble(MPSConstants.WALKING_SPEED_MULTIPLIER, 0.01, "%");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.WALKING_ASSISTANCE, MPSConstants.WALKING_SPEED_MULTIPLIER, 1.99);

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
            public void onPlayerTickActive(PlayerEntity player, ItemStack itemStack) {
                if (player.abilities.isFlying || player.isPassenger() || player.isElytraFlying())
                    onPlayerTickInactive(player, itemStack);

                double horzMovement = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                double totalEnergy = ElectricItemUtils.getPlayerEnergy(player);
                if (horzMovement > 0) { // stop doing drain calculations when player hasn't moved
                    if (player.isSprinting()) {
                        double exhaustion = Math.round(horzMovement * 100.0F) * 0.01;
                        double sprintCost = moduleCap.applyPropertyModifiers(MPSConstants.SPRINT_ENERGY_CONSUMPTION);
                        if (sprintCost < totalEnergy) {
                            double sprintMultiplier = moduleCap.applyPropertyModifiers(MPSConstants.SPRINT_SPEED_MULTIPLIER);
                            double exhaustionComp = moduleCap.applyPropertyModifiers(MPSConstants.SPRINT_FOOD_COMPENSATION);
                            ElectricItemUtils.drainPlayerEnergy(player, (int) (sprintCost * horzMovement * 5));
                            MovementManager.setMovementModifier(itemStack, sprintMultiplier, player);
                            player.getFoodStats().addExhaustion((float) (-0.01 * exhaustion * exhaustionComp));
                            player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                        }
                    } else {
                        double cost = moduleCap.applyPropertyModifiers(MPSConstants.WALKING_ENERGY_CONSUMPTION);
                        if (cost < totalEnergy) {
                            double walkMultiplier = moduleCap.applyPropertyModifiers(MPSConstants.WALKING_SPEED_MULTIPLIER);
                            ElectricItemUtils.drainPlayerEnergy(player, (int) (cost * horzMovement * 5));
                            MovementManager.setMovementModifier(itemStack, walkMultiplier, player);
                            player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                        }
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, ItemStack itemStack) {
                MovementManager.setMovementModifier(itemStack, 0, player);
            }
        }
    }
}