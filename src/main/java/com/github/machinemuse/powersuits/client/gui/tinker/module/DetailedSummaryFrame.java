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

package com.github.machinemuse.powersuits.client.gui.tinker.module;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.string.StringUtils;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class DetailedSummaryFrame extends ScrollableFrame {
    public static final double SCALEFACTOR = 1;
    protected EntityPlayer player;
    protected int slotPoints;
    protected int energy;
    protected double armor;
    protected ItemSelectionFrame itemSelectionFrame;

    public DetailedSummaryFrame(EntityPlayer player,
                                Point2D topleft,
                                Point2D bottomright,
                                Colour borderColour,
                                Colour insideColour,
                                ItemSelectionFrame itemSelectionFrame) {
        super(topleft.times(1.0 / SCALEFACTOR), bottomright.times(1.0 / SCALEFACTOR), borderColour, insideColour);
        this.player = player;
        this.itemSelectionFrame = itemSelectionFrame;
    }

    @Override
    public void update(double mousex, double mousey) {
        energy = 0;
        armor = 0;
        slotPoints = 0;

        if (itemSelectionFrame.getSelectedItem() != null) {
            slotPoints += (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemSelectionFrame.getSelectedItem().getItem(), MPSModuleConstants.SLOT_POINTS);
        }

        for (ItemStack stack : ItemUtils.getLegacyModularItemsEquipped(player)) {
            energy += (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(stack, MPALIbConstants.MAXIMUM_ENERGY);
//            slotPoints += (int)ModuleManager.INSTANCE.getOrSetModularPropertyDouble(stack, MPSModuleConstants.SLOT_POINTS);
            armor += ModuleManager.INSTANCE.getOrSetModularPropertyDouble(stack, MPSModuleConstants.ARMOR_VALUE_PHYSICAL);
            armor += ModuleManager.INSTANCE.getOrSetModularPropertyDouble(stack, MPSModuleConstants.ARMOR_VALUE_ENERGY);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (player != null) {
            GL11.glPushMatrix();
            GL11.glScaled(SCALEFACTOR, SCALEFACTOR, SCALEFACTOR);
            super.render(mouseX, mouseY, partialTicks);
            int margin = 4;
            int nexty = (int) border.top() + margin;
            Renderer.drawCenteredString(I18n.format("gui.powersuits.equippedTotals"), (border.left() + border.right()) / 2, nexty);
            nexty += 10;

            // Max Energy
            String formattedValue = StringUtils.formatNumberFromUnits(energy, PowerModuleBase.getUnit(MPALIbConstants.MAXIMUM_ENERGY));
            String name = I18n.format("gui.powersuits.energyStorage");
            double valueWidth = Renderer.getStringWidth(formattedValue);
            double allowedNameWidth = border.width() - valueWidth - margin * 2;
            List<String> namesList = StringUtils.wrapStringToVisualLength(name, allowedNameWidth);
            for (int i = 0; i < namesList.size(); i++) {
                Renderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
            }
            Renderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
            nexty += 10 * namesList.size() + 1;

            // Slot points
            if (slotPoints > 0) {
                formattedValue = StringUtils.wrapFormatTags(StringUtils.formatNumberFromUnits(slotPoints, "pts"), StringUtils.FormatCodes.BrightGreen);
                name = I18n.format("gui.powersuits.slotpoints");
                valueWidth = Renderer.getStringWidth(formattedValue);
                allowedNameWidth = border.width() - valueWidth - margin * 2;
                namesList = StringUtils.wrapStringToVisualLength(name, allowedNameWidth);
                assert namesList != null;
                for (int i = 0; i < namesList.size(); i++) {
                    Renderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
                }
                Renderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
                nexty += 10 * namesList.size() + 1;
            }

            // Armor points
            formattedValue = StringUtils.formatNumberFromUnits(armor, "pts");
            name = I18n.format("gui.powersuits.armor");
            valueWidth = Renderer.getStringWidth(formattedValue);
            allowedNameWidth = border.width() - valueWidth - margin * 2;
            namesList = StringUtils.wrapStringToVisualLength(name, allowedNameWidth);
            assert namesList != null;
            for (int i = 0; i < namesList.size(); i++) {
                Renderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
            }
            Renderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);

            GL11.glPopMatrix();
        }
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        return null;
    }
}
