package net.machinemuse.powersuits.client.gui.tinker.module;

import com.mojang.blaze3d.platform.GlStateManager;
import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.machinemuse.numina.client.gui.slot.ClickableModuleSlot;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseTile;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.scrollable.ScrollableFrame;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.math.MuseMathUtils;
import net.machinemuse.powersuits.client.gui.tinker.common.ItemSelectionFrame;
import net.machinemuse.powersuits.containers.ModularItemContainer;
import net.minecraft.inventory.container.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


//TODO: replace grid with this, sync slots to box centers on init
public class ModuleInventoryFrame extends ScrollableFrame {
    int padding = 6;
    final int gridWidth = 9; // limit to 9
    final int boxHeight = 18;
    final int boxWidth = 18;
    MusePoint2D ulGui = new MusePoint2D(0, 0);

    List<DrawableMuseTile> boxes;
    List<Integer> slots;
    ItemSelectionFrame itemSelectFrame;
    ModularItemContainer container;
    Colour gridColour;
    Colour backgroundColour;

    public ModuleInventoryFrame(ModularItemContainer containerIn, ItemSelectionFrame itemSelectFrameIn,
                                MusePoint2D topleftIn, MusePoint2D bottomright,
                                Colour borderColourIn,
                                Colour backgroundColour,
                                Colour gridColourIn) {
        super(topleftIn, bottomright, borderColourIn, backgroundColour);
        itemSelectFrame = itemSelectFrameIn;
        container = containerIn;
        gridColour = gridColourIn;
        this.backgroundColour = borderColourIn;
        loadSlots();
    }

    @Override
    public void update(double x, double y) {
        super.update(x, y);
        loadSlots();
    }

    MusePoint2D unseenLocation = new MusePoint2D(-1000, -1000);
    public void closeSlots() {
        if (slots != null) {
            for (int index : slots) {
                Slot slot = container.getSlot(index);
                if (slot instanceof ClickableModuleSlot) {
                    ((ClickableModuleSlot) slot).setVisible(false);
                    ((ClickableModuleSlot) slot).setEnabled(false);
                    ((ClickableModuleSlot) slot).move(unseenLocation);
                }
            }
        }
        boxes = new ArrayList<>();
    }

    public void updateUlGui(MusePoint2D ulGui) {
        this.ulGui = ulGui;
    }

    public void loadSlots() {
        ClickableItem selecteditem = itemSelectFrame.getSelectedItem();
        if (selecteditem != null) {
            Map<ClickableItem, List<Integer>> map = container.getModularItemToSlotMap();
            slots = map.get(selecteditem);
            boxes = new ArrayList<>();
            totalsize = 0;
            if (slots.size() <= gridWidth)
                totalsize = boxHeight;
            else
                totalsize = slots.size() / gridWidth * boxHeight + boxHeight + 2;
            this.currentscrollpixels = Math.min(currentscrollpixels, getMaxScrollPixels());

            int gridHeight = slots.size()/gridWidth ;
            int remainder = slots.size() % gridWidth;
            gridHeight = remainder > 0 ? gridHeight + 1 : gridHeight;

            int i = 0;
            for(int y = 0; y < gridHeight && i < slots.size(); y++) {
                for (int x = 0; x < gridWidth && i < slots.size(); x++) {
                    MusePoint2D box_ul = getUpperLeft().plus(new MusePoint2D(this.boxWidth * x, (double)(this.boxHeight * y) - currentscrollpixels));
                    boxes.add(new DrawableMuseTile(box_ul, box_ul.plus(new MusePoint2D(boxWidth, boxHeight)), backgroundColour.withAlpha(1), gridColour.withAlpha(1)));

                    if (i > 0) {
                        if (x > 0) {
                            this.boxes.get(i).setMeRightOf(this.boxes.get(i - 1));
                        }

                        if (y > 0) {
                            this.boxes.get(i).setMeBelow(this.boxes.get(i - this.gridWidth));
                        }
                    }
                    int index = slots.get(i);//
//                    System.out.println("index: " + index);
                    Slot slot = container.getSlot(index);
                    if (slot instanceof ClickableModuleSlot) {
                        ((ClickableModuleSlot) slot).setVisible(true);
                        ((ClickableModuleSlot) slot).setEnabled(true);

                        // this is kinda stupid, but the container screen rendering class shifts everything over
                        ((ClickableModuleSlot) slot).move(new MusePoint2D(boxes.get(i).left(), boxes.get(i).top()).minus(ulGui));
                    }
                    i++;
                }
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.preRender(mouseX, mouseY, partialTicks);

        if (boxes != null) {
            GlStateManager.lineWidth(2);
            for (DrawableMuseTile rect : boxes) {
                rect.draw();
            }
        }

        if (slots != null) {
            for (int index : slots) {
                Slot slot = container.getSlot(index);
                if (slot instanceof ClickableModuleSlot) {
                    ((ClickableModuleSlot) slot).render(mouseX, mouseY, partialTicks);
                }
            }
        }

        super.postRender(mouseX, mouseY, partialTicks);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
//        System.out.println("sccrolling here");

        if (this.border.containsPoint(mouseX, mouseY)) {
            this.currentscrollpixels = (int) MuseMathUtils.clampDouble(
                    ((int)((double)this.currentscrollpixels - dWheel * this.getScrollAmount())), 0.0D, this.getMaxScrollPixels());


            System.out.println("scrolling here, scroll pixles: " + currentscrollpixels);

            return true;
        } else {
            System.out.println("NOT scrolling here");

            return false;
        }
    }
}
