package net.machinemuse.numina.client.gui.geometry;

import net.machinemuse.numina.client.gui.slot.UniversalSlot;
import net.machinemuse.numina.math.Colour;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;

public class MuseSlotedRectangularGrid extends DrawableMuseRectangularGrid {
    Container container;
    int slotRangeStart;
    int slotRangeStop;
    public MuseSlotedRectangularGrid(Container container, int slotRangeStart, int slotRangeStop, double left, double top, double right, double bottom, boolean growFromMiddle, Colour insideColour, Colour outsideColour, Colour gridColour, int gridHeight, int gridWidth) {
        super(left, top, right, bottom, growFromMiddle, insideColour, outsideColour, gridColour, gridHeight, gridWidth);
        this.container = container;
        this.slotRangeStart = slotRangeStart;
        this.slotRangeStop = slotRangeStop;
    }

    public MuseSlotedRectangularGrid(Container container, int slotRangeStart, int slotRangeStop,  double left, double top, double right, double bottom, Colour insideColour, Colour outsideColour, Colour gridColour, int gridHeight, int gridWidth) {
        super(left, top, right, bottom, insideColour, outsideColour, gridColour, gridHeight, gridWidth);
        this.container = container;
        this.slotRangeStart = slotRangeStart;
        this.slotRangeStop = slotRangeStop;
    }

    public MuseSlotedRectangularGrid(Container container, int slotRangeStart, int slotRangeStop,  MusePoint2D ul, MusePoint2D br, Colour insideColour, Colour outsideColour, Colour gridColour, int gridHeight, int gridWidth) {
        super(ul, br, insideColour, outsideColour, gridColour, gridHeight, gridWidth);
        this.container = container;
        this.slotRangeStart = slotRangeStart;
        this.slotRangeStop = slotRangeStop;
    }

    public MuseSlotedRectangularGrid(Container container, int slotRangeStart, int slotRangeStop,  MuseRelativeRect ref, Colour insideColour, Colour outsideColour, Colour gridColour, int gridHeight, int gridWidth) {
        super(ref, insideColour, outsideColour, gridColour, gridHeight, gridWidth);
        this.container = container;
        this.slotRangeStart = slotRangeStart;
        this.slotRangeStop = slotRangeStop;
    }

    @Override
    void setupGrid() {
        super.setupGrid();
        if (Math.abs(slotRangeStop - slotRangeStart) != boxes.length)
            return;

        int addend = slotRangeStop > slotRangeStart ? 1 : -1;
        for (int i = slotRangeStart; i < slotRangeStop; i += addend) {
            Slot slot = container.getSlot(i);
            if (slot instanceof UniversalSlot) {
                ((UniversalSlot) slot).setPosition(boxes[i].center());
            } else {
                slot.xPos = (int) boxes[i].centerx();
                slot.yPos = (int) boxes[i].centery();
            }
        }
    }
}
