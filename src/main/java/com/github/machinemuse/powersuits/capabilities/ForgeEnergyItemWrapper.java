/*
 * Copyright (c) ${DATE} MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.capabilities;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.legacy.module.IModuleManager;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nonnull;

/**
 * Note that this class does not update any NBT tag data itself, but rather is just part of a wrapper for
 * power storage devices in the item's inventory
 */
public class ForgeEnergyItemWrapper extends EnergyStorage implements INBTSerializable<NBTTagCompound> {
    ItemStack container;
    IModuleManager moduleManager;

    /**
     * TODO: need to set an NNBT tag for the max getValue instead of recalculating over and over.
     *
     * @param container
     * @param moduleManagerIn
     */
    public ForgeEnergyItemWrapper(@Nonnull ItemStack container, IModuleManager moduleManagerIn) {
        super((int) moduleManagerIn.getOrSetModularPropertyDouble(container, MPALIbConstants.MAXIMUM_ENERGY));
        this.moduleManager = moduleManagerIn;
        this.container = container;
    }

    @Override
    public int receiveEnergy(int energyProvided, boolean simulate) {
        if (!canReceive())
            return 0;

        int energyReceived = super.receiveEnergy(energyProvided, simulate);

        if (!simulate && energyReceived > 0) {
            NBTTagCompound nbt = serializeNBT();
            if (nbt.hasKey(MPALIbConstants.CURRENT_ENERGY, Constants.NBT.TAG_INT))
                energy = nbt.getInteger(MPALIbConstants.CURRENT_ENERGY);
            else
                energy = 0;
            NBTUtils.setModularItemDoubleOrRemove(container, MPALIbConstants.CURRENT_ENERGY, energy); // TODO: switch to int
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int energyRequested, boolean simulate) {
        if (!canExtract())
            return 0;

        int energyExtracted = super.extractEnergy(energyRequested, simulate);

        if (!simulate && energyExtracted > 0) {
            NBTTagCompound nbt = serializeNBT();
            if (nbt.hasKey(MPALIbConstants.CURRENT_ENERGY, Constants.NBT.TAG_INT))
                energy = nbt.getInteger(MPALIbConstants.CURRENT_ENERGY);
            else
                energy = 0;
            NBTUtils.setModularItemDoubleOrRemove(container, MPALIbConstants.CURRENT_ENERGY, energy); // TODO: switch to int
        }
        return energyExtracted;
    }

    public void updateFromNBT() {
        capacity = maxExtract = maxReceive = (int) moduleManager.getOrSetModularPropertyDouble(container, MPALIbConstants.MAXIMUM_ENERGY);
        energy = Math.min(capacity, (int) Math.round(NBTUtils.getModularItemDoubleOrZero(container, MPALIbConstants.CURRENT_ENERGY)));
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        if (energy > 0)
            nbt.setInteger(MPALIbConstants.CURRENT_ENERGY, energy);
        nbt.setInteger(MPALIbConstants.MAXIMUM_ENERGY, capacity);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt.hasKey(MPALIbConstants.CURRENT_ENERGY, Constants.NBT.TAG_INT))
            energy = nbt.getInteger(MPALIbConstants.CURRENT_ENERGY);
        else
            energy = 0;
        capacity = maxExtract = maxReceive = nbt.getInteger(MPALIbConstants.MAXIMUM_ENERGY);
    }
}