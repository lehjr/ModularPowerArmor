package net.machinemuse.powersuits.client.gui;

import net.machinemuse.numina.math.geometry.MusePoint2D;
import net.machinemuse.powersuits.containers.ModeChangingContainer;
import net.minecraft.client.MainWindow;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiModeSelector extends MuseGUI2<ModeChangingContainer> {
    PlayerEntity player;
    RadialModeSelectionFrame radialSelect;

    public GuiModeSelector(ModeChangingContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.player = playerInventory.player;
        MainWindow screen = minecraft.mainWindow;
        this.xSize = Math.min(screen.getScaledWidth() - 50, 500);
        this.ySize = Math.min(screen.getScaledHeight() - 50, 300);
    }

    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void init() {
        super.init();
//        radialSelect = new RadialModeSelectionFrame(
//                this.container,
//                new MusePoint2D(absX(-0.5F), absY(-0.5F)),
//                new MusePoint2D(absX(0.5F), absY(0.5F)),
//                player);
//        frames.add(radialSelect);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
//        if (minecraft.gameSettings.keyBindsHotbar[player.inventory.currentItem].matchesKey(keyCode, scanCode)) {
//            super.onClose();
//            container.onContainerClosed(player);
//            return true;
//        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
//        if (!minecraft.isGameFocused()) {
//            super.onClose();
//            container.onContainerClosed(player);
//        }
    }
}