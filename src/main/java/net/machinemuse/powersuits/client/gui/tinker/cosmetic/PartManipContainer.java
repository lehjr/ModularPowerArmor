package net.machinemuse.powersuits.client.gui.tinker.cosmetic;

import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.render.modelspec.ModelRegistry;
import com.github.lehjr.mpalib.client.render.modelspec.SpecBase;
import com.github.lehjr.mpalib.math.Colour;
import net.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public Point2D topleft;
    public Point2D bottomright;
    public Integer lastItemSlot;
    public int lastColour;
    public int lastColourIndex;
    public List<PartSpecManipSubFrame> modelframes;
    protected boolean enabled;
    protected boolean visibile;

    public PartManipContainer(ItemSelectionFrame itemSelect,
                              ColourPickerFrame colourSelect,
                              Point2D topleft,
                              Point2D bottomright,
                              Colour borderColour,
                              Colour insideColour) {
        super(topleft, bottomright, borderColour, insideColour);

        this.itemSelect = itemSelect;
        this.colourSelect = colourSelect;
        this.topleft = topleft;
        this.bottomright = bottomright;
        this.lastItemSlot = null;
        this.lastColour = this.getColour();
        this.lastColourIndex = this.getColourIndex();
        this.modelframes = getModelframes();
        enabled = true;
        visibile = true;
    }

    @Nonnull
    public ItemStack getItem() {
        return (itemSelect.getSelectedItem() != null) ? itemSelect.getSelectedItem().getItem() : ItemStack.EMPTY;
    }

    @Nullable
    public Integer getItemSlot() {
        return (itemSelect.getSelectedItem() != null) ? itemSelect.getSelectedItem().inventorySlot : null;
    }

    public int getColour() {
        if (getItem() == null)
            return Colour.WHITE.getInt();
        if (colourSelect.selectedColour < colourSelect.colours().length && colourSelect.selectedColour >= 0)
            return colourSelect.colours()[colourSelect.selectedColour];
        else
            return Colour.WHITE.getInt();
    }

    public int getColourIndex() {
        return this.colourSelect.selectedColour;
    }

    public List<PartSpecManipSubFrame> getModelframes() {
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
        RelativeRect newborder = new RelativeRect(this.topleft.getX() + 4, this.topleft.getY() + 4, this.bottomright.getX(), this.topleft.getY() + 10);
        newborder.setMeBelow((prev != null) ? prev.border : null);
        return new PartSpecManipSubFrame(modelspec, this.colourSelect, this.itemSelect, newborder);
    }

    @Override
    public void onMouseDown(double x, double y, int button) {
        if (enabled) {
            if (button == 0) {
                for (PartSpecManipSubFrame frame : modelframes) {
                    frame.tryMouseClick(x, y + currentscrollpixels);
                }
            }
        }
    }

    @Override
    public void update(double mousex, double mousey) {
        super.update(mousex, mousey);
        if (enabled) {
            if (!Objects.equals(lastItemSlot, getItemSlot())) {
                lastItemSlot = getItemSlot();
                colourSelect.refreshColours(); // this does nothing

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

    public void hide () {
        visibile = false;
    }

    public void show() {
        visibile = true;
    }

    public boolean isVisibile() {
        return visibile;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void decrAbove(int index) {
        for (PartSpecManipSubFrame frame : modelframes) frame.decrAbove(index);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (visibile) {
            super.preRender(mouseX, mouseY, partialTicks);
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, (double) (-this.currentscrollpixels), 0.0);
            for (PartSpecManipSubFrame f : modelframes) {
                f.drawPartial(currentscrollpixels + 4 + border.top(), this.currentscrollpixels + border.bottom() - 4);
            }
            GL11.glPopMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }
}