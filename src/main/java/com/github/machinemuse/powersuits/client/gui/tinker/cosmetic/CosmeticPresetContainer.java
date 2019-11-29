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

package com.github.machinemuse.powersuits.client.gui.tinker.cosmetic;

import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import com.github.machinemuse.powersuits.config.MPSConfig;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CosmeticPresetContainer extends ScrollableFrame {
    public ItemSelectionFrame itemSelect;
    public ColourPickerFrame colourSelect;
    public Point2D topleft;
    public Point2D bottomright;
    public Integer lastItemSlot;
    public List<CosmeticPresetSelectionSubframe> presetFrames;
    protected boolean enabled;
    protected boolean visibile;

    public CosmeticPresetContainer(ItemSelectionFrame itemSelect,
                                   ColourPickerFrame colourSelect,
                                   Point2D topleft,
                                   Point2D bottomright,
                                   Colour borderColour,
                                   Colour insideColour) {
        super(topleft, bottomright, borderColour, insideColour);
        this.itemSelect = itemSelect;
        this.colourSelect = colourSelect;
        this.topleft = topleft;
        this.bottomright = bottomright;
        this.lastItemSlot = null;
        this.presetFrames = getPresetFrames();
        this.visibile = true;
        this.enabled = true;
    }

    @Nonnull
    public ItemStack getItem() {
        return (itemSelect.getSelectedItem() != null) ? itemSelect.getSelectedItem().getStack() : ItemStack.EMPTY;
    }

    @Nullable
    public Integer getItemSlot() {
        return (itemSelect.getSelectedItem() != null) ? itemSelect.getSelectedItem().inventorySlot : null;
    }

    public List<CosmeticPresetSelectionSubframe> getPresetFrames() {
        List<CosmeticPresetSelectionSubframe> cosmeticFrameList = new ArrayList<>();
        CosmeticPresetSelectionSubframe newFrame;
        CosmeticPresetSelectionSubframe prev = null;
        for (String name :  MPSConfig.INSTANCE.getCosmeticPresets(getItem()).keySet()) {
            newFrame = createNewFrame(name, prev);
            prev = newFrame;
            cosmeticFrameList.add(newFrame);
        }
        return cosmeticFrameList;
    }

    public CosmeticPresetSelectionSubframe createNewFrame(String label, CosmeticPresetSelectionSubframe prev) {
        RelativeRect newborder = new RelativeRect(this.border.finalLeft() + 8, this.border.finalTop() + 10, this.border.finalRight(), this.border.finalTop() + 24);
        newborder.setMeBelow((prev != null) ? prev.border : null);
        return new CosmeticPresetSelectionSubframe(label, new Point2D(newborder.finalLeft(), newborder.centery()),  this.itemSelect, newborder);
    }

    @Override
    public boolean onMouseDown(double x, double y, int button) {
        if (enabled) {
            if (button == 0) {
                for (CosmeticPresetSelectionSubframe frame : presetFrames) {
                    if (frame.hitbox(x, y))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public void update(double mouseX, double mouseY) {
        super.update(mouseX, mouseY);

        if (enabled) {
            if (!Objects.equals(lastItemSlot, getItemSlot())) {
                lastItemSlot = getItemSlot();

                presetFrames = getPresetFrames();
                double x = 0;
                for (CosmeticPresetSelectionSubframe subframe : presetFrames) {
//                subframe.updateItems();
                    x += subframe.border.finalBottom();
                }
                this.totalsize = (int) x;
//        }
                if (colourSelect.decrAbove > -1) {
//            decrAbove(colourSelect.decrAbove);
                    colourSelect.decrAbove = -1;
                }
            }
        }
    }

    public void hide () {
        visibile = false;
    }

    public void show() {
        visibile = true;
    }

    public boolean isVisibile() {
        return visibile;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (visibile) {
            super.preRender(mouseX, mouseY, partialTicks);
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, (double) (-this.currentscrollpixels), 0.0);
            for (CosmeticPresetSelectionSubframe f : presetFrames) {
                f.render(mouseX, mouseY, partialTicks);
            }
            GL11.glPopMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }
}
