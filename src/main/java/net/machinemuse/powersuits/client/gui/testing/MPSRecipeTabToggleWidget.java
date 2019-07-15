package net.machinemuse.powersuits.client.gui.testing;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.recipebook.RecipeTabToggleWidget;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.util.Iterator;
import java.util.List;

public class MPSRecipeTabToggleWidget extends RecipeTabToggleWidget {
    private final RecipeBookCategories category;
    private float animationTime;

    public MPSRecipeTabToggleWidget(RecipeBookCategories categories) {
        super(categories);
        this.initTextureValues(153, 2, 35, 0, MPSRecipeBookGui.RECIPE_BOOK);
        this.category = categories;
    }

    @Override
    public void startAnimation(Minecraft minecraft) {
        ClientRecipeBook recipeBook = minecraft.player.getRecipeBook();
        List<RecipeList> recipeLists = recipeBook.getRecipes(this.category);
        if (minecraft.player.openContainer instanceof RecipeBookContainer) {
            Iterator iter = recipeLists.iterator();

            while(iter.hasNext()) {
                RecipeList recipes = (RecipeList)iter.next();
                Iterator recipeIter = recipes.getRecipes(recipeBook.isFilteringCraftable((RecipeBookContainer)minecraft.player.openContainer)).iterator();

                while(recipeIter.hasNext()) {
                    IRecipe<?> recipe_ = (IRecipe)recipeIter.next();
                    if (recipeBook.isNew(recipe_)) {
                        this.animationTime = 15.0F;
                        return;
                    }
                }
            }

        }
    }


    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        if (this.animationTime > 0.0F) {
            float lvt_4_1_ = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)(this.x + 8), (float)(this.y + 12), 0.0F);
            GlStateManager.scalef(1.0F, lvt_4_1_, 1.0F);
            GlStateManager.translatef((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
        }

        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(this.resourceLocation);
        GlStateManager.disableDepthTest();
        int lvt_5_1_ = this.xTexStart;
        int lvt_6_1_ = this.yTexStart;
        if (this.stateTriggered) {
            lvt_5_1_ += this.xDiffTex;
        }

        if (this.isHovered()) {
            lvt_6_1_ += this.yDiffTex;
        }

        int lvt_7_1_ = this.x;
        if (this.stateTriggered) {
            lvt_7_1_ -= 2;
        }

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(lvt_7_1_, this.y, lvt_5_1_, lvt_6_1_, this.width, this.height);
        GlStateManager.enableDepthTest();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        this.renderIcon(minecraft.getItemRenderer());
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
        if (this.animationTime > 0.0F) {
            GlStateManager.popMatrix();
            this.animationTime -= partialTicks;
        }
    }

    /**
     * Renders the item icons for the tabs. Some tabs have 2 icons, some just one.
     *
     * @param renderer
     */
    private void renderIcon(ItemRenderer renderer) {
        List<ItemStack> icons = this.category.getIcons();
        int offset = this.stateTriggered ? -2 : 0;
        if (icons.size() == 1) {
            renderer.renderItemAndEffectIntoGUI(icons.get(0), this.x + 9 + offset, this.y + 5);
        } else if (icons.size() == 2) {
            renderer.renderItemAndEffectIntoGUI(icons.get(0), this.x + 3 + offset, this.y + 5);
            renderer.renderItemAndEffectIntoGUI(icons.get(1), this.x + 14 + offset, this.y + 5);
        }
    }

    @Override
    public RecipeBookCategories func_201503_d() {
        return this.category;
    }

    @Override
    public boolean func_199500_a(ClientRecipeBook recipeBook) {
        List<RecipeList> recipeLists = recipeBook.getRecipes(this.category);
        this.visible = false;
        if (recipeLists != null) {
            Iterator iter = recipeLists.iterator();

            while(iter.hasNext()) {
                RecipeList recipes = (RecipeList)iter.next();
                if (recipes.isNotEmpty() && recipes.containsValidRecipes()) {
                    this.visible = true;
                    break;
                }
            }
        }

        return this.visible;
    }
}