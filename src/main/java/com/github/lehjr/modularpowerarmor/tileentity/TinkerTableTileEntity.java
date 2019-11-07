package com.github.lehjr.modularpowerarmor.tileentity;

import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.tileentity.MPALibTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

/**
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 10/21/16.
 */
public class TinkerTableTileEntity extends MPALibTileEntity {
    EnumFacing facing;

    public TinkerTableTileEntity() {
        this.facing = EnumFacing.NORTH;
    }

    public TinkerTableTileEntity(EnumFacing facing) {
        this.facing = facing;
    }

    public EnumFacing getFacing() {
        return (this.facing != null) ? this.facing : EnumFacing.NORTH;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("f", facing.ordinal());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("f")) {
            facing = EnumFacing.values()[nbt.getInteger("f")];
        } else {
            MPALibLogger.logDebug("No NBT found! D:");
        }
    }
}