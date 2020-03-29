package com.github.lehjr.modularpowerarmor.entity;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.mpalib.math.Colour;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * TODO:
 *  - remove/adjust bolt arch
 *  - adjust bolt velocity
 *  - change bolt hit/miss sounds
 *  - restore particle trail
 *  - remove on impact with entity or solid blocks
 *
 *
 *  Notes:
 *  - arrow/crossbow code spawns entity then calls shoot method before adding to world
 *  - Velocity for crossbow is 1.6F, accuracy is this mess (float)(14 - this.world.getDifficulty().getId() * 4)
 *  - bow uses charge calculation based on charge for velocity, but innacuracy of 1
 *
 *  quick research suggests average crossbow velocity of 91 meters per second
 *  rail gun velocity appears to be be around 3000 meters per second
 *
 *  */
public class BoltEntity extends Entity implements IProjectile {
    private static final DataParameter<Byte> CRITICAL = EntityDataManager.createKey(BoltEntity.class, DataSerializers.BYTE);
    protected static final DataParameter<Optional<UUID>> field_212362_a = EntityDataManager.createKey(BoltEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<Byte> PIERCE_LEVEL = EntityDataManager.createKey(BoltEntity.class, DataSerializers.BYTE);

    public LivingEntity shootingEntity;
    public UUID shootingEntityUUID;
    private int ticksAlive;
    private int ticksInAir;
    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;
    public double knockbackStrength;
    private double damage = 2.0D;
    double range = 64;

    // these should not be needed (arrow stuck in block)
    @Nullable
    private BlockState inBlockState;
    protected boolean inGround;
    protected int timeInGround;
    public int arrowShake;
    private int ticksInGround;
    private SoundEvent hitSound = this.getHitEntitySound();

    private IntOpenHashSet piercedEntities;
    private List<Entity> hitEntities;
    private BlockPos startPos;

    // default constructor
   public BoltEntity(EntityType<? extends BoltEntity> type, World world) {
        super(type, world);
    }

    public BoltEntity(World world, LivingEntity shootingEntity, double damageIn, double knockback) {
        super(MPAObjects.BOLT_ENTITY_TYPE, world);
        this.knockbackStrength = knockback;
        this.shootingEntity = shootingEntity;
        this.shootingEntityUUID = shootingEntity.getUniqueID();
        this.damage = damageIn;
        Vec3d direction = shootingEntity.getLookVec().normalize();
        double scale = 1.0;
        this.setMotion(
                direction.x * scale,
                direction.y * scale,
                direction.z * scale
        );

        double r = 0.05;
        double xoffset = 1.3f + r - direction.y * shootingEntity.getEyeHeight();
        double yoffset = -.2;
        double zoffset = 0.3f;
        double horzScale = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        double horzx = direction.x / horzScale;
        double horzz = direction.z / horzScale;
        this.posX = shootingEntity.posX + direction.x * xoffset - direction.y * horzx * yoffset - horzz * zoffset;
        this.posY = shootingEntity.posY + shootingEntity.getEyeHeight() + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset;
        this.posZ = shootingEntity.posZ + direction.z * xoffset - direction.y * horzz * yoffset + horzx * zoffset;
        this.startPos = this.getPosition();
        this.setBoundingBox(new AxisAlignedBB(posX - r, posY - r, posZ - r, posX + r, posY + r, posZ + r));
    }

    public void shoot(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy) {
        float f = -MathHelper.sin(yaw * ((float)Math.PI / 180F)) * MathHelper.cos(pitch * ((float)Math.PI / 180F));
        float f1 = -MathHelper.sin(pitch * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(yaw * ((float)Math.PI / 180F)) * MathHelper.cos(pitch * ((float)Math.PI / 180F));
        this.shoot(f, f1, f2, velocity, inaccuracy);
        this.setMotion(this.getMotion().add(shooter.getMotion().x, shooter.onGround ? 0.0D : shooter.getMotion().y, shooter.getMotion().z));
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3d vec3d = (new Vec3d(x, y, z)).normalize().add(this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale(velocity);
        this.setMotion(vec3d);
        float f = MathHelper.sqrt(horizontalMag(vec3d));
        this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, f) * (double)(180F / (float)Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.ticksInGround = 0;
    }














//    // projectile
//    public void tick() {
//        if (this.world.isRemote ||
//                (this.shootingEntity == null ||
//                        !this.shootingEntity.removed)
//                        && this.world.isBlockLoaded(new BlockPos(this))) {
//            super.tick();
//
//                this.setFire(1);
//
//
//            ++this.ticksInAir;
//
//
//            RayTraceResult raytraceresult = ProjectileHelper.rayTrace(this, true, this.ticksInAir >= 25, this.shootingEntity, RayTraceContext.BlockMode.COLLIDER);
//            if (raytraceresult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
//                this.onHit(raytraceresult);
//            }
//
//
//
//
//
//
//
//
//
//            Vec3d vec3d = this.getMotion();
//            this.posX += vec3d.x;
//            this.posY += vec3d.y;
//            this.posZ += vec3d.z;
//            ProjectileHelper.rotateTowardsMovement(this, 0.2F);
//            float f = this.getMotionFactor();
//            if (this.isInWater()) {
//                for(int i = 0; i < 4; ++i) {
//                    float f1 = 0.25F;
//                    this.world.addParticle(ParticleTypes.BUBBLE, this.posX - vec3d.x * 0.25D, this.posY - vec3d.y * 0.25D, this.posZ - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
//                }
//
//                f = 0.8F;
//            }
//
//            this.setMotion(vec3d.add(this.accelerationX, this.accelerationY, this.accelerationZ).scale((double)f));
//            this.world.addParticle(this.getParticle(), this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
//            this.setPosition(this.posX, this.posY, this.posZ);
//        } else {
//            this.remove();
//        }
//    }

    // arrow
    public void tick() {
        super.tick();
        boolean flag = this.getNoClip();
        Vec3d vec3d = this.getMotion();

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(horizontalMag(vec3d));
            this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
            this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, f) * (double)(180F / (float)Math.PI));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        BlockPos blockpos = new BlockPos(this.posX, this.posY, this.posZ);
        BlockState blockstate = this.world.getBlockState(blockpos);
        if (!blockstate.isAir(this.world, blockpos) && !flag) {
            System.out.println("doing something here");


            VoxelShape voxelshape = blockstate.getCollisionShape(this.world, blockpos);
            if (!voxelshape.isEmpty()) {
                for(AxisAlignedBB axisalignedbb : voxelshape.toBoundingBoxList()) {
                    if (axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                        System.out.println("in ground");


                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }


        System.out.println("doing something here");

        if (this.isWet()) {
            this.extinguish();
        }

        System.out.println("doing something here");

        if (this.inGround && !flag) {


            System.out.println("doing something here");

            if (this.inBlockState != blockstate && this.world.areCollisionShapesEmpty(this.getBoundingBox().grow(0.06D))) {
                this.inGround = false;
                this.setMotion(vec3d.mul(this.rand.nextFloat() * 0.2F, this.rand.nextFloat() * 0.2F, this.rand.nextFloat() * 0.2F));
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            } else if (!this.world.isRemote) {
                this.remove();
            }

            ++this.timeInGround;
        } else {
            System.out.println("doing something here");

            this.timeInGround = 0;
            ++this.ticksInAir;
            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d2 = vec3d1.add(vec3d);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vec3d1, vec3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
            if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
                vec3d2 = raytraceresult.getHitVec();
            }

            while(this.isAlive()) {
                System.out.println("doing something here");



                EntityRayTraceResult entityraytraceresult = this.rayTraceEntities(vec3d1, vec3d2);
                if (entityraytraceresult != null) {
                    raytraceresult = entityraytraceresult;
                }

                if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.ENTITY) {
                    Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
                    Entity entity1 = this.getShooter();
                    if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity)entity1).canAttackPlayer((PlayerEntity)entity)) {
                        raytraceresult = null;
                        entityraytraceresult = null;
                    }
                }

                if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                    this.onHit(raytraceresult);
                    this.isAirBorne = true;
                }

                if (entityraytraceresult == null || this.getPierceLevel() <= 0) {
                    break;
                }

                raytraceresult = null;
            }

