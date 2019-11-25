package com.github.lehjr.modularpowerarmor.item.module.special;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.IConfig;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.module.tickable.IPlayerTickModule;
import com.github.lehjr.mpalib.capabilities.module.tickable.PlayerTickModule;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MagnetModule extends AbstractPowerModule {
    public MagnetModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities (ItemStack stack, @Nullable NBTTagCompound nbt){
        return new CapProvider(stack);
    }

    public static class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IPlayerTickModule ticker;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.ticker = new Ticker(module, EnumModuleCategory.SPECIAL, EnumModuleTarget.TORSOONLY, MPAConfig.moduleConfig);
            this.ticker.addBasePropertyDouble(Constants.ENERGY_CONSUMPTION, 0, "RF");
            this.ticker.addTradeoffPropertyDouble(Constants.POWER, Constants.ENERGY_CONSUMPTION, 2000);
            this.ticker.addBasePropertyDouble(Constants.RADIUS, 5);
            this.ticker.addTradeoffPropertyDouble(Constants.POWER, Constants.RADIUS, 10);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
               ticker.updateFromNBT();
               return (T) ticker;
            }
            return null;
        }

        static class Ticker extends PlayerTickModule {
            public Ticker(@Nonnull ItemStack module, EnumModuleCategory category, EnumModuleTarget target, IConfig config) {
                super(module, category, target, config, false);
            }

            @Override
            public void onPlayerTickActive(EntityPlayer player, ItemStack stack) {
                int energyUSage = (int) applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);

                if (ElectricItemUtils.getPlayerEnergy(player) > energyUSage) {
                    boolean isServerSide = !player.world.isRemote;

                    if ((player.world.getTotalWorldTime() % 20) == 0 && isServerSide) {
                        ElectricItemUtils.drainPlayerEnergy(player, energyUSage);
                    }
                    int range = (int) applyPropertyModifiers(Constants.RADIUS);
                    World world = player.world;
                    AxisAlignedBB bounds = player.getEntityBoundingBox().grow(range);


                    if (isServerSide) {
                        bounds.expand(0.2000000029802322D, 0.2000000029802322D, 0.2000000029802322D);
                        if (stack.getItemDamage() >> 1 >= 7) {
                            List<EntityArrow> arrows = world.getEntitiesWithinAABB(EntityArrow.class, bounds);
                            for (EntityArrow arrow : arrows) {
                                if ((arrow.pickupStatus == EntityArrow.PickupStatus.ALLOWED) && (world.rand.nextInt(6) == 0)) {
                                    EntityItem replacement = new EntityItem(world, arrow.posX, arrow.posY, arrow.posZ, new ItemStack(Items.ARROW));
                                    world.spawnEntity(replacement);
                                }
                                world.removeEntity(arrow);
                            }
                        }
                    }

                    for (EntityItem e : world.getEntitiesWithinAABB(EntityItem.class, bounds)) {
                        if (!e.isDead && !e.getItem().isEmpty() && !e.cannotPickup()) {
                            if (isServerSide) {
                                double x = player.posX - e.posX;
                                double y = player.posY - e.posY;
                                double z = player.posZ - e.posZ;

                                double length = Math.sqrt(x * x + y * y + z * z) * 0.75D;

                                x = x / length + player.motionX * 22.0D;
                                y = y / length + player.motionY / 22.0D;
                                z = z / length + player.motionZ * 22.0D;

                                e.motionX = x;
                                e.motionY = y;
                                e.motionZ = z;
                                e.isAirBorne = true;
                                if (e.collidedHorizontally) {
                                    e.motionY += 1.0D;
                                }
                            } else if (world.rand.nextInt(20) == 0) {
                                float pitch = 0.85F - world.rand.nextFloat() * 3.0F / 10.0F;
                                world.playSound(e.posX, e.posY, e.posZ, SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.endermen.teleport")), SoundCategory.PLAYERS, 0.6F, pitch, true);
                            }
                        }
                    }
                }
            }
        }
    }
}