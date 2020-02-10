package com.github.lehjr.modularpowerarmor.client.gui.obsolete;

import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;

@Deprecated
public class ScrollableRectangle extends RelativeRect {
    public ScrollableRectangle(RelativeRect relativeRect) {
        super(relativeRect.left(), relativeRect.top(), relativeRect.right(), relativeRect.bottom());
    }

    public ScrollableRectangle(double left, double top, double right, double bottom) {
        super(left, top, right, bottom);
    }

    public ScrollableRectangle(double left, double top, double right, double bottom, boolean growFromMiddle) {
        super(left, top, right, bottom, growFromMiddle);
    }

    public ScrollableRectangle(Point2D ul, Point2D br) {
        super(ul, br);
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
    }

    public Rect getBorder() {
        return this;
    }
}
