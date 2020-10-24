package com.github.lehjr.modularpowerarmor.client.gui.tinker.module;

import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.client.sound.SoundDictionary;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ModuleSelectionSubFrame {
    protected List<ClickableModule> moduleButtons;
    protected RelativeRect border;
    protected EnumModuleCategory category;

    public ModuleSelectionSubFrame(EnumModuleCategory category, RelativeRect border) {
        this.category = category;
        this.border = border;
        this.moduleButtons = new ArrayList<>();
    }

    public ClickableModule addModule(@Nonnull ItemStack module, int index) {
        ClickableModule clickie = new ClickableModule(module, new Point2F(0, 0), index, category);
        this.moduleButtons.add(clickie);
         refreshButtonPositions();
        return clickie;
    }

    public ClickableModule replaceModule(@Nonnull ItemStack module, int index) {
        int moduleIndex = -1;
        Point2F position = null;
        for (ClickableModule clickie : moduleButtons) {
            if (module.isItemEqual(clickie.getModule())) {
                moduleIndex = moduleButtons.indexOf(clickie);
                position = clickie.getPosition();
                break;
            }
        }
        if (moduleIndex != -1 && position != null) {
            ClickableModule clickie = new ClickableModule(module, position, index, category);
            clickie.setInstalled((index >= 0));
            moduleButtons.set(moduleIndex, clickie);
            return clickie;
        }
        return addModule(module, index);
    }

    public void drawPartial(int min, int max, float partialTicks, float zLevel) {
        refreshButtonPositions();
        double top = border.top();
        Renderer.drawString(this.category.getName(), border.left(), top);
        for (ClickableModule clickie : moduleButtons) {
            clickie.render(min, max, partialTicks, zLevel);
        }
    }

    public void refreshModules(@Nonnull ItemStack selectedItem) {
        selectedItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h->{
            if (h instanceof IModularItem) {
                for(ClickableModule module: moduleButtons) {
                    boolean markForUpdate = false;
                    int slot = -1;
                    for (int i = 0; i < h.getSlots(); i++) {
                        if (h.getStackInSlot(i).isItemEqual(module.getModule())) {
                            slot = i;
                            if(!ItemStack.areItemStacksEqual(h.getStackInSlot(i), module.getModule())) {
                                markForUpdate = true;
                            }
                        }
                    }

                    // installed but not showing
                    if (module.isInstalled() && slot == -1) {
                        ItemStack newModule = new ItemStack(module.getModule().getItem());
                        this.replaceModule(newModule, slot);

                    // not istalled but showing as installed or installed but itemstack does not match
                    } else if (!module.isInstalled() && slot != -1 || markForUpdate) {
                        ItemStack newModule = h.getStackInSlot(slot);
                        this.replaceModule(newModule, slot);
                    }
                }
            }
        });
    }

    public void refreshButtonPositions() {
        int col = 0, row = 0;
        int topMargin = 20;
        int leftMargin = 10;

        for (ClickableModule clickie : moduleButtons) {
            if (col > 4) {
                col = 0;
                row++;
            }
            float x = border.left() + leftMargin + 20 * col;
            float y = border.top() + topMargin + 20 * row;
            clickie.setPosition(new Point2F(x, y));
            col++;
        }
        // top and bottom margin + iconHeight * rows
        border.setHeight(topMargin * 2 + 20 * row);
    }

    int selectedModule = -1;

    public ClickableModule getSelectedModule() {
        if (selectedModule >=0)
            return moduleButtons.get(selectedModule);
        return null;
    }

    public void resetSelection(){
        this.selectedModule = -1;
    }

    public boolean mouseClicked(double x, double y, int button) {
        if (border.containsPoint(x, y)) {
            for (ClickableModule module : moduleButtons) {
                if (module.hitBox((float) x, (float)y)) {
                    Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                    selectedModule = moduleButtons.indexOf(module) ;
                    return true;
                }
            }
        }
        return false;
    }

    public List<ITextComponent> getToolTip(int x, int y) {
        if (border.containsPoint(x, y)) {
            if (moduleButtons != null) {
                for (ClickableModule module : moduleButtons) {
                    if (module.hitBox(x, y)) {
                        return module.getToolTip();
                    }
                }
            }
        }
        return null;
    }

    public Rect getBorder() {
        return border;
    }
}