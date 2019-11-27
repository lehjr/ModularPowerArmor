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

package com.github.machinemuse.powersuits.client.gui.crafting;

import com.github.lehjr.mpalib.client.gui.geometry.DrawableRelativeRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.api.constants.MPSModConstants;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonToggle;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.recipebook.GhostRecipe;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author lehjr
 */
public class MPSRecipeBookGui extends GuiRecipeBook {
    /** The outer green rectangle */
    protected DrawableRelativeRect outerFrame = new DrawableRelativeRect(0, 0, 0, 0,
            true,
            new Colour(0.0F, 0.2F, 0.0F, 0.8F),
            new Colour(0.1F, 0.9F, 0.1F, 0.8F));

    /** The inner blue rectangle */
    protected DrawableRelativeRect innerFrame = new DrawableRelativeRect(0, 0, 0, 0,
            true,
            Colour.DARKBLUE.withAlpha(0.8),
            Colour.LIGHTBLUE.withAlpha(0.8));

    protected static final ResourceLocation RECIPE_BOOK = new ResourceLocation(MPSModConstants.MODID,"textures/gui/recipe_book.png");
    protected static final ResourceLocation SEARCH_ICON = new ResourceLocation(MPSModConstants.MODID,"textures/gui/search.png");

    public int xOffset;
    public int width;
    public int height;
    private final GhostRecipe ghostRecipe = new GhostRecipe();
    private final List<MPSRecipeTabToggleWidget> recipeTabs = new ArrayList<MPSRecipeTabToggleWidget>() {{
        add(new MPSRecipeTabToggleWidget(0, CreativeTabs.SEARCH));
        add(new MPSRecipeTabToggleWidget(0, CreativeTabs.TOOLS));
        add(new MPSRecipeTabToggleWidget(0, CreativeTabs.BUILDING_BLOCKS));
        add(new MPSRecipeTabToggleWidget(0, CreativeTabs.MISC));
        add(new MPSRecipeTabToggleWidget(0, CreativeTabs.REDSTONE));
    }};

    private MPSRecipeTabToggleWidget currentTab;

    /**
     * This button toggles between showing all recipes and showing only craftable recipes
     */
    private GuiButtonToggle toggleRecipesBtn;
    private InventoryCrafting craftingSlots;
    private Minecraft mc;
    private GuiTextField searchBar;
    private String lastSearch = "";
    private RecipeBook recipeBook;
    private final MPSRecipeBookPage recipeBookPage = new MPSRecipeBookPage();
    private RecipeItemHelper stackedContents = new RecipeItemHelper();
    private int timesInventoryChanged;

