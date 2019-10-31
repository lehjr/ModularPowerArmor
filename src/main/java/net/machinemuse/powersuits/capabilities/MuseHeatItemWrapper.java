package net.machinemuse.powersuits.capabilities;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.heat.HeatStorage;
import com.github.lehjr.mpalib.legacy.module.IModuleManager;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class MuseHeatItemWrapper extends HeatStorage implements INBTSerializable<NBTTagCompound> {
    IModuleManager moduleManager;
    ItemStack container;
    double baseMaxHeat;

    public MuseHeatItemWrapper(@Nonnull ItemStack container, double baseMaxHeat, IModuleManager moduleManagerIn) {
        super(moduleManagerIn.getOrSetModularPropertyDouble(container, MPALIbConstants.MAXIMUM_HEAT) + baseMaxHeat);
        this.container = container;
        this.moduleManager = moduleManagerIn;
        this.baseMaxHeat = baseMaxHeat;
    }

    @Override
    public double receiveHeat(double heatProvided, boolean simulate) {
        if (!canReceive())
            return 0;
        double heatReceived = super.receiveHeat(heatProvided, simulate);
        if (!simulate && heatReceived > 0) {
            NBTTagCompound nbt = serializeNBT();
            if (nbt.hasKey(MPALIbConstants.CURRENT_HEAT, Constants.NBT.TAG_DOUBLE))
                heat = nbt.getDouble(MPALIbConstants.CURRENT_HEAT);
            else
                heat = 0;
            capacity = maxExtract = maxReceive = nbt.getDouble(MPALIbConstants.MAXIMUM_HEAT);
            NBTUtils.setModularItemDoubleOrRemove(container, MPALIbConstants.CURRENT_HEAT, heat);
        }
        return heatReceived;
    }

    @Override
    public double extractHeat(double heatRequested, boolean simulate) {
        if (!canExtract())
            return 0;
        double heatExtracted = super.extractHeat(heatRequested, simulate);
        if (!simulate) {
            NBTTagCompound nbt = serializeNBT();
            if (nbt.hasKey(MPALIbConstants.CURRENT_HEAT, Constants.NBT.TAG_DOUBLE))
                heat = nbt.getDouble(MPALIbConstants.CURRENT_HEAT);
            else
                heat = 0;
            capacity = maxExtract = maxReceive = nbt.getDouble(MPALIbConstants.MAXIMUM_HEAT);
            NBTUtils.setModularItemDoubleOrRemove(container, MPALIbConstants.CURRENT_HEAT, heat);
        }
        return heatExtracted;
    }

    public void updateFromNBT() {
        NBTTagCompound itemNBT = NBTUtils.getMuseItemTag(container);
        NBTTagCompound outNBT = new NBTTagCompound();
        capacity = maxExtract = maxReceive = moduleManager.getOrSetModularPropertyDouble(container, MPALIbConstants.MAXIMUM_HEAT) + baseMaxHeat;
        heat = Math.round(NBTUtils.getDoubleOrZero(itemNBT, MPALIbConstants.CURRENT_HEAT));

        outNBT.setDouble(MPALIbConstants.MAXIMUM_HEAT, capacity);
        if (heat > 0)
            outNBT.setDouble(MPALIbConstants.CURRENT_HEAT, heat);
        deserializeNBT(outNBT);
    }

    // INBTSerializable ---------------------------------------------------------------------------
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        if (heat > 0)
            nbt.setDouble(MPALIbConstants.CURRENT_HEAT, heat);
        nbt.setDouble(MPALIbConstants.MAXIMUM_HEAT, capacity);
        return nbt;
    }

    @Override
    public void deserializeNBT(final NBTTagCompound nbt) {
        if (nbt.hasKey(MPALIbConstants.CURRENT_HEAT, Constants.NBT.TAG_DOUBLE))
            heat = nbt.getDouble(MPALIbConstants.CURRENT_HEAT);
        else
            heat = 0;
        capacity = maxExtract = maxReceive = nbt.getDouble(MPALIbConstants.MAXIMUM_HEAT);
    }
}