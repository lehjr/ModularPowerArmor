package net.machinemuse.powersuits.client.gui.testing;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.math.geometry.DrawableMuseRelativeRect;
import net.machinemuse.numina.math.geometry.MusePoint2D;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.play.client.CRecipeInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class MPSRecipeBookGui extends RecipeBookGui {
    /** The outer green rectangle */
    protected DrawableMuseRelativeRect outerFrame = new DrawableMuseRelativeRect(0, 0, 0, 0,
            true,
            new Colour(0.0F, 0.2F, 0.0F, 0.8F),
            new Colour(0.1F, 0.9F, 0.1F, 0.8F));


    /** The inner blue rectangle */
    protected DrawableMuseRelativeRect innerFrame = new DrawableMuseRelativeRect(0, 0, 0, 0,
            true,
            Colour.DARKBLUE.withAlpha(0.8),
            Colour.LIGHTBLUE.withAlpha(0.8));


    // FIXME!!! sync issue when using recipe book and clicking on recpies when player has items to craft them
        // seems to be related to putting items back in player inventory MPSCraftingContainer:func_201771_a:100




    protected static final ResourceLocation RECIPE_BOOK = new ResourceLocation(MPSConstants.MODID,"textures/gui/recipe_book.png");
    private int xOffset;
    private int width;
    private int height;
    protected final GhostRecipe ghostRecipe = new GhostRecipe();

    // TODO: replace these with rectangle buttons
    private final List<MPSRecipeTabToggleWidget> recipeTabs = Lists.newArrayList();
    private MPSRecipeTabToggleWidget currentTab;

    // todo: replace this with a textured button??

    protected ToggleWidget toggleRecipesBtn;


    protected RecipeBookContainer<?> container;


    protected Minecraft mc;


    private TextFieldWidget searchBar;


    private String lastSearch = "";


    protected ClientRecipeBook recipeBook;


    protected final RecipeBookPage recipeBookPage = new RecipeBookPage();


    protected final RecipeItemHelper stackedContents = new RecipeItemHelper();


    private int timesInventoryChanged;
    private boolean field_199738_u;

    /**
        GUI window:
                146 wide x 166 tall
        Tabs:
                25 wide x 25 tall
                    change background colour when selected
                    add some width and draw right edge behind the book gui

        Arrows:
                11 wide x 16 tall

        item boxes:
                23 square


        need a rectangle with square corners



     */




    /**
     *  Looks like the first stage of an init()
     *      so we can probably treat it like the constructor
     *
     */
    @Override
    public void func_201520_a(int width, int height, Minecraft minecraft, boolean widthTooNarrow, RecipeBookContainer<?> container) {



        System.out.println("doing something here");



        this.mc = minecraft;
        this.width = width;
        this.height = height;
        this.container = container;
        minecraft.player.openContainer = container;

        this.recipeBook = minecraft.player.getRecipeBook();

        this.timesInventoryChanged = minecraft.player.inventory.getTimesChanged();
        if (this.isVisible()) {
            this.func_201518_a(widthTooNarrow);
        }
        minecraft.keyboardListener.enableRepeatEvents(true);
    }

    /** Looks like a second stage init() */
    @Override
    public void func_201518_a(boolean widthTooNarrow) {
        System.out.println("doing something here");





        this.xOffset = widthTooNarrow ? 0 : 86;
        int guiLeft = (this.width - 147) / 2 - this.xOffset;
        int guiTop = (this.height - 166) / 2;





        outerFrame.setTargetDimensions(new MusePoint2D(guiLeft, guiTop), new MusePoint2D(146, 166));
        innerFrame.setTargetDimensions(new MusePoint2D(guiLeft + 7, guiTop + 7), new MusePoint2D( 146 - 14, 166 -14));


        this.stackedContents.clear();
        this.mc.player.inventory.func_201571_a(this.stackedContents);
        this.container.func_201771_a(this.stackedContents);


        String s = this.searchBar != null ? this.searchBar.getText() : "";
        this.searchBar = new TextFieldWidget(this.mc.fontRenderer, guiLeft + 25, guiTop + 14, 80, 9 + 5, I18n.format("itemGroup.search"));
        this.searchBar.setMaxStringLength(50);
        this.searchBar.setEnableBackgroundDrawing(false);
        this.searchBar.setVisible(true);
        this.searchBar.setTextColor(16777215);
        this.searchBar.setText(s);


        this.recipeBookPage.init(this.mc, guiLeft, guiTop);
        this.recipeBookPage.addListener(this);


        this.toggleRecipesBtn = new ToggleWidget(guiLeft + 110, guiTop + 12, 26, 16, this.recipeBook.isFilteringCraftable(this.container));


        this.func_205702_a();
        this.recipeTabs.clear();

        for(RecipeBookCategories recipebookcategories : this.container.getRecipeBookCategories()) {
            this.recipeTabs.add(new MPSRecipeTabToggleWidget(recipebookcategories));
        }

        if (this.currentTab != null) {
            this.currentTab = this.recipeTabs.stream().filter((p_209505_1_) -> {
                return p_209505_1_.func_201503_d().equals(this.currentTab.func_201503_d());
            }).findFirst().orElse((MPSRecipeTabToggleWidget)null);
        }

        if (this.currentTab == null) {
            this.currentTab = this.recipeTabs.get(0);
        }

        this.currentTab.setStateTriggered(true);
        this.updateCollections(false);
        this.updateTabs();
    }






    @Override
    public boolean changeFocus(boolean p_changeFocus_1_) {
        return false;
    }

    @Override
    protected void func_205702_a() {
        this.toggleRecipesBtn.initTextureValues(152, 41, 28, 18, RECIPE_BOOK);
    }

    @Override
    public void removed() {
        this.searchBar = null;
        this.currentTab = null;
        this.mc.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public int updateScreenPosition(boolean widthTooNarrow, int width, int xSize) {
        int i;
        if (this.isVisible() && !widthTooNarrow) {
            i = 177 + (width - xSize - 200) / 2;
        } else {
            i = (width - xSize) / 2;
        }

        return i;
    }

    @Override
    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }

    @Override
    public boolean isVisible() {
        return this.recipeBook.isGuiOpen();
    }

    @Override
    protected void setVisible(boolean visible) {
        this.recipeBook.setGuiOpen(visible);
        if (!visible) {
            this.recipeBookPage.setInvisible();
        }

        this.sendUpdateSettings();
    }

    @Override
    public void slotClicked(@Nullable Slot slotIn) {
        if (slotIn != null && slotIn.slotNumber < this.container.getSize()) {
            this.ghostRecipe.clear();
            if (this.isVisible()) {
                this.updateStackedContents();
            }
        }

    }

    private void updateCollections(boolean p_193003_1_) {
        List<RecipeList> list = this.recipeBook.getRecipes(this.currentTab.func_201503_d());
        list.forEach((p_193944_1_) -> {
            p_193944_1_.canCraft(this.stackedContents, this.container.getWidth(), this.container.getHeight(), this.recipeBook);
        });
        List<RecipeList> list1 = Lists.newArrayList(list);
        list1.removeIf((p_193952_0_) -> {
            return !p_193952_0_.isNotEmpty();
        });
        list1.removeIf((p_193953_0_) -> {
            return !p_193953_0_.containsValidRecipes();
        });
        String s = this.searchBar.getText();
        if (!s.isEmpty()) {
            ObjectSet<RecipeList> objectset = new ObjectLinkedOpenHashSet<>(this.mc.func_213253_a(SearchTreeManager.RECIPES).search(s.toLowerCase(Locale.ROOT)));
            list1.removeIf((p_193947_1_) -> {
                return !objectset.contains(p_193947_1_);
            });
        }

        if (this.recipeBook.isFilteringCraftable(this.container)) {
            list1.removeIf((p_193958_0_) -> {
                return !p_193958_0_.containsCraftableRecipes();
            });
        }

        this.recipeBookPage.updateLists(list1, p_193003_1_);
    }

    private void updateTabs() {
        int i = (this.width - 147) / 2 - this.xOffset - 30;
        int j = (this.height - 166) / 2 + 3;
        int k = 27;
        int l = 0;

        for(MPSRecipeTabToggleWidget MPSRecipeTabToggleWidget : this.recipeTabs) {
            RecipeBookCategories recipebookcategories = MPSRecipeTabToggleWidget.func_201503_d();
            if (recipebookcategories != RecipeBookCategories.SEARCH && recipebookcategories != RecipeBookCategories.FURNACE_SEARCH) {
                if (MPSRecipeTabToggleWidget.func_199500_a(this.recipeBook)) {
                    MPSRecipeTabToggleWidget.setPosition(i, j + 27 * l++);
                    MPSRecipeTabToggleWidget.startAnimation(this.mc);
                }
            } else {
                MPSRecipeTabToggleWidget.visible = true;
                MPSRecipeTabToggleWidget.setPosition(i, j + 27 * l++);
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
        this.mc.player.inventory.func_201571_a(this.stackedContents);
        this.container.func_201771_a(this.stackedContents);
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
            GlStateManager.translatef(0.0F, 0.0F, 100.0F);


            this.mc.getTextureManager().bindTexture(RECIPE_BOOK);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = (this.width - 147) / 2 - this.xOffset;
            int j = (this.height - 166) / 2;
            this.blit(i, j, 1, 1, 147, 166);



            this.searchBar.render(mouseX, mouseY, partialTicks);
            RenderHelper.disableStandardItemLighting();


            // move this up to before the outer frame once the texture is no longer needed
            for(MPSRecipeTabToggleWidget MPSRecipeTabToggleWidget : this.recipeTabs) {
                MPSRecipeTabToggleWidget.render(mouseX, mouseY, partialTicks);
            }

            this.toggleRecipesBtn.render(mouseX, mouseY, partialTicks);
            this.recipeBookPage.render(i, j, mouseX, mouseY, partialTicks);
            GlStateManager.popMatrix();




        }
    }

    @Override
    public void renderTooltip(int p_191876_1_, int p_191876_2_, int p_191876_3_, int p_191876_4_) {
        if (this.isVisible()) {
            this.recipeBookPage.renderTooltip(p_191876_3_, p_191876_4_);
            if (this.toggleRecipesBtn.isHovered()) {
                String s = this.func_205703_f();
                if (this.mc.currentScreen != null) {
                    this.mc.currentScreen.renderTooltip(s, p_191876_3_, p_191876_4_);
                }
            }

            this.renderGhostRecipeTooltip(p_191876_1_, p_191876_2_, p_191876_3_, p_191876_4_);
        }
    }

    @Override
    protected String func_205703_f() {
        return I18n.format(this.toggleRecipesBtn.isStateTriggered() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");
    }

    private void renderGhostRecipeTooltip(int p_193015_1_, int p_193015_2_, int p_193015_3_, int p_193015_4_) {
        ItemStack itemstack = null;

        for(int i = 0; i < this.ghostRecipe.size(); ++i) {
            GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ghostRecipe.get(i);
            int j = ghostrecipe$ghostingredient.getX() + p_193015_1_;
            int k = ghostrecipe$ghostingredient.getY() + p_193015_2_;
            if (p_193015_3_ >= j && p_193015_4_ >= k && p_193015_3_ < j + 16 && p_193015_4_ < k + 16) {
                itemstack = ghostrecipe$ghostingredient.getItem();
            }
        }

        if (itemstack != null && this.mc.currentScreen != null) {
            this.mc.currentScreen.renderTooltip(this.mc.currentScreen.getTooltipFromItem(itemstack), p_193015_3_, p_193015_4_);
        }

    }

    @Override
    public void renderGhostRecipe(int p_191864_1_, int p_191864_2_, boolean p_191864_3_, float p_191864_4_) {
        this.ghostRecipe.render(this.mc, p_191864_1_, p_191864_2_, p_191864_3_, p_191864_4_);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isVisible() && !this.mc.player.isSpectator()) {
            if (this.recipeBookPage.func_198955_a(mouseX, mouseY, button, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
                IRecipe<?> irecipe = this.recipeBookPage.getLastClickedRecipe();
                RecipeList recipelist = this.recipeBookPage.getLastClickedRecipeList();
                if (irecipe != null && recipelist != null) {
                    if (!recipelist.isCraftable(irecipe) && this.ghostRecipe.getRecipe() == irecipe) {
                        return false;
                    }

                    this.ghostRecipe.clear();
                    this.mc.playerController.func_203413_a(this.mc.player.openContainer.windowId, irecipe, Screen.hasShiftDown());
                    if (!this.isOffsetNextToMainGUI()) {
                        this.setVisible(false);
                    }
                }

                return true;
            } else if (this.searchBar.mouseClicked(mouseX, mouseY, button)) {
                return true;
            } else if (this.toggleRecipesBtn.mouseClicked(mouseX, mouseY, button)) {
                boolean flag = this.toggleCraftableFilter();
                this.toggleRecipesBtn.setStateTriggered(flag);
                this.sendUpdateSettings();
                this.updateCollections(false);
                return true;
            } else {
                for(MPSRecipeTabToggleWidget MPSRecipeTabToggleWidget : this.recipeTabs) {
                    if (MPSRecipeTabToggleWidget.mouseClicked(mouseX, mouseY, button)) {
                        if (this.currentTab != MPSRecipeTabToggleWidget) {
                            this.currentTab.setStateTriggered(false);
                            this.currentTab = MPSRecipeTabToggleWidget;
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
    protected boolean toggleCraftableFilter() {
        boolean flag = !this.recipeBook.isFilteringCraftable();
        this.recipeBook.setFilteringCraftable(flag);
        return flag;
    }

    @Override
    public boolean func_195604_a(double mouseX, double mouseY, int parentGuiLeft, int parentGuiTop, int parentXSize, int parentYSize, int button) {
        if (!this.isVisible()) {
            return true;
        } else {
            boolean flag = mouseX < (double)parentGuiLeft || mouseY < (double)parentGuiTop || mouseX >= (double)(parentGuiLeft + parentXSize) || mouseY >= (double)(parentGuiTop + parentYSize);
            boolean flag1 = (double)(parentGuiLeft - 147) < mouseX && mouseX < (double)parentGuiLeft && (double)parentGuiTop < mouseY && mouseY < (double)(parentGuiTop + parentYSize);
            return flag && !flag1 && !this.currentTab.isHovered();
        }
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        this.field_199738_u = false;
        if (this.isVisible() && !this.mc.player.isSpectator()) {
            if (p_keyPressed_1_ == 256 && !this.isOffsetNextToMainGUI()) {
                this.setVisible(false);
                return true;
            } else if (this.searchBar.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
                this.updateSearch();
                return true;
            } else if (this.searchBar.isFocused() && this.searchBar.getVisible() && p_keyPressed_1_ != 256) {
                return true;
            } else if (this.mc.gameSettings.keyBindChat.matchesKey(p_keyPressed_1_, p_keyPressed_2_) && !this.searchBar.isFocused()) {
                this.field_199738_u = true;
                this.searchBar.setFocused2(true);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean keyReleased(int p_223281_1_, int p_223281_2_, int p_223281_3_) {
        this.field_199738_u = false;
        return super.keyReleased(p_223281_1_, p_223281_2_, p_223281_3_);
    }

    @Override
    public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
        if (this.field_199738_u) {
            return false;
        } else if (this.isVisible() && !this.mc.player.isSpectator()) {
            if (this.searchBar.charTyped(p_charTyped_1_, p_charTyped_2_)) {
                this.updateSearch();
                return true;
            } else {
                return super.charTyped(p_charTyped_1_, p_charTyped_2_);
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    private void updateSearch() {
        String s = this.searchBar.getText().toLowerCase(Locale.ROOT);
        this.pirateRecipe(s);
        if (!s.equals(this.lastSearch)) {
            this.updateCollections(false);
            this.lastSearch = s;
        }

    }

    /**
     * "Check if we should activate the pirate speak easter egg"
     */
    private void pirateRecipe(String text) {
        if ("excitedze".equals(text)) {
            LanguageManager languagemanager = this.mc.getLanguageManager();
            Language language = languagemanager.getLanguage("en_pt");
            if (languagemanager.getCurrentLanguage().compareTo(language) == 0) {
                return;
            }

            languagemanager.setCurrentLanguage(language);
            this.mc.gameSettings.language = language.getCode();
            net.minecraftforge.client.ForgeHooksClient.refreshResources(this.mc, net.minecraftforge.resource.VanillaResourceType.LANGUAGES);
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
    public void recipesShown(List<IRecipe<?>> recipes) {
        for(IRecipe<?> irecipe : recipes) {
            this.mc.player.removeRecipeHighlight(irecipe);
        }
    }

    @Override
    public void setupGhostRecipe(IRecipe<?> recipe, List<Slot> slots) {
        ItemStack itemstack = recipe.getRecipeOutput();
        this.ghostRecipe.setRecipe(recipe);
        this.ghostRecipe.addIngredient(Ingredient.fromStacks(itemstack), (slots.get(0)).xPos, (slots.get(0)).yPos);
        this.placeRecipe(this.container.getWidth(), this.container.getHeight(), this.container.getOutputSlot(), recipe, recipe.getIngredients().iterator(), 0);
    }

    @Override
    public void setSlotContents(Iterator<Ingredient> ingredients, int slotIn, int maxAmount, int y, int x) {
        Ingredient ingredient = ingredients.next();
        if (!ingredient.hasNoMatchingItems()) {
            Slot slot = this.container.inventorySlots.get(slotIn);
            this.ghostRecipe.addIngredient(ingredient, slot.xPos, slot.yPos);
        }
    }

    @Override
    protected void sendUpdateSettings() {
        if (this.mc.getConnection() != null) {
            System.out.println("sending packet");


            this.mc.getConnection().sendPacket(new CRecipeInfoPacket(this.recipeBook.isGuiOpen(), this.recipeBook.isFilteringCraftable(), this.recipeBook.isFurnaceGuiOpen(), this.recipeBook.isFurnaceFilteringCraftable(), this.recipeBook.func_216758_e(), this.recipeBook.func_216761_f()));
        }
    }
}