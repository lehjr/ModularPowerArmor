package net.machinemuse.powersuits.client.gui.common;

import net.machinemuse.numina.client.gui.frame.IGuiFrame;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseRect;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseTile;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.slot.UniversalSlot;
import net.machinemuse.numina.math.Colour;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public class InventoryFrame implements IGuiFrame {
    Container container;
    protected DrawableMuseRect border;
    Colour backgroundColour;
    Colour gridColour;
    public final int gridWidth;
    public final int gridHeight;
    List<Integer> slotIndexes;
    List<DrawableMuseTile> tiles;
    MusePoint2D slot_ulShift = new MusePoint2D(0, 0);

    public InventoryFrame(Container containerIn,
                          MusePoint2D topleft,
                          MusePoint2D bottomright,
                          Colour backgroundColour,
                          Colour borderColour,
                          Colour gridColourIn,
                          int gridWidth,
                          int gridHeight,
                          List<Integer> slotIndexesIn) {
        this.container = containerIn;
        this.border = new DrawableMuseRect(topleft, bottomright, backgroundColour, borderColour);
        this.backgroundColour = backgroundColour;
        this.gridColour = gridColourIn;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.slotIndexes = slotIndexesIn;
        this.tiles = new ArrayList<>();
    }

    public void loadSlots() {
        MusePoint2D wh = new MusePoint2D(18, 18);
        MusePoint2D ul = new MusePoint2D(border.left(), border.top());
        tiles = new ArrayList<>();
        int i = 0;
        outerLoop:
        for(int row = 0; row < gridHeight; row ++) {
            for (int col = 0; col < gridWidth; col ++) {
                if (i == slotIndexes.size()){
                    break outerLoop;
                }
                DrawableMuseTile tile = new DrawableMuseTile(ul, ul.plus(wh), backgroundColour, gridColour);
                tiles.add(tile);

                if (i > 0) {
                    if (col > 0) {
                        this.tiles.get(i).setMeRightOf(this.tiles.get(i - 1));
                    }

                    if (row > 0) {
                        this.tiles.get(i).setMeBelow(this.tiles.get(i - this.gridWidth));
                    }
                }
                MusePoint2D position = tile.center().copy().minus(slot_ulShift);

                Slot slot = container.getSlot(slotIndexes.get(i));
                if (slot instanceof UniversalSlot) {
                    ((UniversalSlot) slot).setPosition(position);
                } else {
                    slot.xPos = (int)position.getX();
                    slot.yPos = (int)position.getY();
                }
                i++;
            }
        }
    }

    @Override
    public boolean mouseClicked(double v, double v1, int i) {
        return false;
    }

    @Override
    public boolean mouseReleased(double v, double v1, int i) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double v, double v1, double v2) {
        return false;
    }

    public MusePoint2D getUlShift() {
        return slot_ulShift;
    }

    public void setUlShift(MusePoint2D ulShift) {
        this.slot_ulShift = ulShift;
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        this.border.setTargetDimensions(left, top, right, bottom);
        loadSlots();
    }

    @Override
    public void update(double v, double v1) {
        loadSlots();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        border.preDraw();
        border.drawBackground();

        if (this.tiles != null && !this.tiles.isEmpty()) {
            for (DrawableMuseTile tile : tiles) {
                tile.draw();
            }
        }

        border.drawBorder();
        this.border.postDraw();
    }

    @Override
    public List<ITextComponent> getToolTip(int i, int i1) {
        return null;
    }
}