            vec3d = this.getMotion();
            double d1 = vec3d.x;
            double d2 = vec3d.y;
            double d0 = vec3d.z;
            if (this.getIsCritical()) {
                for(int i = 0; i < 4; ++i) {
                    this.world.addParticle(ParticleTypes.CRIT, this.posX + d1 * (double)i / 4.0D, this.posY + d2 * (double)i / 4.0D, this.posZ + d0 * (double)i / 4.0D, -d1, -d2 + 0.2D, -d0);
                }
            }

            this.posX += d1;
            this.posY += d2;
            this.posZ += d0;
            float f4 = MathHelper.sqrt(horizontalMag(vec3d));
            if (flag) {
                this.rotationYaw = (float)(MathHelper.atan2(-d1, -d0) * (double)(180F / (float)Math.PI));
            } else {
                this.rotationYaw = (float)(MathHelper.atan2(d1, d0) * (double)(180F / (float)Math.PI));
            }

            for(this.rotationPitch = (float)(MathHelper.atan2(d2, f4) * (double)(180F / (float)Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
            }

            while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }

            while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }

            while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
            this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
            float f1 = 0.99F;
            float f2 = 0.05F;
            if (this.isInWater()) {
                for(int j = 0; j < 4; ++j) {
                    float f3 = 0.25F;
                    this.world.addParticle(ParticleTypes.BUBBLE, this.posX - d1 * 0.25D, this.posY - d2 * 0.25D, this.posZ - d0 * 0.25D, d1, d2, d0);
                }

                f1 = this.getWaterDrag();
            }

