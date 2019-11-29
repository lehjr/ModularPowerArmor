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

package com.github.machinemuse.powersuits.client.gui.tinker.module;

import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.client.gui.clickable.ClickableItem;
import com.github.machinemuse.powersuits.client.gui.clickable.ClickableModule;
import com.github.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import com.github.machinemuse.powersuits.client.sound.SoundDictionary;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import net.minecraft.util.SoundCategory;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class ModuleSelectionFrame extends ScrollableFrame {
    protected ItemSelectionFrame target;
    protected Map<String, ModuleSelectionSubFrame> categories = new LinkedHashMap<>();
    protected List<ClickableModule> moduleButtons = new LinkedList<>();
    protected int selectedModule = -1;
    protected IPowerModule prevSelection;
    protected ClickableItem lastItem;
    protected Rect lastPosition;

    public ModuleSelectionFrame(Point2D topleft, Point2D bottomright, Colour borderColour, Colour insideColour, ItemSelectionFrame target) {
        super(topleft, bottomright, borderColour, insideColour);
        this.target = target;
    }

    @Override
    public void update(double mousex, double mousey) {
        super.update(mousex, mousey);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        for (ModuleSelectionSubFrame frame : categories.values()) {
            frame.refreshButtonPositions();
        }
        if (target.getSelectedItem() != null) {
            if (lastItem != target.getSelectedItem()) {
                loadModules(false);
            }
            this.totalsize = 0;
            for (ModuleSelectionSubFrame frame : categories.values()) {
                totalsize = (int) Math.max(frame.border.bottom() - this.border.top(), totalsize);
            }
            this.currentscrollpixels = Math.min(currentscrollpixels, getMaxScrollPixels());

            super.preRender(mouseX, mouseY, partialTicks);
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -currentscrollpixels, 0);
            drawItems();
            drawSelection();
            GL11.glPopMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }

    private void drawItems() {
        for (ModuleSelectionSubFrame frame : categories.values()) {
            frame.drawPartial((int) (this.currentscrollpixels + border.top() + 4),
                    (int) (this.currentscrollpixels + border.top() + border.height() - 4));
        }
    }

    private void drawSelection() {
        ClickableModule module = getSelectedModule();
        if (module != null) {
            Point2D pos = moduleButtons.get(selectedModule).getPosition();
            if (pos.getY() > this.currentscrollpixels + border.top() + 4 && pos.getY() < this.currentscrollpixels + border.top() + border.height() - 4) {
                Renderer.drawCircleAround(pos.getX(), pos.getY(), 10);
            }
        }
    }

    public ClickableModule getSelectedModule() {
        if (moduleButtons.size() > selectedModule && selectedModule != -1) {
            return moduleButtons.get(selectedModule);
        } else {
            return null;
        }
    }

    public void loadModules(boolean preserveSelected) {
        this.lastPosition = null;
        ClickableItem selectedItem = target.getSelectedItem();
        if (selectedItem != null) {
            moduleButtons = new LinkedList<>();
            categories = new LinkedHashMap<>();

            List<IPowerModule> workingModules = ModuleManager.INSTANCE.getValidModulesForItem(selectedItem.getStack());

            // Prune the list of disallowed modules, if not installed on this item.
            for (Iterator<IPowerModule> it = workingModules.iterator(); it.hasNext(); ) {
                IPowerModule module = it.next();
                if (!module.isAllowed() && !ModuleManager.INSTANCE.itemHasModule(selectedItem.getStack(), module.getDataName())) {
                    it.remove();
                }
            }

            if (workingModules.size() > 0) {
                this.selectedModule = -1;
                for (IPowerModule module : workingModules) {
                    ModuleSelectionSubFrame frame = getOrCreateCategory(module.getCategory().getName());
                    ClickableModule moduleClickable = frame.addModule(module);
                    // Indicate installed modules
                    if (!module.isAllowed()) {
                        // If a disallowed module made it to the list, indicate
                        // it as disallowed
                        moduleClickable.setAllowed(false);
                    } else if (ModuleManager.INSTANCE.itemHasModule(selectedItem.getStack(), module.getDataName())) {
                        moduleClickable.setInstalled(true);
                    }
                    if (moduleClickable.getModule().equals(this.prevSelection)) {
                        this.selectedModule = moduleButtons.size();
                    }
                    moduleButtons.add(moduleClickable);
                }
            }
            for (ModuleSelectionSubFrame frame : categories.values()) {
                frame.refreshButtonPositions();
            }
        }
    }

    private ModuleSelectionSubFrame getOrCreateCategory(String category) {
        if (categories.containsKey(category)) {
            return categories.get(category);
        } else {
            RelativeRect position = new RelativeRect(
                    border.left() + 4,
                    border.top() + 4,
                    border.right() - 4,
                    border.top() + 32);
            position.setMeBelow(lastPosition);
            lastPosition = position;
            ModuleSelectionSubFrame frame = new ModuleSelectionSubFrame(
                    category,
                    position);

            categories.put(category, frame);
            return frame;
        }
    }

    @Override
    public boolean onMouseDown(double x, double y, int button) {
        super.onMouseDown(x, y, button);
        if (border.left() < x && border.right() > x && border.top() < y && border.bottom() > y) {
            y += currentscrollpixels;
            // loadModules();
            int i = 0;
            for (ClickableModule module : moduleButtons) {
                if (module.hitBox(x, y)) {
                    Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.BLOCKS, 1, null);
                    selectedModule = i;
                    prevSelection = module.getModule();
                    return true;
                } else {
                    i++;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        if (border.left() < x && border.right() > x && border.top() < y && border.bottom() > y) {
            y += currentscrollpixels;
            if (moduleButtons != null) {
                int moduleHover = -1;
                int i = 0;
                for (ClickableModule module : moduleButtons) {
                    if (module.hitBox(x, y)) {
                        moduleHover = i;
                        break;
                    } else {
                        i++;
                    }
                }
                if (moduleHover > -1) {
                    return moduleButtons.get(moduleHover).getToolTip();
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}