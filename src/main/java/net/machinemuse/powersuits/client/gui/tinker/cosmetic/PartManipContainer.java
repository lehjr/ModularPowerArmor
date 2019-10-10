package net.machinemuse.powersuits.client.gui.tinker.cosmetic;

import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.geometry.MuseRelativeRect;
import net.machinemuse.numina.client.gui.scrollable.ScrollableFrame;
import net.machinemuse.numina.client.render.modelspec.ModelRegistry;
import net.machinemuse.numina.client.render.modelspec.SpecBase;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 6:39 PM, 29/04/13
 * <p>
 * Ported to Java by lehjr on 11/9/16.
 */
public class PartManipContainer extends ScrollableFrame {
    public ItemSelectionFrame itemSelect;
    public ColourPickerFrame colourSelect;
    public ClickableItem lastItemSlot;
    public int lastColour;
    public int lastColourIndex;
    public List<PartSpecManipSubFrame> modelframes;

    public PartManipContainer(ItemSelectionFrame itemSelect,
                              ColourPickerFrame colourSelect,
                              MusePoint2D topleft,
                              MusePoint2D bottomright,
                              Colour borderColour,
                              Colour insideColour) {
        super(topleft, bottomright, borderColour, insideColour);

        this.itemSelect = itemSelect;
        this.colourSelect = colourSelect;
        this.lastItemSlot = null;
        this.lastColour = this.getColour();
        this.lastColourIndex = this.getColourIndex();
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
        if (itemSelect.hasNoItems()) {
            this.disable();
            this.hide();
        } else {
            this.enable();
            this.show();
            this.modelframes = getModelframes();
        }
    }

    @Nonnull
    public ItemStack getItem() {
        return (itemSelect.getSelectedItem() != null) ? itemSelect.getSelectedItem().getStack() : ItemStack.EMPTY;
    }

    public int getColour() {
        if (getItem().isEmpty()) {
            return Colour.WHITE.getInt();
        } else if (colourSelect.selectedColour < colourSelect.colours().length && colourSelect.selectedColour >= 0) {
            return colourSelect.colours()[colourSelect.selectedColour];
        }
        return Colour.WHITE.getInt();
    }

    public int getColourIndex() {
        return this.colourSelect.selectedColour;
    }

    public List<PartSpecManipSubFrame> getModelframes() {
        System.out.println("getting fames");

        List<PartSpecManipSubFrame> modelframesList = new ArrayList<>();
        Iterable<SpecBase> specCollection = ModelRegistry.getInstance().getSpecs();
        PartSpecManipSubFrame prev = null;
        PartSpecManipSubFrame newframe;
        for (SpecBase modelspec : specCollection) {
            newframe = createNewFrame(modelspec, prev);
            prev = newframe;
            modelframesList.add(newframe);
        }
        return modelframesList;
    }

    public PartSpecManipSubFrame createNewFrame(SpecBase modelspec, PartSpecManipSubFrame prev) {
        MuseRelativeRect newborder = new MuseRelativeRect(
                border.finalLeft() + 4,
                border.finalTop() + 4,
                border.finalRight(),
                border.finalTop() + 10
        );
        newborder.setMeBelow((prev != null) ? prev.border : null);
        return new PartSpecManipSubFrame(modelspec, this.colourSelect, this.itemSelect, newborder);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (this.isEnabled() && this.isVisibile()) {
            if (button == 0) {
                for (PartSpecManipSubFrame frame : modelframes) {
                    if (frame.tryMouseClick(x, y + currentscrollpixels))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public void update(double mousex, double mousey) {
        super.update(mousex, mousey);

        // only completely disable this if the player has no items equipped
        if (itemSelect.hasNoItems()) {
            this.disable();
            this.hide();
        } else if (itemSelect.getSelectedItem() != null) {
            this.enable();
            this.show();

            if (!Objects.equals(lastItemSlot, itemSelect.getSelectedItem())) {
                lastItemSlot = itemSelect.getSelectedItem();
                double x = 0;
                for (PartSpecManipSubFrame subframe : modelframes) {
                    subframe.updateItems();
                    x += subframe.border.bottom();
                }
                this.totalsize = (int) x;
            }
            if (colourSelect.decrAbove > -1) {
                decrAbove(colourSelect.decrAbove);
                colourSelect.decrAbove = -1;
            }
        }
    }

    public void decrAbove(int index) {
        for (PartSpecManipSubFrame frame : modelframes) frame.decrAbove(index);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.isVisibile()) {
            super.preRender(mouseX, mouseY, partialTicks);
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, -this.currentscrollpixels, 0.0);
            for (PartSpecManipSubFrame f : modelframes) {
                f.drawPartial(currentscrollpixels + 4 + border.finalTop(), this.currentscrollpixels + border.finalBottom() - 4);
            }
            GL11.glPopMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }
}