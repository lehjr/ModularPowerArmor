package com.github.lehjr.modularpowerarmor.client.render;


import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.render.IArmorModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBT;
import com.github.lehjr.mpalib.client.render.modelspec.*;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ArmorModelSpecNBT extends ModelSpecNBT implements IArmorModelSpecNBT {
    public ArmorModelSpecNBT(@Nonnull ItemStack itemStackIn) {
        super(itemStackIn);
    }

    @Override
    public EnumSpecType getSpecType() {
        NBTTagCompound itemTag = NBTUtils.getMuseItemTag(getItemStack());
        NBTTagCompound renderTag = itemTag.getCompoundTag(MPALIbConstants.TAG_RENDER);
        if (renderTag == null || renderTag.isEmpty()) {
              renderTag = getDefaultRenderTag();
        }

        try {
            TexturePartSpec partSpec = (TexturePartSpec) ModelRegistry.getInstance().getPart(renderTag.getCompoundTag(MPALIbConstants.NBT_TEXTURESPEC_TAG));
            if (partSpec != null) {
                return EnumSpecType.ARMOR_SKIN;
            }
        } catch (Exception ignored) {
        }

        for (String key : renderTag.getKeySet()) {
            if (key.equals("colours")) {
                continue;
            }
            if (renderTag.getTag(key) instanceof NBTTagCompound) {
                SpecBase testSpec = ModelRegistry.getInstance().getModel(renderTag.getCompoundTag(key));
                if (testSpec instanceof ModelSpec) {
                    return EnumSpecType.ARMOR_MODEL;
                }
            }
        }
        return EnumSpecType.NONE;
    }

    @Override
    public NBTTagCompound getDefaultRenderTag() {
        if (getItemStack().isEmpty())
            return new NBTTagCompound();

        List<NBTTagCompound> prefArray = new ArrayList<>();

        // ModelPartSpecs
        NBTTagList specList = new NBTTagList();

        // TextureSpecBase (only one texture visible at a time)
        NBTTagCompound texSpecTag = new NBTTagCompound();

        // List of EnumColour indexes
        List<Integer> colours = new ArrayList<>();

        // temp data holder
        NBTTagCompound tempNBT;

        EntityEquipmentSlot slot = getItemStack().getEquipmentSlot();
        if (slot == null) {
            slot = EntityMob.getSlotForItemStack(getItemStack());
        }

        for (SpecBase spec : ModelRegistry.getInstance().getSpecs()) {
            // Only generate NBT data from Specs marked as "default"
            if (spec.isDefault()) {
                if (getItemStack().getItem() instanceof ItemArmor) {
                    colours = addNewColourstoList(colours, spec.getColours()); // merge new color int arrays in

                    // Armor Skin
                    if (spec.getSpecType().equals(EnumSpecType.ARMOR_SKIN) && spec.get(slot.getName()) != null) {
                        // only a single texture per equipment slot can be used at a time
                        texSpecTag = spec.get(slot.getName()).multiSet(new NBTTagCompound(),
                                getNewColourIndex(colours, spec.getColours(), spec.get(slot.getName()).getDefaultColourIndex()));
                    }

                    // Armor models
                    else if (spec.getSpecType().equals(EnumSpecType.ARMOR_MODEL) && MPAConfig.COSMETIC_ALLOW_HIGH_POLLY_ARMOR_MODELS.get()) {
                        for (PartSpecBase partSpec : spec.getPartSpecs()) {
                            if (partSpec.getBinding().getSlot() == slot) {
                                /*
                                // jet pack model not displayed by default
                                if (partSpec.binding.getItemState().equals("all") ||
                                        (partSpec.binding.getItemState().equals("jetpack") &&
                                                ModuleManager.INSTANCE.itemHasModule(stack, MPSModuleConstants.MODULE_JETPACK__REGNAME))) { */
                                prefArray.add(((ModelPartSpec) partSpec).multiSet(new NBTTagCompound(),
                                        getNewColourIndex(colours, spec.getColours(), partSpec.getDefaultColourIndex()),
                                        ((ModelPartSpec) partSpec).getGlow()));
                                /*} */
                            }
                        }
                    }
                }
            }
        }

        NBTTagCompound nbt = new NBTTagCompound();
        for (NBTTagCompound elem : prefArray) {
            nbt.setTag(elem.getString(MPALIbConstants.TAG_MODEL) + "." + elem.getString(MPALIbConstants.TAG_PART), elem);
        }

        if (!specList.isEmpty())
            nbt.setTag(MPALIbConstants.NBT_SPECLIST_TAG, specList);

        if (!texSpecTag.isEmpty())
            nbt.setTag(MPALIbConstants.NBT_TEXTURESPEC_TAG, texSpecTag);

        nbt.setTag(MPALIbConstants.TAG_COLOURS, new NBTTagIntArray(colours));
        return nbt;
    }

    @Override
    public String getArmorTexture() {
        NBTTagCompound itemTag = NBTUtils.getMuseItemTag(getItemStack());
        NBTTagCompound renderTag = itemTag.getCompoundTag(MPALIbConstants.TAG_RENDER);
        try {
            TexturePartSpec partSpec = (TexturePartSpec) ModelRegistry.getInstance().getPart(renderTag.getCompoundTag(MPALIbConstants.NBT_TEXTURESPEC_TAG));
            return partSpec.getTextureLocation();
        } catch (Exception ignored) {
            return MPALIbConstants.BLANK_ARMOR_MODEL_PATH;
        }
    }
}
