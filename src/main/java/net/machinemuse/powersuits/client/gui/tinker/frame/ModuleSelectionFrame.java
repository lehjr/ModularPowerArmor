package net.machinemuse.powersuits.client.gui.tinker.frame;

import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.client.gui.clickable.ClickableModularItem;
import net.machinemuse.numina.client.gui.clickable.ClickableModule;
import net.machinemuse.numina.client.gui.scrollable.ScrollableFrame;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.math.MuseMathUtils;
import net.machinemuse.numina.math.geometry.MusePoint2D;
import net.machinemuse.numina.math.geometry.MuseRect;
import net.machinemuse.numina.math.geometry.MuseRelativeRect;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.containers.TinkerTableContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class ModuleSelectionFrame extends ScrollableFrame {
    protected ItemSelectionFrame target;
    protected Map<String, ModuleSelectionSubFrame> categories = new LinkedHashMap<>();
    protected List<ClickableModule> moduleButtons = new LinkedList<>();
    protected int selectedModule = -1;
    protected ClickableModule prevSelection;
    protected ClickableModularItem lastItem;
    protected MuseRect lastPosition;
    TinkerTableContainer container;

    public ModuleSelectionFrame(TinkerTableContainer container, MusePoint2D topleft, MusePoint2D bottomright, Colour borderColour, Colour insideColour, ItemSelectionFrame target) {
        super(topleft, bottomright, borderColour, insideColour);
        this.target = target;
        this.container = container;
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
                loadModules();
            }
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
            MusePoint2D pos = moduleButtons.get(selectedModule).getPosition();
            if (pos.getY() > this.currentscrollpixels + border.top() + 4 && pos.getY() < this.currentscrollpixels + border.top() + border.height() - 4) {
                MuseRenderer.drawCircleAround(pos.getX(), pos.getY(), 10);
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

    public void loadModules() {
        this.lastPosition = null;
        ClickableModularItem selectedItem = target.getSelectedItem();
        if (selectedItem != null) {
            moduleButtons = new LinkedList<>();
            categories = new LinkedHashMap<>();


            Set<ClickableModule> moduleSlots = container.getModularItemToSlotMap().get(selectedItem);

            selectedItem.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler->{
                if (itemHandler instanceof IModularItem) {
                    this.selectedModule = -1;

                    // Slots in the Modular Item
                    for (ClickableModule slot : moduleSlots) {
                        // validate slot and add if not empty
                        slot.getStack().getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(m -> {
                            slot.setInstalled(true);
                            slot.setAllowed((((IModularItem) itemHandler).isModuleValid(slot.getStack())));
                            getOrCreateCategory(m.getCategory().getName(), slot);
                        });
                    }

                    for (ClickableModule slot: container.getModulesInPlayerInventory()) {
                        if (!(((IModularItem) itemHandler).isModuleValid(slot.getStack())))
                            continue;

                        if((((IModularItem) itemHandler).isModuleInstalled(slot.getStack().getItem().getRegistryName())))
                            continue;

                        boolean alreadyInList = false;
                        for (ClickableModule slotOther : moduleButtons) {
                            if (ItemStack.areItemsEqual(slot.getStack(), slotOther.getStack())) {
                                alreadyInList = true;
                                break;
                            }
                        }

                        if (alreadyInList)
                            continue;

                        slot.getStack().getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(m -> {
                            slot.setInstalled(true);
                            slot.setAllowed(true);
                            getOrCreateCategory(m.getCategory().getName(), slot);
                        });
                    }

                    for (ClickableModule slot: container.getAllModules()) {
                        if (!(((IModularItem) itemHandler).isModuleValid(slot.getStack())))
                            continue;

                        if((((IModularItem) itemHandler).isModuleInstalled(slot.getStack().getItem().getRegistryName())))
                            continue;

                        boolean alreadyInList = false;
                        for (ClickableModule slotOther : moduleButtons) {
                            if (ItemStack.areItemsEqual(slot.getStack(), slotOther.getStack())) {
                                alreadyInList = true;
                                break;
                            }
                        }

                        if (alreadyInList)
                            continue;

                        slot.getStack().getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(m -> {
                            slot.setInstalled(false);
                            slot.setAllowed(true);
                            getOrCreateCategory(m.getCategory().getName(), slot);
                        });
                    }
                }
            });

            for (ModuleSelectionSubFrame frame : categories.values()) {
                frame.refreshButtonPositions();
            }
        }
    }

    private void getOrCreateCategory(String category, ClickableModule module) {
        ModuleSelectionSubFrame frame;

        if (categories.containsKey(category)) {
            frame = categories.get(category);
        } else {
            MuseRelativeRect position = new MuseRelativeRect(
                    border.left() + 4,
                    border.top() + 4,
                    border.right() - 4,
                    border.top() + 32);
            position.setBelow(lastPosition);
            lastPosition = position;
            frame = new ModuleSelectionSubFrame(
                    category,
                    position);

            categories.put(category, frame);
        }

        ClickableModule moduleClickable = frame.addModule(module);
        if (moduleClickable == this.prevSelection) {
            this.selectedModule = moduleButtons.size();
        }
        moduleButtons.add(moduleClickable);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        super.mouseClicked(x, y, button);
        if (border.left() < x && border.right() > x && border.top() < y && border.bottom() > y) {
            y += currentscrollpixels;
            // loadModules();
            int i = 0;
            for (ClickableModule module : moduleButtons) {
                if (module.hitBox(x, y)) {
                    Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                    selectedModule = i;
                    prevSelection = module;
                    return true;
                } else {
                    i++;
                }
            }
        }
        return false;
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        if (border.left() < x && border.right() > x && border.top() < y && border.bottom() > y) {
            y += currentscrollpixels;
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

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double dWheel) {
        if (border.left() < mouseX && border.right() > mouseX && border.top() < mouseY && border.bottom() > mouseY) {
            // prevent negative total scroll values
            currentscrollpixels  = (int) MuseMathUtils.clampDouble(currentscrollpixels+= dWheel * 8, 0, Double.MAX_VALUE);
            return true;
        }
        return super.onMouseScrolled(mouseX, mouseY, dWheel);
    }
}