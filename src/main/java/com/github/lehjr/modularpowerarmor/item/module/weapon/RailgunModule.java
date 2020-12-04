package com.github.lehjr.modularpowerarmor.item.module.weapon;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.entity.RailgunBoltEntity;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.module.rightclick.IRightClickModule;
import com.github.lehjr.mpalib.util.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.util.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.util.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.util.heat.HeatUtils;
import com.github.lehjr.mpalib.util.math.MathUtils;
import com.github.lehjr.mpalib.util.nbt.NBTUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class RailgunModule extends AbstractPowerModule {
    public RailgunModule() {
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
            this.ticker = new Ticker(module, EnumModuleCategory.WEAPON, EnumModuleTarget.TOOLONLY, MPASettings::getModuleConfig);
            this.ticker.addBaseProperty(MPAConstants.RAILGUN_TOTAL_IMPULSE, 500, "Ns");
            this.ticker.addBaseProperty(MPAConstants.RAILGUN_ENERGY_COST, 5000, "FE");
            this.ticker.addBaseProperty(MPAConstants.RAILGUN_HEAT_EMISSION, 2, "");
            this.ticker.addTradeoffProperty(MPAConstants.VOLTAGE, MPAConstants.RAILGUN_TOTAL_IMPULSE, 2500);
            this.ticker.addTradeoffProperty(MPAConstants.VOLTAGE, MPAConstants.RAILGUN_ENERGY_COST, 25000);
            this.ticker.addTradeoffProperty(MPAConstants.VOLTAGE, MPAConstants.RAILGUN_HEAT_EMISSION, 10);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> ticker));
        }

        class Ticker extends PlayerTickModule implements IRightClickModule {

            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, Callable<IConfig> config) {
                super(module, category, target, config, true);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack itemStackIn) {
                double timer = NBTUtils.getModularItemDoubleOrZero(itemStackIn, MPAConstants.TIMER);
                if (timer > 0) {
                    NBTUtils.setModularItemDoubleOrRemove(itemStackIn, MPAConstants.TIMER, timer - 1 > 0 ? timer - 1 : 0);
                }
            }

            @Override
            public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, PlayerEntity playerIn, Hand hand) {
                if (hand == Hand.MAIN_HAND && ElectricItemUtils.getPlayerEnergy(playerIn) > getEnergyUsage()) {
                    playerIn.setActiveHand(hand);
                    return ActionResult.resultSuccess(itemStackIn);
                }
                return ActionResult.resultPass(itemStackIn);
            }


            public void onPlayerStoppedUsing1(ItemStack itemStack, World worldIn, LivingEntity playerIn, int timeLeft) {
                int chargeTicks = (int) MathUtils.clampDouble(itemStack.getUseDuration() - timeLeft, 10, 50);
                float chargePercent = chargeTicks * 0.02F; // chargeticks/50
                float energyConsumption = getEnergyUsage() * chargePercent;
                double timer = NBTUtils.getModularItemDoubleOrZero(itemStack, MPAConstants.TIMER);
                System.out.println("timer: " + timer);

                System.out.println("chargePercent: " + chargePercent);


                if (!worldIn.isRemote && ElectricItemUtils.getPlayerEnergy(playerIn) > energyConsumption && timer == 0) {
                    NBTUtils.setModularItemDoubleOrRemove(itemStack, MPAConstants.TIMER, 10);
                    double velocity = applyPropertyModifiers(MPAConstants.RAILGUN_TOTAL_IMPULSE) * chargePercent;

                    RailgunBoltEntity bolt = new RailgunBoltEntity(worldIn, playerIn);

                    Vector3d lookVec = playerIn.getLookVec();

                    System.out.println("velocity: " + velocity);

                    double inaccuracy = 0.025F * chargePercent; // fixme: inaccuracy should increase with power output


                    bolt.shoot(lookVec, velocity, inaccuracy);
                    if (worldIn.addEntity(bolt)) {
                        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        ElectricItemUtils.drainPlayerEnergy(playerIn, (int) energyConsumption);
                        HeatUtils.heatPlayer(playerIn, applyPropertyModifiers(MPAConstants.RAILGUN_HEAT_EMISSION));
                        double knockback = velocity * 0.01 * 0.02;
                        playerIn.addVelocity(-lookVec.x * knockback, Math.abs(-lookVec.y + 0.2f) * knockback, -lookVec.z * knockback);
//                        playerIn.applyKnockback();
                    } else {
                        System.out.println("bolt not added");
                    }
                }
            }

            @Override
            // from bow, since bow launches correctly each time
            public void onPlayerStoppedUsing(ItemStack itemStack, World worldIn, LivingEntity entityLiving, int timeLeft) {
                int chargeTicks = (int) MathUtils.clampDouble(itemStack.getUseDuration() - timeLeft, 10, 50);
                double chargePercent = chargeTicks * 0.02; // chargeticks/50
                double energyConsumption = getEnergyUsage() * chargePercent;
                double timer = NBTUtils.getModularItemDoubleOrZero(itemStack, MPAConstants.TIMER);
                System.out.println("timer: " + timer);

                System.out.println("chargePercent: " + chargePercent);


                if (!worldIn.isRemote && ElectricItemUtils.getPlayerEnergy(entityLiving) > energyConsumption && timer == 0) {
                    NBTUtils.setModularItemDoubleOrRemove(itemStack, MPAConstants.TIMER, 10);
                    double velocity = applyPropertyModifiers(MPAConstants.RAILGUN_TOTAL_IMPULSE) * chargePercent;





                    PlayerEntity playerentity = (PlayerEntity)entityLiving;



                    RailgunBoltEntity bolt = new RailgunBoltEntity(worldIn, entityLiving);
                    bolt.func_234612_a_(
                            playerentity, // shooter
                            playerentity.rotationPitch, // pitch
                            playerentity.rotationYaw, // yaw
                            0.0F,  // ??
                            (float) velocity, // velocity
                            (float) (chargePercent * 0.25F) * 0); // inaccuracy
                    if (chargePercent >= 1.0) {
                        bolt.setIsCritical(true);
                    }

                    int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, itemStack);
                    if (j > 0) {
                        bolt.setDamage(bolt.getDamage() + (double)j * 0.5D + 0.5D);
                    }

                    int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, itemStack);
                    if (k > 0) {
                        bolt.setKnockbackStrength(k);
                    }

                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, itemStack) > 0) {
                        bolt.setFire(100);
                    }
