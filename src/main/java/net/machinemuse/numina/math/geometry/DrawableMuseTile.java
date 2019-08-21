//package net.machinemuse.numina.client.gui.geometry;
//
//import com.mojang.blaze3d.platform.GlStateManager;
//import net.machinemuse.numina.client.render.RenderState;
//import net.machinemuse.numina.math.Colour;
//import org.lwjgl.opengl.GL11;
//
//public class DrawableMuseTile extends MuseRelativeRect {
//    Colour backgroundColour;
//    Colour borderColour;
//
//    public DrawableMuseTile(double left, double top, double right, double bottom, boolean growFromMiddle,
//                            Colour backgroundColour,
//                            Colour borderColour) {
//        super(left, top, right, bottom, growFromMiddle);
//        this.backgroundColour = backgroundColour;
//        this.borderColour = borderColour;
//    }
//
//    public DrawableMuseTile(double left, double top, double right, double bottom,
//                            Colour backgroundColour,
//                            Colour borderColour) {
//        super(left, top, right, bottom, false);
//        this.backgroundColour = backgroundColour;
//        this.borderColour = borderColour;
//    }
//
//    public DrawableMuseTile(MusePoint2D ul, MusePoint2D br,
//                            Colour backgroundColour,
//                            Colour borderColour) {
//        super(ul, br);
//        this.backgroundColour = backgroundColour;
//        this.borderColour = borderColour;
//    }
//
//    @Override
//    public DrawableMuseTile copyOf() {
//        return new DrawableMuseTile(super.left(), super.top(), super.right(), super.bottom(),
//                (this.ul != this.ulFinal || this.wh != this.whFinal), backgroundColour, borderColour);
//    }
//
//    @Override
//    public DrawableMuseTile setLeft(double value) {
//        super.setLeft(value);
//        return this;
//    }
//
//    @Override
//    public DrawableMuseTile setRight(double value) {
//        super.setRight(value);
//        return this;
//    }
//
//    @Override
//    public DrawableMuseTile setTop(double value) {
//        super.setTop(value);
//        return this;
//    }
//
//    @Override
//    public DrawableMuseTile setBottom(double value) {
//        super.setBottom(value);
//        return this;
//    }
//
//    @Override
//    public DrawableMuseTile setWidth(double value) {
//        super.setWidth(value);
//        return this;
//    }
//
//    @Override
//    public DrawableMuseTile setHeight(double value) {
//        super.setHeight(value);
//        return this;
//    }
//
//    void vertices() {
//        GlStateManager.vertex3f((float)left(), (float)top(), 1);
//        GlStateManager.vertex3f((float)right(), (float)top(), 1);
//        GlStateManager.vertex3f((float)right(), (float)bottom(), 1);
//        GlStateManager.vertex3f((float)left(), (float)bottom(), 1);
//    }
//
//    public void preDraw() {
//        RenderState.on2D();
//        RenderState.texturelessOn();
//
//        // makes the lines and radii nicer
//        GL11.glEnable(GL11.GL_LINE_SMOOTH);
//        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT,  GL11.GL_NICEST);
//    }
//
//    public void postDraw() {
//        Colour.WHITE.doGL();
//        RenderState.texturelessOff();
//        RenderState.glowOff();
//    }
//
//    public void draw() {
//        preDraw();
//
//        GlStateManager.begin(GL11.GL_POLYGON);
//        backgroundColour.doGL();
//        vertices();
//        GlStateManager.end();
//
//        GlStateManager.begin(GL11.GL_LINE_LOOP);
////        GlStateManager.lineWidth(4f);
//        borderColour.doGL();
//        vertices();
//        GlStateManager.end();
//        postDraw();
//    }
//    public DrawableMuseTile setBackgroundColour(Colour insideColour) {
//        this.backgroundColour = insideColour;
//        return this;
//    }
//
//    public DrawableMuseTile setBorderColour(Colour outsideColour) {
//        this.borderColour = outsideColour;
//        return this;
//    }
//}