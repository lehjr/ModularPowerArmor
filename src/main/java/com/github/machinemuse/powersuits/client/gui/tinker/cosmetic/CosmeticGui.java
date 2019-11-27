/*
 * Copyright (c) ${DATE} MachineMuse, Lehjr
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

package com.github.machinemuse.powersuits.client.gui.tinker.cosmetic;

import com.github.lehjr.mpalib.client.gui.ContainerlessGui;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import com.github.machinemuse.powersuits.client.gui.common.TabSelectFrame;
import com.github.machinemuse.powersuits.config.MPSConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 6:32 PM, 29/04/13
 * <p>
 * Ported to Java by lehjr on 10/19/16.
 */
public class CosmeticGui extends ContainerlessGui {
    EntityPlayer player;
    int worldx;
    int worldy;
    int worldz;
    ItemSelectionFrame itemSelect;
    PlayerModelViewFrame renderframe;
    LoadSaveResetSubFrame loadSaveResetSubFrame;
    ColourPickerFrame colourpicker;
    PartManipContainer partframe;
    CosmeticPresetContainer cosmeticFrame;
    TabSelectFrame tabFrame;

    protected final boolean allowCosmeticPresetCreation;
    protected final boolean usingCosmeticPresets;

    public CosmeticGui(EntityPlayer player, int worldx, int worldy, int worldz) {
        this.player = player;
        this.worldx = worldx;
        this.worldy = worldy;
        this.worldz = worldz;

        rescale();

        usingCosmeticPresets = !MPSConfig.INSTANCE.useLegacyCosmeticSystem();
        if (usingCosmeticPresets) {
            // check if player is the server owner
            if (FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) {
                allowCosmeticPresetCreation = player.getName().equals(FMLCommonHandler.instance().getMinecraftServerInstance().getServerOwner());
            } else {
                // check if player is top level op
                UserListOpsEntry opEntry =  FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
                int opLevel = opEntry != null ? opEntry.getPermissionLevel() : 0;
                allowCosmeticPresetCreation = opLevel == 4;
            }
        } else allowCosmeticPresetCreation = false;


        itemSelect = new ItemSelectionFrame(
                new Point2D(absX(-0.95F), absY(-0.95F)),
                new Point2D(absX(-0.78F), absY(-0.025F)),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHTBLUE.withAlpha(0.8F),
                player);
        frames.add(itemSelect);

        renderframe = new PlayerModelViewFrame(
                itemSelect,
                new Point2D(absX(-0.75F), absY(-0.95f)),
                new Point2D(absX(0.15F), absY(-0.025f)),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHTBLUE.withAlpha(0.8F));
        frames.add(renderframe);

        colourpicker = new ColourPickerFrame(
                new Point2D(absX(0.18f), absY(-0.95f)),

                new Point2D(absX(0.95f), absY(-0.27f)),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHTBLUE.withAlpha(0.8F),
                itemSelect);
        frames.add(colourpicker);

        partframe = new PartManipContainer(
                itemSelect, colourpicker,
                new Point2D(absX(-0.75F), absY(0.025f)),
                new Point2D(absX(+0.95F), absY(0.95f)),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHTBLUE.withAlpha(0.8F));
        frames.add(partframe);

        cosmeticFrame = new CosmeticPresetContainer(
                itemSelect, colourpicker,
                new Point2D(absX(-0.75F), absY(0.025f)),
                new Point2D(absX(+0.95F), absY(0.95f)),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHTBLUE.withAlpha(0.8F));
        frames.add(cosmeticFrame);

        // if not using presets then only the reset button is displayed
        loadSaveResetSubFrame = new LoadSaveResetSubFrame(
                colourpicker,
                player,
                new Rect(
                        absX(0.18f),
                        absY(-0.23f),
                        absX(0.95f),
                        absY(-0.025f)),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHTBLUE.withAlpha(0.8F),
                itemSelect,
                usingCosmeticPresets,
                allowCosmeticPresetCreation,
                partframe,
                cosmeticFrame);
        frames.add(loadSaveResetSubFrame);

        tabFrame = new TabSelectFrame(player, 2, worldx, worldy, worldz);
        frames.add(tabFrame);
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
        itemSelect.init(absX(-0.975F), absY(-0.95F), absX(-0.78F), absY(0.95F));



        renderframe.init(absX(-0.75F), absY(-0.95f), absX(0.15F), absY(-0.025f));


        colourpicker.init(absX(0.18f), absY(-0.95f), absX(0.95f), absY(-0.27f));


        partframe.init(absX(-0.75F), absY(0.025f), absX(+0.95F), absY(0.95f));


        cosmeticFrame.init(absX(-0.95F), absY(0.025f), absX(+0.95F), absY(0.95f));


        loadSaveResetSubFrame.init(absX(0.18f), absY(-0.23f), absX(0.95f), absY(-0.025f));


        tabFrame.init(absX(-0.95F), absY(-1.05f), absX(0.95F), absY(-0.95f));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        loadSaveResetSubFrame.onGuiClosed();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (loadSaveResetSubFrame != null)
            loadSaveResetSubFrame.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int x, int y, float z) {
        super.drawScreen(x, y, z);
    }
}