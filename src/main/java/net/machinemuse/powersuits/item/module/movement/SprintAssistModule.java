package net.machinemuse.powersuits.item.module.movement;

import net.machinemuse.numina.capabilities.IConfig;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleTarget;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.module.tickable.IPlayerTickModule;
import net.machinemuse.numina.capabilities.module.tickable.PlayerTickModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
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
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.MOVEMENT, EnumModuleTarget.LEGSONLY, CommonConfig.moduleConfig);

            this.ticker.addBasePropertyDouble(MPSConstants.SPRINT_ENERGY_CONSUMPTION, 0, "RF");
            this.ticker.addTradeoffPropertyDouble(MPSConstants.SPRINT_ASSIST, MPSConstants.SPRINT_ENERGY_CONSUMPTION, 100);
            this.ticker.addBasePropertyDouble(MPSConstants.SPRINT_SPEED_MULTIPLIER, .01, "%");
            this.ticker.addTradeoffPropertyDouble(MPSConstants.SPRINT_ASSIST, MPSConstants.SPRINT_SPEED_MULTIPLIER, 2.49);

            this.ticker.addBasePropertyDouble(MPSConstants.SPRINT_ENERGY_CONSUMPTION, 0, "RF");
            this.ticker.addTradeoffPropertyDouble(MPSConstants.COMPENSATION, MPSConstants.SPRINT_ENERGY_CONSUMPTION, 20);
            this.ticker.addBasePropertyDouble(MPSConstants.FOOD_COMPENSATION, 0, "%");
            this.ticker.addTradeoffPropertyDouble(MPSConstants.COMPENSATION, MPSConstants.FOOD_COMPENSATION, 1);

            this.ticker.addBasePropertyDouble(MPSConstants.WALKING_ENERGY_CONSUMPTION, 0, "RF");
            this.ticker.addTradeoffPropertyDouble(MPSConstants.WALKING_ASSISTANCE, MPSConstants.WALKING_ENERGY_CONSUMPTION, 100);
            this.ticker.addBasePropertyDouble(MPSConstants.WALKING_SPEED_MULTIPLIER, 0.01, "%");
            this.ticker.addTradeoffPropertyDouble(MPSConstants.WALKING_ASSISTANCE, MPSConstants.WALKING_SPEED_MULTIPLIER, 1.99);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IPlayerTickModule) {
                System.out.println("ismodule online: " + ticker.isAllowed());
                ticker.updateFromNBT();
                System.out.println("ismodule online (after update): " + ticker.isAllowed());
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> ticker));
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack itemStack) {
                if (player.abilities.isFlying || player.isPassenger() || player.isElytraFlying())
                    onPlayerTickInactive(player, itemStack);

                double horzMovement = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                double totalEnergy = ElectricItemUtils.getPlayerEnergy(player);
                if (horzMovement > 0) { // stop doing drain calculations when player hasn't moved
                    if (player.isSprinting()) {
                        double exhaustion = Math.round(horzMovement * 100.0F) * 0.01;
                        double sprintCost = applyPropertyModifiers(MPSConstants.SPRINT_ENERGY_CONSUMPTION);
                        if (sprintCost < totalEnergy) {
                            double sprintMultiplier = applyPropertyModifiers(MPSConstants.SPRINT_SPEED_MULTIPLIER);
                            double exhaustionComp = applyPropertyModifiers(MPSConstants.FOOD_COMPENSATION);
                            ElectricItemUtils.drainPlayerEnergy(player, (int) (sprintCost * horzMovement * 5));
                            MovementManager.setMovementModifier(itemStack, sprintMultiplier, player);
                            player.getFoodStats().addExhaustion((float) (-0.01 * exhaustion * exhaustionComp));
                            player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                        }
                    } else {
                        double cost = applyPropertyModifiers(MPSConstants.WALKING_ENERGY_CONSUMPTION);
                        if (cost < totalEnergy) {
                            double walkMultiplier = applyPropertyModifiers(MPSConstants.WALKING_SPEED_MULTIPLIER);
                            ElectricItemUtils.drainPlayerEnergy(player, (int) (cost * horzMovement * 5));
                            MovementManager.setMovementModifier(itemStack, walkMultiplier, player);
                            player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                        }
                    }
                }
            }

            @Override
            public void onPlayerTickInactive(PlayerEntity player, @Nonnull ItemStack itemStack) {
                MovementManager.setMovementModifier(itemStack, 0, player);
            }
        }
    }
}