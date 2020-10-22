package com.github.lehjr.modularpowerarmor.entity;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.modularpowerarmor.block.BlockLuxCapacitor;
import com.github.lehjr.modularpowerarmor.tileentity.TileEntityLuxCapacitor;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class LuxCapacitorEntity extends ThrowableEntity implements IEntityAdditionalSpawnData {
    public Colour color;

    public LuxCapacitorEntity(EntityType<? extends LuxCapacitorEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        if (color == null) {
            color = BlockLuxCapacitor.defaultColor;
        }
    }

    public LuxCapacitorEntity(World world, LivingEntity shootingEntity, Colour color) {
        super(MPAObjects.LUX_CAPACITOR_ENTITY_TYPE, shootingEntity, world);
        this.setNoGravity(true);
        this.color = color != null ? color : BlockLuxCapacitor.defaultColor;
        Vec3d direction = shootingEntity.getLookVec().normalize();
        double speed = 1.0;
        this.setMotion(
                direction.x * speed,
                direction.y * speed,
                direction.z * speed
        );

        double r = 0.4375;
        double xoffset = 0.1;
        double yoffset = 0;
        double zoffset = 0;
        double horzScale = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        double horzx = direction.x / horzScale;
        double horzz = direction.z / horzScale;
        this.setPosition(
                (shootingEntity.getPosX() + direction.x * xoffset - direction.y * horzx * yoffset - horzz * zoffset),
                (shootingEntity.getPosY() + shootingEntity.getEyeHeight() + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset),
                (shootingEntity.getPosZ() + direction.z * xoffset - direction.y * horzz * yoffset + horzx * zoffset));
        this.setBoundingBox(new AxisAlignedBB(getPosX() - r, getPosY() - 0.0625, getPosZ() - r, getPosX() + r, getPosY() + 0.0625, getPosZ() + r));
    }

    BlockState getBlockState(BlockPos pos, Direction facing) {
        IFluidState ifluidstate = world.getFluidState(pos);
        return MPAObjects.INSTANCE.luxCapacitor.getDefaultState().with(DirectionalBlock.FACING, facing)
                .with(BlockLuxCapacitor.WATERLOGGED, Boolean.valueOf(ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8));
    }

    @Override
    protected void onImpact(RayTraceResult hitResult) {
        if (color == null) {
            color = BlockLuxCapacitor.defaultColor;
        }

        if (this.isAlive() && hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockRayTrace = (BlockRayTraceResult)hitResult;
            Direction dir = blockRayTrace.getFace().getOpposite();
            int x = blockRayTrace.getPos().getX() - dir.getXOffset();
            int y = blockRayTrace.getPos().getY() - dir.getYOffset();
            int z = blockRayTrace.getPos().getZ() - dir.getZOffset();
            if (y > 0) {
                BlockPos blockPos = new BlockPos(x, y, z);
                if (MPAObjects.INSTANCE.luxCapacitor.getDefaultState().isValidPosition(world, blockPos)) {
                    BlockState blockState = getBlockState(blockPos, blockRayTrace.getFace());
                    world.setBlockState(blockPos, blockState);
                    world.setTileEntity(blockPos, new TileEntityLuxCapacitor(color));
                } else {
                    for (Direction facing : Direction.values()) {
                        if (MPAObjects.INSTANCE.luxCapacitor.getDefaultState().with(BlockLuxCapacitor.FACING, facing).isValidPosition(world, blockPos)) {
                            BlockState blockState = getBlockState(blockPos, facing);
                            world.setBlockState(blockPos, blockState);
                            world.setTileEntity(blockPos, new TileEntityLuxCapacitor(color));
                            break;
                        }
                    }
                }
                this.remove();
            }
        }
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravityVelocity() {
        return 0;
    }

    @Override
    protected void registerData() {

    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.ticksExisted > 400) {
            this.remove();
        }
    }

    /**
     * Called by the server when constructing the spawn packet.
     * Data should be added to the provided stream.
     *
     * @param buffer The packet data stream
     */
    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(this.color.getInt());
    }

    /**
     * Called by the client when it receives a Entity spawn packet.
     * Data should be read out of the stream in the same way as it was written.
     *
     * @param additionalData The packet data stream
     */
    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.color = new Colour(additionalData.readInt());
    }
}