package com.github.lehjr.modularpowerarmor.item.module.weapon;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.entity.PlasmaBoltEntity;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.capabilities.module.rightclick.RightClickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.math.MathUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlasmaCannonModule extends AbstractPowerModule {
    public PlasmaCannonModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IRightClickModule rightClickie;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.rightClickie = new RightClickie(module, EnumModuleCategory.WEAPON, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
            this.rightClickie.addBasePropertyDouble(Constants.PLASMA_CANNON_ENERGY_PER_TICK, 100, "RF");
            this.rightClickie.addBasePropertyDouble(Constants.PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE, 2, "pt");
            this.rightClickie.addTradeoffPropertyDouble(Constants.AMPERAGE, Constants.PLASMA_CANNON_ENERGY_PER_TICK, 1500, "RF");
            this.rightClickie.addTradeoffPropertyDouble(Constants.AMPERAGE, Constants.PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE, 38, "pt");
            this.rightClickie.addTradeoffPropertyDouble(Constants.VOLTAGE, Constants.PLASMA_CANNON_ENERGY_PER_TICK, 500, "RF");
            this.rightClickie.addTradeoffPropertyDouble(Constants.VOLTAGE, Constants.PLASMA_CANNON_EXPLOSIVENESS, 0.5, Constants.CREEPER);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) rightClickie;
            }
            return null;
        }

        class RightClickie extends RightClickModule {
            public RightClickie(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config);
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
                System.out.println("doing somethign here");

                if (hand == EnumHand.MAIN_HAND && ElectricItemUtils.getPlayerEnergy(playerIn) > getEnergyUsage()) {
                    System.out.println("doing somethign here");

                    playerIn.setActiveHand(hand);
                    return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
                }
                System.out.println("doing somethign here");
                return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(hand));
            }

            @Override
            public void onPlayerStoppedUsing(ItemStack itemStack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
                int chargeTicks = (int) MathUtils.clampDouble(itemStack.getMaxItemUseDuration() - timeLeft, 10, 50);
                System.out.println("time left: " + timeLeft);


                if (!worldIn.isRemote) {
                    double energyConsumption = getEnergyUsage()* chargeTicks;
                    if (entityLiving instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) entityLiving;
                        HeatUtils.heatPlayer(player, energyConsumption / 5000);
                        if (ElectricItemUtils.getPlayerEnergy(player) > energyConsumption) {
                            ElectricItemUtils.drainPlayerEnergy(player, (int) energyConsumption);
                            double explosiveness = rightClickie.applyPropertyModifiers(Constants.PLASMA_CANNON_EXPLOSIVENESS);
                            double damagingness = rightClickie.applyPropertyModifiers(Constants.PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE);

                            PlasmaBoltEntity plasmaBolt = new PlasmaBoltEntity(worldIn, player, explosiveness, damagingness, chargeTicks);
                            worldIn.spawnEntity(plasmaBolt);
                        }
                    }
                }
            }

            @Override
            public int getEnergyUsage() {
                return (int) Math.round(rightClickie.applyPropertyModifiers(Constants.PLASMA_CANNON_ENERGY_PER_TICK));
            }
        }
    }
}