//package net.machinemuse.powersuits.client.gui.testing;
//
//import net.machinemuse.numina.client.render.RenderState;
//import net.machinemuse.numina.math.Colour;
//import net.machinemuse.numina.math.geometry.*;
//import org.lwjgl.BufferUtils;
//import org.lwjgl.opengl.GL11;
//
//import java.nio.DoubleBuffer;
//
//public class DrawableMuseRelativeRect extends MuseRelativeRect {
//    Colour insideColour;
//    Colour outsideColour;
//    DoubleBuffer vertices;
//    DoubleBuffer coloursInside;
//    DoubleBuffer coloursOutside;
//    double cornerradius = 3;
//    double zLevel = 1;
//
//    public DrawableMuseRelativeRect(double left, double top, double right, double bottom, boolean growFromMiddle,
//                                    Colour insideColour,
//                                    Colour outsideColour) {
//        super(left, top, right, bottom, growFromMiddle);
//        this.insideColour = insideColour;
//        this.outsideColour = outsideColour;
//    }
//
//    public DrawableMuseRelativeRect(double left, double top, double right, double bottom,
//                            Colour insideColour,
//                            Colour outsideColour) {
//        super(left, top, right, bottom, false);
//        this.insideColour = insideColour;
//        this.outsideColour = outsideColour;
//    }
//
//    public DrawableMuseRelativeRect(MusePoint2D ul, MusePoint2D br,
//                            Colour insideColour,
//                            Colour outsideColour) {
//        super(ul, br);
//        this.insideColour = insideColour;
//        this.outsideColour = outsideColour;
//    }
//
//    @Override
//    public DrawableMuseRelativeRect copyOf() {
//        return new DrawableMuseRelativeRect(super.left(), super.top(), super.right(), super.bottom(),
//                (this.ul != this.ulFinal || this.wh != this.whFinal) , insideColour, outsideColour);
//    }
//
//    @Override
//    public DrawableMuseRelativeRect setLeft(double value) {
//        super.setLeft(value);
//        return this;
//    }
//
//    @Override
//    public net.machinemuse.numina.math.geometry.DrawableMuseRect setRight(double value) {
//        super.setRight(value);
//        return this;
//    }
//
//    @Override
//    public net.machinemuse.numina.math.geometry.DrawableMuseRect setTop(double value) {
//        super.setTop(value);
//        return this;
//    }
//
//    @Override
//    public net.machinemuse.numina.math.geometry.DrawableMuseRect setBottom(double value) {
//        super.setBottom(value);
//        return this;
//    }
//
//    @Override
//    public net.machinemuse.numina.math.geometry.DrawableMuseRect setWidth(double value) {
//        super.setWidth(value);
//        return this;
//    }
//
//    @Override
//    public net.machinemuse.numina.math.geometry.DrawableMuseRect setHeight(double value) {
//        super.setHeight(value);
//        return this;
//    }
//
//    void preDraw() {
//        if (vertices == null || coloursInside == null || coloursOutside == null
//                || (this.ul != this.ulFinal || this.wh != this.whFinal)) {
//
//            // top left corner
//            DoubleBuffer corner = GradientAndArcCalculator.getArcPoints(Math.PI,
//                    3.0 * Math.PI / 2.0, cornerradius, left() + cornerradius,
//                    top() + cornerradius, zLevel);
//
//            vertices = BufferUtils.createDoubleBuffer(corner.limit() * 4);
//            vertices.put(corner);
//
//            // bottom left corner
//            corner = GradientAndArcCalculator.getArcPoints(3.0 * Math.PI / 2.0,
//                    2.0 * Math.PI, cornerradius, left() + cornerradius,
//                    bottom() - cornerradius, zLevel);
//            vertices.put(corner);
//
//            // bottom right corner
//            corner = GradientAndArcCalculator.getArcPoints(0, Math.PI / 2.0, cornerradius,
//                    right() - cornerradius, bottom() - cornerradius, zLevel);
//            vertices.put(corner);
//
//
//            // top right corner
//            corner = GradientAndArcCalculator.getArcPoints(Math.PI / 2.0, Math.PI,
//                    cornerradius, right() - cornerradius, top() + cornerradius,
//                    zLevel);
//            vertices.put(corner);
//            vertices.flip();
//            coloursInside = GradientAndArcCalculator.getColourGradient(outsideColour,
//                    outsideColour, vertices.limit() * 4 / 3 + 8);
//            coloursOutside = GradientAndArcCalculator.getColourGradient(insideColour,
//                    insideColour, vertices.limit() * 4 / 3 + 8);
//
//        }
//
//        RenderState.blendingOn();
//        RenderState.on2D();
//        RenderState.texturelessOn();
//
//        RenderState.arraysOnColor();
//    }
//
//    void drawBackground() {
//        // render inside
//        RenderState.glColorPointer(4, 0, coloursInside);
//        RenderState.glVertexPointer(3, 0, vertices);
//        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, vertices.limit() / 3);
//    }
//
//    void drawBorder() {
//        // render border
//        RenderState.glColorPointer(4, 0, coloursOutside);
//        RenderState.glVertexPointer(3, 0, vertices);
//        GL11.glDrawArrays(GL11.GL_LINE_LOOP, 0, vertices.limit() / 3);
//    }
//
//    void postDraw() {
//        RenderState.texturelessOff();
//        RenderState.off2D();
//        RenderState.blendingOff();
//        RenderState.arraysOff();
//    }
//
//    public void draw() {
//        preDraw();
//        drawBackground();
//        drawBorder();
//        postDraw();
//    }
//
//    public net.machinemuse.numina.math.geometry.DrawableMuseRect setInsideColour(Colour insideColour) {
//        this.insideColour = insideColour;
//        return this;
//    }
//
//    public net.machinemuse.numina.math.geometry.DrawableMuseRect setOutsideColour(Colour outsideColour) {
//        this.outsideColour = outsideColour;
//        return this;
//    }
//}
//
//
//
//
//
//
//
//
//
//    //--////////
//
//
//
//
//    public DrawableMuseRelativeRect(MuseRelativeRect museRect,
//                                    Colour insideColour,
//                                    Colour outsideColour) {
//        this.thisRect = museRect;
//        this.insideColour = insideColour;
//        this.outsideColour = outsideColour;
//        if (this.thisRect.isGrowFromMiddle()) {
//            this.lastRect = thisRect.copyOf();
//        }
//    }
//
//    public void setRect(MuseRelativeRect rectangle) {
//        this.thisRect = rectangle;
//        if (this.thisRect.isGrowFromMiddle()) {
//            this.lastRect = thisRect.copyOf();
//        }
//    }
//
//    public DrawableMuseRelativeRect copyOf() {
//        return new DrawableMuseRelativeRect(thisRect, insideColour, outsideColour);
//    }
//
//    public DrawableMuseRelativeRect setLeft(double value) {
//        this.thisRect.setLeft(value);
//        return this;
//    }
//
//    public DrawableMuseRelativeRect setRight(double value) {
//        this.thisRect.setRight(value);
//        return this;
//    }
//
//    public DrawableMuseRelativeRect setTop(double value) {
//        this.thisRect.setTop(value);
//        return this;
//    }
//
//    public DrawableMuseRelativeRect setBottom(double value) {
//        this.thisRect.setBottom(value);
//        return this;
//    }
//
//    public DrawableMuseRelativeRect setWidth(double value) {
//        this.thisRect.setWidth(value);
//        return this;
//    }
//
//    public DrawableMuseRelativeRect setHeight(double value) {
//        this.thisRect.setHeight(value);
//        return this;
//    }
//
//    void drawBackground() {
//        // render inside
//        RenderState.glColorPointer(4, 0, coloursInside);
//        RenderState.glVertexPointer(3, 0, vertices);
//        GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, vertices.limit() / 3);
//    }
//
//    void drawBorder() {
//        RenderState.glColorPointer(4, 0, coloursOutside);
//        RenderState.glVertexPointer(3, 0, vertices);
//        GL11.glDrawArrays(GL11.GL_LINE_LOOP, 0, vertices.limit() / 3);
//    }
//
//    void preDraw() {
//        if (vertices == null || coloursInside == null || coloursOutside == null || (lastRect != null && !lastRect.equals(this.thisRect))) {
//            this.lastRect.setLeft(left());
//            this.lastRect.setBottom(bottom());
//            this.lastRect.setRight(right());
//            this.lastRect.setTop(top());
//
//            // top left corner
//            DoubleBuffer corner = GradientAndArcCalculator.getArcPoints(Math.PI,
//                    3.0 * Math.PI / 2.0, cornerradius, thisRect.left() + cornerradius,
//                    thisRect.top() + cornerradius, zLevel);
//
//            vertices = BufferUtils.createDoubleBuffer(corner.limit() * 4);
//
//            vertices.put(corner);
//
//            // bottom left corner
//            corner = GradientAndArcCalculator.getArcPoints(3.0 * Math.PI / 2.0,
//                    2.0 * Math.PI, cornerradius, thisRect.left() + cornerradius,
//                    thisRect.bottom() - cornerradius, zLevel);
//            vertices.put(corner);
//
//            // bottom right corner
//            corner = GradientAndArcCalculator.getArcPoints(0, Math.PI / 2.0, cornerradius,
//                    thisRect.right() - cornerradius, thisRect.bottom() - cornerradius, zLevel);
//            vertices.put(corner);
//
//
//            // top right corner
//            corner = GradientAndArcCalculator.getArcPoints(Math.PI / 2.0, Math.PI,
//                    cornerradius, thisRect.right() - cornerradius, thisRect.top() + cornerradius,
//                    zLevel);
//            vertices.put(corner);
//            vertices.flip();
//            coloursInside = GradientAndArcCalculator.getColourGradient(outsideColour,
//                    outsideColour, vertices.limit() * 4 / 3 + 8);
//            coloursOutside = GradientAndArcCalculator.getColourGradient(insideColour,
//                    insideColour, vertices.limit() * 4 / 3 + 8);
//        }
//
//        RenderState.blendingOn();
//        RenderState.on2D();
//        RenderState.texturelessOn();
//        RenderState.arraysOnColor();
//        GL11.glEnable(GL11.GL_LINE_SMOOTH);
//        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT,  GL11.GL_NICEST);
//    }
//
//    void postDraw() {
//        RenderState.texturelessOff();
//        RenderState.off2D();
//        RenderState.blendingOff();
//        RenderState.arraysOff();
//    }
//    public void draw() {
//        preDraw();
//
//        drawBackground();
//        drawBorder();
//
//        postDraw();
//    }
//
//    public DrawableMuseRelativeRect setInsideColour(Colour insideColour) {
//        this.insideColour = insideColour;
//        return this;
//    }
//
//    public DrawableMuseRelativeRect setOutsideColour(Colour outsideColour) {
//        this.outsideColour = outsideColour;
//        return this;
//    }
//
//    public double top() {
//        return thisRect.top();
//    }
//
//    public double bottom() {
//        return thisRect.bottom();
//    }
//
//    public double left() {
//        return thisRect.left();
//    }
//
//    public double right() {
//        return thisRect.right();
//    }
//
//    public double width() {
//        return thisRect.width();
//    }
//
//    public double height() {
//        return thisRect.height();
//    }
//}