//
//                    worldIn.addEntity(bolt);
//                    worldIn.playSound(null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
//
//
//

                    if (worldIn.addEntity(bolt)) {
                        Vector3d lookVec = playerentity.getLookVec();

                        worldIn.playSound(null, entityLiving.getPosX(), entityLiving.getPosY(), entityLiving.getPosZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        ElectricItemUtils.drainPlayerEnergy(entityLiving, (int) energyConsumption);
                        HeatUtils.heatPlayer(entityLiving, applyPropertyModifiers(MPAConstants.RAILGUN_HEAT_EMISSION) * chargePercent);
                        double knockback = velocity * 0.01 * 0.02;
                        entityLiving.addVelocity(-lookVec.x * knockback, Math.abs(-lookVec.y + 0.2f) * knockback, -lookVec.z * knockback);
//                        playerIn.applyKnockback();
                    } else {
                        System.out.println("bolt not added");
                    }

                }


//                            worldIn.playSound(null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);
//                            playerentity.addStat(Stats.ITEM_USED.get(this));

            }


            /**
             * Gets the velocity of the arrow entity from the bow's charge
             *
             * Looks like it's capped at 1.0F
             */
            public float getArrowVelocity(int charge) {
                float f = (float)charge / 20.0F;
                f = (f * f + f * 2.0F) / 3.0F;
                if (f > 1.0F) {
                    f = 1.0F;
                }

                return f;
            }

            /**
             * How long it takes to use or consume an item
             */
            public int getUseDuration(ItemStack stack) {
                return 72000;
            }

            public AbstractArrowEntity customArrow(AbstractArrowEntity arrow) {
                return arrow;
            }



            @Override
            public int getEnergyUsage() {
                return (int) Math.round(applyPropertyModifiers(MPAConstants.RAILGUN_ENERGY_COST));
            }
        }
    }
}