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

package com.github.machinemuse.powersuits.client.gui.modeselection;

import com.github.lehjr.mpalib.client.gui.ContainerlessGui;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiModeSelector extends ContainerlessGui {
    EntityPlayer player;
    RadialModeSelectionFrame radialSelect;

    public GuiModeSelector(EntityPlayer player) {
        this.player = player;
        ScaledResolution screen = new ScaledResolution(Minecraft.getMinecraft());
        this.xSize = Math.min(screen.getScaledWidth() - 50, 500);
        this.ySize = Math.min(screen.getScaledHeight() - 50, 300);
    }

    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void initGui() {
        super.initGui();
        radialSelect = new RadialModeSelectionFrame(
                new Point2D(absX(-0.5F), absY(-0.5F)),
                new Point2D(absX(0.5F), absY(0.5F)),
                player);
        frames.add(radialSelect);
    }

    @Override
    public void drawBackground() {

    }

    @Override
    public void update(double x, double y) {
        super.update(x, y);
        if (!Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindsHotbar[player.inventory.currentItem].getKeyCode())) {
            //close animation
            //TODO
            //close Gui
            try {
                keyTyped('1', 1);
            } catch (IOException e) {
            }
        }
    }
}