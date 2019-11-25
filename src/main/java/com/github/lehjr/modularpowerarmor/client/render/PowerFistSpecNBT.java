package com.github.lehjr.modularpowerarmor.client.render;

import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.render.IHandHeldModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBT;
import com.github.lehjr.mpalib.client.render.modelspec.*;
import com.github.lehjr.mpalib.nbt.NBTUtils;
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
        BiMap<String, NBTTagCompound> presetMap = MPAConfig.INSTANCE.getCosmeticPresets(getItemStack());
        NBTTagCompound itemTag = NBTUtils.getMuseItemTag(getItemStack());
        String presetName = itemTag.getString(MPALIbConstants.TAG_COSMETIC_PRESET);
        if (presetName != null) {
            return presetMap.getOrDefault("Default", null);
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
                if (getItemStack().getItem() instanceof ItemPowerFist && spec.getSpecType().equals(EnumSpecType.HANDHELD)) {
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
            BiMap<String, NBTTagCompound> presetMap = MPAConfig.INSTANCE.getCosmeticPresets(getItemStack());
            return presetMap.getOrDefault("Default", null);
        }
    }
}