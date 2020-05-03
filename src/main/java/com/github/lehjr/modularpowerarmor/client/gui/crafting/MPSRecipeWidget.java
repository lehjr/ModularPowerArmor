package com.github.lehjr.modularpowerarmor.client.gui.crafting;

import com.github.lehjr.mpalib.client.gui.geometry.DrawableTile;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.math.Colour;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.RecipeBookPage;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.recipebook.RecipeWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class MPSRecipeWidget extends RecipeWidget {
    Colour disabledBackground = new Colour(0.416F, 0.416F, 0.416F, 1);
    Colour enabledBackground = new Colour(0.545F, 0.545F, 0.545F, 1);
    Colour disabledBorder = new Colour(0.541F, 0.133F, 0.133F, 1);
    Colour enabledBorder = new Colour(0.8F, 0.8F, 0.8F, 1);

    private RecipeBookContainer<?> bookContainer;
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
    public void func_203400_a(RecipeList p_203400_1_, RecipeBookPage page) {
        this.list = p_203400_1_;
        this.bookContainer = (RecipeBookContainer)page.func_203411_d().player.openContainer;
        this.book = page.func_203412_e();
        List<IRecipe<?>> list = p_203400_1_.getRecipes(this.book.isFilteringCraftable(this.bookContainer));

        for(IRecipe<?> irecipe : list) {
            if (this.book.isNew(irecipe)) {
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

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTickTime) {
        tile.setTargetDimensions(new Point2F(x, y), new Point2F(22, 22));
        if (!this.list.containsCraftableRecipes()) {
            tile.setBackgroundColour(disabledBackground);
            tile.setBorderColour(disabledBorder);
        } else {
            tile.setBackgroundColour(enabledBackground);
            tile.setBorderColour(enabledBorder);
        }
        tile.draw(Minecraft.getInstance().currentScreen.getBlitOffset());

        if (!Screen.hasControlDown()) {
            this.time += partialTickTime;
        }

        Minecraft minecraft = Minecraft.getInstance();
        List<IRecipe<?>> list = this.getOrderedRecipes();
        this.currentIndex = MathHelper.floor(this.time / 30.0F) % list.size();
        ItemStack itemstack = list.get(this.currentIndex).getRecipeOutput();
        int k = 4;
        if (this.list.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
            minecraft.getItemRenderer().renderItemAndEffectIntoGUI(itemstack, this.x + k + 1, this.y + k + 1);
            --k;
        }

        minecraft.getItemRenderer().renderItemAndEffectIntoGUI(itemstack, this.x + k, this.y + k);
    }

    private List<IRecipe<?>> getOrderedRecipes() {
        List<IRecipe<?>> list = this.list.getDisplayRecipes(true);
        if (!this.book.isFilteringCraftable(this.bookContainer)) {
            list.addAll(this.list.getDisplayRecipes(false));
        }
        return list;
    }

    @Override
    public boolean isOnlyOption() {
        return this.getOrderedRecipes().size() == 1;
    }

    @Override
    public IRecipe<?> getRecipe() {
        List<IRecipe<?>> list = this.getOrderedRecipes();
        return list.get(this.currentIndex);
    }

    @Override
    public List<String> getToolTipText(Screen screen) {
        ItemStack itemstack = this.getOrderedRecipes().get(this.currentIndex).getRecipeOutput();
        List<String> list = screen.getTooltipFromItem(itemstack);
        if (this.list.getRecipes(this.book.isFilteringCraftable(this.bookContainer)).size() > 1) {
            list.add(I18n.format("gui.recipebook.moreRecipes"));
        }
        return list;
    }

    @Override
    protected boolean isValidClickButton(int mouseButton) {
        return mouseButton == 0 || mouseButton == 1;
    }
}
