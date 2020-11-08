package com.github.lehjr.modularpowerarmor.entity;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class PlasmaBoltEntity extends ThrowableEntity implements IEntityAdditionalSpawnData {
    private static final DataParameter<Float> ACTUAL_SIZE = EntityDataManager.createKey(PlasmaBoltEntity.class, DataSerializers.FLOAT);
    public double damagingness;
    public double explosiveness;

    public PlasmaBoltEntity(EntityType<? extends PlasmaBoltEntity> entityType, World world) {
        super(entityType, world);
    }

    public PlasmaBoltEntity(World world, LivingEntity shootingEntity, double explosivenessIn, double damagingnessIn, int chargeTicks) {
        super(MPAObjects.PLASMA_BOLT_ENTITY_TYPE.get(), world);
        this.setShooter(shootingEntity);

        float size = ((chargeTicks) > 50F ? 50F : chargeTicks);
        this.dataManager.set(ACTUAL_SIZE, size);
        this.explosiveness = explosivenessIn;
        this.damagingness = damagingnessIn;
        Vector3d direction = shootingEntity.getLookVec().normalize();
        double scale = 1.0;
        this.setMotion(
                direction.x * scale,
                direction.y * scale,
                direction.z * scale
        );

        double r = size / 50.0;
        double xoffset = 1.3f + r - direction.y * shootingEntity.getEyeHeight();
        double yoffset = -.2;
        double zoffset = 0.3f;
        double horzScale = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        double horzx = direction.x / horzScale;
        double horzz = direction.z / horzScale;
        this.setPosition(
                // x
                (shootingEntity.getPosX() + direction.x * xoffset - direction.y * horzx * yoffset - horzz * zoffset),
                // y
                (shootingEntity.getPosY() + shootingEntity.getEyeHeight() + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset),
                //z
                (shootingEntity.getPosZ() + direction.z * xoffset - direction.y * horzz * yoffset + horzx * zoffset)
        );

        this.setBoundingBox(new AxisAlignedBB(getPosX() - r, getPosY() - r, getPosZ()- r, getPosX() + r, getPosY() + r, getPosZ() + r));
    }

    @Override
    protected void registerData() {
        dataManager.register(ACTUAL_SIZE, 0F);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.ticksExisted > this.getMaxLifetime()) {
            this.remove();
        }

        if (this.isInWater()) {
            this.remove();
            for (int i = 0; i <  this.dataManager.get(ACTUAL_SIZE); ++i) {
                this.world.addParticle(ParticleTypes.FLAME,
                        this.getPosX() + Math.random() * 1, this.getPosY() + Math.random() * 1, this.getPosZ() + Math.random()
                                * 0.1,
                        0.0D, 0.0D, 0.0D);
            }
        }
    }

    public int getMaxLifetime() {
        return 200;
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
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        float size = this.dataManager.get(ACTUAL_SIZE);

        double damage =  size/ 50.0 * this.damagingness;
        switch (result.getType()) {
            case ENTITY:
                EntityRayTraceResult rayTraceResult = (EntityRayTraceResult) result;
                if (rayTraceResult.getEntity() != null && rayTraceResult.getEntity() != func_234616_v_()) {
                    rayTraceResult.getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, func_234616_v_()), (int) damage);
                }
                break;
            case BLOCK:
                break;
            default:
                break;
        }
        if (!this.world.isRemote) { // Dist.SERVER
            boolean flag = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING);

            // FIXME: this is probably all wrone
            this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), (float) (size / 50.0f * 3 * this.explosiveness), flag ? Explosion.Mode.DESTROY : Explosion.Mode.BREAK);
        }
        for (int var3 = 0; var3 < 8; ++var3) {
            this.world.addParticle(ParticleTypes.FLAME,
                    this.getPosX() + Math.random() * 0.1, this.getPosY() + Math.random() * 0.1, this.getPosZ() + Math.random() * 0.1,
                    0.0D, 0.0D, 0.0D);
        }
        if (!this.world.isRemote) {
            this.remove();
        }
    }

    public float getActualSize() {
        return this.dataManager.get(ACTUAL_SIZE);
    }

    /**
     * Called by the server when constructing the spawn packet.
     * Data should be added to the provided stream.
     *
     * @param buffer The packet data stream
     */
    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeFloat(this.dataManager.get(ACTUAL_SIZE));
    }

    /**
     * Called by the client when it receives a Entity spawn packet.
     * Data should be read out of the stream in the same way as it was written.
     *
     * @param additionalData The packet data stream
     */
    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.dataManager.set(ACTUAL_SIZE, additionalData.readFloat());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}