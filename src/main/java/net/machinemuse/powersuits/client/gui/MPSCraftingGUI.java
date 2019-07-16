package net.machinemuse.powersuits.client.gui;

import net.machinemuse.numina.client.gui.clickable.TexturedButton;
import net.machinemuse.numina.client.gui.frame.IGuiFrame;
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.math.geometry.DrawableMuseArrow;
import net.machinemuse.numina.math.geometry.DrawableMuseRectangularGrid;
import net.machinemuse.numina.math.geometry.DrawableMuseRelativeRect;
import net.machinemuse.numina.math.geometry.MusePoint2D;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.client.gui.testing.MPSRecipeBookGui;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.containers.MPSCraftingContainer;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;


@OnlyIn(Dist.CLIENT)
public class MPSCraftingGUI extends MuseGUI2<MPSCraftingContainer> implements IRecipeShownListener, IGuiFrame {
    /** need this for various reasons */
    private final MPSRecipeBookGui recipeBookGui = new MPSRecipeBookGui();
    private boolean widthTooNarrow;
    protected MPSCraftingContainer container;

    /** The outer green rectangle */
    protected DrawableMuseRelativeRect border;
    protected final Colour gridColour = new Colour(0.1F, 0.3F, 0.4F, 0.7F);
    protected final Colour gridBorderColour = Colour.LIGHTBLUE.withAlpha(0.8);
    protected final Colour gridBackGound = Colour.DARKBLUE.withAlpha(0.8);
    protected DrawableMuseRectangularGrid hotbar, mainInventory, craftingGrid;
    protected DrawableMuseRelativeRect result;
    protected DrawableMuseArrow arrow;
    protected TexturedButton recipeBookButton;

    /**
     * Fun fact: initializing GUI element placement values in the constructor is meaningless because the placement
     *  values aren't set until init()
     */
    public MPSCraftingGUI(MPSCraftingContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.container = container;
        border = new DrawableMuseRelativeRect(0, 0, 0, 0, true,
                new Colour(0.0F, 0.2F, 0.0F, 0.8F),
                new Colour(0.1F, 0.9F, 0.1F, 0.8F));
        hotbar = new DrawableMuseRectangularGrid(0, 0, 0, 0, true,
                gridBackGound, gridBorderColour, gridColour,  1, 9);
        mainInventory = new DrawableMuseRectangularGrid(0, 0, 0, 0, true,
                gridBackGound, gridBorderColour, gridColour,  3, 9);
        craftingGrid = new DrawableMuseRectangularGrid(0, 0, 0, 0, true,
                gridBackGound, gridBorderColour, gridColour,  3, 3);
        result = new DrawableMuseRelativeRect(0, 0, 0, 0, true,
                gridBackGound, gridBorderColour);

        // FIXME: color is backwards
        arrow = new DrawableMuseArrow(0, 0, 0, 0, true, gridBackGound, gridBorderColour);

        recipeBookButton = new TexturedButton(
                0, 0, 0, 0, true,
                gridBackGound,
                gridBackGound,
                gridBorderColour,
                gridBorderColour,
                16,
                new ResourceLocation(MPSConstants.TEXTURE_PREFIX + "gui/recipe_book_button.png"));
        recipeBookButton.setEnabled(true);
        recipeBookButton.setVisible(true);
        recipeBookButton.setTextureOffsetX(-8.0);
        recipeBookButton.setTextureOffsetY(-8.0);
    }

    private void guiElementsMoveLeft(double moveAmmount) {
        border.setLeft(border.left() + moveAmmount);
        hotbar.setLeft(hotbar.left() + moveAmmount);
        mainInventory.setLeft(mainInventory.left() + moveAmmount);
        craftingGrid.setLeft(craftingGrid.left() + moveAmmount);
        result.setLeft(result.left() + moveAmmount);
        arrow.setLeft(arrow.left() + moveAmmount);
        recipeBookButton.setLeft(recipeBookButton.left() + moveAmmount);
    }

