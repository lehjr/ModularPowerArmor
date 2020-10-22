package com.github.lehjr.modularpowerarmor.client.gui.obsolete;

import com.github.lehjr.mpalib.client.gui.clickable.ClickableSlider;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;

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
    public void render(int mouseX, int mouseY, float partialTicks, float zLevel) {
        slider.render(mouseX, mouseY, partialTicks, zLevel);
    }
}
