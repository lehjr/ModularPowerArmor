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
            NBTTagCompound itemTag = NBTUtils.getItemTag(stack);
            return itemTag != null ? itemTag.getString(TAG_EIO_NO_COMPLETE) : "";
        }
        return "";
    }

    public static void setEIONoCompete(@Nonnull ItemStack stack, String s) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            NBTTagCompound itemTag = NBTUtils.getItemTag(stack);
            itemTag.setString(TAG_EIO_NO_COMPLETE, s);
        }
    }

    public static boolean getEIOFacadeTransparency(@Nonnull ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            NBTTagCompound itemTag = NBTUtils.getItemTag(stack);
            if (itemTag != null) {
                return itemTag.getBoolean(TAG_EIO_FACADE_TRANSPARENCY);
            }
        }
        return false;
    }

    public static void setEIOFacadeTransparency(@Nonnull ItemStack stack, boolean b) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            NBTTagCompound itemTag = NBTUtils.getItemTag(stack);
            itemTag.setBoolean(TAG_EIO_FACADE_TRANSPARENCY, b);
        }
    }
}