package com.github.lehjr.modularpowerarmor.client.gui.clickable;

import com.github.lehjr.mpalib.client.gui.clickable.ClickableSlider;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Ported to Java by lehjr on 10/19/16.
 */
public class ClickableTinkerSlider extends ClickableSlider {
    public NBTTagCompound moduleTag;

    public ClickableTinkerSlider(Point2D topmiddle, double width, NBTTagCompound moduleTag, String id, String label) {
        super(topmiddle, width, id, label);
        this.moduleTag = moduleTag;
    }

    @Override
    public double getValue() {
        return (moduleTag.hasKey(this.id())) ? moduleTag.getDouble(id()) : 0;
    }

    @Override
    public void setValueByX(double x) {
        super.setValueByX(x);
        moduleTag.setDouble(id(), super.getValue());
    }
}