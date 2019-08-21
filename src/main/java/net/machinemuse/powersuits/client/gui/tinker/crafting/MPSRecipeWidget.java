package net.machinemuse.powersuits.client.gui.tinker.crafting;

import com.mojang.blaze3d.platform.GlStateManager;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseTile;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.math.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.RecipeBookPage;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.recipebook.RecipeWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class MPSRecipeWidget extends RecipeWidget {
    Colour disabledBackground = new Colour(0.416D, 0.416D, 0.416D, 1);
    Colour enabledBackground = new Colour(0.545D, 0.545D, 0.545D, 1);
    Colour disabledBorder = new Colour(0.541D, 0.133D, 0.133D, 1);
    Colour enabledBorder = new Colour(0.8D, 0.8D, 0.8D, 1);

    private RecipeBookContainer<?> bookContainer;
    private RecipeBook book;
    private RecipeList list;
    private float time;
    private float animationTime;
    private int currentIndex;

    DrawableMuseTile tile;

    public MPSRecipeWidget() {
        tile = new DrawableMuseTile(0, 0, 0, 0, false, disabledBackground, disabledBorder);
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
        tile.setTargetDimensions(new MusePoint2D(x, y), new MusePoint2D(22, 22));
        if (!this.list.containsCraftableRecipes()) {
            tile.setBackgroundColour(disabledBackground);
            tile.setBorderColour(disabledBorder);
        } else {
            tile.setBackgroundColour(enabledBackground);
            tile.setBorderColour(enabledBorder);
        }
        tile.draw();

        if (!Screen.hasControlDown()) {
            this.time += partialTickTime;
        }

        RenderHelper.enableGUIStandardItemLighting();
        Minecraft minecraft = Minecraft.getInstance();
//        GlStateManager.disableLighting();
        List<IRecipe<?>> list = this.getOrderedRecipes();
        this.currentIndex = MathHelper.floor(this.time / 30.0F) % list.size();
        ItemStack itemstack = list.get(this.currentIndex).getRecipeOutput();
        int k = 4;
        if (this.list.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
            minecraft.getItemRenderer().renderItemAndEffectIntoGUI(itemstack, this.x + k + 1, this.y + k + 1);
            --k;
        }

        minecraft.getItemRenderer().renderItemAndEffectIntoGUI(itemstack, this.x + k, this.y + k);
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
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
