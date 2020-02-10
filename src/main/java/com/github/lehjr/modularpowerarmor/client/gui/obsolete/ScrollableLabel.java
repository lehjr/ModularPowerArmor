package com.github.lehjr.modularpowerarmor.client.gui.obsolete;

import com.github.lehjr.mpalib.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;

@Deprecated
public class ScrollableLabel extends ScrollableRectangle {
    ClickableLabel label;
    boolean enabled = true;

    public ScrollableLabel(ClickableLabel label, RelativeRect relativeRect) {
        super(relativeRect);
        this.label = label;
    }

    public ScrollableLabel setMode(int mode) {
        this.label = this.label.setMode(mode);
        return this;
    }

    public void setText(String text) {
        label.setLabel(text);
    }

    public boolean hitbox(double x, double y) {
        return enabled && label.hitBox(x, y);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (enabled)
            label.render(mouseX, mouseY, partialTicks);
    }
}