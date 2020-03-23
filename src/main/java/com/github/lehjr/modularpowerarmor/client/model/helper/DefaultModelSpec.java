package com.github.lehjr.modularpowerarmor.client.model.helper;

import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmor;
import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.client.render.modelspec.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 9:11 AM, 29/04/13
 * <p>
 * Ported to Java by lehjr on 11/8/16.
 * rewritten to be custom model compatible by lehjr on 12/26/17
 * <p>
 * Special note: tried forEach() with a filter, but speed was up to 8 times slower
 */

// FIXME: update to respect config settings...


//@OnlyIn(Dist.CLIENT)
public class DefaultModelSpec {
    public static CompoundNBT makeModelPrefs(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemPowerArmor)
                return makeModelPrefs(stack, ((ItemPowerArmor) stack.getItem()).getEquipmentSlot());
            if (stack.getItem() instanceof ItemPowerFist)
                return makeModelPrefs(stack, EquipmentSlotType.MAINHAND);
        }
        return new CompoundNBT();
    }

    public static CompoundNBT makeModelPrefs(@Nonnull ItemStack stack, EquipmentSlotType slot) {
        if (stack.isEmpty())
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

        // here we loop through the registry looking for the default that applies to the ItemStack
        for (SpecBase spec : ModelRegistry.getInstance().getSpecs()) {
            // Only generate NBT data from Specs marked as "default"
            if (spec.isDefault()) {

                /** Power Fist -------------------------------------------------------------------- */
                if (stack.getItem() instanceof ItemPowerFist && spec.getSpecType().equals(EnumSpecType.HANDHELD)) {
                    colours = addNewColourstoList(colours, spec.getColours()); // merge new color int arrays in

                    for (PartSpecBase partSpec : spec.getPartSpecs()) {
                        if (partSpec instanceof ModelPartSpec) {
                            prefArray.add(((ModelPartSpec) partSpec).multiSet(new CompoundNBT(),
                                    getNewColourIndex(colours, spec.getColours(), partSpec.getDefaultColourIndex()),
                                    ((ModelPartSpec) partSpec).getGlow()));
                        }
                    }

                    /** Power Armor ------------------------------------------------------------------- */
                } else if (stack.getItem() instanceof ItemPowerArmor) {
                    colours = addNewColourstoList(colours, spec.getColours()); // merge new color int arrays in

                    // Armor Skin
                    if (spec.getSpecType().equals(EnumSpecType.ARMOR_SKIN) && spec.get(slot.getName()) != null) {
                        // only a single texture per equipment itemSlot can be used at a time
                        texSpecTag = spec.get(slot.getName()).multiSet(new CompoundNBT(),
                                getNewColourIndex(colours, spec.getColours(), spec.get(slot.getName()).getDefaultColourIndex()));
                    }

                    // Armor models
                    else if (spec.getSpecType().equals(EnumSpecType.ARMOR_MODEL) && CommonConfigX.COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS.get()) {

                        for (PartSpecBase partSpec : spec.getPartSpecs()) {
                            if (partSpec.getBinding().getSlot() == slot) {
                                /*
                                // jet pack model not displayed by default
                                if (partSpec.binding.getItemState().equals("all") ||
                                        (partSpec.binding.getItemState().equals("jetpack") &&
                                                ModuleManager.INSTANCE.itemHasModule(stack, MPSModuleConstants.MODULE_JETPACK__DATANAME))) { */
                                    prefArray.add(((ModelPartSpec) partSpec).multiSet(new CompoundNBT(),
                                            getNewColourIndex(colours, spec.getColours(), partSpec.getDefaultColourIndex()),
                                            ((ModelPartSpec) partSpec).getGlow()));
                                /*} */
                            }
                        }
                    }
                }
            }
        }

        CompoundNBT nbt = new CompoundNBT();
        for (CompoundNBT elem : prefArray) {
            nbt.put(elem.getString(MPALIbConstants.TAG_MODEL) + "." + elem.getString(MPALIbConstants.TAG_PART), elem);
        }

        if (!specList.isEmpty())
            nbt.put(MPALIbConstants.NBT_SPECLIST_TAG, specList);

        if (!texSpecTag.isEmpty())
            nbt.put(MPALIbConstants.NBT_TEXTURESPEC_TAG, texSpecTag);

        nbt.put(MPALIbConstants.TAG_COLOURS, new IntArrayNBT(colours));

        return nbt;
    }

    /**
     * When dealing with possibly multiple specs and color lists, new list needs to be created, since there is only one list per item.
     */
    static List<Integer> addNewColourstoList(List<Integer> colours, List<Integer> coloursToAdd) {
        for (Integer i : coloursToAdd) {
            if (!colours.contains(i))
                colours.add(i);
        }
        return colours;
    }

    /**
     * new array means setting a new array index for the same getValue
     */
    public static int getNewColourIndex(List<Integer> colours, List<Integer> oldColours, Integer index) {
        return colours.indexOf(oldColours.get(index != null ? index : 0));
    }
}