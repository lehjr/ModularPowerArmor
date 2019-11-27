package com.github.machinemuse.powersuits.utils.modulehelpers;

import com.github.lehjr.mpalib.legacy.item.IModularItem;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class OmniProbeHelper {
    public static final String TAG_EIO_NO_COMPLETE = "eioNoCompete";
    public static final String TAG_EIO_FACADE_TRANSPARENCY = "eioFacadeTransparency";

    public static String getEIONoCompete(@Nonnull ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);
            return itemTag != null ? itemTag.getString(TAG_EIO_NO_COMPLETE) : "";
        }
        return "";
    }

    public static void setEIONoCompete(@Nonnull ItemStack stack, String s) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);
            itemTag.setString(TAG_EIO_NO_COMPLETE, s);
        }
    }

    public static boolean getEIOFacadeTransparency(@Nonnull ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);
            if (itemTag != null) {
                return itemTag.getBoolean(TAG_EIO_FACADE_TRANSPARENCY);
            }
        }
        return false;
    }

    public static void setEIOFacadeTransparency(@Nonnull ItemStack stack, boolean b) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);
            itemTag.setBoolean(TAG_EIO_FACADE_TRANSPARENCY, b);
        }
    }
}