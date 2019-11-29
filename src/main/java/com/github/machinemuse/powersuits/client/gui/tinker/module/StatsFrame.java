/*
 * ModularPowersuits (Maintenance builds by lehjr)
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

package com.github.machinemuse.powersuits.client.gui.tinker.module;

import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.nbt.NBTTagAccessor;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Set;

public class StatsFrame extends ScrollableFrame {
    protected NBTTagCompound properties;
    protected ItemStack stack;
    protected Set<String> propertiesToList;

    public StatsFrame(Point2D topleft, Point2D bottomright,
                      Colour borderColour, Colour insideColour, ItemStack stack) {
        super(topleft, bottomright, borderColour, insideColour);
        this.stack = stack;
        this.properties = NBTUtils.getItemTag(stack);
        this.propertiesToList = NBTTagAccessor.getMap(properties).keySet();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushMatrix();
        super.render(mouseX, mouseY, partialTicks);
        int xoffset = 8;
        int yoffset = 8;
        int i = 0;
        for (String propName : propertiesToList) {
            double propValue = NBTUtils.getDoubleOrZero(properties, propName);
            String propValueString = String.format("%.2f", propValue);
            double strlen = Renderer.getStringWidth(propValueString);
            Renderer.drawString(propName, border.left() + xoffset, border.top() + yoffset + i * 10);
            Renderer.drawString(propValueString, border.bottom() - xoffset - strlen - 40, border.top() + yoffset + i * 10);
            i++;
        }
        GL11.glPopMatrix();
    }

    @Override
    public boolean onMouseDown(double x, double y, int button) {
        return false;
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onMouseUp(double x, double y, int button) {
        return false;
        // TODO Auto-generated method stub
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        // TODO Auto-generated method stub
        return null;
    }
}
