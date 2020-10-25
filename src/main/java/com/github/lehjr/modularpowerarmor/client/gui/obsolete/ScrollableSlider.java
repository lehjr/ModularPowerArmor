package com.github.lehjr.modularpowerarmor.client.gui.obsolete;

import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableSlider;
import com.github.lehjr.mpalib.util.client.gui.geometry.RelativeRect;
import com.mojang.blaze3d.matrix.MatrixStack;

@Deprecated
public class ScrollableSlider extends ScrollableRectangle {
    ClickableSlider slider;

    public ScrollableSlider(ClickableSlider slider, RelativeRect relativeRect) {
        super(relativeRect);
        this.slider = slider;
    }

    public double getValue() {
        return slider.getValue();
    }

    public void setValue(double value) {
        slider.setValue(value);
    }

    public ClickableSlider getSlider() {
        return slider;
    }

    public boolean hitBox(float x, float y) {
        return slider.hitBox(x, y);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, float zLevel) {
        slider.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);
    }
}
