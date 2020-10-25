package com.github.lehjr.modularpowerarmor.client.gui.obsolete;

import com.github.lehjr.mpalib.util.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.util.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.util.client.gui.geometry.RelativeRect;
import com.mojang.blaze3d.matrix.MatrixStack;

@Deprecated
public class ScrollableRectangle extends RelativeRect {
    public ScrollableRectangle(RelativeRect relativeRect) {
        super(relativeRect.left(), relativeRect.top(), relativeRect.right(), relativeRect.bottom());
    }

    public ScrollableRectangle(double left, double top, double right, double bottom) {
        super(left, top, right, bottom);
    }

    public ScrollableRectangle(double left, double top, double right, float bottom, boolean growFromMiddle) {
        super(left, top, right, bottom, growFromMiddle);
    }

    public ScrollableRectangle(Point2D ul, Point2D br) {
        super(ul, br);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, float zLevel) {
    }

    public Rect getBorder() {
        return this;
    }
}
