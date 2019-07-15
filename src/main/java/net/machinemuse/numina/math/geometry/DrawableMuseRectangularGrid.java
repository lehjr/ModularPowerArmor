package net.machinemuse.numina.math.geometry;

import com.mojang.blaze3d.platform.GlStateManager;
import net.machinemuse.numina.client.render.RenderState;
import net.machinemuse.numina.math.Colour;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2d;

public class DrawableMuseRectangularGrid extends DrawableMuseRelativeRect {
    Colour gridColour;
    int gridHeight;
    int gridWidth;
    Float horizontalSegmentSize;
    Float verticleSegmentSize;
    final MuseRelativeRect[] boxes;

    public DrawableMuseRectangularGrid(double left, double top, double right, double bottom, boolean growFromMiddle,
                                       Colour insideColour,
                                       Colour outsideColour,
                                       Colour gridColour,
                                       int gridHeight,
                                       int gridWidth) {
        super(left, top, right, bottom, growFromMiddle, insideColour, outsideColour);
        this.gridColour = gridColour;
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.boxes = new MuseRelativeRect[gridHeight*gridWidth];
        setBoxes();
    }

    public DrawableMuseRectangularGrid(double left, double top, double right, double bottom,
                                       Colour insideColour,
                                       Colour outsideColour,
                                       Colour gridColour,
                                       int gridHeight,
                                       int gridWidth) {
        super(left, top, right, bottom, false, insideColour, outsideColour);
        this.gridColour = gridColour;
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.boxes = new MuseRelativeRect[gridHeight*gridWidth];
        setBoxes();
    }

    public DrawableMuseRectangularGrid(MusePoint2D ul, MusePoint2D br,
                                       Colour insideColour,
                                       Colour outsideColour,
                                       Colour gridColour,
                                       int gridHeight,
                                       int gridWidth) {
        super(ul, br, insideColour, outsideColour);
        this.gridColour = gridColour;
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.boxes = new MuseRelativeRect[gridHeight*gridWidth];
        setBoxes();
    }

    public DrawableMuseRectangularGrid(MuseRelativeRect ref,
                                       Colour insideColour,
                                       Colour outsideColour,
                                       Colour gridColour,
                                       int gridHeight,
                                       int gridWidth) {
        super(ref.left(), ref.top(), ref.right(), ref.bottom(), ref.growFromMiddle(), insideColour, outsideColour);
        this.gridColour = gridColour;
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.boxes = new MuseRelativeRect[gridHeight*gridWidth];
        setBoxes();
    }

    void setBoxes() {
        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = new MuseRelativeRect(0, 0, 0, 0);
        }
    }


    public MuseRelativeRect[] getBoxes() {
        return boxes;
    }

    void drawGrid() {
        // reinitialize values on "growFromCenter" or resize
        boolean needInt = false;
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i] == null) {
                needInt = true;
                break;
            }
        }

        if (needInt)
            setBoxes();

        if (horizontalSegmentSize == null || verticleSegmentSize == null || (this.ul != this.ulFinal || this.wh != this.whFinal)) {
            horizontalSegmentSize = (float) (width() / gridWidth);
            verticleSegmentSize = (float) (height() / gridHeight);

            // uper left coner
            MusePoint2D box_ul;
            // bottom right
            MusePoint2D box_br;
            // width and height of each box

            MusePoint2D box_offset = new MusePoint2D(horizontalSegmentSize, verticleSegmentSize);
            int i = 0;

            // These boxes provide centers for the slots
            for (int y = 0; y < gridHeight; y++) {
                for (int x = 0; x < gridWidth; x++) {
                    box_ul = new MusePoint2D(horizontalSegmentSize * x, verticleSegmentSize * y);
                    boxes[i].setTargetDimensions(box_ul, box_offset);

                    if (i >0) {
                        if (x > 0)
                            boxes[i].setRightOf(boxes[i-1]);
                        if (y > 0){
                            boxes[i].setBelow(boxes[i-gridWidth]);
                        }
                    }
                    i++;
                }
            }
        }

        GlStateManager.lineWidth(4f);
        gridColour.doGL();
        GlStateManager.begin(GL11.GL_LINES);

        // Horizontal lines
        if (gridHeight >1)
            for (float y = (float) (verticleSegmentSize + top()); y < bottom(); y+= verticleSegmentSize) {
                GlStateManager.vertex3f((float)left(), y, 1);
                GlStateManager.vertex3f((float)right(), y, 1);
            }

        // Vertical lines
        if(gridWidth > 1)
            for (float x = (float) (horizontalSegmentSize + left()); x < right(); x += horizontalSegmentSize ) {
                GlStateManager.vertex3f(x, (float)top(), 1);
                GlStateManager.vertex3f(x, (float)bottom(), 1);
            }
        Colour.WHITE.doGL();
        GlStateManager.end();
    }

    @Override
    public DrawableMuseRelativeRect setLeft(double value) {
        double diff = value - left();
        super.setLeft(value);
        for (MuseRelativeRect box : boxes) {
            if (box != null)
                box.setLeft(box.left() + diff);
        }
        return this;
    }

    @Override
    public void preDraw() {
        GlStateManager.lineWidth(4f);
        super.preDraw();
    }

    @Override
    public void draw() {
        preDraw();
        drawBackground();
        drawGrid();
        drawBorder();
        postDraw();
    }
}