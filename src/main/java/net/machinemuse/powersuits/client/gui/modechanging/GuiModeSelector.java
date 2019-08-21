package net.machinemuse.powersuits.client.gui.modechanging;

import net.machinemuse.numina.client.gui.MuseContainerlessGui;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.powersuits.client.gui.modechanging.RadialModeSelectionFrame;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;

public class GuiModeSelector extends MuseContainerlessGui {
    PlayerEntity player;
    RadialModeSelectionFrame radialSelect;

    public GuiModeSelector(PlayerEntity player, ITextComponent titleIn) {
        super(titleIn);
        Minecraft.getInstance().keyboardListener.enableRepeatEvents(true);
        this.player = player;
        MainWindow screen = Minecraft.getInstance().mainWindow;
        this.xSize = Math.min(screen.getScaledWidth() - 50, 500);
        this.ySize = Math.min(screen.getScaledHeight() - 50, 300);



        radialSelect = new RadialModeSelectionFrame(
                new MusePoint2D(absX(-0.5F), absY(-0.5F)),
                new MusePoint2D(absX(0.5F), absY(0.5F)),
                player);
        frames.add(radialSelect);
    }

    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void init() {
        super.init();
        radialSelect.init(absX(-0.5F), absY(-0.5F), absX(0.5F), absY(0.5F));
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (minecraft.gameSettings.keyBindsHotbar[player.inventory.currentItem].matchesKey(keyCode, scanCode)) {
            this.player.closeScreen();
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!minecraft.isGameFocused()) {
            this.player.closeScreen();
//            super.onClose();
//            container.onContainerClosed(player);
        }
    }


//
//    @Override
//    public void update() {
//        super.update();
//        if (!Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindsHotbar[player.inventory.currentItem].getKeyCode())) {
//            //close animation
//            //TODO
//            //close Gui
//            try {
//                keyTyped('1', 1);
//            } catch (IOException e) {
//            }
//        }
//    }
}