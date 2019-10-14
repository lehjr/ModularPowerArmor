package com.github.lehjr.modularpowerarmor.client.gui.tinker.module;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableMuseArrow;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableTile;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class RecipeGridWidget {
    Point2D ul;
    protected DrawableRect border;
    RecipeList recipeList;
    Colour borderColour;
    Colour backgroundColourEnabled;
    Colour backgroundColourDisabled;
    Colour gridColour;
    int currentIndex;
    ClickableMuseArrow forwardArrow;
    ClickableMuseArrow backArrow;
    ResourceLocation moduleName;
    List<DrawableTile> tiles;


    public RecipeGridWidget(
            RecipeList recipeList,
            Point2D ul,
            ResourceLocation moduleName,
            Colour borderColour,
            Colour backgroundColourEnabled,
            Colour backgroundColourDisabled,
            Colour gridColour) {
        this.recipeList = recipeList;
        this.ul = ul;
        this.moduleName = moduleName;
        border = new DrawableRect(ul, ul.plus(new Point2D(54, 54)), backgroundColourEnabled, borderColour);
        this.currentIndex = 0;

        int i = 0;
        for (int row =0; row < 4; row ++) {
            for (int col = 0; col < 4; col++) {
                tiles.add(new DrawableTile(ul, ul.plus(18, 18), backgroundColourEnabled, gridColour));
                if (i > 0) {
                    if (col > 0) {
                        this.tiles.get(i).setMeRightOf(this.tiles.get(i - 1));
                    }

                    if (row > 0) {
                        this.tiles.get(i).setMeBelow(this.tiles.get(i - 3));
                    }
                }
                i++;
            }
        }



        //TODO: get recipes for selected module

    }




    // from the recipebook gui
    private void updateCollections(boolean p_193003_1_) {
        ClientRecipeBook recipeBook = Minecraft.getInstance().player.getRecipeBook();
        final RecipeItemHelper stackedContents = new RecipeItemHelper(); // <- this thing is gonna want a container at some point
        List<RecipeList> list = recipeBook.getRecipes();
        stackedContents.clear();
        Minecraft.getInstance().player.inventory.accountStacks(stackedContents);

        list.forEach((recipeList) -> recipeList.canCraft(stackedContents, 3, 3, recipeBook));

        List<RecipeList> list1 = Lists.newArrayList(list);
        list1.removeIf((recipeList) -> !recipeList.isNotEmpty());
        list1.removeIf((recipeList) -> !recipeList.containsValidRecipes());

        list1.removeIf((recipeList) -> !recipeList.containsValidRecipes());

        String s = moduleName.toString();
        if (!s.isEmpty()) {
            ObjectSet<RecipeList> objectset = new ObjectLinkedOpenHashSet<>(Minecraft.getInstance().getSearchTree(SearchTreeManager.RECIPES).search(s));
            list1.removeIf((p_193947_1_) -> {
                return !objectset.contains(p_193947_1_);
            });
        }



        System.out.println("recipe list size: ");

//
//        if (this.recipeBook.isFilteringCraftable(this.container)) {
//            list1.removeIf((p_193958_0_) -> {
//                return !p_193958_0_.containsCraftableRecipes();
//            });
//        }
//
//        this.recipeBookPage.updateLists(list1, p_193003_1_);
    }




}
