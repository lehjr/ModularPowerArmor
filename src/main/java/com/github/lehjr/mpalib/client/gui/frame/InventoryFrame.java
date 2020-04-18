package com.github.lehjr.mpalib.client.gui.frame;

import com.github.lehjr.modularpowerarmor.container.IHideableSlot;
import com.github.lehjr.modularpowerarmor.container.TinkerTableContainer;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableTile;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.gui.slot.UniversalSlot;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;

import java.nio.FloatBuffer;
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
    Point2F slot_ulShift = new Point2F(0, 0);

    public InventoryFrame(Container containerIn,
                          Point2F topleft,
                          Point2F bottomright,
                          float zLevel,
                          Colour backgroundColour,
                          Colour borderColour,
                          Colour gridColourIn,
                          int gridWidth,
                          int gridHeight,
                          List<Integer> slotIndexesIn) {
        super(topleft, bottomright, zLevel, backgroundColour, borderColour);
        this.container = containerIn;
        this.backgroundColour = backgroundColour;
        this.gridColour = gridColourIn;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.slotIndexes = slotIndexesIn;
        this.tiles = new ArrayList<>();
    }

        public void loadSlots() {
            Point2F wh = new Point2F(18, 18);
            Point2F ul = new Point2F(border.left(), border.top());
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
                    Point2F position = tile.center().copy().minus(slot_ulShift);

                    Slot slot = container.getSlot(slotIndexes.get(i));
                    if (slot instanceof UniversalSlot) {
                        ((UniversalSlot) slot).setPosition(position);
                    } else if (slot instanceof IHideableSlot) {
                        ((IHideableSlot) slot).setPosition(position);
                    } else {
                        System.out.println("fixme: slot positions are final!!!");
                        System.out.println("slot class:" + slot.getClass());
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

        public Point2F getUlShift() {
            return slot_ulShift;
        }

        public void setUlShift(Point2F ulShift) {
            this.slot_ulShift = ulShift;
        }

        @Override
        public void init(float left, float top, float right, float bottom) {
            super.init(left, top, right, bottom);
            loadSlots();
        }

        @Override
        public void update(double mouseX, double mouseY) {
            loadSlots();
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks) {
            FloatBuffer buffer = border.preDraw(zLevel);
            border.drawBackground(buffer);

            if (this.tiles != null && !this.tiles.isEmpty()) {
                for (DrawableTile tile : tiles) {
                    tile.draw(zLevel);
                }
            }

            border.drawBorder(buffer);
        }

        @Override
        public List<ITextComponent> getToolTip(int i, int i1) {
            return null;
        }
    }
