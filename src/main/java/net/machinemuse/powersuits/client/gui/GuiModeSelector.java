package net.machinemuse.powersuits.client.gui;

import net.machinemuse.numina.client.gui.MuseGui;
import net.machinemuse.numina.math.geometry.MusePoint2D;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

public class GuiModeSelector extends MuseGui {
    PlayerEntity player;
    RadialModeSelectionFrame radialSelect;

    public GuiModeSelector(Container container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
    }

//    public GuiModeSelector(PlayerEntity player) {
//        this.player = player;
//        this.xSize = Math.min(minecraft.mainWindow.getScaledWidth() - 50, 500);
//        this.ySize = Math.min(minecraft.mainWindow.getScaledHeight() - 50, 300);
//    }
//
//    /**
//     * Add the buttons (and other controls) to the screen.
//     */
//    @Override
//    public void initGui() {
//        super.initGui();
//        radialSelect = new RadialModeSelectionFrame(
//                new MusePoint2D(absX(-0.5F), absY(-0.5F)),
//                new MusePoint2D(absX(0.5F), absY(0.5F)),
//                player);
//        frames.add(radialSelect);
//    }
//
//    @Override
//    public void drawBackground() {
//
//    }
//
//    @Override
//    public void update() {
//        super.update();
//        if (!minecraft.gameSettings.keyBindsHotbar[player.inventory.currentItem].isKeyDown()) {
//            this.close();
//        }
//    }
}