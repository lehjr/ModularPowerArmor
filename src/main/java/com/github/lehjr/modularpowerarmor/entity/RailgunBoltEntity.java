package com.github.lehjr.modularpowerarmor.entity;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

// TODO: use arrow as base class.. maybe copy spectral arrorw.. this is taking way longer than it should
// currently, launching still seems off on the position. Check bow and crossbow launch code.
// even with using an arrow it's still off compared to the plasma ball...
// todo: switch to using more like plasma cannon  (charge up to launch ... maybe use some type of cube model so orientation doesn't matter)

public class RailgunBoltEntity extends AbstractArrowEntity implements IEntityAdditionalSpawnData {
    private int duration = 200;

    double damage = 0;
    double knockbackStrength = 0;

    public RailgunBoltEntity(EntityType<? extends RailgunBoltEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public RailgunBoltEntity(World world, LivingEntity shooter) {
        super(MPAObjects.RAILGUN_BOLT_ENTITY_TYPE.get(), shooter, world);
        this.pickupStatus = PickupStatus.DISALLOWED;

        // todo: replace with something resembling original code
        if (shooter instanceof PlayerEntity) {
            setIsCritical(true);
        }

        setHitSound(SoundEvents.ENTITY_GENERIC_EXPLODE);

        setShotFromCrossbow(false);

        setPierceLevel((byte)3);
        setNoGravity(true);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.ticksExisted > this.getMaxLifetime()) {
            this.remove();
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
    }

    public int getMaxLifetime() {
        return 20;
        //return 200;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot(Vector3d direction, double velocity, double inaccuracy) {
        if (inaccuracy > 0) {
            direction = direction
                    .normalize()
                    .add(this.rand.nextGaussian() * 0.0075 * inaccuracy,
                            this.rand.nextGaussian() * 0.0075 * inaccuracy,
                            this.rand.nextGaussian() * 0.0075 * inaccuracy);
        }
        //  Note that while this doesn't actually change the start point,
        //      the vector change in the first line makes it render like it's starting from another point.
        direction = direction.scale(velocity);
        this.setMotion(direction);
        float f = MathHelper.sqrt(horizontalMag(direction));
        this.rotationYaw = (float)(MathHelper.atan2(direction.x, direction.z) * (double)(180F / (float)Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(direction.y, f) * (double)(180F / (float)Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    protected ItemStack getArrowStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(this.duration);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.duration = additionalData.readInt();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}