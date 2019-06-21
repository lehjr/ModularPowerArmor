package net.machinemuse.powersuits.item.module.special;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.capabilities.module.tickable.IModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTick;
import net.machinemuse.numina.capabilities.module.tickable.ModuleTickCapability;
import net.machinemuse.numina.capabilities.module.toggleable.IModuleToggle;
import net.machinemuse.numina.capabilities.module.toggleable.Toggle;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MagnetModule extends AbstractPowerModule {
    public MagnetModule(String regName) {
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
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_SPECIAL, EnumModuleTarget.TOOLONLY, MPSConfig.INSTANCE);

            this.moduleCap.addBasePropertyDouble(MPSConstants.ENERGY_CONSUMPTION, 0, "RF");
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.POWER, MPSConstants.ENERGY_CONSUMPTION, 2000);
            this.moduleCap.addBasePropertyDouble(MPSConstants.RADIUS, 5);
            this.moduleCap.addTradeoffPropertyDouble(MPSConstants.POWER, MPSConstants.RADIUS, 10);

            this.toggle = new Toggle(module);
            this.ticker = new Ticker();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ModuleTickCapability.TICK)
                return ModuleTickCapability.TICK.orEmpty(cap, LazyOptional.of(() -> ticker));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class Ticker extends ModuleTick {
            @Override
            public void onPlayerTickActive(PlayerEntity player, ItemStack stack) {
                int energyUSage = (int) moduleCap.applyPropertyModifiers(MPSConstants.ENERGY_CONSUMPTION);

                if (ElectricItemUtils.getPlayerEnergy(player) > energyUSage) {
                    boolean isServerSide = !player.world.isRemote;

                    if ((player.world.getGameTime() % 20) == 0 && isServerSide) {
                        ElectricItemUtils.drainPlayerEnergy(player, energyUSage);
                    }
                    int range = (int) moduleCap.applyPropertyModifiers(MPSConstants.RADIUS);
                    World world = player.world;
                    AxisAlignedBB bounds = player.getBoundingBox().grow(range);

                    if (isServerSide) {
                        bounds.expand(0.2000000029802322D, 0.2000000029802322D, 0.2000000029802322D);
                        if (stack.getDamage() >> 1 >= 7) {
                            List<ArrowEntity> arrows = world.getEntitiesWithinAABB(ArrowEntity.class, bounds);
                            for (ArrowEntity arrow : arrows) {
                                if ((arrow.pickupStatus == ArrowEntity.PickupStatus.ALLOWED) && (world.rand.nextInt(6) == 0)) {
                                    ItemEntity replacement = new ItemEntity(world, arrow.posX, arrow.posY, arrow.posZ, new ItemStack(Items.ARROW));
                                    world.addEntity(replacement);
                                }
                                arrow.remove();
                            }
                        }
                    }

                    for (ItemEntity e : world.getEntitiesWithinAABB(ItemEntity.class, bounds)) {
                        if (e.isAlive() && !e.getItem().isEmpty() && !e.cannotPickup()) {
                            if (isServerSide) {
                                double x = player.posX - e.posX;
                                double y = player.posY - e.posY;
                                double z = player.posZ - e.posZ;

                                double length = Math.sqrt(x * x + y * y + z * z) * 0.75D;

                                x = x / length + player.getMotion().x * 22.0D;
                                y = y / length + player.getMotion().y / 22.0D;
                                z = z / length + player.getMotion().z * 22.0D;

                                e.setMotion(x, y, z);

                                e.isAirBorne = true;
                                if (e.collidedHorizontally) {
                                    e.setMotion(e.getMotion().add(0, 1, 0));
                                }
                            } else if (world.rand.nextInt(20) == 0) {
                                float pitch = 0.85F - world.rand.nextFloat() * 3.0F / 10.0F;
                                world.playSound(e.posX, e.posY, e.posZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 0.6F, pitch, true);
                            }
                        }
                    }
                }
            }
        }
    }
}