            this.setMotion(vec3d.scale(f1));
            if (!this.hasNoGravity() && !flag) {
                Vec3d vec3d3 = this.getMotion();
                this.setMotion(vec3d3.x, vec3d3.y - (double)0.05F, vec3d3.z);
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }


        if (this.world.isRemote) {
            if (this.inGround) {
                if (this.timeInGround % 5 == 0) {
                    this.spawnPotionParticles(1);
                }
            } else {
                this.spawnPotionParticles(2);
            }
        } else if (this.inGround
                && this.timeInGround != 0
                && this.timeInGround >= 600) {
            this.world.setEntityState(this, (byte)0);
//            this.dataManager.set(COLOR, -1);
        }
    }




















    protected boolean isFireballFiery() {
        return true;
    }

    protected IParticleData getParticle() {
        return ParticleTypes.SMOKE;
    }

    protected float getMotionFactor() {
        return 0.95F;
    }


    public boolean canBeCollidedWith() {
        return true;
    }

    public float getCollisionBorderSize() {
        return 1.0F;
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            this.markVelocityChanged();
            if (source.getTrueSource() != null) {
                Vec3d vec3d = source.getTrueSource().getLookVec();
                this.setMotion(vec3d);
                this.accelerationX = vec3d.x * 0.1D;
                this.accelerationY = vec3d.y * 0.1D;
                this.accelerationZ = vec3d.z * 0.1D;
                if (source.getTrueSource() instanceof LivingEntity) {
                    this.shootingEntity = (LivingEntity)source.getTrueSource();
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public float getBrightness() {
        return 1.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }



// End DamagingProjectileEntity -------------------------------------------------------------------------------------------------------------
// Start Abstract arrow entity ----







    public void setHitSound(SoundEvent soundIn) {
        this.hitSound = soundIn;
    }



    protected void registerData() {
        this.dataManager.register(CRITICAL, (byte)0);
        this.dataManager.register(field_212362_a, Optional.empty());
        this.dataManager.register(PIERCE_LEVEL, (byte)0);
    }



    @OnlyIn(Dist.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @OnlyIn(Dist.CLIENT)
    public void setVelocity(double x, double y, double z) {
        this.setMotion(x, y, z);
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(x * x + z * z);
            this.rotationPitch = (float)(MathHelper.atan2(y, f) * (double)(180F / (float)Math.PI));
            this.rotationYaw = (float)(MathHelper.atan2(x, z) * (double)(180F / (float)Math.PI));
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }


    protected void onHit(RayTraceResult raytraceResultIn) {
        RayTraceResult.Type raytraceresult$type = raytraceResultIn.getType();
        if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
            this.onEntityHit((EntityRayTraceResult)raytraceResultIn);
        } else if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceResultIn;
            BlockState blockstate = this.world.getBlockState(blockraytraceresult.getPos());
            this.inBlockState = blockstate;
            Vec3d vec3d = blockraytraceresult.getHitVec().subtract(this.posX, this.posY, this.posZ);
            this.setMotion(vec3d);
            Vec3d vec3d1 = vec3d.normalize().scale(0.05F);
            this.posX -= vec3d1.x;
            this.posY -= vec3d1.y;
            this.posZ -= vec3d1.z;
            this.playSound(this.getHitGroundSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.arrowShake = 7;
            this.setIsCritical(false);
            this.setPierceLevel((byte)0);
            this.setHitSound(SoundEvents.ENTITY_GENERIC_EXPLODE);
            this.setShotFromCrossbow(false);
            this.func_213870_w();
            blockstate.onProjectileCollision(this.world, blockstate, blockraytraceresult, this);
        }
    }

    protected void onEntityHit(EntityRayTraceResult result) {
        Entity entity = result.getEntity();
        float f = (float)this.getMotion().length();
        int i = MathHelper.ceil(Math.max((double)f * this.damage, 0.0D));
        if (this.getPierceLevel() > 0) {
            if (this.piercedEntities == null) {
                this.piercedEntities = new IntOpenHashSet(5);
            }

            if (this.hitEntities == null) {
                this.hitEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.piercedEntities.size() >= this.getPierceLevel() + 1) {
                this.remove();
                return;
            }

            this.piercedEntities.add(entity.getEntityId());
        }

        if (this.getIsCritical()) {
            i += this.rand.nextInt(i / 2 + 2);
        }

        Entity entity1 = this.getShooter();
        DamageSource damagesource;
        if (entity1 == null) {
            damagesource = causeBoltDamage(this, this);
        } else {
            damagesource = causeBoltDamage(this, entity1);
            if (entity1 instanceof LivingEntity) {
                ((LivingEntity)entity1).setLastAttackedEntity(entity);
            }
        }

        int j = entity.getFireTimer();
        if (this.isBurning() && !(entity instanceof EndermanEntity)) {
            entity.setFire(5);
        }

        if (entity.attackEntityFrom(damagesource, (float)i)) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)entity;
                if (!this.world.isRemote && this.getPierceLevel() <= 0) {
                    livingentity.setArrowCountInEntity(livingentity.getArrowCountInEntity() + 1);
                }

                if (this.knockbackStrength > 0) {
                    Vec3d vec3d = this.getMotion().mul(1.0D, 0.0D, 1.0D).normalize().scale(this.knockbackStrength * 0.6D);
                    if (vec3d.lengthSquared() > 0.0D) {
                        livingentity.addVelocity(vec3d.x, 0.1D, vec3d.z);
                    }
                }

                if (!this.world.isRemote && entity1 instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(livingentity, entity1);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity)entity1, livingentity);
                }

                this.arrowHit(livingentity);
                if (entity1 != null && livingentity != entity1 && livingentity instanceof PlayerEntity && entity1 instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity)entity1).connection.sendPacket(new SChangeGameStatePacket(6, 0.0F));
                }

                if (!entity.isAlive() && this.hitEntities != null) {
                    this.hitEntities.add(livingentity);
                }

                if (!this.world.isRemote && entity1 instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity1;
                    if (this.hitEntities != null && this.getShotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayerentity, this.hitEntities, this.hitEntities.size());
                    } else if (!entity.isAlive() && this.getShotFromCrossbow()) {
                        CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayerentity, Arrays.asList(entity), 0);
                    }
                }
            }

            this.playSound(this.hitSound, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            if (this.getPierceLevel() <= 0 && !(entity instanceof EndermanEntity)) {
                this.remove();
            }
        } else {
            entity.setFireTimer(j);
            this.setMotion(this.getMotion().scale(-0.1D));
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            this.ticksInAir = 0;
            if (!this.world.isRemote && this.getMotion().lengthSquared() < 1.0E-7D) {
                this.remove();
            }
        }
    }

    private void func_213870_w() {
        if (this.hitEntities != null) {
            this.hitEntities.clear();
        }

        if (this.piercedEntities != null) {
            this.piercedEntities.clear();
        }
    }



    protected SoundEvent getHitEntitySound() {
        return SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE;
    }

    protected final SoundEvent getHitGroundSound() {
        return this.hitSound;
    }

    protected void arrowHit(LivingEntity living) {
    }

    @Nullable
    protected EntityRayTraceResult rayTraceEntities(Vec3d startVec, Vec3d endVec) {
        return ProjectileHelper.rayTraceEntities(this.world,
                this,
                startVec,
                endVec,
                this.getBoundingBox().expand(this.getMotion()).grow(1.0D), (entity) -> !entity.isSpectator() && entity.isAlive() && entity.canBeCollidedWith() && (entity != this.getShooter() || this.ticksInAir >= 5) && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getEntityId())));
    }

    // pickup code
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        // this was just code to pick up an arrow
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    public void setDamage(double damageIn) {
        this.damage = damageIn;
    }

    public double getDamage() {
        return this.damage;
    }

    public boolean canBeAttackedWithItem() {
        return false;
    }

    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.0F;
    }

    public void setIsCritical(boolean critical) {
        this.setArrowFlag(1, critical);
    }

    public void setPierceLevel(byte level) {
        this.dataManager.set(PIERCE_LEVEL, level);
    }



    public boolean getIsCritical() {
        byte b0 = this.dataManager.get(CRITICAL);
        return (b0 & 1) != 0;
    }

    public boolean getShotFromCrossbow() {
        byte b0 = this.dataManager.get(CRITICAL);
        return (b0 & 4) != 0;
    }

    public byte getPierceLevel() {
        return this.dataManager.get(PIERCE_LEVEL);
    }



    protected float getWaterDrag() {
        return 0.6F;
    }

    public void setNoClip(boolean noClipIn) {
        this.noClip = noClipIn;
        this.setArrowFlag(2, noClipIn);
    }

    public boolean getNoClip() {
        if (!this.world.isRemote) {
            return this.noClip;
        } else {
            return (this.dataManager.get(CRITICAL) & 2) != 0;
        }
    }


    private void setArrowFlag(int p_203049_1_, boolean p_203049_2_) {
        byte b0 = this.dataManager.get(CRITICAL);
        if (p_203049_2_) {
            this.dataManager.set(CRITICAL, (byte)(b0 | p_203049_1_));
        } else {
            this.dataManager.set(CRITICAL, (byte)(b0 & ~p_203049_1_));
        }

    }

    public void setShotFromCrossbow(boolean fromCrossbow) {
        this.setArrowFlag(4, fromCrossbow);
    }



