package com.github.lehjr.modularpowerarmor.item.module.environmental;

import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.modularpowerarmor.basemod.Constants;

import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by User: Andrew2448
 * 8:26 PM 4/25/13
 */
public class MobRepulsorModule extends AbstractPowerModule {
    public MobRepulsorModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities (ItemStack stack, @Nullable NBTTagCompound nbt){
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.ENVIRONMENTAL, EnumModuleTarget.TORSOONLY, MPAConfig.moduleConfig);
            this.ticker.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 2500, "RF");
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap instanceof IToggleableModule) {
                ((IToggleableModule) cap).updateFromNBT();
            }
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(()-> ticker));
        }

        class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, false);
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, @Nonnull ItemStack item) {
                int energyConsumption = (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
                if (ElectricItemUtils.getPlayerEnergy(player) > energyConsumption) {
                    if (player.world.getGameTime() % 20 == 0) {
                        ElectricItemUtils.drainPlayerEnergy(player, energyConsumption);
                    }
                    repulse(player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
                }
            }

            public void repulse(World world, int i, int j, int k) {
                float distance = 5.0F;
                Entity entity;
                Iterator iterator;
                List list = world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
                for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
                    entity = (Entity) iterator.next();
                }
                list = world.getEntitiesWithinAABB(ArrowEntity.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
                for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
                    entity = (Entity) iterator.next();
                }
                list = world.getEntitiesWithinAABB(FireballEntity.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
                for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
                    entity = (Entity) iterator.next();
                }
                list = world.getEntitiesWithinAABB(PotionEntity.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
                for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
                    entity = (Entity) iterator.next();
                }
            }

            private void push(Entity entity, int i, int j, int k) {
                if (!(entity instanceof PlayerEntity) && !(entity instanceof EnderDragonEntity)) {
                    double d = i - entity.posX;
                    double d1 = j - entity.posY;
                    double d2 = k - entity.posZ;
                    double d4 = d * d + d1 * d1 + d2 * d2;
                    d4 *= d4;
                    if (d4 <= Math.pow(6.0D, 4.0D)) {
                        double d5 = -(d * 0.01999999955296516D / d4) * Math.pow(6.0D, 3.0D);
                        double d6 = -(d1 * 0.01999999955296516D / d4) * Math.pow(6.0D, 3.0D);
                        double d7 = -(d2 * 0.01999999955296516D / d4) * Math.pow(6.0D, 3.0D);
                        if (d5 > 0.0D) {
                            d5 = 0.22D;
                        } else if (d5 < 0.0D) {
                            d5 = -0.22D;
                        }
                        if (d6 > 0.2D) {
                            d6 = 0.12D;
                        } else if (d6 < -0.1D) {
                            d6 = 0.12D;
                        }
                        if (d7 > 0.0D) {
                            d7 = 0.22D;
                        } else if (d7 < 0.0D) {
                            d7 = -0.22D;
                        }
                        Vec3d motion = entity.getMotion();
                        entity.setMotion(motion.x + d5, motion.y + d6, motion.z + d7);
                    }
                }
            }
        }
    }
}