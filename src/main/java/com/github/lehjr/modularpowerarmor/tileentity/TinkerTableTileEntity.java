package com.github.lehjr.modularpowerarmor.tileentity;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.tileentity.MPALibTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

/**
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 10/21/16.
 */
public class TinkerTableTileEntity extends MPALibTileEntity {
    Direction facing;

    public TinkerTableTileEntity() {
        super(MPAObjects.tinkerTableTileEntityType);
        this.facing = Direction.NORTH;
    }

    public TinkerTableTileEntity(Direction facing) {
        super(MPAObjects.tinkerTableTileEntityType);
        this.facing = facing;
    }

    public Direction getFacing() {
        return (this.facing != null) ? this.facing : Direction.NORTH;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        nbt.putInt("f", facing.ordinal());
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        if (nbt.contains("f")) {
            facing = Direction.values()[nbt.getInt("f")];
        } else {
            MPALibLogger.logger.debug("No NBT found! D:");
        }
    }
}