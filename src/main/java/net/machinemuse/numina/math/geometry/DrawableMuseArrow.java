package net.machinemuse.numina.math.geometry;

import com.mojang.blaze3d.platform.GlStateManager;
import net.machinemuse.numina.client.render.RenderState;
import net.machinemuse.numina.math.Colour;
import org.lwjgl.opengl.GL11;

public class DrawableMuseArrow extends MuseRelativeRect {
    Colour insideColour;
    Colour outsideColour;
    boolean drawShaft = true;
    ArrowDirection facing = ArrowDirection.RIGHT;

    public DrawableMuseArrow(double left, double top, double right, double bottom, boolean growFromMiddle,
                             Colour insideColour,
                             Colour outsideColour) {
        super(left, top, right, bottom, growFromMiddle);
        this.insideColour = insideColour;
        this.outsideColour = outsideColour;
    }

    public DrawableMuseArrow(double left, double top, double right, double bottom,
                             Colour insideColour,
                             Colour outsideColour) {
        super(left, top, right, bottom, false);
        this.insideColour = insideColour;
        this.outsideColour = outsideColour;
    }

    public DrawableMuseArrow(MusePoint2D ul, MusePoint2D br,
                             Colour insideColour,
                             Colour outsideColour) {
        super(ul, br);
        this.insideColour = insideColour;
        this.outsideColour = outsideColour;
    }

    public DrawableMuseArrow(MuseRelativeRect ref,
                             Colour insideColour,
                             Colour outsideColour) {
        super(ref.left(), ref.top(), ref.right(), ref.bottom(), ref.growFromMiddle());
        this.insideColour = insideColour;
        this.outsideColour = outsideColour;
    }

    public void setDirection(ArrowDirection facing) {
        this.facing = facing;
    }

    public void setDrawShaft(boolean drawShaft) {
        this.drawShaft = drawShaft;
    }


    void drawArrowShaftpt1() {
        GlStateManager.vertex3f((float)left(), (float) (centery() - (height()* 0.15)), 1);
        GlStateManager.vertex3f((float) (centerx() + (width() * 0.15)), (float) (centery() - (height()* 0.15)), 1);
    }

    void drawArrowHead() {
        GlStateManager.vertex3f(drawShaft ? (float) (centerx() + (width() * 0.15)) : (float)left(), (float) (centery() - (height() * 0.4F)), 1);
        GlStateManager.vertex3f((float)right(), (float)centery(), 1);
        GlStateManager.vertex3f(drawShaft ? (float) (centerx() + (width() * 0.15)) : (float)left(), (float) (centery() + (height() * 0.4F)), 1);
    }

    void drawArrowShaftpt2() {
        GlStateManager.vertex3f((float) (centerx() + (width() * 0.15)), (float) (centery() + (height() * 0.15)), 1);
        GlStateManager.vertex3f((float)left(), (float) (centery() + (height()* 0.15)), 1);
    }

    public void preDraw() {
        RenderState.on2D();
        RenderState.texturelessOn();

        // makes the lines and radii nicer
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT,  GL11.GL_NICEST);
    }

    public void postDraw() {
        Colour.WHITE.doGL();
        RenderState.texturelessOff();
        RenderState.glowOff();
    }

    public void postRender(int mouseX, int mouseY, float partialTicks) {

    }

    public void draw() {
        preDraw();
        GlStateManager.pushMatrix();
        GlStateManager.rotated(facing.getRotation(), 0, 1, 0);


//        // draw arrow head
        GlStateManager.begin(GL11.GL_POLYGON);
        insideColour.doGL();
        drawArrowHead();
        GlStateManager.end();

        if (drawShaft) {
            // draw arrow shaft
            GlStateManager.begin(GL11.GL_POLYGON);
            insideColour.doGL();
            drawArrowShaftpt1();
            drawArrowShaftpt2();
            GlStateManager.end();
        }

        // draw arrow border
        GlStateManager.begin(GL11.GL_LINE_LOOP);
        outsideColour.doGL();
        if (drawShaft)
            drawArrowShaftpt1();
        drawArrowHead();
        if (drawShaft)
            drawArrowShaftpt2();
        GlStateManager.end();

        GlStateManager.popMatrix();
        postDraw();
    }

    public enum ArrowDirection {
        UP(270),
        DOWN(90),
        LEFT(180),
        RIGHT(0);

        int rotation;

        ArrowDirection(int rotation) {
            this.rotation = rotation;
        }

        public int getRotation() {
            return this.rotation;
        }
    }
}