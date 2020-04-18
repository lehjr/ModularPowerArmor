package com.github.lehjr.modularpowerarmor.container;

import com.github.lehjr.mpalib.client.gui.geometry.Point2F;

public interface IHideableSlot {
    void enable();

    void disable();

    boolean isEnabled();

    void setPosition(Point2F position);
}
