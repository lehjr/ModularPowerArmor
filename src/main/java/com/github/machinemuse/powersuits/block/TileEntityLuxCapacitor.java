/*
 * Copyright (c) 2019 MachineMuse, Lehjr
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