// End arrow entity -------------------------------------------------------------------------------












//    @Override
//    protected void registerData() {
//
//    }
//
//
//    @Override
//    public void baseTick() {
//        super.baseTick();
//        if (this.ticksExisted > this.getMaxLifetime()) {
//            this.remove();
//        }
//
//        if (world.isRemote() && owner != null) {
//            drawParticleStreamTo(owner, world, posX, posY, posZ);
//        }
//    }
//
//



//    public int getMaxLifetime() {
//        return 200;
//    }
//
//    /**
//     * returns if this entity triggers Block.onEntityWalking on the blocks they
//     * walk on. used for spiders and wolves to prevent them from trampling crops
//     */
//    @Override
//    protected boolean canTriggerWalking() {
//        return false;
//    }
//
//
//    /**
//     * Gets the amount of gravity to apply to the thrown entity with each tick.
//     */
//    @Override
//    protected float getGravityVelocity() {
//        return 0;
//    }
//
//    @Override
//    protected void onImpact(RayTraceResult result) {
//        if (result != null) {
//            System.out.println("hit type: " + result.getType());
//
//
//            switch (result.getType()) {
//                case MISS:
//                    world.playSound(
//                            owner.posX,
//                            owner.posY,
//                            owner.posZ,
//                            SoundEvents.ENTITY_ARROW_SHOOT,
//                            SoundCategory.MASTER,
//                            0.5F,
//                            0.4F / ((float) Math.random() * 0.4F + 0.8F),
//                            true);
//                    break;
//
//                case BLOCK:
////                                    drawParticleStreamTo(playerIn, worldIn, result.getHitVec().x, result.getHitVec().y, result.getHitVec().z);
//                    world.playSound(
//                            owner.posX,
//                            owner.posY,
//                            owner.posZ,
//                            SoundEvents.ENTITY_GENERIC_EXPLODE,
//                            SoundCategory.MASTER,
//                            1.5F,
//                            0.4F / ((float) Math.random() * 0.4F + 0.8F),
//                            true);
//                    break;
//
//                case ENTITY:
//                    Entity target = ((EntityRayTraceResult) result).getEntity();
////                                    drawParticleStreamTo(playerIn, worldIn, result.getHitVec().x, result.getHitVec().y, result.getHitVec().z);
//                    world.playSound(
//                            owner.posX,
//                            owner.posY,
//                            owner.posZ,
//                            SoundEvents.ENTITY_GENERIC_EXPLODE,
//                            SoundCategory.MASTER,
//                            1.5F,
//                            0.4F / ((float) Math.random() * 0.4F + 0.8F),
//                            true);
//
//                    DamageSource damageSource = new IndirectEntityDamageSource("bolt", this, owner).setProjectile();
//                    if (target.attackEntityFrom(damageSource, (int) damagingness)) {
//
//
////                        target.addVelocity(lookVec.x * knockback, Math.abs(lookVec.y + 0.2f) * knockback, lookVec.z * knockback);
//                    }
//                    break;
//            }
//            System.out.println("knockback:  " + knockbackStrength);
//
//            owner.knockBack(owner, 0.2F, 0.5, 0.5);
//
////            shootingEntity.addVelocity(-lookVec.x * knockback, Math.abs(-lookVec.y + 0.2f) * knockback, -lookVec.z * knockback);
//
//        }
//        this.remove();
//    }
//
//    @Override
//    public void readAdditional(CompoundNBT compound) {
//        super.readAdditional(compound);
//
//        System.out.println("compund: " + compound);
//
//    }
//
//    @Override
//    public void writeAdditional(CompoundNBT compound) {
//        super.writeAdditional(compound);
//
//        System.out.println("compund: " + compound);
//    }
//
//
//
//
//    /**
//     * Called by the server when constructing the spawn packet.
//     * Data should be added to the provided stream.
//     *
//     * @param buffer The packet data stream
//     */
//    @Override
//    public void writeSpawnData(PacketBuffer buffer) {
//
//    }
//
//    /**
//     * Called by the client when it receives a Entity spawn packet.
//     * Data should be read out of the stream in the same way as it was written.
//     *
//     * @param additionalData The packet data stream
//     */
//    @Override
//    public void readSpawnData(PacketBuffer additionalData) {
//
//    }

            /*
            public static class NBT
    {
        public static final int TAG_END         = 0;
        public static final int TAG_BYTE        = 1;
        public static final int TAG_SHORT       = 2;
        public static final int TAG_INT         = 3;
        public static final int TAG_LONG        = 4;
        public static final int TAG_FLOAT       = 5;
        public static final int TAG_DOUBLE      = 6;
        public static final int TAG_BYTE_ARRAY  = 7;
        public static final int TAG_STRING      = 8;
        public static final int TAG_LIST        = 9;
        public static final int TAG_COMPOUND    = 10;
        public static final int TAG_INT_ARRAY   = 11;
        public static final int TAG_LONG_ARRAY  = 12;
        public static final int TAG_ANY_NUMERIC = 99;
    }
         */


    public void writeAdditional(CompoundNBT compound) {
        Vec3d vec3d = this.getMotion();
        compound.put("direction", this.newDoubleNBTList(vec3d.x, vec3d.y, vec3d.z));
        compound.put("power", this.newDoubleNBTList(this.accelerationX, this.accelerationY, this.accelerationZ));
        compound.putInt("life", this.ticksAlive);


        compound.putShort("life", (short)this.ticksInGround);
        if (this.inBlockState != null) {
            compound.put("inBlockState", NBTUtil.writeBlockState(this.inBlockState));
        }

        compound.putByte("shake", (byte)this.arrowShake);
        compound.putByte("inGround", (byte)(this.inGround ? 1 : 0));
        compound.putDouble("damage", this.damage);
        compound.putBoolean("crit", this.getIsCritical());
        compound.putByte("PierceLevel", this.getPierceLevel());
        if (this.shootingEntity != null) {
            compound.putUniqueId("OwnerUUID", this.shootingEntityUUID);
        }

        compound.putString("SoundEvent", Registry.SOUND_EVENT.getKey(this.hitSound).toString());
    }

    public void readAdditional(CompoundNBT compound) {
        if (compound.contains("power", Constants.NBT.TAG_LIST)) {
            ListNBT listnbt = compound.getList("power", Constants.NBT.TAG_DOUBLE);
            if (listnbt.size() == 3) {
                this.accelerationX = listnbt.getDouble(0);
                this.accelerationY = listnbt.getDouble(1);
                this.accelerationZ = listnbt.getDouble(2);
            }
        }

        this.ticksAlive = compound.getInt("life");
        if (compound.contains("direction", Constants.NBT.TAG_LIST) &&
                compound.getList("direction", Constants.NBT.TAG_DOUBLE).size() == 3) {
            ListNBT listnbt1 = compound.getList("direction", Constants.NBT.TAG_DOUBLE);
            this.setMotion(
                    listnbt1.getDouble(0),
                    listnbt1.getDouble(1),
                    listnbt1.getDouble(2));
        } else {
            this.remove();
        }

        this.arrowShake = compound.getByte("shake") & 255;
        this.inGround = compound.getByte("inGround") == 1;
        if (compound.contains("damage", Constants.NBT.TAG_ANY_NUMERIC)) {
            this.damage = compound.getDouble("damage");
        }

        this.setIsCritical(compound.getBoolean("crit"));
        this.setPierceLevel(compound.getByte("PierceLevel"));
        if (compound.hasUniqueId("OwnerUUID")) {
            this.shootingEntityUUID = compound.getUniqueId("OwnerUUID");
        }

        if (compound.contains("SoundEvent", Constants.NBT.TAG_STRING)) {
            this.hitSound = Registry.SOUND_EVENT.getValue(new ResourceLocation(compound.getString("SoundEvent"))).orElse(this.getHitEntitySound());
        }
    }





    /* // abstract projectile
    public IPacket<?> createSpawnPacket() {
        int i = this.shootingEntity == null ? 0 : this.shootingEntity.getEntityId();
        return new SSpawnObjectPacket(this.getEntityId(), this.getUniqueID(), this.posX, this.posY, this.posZ, this.rotationPitch, this.rotationYaw, this.getType(), i, new Vec3d(this.accelerationX, this.accelerationY, this.accelerationZ));
    }
*/

    /* // arrow
        public IPacket<?> createSpawnPacket() {
        Entity entity = this.getShooter();
        return new SSpawnObjectPacket(this, entity == null ? 0 : entity.getEntityId());
    }
     */


    private void spawnPotionParticles(int particleCount) {
        int i = Colour.WHITE.getInt();
        System.out.println("particle count: " + particleCount);


        if (i != -1 && particleCount > 0) {
            double d0 = (double)(i >> 16 & 255) / 255.0D;
            double d1 = (double)(i >> 8 & 255) / 255.0D;
            double d2 = (double)(i >> 0 & 255) / 255.0D;

            for(int j = 0; j < particleCount; ++j) {
                this.world.addParticle(ParticleTypes.ENTITY_EFFECT,
                        this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getWidth(),
                        this.posY + this.rand.nextDouble() * (double)this.getHeight(),
                        this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getWidth(), d0, d1, d2);
            }
        }
    }

    public void drawParticleStreamTo(LivingEntity source, World world, double x, double y, double z) {
        if (source != null) {
            Vec3d direction = source.getLookVec().normalize();
            double xoffset = 1.3f;
            double yoffset = -.2;
            double zoffset = 0.3f;
            Vec3d horzdir = direction.normalize();
            horzdir = new Vec3d(horzdir.x, 0, horzdir.z);
            horzdir = horzdir.normalize();
            double cx = source.posX + direction.x * xoffset - direction.y * horzdir.x * yoffset - horzdir.z * zoffset;
            double cy = source.posY + source.getEyeHeight() + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset;
            double cz = source.posZ + direction.z * xoffset - direction.y * horzdir.z * yoffset + horzdir.x * zoffset;
            double dx = x - cx;
            double dy = y - cy;
            double dz = z - cz;
            double ratio = Math.sqrt(dx * dx + dy * dy + dz * dz);

            while (Math.abs(cx - x) > Math.abs(dx / ratio)) {
                world.addParticle(ParticleTypes.MYCELIUM, cx, cy, cz, 0.0D, 0.0D, 0.0D);
                cx += dx * 0.1 / ratio;
                cy += dy * 0.1 / ratio;
                cz += dz * 0.1 / ratio;
            }
        }
    }









    // AbstractArrowEntity
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        double d0 = this.getBoundingBox().getAverageEdgeLength() * 10.0D;
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }

        d0 = d0 * 64.0D * getRenderDistanceWeight();
        return distance > 2 && distance < d0 * d0;
    }

    @Nullable
    public Entity getShooter() {
        if (shootingEntity != null) {
            return shootingEntity;
        }
        return this.shootingEntityUUID != null && this.world instanceof ServerWorld ? ((ServerWorld)this.world).getEntityByUuid(this.shootingEntityUUID) : null;
    }

    public static DamageSource causeBoltDamage(BoltEntity bolt, @Nullable Entity shooter) {
        return (new IndirectEntityDamageSource("bolt", bolt, shooter)).setProjectile();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}