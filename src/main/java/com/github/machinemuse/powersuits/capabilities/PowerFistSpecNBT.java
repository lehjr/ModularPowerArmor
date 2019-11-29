/*
 * ModularPowersuits (Maintenance builds by lehjr)
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

package com.github.machinemuse.powersuits.capabilities;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.render.IHandHeldModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBT;
import com.github.lehjr.mpalib.client.render.modelspec.*;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.github.machinemuse.powersuits.config.MPSConfig;
import com.google.common.collect.BiMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PowerFistSpecNBT extends ModelSpecNBT implements IHandHeldModelSpecNBT {
    public PowerFistSpecNBT(@Nonnull ItemStack itemStackIn) {
        super(itemStackIn);
    }

    @Nullable
    @Override
    public NBTTagCompound getPresetTagOrNull() {
        BiMap<String, NBTTagCompound> presetMap = MPSConfig.INSTANCE.getCosmeticPresets(getItemStack());
        NBTTagCompound itemTag = NBTUtils.getItemTag(getItemStack());
        String presetName = itemTag.getString(MPALIbConstants.TAG_COSMETIC_PRESET);
        if (presetName != null && !presetName.isEmpty()) {
            return presetMap.getOrDefault(presetName, null);
        }
        return null;
    }

    @Override
    public NBTTagCompound getDefaultRenderTag() {
        if (getItemStack().isEmpty()) {
            return new NBTTagCompound();
        }

        List<NBTTagCompound> prefArray = new ArrayList<>();

        // ModelPartSpecs
        NBTTagList specList = new NBTTagList();

        // TextureSpecBase (only one texture visible at a time)
        NBTTagCompound texSpecTag = new NBTTagCompound();

        // List of EnumColour indexes
        List<Integer> colours = new ArrayList<>();

        // temp data holder
        NBTTagCompound tempNBT;

        for (SpecBase spec : ModelRegistry.getInstance().getSpecs()) {
            // Only generate NBT data from Specs marked as "default"
            if (spec.isDefault()) {
                if (spec.getSpecType().equals(EnumSpecType.HANDHELD)) {
                    colours = addNewColourstoList(colours, spec.getColours()); // merge new color int arrays in

                    for (PartSpecBase partSpec : spec.getPartSpecs()) {
                        if (partSpec instanceof ModelPartSpec) {
                            prefArray.add(((ModelPartSpec) partSpec).multiSet(new NBTTagCompound(),
                                    getNewColourIndex(colours, spec.getColours(), partSpec.getDefaultColourIndex()),
                                    ((ModelPartSpec) partSpec).getGlow()));
                        }
                    }
                }
            }
        }

        NBTTagCompound nbt = new NBTTagCompound();
        for (NBTTagCompound elem : prefArray) {
            nbt.setTag(elem.getString(MPALIbConstants.TAG_MODEL) + "." + elem.getString(MPALIbConstants.TAG_PART), elem);
        }

        if (!specList.isEmpty()) {
            nbt.setTag(MPALIbConstants.NBT_SPECLIST_TAG, specList);
        }

        if (!texSpecTag.isEmpty()) {
            nbt.setTag(MPALIbConstants.NBT_TEXTURESPEC_TAG, texSpecTag);
        }

        if (!nbt.isEmpty()) {
            nbt.setTag(MPALIbConstants.TAG_COLOURS, new NBTTagIntArray(colours));
            return nbt;
        } else {
            BiMap<String, NBTTagCompound> presetMap = MPSConfig.INSTANCE.getCosmeticPresets(getItemStack());
            return presetMap.getOrDefault("Default", null);
        }
    }
}