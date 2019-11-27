/*
 * Copyright (c) ${DATE} MachineMuse, Lehjr
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

import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import java.util.ArrayList;
import java.util.List;

public class ItemHandlerPowerFist extends ItemStackHandler {
    // Scannable
    public static final int SCANNER_ACTIVE_MODULE_COUNT = 3;
    public static final int SCANNER_TOTAL_MODULE_COUNT = 9;
    public static final int TOTAL_SIZE = SCANNER_TOTAL_MODULE_COUNT + 10;
    private static final String TAG_ITEMS = "items";
    private final ItemStack container;


    public ItemHandlerPowerFist(final ItemStack container) {
        super(TOTAL_SIZE);
        this.container = container;

    }

    public IItemHandler getActiveModules() {
        return new RangedWrapper(this, 0, SCANNER_ACTIVE_MODULE_COUNT);
    }

    public IItemHandler getInactiveModules() {
        return new RangedWrapper(this, SCANNER_ACTIVE_MODULE_COUNT, SCANNER_TOTAL_MODULE_COUNT);
    }

    // Store the emulated tools in an inventory. This won't be accessable by the player.
    public IItemHandler getEmulatedTools() {
        return new RangedWrapper(this, SCANNER_TOTAL_MODULE_COUNT, TOTAL_SIZE);
    }

    public void updateFromNBT() {
        final NBTTagCompound itemTag = NBTUtils.getMuseItemTag(container);

        // TODO: edit to hold other things like emulated tools
        if (itemTag != null && itemTag.hasKey(TAG_ITEMS, Constants.NBT.TAG_COMPOUND)) {
            deserializeNBT((NBTTagCompound) itemTag.getTag(TAG_ITEMS));
            if (stacks.size() != TOTAL_SIZE) {
                final List<ItemStack> oldStacks = new ArrayList<>(stacks);
                setSize(TOTAL_SIZE);
                final int count = Math.min(TOTAL_SIZE, oldStacks.size());
                for (int slot = 0; slot < count; slot++) {
                    stacks.set(slot, oldStacks.get(slot));
                }
            }
        }
    }

    @Override
    protected void onContentsChanged(final int slot) {
        super.onContentsChanged(slot);
        NBTTagCompound itemTag = NBTUtils.getMuseItemTag(container);
        itemTag.setTag(TAG_ITEMS, serializeNBT());
    }
}