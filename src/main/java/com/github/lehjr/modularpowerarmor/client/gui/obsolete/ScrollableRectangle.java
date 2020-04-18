package com.github.lehjr.modularpowerarmor.client.gui.obsolete;

import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;

@Deprecated
public class ScrollableRectangle extends RelativeRect {
    public ScrollableRectangle(RelativeRect relativeRect) {
        super(relativeRect.left(), relativeRect.top(), relativeRect.right(), relativeRect.bottom());
    }

    public ScrollableRectangle(float left, float top, float right, float bottom) {
        super(left, top, right, bottom);
    }

    public ScrollableRectangle(float left, float top, float right, float bottom, boolean growFromMiddle) {
        super(left, top, right, bottom, growFromMiddle);
    }

    public ScrollableRectangle(Point2F ul, Point2F br) {
        super(ul, br);
    }

    public void render(int mouseX, int mouseY, float partialTicks, float zLevel) {
    }

    public Rect getBorder() {
        return this;
    }
}
