package com.github.machinemuse.powersuits.block;

import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.tileentity.MPALibTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.property.IExtendedBlockState;

public class TileEntityLuxCapacitor extends MPALibTileEntity {
    private Colour color;

    public TileEntityLuxCapacitor() {
        this.color = BlockLuxCapacitor.defaultColor;
    }

    public TileEntityLuxCapacitor(Colour colour) {
        this.color = colour;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (color == null)
            color = ((IExtendedBlockState) this.getWorld().getBlockState(this.getPos())).getValue(BlockLuxCapacitor.COLOR);
        if (color == null)
            color = BlockLuxCapacitor.defaultColor;
        nbt.setInteger("c", color.getInt());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("c")) {
            color = new Colour(nbt.getInteger("c"));
        } else {
            MPALibLogger.logDebug("No NBT found! D:");
        }
    }

    public Colour getColor() {
        return color != null ? color : BlockLuxCapacitor.defaultColor;
    }
}
