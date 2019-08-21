package net.machinemuse.powersuits.item.module.environmental;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.capabilities.module.toggleable.IModuleToggle;
import net.machinemuse.numina.capabilities.module.toggleable.ToggleCapability;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
    public ICapabilityProvider initCapabilities (ItemStack stack, @Nullable CompoundNBT nbt){
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;
        IModuleTick ticker;
        IModuleToggle toggle;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.ENVIRONMENTAL, EnumModuleTarget.TORSOONLY, CommonConfig.moduleConfig);
            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 2500, "RF");
            this.ticker = new Ticker(moduleCap);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ModuleTickCapability.TICK)
                return ModuleTickCapability.TICK.orEmpty(cap, LazyOptional.of(() -> ticker));
            if(cap == ToggleCapability.TOGGLEABLE_MODULE)
                return ToggleCapability.TOGGLEABLE_MODULE.orEmpty(cap, LazyOptional.of(() -> toggle));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class Ticker extends ModuleTick {
            IPowerModule moduleCap;

            public Ticker(IPowerModule moduleCapIn) {
                this.moduleCap = moduleCapIn;
            }

            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack item) {
                if (ElectricItemUtils.getPlayerEnergy(player) > moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION)) {
                    if (player.world.getGameTime() % 20 == 0) {
                        ElectricItemUtils.drainPlayerEnergy(player, (int) Math.round(moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION)));
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