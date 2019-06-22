package net.machinemuse.powersuits.item.module.weapon;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.rightclick.IRightClickModule;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickCapability;
import net.machinemuse.numina.capabilities.module.rightclick.RightClickModule;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.heat.MuseHeatUtils;
import net.machinemuse.numina.math.MuseMathUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.entity.PlasmaBoltEntity;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlasmaCannonModule extends AbstractPowerModule {
    public PlasmaCannonModule(String regName) {
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
        IRightClickModule rightClickie;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_WEAPON, EnumModuleTarget.TOOLONLY, MPSConfig.INSTANCE);
            this.moduleCap.addBasePropertyDouble(MPSConstants.PLASMA_CANNON_ENERGY_PER_TICK, 100, "RF");
            this.moduleCap.addBasePropertyDouble(MPSConstants.PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE, 2, "pt");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.AMPERAGE, MPSConstants.PLASMA_CANNON_ENERGY_PER_TICK, 1500, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.AMPERAGE, MPSConstants.PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE, 38, "pt");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.VOLTAGE, MPSConstants.PLASMA_CANNON_ENERGY_PER_TICK, 500, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.VOLTAGE, MPSConstants.PLASMA_CANNON_EXPLOSIVENESS, 0.5, MPSConstants.CREEPER);
            this.rightClickie = new RightClickie(module, moduleCap);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == RightClickCapability.RIGHT_CLICK)
                return RightClickCapability.RIGHT_CLICK.orEmpty(cap, LazyOptional.of(() -> rightClickie));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class RightClickie extends RightClickModule {
            ItemStack module;
            IPowerModule moduleCap;

            public RightClickie(@Nonnull ItemStack module, IPowerModule moduleCap) {
                this.module = module;
                this.moduleCap = moduleCap;
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                if (hand == Hand.MAIN_HAND && ElectricItemUtils.getPlayerEnergy(playerIn) > 500) {
                    playerIn.setActiveHand(hand);
                    return new ActionResult(ActionResultType.SUCCESS, itemStackIn);
                }
                return ActionResult.newResult(ActionResultType.PASS, itemStackIn);
            }

            @Override
            public void onPlayerStoppedUsing(ItemStack itemStack, World worldIn, LivingEntity entityLiving, int timeLeft) {
                int chargeTicks = (int) MuseMathUtils.clampDouble(itemStack.getUseDuration() - timeLeft, 10, 50);
                if (!worldIn.isRemote) {
                    double energyConsumption = getEnergyUsage()* chargeTicks;
                    if (entityLiving instanceof PlayerEntity) {
                        PlayerEntity player = (PlayerEntity) entityLiving;
                        MuseHeatUtils.heatPlayer(player, energyConsumption / 5000);
                        if (ElectricItemUtils.getPlayerEnergy(player) > energyConsumption) {
                            ElectricItemUtils.drainPlayerEnergy(player, (int) energyConsumption);
                            double explosiveness = moduleCap.applyPropertyModifiers(MPSConstants.PLASMA_CANNON_EXPLOSIVENESS);
                            double damagingness = moduleCap.applyPropertyModifiers(MPSConstants.PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE);

                            PlasmaBoltEntity plasmaBolt = new PlasmaBoltEntity(worldIn, player, explosiveness, damagingness, chargeTicks);
                            worldIn.addEntity(plasmaBolt);
                        }
                    }
                }
            }

            @Override
            public int getEnergyUsage() {
                return (int) Math.round(moduleCap.applyPropertyModifiers(MPSConstants.PLASMA_CANNON_ENERGY_PER_TICK));
            }
        }
    }
}