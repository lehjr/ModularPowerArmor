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

package com.github.machinemuse.powersuits.client.gui.keybind;

import com.github.lehjr.mpalib.client.gui.ContainerlessGui;
import com.github.machinemuse.powersuits.client.control.KeybindManager;
import com.github.machinemuse.powersuits.client.gui.common.TabSelectFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class KeyConfigGui extends ContainerlessGui {
    protected KeybindConfigFrame keybindConfigFrame;
    protected TabSelectFrame tabFrame;
    protected int worldx;
    protected int worldy;
    protected int worldz;

    private EntityPlayer player;

    public KeyConfigGui(EntityPlayer player, int x, int y, int z) {
        super();
        KeybindManager.readInKeybinds();
        this.player = player;
        ScaledResolution screen = new ScaledResolution(Minecraft.getMinecraft());
        this.xSize = screen.getScaledWidth() - 50;
        this.ySize = screen.getScaledHeight() - 50;

        this.worldx = x;
        this.worldy = y;
        this.worldz = z;


        keybindConfigFrame = new KeybindConfigFrame(this, player);
        frames.add(keybindConfigFrame);

        tabFrame = new TabSelectFrame(player, 1, worldx, worldy, worldz);
        frames.add(tabFrame);
    }

    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void initGui() {
        super.initGui();
        keybindConfigFrame.init(absX(-0.95), absY(-0.95), absX(0.95), absY(0.95));
        tabFrame.init(absX(-0.95F), absY(-1.05f), absX(0.95F), absY(-0.95f));
    }

    @Override
    public void handleKeyboardInput() {
        try {
            super.handleKeyboardInput();
            keybindConfigFrame.handleKeyboard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        KeybindManager.writeOutKeybinds();
    }
}