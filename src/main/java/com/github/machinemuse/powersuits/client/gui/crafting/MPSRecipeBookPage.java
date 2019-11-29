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

import com.github.lehjr.mpalib.client.gui.clickable.ClickableArrow;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableArrow;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.client.sound.SoundDictionary;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.*;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class MPSRecipeBookPage extends RecipeBookPage {
    private final List<MPSRecipeWidget> buttons = Lists.newArrayListWithCapacity(20);
    private MPSRecipeWidget hoveredButton;
    private GuiRecipeOverlay overlay = new GuiRecipeOverlay();
    private Minecraft minecraft;
    private List<IRecipeUpdateListener> listeners = new ArrayList<>();
    private List<RecipeList> recipeLists;
    private ClickableArrow forwardArrow;
    private ClickableArrow backArrow;
    private int totalPages;
    private int currentPage;
    private RecipeBook recipeBook;
    private IRecipe lastClickedRecipe;
    private RecipeList lastClickedRecipeList;
    private final Colour arrowBorderColour = Colour.LIGHTBLUE.withAlpha(0.8);
    private final Colour arrowNormalBackGound = new Colour(0.1F, 0.3F, 0.4F, 0.7F);
    private final Colour arrowHighlightedBackground = Colour.WHITE;

    public MPSRecipeBookPage() {
        for (int i = 0; i < 20; ++i) {
            this.buttons.add(new MPSRecipeWidget());
        }

        forwardArrow = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        forwardArrow.setDrawShaft(false);

        backArrow = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        backArrow.setDrawShaft(false);
        backArrow.setDirection(DrawableArrow.ArrowDirection.LEFT);
    }

    @Override
    public void init(Minecraft minecraft, int x, int y) {
        this.minecraft = minecraft;
        this.recipeBook = minecraft.player.getRecipeBook();

        for (int i = 0; i < this.buttons.size(); ++i) {
            this.buttons.get(i).setPosition(x + 11 + 25 * (i % 5), y + 31 + 25 * (i / 5));
        }

        this.forwardArrow.setTargetDimensions(new Point2D(x + 93, y + 137), new Point2D(12, 17));
        this.backArrow.setTargetDimensions(new Point2D(x + 38, y + 137), new Point2D(12, 17));
    }

    @Override
    public void addListener(GuiRecipeBook recipeBook) {
        this.listeners.remove(recipeBook);
        this.listeners.add(recipeBook);
    }

    @Override
    public void updateLists(List<RecipeList> recipeLists, boolean p_194192_2_) {
        this.recipeLists = recipeLists;
        this.totalPages = (int) Math.ceil((double) recipeLists.size() / 20.0D);
        if (this.totalPages <= this.currentPage || p_194192_2_) {
            this.currentPage = 0;
        }

        this.updateButtonsForPage();
    }

    private void updateButtonsForPage() {
        int i = 20 * this.currentPage;

        for (int j = 0; j < this.buttons.size(); ++j) {
            MPSRecipeWidget recipewidget = this.buttons.get(j);
            if (i + j < this.recipeLists.size()) {
                RecipeList recipelist = this.recipeLists.get(i + j);
                recipewidget.init(recipelist, this, this.recipeBook);
                recipewidget.visible = true;
            } else {
                recipewidget.visible = false;
            }
        }

        this.updateArrowButtons();
    }

    private void updateArrowButtons() {
        this.forwardArrow.setVisible(this.totalPages > 1 && this.currentPage < this.totalPages - 1);
        this.backArrow.setVisible(this.totalPages > 1 && this.currentPage > 0);
    }

    @Override
    public void render(int x, int y, int mouseX, int mouseY, float partialTicks) {
        if (this.totalPages > 1) {
            String s = this.currentPage + 1 + "/" + this.totalPages;
            int i = this.minecraft.fontRenderer.getStringWidth(s);
            this.minecraft.fontRenderer.drawString(s, x - i / 2 + 73, y + 141, -1);
        }

        RenderHelper.disableStandardItemLighting();
        this.hoveredButton = null;

        for (MPSRecipeWidget recipewidget : this.buttons) {
            recipewidget.drawButton(this.minecraft, mouseX, mouseY, partialTicks);
            if (recipewidget.visible && recipewidget.isMouseOver()) {
                this.hoveredButton = recipewidget;
            }
        }

        forwardArrow.render(mouseX, mouseY, partialTicks);
        backArrow.render(mouseX, mouseY, partialTicks);
        this.overlay.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderTooltip(int mouseX, int mouseY) {
        if (this.minecraft.currentScreen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
            this.minecraft.currentScreen.drawHoveringText(this.hoveredButton.getToolTipText(this.minecraft.currentScreen), mouseX, mouseY);
        }
    }

    @Override
    @Nullable
    public IRecipe getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Override
    @Nullable
    public RecipeList getLastClickedRecipeList() {
        return this.lastClickedRecipeList;
    }

    @Override
    public void setInvisible() {
        this.overlay.setVisible(false);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton, int p_194196_4_, int p_194196_5_, int p_194196_6_, int p_194196_7_) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeList = null;
        if (this.overlay.isVisible()) {
            if (this.overlay.buttonClicked(mouseX, mouseY, mouseButton)) {
                this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
                this.lastClickedRecipeList = this.overlay.getRecipeList();
            } else {
                this.overlay.setVisible(false);
            }
            return true;
        } else if (forwardArrow.hitBox(mouseX, mouseY) && mouseButton == 0) {
            Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, minecraft.player.getPosition());
            ++this.currentPage;
            this.updateButtonsForPage();
            return true;

        } else if (this.backArrow.hitBox(mouseX, mouseY) && mouseButton == 0) {
            Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER,1, minecraft.player.getPosition());
            --this.currentPage;
            this.updateButtonsForPage();
            return true;
        } else {
            for (MPSRecipeWidget recipewidget : this.buttons) {
                if (recipewidget.mousePressed(minecraft, mouseX, mouseY)) {
                    if (mouseButton == 0) {
                        this.lastClickedRecipe = recipewidget.getRecipe();
                        this.lastClickedRecipeList = recipewidget.getList();
                    } else if (mouseButton == 1 && !this.overlay.isVisible() && !recipewidget.isOnlyOption()) {
                        this.overlay.init(this.minecraft, recipewidget.getList(), recipewidget.x, recipewidget.y, p_194196_4_ + p_194196_6_ / 2, p_194196_5_ + 13 + p_194196_7_ / 2, (float) recipewidget.getButtonWidth(), this.recipeBook);
                    }

                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public void recipesShown(List<IRecipe> iRecipeList) {
        for (IRecipeUpdateListener irecipeupdatelistener : this.listeners) {
            irecipeupdatelistener.recipesShown(iRecipeList);
        }
    }
}