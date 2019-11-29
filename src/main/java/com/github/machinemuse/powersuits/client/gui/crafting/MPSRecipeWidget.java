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

package com.github.machinemuse.powersuits.client.gui.crafting;

import com.github.lehjr.mpalib.client.gui.geometry.DrawableTile;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.client.sound.SoundDictionary;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.recipebook.GuiButtonRecipe;
import net.minecraft.client.gui.recipebook.RecipeBookPage;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class MPSRecipeWidget extends GuiButtonRecipe {
    Colour disabledBackground = new Colour(0.416D, 0.416D, 0.416D, 1);
    Colour enabledBackground = new Colour(0.545D, 0.545D, 0.545D, 1);
    Colour disabledBorder = new Colour(0.541D, 0.133D, 0.133D, 1);
    Colour enabledBorder = new Colour(0.8D, 0.8D, 0.8D, 1);

    private RecipeBook book;
    private RecipeList list;
    private float time;
    private float animationTime;
    private int currentIndex;
    DrawableTile tile;

    public MPSRecipeWidget() {
        tile = new DrawableTile(0, 0, 0, 0, false, disabledBackground, disabledBorder);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, mc.player.getPosition());
            return true;
        }
        return false;
    }

    @Override
    public void init(RecipeList recipeList, RecipeBookPage page, RecipeBook recipeBook) {
        this.list = recipeList;
        this.book = recipeBook;
        List<IRecipe> list = recipeList.getRecipes(recipeBook.isFilteringCraftable());

        for (IRecipe irecipe : list) {
            if (recipeBook.isNew(irecipe)) {
                page.recipesShown(list);
                this.animationTime = 15.0F;
                break;
            }
        }
    }

    @Override
    public RecipeList getList() {
        return this.list;
    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            tile.setTargetDimensions(new Point2D(x, y), new Point2D(22, 22));
            if (!this.list.containsCraftableRecipes()) {
                tile.setBackgroundColour(disabledBackground);
                tile.setBorderColour(disabledBorder);
            } else {
                tile.setBackgroundColour(enabledBackground);
                tile.setBorderColour(enabledBorder);
            }
            tile.draw();

            if (!GuiScreen.isCtrlKeyDown()) {
                this.time += partialTicks;
            }

            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            List<IRecipe> list = this.getOrderedRecipes();
            this.currentIndex = MathHelper.floor(this.time / 30.0F) % list.size();
            ItemStack itemstack = list.get(this.currentIndex).getRecipeOutput();
            int k = 4;
            if (this.list.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemstack, this.x + k + 1, this.y + k + 1);
                --k;
            }

            mc.getRenderItem().renderItemAndEffectIntoGUI(itemstack, this.x + k, this.y + k);
            GlStateManager.enableLighting();
            RenderHelper.disableStandardItemLighting();
        }
    }

    private List<IRecipe> getOrderedRecipes() {
        List<IRecipe> list = this.list.getDisplayRecipes(true);

        if (!this.book.isFilteringCraftable()) {
            list.addAll(this.list.getDisplayRecipes(false));
        }

        return list;
    }

    @Override
    public IRecipe getRecipe() {
        List<IRecipe> list = this.getOrderedRecipes();
        return list.get(this.currentIndex);
    }

    @Override
    public List<String> getToolTipText(GuiScreen p_191772_1_) {
        ItemStack itemstack = this.getOrderedRecipes().get(this.currentIndex).getRecipeOutput();
        List<String> list = p_191772_1_.getItemToolTip(itemstack);

        if (this.list.getRecipes(this.book.isFilteringCraftable()).size() > 1) {
            list.add(I18n.format("gui.recipebook.moreRecipes"));
        }

        return list;
    }

    public int getButtonWidth() {
        return 25;
    }
}