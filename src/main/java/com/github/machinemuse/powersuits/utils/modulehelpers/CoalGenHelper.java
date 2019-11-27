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

package com.github.machinemuse.powersuits.utils.modulehelpers;

import com.github.lehjr.mpalib.legacy.item.IModularItem;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public class CoalGenHelper {
    public static final String TAG_COAL = "Coal";

    public static int getCoalLevel(@Nonnull ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);
            Integer coalLevel = itemTag.getInteger(TAG_COAL);
            if (coalLevel != null) {
                return coalLevel;
            }
        }
        return 0;
    }

    public static void setCoalLevel(@Nonnull ItemStack stack, int i) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModularItem) {
            NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);
            itemTag.setInteger(TAG_COAL, i);
        }
    }
}
