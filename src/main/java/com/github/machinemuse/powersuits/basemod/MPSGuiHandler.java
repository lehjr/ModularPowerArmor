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

package com.github.machinemuse.powersuits.basemod;

import com.github.machinemuse.powersuits.client.gui.crafting.MPSCraftingContainer;
import com.github.machinemuse.powersuits.client.gui.crafting.MPSCraftingGui;
import com.github.machinemuse.powersuits.client.gui.keybind.KeyConfigGui;
import com.github.machinemuse.powersuits.client.gui.modeselection.GuiModeSelector;
import com.github.machinemuse.powersuits.client.gui.scanner.ScannerContainer;
import com.github.machinemuse.powersuits.client.gui.scanner.ScannerGUI;
import com.github.machinemuse.powersuits.client.gui.tinker.cosmetic.CosmeticGui;
import com.github.machinemuse.powersuits.client.gui.tinker.module.TinkerTableGui;
import com.github.machinemuse.powersuits.item.tool.ItemPowerFist;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Gui handler for this mod. Mainly just takes an ID according to what was
 * passed to player.OpenGUI, and opens the corresponding GUI.
 *
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 11/3/16.
 */
public enum MPSGuiHandler implements IGuiHandler {
    INSTANCE;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 3)
            return new MPSCraftingContainer(player.inventory, world, new BlockPos(x, y, z));
        if (ID == 5) {
            return new ScannerContainer(player, getPlayerHand(player));
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        //        Minecraft.getMinecraft().player.addStat(AchievementList.OPEN_INVENTORY, 1);
        switch (ID) {
            case 0:
                return new TinkerTableGui(player, x, y, z);
            case 1:
                return new KeyConfigGui(player, x, y, z);
            case 2:
                return new CosmeticGui(player, x, y, z);
            case 3:
                return new MPSCraftingGui(player.inventory, world, new BlockPos(x, y, z));
            case 4:
                return new GuiModeSelector(player);
            case 5:
                return new ScannerGUI(new ScannerContainer(player, getPlayerHand(player)));
            default:
                return null;
        }
    }

    @Nonnull
    EnumHand getPlayerHand(EntityPlayer player) {
        EnumHand hand;
        hand = player.getActiveHand();
        if (hand == null) {
            ItemStack held = player.getHeldItemMainhand();
            if (!held.isEmpty() && held.getItem() instanceof ItemPowerFist)
                return EnumHand.MAIN_HAND;
            else
                return EnumHand.OFF_HAND;
        }
        return hand;
    }
}
