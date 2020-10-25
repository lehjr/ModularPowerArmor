package com.github.lehjr.modularpowerarmor.client.gui.obsolete;

import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.util.client.gui.geometry.RelativeRect;
import com.mojang.blaze3d.matrix.MatrixStack;

@Deprecated
public class ScrollableLabel extends ScrollableRectangle {
    ClickableLabel label;
    boolean enabled = true;

    public ScrollableLabel(ClickableLabel label, RelativeRect relativeRect) {
        super(relativeRect);
        this.label = label;
    }

    public ScrollableLabel setMode(ClickableLabel.JustifyMode mode) {
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, float zLevel) {
        if (enabled) {
            label.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);
        }
    }
}