///*
// * Copyright (c) 2019 MachineMuse, Lehjr
// * All rights reserved.
// *
// * Redistribution and use in source and binary forms, with or without
// * modification, are permitted provided that the following conditions are met:
// *
// *  * Redistributions of source code must retain the above copyright notice, this
// *    list of conditions and the following disclaimer.
// *
// *  * Redistributions in binary form must reproduce the above copyright notice,
// *    this list of conditions and the following disclaimer in the documentation
// *    and/or other materials provided with the distribution.
// *
// * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
// */
//
//package com.github.lehjr.mpalib.client.gui.frame;
//
//import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
//import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
//import com.github.lehjr.mpalib.client.render.Renderer;
//import com.github.lehjr.mpalib.math.Colour;
//import com.github.lehjr.mpalib.nbt.NBTTagAccessor;
//import com.github.lehjr.mpalib.nbt.NBTUtils;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.text.ITextComponent;
//import org.lwjgl.opengl.GL11;
//
//import java.util.List;
//import java.util.Set;
//
///**
// * Currently Unused. Left for reference
// *
// * nothing works this way anymore. May fix for later
// */
//public class StatsFrame extends ScrollableFrame {
//    protected CompoundNBT properties;
//    protected ItemStack stack;
//    protected Set<String> propertiesToList;
//
//    public StatsFrame(Point2F topleft, Point2F bottomright, float zLevel,
//                      Colour borderColour, Colour insideColour, ItemStack stack) {
//        super(topleft, bottomright, zLevel, borderColour, insideColour);
//        this.stack = stack;
//        this.properties = NBTUtils.getMuseItemTag(stack);
//        this.propertiesToList = NBTTagAccessor.getMap(properties).keySet();
//    }
//
//    @Override
//    public void render(int mouseX, int mouseY, float partialTicks) {
//        GL11.glPushMatrix();
//        super.render(mouseX, mouseY, partialTicks);
//        int xoffset = 8;
//        int yoffset = 8;
//        int i = 0;
//        for (String propName : propertiesToList) {
//            double propValue = NBTUtils.getDoubleOrZero(properties, propName);
//            String propValueString = String.format("%.2f", propValue);
//            double strlen = Renderer.getStringWidth(propValueString);
//            Renderer.drawString(propName, border.left() + xoffset, border.top() + yoffset + i * 10);
//            Renderer.drawString(propValueString, border.bottom() - xoffset - strlen - 40, border.top() + yoffset + i * 10);
//            i++;
//        }
//        GL11.glPopMatrix();
//    }
//
//    @Override
//    public boolean mouseClicked(double x, double y, int button) {
//        return false;
//    }
//
//    @Override
//    public boolean mouseReleased(double x, double y, int button) {
//        return false;
//    }
//
//    @Override
//    public List<ITextComponent> getToolTip(int x, int y) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//}
