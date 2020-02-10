package com.github.lehjr.modularpowerarmor.client.gui.common;

import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableTile;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.slot.UniversalSlot;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public class InventoryFrame extends ScrollableFrame {
    Container container;
    Colour backgroundColour;
    Colour gridColour;
    public final int gridWidth;
    public final int gridHeight;
    List<Integer> slotIndexes;
    List<DrawableTile> tiles;
    Point2D slot_ulShift = new Point2D(0, 0);

    public InventoryFrame(Container containerIn,
                          Point2D topleft,
                          Point2D bottomright,
                          Colour backgroundColour,
                          Colour borderColour,
                          Colour gridColourIn,
                          int gridWidth,
                          int gridHeight,
                          List<Integer> slotIndexesIn) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.container = containerIn;
        this.backgroundColour = backgroundColour;
        this.gridColour = gridColourIn;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.slotIndexes = slotIndexesIn;
        this.tiles = new ArrayList<>();
    }

        public void loadSlots() {
            Point2D wh = new Point2D(18, 18);
            Point2D ul = new Point2D(border.left(), border.top());
            tiles = new ArrayList<>();
            int i = 0;
            outerLoop:
            for(int row = 0; row < gridHeight; row ++) {
                for (int col = 0; col < gridWidth; col ++) {
                    if (i == slotIndexes.size()){
                        break outerLoop;
                    }
                    DrawableTile tile = new DrawableTile(ul, ul.plus(wh), backgroundColour, gridColour);
                    tiles.add(tile);

                    if (i > 0) {
                        if (col > 0) {
                            this.tiles.get(i).setMeRightOf(this.tiles.get(i - 1));
                        }

                        if (row > 0) {
                            this.tiles.get(i).setMeBelow(this.tiles.get(i - this.gridWidth));
                        }
                    }
                    Point2D position = tile.center().copy().minus(slot_ulShift);

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

        public Point2D getUlShift() {
            return slot_ulShift;
        }

        public void setUlShift(Point2D ulShift) {
            this.slot_ulShift = ulShift;
        }

        @Override
        public void init(double left, double top, double right, double bottom) {
            super.init(left, top, right, bottom);
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
                for (DrawableTile tile : tiles) {
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
