//package net.machinemuse.powersuits.client.gui.bu;
//
//import com.mojang.blaze3d.platform.GlStateManager;
//import net.machinemuse.numina.client.gui.clickable.ClickableButton;
//import net.machinemuse.numina.client.gui.clickable.ClickableItemSlot;
//import net.machinemuse.numina.client.gui.frame.IGuiFrame;
//import net.machinemuse.numina.math.Colour;
//import net.machinemuse.numina.math.geometry.DrawableMuseArrow;
//import net.machinemuse.numina.math.geometry.DrawableMuseRectangularGrid;
//import net.machinemuse.numina.math.geometry.DrawableMuseRelativeRect;
//import net.machinemuse.numina.math.geometry.MusePoint2D;
//import net.machinemuse.powersuits.basemod.MPSConstants;
//import net.machinemuse.powersuits.client.gui.MuseGUI2;
//import net.machinemuse.powersuits.containers.MPSCraftingContainer;
//import net.minecraft.client.gui.recipebook.IRecipeShownListener;
//import net.minecraft.client.gui.recipebook.RecipeBookGui;
//import net.minecraft.client.gui.widget.button.ImageButton;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.inventory.container.ClickType;
//import net.minecraft.inventory.container.Slot;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.text.ITextComponent;
//
////import net.machinemuse.powersuits.client.gui.testing.DrawableMuseRectangularGrid;
//
//public class MPSCraftingGUI extends MuseGUI2<MPSCraftingContainer> implements IRecipeShownListener, IGuiFrame {
//    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(MPSConstants.MODID,"textures/gui/crafting_table2.png");
//    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
//    private final RecipeBookGui recipeBookGui = new RecipeBookGui();
//    private boolean widthTooNarrow;
//
//    protected final PlayerEntity player;
//
//    protected final Colour gridColour = new Colour(0.1F, 0.3F, 0.4F, 0.7F);
//    protected final Colour gridBorderColour = Colour.LIGHTBLUE.withAlpha(0.8);
//    protected final Colour gridBackGound = Colour.DARKBLUE.withAlpha(0.8);
//
//    int padding = 4;
//    int gridSize = 18;
//
//    protected DrawableMuseRelativeRect border;
//    protected DrawableMuseRectangularGrid hotbarRect;
//    protected DrawableMuseRectangularGrid mainInventoryRect;
//    protected DrawableMuseRectangularGrid craftingGridRect;
//    protected DrawableMuseArrow arrow;
//
//    protected DrawableMuseRelativeRect resultRect;
//
//    ClickableButton bookButton;
//
//
//
//
//    // rectangle coordinates in the constructor don't mean anything. put real ones init.
//    public MPSCraftingGUI(MPSCraftingContainer container, PlayerInventory playerInventory, ITextComponent title) {
//        super(container, playerInventory, title);
//        this.player = playerInventory.player;
//
//        /*
//            slot map
//            ------------
//            0 -> crafting result
//            1-9 - crafting inventory
//            10 - 37 main inventory
//            38 - 46 hotbar
//
//
//
//         */
//
//
//
//        System.out.println(("container size: " + container.inventorySlots.size()));
//
//
//
//
//
//
//        // xsize and y size change the drawing area and placement...
//
//        border = new DrawableMuseRelativeRect(0, 0, 0, 0, true,
//                new Colour(0.1F, 0.9F, 0.1F, 0.8F),
//                new Colour(0.0F, 0.2F, 0.0F, 0.8F));
//
//        craftingGridRect = new DrawableMuseRectangularGrid(getGuiLeft() + 4, border.bottom() + 10, border.left() -2, border.bottom() +2, true,
//                gridBackGound, gridBorderColour, gridColour, 3, 3);
//
//        mainInventoryRect = new DrawableMuseRectangularGrid(getGuiLeft() + 4, border.bottom() + 10, border.left() -2, border.bottom() +2, true,
//                gridBackGound, gridBorderColour, gridColour, 3, 9);
//
//        hotbarRect = new DrawableMuseRectangularGrid(getGuiLeft() + 4, border.bottom() + 10, border.left() -2, border.bottom() +2, true,
//                gridBackGound, gridBorderColour, gridColour, 1, 9);
//
//        arrow = new DrawableMuseArrow(getGuiLeft() + 4, border.bottom() + 10, border.left() -2, border.bottom() +2, true,
//                gridBorderColour, gridBackGound);
//
//        resultRect = new DrawableMuseRelativeRect(absX(-1), absY(-1), absX(1), absY(1), true, gridBackGound, gridBorderColour);
//
//        bookButton = new ClickableButton("All your tortoise are belong to us", new MusePoint2D(getGuiLeft() + padding, padding + gridSize * 1.5), true);
//
//        container.inventorySlots.get(0).xPos = (int)resultRect.centerx();
//        container.inventorySlots.get(0).yPos = (int)resultRect.centery();
//
//        // set output locations
//        for (int index = 1; index < 9; index ++) {
//            container.inventorySlots.get(index).xPos = (int)craftingGridRect.getBoxes()[index -1].centerx();
//            container.inventorySlots.get(index).yPos = (int)craftingGridRect.getBoxes()[index -1].centery();
//        }
//
//        // set grid locations
//        for (int index = 10; index < 37; index ++) {
//            container.inventorySlots.get(index).xPos = (int)mainInventoryRect.getBoxes()[index -10].centerx();
//            container.inventorySlots.get(index).yPos = (int)mainInventoryRect.getBoxes()[index -10].centery();
//        }
//
//        // set hotbar locations
//        for (int index = 38; index < 46; index ++) {
//            container.inventorySlots.get(index).xPos = (int)hotbarRect.getBoxes()[index -38].centerx();
//            container.inventorySlots.get(index).yPos = (int)hotbarRect.getBoxes()[index -38].centery();
//        }
//    }
//
//    // current screen settings put grid height at about 21 per item
//    @Override
//    protected void init() {
//        super.init();
//        border.setTargetDimensions(new MusePoint2D(getGuiLeft(), getGuiTop()), new MusePoint2D(getXSize(), getYSize()));
//        craftingGridRect.setTargetDimensions(new MusePoint2D(getGuiLeft() + 32, getGuiTop() + (padding * 4)), new MusePoint2D(gridSize * 3, gridSize * 3));
//        arrow.setTargetDimensions(new MusePoint2D(getGuiLeft() + 32 + (gridSize * 3) + padding, getGuiTop() + padding * 2 +  gridSize * 1.5), new MusePoint2D(gridSize, gridSize));
//
//        resultRect.setTargetDimensions(new MusePoint2D(getGuiLeft() + 32 + (gridSize * 3) + padding * 3 + gridSize, getGuiTop() + padding +  gridSize * 1.5), new MusePoint2D(gridSize + padding *2, gridSize + padding *2));
//
//        mainInventoryRect.setTargetDimensions(new MusePoint2D(getGuiLeft() + padding, getGuibottom() - (gridSize * 4) - (padding * 2)), new MusePoint2D(getXSize() - (padding * 2), gridSize * 3));
//        hotbarRect.setTargetDimensions(new MusePoint2D(getGuiLeft() + padding, getGuibottom() - gridSize  - padding), new MusePoint2D(getXSize() - (padding * 2), gridSize));
//
//
//
//        this.widthTooNarrow = this.width < 379;
//        this.recipeBookGui.func_201520_a(this.width, this.height, this.minecraft, this.widthTooNarrow, this.container);
//        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
//        this.children.add(this.recipeBookGui);
//        this.func_212928_a(this.recipeBookGui);
//        this.addButton(new ImageButton(this.guiLeft + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (p_214076_1_) -> {
//            this.recipeBookGui.func_201518_a(this.widthTooNarrow);
//            this.recipeBookGui.toggleVisibility();
//            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
//            ((ImageButton)p_214076_1_).setPosition(this.guiLeft + 5, this.height / 2 - 49);
//        }));
//    }
//
//    @Override
//    public void tick() {
//        super.tick();
//        this.recipeBookGui.tick();
//    }
//
//    @Override
//    public void render(int mouseX, int mouseY, float partialTicks) {
//        this.renderBackground();
//        bookButton.render(mouseX, mouseY, partialTicks);
//
//        for (int i = 1; i < container.inventorySlots.size(); i++) {
//            ((ClickableItemSlot)container.inventorySlots.get(i)).render(mouseX, mouseY, partialTicks);
//        }
//
//        if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
//            this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
//            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
//        } else {
//            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
//            super.render(mouseX, mouseY, partialTicks);
//            this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
//        }
//
//        this.renderHoveredToolTip(mouseX, mouseY);
//        this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
//        this.func_212932_b(this.recipeBookGui);
//    }
//
//
//    // fixme: eliminate and use the slot version instead
//    protected void renderHoveredToolTip(int p_191948_1_, int p_191948_2_) {
//        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
//            this.renderTooltip(this.hoveredSlot.getStack(), p_191948_1_, p_191948_2_);
//        }
//    }
//
//
//    /**
//     * Draw the foreground layer for the GuiContainer (everything in front of the items)
//     */
//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//        this.font.drawString(this.title.getFormattedText(), 28.0F  + getGuiLeft(), padding + getGuiTop(), Colour.WHITE.getInt());
//        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F+ getGuiLeft(), (float)(this.ySize - 96 + padding) + getGuiTop(), Colour.WHITE.getInt());
//    }
//
//    /**
//     * Draws the background layer of this container (behind the items).
//     */
//    @Override
//    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//        this.minecraft.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
//        int i = this.guiLeft;
//        int j = (this.height - this.ySize) / 2;
//        this.blit(i, j, 0, 0, this.xSize, this.ySize);
//    }
//
//    @Override
//    protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {
//        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
//    }
//
//    @Override
//    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
//        if (this.recipeBookGui.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
//            return true;
//        } else {
//            return this.widthTooNarrow && this.recipeBookGui.isVisible() ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
//        }
//    }
//
//    @Override
//    public boolean onMouseScrolled(double v, double v1, double v2) {
//        return false;
//    }
//
//    @Override
//    public void update(double v, double v1) {
//
//    }
//
//    @Override
//    protected boolean hasClickedOutside(double mouseX, double mouseY, int p_195361_5_, int p_195361_6_, int button) {
//        boolean flag = mouseX < (double)p_195361_5_ || mouseY < (double)p_195361_6_ || mouseX >= (double)(p_195361_5_ + this.xSize) || mouseY >= (double)(p_195361_6_ + this.ySize);
//        return this.recipeBookGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, button) && flag;
//    }
//
//    /**
//     * Called when the mouse is clicked over a slot or outside the gui.
//     */
//    @Override
//    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
//        super.handleMouseClick(slotIn, slotId, mouseButton, type);
//        this.recipeBookGui.slotClicked(slotIn);
//    }
//
//    @Override
//    public void recipesUpdated() {
//        this.recipeBookGui.recipesUpdated();
//    }
//
//    @Override
//    public void removed() {
//        this.recipeBookGui.removed();
//        super.removed();
//    }
//
//    @Override
//    public void renderBackground() {
//        super.renderBackground();
//        border.draw();
//        craftingGridRect.draw();
//        arrow.draw();
//        resultRect.draw();
//
//        mainInventoryRect.draw();
//        hotbarRect.draw();
//
//
//
//    }
//
//    @Override
//    public RecipeBookGui func_194310_f() {
//        return this.recipeBookGui;
//    }
//}
