package net.machinemuse.powersuits.client.gui.tinker.module;

import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.machinemuse.numina.client.gui.clickable.ClickableModule;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.geometry.MuseRect;
import net.machinemuse.numina.client.gui.geometry.MuseRelativeRect;
import net.machinemuse.numina.client.gui.scrollable.ScrollableFrame;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.basemod.MPSModules;
import net.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModuleSelectionFrame extends ScrollableFrame {
    protected ItemSelectionFrame target;
    protected Map<EnumModuleCategory, ModuleSelectionSubFrame> categories = new LinkedHashMap<>();
    protected MuseRect lastPosition;

    public ModuleSelectionFrame(ItemSelectionFrame itemSelectFrameIn, MusePoint2D topleft, MusePoint2D bottomright, Colour backgroundColour, Colour borderColour) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.target = itemSelectFrameIn;
    }

    private ModuleSelectionSubFrame getOrCreateCategory(EnumModuleCategory category) {
        if (categories.containsKey(category)) {
            return categories.get(category);
        } else {
            MuseRelativeRect position = new MuseRelativeRect(
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

    /**
     * Populates the module list
     * load this whenever a modular item is selected or when a module is installed
     */
    public void loadModules(boolean preserveSelected) {
        this.lastPosition = null;
        // temp holder
        ClickableModule selCopy = getSelectedModule();

        ClickableItem selectedItem = target.getSelectedItem();
        if (selectedItem != null) {
            if (!preserveSelected) {
                selCopy = null;
            } else if(getSelectedModule() != null) {
                ClickableModule sel = getSelectedModule();
                selCopy = new ClickableModule(sel.getModule(), new MusePoint2D(0, 0), -1, sel.category);
            }

            categories = new LinkedHashMap<>();
            selectedItem.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler->{
                if (itemHandler instanceof IModularItem) {
                    List<ResourceLocation> moduleRegNameList = new ArrayList<>(MPSModules.INSTANCE.getModuleRegNames()); // copy of the list
                    // check the list of all possible modules
                    for (ResourceLocation regName : moduleRegNameList) {
                        if(!((IModularItem) itemHandler).isModuleInstalled(regName)) {
                            ItemStack module = new ItemStack(ForgeRegistries.ITEMS.getValue(regName));
                            EnumModuleCategory category = module.getCapability(PowerModuleCapability.POWER_MODULE).map(m->m.getCategory()).orElse(EnumModuleCategory.NONE);

                            if (((IModularItem) itemHandler).isModuleValid(module)) {
                                ModuleSelectionSubFrame frame = getOrCreateCategory(category);
                                ClickableModule clickie = frame.addModule(module,  -1);
                                clickie.setInstalled(false);
                            }
                        }
                    }

                    // Occupied slots in the Modular Item
                    for (int index = 0; index < itemHandler.getSlots(); index++) {
                        ItemStack module = itemHandler.getStackInSlot(index);
                        int finalIndex = index;
                        module.getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(m->{
                            if (m.isAllowed()) {
                                ModuleSelectionSubFrame frame = getOrCreateCategory(m.getCategory());
                                ClickableModule clickie =  frame.addModule(module, finalIndex);
                                clickie.setInstalled(true);
                            }
                        });
                    }
                }
            });
        }

        for (ModuleSelectionSubFrame frame : categories.values()) {
            frame.refreshButtonPositions();
            // actually preserve the module selection during call to init due to it being called on gui resize
            if(preserveSelected && selCopy != null && frame.category == selCopy.category) {
                for (ClickableModule button : frame.moduleButtons) {
                    if (button.getModule().isItemEqual(selCopy.getModule())) {
                        frame.selectedModule = frame.moduleButtons.indexOf(button);
                        preserveSelected = false; // just to skip checking the rest
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        for (ModuleSelectionSubFrame frame : categories.values()) {
            frame.refreshButtonPositions();
            if (target.getSelectedItem() != null) {
                frame.refreshModules(target.getSelectedItem().getStack());
            }
        }

        if (target.getSelectedItem() != null) {
            this.totalsize = 0;
            for (ModuleSelectionSubFrame frame : categories.values()) {
                totalsize = (int) Math.max(frame.border.bottom() - this.border.top(), totalsize);
            }
            this.currentscrollpixels = Math.min(currentscrollpixels, getMaxScrollPixels());
            super.preRender(mouseX, mouseY, partialTicks);
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -currentscrollpixels, 0);
            drawItems(mouseX, mouseY, partialTicks);
            drawSelection();
            GL11.glPopMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }

    private void drawItems(int mouseX, int mouseY, float partialTicks) {
        for (ModuleSelectionSubFrame frame : categories.values()) {
            frame.drawPartial((int) (this.currentscrollpixels + border.top() + 4),
                    (int) (this.currentscrollpixels + border.top() + border.height() - 4), partialTicks);
        }
    }

    private void drawSelection() {
        ClickableModule module = getSelectedModule();
        if (module != null) {
            MusePoint2D pos = module.getPosition();
            if (pos.getY() > this.currentscrollpixels + border.top() + 4 && pos.getY() < this.currentscrollpixels + border.top() + border.height() - 4) {
                MuseRenderer.drawCircleAround(pos.getX(), pos.getY(), 10);
            }
        }
    }

    public ClickableModule getSelectedModule() {
        ClickableModule ret = null;
        if (!categories.isEmpty()) {
            for (ModuleSelectionSubFrame frame : categories.values()) {
                ret = frame.getSelectedModule();
                if (ret != null)
                    break;
            }
        }
        return ret;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (super.mouseClicked(x, y, button))
            return true;

        ModuleSelectionSubFrame sel = null;

        if (border.containsPoint(x, y)) {
            y += currentscrollpixels;
            int i = 0;

            for (ModuleSelectionSubFrame frame : categories.values()) {
                if (frame.mouseClicked(x, y, button)) {
                    sel = frame;
                }
            }

            if(sel != null && sel.getSelectedModule() != null) {
                for (ModuleSelectionSubFrame frame : categories.values()) {
                    if (frame != sel) {
                        frame.resetSelection();
                    }
                }
            }
        }
        return sel != null;
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        if (border.containsPoint(x, y)) {
            y += currentscrollpixels;
            if (!categories.isEmpty()) {
                for (ModuleSelectionSubFrame category : categories.values()) {
                    List<ITextComponent> tooltip = category.getToolTip(x, y);
                    if(tooltip != null) {
                        return tooltip;
                    }
                }
            }
        }
        return null;
    }


    /**
     * Sets code to be executed when a new item is selected
     * @param doThisIn
     */
    OnSelectNewModule doThis;
    public void setDoOnNewSelect(OnSelectNewModule doThisIn) {
        doThis = doThisIn;
    }

    /**
     * runs preset code when new module is selected
     */
    void onSelected() {
        if(this.doThis != null) {
            this.doThis.onSelected(this);
        }
    }

    public interface OnSelectNewModule {
        void onSelected(ModuleSelectionFrame doThis);
    }
}