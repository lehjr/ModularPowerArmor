package com.github.lehjr.modularpowerarmor.client.gui.tinker.module;

import com.github.lehjr.modularpowerarmor.basemod.Modules;
import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableItem;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.CapabilityItemHandler;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class ModuleSelectionFrame extends ScrollableFrame {
    protected List<ClickableModule> moduleButtons = new LinkedList<>();
    protected int selectedModule = -1;
    protected ItemStack prevSelection;
    protected ClickableItem lastItem;
    protected ItemSelectionFrame target;
    protected Map<EnumModuleCategory, ModuleSelectionSubFrame> categories = new LinkedHashMap<>();
    protected Rect lastPosition;

    public ModuleSelectionFrame(ItemSelectionFrame itemSelectFrameIn, Point2D topleft, Point2D bottomright, Colour backgroundColour, Colour borderColour) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.target = itemSelectFrameIn;
    }

    @Override
    public void update(double mousex, double mousey) {
        super.update(mousex, mousey);
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
            frame.drawPartial((int) (this.currentscrollpixels + border.finalTop() + 4),
                    (int) (this.currentscrollpixels + border.finalTop() + border.finalHeight() - 4), partialTicks);
        }
    }

    private void drawSelection() {
        ClickableModule module = getSelectedModule();
        if (module != null) {
            Point2D pos = module.getPosition();
            if (pos.getY() > this.currentscrollpixels + border.finalTop() + 4 && pos.getY() < this.currentscrollpixels + border.finalTop() + border.finalHeight() - 4) {
                Renderer.drawCircleAround(pos.getX(), pos.getY(), 10);
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

    /**
     * Populates the module list
     * load this whenever a modular item is selected or when a module is installed
     */
    public void loadModules(boolean preserveSelected) {
        this.lastPosition = null;
        // temp holder
        ClickableModule selCopy = getSelectedModule();

        com.github.lehjr.mpalib.client.gui.clickable.ClickableItem selectedItem = target.getSelectedItem();
        if (selectedItem != null) {
            if (!preserveSelected) {
                selCopy = null;
            } else if(getSelectedModule() != null) {
                ClickableModule sel = getSelectedModule();
                selCopy = new ClickableModule(sel.getModule(), new Point2D(0, 0), -1, sel.category);
            }

            categories = new LinkedHashMap<>();
            Optional.ofNullable(selectedItem.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(itemHandler->{
                if (itemHandler instanceof IModularItem) {
                    List<ResourceLocation> moduleRegNameList = new ArrayList<>(Modules.INSTANCE.getModuleRegNames()); // copy of the list
                    // check the list of all possible modules
                    for (ResourceLocation regName : moduleRegNameList) {
                        if(!((IModularItem) itemHandler).isModuleInstalled(regName)) {
                            ItemStack module = new ItemStack(ForgeRegistries.ITEMS.getValue(regName));
                            EnumModuleCategory category = Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null))
                                    .map(m->m.getCategory()).orElse(EnumModuleCategory.NONE);

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
                        Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).ifPresent(m->{
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

    private ModuleSelectionSubFrame getOrCreateCategory(EnumModuleCategory category) {
        if (categories.containsKey(category)) {
            return categories.get(category);
        } else {
            RelativeRect position = new RelativeRect(
                    border.finalLeft() + 4,
                    border.finalTop() + 4,
                    border.finalRight() - 4,
                    border.finalTop() + 32);
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
        ModuleSelectionSubFrame sel = null;
        if (border.containsPoint(x, y)) {
            y += currentscrollpixels;
            int i = 0;

            for (ModuleSelectionSubFrame frame : categories.values()) {
                if (frame.onMouseDown(x, y, button)) {
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
    public List<String> getToolTip(int x, int y) {
        if (border.containsPoint(x, y)) {
            y += currentscrollpixels;
            if (!categories.isEmpty()) {
                for (ModuleSelectionSubFrame category : categories.values()) {
                    List<String> tooltip = category.getToolTip(x, y);
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