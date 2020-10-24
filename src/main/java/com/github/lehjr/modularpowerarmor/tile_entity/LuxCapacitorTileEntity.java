package com.github.lehjr.modularpowerarmor.tile_entity;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.mpalib.util.math.Colour;
import com.github.lehjr.mpalib.util.tileentity.MPALibTileEntity;

public class LuxCapacitorTileEntity extends MPALibTileEntity {
    private Colour color = Colour.CYAN;

    public LuxCapacitorTileEntity() {
        super(MPAObjects.LUX_CAP_TILE_TYPE.get());
        this.color = /*LuxCapacitorBlock.defaultColor*/ Colour.CYAN;
    }

    public LuxCapacitorTileEntity(Colour colour) {
        super(MPAObjects.LUX_CAP_TILE_TYPE.get());
        this.color = colour;
    }

    public void setColor(Colour colour) {
        this.color = colour;
    }

//    @Override
//    public CompoundNBT write(CompoundNBT nbt) {
//        super.write(nbt);
//        if (color == null)
//            color = LuxCapacitorBlock.defaultColor;
//        nbt.putInt("c", color.getInt());
//        return nbt;
//    }
//
//    @Nonnull
//    @Override
//    public IModelData getModelData() {
//        return LuxCapHelper.getModelData(getColor().getInt());
//    }
//
//    @Override
//    public void read(CompoundNBT nbt) {
//        super.read(nbt);
//        if (nbt.contains("c")) {
//            color = new Colour(nbt.getInt("c"));
//        } else {
//            MPALibLogger.logger.debug("No NBT found! D:");
//        }
//    }
//
//    public Colour getColor() {
//        return color != null ? color : LuxCapacitorBlock.defaultColor;
//    }
}
