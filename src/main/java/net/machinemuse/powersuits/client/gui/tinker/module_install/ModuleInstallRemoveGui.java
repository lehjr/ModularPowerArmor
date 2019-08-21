package net.machinemuse.powersuits.client.gui.tinker.module_install;

import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.client.gui.MuseContainerGui;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseRect;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.client.gui.tinker.common.InventoryFrame;
import net.machinemuse.powersuits.client.gui.tinker.common.ItemSelectionFrame;
import net.machinemuse.powersuits.containers.IModularItemToSlotMapProvider;
import net.machinemuse.powersuits.containers.ModularItemContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Requires all module and inventory slots be accounted for before constructing
 *
 *
 */
public class ModuleInstallRemoveGui extends MuseContainerGui<ModularItemContainer> {
    protected DrawableMuseRect backgroundRect;
    protected ItemSelectionFrame itemSelectFrame;
    protected final PlayerEntity player;
    Container container;
    protected final Colour gridColour = new Colour(0.1F, 0.3F, 0.4F, 0.7F);
    protected final Colour gridBorderColour = Colour.LIGHTBLUE.withAlpha(0.8);
    protected final Colour gridBackGound = Colour.DARKBLUE.withAlpha(0.8);
    ModuleSelectionFrame2 moduleSelectFrame;
    protected InventoryFrame hotbar, mainInventory;
    final int slotWidth = 18;
    final int slotHeight = 18;
    final int spacer = 6;


    /**
     * FIXME: add way to disable moving the modular items
     * FIXME: need to bring back old ClickableModularItems as buttons.
     * Fixme: make item selection frame scrollable and have mimimumn spacing
     *

     */
    public ModuleInstallRemoveGui(ModularItemContainer containerIn, PlayerInventory playerInventory, ITextComponent titleIn) {
        super(containerIn, playerInventory, titleIn);
        this.player = playerInventory.player;
        this.container = containerIn;
        rescale();

        backgroundRect = new DrawableMuseRect(absX(-1), absY(-1), absX(1), absY(1), true,
                new Colour(0.0F, 0.2F, 0.0F, 0.8F),
                new Colour(0.1F, 0.9F, 0.1F, 0.8F));

        itemSelectFrame = new ItemSelectionFrame((IModularItemToSlotMapProvider) this.container,
                new MusePoint2D(absX(-0.95F), absY(-0.95F)),
                new MusePoint2D(absX(-0.78F), absY(0.95F)),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHTBLUE.withAlpha(0.8F), player);
        frames.add(itemSelectFrame);

        hotbar = new InventoryFrame(containerIn,
                new MusePoint2D(0,0), new MusePoint2D(0, 0),
                gridBackGound, gridBorderColour, gridColour,
                9, 1, new ArrayList<Integer>(){{
            IntStream.range(0, 9).forEach(i-> add(i));
        }});
        frames.add(hotbar);

        mainInventory = new InventoryFrame(containerIn,
                new MusePoint2D(0,0), new MusePoint2D(0, 0),
                gridBackGound, gridBorderColour, gridColour,
                9, 3, new ArrayList<Integer>(){{
            IntStream.range(9, 36).forEach(i-> add(i));
        }});
        frames.add(mainInventory);

        moduleSelectFrame = new ModuleSelectionFrame2(containerIn, itemSelectFrame,
                new MusePoint2D(absX(-0.75F), absY(-0.95f)), new MusePoint2D(absX(-0.05F), absY(0.75f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8));
        frames.add(moduleSelectFrame);

        itemSelectFrame.setDoOnNewSelect(doThis-> moduleSelectFrame.loadModules());

        /*
         TODO:
          * summary frame
          * install/remove subframe with crafting grid
          * tweak frame/subframe
          * itemSelection frame needs minimum spacing with scroll option






         */








//        itemSelectFrame.setDoOnNewSelect(thing->{
//            inventoryFrame.closeSlots();
//            inventoryFrame.loadSlots();
//        });
    }

    public void rescale() {
        this.setXSize(Math.min(minecraft.mainWindow.getScaledWidth() - 20, 300));
        this.setYSize(Math.min(minecraft.mainWindow.getScaledHeight() - 20, 300));

        System.out.println("xSize: " + xSize);
        System.out.println("ySize: " + ySize);

    }

    @Override
    public void init() {
//        this.setXSize(200);
        rescale();
        super.init();
        backgroundRect.setTargetDimensions(getGuiLeft(), getGuiTop(), getGuiLeft() + getXSize(), getGuiTop() + getYSize());

        itemSelectFrame.init(
                backgroundRect.finalLeft()  + spacer,
                backgroundRect.finalTop() + spacer,
                backgroundRect.finalLeft() + 32,
                backgroundRect.finalBottom() - spacer);

        // 8 = 1/2 actual slot size of 16x16 because their position is the upper left, not center
        MusePoint2D ulOffset = new MusePoint2D(guiLeft + 8, guiTop + 8);

        hotbar.setUlShift( ulOffset);
        hotbar.init(
                backgroundRect.finalLeft() + 32 + spacer, // item selection frame right plus spacer
                backgroundRect.finalBottom() - spacer - slotHeight, // bottom minus 1 slot
                backgroundRect.finalLeft() + 32 + spacer + 9 * slotWidth, // item selection frame right plus spacer + 9 slots
                backgroundRect.finalBottom() - spacer);

        mainInventory.init(
                backgroundRect.finalLeft() + 32 + spacer, // item selection frame right plus spacer
                backgroundRect.finalBottom() - spacer - slotHeight - spacer - 3 * slotHeight, // hotbar top minus spacer minus 3 slots high
                backgroundRect.finalLeft() + 32 + spacer + 9 * slotWidth, // item selection frame right plus spacer + 9 slots wide
                backgroundRect.finalBottom() - spacer - slotHeight - spacer); // hotbar top minus spacer
        mainInventory.setUlShift(ulOffset);


        moduleSelectFrame.init(
                backgroundRect.finalLeft() + 32 + spacer, // item selection frame right plus spacer,
                backgroundRect.finalTop() + spacer, // border top plus spacer
                absX(-0.05F),
                backgroundRect.finalBottom() - spacer - slotHeight - spacer - 3 * slotHeight - spacer);

        //        inventoryFrame.updateUlGui(new MusePoint2D(guiLeft, guiTop));
//        inventoryFrame.init(getGuiLeft() + 32, getGuiTop() + 6, getGuiLeft() + getXSize() -6, getGuiTop() + ySize - 90);
//


    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void renderBackground() {
        super.renderBackground();
        this.backgroundRect.draw();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        System.out.println("clicked: " + x + ", " + y);



        return super.mouseClicked(x, y, button);
    }

    /**
     * Avoid moving the modular items while inventory open
     *
     * @param slotIn
     * @param slotId
     * @param mouseButton
     * @param type
     */
    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if (slotIn != null && slotIn.getHasStack()) {
            if(slotIn.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(handler-> handler instanceof IModularItem).orElse(false))
                return;
        }
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }
}
