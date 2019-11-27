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

package com.github.machinemuse.powersuits.client.gui.tinker.module;

import com.github.lehjr.mpalib.client.gui.ContainerlessGui;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import com.github.machinemuse.powersuits.client.gui.common.TabSelectFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

/**
 * The gui class for the TinkerTable block.
 *
 * @author MachineMuse
 */
public class TinkerTableGui extends ContainerlessGui {
    protected final EntityPlayerSP player;
    protected ItemSelectionFrame itemSelectFrame;
    protected DetailedSummaryFrame statsFrame;
    protected ModuleSelectionFrame moduleSelectFrame;
    protected InstallSalvageFrame installFrame;
    protected TabSelectFrame tabFrame;
    protected ModuleTweakFrame tweakFrame;
    protected Point2D tweakFrameUL, tweakFrameBR;
    protected int worldx;
    protected int worldy;
    protected int worldz;

    protected static double TWEAK_FRAME_SCALERATIO = 0.75;

    /**
     * Constructor. Takes a player as an argument.
     *
     * @param player
     */
    public TinkerTableGui(EntityPlayer player) {
        this.player = (EntityPlayerSP) player;
        ScaledResolution screen = new ScaledResolution(Minecraft.getMinecraft());
        this.xSize = Math.min(screen.getScaledWidth() - 50, 500);
        this.ySize = Math.min(screen.getScaledHeight() - 50, 300);
    }

    public TinkerTableGui(EntityPlayer player, int x, int y, int z) {
        this.player = (EntityPlayerSP) player;
        this.worldx = x;
        this.worldy = y;
        this.worldz = z;

        rescale();

        // setup all frames here, since they are no longer recreated in the initGUI section

        itemSelectFrame = new ItemSelectionFrame(
                new Point2D(absX(-0.95F), absY(-0.95F)),
                new Point2D(absX(-0.78F), absY(0.95F)),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHTBLUE.withAlpha(0.8F),
                player);
        frames.add(itemSelectFrame);

        statsFrame = new DetailedSummaryFrame(player,
                new Point2D(absX(0f), absY(-0.9f)),
                new Point2D(absX(0.95f), absY(-0.3f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8),
                itemSelectFrame);
        frames.add(statsFrame);

        moduleSelectFrame = new ModuleSelectionFrame(
                new Point2D(absX(-0.75F), absY(-0.95f)),
                new Point2D(absX(-0.05F), absY(0.55f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8), itemSelectFrame);
        frames.add(moduleSelectFrame);

        installFrame = new InstallSalvageFrame(
                player,
                new Point2D(absX(-0.75F), absY(0.6f)),
                new Point2D(absX(-0.05F), absY(0.95f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8),
                itemSelectFrame, moduleSelectFrame);
        frames.add(installFrame);

        tweakFrame = new ModuleTweakFrame(player,
                new Point2D(absX(0f), absY(-0.25f)),
                new Point2D(absX(0.95f), absY(0.95f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8),
                itemSelectFrame,
                moduleSelectFrame);
        frames.add(tweakFrame);

        tabFrame = new TabSelectFrame(player, 0, worldx, worldy, worldz);
        frames.add(tabFrame);

        itemSelectFrame.setDoOnNewSelect(doThis-> {
            moduleSelectFrame.loadModules(false);
        });
    }

    public void rescale() {
        ScaledResolution screen = new ScaledResolution(Minecraft.getMinecraft());
        this.setXSize(Math.min(screen.getScaledWidth() - 50, 500));
        this.setYSize(Math.min(screen.getScaledHeight() - 50, 300));
    }


    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void initGui() {
        super.initGui();
        rescale();
        backgroundRect.setTargetDimensions(absX(-1), absY(-1), absX(1), absY(1));
        itemSelectFrame.init(absX(-0.975F), absY(-0.95F), absX(-0.78F), absY(0.95F));
        statsFrame.init(absX(-0.025F), absY(-0.95F), absX(0.975f), absY(-0.3f));
        installFrame.init(absX(-0.75F), absY(0.6f), absX(-0.05F), absY(0.95f));
        moduleSelectFrame.init(absX(-0.75F), absY(-0.95f), absX(-0.05F), absY(0.55f));
        tabFrame.init(absX(-0.95F), absY(-1.05f), absX(0.95F), absY(-0.95f));
        tweakFrame.init(absX(-0.025F),  absY(-0.25f), absX(0.975f), absY(0.95f));
    }

    @Override
    public void drawScreen(int x, int y, float z) {
        super.drawScreen(x, y, z);
        if (itemSelectFrame.hasNoItems()) {
            double centerx = absX(0);
            double centery = absY(0);
            Renderer.drawCenteredString(I18n.format("gui.powersuits.noModulesFound.line1"), centerx, centery - 5);
            Renderer.drawCenteredString(I18n.format("gui.powersuits.noModulesFound.line2"), centerx, centery + 5);
        }
    }
}