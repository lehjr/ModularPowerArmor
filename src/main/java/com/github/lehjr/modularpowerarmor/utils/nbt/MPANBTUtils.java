package com.github.lehjr.modularpowerarmor.utils.nbt;

import com.github.lehjr.modularpowerarmor.client.render.modelspec.DefaultModelSpec;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.tool.ItemPowerFist;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.client.render.modelspec.ModelRegistry;
import com.github.lehjr.mpalib.client.render.modelspec.TexturePartSpec;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Objects;



public class MPANBTUtils {
    public static NBTTagCompound getMuseRenderTag(@Nonnull ItemStack stack, EntityEquipmentSlot armorSlot) {
        NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);
        NBTTagCompound renderTag = new NBTTagCompound();
        if (itemTag.hasKey(MPALIbConstants.TAG_RENDER, Constants.NBT.TAG_COMPOUND))
            renderTag = itemTag.getCompoundTag(MPALIbConstants.TAG_RENDER);
        else if (itemTag.hasKey(MPALIbConstants.TAG_COSMETIC_PRESET, Constants.NBT.TAG_STRING))
            renderTag = MPAConfig.INSTANCE.getPresetNBTFor(stack, itemTag.getString(MPALIbConstants.TAG_COSMETIC_PRESET));

        if (renderTag != null && !renderTag.isEmpty())
            return renderTag;

        // if cosmetic presets are to be used, or powerfist customization is not allowed set to default preset
        if (!MPAConfig.INSTANCE.useLegacyCosmeticSystem() ||
                (stack.getItem() instanceof ItemPowerFist && MPAConfig.INSTANCE.allowPowerFistCustomization())) {
            itemTag.setString(MPALIbConstants.TAG_COSMETIC_PRESET, "Default");
            return MPAConfig.INSTANCE.getPresetNBTFor(stack, "Default");
        }

        renderTag = DefaultModelSpec.makeModelPrefs(stack, armorSlot);
        itemTag.setTag(MPALIbConstants.TAG_RENDER, renderTag);
        return renderTag;
    }

    public static NBTTagCompound getMuseRenderTag(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemPowerArmor)
                return getMuseRenderTag(stack, ((ItemPowerArmor) stack.getItem()).armorType);
            if (stack.getItem() instanceof ItemPowerFist)
                return getMuseRenderTag(stack, EntityEquipmentSlot.MAINHAND);
        }
        return new NBTTagCompound();
    }

}