package net.machinemuse.powersuits.tileentity;

import net.machinemuse.numina.basemod.MuseLogger;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.tileentity.MuseTileEntity;
import net.machinemuse.powersuits.basemod.MPSObjects;
import net.machinemuse.powersuits.block.BlockLuxCapacitor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;

public class TileEntityLuxCapacitor extends MuseTileEntity {
    private Colour color;

    public TileEntityLuxCapacitor() {
        super(MPSObjects.capacitorTileEntityType);
        this.color = BlockLuxCapacitor.defaultColor;
    }

    public TileEntityLuxCapacitor(Colour colour) {
        super(MPSObjects.capacitorTileEntityType);
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
        return new ModelDataMap.Builder().withInitial(BlockLuxCapacitor.COLOUR_PROP, color.getInt()).build();
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        if (nbt.contains("c")) {
            color = new Colour(nbt.getInt("c"));
        } else {
            MuseLogger.logger.debug("No NBT found! D:");
        }
    }

    public Colour getColor() {
        return color != null ? color : BlockLuxCapacitor.defaultColor;
    }
}
