package com.github.lehjr.modularpowerarmor.client.render;

import com.github.lehjr.modularpowerarmor.item.tool.PowerFist;
import com.github.lehjr.mpalib.basemod.MPALibConstants;
import com.github.lehjr.mpalib.util.capabilities.render.IHandHeldModelSpecNBT;
import com.github.lehjr.mpalib.util.capabilities.render.ModelSpecNBT;
import com.github.lehjr.mpalib.util.capabilities.render.modelspec.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PowerFistSpecNBT extends ModelSpecNBT implements IHandHeldModelSpecNBT {
    public PowerFistSpecNBT(@Nonnull ItemStack itemStackIn) {
        super(itemStackIn);
    }

    @Override
    public CompoundNBT getDefaultRenderTag() {
        if (getItemStack().isEmpty())
            return new CompoundNBT();

        List<CompoundNBT> prefArray = new ArrayList<>();

        // ModelPartSpecs
        ListNBT specList = new ListNBT();

        // TextureSpecBase (only one texture visible at a time)
        CompoundNBT texSpecTag = new CompoundNBT();

        // List of EnumColour indexes
        List<Integer> colours = new ArrayList<>();

        // temp data holder
        CompoundNBT tempNBT;

        EquipmentSlotType slot = getItemStack().getEquipmentSlot();

        for (SpecBase spec : ModelRegistry.getInstance().getSpecs()) {
            // Only generate NBT data from Specs marked as "default"
            if (spec.isDefault()) {
                if (getItemStack().getItem() instanceof PowerFist && spec.getSpecType().equals(EnumSpecType.HANDHELD)) {
                    colours = addNewColourstoList(colours, spec.getColours()); // merge new color int arrays in

                    for (PartSpecBase partSpec : spec.getPartSpecs()) {
                        if (partSpec instanceof ModelPartSpec) {
                            prefArray.add(((ModelPartSpec) partSpec).multiSet(new CompoundNBT(),
                                    getNewColourIndex(colours, spec.getColours(), partSpec.getDefaultColourIndex()),
                                    ((ModelPartSpec) partSpec).getGlow()));
                        }
                    }
                }
            }
        }

        CompoundNBT nbt = new CompoundNBT();
        for (CompoundNBT elem : prefArray) {
            nbt.put(elem.getString(MPALibConstants.TAG_MODEL) + "." + elem.getString(MPALibConstants.TAG_PART), elem);
        }

        if (!specList.isEmpty())
            nbt.put(MPALibConstants.NBT_SPECLIST_TAG, specList);

        if (!texSpecTag.isEmpty())
            nbt.put(MPALibConstants.NBT_TEXTURESPEC_TAG, texSpecTag);

        nbt.put(MPALibConstants.TAG_COLOURS, new IntArrayNBT(colours));
        return nbt;
    }
}
