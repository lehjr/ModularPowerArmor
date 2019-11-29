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

import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.GuiButtonRecipeTab;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class MPSRecipeTabToggleWidget extends GuiButtonRecipeTab {
    private final CreativeTabs category;
    private final Colour activeColor = new Colour(0.1F, 0.3F, 0.4F, 0.7F);
    private final Colour inactiveColor = Colour.DARKBLUE.withAlpha(0.8);

    DrawableRect tabRectangle;

    public MPSRecipeTabToggleWidget(int p_i47588_1_, CreativeTabs category) {
        super(p_i47588_1_, category);
        this.initTextureValues(153, 2, 35, 0, MPSRecipeBookGui.RECIPE_BOOK);
        this.category = category;
        this.tabRectangle = new DrawableRect(0, 0, 0, 0, inactiveColor, Colour.LIGHTBLUE.withAlpha(0.8));
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if(this.visible) {
            int xChange = this.stateTriggered ? 2 : 0;
            tabRectangle.setTargetDimensions(new Point2D(this.x - xChange, this.y), new Point2D(28 + xChange, 27));
            tabRectangle.setBackgroundColour(this.stateTriggered ? activeColor : inactiveColor);
            tabRectangle.draw();
            // render the item models
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            this.renderIcon(mc.getRenderItem());
            GlStateManager.enableLighting();
            RenderHelper.disableStandardItemLighting();
        }
    }

    /**
     * Renders the item icons for the tabs.
     */
    private void renderIcon(RenderItem renderItem) {
        ItemStack itemstack = this.category.getIcon();

        if (this.category == CreativeTabs.TOOLS) {
            renderItem.renderItemAndEffectIntoGUI(itemstack, this.x + 3, this.y + 5);
            renderItem.renderItemAndEffectIntoGUI(CreativeTabs.COMBAT.getIcon(), this.x + 14, this.y + 5);
        } else if (this.category == CreativeTabs.MISC) {
            renderItem.renderItemAndEffectIntoGUI(itemstack, this.x + 3, this.y + 5);
            renderItem.renderItemAndEffectIntoGUI(CreativeTabs.FOOD.getIcon(), this.x + 14, this.y + 5);
        } else {
            renderItem.renderItemAndEffectIntoGUI(itemstack, this.x + 9, this.y + 5);
        }
    }

    @Override
    public CreativeTabs getCategory() {
        return this.category;
    }

    @Override
    public boolean updateVisibility() {
        List<RecipeList> list = (List) RecipeBookClient.RECIPES_BY_TAB.get(this.category);
        this.visible = false;

        for (RecipeList recipelist : list) {
            if (recipelist.isNotEmpty() && recipelist.containsValidRecipes()) {
                this.visible = true;
                break;
            }
        }

        return this.visible;
    }
}