    @Override
    public void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        // do not call anything recipe book related before this
        this.recipeBookGui.func_201520_a(this.width, this.height, this.minecraft, this.widthTooNarrow, this.container);
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
        border.setTargetDimensions(new MusePoint2D(getGuiLeft(), getGuiTop()), new MusePoint2D(getXSize(), getYSize()));
        hotbar.setTargetDimensions(new MusePoint2D(getGuiLeft() + 7, getGuiTop()+ 141), new MusePoint2D(getXSize() - 14, 18));
        mainInventory.setTargetDimensions(new MusePoint2D(getGuiLeft() + 7, getGuiTop() + 83), new MusePoint2D(getXSize() - 14, 54));
        craftingGrid.setTargetDimensions(new MusePoint2D(getGuiLeft() + 29, getGuiTop() + 16), new MusePoint2D(54, 54));
        result.setTargetDimensions(new MusePoint2D(getGuiLeft() + 120, getGuiTop() + 31), new MusePoint2D(24, 24));
        arrow.setTargetDimensions(new MusePoint2D(getGuiLeft() + 90, getGuiTop() + 31), new MusePoint2D(24, 24));

        this.children.add(this.recipeBookGui);
        this.func_212928_a(this.recipeBookGui);

        recipeBookButton.setTargetDimensions(new MusePoint2D(getGuiLeft() + 5, this.height / 2 - 49), new MusePoint2D(18, 20));
        recipeBookButton.setOnPressed((button)-> {
            /** this is everything the button does when pressed */
            this.recipeBookGui.func_201518_a(this.widthTooNarrow);
            this.recipeBookGui.toggleVisibility();
            double oldLeft = guiLeft;
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            guiElementsMoveLeft(guiLeft - oldLeft);
            Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
        });
    }

    @Override
    public void tick() {
        super.tick();
        this.recipeBookGui.tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();


        // TODO: make items and textured buttons not show until gui done growing
        if (border.width() == getXSize()) {
            recipeBookButton.render(mouseX, mouseY, partialTicks);
            if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
                this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
                this.recipeBookGui.render(mouseX, mouseY, partialTicks);
            } else {
                this.recipeBookGui.render(mouseX, mouseY, partialTicks);
                super.render(mouseX, mouseY, partialTicks);
                this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
            }
            this.renderHoveredToolTip(mouseX, mouseY);
            this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
            this.func_212932_b(this.recipeBookGui);
        }
    }
    @Override
    public List<ITextComponent> getToolTip(int i, int i1) {
        return null;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 28.0F, 6.0F, Colour.WHITE.getInt());
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), Colour.WHITE.getInt());
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }

    @Override
    protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {

//        System.out.println("p_195359_1_: " + p_195359_1_);
//        System.out.println("p_195359_2_: " + p_195359_2_);
//        System.out.println("p_195359_3_: " + p_195359_3_);
//        System.out.println("p_195359_4_: " + p_195359_4_);
//        System.out.println("p_195359_5_: " + p_195359_5_);


        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBookButton.hitBox(mouseX, mouseY)) {
            recipeBookButton.onPressed();
            return true;
        }

        if (this.recipeBookGui.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            return this.widthTooNarrow && this.recipeBookGui.isVisible() ? true : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        return false;
    }

    @Override
    public void update(double mouseX, double mouseY) {

    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int scaledWidth, int scaledHeight, int button) {
        boolean flag = mouseX < (double) scaledWidth || mouseY < (double) scaledHeight || mouseX >= (double) (scaledWidth + this.xSize) || mouseY >= (double) (scaledHeight + this.ySize);
        return this.recipeBookGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, button) && flag;
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    @Override
    public void renderBackground() {
        super.renderBackground();
        border.draw();
        hotbar.draw();
        mainInventory.draw();
        craftingGrid.draw();
        result.draw();
        arrow.draw();



        //    @Override
//    public void renderBackground() {
//        super.renderBackground();
//
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

    }

    @Override
    public void recipesUpdated() {
        this.recipeBookGui.recipesUpdated();
    }

    @Override
    public void removed() {
        this.recipeBookGui.removed();
        super.removed();
    }

    @Override
    public RecipeBookGui func_194310_f() {
        return this.recipeBookGui;
    }
}