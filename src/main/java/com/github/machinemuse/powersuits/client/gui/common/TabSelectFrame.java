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

package com.github.machinemuse.powersuits.client.gui.common;

import com.github.lehjr.mpalib.client.gui.clickable.ClickableButton;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.machinemuse.powersuits.client.sound.SoundDictionary;
import com.github.machinemuse.powersuits.basemod.ModularPowersuits;
import com.github.machinemuse.powersuits.network.MPSPackets;
import com.github.machinemuse.powersuits.network.packets.CraftingGuiServerSidePacket;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 10/19/16.
 */
public class TabSelectFrame extends Rect implements IGuiFrame {
    EntityPlayer player;

    int worldx;
    int worldy;
    int worldz;

    List<ClickableButton> buttons = new ArrayList<>();

    public TabSelectFrame(EntityPlayer player, int exclude, int worldx, int worldy, int worldz) {
        super(0, 0, 0, 0);
        this.player = player;

        this.worldx = worldx;
        this.worldy = worldy;
        this.worldz = worldz;

        BlockPos pos = new BlockPos(worldx, worldy, worldz);

        ClickableButton button;
        if (exclude != 0) {
            button = new ClickableButton(I18n.format("gui.powersuits.tab.tinker"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, pos);
                player.openGui(ModularPowersuits.getInstance(), 0, player.world, worldx, worldy, worldz);
            });
            buttons.add(button);
        }

        if (exclude !=1) {
            button = new ClickableButton(I18n.format("gui.powersuits.tab.keybinds"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, pos);
                player.openGui(ModularPowersuits.getInstance(), 1, player.world, worldx, worldy, worldz);
            });
            buttons.add(button);
        }

        if (exclude !=2) {
            button = new ClickableButton(I18n.format("gui.powersuits.tab.visual"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, pos);
                player.openGui(ModularPowersuits.getInstance(), 2, player.world, worldx, worldy, worldz);
            });
            buttons.add(button);
        }

        if (exclude != 3) {
            button = new ClickableButton(I18n.format("container.crafting"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                MPSPackets.INSTANCE.sendToServer(new CraftingGuiServerSidePacket());

                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, pos);
                player.openGui(ModularPowersuits.getInstance(), 3, player.world, worldx, worldy, worldz);
            });
            buttons.add(button);
        }

        for(ClickableButton b : buttons) {
            b.setVisible(true);
        }
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        this.setTargetDimensions(left, top, right, bottom);
        double totalButtonWidth = 0;
        for (ClickableButton button : buttons) {
            totalButtonWidth += (button.getRadius().getX() * 2);
        }
        // totalButtonWidth greater than width will produce a negative spacing value
        double spacing = (this.width() - totalButtonWidth) / (buttons.size() +1);

        double x = spacing; // first entry may be negative and will allow an oversized tab frame to be centered
        for (ClickableButton button : buttons) {
            button.setPosition(new Point2D(this.left() + x + button.getRadius().getX(), this.top() -6));
            x += Math.abs(spacing) + button.getRadius().getX() * 2;
        }
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }

        for (ClickableButton b : buttons) {
            if (b.isEnabled() && b.hitBox(mouseX, mouseY)) {
                b.onPressed();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMouseUp(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public void update(double mousex, double mousey) {
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        for (ClickableButton b : buttons) {
            b.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        return null;
    }
}