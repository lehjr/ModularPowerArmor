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

import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.github.lehjr.mpalib.nbt.propertymodifier.IPropertyModifier;
import com.github.lehjr.mpalib.nbt.propertymodifier.PropertyModifierLinearAdditiveDouble;
import com.github.lehjr.mpalib.network.MPALibPackets;
import com.github.lehjr.mpalib.string.StringUtils;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.gui.clickable.ClickableItem;
import com.github.machinemuse.powersuits.client.gui.clickable.ClickableTinkerSlider;
import com.github.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.network.packets.TweakRequestDoublePacket;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class ModuleTweakFrame extends ScrollableFrame {
//    protected static double SCALERATIO = 0.75;
    protected static int margin = 4;
    protected ItemSelectionFrame itemTarget;
    protected ModuleSelectionFrame moduleTarget;
    protected List<ClickableTinkerSlider> sliders;
    protected Map<String, Double> propertyStrings;
    protected ClickableTinkerSlider selectedSlider;
    protected EntityPlayer player;

    public ModuleTweakFrame(
            EntityPlayer player,
            Point2D topleft,
            Point2D bottomright,
            Colour borderColour,
            Colour insideColour,
            ItemSelectionFrame itemTarget,
            ModuleSelectionFrame moduleTarget) {
        super(topleft, bottomright, borderColour, insideColour);
        this.itemTarget = itemTarget;
        this.moduleTarget = moduleTarget;
        this.player = player;
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
    }

    @Override
    public void update(double mousex, double mousey) {
//        mousex /= SCALERATIO;
        if (itemTarget.getSelectedItem() != null && moduleTarget.getSelectedModule() != null) {
            ItemStack stack = itemTarget.getSelectedItem().getStack();
            IPowerModule module = moduleTarget.getSelectedModule().getModule();
            if (ModuleManager.INSTANCE.itemHasModule(itemTarget.getSelectedItem().getStack(), moduleTarget.getSelectedModule().getModule().getDataName())) {
                loadTweaks(stack, module);
            } else {
                sliders = null;
                propertyStrings = null;
            }
        } else {
            sliders = null;
            propertyStrings = null;
        }
        if (selectedSlider != null) {
            selectedSlider.setValueByX(mousex);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (sliders != null) {
//            GL11.glPushMatrix();
//            GL11.glScaled(SCALERATIO, SCALERATIO, SCALERATIO);
            super.render(mouseX, mouseY, partialTicks);
            Renderer.drawCenteredString("Tinker", (border.left() + border.right()) / 2, border.top() + 2);
            for (ClickableTinkerSlider slider : sliders) {
                slider.render(mouseX, mouseY, partialTicks);
            }
            int nexty = (int) (sliders.size() * 20 + border.top() + 23);
            for (Map.Entry<String, Double> property : propertyStrings.entrySet()) {
                String formattedValue = StringUtils.formatNumberFromUnits(property.getValue(), PowerModuleBase.getUnit(property.getKey()));
                String name = property.getKey();
                double valueWidth = Renderer.getStringWidth(formattedValue);
                double allowedNameWidth = border.width() - valueWidth - margin * 2;

                List<String> namesList = StringUtils.wrapStringToVisualLength(
                        I18n.format(MPSModuleConstants.MODULE_TRADEOFF_PREFIX + name), allowedNameWidth);
                for (int i = 0; i < namesList.size(); i++) {
                    Renderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
                }
                Renderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
                nexty += 9 * namesList.size() + 1;

            }
//            GL11.glPopMatrix();
        }
    }

    private void loadTweaks(ItemStack stack, IPowerModule module) {
        NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);
        NBTTagCompound moduleTag = itemTag.getCompoundTag(module.getDataName());

        propertyStrings = new HashMap();
        Set<String> tweaks = new HashSet<String>();

        Map<String, List<IPropertyModifier>> propertyModifiers = module.getPropertyModifiers();
        for (Map.Entry<String, List<IPropertyModifier>> property : propertyModifiers.entrySet()) {
            double currValue = 0;
            for (IPropertyModifier modifier : property.getValue()) {
                currValue = (double) modifier.applyModifier(moduleTag, currValue);
                if (modifier instanceof PropertyModifierLinearAdditiveDouble) {
                    tweaks.add(((PropertyModifierLinearAdditiveDouble) modifier).getTradeoffName());
                }
            }
            propertyStrings.put(property.getKey(), currValue);
        }

        sliders = new LinkedList();
        int y = 0;
        for (String tweak : tweaks) {
            y += 20;
            Point2D center = new Point2D((border.left() + border.right()) / 2, border.top() + y);
            ClickableTinkerSlider slider = new ClickableTinkerSlider(
                    center,
                    border.right() - border.left() - 8,
                    moduleTag,
                    tweak, I18n.format(MPSModuleConstants.MODULE_TRADEOFF_PREFIX + tweak));
            sliders.add(slider);
            if (selectedSlider != null && slider.hitBox(center.getX(), center.getY())) {
                selectedSlider = slider;
            }
        }
    }

    @Override
    public boolean onMouseDown(double x, double y, int button) {
        boolean handled = false;
        if (button == 0) {
            if (sliders != null) {
                for (ClickableTinkerSlider slider : sliders) {
                    if (slider.hitBox(x, y)) {
                        selectedSlider = slider;
                        handled = true;
                        break;
                    }
                }
            }
        }
        return handled;
    }

    @Override
    public boolean onMouseUp(double x, double y, int button) {
        boolean handled = false;
        if (selectedSlider != null && itemTarget.getSelectedItem() != null && moduleTarget.getSelectedModule() != null) {
            ClickableItem item = itemTarget.getSelectedItem();
            IPowerModule module = moduleTarget.getSelectedModule().getModule();
            MPALibPackets.INSTANCE.sendToServer(
                    new TweakRequestDoublePacket(item.inventorySlot, module.getDataName(), selectedSlider.id(), selectedSlider.getValue()));
            handled = true;
        }
        if (button == 0) {
            selectedSlider = null;
            handled = true;
        }
        return handled;
    }
}