    @Override
    public void /*init*/func_194303_a(int width, int height, Minecraft minecraft, boolean widthTooNarrow, InventoryCrafting craftingInventory) {
        this.mc = minecraft;
        this.width = width;
        this.height = height;
        this.craftingSlots = craftingInventory;
        this.recipeBook = minecraft.player.getRecipeBook();
        this.timesInventoryChanged = minecraft.player.inventory.getTimesChanged();
        this.currentTab = this.recipeTabs.get(0);
        this.currentTab.setStateTriggered(true);

        if (this.isVisible()) {
            this.initVisuals(widthTooNarrow, craftingInventory);
        }

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void initVisuals(boolean widthTooNarrow, InventoryCrafting craftingInventory) {
        this.xOffset = widthTooNarrow ? 0 : 86;
        int guiLeft = (this.width - 147) / 2 - this.xOffset;
        int guiTop = (this.height - 166) / 2;
        outerFrame.setTargetDimensions(new Point2D(guiLeft, guiTop), new Point2D(146, 166));
        innerFrame.setTargetDimensions(new Point2D(guiLeft + 7, guiTop + 7), new Point2D( 146 - 14, 166 -14));
        this.stackedContents.clear();
        this.mc.player.inventory.fillStackedContents(this.stackedContents, false);
        craftingInventory.fillStackedContents(this.stackedContents);
        this.searchBar = new GuiTextField(0, this.mc.fontRenderer, guiLeft + 25, guiTop + 14, 80, this.mc.fontRenderer.FONT_HEIGHT + 5);
        this.searchBar.setMaxStringLength(50);
        this.searchBar.setEnableBackgroundDrawing(false);
        this.searchBar.setVisible(true);
        this.searchBar.setTextColor(16777215);
        this.recipeBookPage.init(this.mc, guiLeft, guiTop);
        this.recipeBookPage.addListener(this);
        this.toggleRecipesBtn = new GuiButtonToggle(0, guiLeft + 110, guiTop + 12, 26, 16, this.recipeBook.isFilteringCraftable());
        this.toggleRecipesBtn.initTextureValues(152, 41, 28, 18, RECIPE_BOOK);
        this.updateCollections(false);
        this.updateTabs();
    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public int updateScreenPosition(boolean widthTooNarrow, int width, int xSize) {
        int offset;

        if (this.isVisible() && !widthTooNarrow) {
            offset = 177 + (width - xSize - 200) / 2;
        } else {
            offset = (width - xSize) / 2;
        }

        return offset;
    }

    @Override
    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }

    @Override
    public boolean isVisible() {
        return this.recipeBook.isGuiOpen();
    }

    private void setVisible(boolean visible) {
        this.recipeBook.setGuiOpen(visible);

        if (!visible) {
            this.recipeBookPage.setInvisible();
        }

        this.sendUpdateSettings();
    }

    @Override
    public void slotClicked(@Nullable Slot slotIn) {
        if (slotIn != null && slotIn.slotNumber <= 9) {
            this.ghostRecipe.clear();

            if (this.isVisible()) {
                this.updateStackedContents();
            }
        }
    }

    private void updateCollections(boolean p_193003_1_) {
        List<RecipeList> list = (List) RecipeBookClient.RECIPES_BY_TAB.get(this.currentTab.getCategory());
        list.forEach((recipeList) ->
                recipeList.canCraft(this.stackedContents, this.craftingSlots.getWidth(), this.craftingSlots.getHeight(), this.recipeBook));

        List<RecipeList> list1 = new ArrayList<>(list);
        list1.removeIf((recipeList) -> !recipeList.isNotEmpty());
        list1.removeIf((recipeList) -> !recipeList.containsValidRecipes());
        String s = this.searchBar.getText();

        if (!s.isEmpty()) {
            ObjectSet<RecipeList> objectset = new ObjectLinkedOpenHashSet<RecipeList>(this.mc.getSearchTree(SearchTreeManager.RECIPES).search(s.toLowerCase(Locale.ROOT)));
            list1.removeIf((recipeList) -> !objectset.contains(recipeList));
        }

        if (this.recipeBook.isFilteringCraftable()) {
            list1.removeIf((recipeList) -> !recipeList.containsCraftableRecipes());
        }

        this.recipeBookPage.updateLists(list1, p_193003_1_);
    }

    private void updateTabs() {
        int i = (this.width - 147) / 2 - this.xOffset - 30;
        int j = (this.height - 166) / 2 + 3;
        int k = 27;
        int l = 0;

        for (MPSRecipeTabToggleWidget guibuttonrecipetab : this.recipeTabs) {
            CreativeTabs creativetabs = guibuttonrecipetab.getCategory();
            if (creativetabs == CreativeTabs.SEARCH) {
                guibuttonrecipetab.visible = true;
                guibuttonrecipetab.setPosition(i, j + k * l++);
            } else if (guibuttonrecipetab.updateVisibility()){
                guibuttonrecipetab.setPosition(i, j + k * l++);
                guibuttonrecipetab.startAnimation(this.mc);
            }
        }
    }

    @Override
    public void tick() {
        if (this.isVisible()) {
            if (this.timesInventoryChanged != this.mc.player.inventory.getTimesChanged()) {
                this.updateStackedContents();
                this.timesInventoryChanged = this.mc.player.inventory.getTimesChanged();
            }
        }
    }

    private void updateStackedContents() {
        this.stackedContents.clear();
        this.mc.player.inventory.fillStackedContents(this.stackedContents, false);
        this.craftingSlots.fillStackedContents(this.stackedContents);
        this.updateCollections(false);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.isVisible()) {
            outerFrame.draw();
            innerFrame.draw();

            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.pushMatrix();

            this.mc.getTextureManager().bindTexture(SEARCH_ICON);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int x = (this.width - 147) / 2 - this.xOffset;
            int y = (this.height - 166) / 2;

            this.drawTexturedModalRect(x + 9, y + 11, 0, 0, 16, 16);
            this.searchBar.drawTextBox();
            RenderHelper.disableStandardItemLighting();

            // move this up to before the outer frame once the texture is no longer needed
            for(MPSRecipeTabToggleWidget MPSRecipeTabToggleWidget : this.recipeTabs) {
                MPSRecipeTabToggleWidget.drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            this.toggleRecipesBtn.drawButton(this.mc, mouseX, mouseY, partialTicks);
            this.recipeBookPage.render(x, y, mouseX, mouseY, partialTicks);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void renderTooltip(int guiLeft, int guiTop, int mouseX, int mouseY) {
        if (this.isVisible()) {
            this.recipeBookPage.renderTooltip(mouseX, mouseY);

            if (this.toggleRecipesBtn.isMouseOver()) {
                String s1 = I18n.format(this.toggleRecipesBtn.isStateTriggered() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");

                if (this.mc.currentScreen != null) {
                    this.mc.currentScreen.drawHoveringText(s1, mouseX, mouseY);
                }
            }

            this.renderGhostRecipeTooltip(guiLeft, guiTop, mouseX, mouseY);
        }
    }

    private void renderGhostRecipeTooltip(int guiLeft, int guiTop, int mouseX, int mouseY) {
        ItemStack itemstack = null;

        for (int i = 0; i < this.ghostRecipe.size(); ++i) {
            GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ghostRecipe.get(i);
            int j = ghostrecipe$ghostingredient.getX() + guiLeft;
            int k = ghostrecipe$ghostingredient.getY() + guiTop;

            if (mouseX >= j && mouseY >= k && mouseX < j + 16 && mouseY < k + 16) {
                itemstack = ghostrecipe$ghostingredient.getItem();
            }
        }

        if (itemstack != null && this.mc.currentScreen != null) {
            this.mc.currentScreen.drawHoveringText(this.mc.currentScreen.getItemToolTip(itemstack), mouseX, mouseY);
        }
    }

    @Override
    public void renderGhostRecipe(int guiLeft, int guiTop, boolean isFullSizeGrid, float partialTicks) {
        this.ghostRecipe.render(this.mc, guiLeft, guiTop, isFullSizeGrid, partialTicks);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isVisible() && !this.mc.player.isSpectator()) {
            if (this.recipeBookPage.mouseClicked(mouseX, mouseY, mouseButton, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
                IRecipe irecipe = this.recipeBookPage.getLastClickedRecipe();
                RecipeList recipelist = this.recipeBookPage.getLastClickedRecipeList();

                if (irecipe != null && recipelist != null) {
                    if (!recipelist.isCraftable(irecipe) && this.ghostRecipe.getRecipe() == irecipe) {
                        return false;
                    }

                    this.ghostRecipe.clear();
                    this.mc.playerController.func_194338_a(this.mc.player.openContainer.windowId, irecipe, GuiScreen.isShiftKeyDown(), this.mc.player);

                    if (!this.isOffsetNextToMainGUI() && mouseButton == 0) {
                        this.setVisible(false);
                    }
                }

                return true;
            } else if (mouseButton != 0) {
                return false;
            } else if (this.searchBar.mouseClicked(mouseX, mouseY, mouseButton)) {
                return true;
            } else if (this.toggleRecipesBtn.mousePressed(this.mc, mouseX, mouseY)) {
                boolean flag = !this.recipeBook.isFilteringCraftable();
                this.recipeBook.setFilteringCraftable(flag);
                this.toggleRecipesBtn.setStateTriggered(flag);
                this.toggleRecipesBtn.playPressSound(this.mc.getSoundHandler());
                this.sendUpdateSettings();
                this.updateCollections(false);
                return true;
            } else {
                for (MPSRecipeTabToggleWidget guibuttonrecipetab : this.recipeTabs) {
                    if (guibuttonrecipetab.mousePressed(this.mc, mouseX, mouseY)) {
                        if (this.currentTab != guibuttonrecipetab) {
                            guibuttonrecipetab.playPressSound(this.mc.getSoundHandler());
                            this.currentTab.setStateTriggered(false);
                            this.currentTab = guibuttonrecipetab;
                            this.currentTab.setStateTriggered(true);
                            this.updateCollections(true);
                        }

                        return true;
                    }
                }
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean hasClickedOutside(int mouseX, int mouseY, int guiLeft, int guiTop, int xSize, int ySize) {
        if (!this.isVisible()) {
            return true;
        } else {
            boolean flag = mouseX < guiLeft || mouseY < guiTop || mouseX >= guiLeft + xSize || mouseY >= guiTop + ySize;
            boolean flag1 = guiLeft - 147 < mouseX && mouseX < guiLeft && guiTop < mouseY && mouseY < guiTop + ySize;
            return flag && !flag1 && !this.currentTab.mousePressed(this.mc, mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(char typedChar, int keycode) {
        if (this.isVisible() && !this.mc.player.isSpectator()) {
            if (keycode == 1 && !this.isOffsetNextToMainGUI()) {
                this.setVisible(false);
                return true;
            } else {
                if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat) && !this.searchBar.isFocused()) {
                    this.searchBar.setFocused(true);
                } else if (this.searchBar.textboxKeyTyped(typedChar, keycode)) {
                    String s1 = this.searchBar.getText().toLowerCase(Locale.ROOT);
                    this.pirateRecipe(s1);

                    if (!s1.equals(this.lastSearch)) {
                        this.updateCollections(false);
                        this.lastSearch = s1;
                    }
                    return true;
                }
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * "Check if we should activate the pirate speak easter egg"
     *
     * @param text 'if equal to "excitedze", activate the easter egg'
     */
    private void pirateRecipe(String text) {
        if ("excitedze".equals(text)) {
            LanguageManager languagemanager = this.mc.getLanguageManager();
            Language language = languagemanager.getLanguage("en_pt");

            if (languagemanager.getCurrentLanguage().compareTo(language) == 0) {
                return;
            }

            languagemanager.setCurrentLanguage(language);
            this.mc.gameSettings.language = language.getLanguageCode();
            net.minecraftforge.fml.client.FMLClientHandler.instance().refreshResources(net.minecraftforge.client.resource.VanillaResourceType.LANGUAGES);
            this.mc.fontRenderer.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.mc.gameSettings.forceUnicodeFont);
            this.mc.fontRenderer.setBidiFlag(languagemanager.isCurrentLanguageBidirectional());
            this.mc.gameSettings.saveOptions();
        }
    }

    private boolean isOffsetNextToMainGUI() {
        return this.xOffset == 86;
    }

    @Override
    public void recipesUpdated() {
        this.updateTabs();

        if (this.isVisible()) {
            this.updateCollections(false);
        }
    }

    @Override
    public void recipesShown(List<IRecipe> recipes) {
        for (IRecipe irecipe : recipes) {
            this.mc.player.removeRecipeHighlight(irecipe);
        }
    }

    @Override
    public void setupGhostRecipe(IRecipe iRecipe, List<Slot> slots) {
        ItemStack itemstack = iRecipe.getRecipeOutput();
        this.ghostRecipe.setRecipe(iRecipe);
        this.ghostRecipe.addIngredient(Ingredient.fromStacks(itemstack), (slots.get(0)).xPos, (slots.get(0)).yPos);
        int gridWidth = this.craftingSlots.getWidth();
        int gridHeight = this.craftingSlots.getHeight();
        int recipeWidth = iRecipe instanceof net.minecraftforge.common.crafting.IShapedRecipe ? ((net.minecraftforge.common.crafting.IShapedRecipe) iRecipe).getRecipeWidth() : gridWidth;
        int slotIndex = 1; // starts at 1 because index 0 is output slot
        Iterator<Ingredient> iterator = iRecipe.getIngredients().iterator();

        for (int row = 0; row < gridHeight; ++row) {
            for (int col = 0; col < recipeWidth; ++col) {
                if (!iterator.hasNext()) {
                    return;
                }

                Ingredient ingredient = iterator.next();

                if (ingredient.getMatchingStacks().length > 0) {
                    Slot slot = slots.get(slotIndex);
                    this.ghostRecipe.addIngredient(ingredient, slot.xPos, slot.yPos);
                }

                ++slotIndex;
            }

            if (recipeWidth < gridWidth) {
                slotIndex += gridWidth - recipeWidth;
            }
        }
    }

    private void sendUpdateSettings() {
        if (this.mc.getConnection() != null) {
            this.mc.getConnection().sendPacket(new CPacketRecipeInfo(this.isVisible(), this.recipeBook.isFilteringCraftable()));
        }
    }
}
