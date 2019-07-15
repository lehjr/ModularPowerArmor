package net.machinemuse.powersuits.entity;

import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.basemod.MPSObjects;
import net.machinemuse.powersuits.block.BlockLuxCapacitor;
import net.machinemuse.powersuits.tileentity.TileEntityLuxCapacitor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class LuxCapacitorEntity extends ThrowableEntity implements IEntityAdditionalSpawnData {
    public Colour color;

    public LuxCapacitorEntity(EntityType<? extends LuxCapacitorEntity> entityType, World world) {
        super(entityType, world);
        if (color == null)
            color = Colour.WHITE;
    }

    public LuxCapacitorEntity(World world, LivingEntity shootingEntity, Colour color) {
        super(MPSObjects.LUX_CAPACITOR_ENTITY_TYPE, shootingEntity, world);
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
        this.posX = shootingEntity.posX + direction.x * xoffset - direction.y * horzx * yoffset - horzz * zoffset;
        this.posY = shootingEntity.posY + shootingEntity.getEyeHeight() + direction.y * xoffset + (1 - Math.abs(direction.y)) * yoffset;
        this.posZ = shootingEntity.posZ + direction.z * xoffset - direction.y * horzz * yoffset + horzx * zoffset;
        this.setBoundingBox(new AxisAlignedBB(posX - r, posY - 0.0625, posZ - r, posX + r, posY + 0.0625, posZ + r));
    }

    BlockItemUseContext getUseContext(BlockPos pos, Direction facing, BlockRayTraceResult hitResult) {
        return new BlockItemUseContext(
                new ItemUseContext(
                        (PlayerEntity)this.getThrower(),
                        this.getThrower().getActiveHand(),
                        hitResult));
    }

    @Override
    protected void onImpact(RayTraceResult hitResult) {
        if (color == null)
            color = Colour.WHITE;

        if (this.isAlive() && hitResult.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockRayTrace = (BlockRayTraceResult)hitResult;
            Direction dir = blockRayTrace.getFace().getOpposite();
            int x = blockRayTrace.getPos().getX() - dir.getXOffset();
            int y = blockRayTrace.getPos().getY() - dir.getYOffset();
            int z = blockRayTrace.getPos().getZ() - dir.getZOffset();
            if (y > 0) {
                BlockPos blockPos = new BlockPos(x, y, z);
                if (MPSObjects.INSTANCE.luxCapacitor.getDefaultState().isValidPosition(world, blockPos)) {
                    BlockState blockState = MPSObjects.INSTANCE.luxCapacitor.getStateForPlacement(getUseContext(blockPos, blockRayTrace.getFace(), blockRayTrace));
                    world.setBlockState(blockPos, blockState);
                    world.setTileEntity(blockPos, new TileEntityLuxCapacitor(color));
                } else {
                    for (Direction facing : Direction.values()) {
                        if (MPSObjects.INSTANCE.luxCapacitor.getDefaultState().with(BlockLuxCapacitor.FACING, facing).isValidPosition(world, blockPos)) {
                            BlockState blockState = MPSObjects.INSTANCE.luxCapacitor.getStateForPlacement(getUseContext(blockPos, facing, blockRayTrace));
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

    /* using these to sync color between client and server, since without this, color isn't initialized */
    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        if (color == null)
            color = Colour.WHITE;
        buffer.writeInt(color.getInt());
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        this.color = new Colour(additionalData.readInt());
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
    public void tick() {
        super.tick();
        if (this.ticksExisted > 400) {
            this.remove();
        }
    }
}