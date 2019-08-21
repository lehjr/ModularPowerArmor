package net.machinemuse.powersuits.client.gui.tinker.module_install;

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
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.basemod.MPSModules;
import net.machinemuse.powersuits.client.gui.tinker.common.ItemSelectionFrame;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.containers.ModularItemContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.opengl.GL11;

import java.util.*;


// fixme: lastItem requires doubleclick to set
public class ModuleSelectionFrame2 extends ScrollableFrame {
    protected ItemSelectionFrame target;
    protected Map<String, ModuleSelectionSubFrame2> categories = new LinkedHashMap<>();
    List<ClickableModule> moduleButtons = new LinkedList<>(); // fixme: ditch this

    protected int selectedModule = -1;
    protected ClickableModule prevSelection;
    protected MuseRect lastPosition;
    ModularItemContainer container;

    public ModuleSelectionFrame2(ModularItemContainer containerIn, ItemSelectionFrame itemSelectFrameIn, MusePoint2D topleft, MusePoint2D bottomright, Colour backgroundColour, Colour borderColour) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.container = containerIn;
        this.target = itemSelectFrameIn;
    }

    private ModuleSelectionSubFrame2 getOrCreateCategory(String category) {
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
            ModuleSelectionSubFrame2 frame = new ModuleSelectionSubFrame2(
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
    public void loadModules() {
        this.lastPosition = null;
        ClickableItem selectedItem = target.getSelectedItem();
        if (selectedItem != null) {
            moduleButtons = new LinkedList<>();
            categories = new LinkedHashMap<>();

            List<Integer> inventoryIndexes = container.getModularItemToSlotMap().get(selectedItem);
            selectedItem.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler->{
                if (itemHandler instanceof IModularItem) {
                    List<ResourceLocation> moduleRegNameList = new ArrayList<>(MPSModules.INSTANCE.getModuleRegNames()); // copy of the list

                    this.selectedModule = -1;
                    //
                    // check the list of all possible modules
                    for (ResourceLocation regName : moduleRegNameList) {
                        if(!((IModularItem) itemHandler).isModuleInstalled(regName)) {
                            ItemStack module = new ItemStack(ForgeRegistries.ITEMS.getValue(regName));
                            EnumModuleCategory category = module.getCapability(PowerModuleCapability.POWER_MODULE).map(m->m.getCategory()).orElse(EnumModuleCategory.NONE);

                            if (((IModularItem) itemHandler).isModuleValid(module)) {
                                ModuleSelectionSubFrame2 frame = getOrCreateCategory(category.getName());

                                ClickableModule clickie = frame.addModule(module,  -1);
                                clickie.setInstalled(false);
                                moduleButtons.add(clickie);
                            }
                        }
                    }

                    // Occupied slots in the Modular Item
                    for (int index : inventoryIndexes) {
                        Slot slot = container.getSlot(index);
                        ItemStack module = slot.getStack();
                        module.getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(m->{
                            if (m.isAllowed()) {
                                ModuleSelectionSubFrame2 frame = getOrCreateCategory(m.getCategory().getName());
                                ClickableModule clickie =  frame.addModule(module, index);
                                clickie.setInstalled(true);
                                moduleButtons.add(clickie);
                            }
                        });
                    }
                }
            });
        }

        for (ModuleSelectionSubFrame2 frame : categories.values()) {
            frame.refreshButtonPositions();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        for (ModuleSelectionSubFrame2 frame : categories.values()) {
            frame.refreshButtonPositions();
        }

        if (target.getSelectedItem() != null) {
            this.totalsize = 0;
            for (ModuleSelectionSubFrame2 frame : categories.values()) {
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
        for (ModuleSelectionSubFrame2 frame : categories.values()) {
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
        } else {
//            System.out.println("no module selected");
        }
    }

    public ClickableModule getSelectedModule() {
        if (moduleButtons.size() > selectedModule && selectedModule != -1) {
            return moduleButtons.get(selectedModule);
        } else {
            return null;
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (super.mouseClicked(x, y, button))
            return true;

        if (border.containsPoint(x, y)) {
            y += currentscrollpixels;
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
        if (border.containsPoint(x, y)) {
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
}