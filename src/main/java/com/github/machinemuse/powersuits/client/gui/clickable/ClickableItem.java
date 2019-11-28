/*
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.client.gui.clickable;

import com.github.lehjr.mpalib.client.gui.clickable.Clickable;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Extends the Clickable class to add a clickable IC2ItemTest - note that this
 * will be a button that looks like the item, not a container slot.
 *
 * @author MachineMuse
 */
public class ClickableItem extends Clickable {
    public static final int offsetx = 8;
    public static final int offsety = 8;
    public static RenderItem itemRenderer;
    public int inventorySlot;
    protected ItemStack item;

    public ClickableItem(@Nonnull ItemStack item, Point2D pos, int inventorySlot) {
        super(pos);
        this.inventorySlot = inventorySlot;
        this.item = item;
    }

    @Nonnull
    public ItemStack getStack() {
        return item;
    }

    @Override
    public boolean hitBox(double x, double y) {
        boolean hitx = Math.abs(x - getPosition().getX()) < offsetx;
        boolean hity = Math.abs(y - getPosition().getY()) < offsety;
        return hitx && hity;
    }

    @Override
    public List<String> getToolTip() {
        return item.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL);
    }

    /**
     * Draws the specified itemstack at the *relative* coordinates getX,getY. Used
     * mainly in clickables.
     */

    @Override
    public void render(int i, int i1, float v) {
        Renderer.drawItemAt(
                getPosition().getX() - offsetx,
                getPosition().getY() - offsety, item);
        if (inventorySlot > 35 || Minecraft.getMinecraft().player.inventory.getCurrentItem() == item) {
            Renderer.drawString("e", getPosition().getX() + 3, getPosition().getY() + 1, Colour.DARKGREEN);
        }
    }
}