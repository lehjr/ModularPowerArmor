package com.github.lehjr.modularpowerarmor.client.gui.crafting;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableArrow;
import com.github.lehjr.mpalib.client.gui.clickable.TexturedButton;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.frame.InventoryFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRelativeRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.modularpowerarmor.client.sound.SoundDictionary;
import com.github.lehjr.modularpowerarmor.client.gui.common.TabSelectFrame;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CraftingGui extends GuiContainer implements IRecipeShownListener {
    private final RecipeBookGui recipeBookGui;
    private boolean widthTooNarrow;
    protected DrawableRect backgroundRect;
    protected List<IGuiFrame> frames;
    protected InventoryFrame craftingGrid, mainInventory, hotbar;
    protected final Colour gridColour = new Colour(0.1F, 0.3F, 0.4F, 0.7F);
    protected final Colour gridBorderColour = Colour.LIGHTBLUE.withAlpha(0.8);
    protected final Colour gridBackGound = new Colour(0.545D, 0.545D, 0.545D, 1);
    protected DrawableRelativeRect result;
    protected ClickableArrow arrow;
    protected TexturedButton recipeBookButton;
    protected TabSelectFrame tabSelectFrame;
    final int slotWidth = 18;
    final int slotHeight = 18;
    int spacer = 7;
    EntityPlayer player;
    public CraftingGui(InventoryPlayer playerInv, World worldIn) {
        this(playerInv, worldIn, BlockPos.ORIGIN);
    }

    public CraftingGui(InventoryPlayer playerInv, World worldIn, BlockPos blockPosition) {
        super(new ContainerWorkbench(playerInv, worldIn, blockPosition));
        player = playerInv.player;
        frames = new ArrayList<>();

        this.recipeBookGui = new RecipeBookGui();

        backgroundRect = new DrawableRect(absX(-1), absY(-1), absX(1), absY(1), true,
                new Colour(0.0F, 0.2F, 0.0F, 0.8F),
                new Colour(0.1F, 0.9F, 0.1F, 0.8F));

        tabSelectFrame = new TabSelectFrame(playerInv.player, 3, blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());

        // slot 0 (result)
        result = new DrawableRelativeRect(0, 0, 0, 0, true,
                gridBackGound, gridBorderColour);

        // slot 1-9
        craftingGrid = new InventoryFrame(this.inventorySlots,
                new Point2D(0,0), new Point2D(0, 0),
                gridBackGound, gridBorderColour, gridColour,
                3, 3, new ArrayList<Integer>(){{
            IntStream.range(1, 10).forEach(i-> add(i));
        }});
        frames.add(craftingGrid);

        // slot 10-36
        mainInventory = new InventoryFrame(this.inventorySlots,
                new Point2D(0,0), new Point2D(0, 0),
                gridBackGound, gridBorderColour, gridColour,
                9, 3, new ArrayList<Integer>(){{
            IntStream.range(10, 37).forEach(i-> add(i));
        }});
        frames.add(mainInventory);

        hotbar = new InventoryFrame(this.inventorySlots,
                new Point2D(0,0), new Point2D(0, 0),
                gridBackGound, gridBorderColour, gridColour,
                9, 1, new ArrayList<Integer>(){{
            IntStream.range(37, 46).forEach(i-> add(i));
        }});
        frames.add(hotbar);

        arrow = new ClickableArrow(0, 0, 0, 0, true, gridBackGound, Colour.WHITE, gridBorderColour);
        arrow.show();

        recipeBookButton = new TexturedButton(
                0, 0, 0, 0, true,
                gridBackGound,
                gridBackGound,
                gridBorderColour,
                gridBorderColour,
                16,
                Constants.TEXTURE_PREFIX + "gui/recipe_book_button.png");
        recipeBookButton.setEnabled(true);
        recipeBookButton.setVisible(true);
        recipeBookButton.setTextureOffsetX(-8.0);
        recipeBookButton.setTextureOffsetY(-8.0);
    }

    Point2D getUlOffset () {
        return new Point2D(guiLeft + 8, guiTop + 8);
    }

    private void guiElementsMoveLeft(double moveAmmount) {
        backgroundRect.setLeft(backgroundRect.left() + moveAmmount);

        hotbar.setUlShift(getUlOffset());
        hotbar.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalBottom() - spacer - slotHeight,
                backgroundRect.finalLeft() + spacer + 9 * slotWidth,
                backgroundRect.finalBottom() - spacer);

        mainInventory.setUlShift(getUlOffset());
        mainInventory.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalBottom() - spacer - slotHeight - spacer - 3 * slotHeight,
                backgroundRect.finalLeft() + spacer + 9 * slotWidth,
                backgroundRect.finalBottom() - spacer - slotHeight - spacer);

        craftingGrid.setUlShift(getUlOffset());
        craftingGrid.init(
                backgroundRect.finalLeft() + 29,
                backgroundRect.finalTop() + 16,
                backgroundRect.finalLeft() + 29  + 3 * slotWidth,
                backgroundRect.finalTop() + 16 + 3 * slotHeight);

        result.setLeft(result.left() + moveAmmount);

        arrow.setLeft(arrow.left() + moveAmmount);

        recipeBookButton.setLeft(recipeBookButton.left() + moveAmmount);

        tabSelectFrame.init(
                (recipeBookGui.isVisible() ? recipeBookGui.xOffset : guiLeft),
                absY(-1.05F),
                absX(0.95F),
                absY(-0.95f)
        );
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        super.initGui();
        this.widthTooNarrow = this.width < 379;
        // do not call anything recipe book related before this
        this.recipeBookGui./*init*/func_194303_a(this.width, this.height, this.mc, this.widthTooNarrow, ((ContainerWorkbench) this.inventorySlots).craftMatrix);
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
        backgroundRect.setTargetDimensions(new Point2D(getGuiLeft(), getGuiTop()), new Point2D(getXSize(), getYSize()));
        hotbar.setUlShift(getUlOffset());
        hotbar.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalBottom() - spacer - slotHeight,
                backgroundRect.finalLeft() + spacer + 9 * slotWidth,
                backgroundRect.finalBottom() - spacer);


        mainInventory.setUlShift(getUlOffset());
        mainInventory.init(
                backgroundRect.finalLeft() + spacer,
                backgroundRect.finalBottom() - spacer - slotHeight - spacer - 3 * slotHeight,
                backgroundRect.finalLeft() + spacer + 9 * slotWidth,
                backgroundRect.finalBottom() - spacer - slotHeight - spacer);

        craftingGrid.setUlShift(getUlOffset());
        craftingGrid.init(
                backgroundRect.finalLeft() + 29,
                backgroundRect.finalTop() + 16,
                backgroundRect.finalLeft() + 29  + 3 * slotWidth,
                backgroundRect.finalTop() + 16 + 3 * slotHeight);

        result.setTargetDimensions(new Point2D(getGuiLeft() + 120, getGuiTop() + 31), new Point2D(24, 24));

        arrow.setTargetDimensions(new Point2D(getGuiLeft() + 90, getGuiTop() + 31), new Point2D(24, 24));
        arrow.setOnPressed(press-> {
            Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, player.getPosition());
            inventorySlots.transferStackInSlot(player, 0);
        });

        tabSelectFrame.init(
                (recipeBookGui.isVisible() ? recipeBookGui.xOffset : guiLeft),
                absY(-1.05F),
                absX(0.95F),
                absY(-0.95f)
        );

        recipeBookButton.setTargetDimensions(new Point2D(getGuiLeft() + 5, this.height / 2 - 49), new Point2D(18, 20));
        recipeBookButton.setOnPressed((button)-> {
            /** this is everything the button does when pressed */
            this.recipeBookGui.initVisuals(this.widthTooNarrow, ((ContainerWorkbench) this.inventorySlots).craftMatrix);
            this.recipeBookGui.toggleVisibility();
            double oldLeft = guiLeft;
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            guiElementsMoveLeft(guiLeft - oldLeft);
            Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, player.getPosition());
        });
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        super.updateScreen();
        this.recipeBookGui.tick();
    }

    public void drawRectangularBackground() {
        backgroundRect.draw();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawRectangularBackground();
        update(mouseX, mouseY);
        if (backgroundRect.width() == getXSize()) {
            if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
                this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
                this.recipeBookGui.render(mouseX, mouseY, partialTicks);
            } else {
                this.recipeBookGui.render(mouseX, mouseY, partialTicks);

                renderFrames(mouseX, mouseY, partialTicks);
                super.drawScreen(mouseX, mouseY, partialTicks);
                this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
            }
            tabSelectFrame.render(mouseX, mouseY, partialTicks);
            this.renderHoveredToolTip(mouseX, mouseY);
            this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
        }
    }

    // new
    public void update(double x, double y) {
        for (IGuiFrame frame : frames) {
            frame.update(x, y);
        }
    }

    public void renderFrames(int mouseX, int mouseY, float partialTicks) {
        for (IGuiFrame frame : frames) {
            frame.render(mouseX, mouseY, partialTicks);
        }
        result.draw();
        arrow.render(mouseX, mouseY, partialTicks);
        recipeBookButton.render(mouseX, mouseY, partialTicks);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.format("container.crafting"), 28, 6, Colour.WHITE.getInt());
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, Colour.WHITE.getInt());
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Test if the 2D point is in a rectangle (relative to the GUI). Args : rectX, rectY, rectWidth, rectHeight, pointX,
     * pointY
     */
    protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        tabSelectFrame.onMouseDown(mouseX, mouseY, mouseButton);

        if (this.recipeBookButton.mouseClicked(mouseX, mouseY, mouseButton)) {
            recipeBookButton.onPressed();
            return;
        }

        if (this.arrow.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
        }

        if (this.recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
        } else {
            if (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) {
                super.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected boolean hasClickedOutside(int mouseX, int mouseY, int guiLeft, int guiTop) {
        boolean flag = mouseX < guiLeft || mouseY < guiTop || mouseX >= guiLeft + this.xSize || mouseY >= guiTop + this.ySize;
        return this.recipeBookGui.hasClickedOutside(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize) && flag;
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        // moved to button code above
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.recipeBookGui.keyPressed(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    public void recipesUpdated() {
        this.recipeBookGui.recipesUpdated();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
        this.recipeBookGui.removed();
        super.onGuiClosed();
    }

    public GuiRecipeBook /* getRecipeBook */ func_194310_f() {
        return this.recipeBookGui;
    }

    /**
     * Returns absolute screen coordinates (int 0 to width) from a relative
     * coordinate (float -1.0F to +1.0F)
     *
     * @param relx Relative X coordinate
     * @return Absolute X coordinate
     */
    public int absX(double relx) {
        int absx = (int) ((relx + 1) * xSize / 2);
        int xpadding = (width - xSize) / 2;
        return absx + xpadding;
    }

    /**
     * Returns absolute screen coordinates (int 0 to width) from a relative
     * coordinate (float -1.0F to +1.0F)
     *
     * @param rely Relative Y coordinate
     * @return Absolute Y coordinate
     */
    public int absY(double rely) {
        int absy = (int) ((rely + 1) * ySize / 2);
        int ypadding = (height - ySize) / 2;
        return absy + ypadding;
    }
}