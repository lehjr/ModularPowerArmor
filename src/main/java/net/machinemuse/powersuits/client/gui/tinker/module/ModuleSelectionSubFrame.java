package net.machinemuse.powersuits.client.gui.tinker.module;

import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.client.gui.clickable.ClickableModule;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.geometry.MuseRect;
import net.machinemuse.numina.client.gui.geometry.MuseRelativeRect;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ModuleSelectionSubFrame {
    protected List<ClickableModule> moduleButtons;
    protected MuseRelativeRect border;
    protected EnumModuleCategory category;

    public ModuleSelectionSubFrame(EnumModuleCategory category, MuseRelativeRect border) {
        this.category = category;
        this.border = border;
        this.moduleButtons = new ArrayList<>();
    }

    public ClickableModule addModule(@Nonnull ItemStack module, int index) {
        ClickableModule clickie = new ClickableModule(module, new MusePoint2D(0, 0), index, category);
        this.moduleButtons.add(clickie);
//         refreshButtonPositions();
        return clickie;
    }

    public ClickableModule replaceModule(@Nonnull ItemStack module, int index) {
        int moduleIndex = -1;
        MusePoint2D position = null;
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

    public void drawPartial(int min, int max, float partialTicks) {
        refreshButtonPositions();
        double top = border.top();
        MuseRenderer.drawString(this.category.getName(), border.left(), top);
        for (ClickableModule clickie : moduleButtons) {
            clickie.render(min, max, partialTicks);
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
        for (ClickableModule clickie : moduleButtons) {
            if (col > 4) {
                col = 0;
                row++;
            }
            double x = border.left() + 8 + 16 * col;
            double y = border.top() + 16 + 16 * row;
            clickie.move(x, y);
            col++;
        }
        border.setHeight(28 + 16 * row);
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
                if (module.hitBox(x, y)) {
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

    public MuseRect getBorder() {
        return border;
    }
}