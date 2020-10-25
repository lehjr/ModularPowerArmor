package com.github.lehjr.modularpowerarmor.client.gui.modding.module;

import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.modularpowerarmor.container.MPAWorkbenchContainer;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.network.packets.reworked_crafting_packets.CPlaceRecipePacket;
import com.github.lehjr.mpalib.client.sound.SoundDictionary;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableArrow;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableButton;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableItem;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.util.client.gui.frame.InventoryFrame;
import com.github.lehjr.mpalib.util.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.util.client.gui.geometry.DrawableArrow;
import com.github.lehjr.mpalib.util.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.util.client.gui.slot.IHideableSlot;
import com.github.lehjr.mpalib.util.math.Colour;
import com.github.lehjr.mpalib.util.string.StringUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.GhostRecipe;
import net.minecraft.client.gui.recipebook.IRecipeUpdateListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class InstallSalvageFrame extends ScrollableFrame implements IRecipeUpdateListener, IRecipePlacer<Ingredient> {
    MPAWorkbenchContainer container;
    protected ItemSelectionFrame targetItem;
    protected ModuleSelectionFrame targetModule;
    protected ClickableButton craftAndInstallButton;
    protected ClickableButton installButton;
    protected ClickableButton salvageButton;
    protected InventoryFrame craftingGrid;
    protected boolean craftingGridIsVisible = false;
    private ClickableArrow forwardArrow;
    private ClickableArrow backArrow;
    List<IRecipe> recipeList = new ArrayList<>();
    private int recipeIndex = -1;
    protected final GhostRecipe ghostRecipe = new GhostRecipe();
    private IRecipe<?> lastClickedRecipe;
    private final Colour arrowBorderColour = Colour.LIGHT_BLUE.withAlpha(0.8F);
    private final Colour arrowNormalBackGound = new Colour(0.1F, 0.3F, 0.4F, 0.7F);
    private final Colour arrowHighlightedBackground = Colour.WHITE;
    protected PlayerEntity player;
    final int craftingGridSize = 54; // height and width same
    Minecraft mc;
    ClientRecipeBook recipeBook;
    IRecipe recipe = null;
    int timesInventoryChanged = 0;
    protected final RecipeItemHelper stackedContents = new RecipeItemHelper();
    float zLevel;

    public InstallSalvageFrame(
            MPAWorkbenchContainer containerIn,
            PlayerEntity player,
            Point2D topleft,
            Point2D bottomright,
            float zLevel,
            Colour backgroundColour,
            Colour borderColour,
            ItemSelectionFrame targetItem,
            ModuleSelectionFrame targetModule) {
        super(topleft, bottomright, zLevel, backgroundColour, borderColour);
        this.container = containerIn;
        this.player = player;
        this.targetItem = targetItem;
        this.targetModule = targetModule;
        double sizex = border.right() - border.left();
        double sizey = border.bottom() - border.top();
        mc = Minecraft.getInstance();

        forwardArrow = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        forwardArrow.setDrawShaft(false);
        forwardArrow.setOnPressed(press-> setRecipe(recipeIndex +1));

        backArrow = new ClickableArrow(0, 0, 0, 0, true, arrowNormalBackGound, arrowHighlightedBackground, arrowBorderColour);
        backArrow.setDrawShaft(false);
        backArrow.setDirection(DrawableArrow.ArrowDirection.LEFT);
        backArrow.setOnPressed(press-> setRecipe(recipeIndex -1));

        this.craftingGrid = new InventoryFrame(container,
                new Point2D(0,0), new Point2D(0, 0),
                zLevel,
//                new Colour(0.545D, 0.545D, 0.545D, 1),
                Colour.DARK_GREY,
                Colour.LIGHT_BLUE.withAlpha(0.8F),
                new Colour(0.1F, 0.3F, 0.4F, 0.7F),
                3, 3, new ArrayList<Integer>(){{
            IntStream.range(1, 10).forEach(i-> add(i));
        }});

        /** Craft and Install button ---------------------------------------------------------------------------------- */
        this.craftAndInstallButton = new ClickableButton(new TranslationTextComponent("gui.modularpowerarmor.craftAndInstall"),
                new Point2D(border.right() - sizex / 2.0, border.bottom() - sizey / 4.0),
                true);

        craftAndInstallButton.setOnPressed(pressed->{
            if (targetItem.getSelectedItem() == null || targetModule.getSelectedModule() == null)
                return;

            CraftingResultSlot slot = (CraftingResultSlot) containerIn.getSlot(containerIn.getOutputSlot());
            if (!slot.canTakeStack(player))
                return;

            Integer containerIndex = targetItem.getSelectedItem().containerIndex;

            // target container slot index
            int moduleTarget = -1;
            if (containerIndex != null) {
                moduleTarget = getModuleTargetIndexInModularItem(containerIndex, targetModule.getSelectedModule().getModule());
                if (moduleTarget != -1 && containerIn.getSlot(containerIn.getOutputSlot()).getHasStack()) {
                    player.playSound(SoundDictionary.SOUND_EVENT_GUI_INSTALL, 1, 0);
                    containerIn.move(container.getOutputSlot(), moduleTarget);
                }
            }
        });


        /** Install button -------------------------------------------------------------------------------------------- */
        this.installButton = new ClickableButton(new TranslationTextComponent("gui.modularpowerarmor.install"),
                new Point2D(border.right() - sizex / 2.0, border.bottom() - sizey / 4.0),
                true);

        this.installButton.setOnPressed(press->{
            if (targetItem.getSelectedItem() == null || targetModule.getSelectedModule() == null)
                return;
            ItemStack module =  targetModule.getSelectedModule().getModule();
            Integer containerIndex = targetItem.getSelectedItem().containerIndex;

            // target container slot index
            int moduleTarget = -1;
            if (containerIndex != null) {
                moduleTarget = getModuleTargetIndexInModularItem(containerIndex, targetModule.getSelectedModule().getModule());
            }

            if (moduleTarget != -1) {
                int sourceIndex = getContainerIndexForModuleIndexInPlayerInventory(module);
                if (sourceIndex > 9) {
                    player.playSound(SoundDictionary.SOUND_EVENT_GUI_INSTALL, 1, 0);
                    containerIn.move(sourceIndex, moduleTarget);
                } else if (player.abilities.isCreativeMode) {
                    player.playSound(SoundDictionary.SOUND_EVENT_GUI_INSTALL, 1, 0);
                    containerIn.creativeInstall(moduleTarget, new ItemStack(module.getItem()));
                }
            }
        });

        /** Salvage button -------------------------------------------------------------------------------------------- */
        this.salvageButton = new ClickableButton(new TranslationTextComponent("gui.modularpowerarmor.salvage"),
                new Point2D(border.left() + sizex / 2.0, border.top() + sizey / 4.0),
                true);
        this.salvageButton.setOnPressed(pressed->{
            if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null && targetModule.getSelectedModule().isInstalled()) {
                Integer containerIndex = targetItem.getSelectedItem().containerIndex;

                if (containerIndex != null) {
                    List<SlotItemHandler> slots = container.getModularItemToSlotMap().get(targetItem.getSelectedItem().containerIndex);

                    Integer moduleContainerIndex = null;
                    for (SlotItemHandler slot : slots) {
                        if (ItemStack.areItemStacksEqual(slot.getStack(), targetModule.getSelectedModule().getModule())) {
                            moduleContainerIndex = slot.slotNumber;
                            break;
                        }
                    }

                    if(moduleContainerIndex != null) {
                        int targetIndex = getModuleTargetIndexInPlayerInventory(targetModule.getSelectedModule().getModule());
                        if (targetIndex > 0) {
                            player.playSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1, 0);
                            this.container.move(moduleContainerIndex, targetIndex);
                        }
                    }
                }
            }
        });
    }

    /**
     * Find a container slot index that holds the module to install
     * Only returns the first inventory index.
     *
     * @param module
     * @return
     */
    int getContainerIndexForModuleIndexInPlayerInventory(@Nonnull ItemStack module) {
        if (module.isEmpty())
            return -1;

        for (Slot slot : container.inventorySlots) {
            if (!slot.getStack().isEmpty() &&
                    slot.inventory == player.inventory &&
                    slot.getStack().isItemEqual(module)) {
                return slot.slotNumber;
            }
        }
        return -1;
    }

    /**
     *  Finds an empty container slot linked to player inventory to
     * put the module into when removed
     * @param module
     * @return
     */
    int getModuleTargetIndexInPlayerInventory(@Nonnull ItemStack module) {
        int index = -1;
        int targetIndex = player.inventory.getFirstEmptyStack();

        for (Slot slot : container.inventorySlots) {
            if (slot.inventory == player.inventory && targetIndex == slot.getSlotIndex()) {
                return container.inventorySlots.indexOf(slot);
            }
        }
        return index;
    }

    /**
     * Finds a slot that will except the module
     * @param modularItemIndex
     * @param module
     * @return
     */
    int getModuleTargetIndexInModularItem(int modularItemIndex, @Nonnull ItemStack module) {
        Slot slot = container.getSlot(modularItemIndex);
        if (slot == null)
            return -1;

        List<SlotItemHandler> slots = container.getModularItemToSlotMap().get(modularItemIndex);

        int slotIndex = -1;
        for (SlotItemHandler slotItemHandler: slots) {
            int index = slotItemHandler.getSlotIndex();

            if (slotItemHandler.getItemHandler() instanceof IModularItem) {
                if(slotItemHandler.getItemHandler().insertItem(index, module, true).isEmpty()) {
                    slotIndex = slotItemHandler.slotNumber;
                    break;
                }
            }
        }
        return slotIndex;
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
        this.craftingGrid.init(border.right() -7 - craftingGridSize,
                border.finalTop() + 5,
                border.right() -7,
                border.finalTop() + 5  + craftingGridSize);

        Point2D arrowsCenter =
                // center of crafting grid
                new Point2D(border.right() -14 - craftingGridSize/2,
                        // center between grid and border bottom
                        (border.finalBottom() - ( border.finalHeight() - craftingGridSize - 7)/2));

        this.forwardArrow.setTargetDimensions(arrowsCenter.plus(10, -10), new Point2D(12, 17));
        this.backArrow.setTargetDimensions(arrowsCenter.plus(-10, -10), new Point2D(12, 17));
    }

    @Override
    public void update(double x, double y) {
        super.update(x, y);

        double sizex = border.finalWidth();
        double sizey = border.finalHeight();

        this.craftAndInstallButton.setPosition(
                new Point2D(border.left() + (sizex - craftingGridSize -7)/ 2.0, border.bottom() - sizey / 2.0));
        this.installButton.setPosition(
                new Point2D( border.left() + sizex / 2.0, border.bottom() - sizey / 2.0));
        this.salvageButton.setPosition(
                new Point2D( border.left() + sizex / 2.0, border.bottom() - sizey / 2.0));

        if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null ) {
            if (this.timesInventoryChanged != this.mc.player.inventory.getTimesChanged()) {
                this.updateStackedContents();
                this.timesInventoryChanged = this.mc.player.inventory.getTimesChanged();
            }

            ClickableModule selectedModule = targetModule.getSelectedModule();
            // fixme: show conflicts, but where... maybe in the summary frame
            // fixme: add cooldown timer for changes to this logic. Sometimes the grid shows for a frame or 2 when it shoudlnt
            if (selectedModule.isInstalled()) {
                this.salvageButton.enableAndShow();
                this.installButton.disableAndHide();
                craftAndInstallButton.disableAndHide();
                craftingGridHide();
            } else if (player.abilities.isCreativeMode ||
                    player.inventory.hasItemStack(selectedModule.getModule())) {
                salvageButton.disableAndHide();
                installButton.enableAndShow();
                craftAndInstallButton.disableAndHide();
                craftingGridHide();
            } else {
                craftingGridShow();
                this.recipeList = getRecipesForModule();
                salvageButton.disableAndHide();
                installButton.disableAndHide();
                craftAndInstallButton.show();

                if (recipeList.isEmpty()) {
                    recipeIndex = -1;
                    craftAndInstallButton.disable();
                } else if (recipeIndex < 0) {
                    setRecipe(0);
                }
                if(this.recipe != null && !this.recipe.getRecipeOutput().isItemEqual(selectedModule.getModule())) {
                    this.setRecipe(recipeIndex);
                }
                craftAndInstallButton.setEnabled(this.recipe != null &&
                        // ghost recipe apparently isn't actually in the slots but just rendered over them
                        container.getSlot(container.getOutputSlot()).getHasStack());
            }
        }
    }

    public void updateStackedContents() {
        this.stackedContents.clear();
        this.mc.player.inventory.accountStacks(this.stackedContents);
        this.container.fillStackedContents(this.stackedContents);
//        this.updateCollections(false);
    }

    void craftingGridHide() {
        craftingGridIsVisible = false;
        setRecipe(-1);
        this.container.clear();
        for (int i = 1; i <10; i++ ) {
            Slot slot = container.getSlot(i);
            if (slot instanceof IHideableSlot) {
                ((IHideableSlot) slot).disable();
            }
        }
    }

    void craftingGridShow() {
        craftingGridIsVisible = true;
        for (int i = 1; i <10; i++ ) {
            Slot slot = container.getSlot(i);
            if (slot instanceof IHideableSlot) {
                ((IHideableSlot) slot).enable();
            }
        }
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        ITextComponent ret = null;
        if (salvageButton.isVisible() && salvageButton.hitBox(x, y)) {
            ret = new TranslationTextComponent("gui.modularpowerarmor.salvage.desc");
        }
        if (installButton.isVisible() && installButton.hitBox(x, y)) {
            if (installButton.isEnabled() && player.abilities.isCreativeMode) {
                ret = new TranslationTextComponent("gui.modularpowerarmor.install.creative.desc");
            } else if (installButton.isEnabled()) {
                Collections.singletonList(new TranslationTextComponent("gui.modularpowerarmor.install.desc"));
            } else {
                // todo: tell user why disabled...
                ret = new TranslationTextComponent("gui.modularpowerarmor.install.disabled.desc");
            }
        }

        if (craftAndInstallButton.isVisible() && craftAndInstallButton.hitBox(x, y)) {
            if (craftAndInstallButton.isEnabled()) {
                ret = new TranslationTextComponent("gui.modularpowerarmor.craftAndInstall.desc");
            } else {
                ret = new TranslationTextComponent("gui.modularpowerarmor.craftAndInstall.disabled.desc");
            }
        }

        if (ret != null) {
            return StringUtils.wrapITextComponentToLength(ret, 30);
        }
        return null;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null) {
            drawBackground(matrixStack,mouseX, mouseY, partialTicks);
            drawButtons(matrixStack, mouseX, mouseY, partialTicks);

            if (craftAndInstallButton.isVisible() && craftingGridIsVisible) {
                craftingGrid.render(matrixStack, mouseX, mouseY, partialTicks);
                if (!this.recipeList.isEmpty() && this.recipeList.size() > 1 && this.recipeIndex < this.recipeList.size() -1) {
                    forwardArrow.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);
                }
                if (!this.recipeList.isEmpty() && this.recipeList.size() > 1 && this.recipeIndex > 0){
                    backArrow.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);
                }
            } else {
                this.ghostRecipe.clear();
            }
        }
    }

    private void drawBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void drawButtons(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        salvageButton.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);
        installButton.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);
        craftAndInstallButton.render(matrixStack, mouseX, mouseY, partialTicks, zLevel);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (!border.containsPoint(x, y))
            return false;
        ClickableItem selectedItem = targetItem.getSelectedItem();
        ClickableModule selectedModule = targetModule.getSelectedModule();
        AtomicBoolean handled = new AtomicBoolean(false);
        if (selectedItem != null && !selectedItem.getStack().isEmpty() && selectedModule != null && !selectedModule.getModule().isEmpty()) {
            selectedItem.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                if (cap instanceof IModularItem) {
                    if (((IModularItem) cap).isModuleInstalled(selectedModule.getModule().getItem().getRegistryName())) {
                        if (salvageButton.mouseClicked(x, y, button)) {
                            handled.set(true);
                        }
                    } else {
                        if (craftAndInstallButton.mouseClicked(x, y, button) ||
                                forwardArrow.mouseClicked(x, y, button) ||
                                backArrow.mouseClicked(x, y, button) ||
                                installButton.mouseClicked(x, y, button)) {
                            handled.set(true);
                        }
                    }
                }
            });
        }
        return handled.get();
    }

    /**
     * Sets the guiLeft and guiTop offsets for the slot positions
     * @param ulOffset
     */
    public void setUlShift(Point2D ulOffset) {
        this.craftingGrid.setUlShift(ulOffset);
    }

    /**
     * Handles changing the current recipe list and current recipe.
     * A negative value clears the list, recipe, and grid.
     *
     * @param indexIn
     */
    void setRecipe(int indexIn) {
        this.ghostRecipe.clear();
        if (indexIn < 0) {
            this.recipe = null;
            this.recipeIndex = indexIn;
            this.container.clear();
        } else if (indexIn -1 <= this.recipeList.size()) {
//            if (indexIn != this.recipeIndex || this.recipe != this.recipeList.get(indexIn)) {
//                this.recipeIndex = indexIn;
//                this.recipe = this.recipeList.get(indexIn);
//
//                if (this.lastClickedRecipe != this.recipeList.get(indexIn)) {
//                    this.lastClickedRecipe = this.recipeList.get(indexIn);
//
//                    // FIXME: may need testing..
////                    this.container.onContainerClosed(player);
//
//                    this.recipeBook.setGuiOpen(false);
//                    this.recipeBook.setFilteringCraftable(true);
//
//                    Minecraft.getInstance().getConnection().sendPacket(
//                            new CRecipeInfoPacket(this.recipeBook.isGuiOpen(),
//                                    /* this.recipeBook.isFilteringCraftable()*/ false,
//                                    /* this.recipeBook.isFurnaceGuiOpen() */ false,
//                                    /* this.recipeBook.isFurnaceFilteringCraftable() */ false,
////                            this.recipeBook.func_216758_e(),
//                                    false,
////                            this.recipeBook.func_216761_f()
//                                    false));
//                    // send packet to setup recipe. If player can craft current recipe, then actual recipe is setup, otherwise a ghost recipe is setup
//                    MPAPackets.CHANNEL_INSTANCE.sendToServer(new CPlaceRecipePacket(player.openContainer.windowId, this.recipe, Screen.hasShiftDown()));
//                }
//            }
        }
    }


    /**
     * Another cloned vanilla method. This one renders the ghost recipe since it's an overlay on the crafting
     * grid rather than items in the slots. Apparently this simulates having items in the grid but disables
     * interaction with them.
     *
     * @param guiLeft
     * @param guiTop
     * @param isThreeByThree
     * @param partialTicks
     */
    public void renderGhostRecipe(int guiLeft,
                                  int guiTop,
                                  boolean isThreeByThree, // not really sure what this parameter is
                                  float partialTicks) {
//        this.ghostRecipe.render(Minecraft.getInstance(), guiLeft, guiTop, isThreeByThree, partialTicks);
    }

    /**
     * Renders the tooltip for the ghost recipe. It has a separate method like in vanilla due to the
     * weird xy transform in the "super most" (??) container screen method.
     *
     * @param guiLeft
     * @param guiTop
     * @param mouseX
     * @param mouseY
     */
    public void renderGhostRecipeTooltip(int guiLeft, int guiTop, int mouseX, int mouseY) {
//        ItemStack itemstack = ItemStack.EMPTY;
//
//        for(int i = 0; i < this.ghostRecipe.size(); ++i) {
//            GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ghostRecipe.get(i);
//            int j = ghostrecipe$ghostingredient.getX() + guiLeft;
//            int k = ghostrecipe$ghostingredient.getY() + guiTop;
//            if (mouseX >= j && mouseY >= k && mouseX < j + 16 && mouseY < k + 16) {
//                itemstack = ghostrecipe$ghostingredient.getItem();
//            }
//        }
//
//        if (itemstack != null && this.mc.currentScreen != null) {
//            this.mc.currentScreen.renderTooltip(this.mc.currentScreen.getTooltipFromItem(itemstack), mouseX, mouseY);
//        }
    }

    /**
     * Fetches the full list of recipes for the selected module whether they have been unlocked or not.
     * If no recipes found then the list is empty
     *
     * @return
     */
    List<IRecipe> getRecipesForModule() {
        List<IRecipe> out = new ArrayList<>();
        ItemStack module = targetModule.getSelectedModule().getModule();

        this.recipeBook = Minecraft.getInstance().player.getRecipeBook();

        if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null && !targetModule.getSelectedModule().isInstalled()) {
            for (IRecipe<?> irecipe : player.world.getRecipeManager().getRecipes()) {
                if (!irecipe.isDynamic()) {
                    if (irecipe.getType() == IRecipeType.CRAFTING &&
                            !irecipe.getRecipeOutput().isEmpty() &&
                            irecipe.getRecipeOutput().isItemEqual(module)) {
                        recipeBook.unlock(irecipe);
                        out.add(irecipe);
                    }
                }
            }
        }
        return out;
    }

    // AFAICT this is for marking the recipe as being seen by the player. Why, I have no idea.
    @Override
    public void recipesShown(List<IRecipe<?>> recipes) {
        for(IRecipe<?> irecipe : recipes) {
            Minecraft.getInstance().player.removeRecipeHighlight(irecipe);
        }
    }

    /**
     * Sets up the "Ghost Recipe" which is basically a recipe that cannot be crafted for whatever reason
     * but still needs to be displayed
     * @param recipe
     * @param slots
     */
    public void setupGhostRecipe(IRecipe<?> recipe, List<Slot> slots) {
        this.ghostRecipe.clear();
        ItemStack itemstack = recipe.getRecipeOutput();
        this.ghostRecipe.setRecipe(recipe);
        this.ghostRecipe.addIngredient(Ingredient.fromStacks(itemstack), (slots.get(0)).xPos, (slots.get(0)).yPos);
        this.placeRecipe(this.container.getWidth(), this.container.getHeight(), this.container.getOutputSlot(), recipe, recipe.getIngredients().iterator(), 0);
    }

    /** IRecipePlacer ------------------------------------------------------------------------------------------------- */
    @Override
    public void setSlotContents(Iterator<Ingredient> ingredients, int slotIn, int maxAmount, int y, int x) {
        Ingredient ingredient = ingredients.next();
        if (!ingredient.hasNoMatchingItems()) {
            Slot slot = this.container.inventorySlots.get(slotIn);
            this.ghostRecipe.addIngredient(ingredient, slot.xPos, slot.yPos);
        }
    }
}