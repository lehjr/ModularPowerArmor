package net.machinemuse.powersuits.client.gui.tinker.module;

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
    protected DetailedSummaryFrame summaryFrame;
    protected InstallSalvageFrame installFrame;


    protected final PlayerEntity player;
    Container container;
    protected final Colour gridColour = new Colour(0.1F, 0.3F, 0.4F, 0.7F);
    protected final Colour gridBorderColour = Colour.LIGHTBLUE.withAlpha(0.8);
    protected final Colour gridBackGound = Colour.DARKBLUE.withAlpha(0.8);
    ModuleSelectionFrame moduleSelectFrame;
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

        moduleSelectFrame = new ModuleSelectionFrame(containerIn, itemSelectFrame,
                new MusePoint2D(absX(-0.75F), absY(-0.95f)), new MusePoint2D(absX(-0.05F), absY(0.75f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8));
        frames.add(moduleSelectFrame);

        itemSelectFrame.setDoOnNewSelect(doThis-> moduleSelectFrame.loadModules());

        summaryFrame = new DetailedSummaryFrame(player,
                new MusePoint2D(absX(0f), absY(-0.9f)),
                new MusePoint2D(absX(0.95f), absY(-0.3f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8),
                itemSelectFrame);
        frames.add(summaryFrame);

        installFrame = new InstallSalvageFrame(containerIn,
                player,
                new MusePoint2D(absX(-0.75F),
                absY(0.6f)),
                new MusePoint2D(absX(-0.05F),
                absY(0.95f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8),
                gridColour,
                itemSelectFrame,
                moduleSelectFrame);
        frames.add(installFrame);



        /*
         TODO:
          * summary frame needs more detail ... energy not showing
          * install/remove subframe with crafting grid
          * tweak frame/subframe



        modes:
        craft & install
        install (creative or module already in inventory)

        remove
        tweak






         */








//        itemSelectFrame.setDoOnNewSelect(thing->{
//            inventoryFrame.closeSlots();
//            inventoryFrame.loadSlots();
//        });
    }

    public void rescale() {
        this.setXSize(Math.min(minecraft.mainWindow.getScaledWidth(), 320));
        this.setYSize(Math.min(minecraft.mainWindow.getScaledHeight() - 20, 320));
        System.out.println("minecraft.mainWindow.getScaledWidth() - 20: " +  (minecraft.mainWindow.getScaledWidth()));
        System.out.println("xSize: " + xSize);

        System.out.println("minecraft.mainWindow.getScaledHeight() - 20: " + (minecraft.mainWindow.getScaledHeight() - 20));
        System.out.println("ySize: " + ySize);
    }

    @Override
    public void init() {
        rescale();
        super.init();
        backgroundRect.setTargetDimensions(getGuiLeft(), getGuiTop(), getGuiLeft() + getXSize(), getGuiTop() + getYSize());

        itemSelectFrame.init(
                backgroundRect.finalLeft()  + spacer,
                backgroundRect.finalTop() + spacer,
                backgroundRect.finalLeft() + spacer + 36,
                backgroundRect.finalBottom() - spacer - slotHeight - spacer - 3 * slotHeight - spacer); // top of main inventory plus spacer

        // 8 = 1/2 actual slot size of 16x16 because their position is the upper left, not center
        MusePoint2D ulOffset = new MusePoint2D(guiLeft + 8, guiTop + 8);

        hotbar.setUlShift( ulOffset);
        hotbar.init(
                backgroundRect.finalLeft() + spacer, // border plus spacer
                backgroundRect.finalBottom() - spacer - slotHeight, // bottom minus 1 slot
                backgroundRect.finalLeft() + spacer + 9 * slotWidth, // border plus spacer + 9 slots
                backgroundRect.finalBottom() - spacer);

        mainInventory.init(
                backgroundRect.finalLeft() + spacer, // border plus spacer
                backgroundRect.finalBottom() - spacer - slotHeight - spacer - 3 * slotHeight, // hotbar top minus spacer minus 3 slots high
                backgroundRect.finalLeft() + spacer + 9 * slotWidth, // border plus spacer + 9 slots wide
                backgroundRect.finalBottom() - spacer - slotHeight - spacer); // hotbar top minus spacer
        mainInventory.setUlShift(ulOffset);


        moduleSelectFrame.init(
                backgroundRect.finalLeft() + spacer + 36 + spacer, // border plus spacer,
                backgroundRect.finalTop() + spacer, // border top plus spacer
                backgroundRect.finalLeft() + spacer + 9 * slotWidth, // border plus spacer + 9 slots wide
                backgroundRect.finalBottom() - spacer - slotHeight - spacer - 3 * slotHeight - spacer);

        summaryFrame.init(
                backgroundRect.finalLeft() + spacer + 9 * slotWidth + spacer, // border plus spacer + 9 slots wide
                    backgroundRect.finalTop() + spacer,
                backgroundRect.finalRight() -  spacer,
                backgroundRect.finalBottom() - spacer - slotHeight - spacer - 3 * slotHeight - spacer);

        installFrame.init(
                backgroundRect.finalLeft() + spacer + 9 * slotWidth + spacer, // border plus spacer + 9 slots wide
                backgroundRect.finalBottom() - spacer - slotHeight - spacer - 3 * slotHeight,
                backgroundRect.finalRight() -  spacer,
                backgroundRect.finalBottom() - spacer
        );



        /*
            TODO:
                * Crafting grid for "craft and install"
                * craft and install button
                * result should point to appropriate slot in the modular item
                * install button
                * remove button
                * adjust detect and send changes code
*/










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
        // prevent modular items from being moved.
        if (slotIn != null && slotIn.getHasStack()) {
            if(slotIn.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(handler-> handler instanceof IModularItem).orElse(false))
                return;
        }
        // todo: prevent modules from being moved?


        super.handleMouseClick(slotIn, slotId, mouseButton, type);
    }
}
