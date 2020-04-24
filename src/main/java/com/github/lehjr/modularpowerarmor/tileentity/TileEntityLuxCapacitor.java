package com.github.lehjr.modularpowerarmor.tileentity;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.modularpowerarmor.block.BlockLuxCapacitor;
import com.github.lehjr.modularpowerarmor.client.model.helper.LuxCapHelper;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.tileentity.MPALibTileEntity;
import com.google.common.collect.ImmutableMap;
import forge.OBJPartData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;

public class TileEntityLuxCapacitor extends MPALibTileEntity {
    private Colour color;

    public TileEntityLuxCapacitor() {
        super(MPAObjects.capacitorTileEntityType);
        this.color = BlockLuxCapacitor.defaultColor;
    }

    public TileEntityLuxCapacitor(Colour colour) {
        super(MPAObjects.capacitorTileEntityType);
        this.color = colour;
    }

    public void setColor(Colour colour) {
        this.color = colour;
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        if (color == null)
            color = BlockLuxCapacitor.defaultColor;
        nbt.putInt("c", color.getInt());
        return nbt;
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        return LuxCapHelper.getModelData(getColor().getInt());
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        if (nbt.contains("c")) {
            color = new Colour(nbt.getInt("c"));
        } else {
            MPALibLogger.logger.debug("No NBT found! D:");
        }
    }

    public Colour getColor() {
        return color != null ? color : BlockLuxCapacitor.defaultColor;
    }
}
