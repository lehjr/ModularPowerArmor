package net.machinemuse.powersuits.client.gui.modeselection;

import com.github.lehjr.mpalib.client.gui.ContainerlessGui;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiModeSelector extends ContainerlessGui {
    EntityPlayer player;
    RadialModeSelectionFrame radialSelect;

    public GuiModeSelector(EntityPlayer player) {
        this.player = player;
        ScaledResolution screen = new ScaledResolution(Minecraft.getMinecraft());
        this.xSize = Math.min(screen.getScaledWidth() - 50, 500);
        this.ySize = Math.min(screen.getScaledHeight() - 50, 300);
    }

    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void initGui() {
        super.initGui();
        radialSelect = new RadialModeSelectionFrame(
                new Point2D(absX(-0.5F), absY(-0.5F)),
                new Point2D(absX(0.5F), absY(0.5F)),
                player);
        frames.add(radialSelect);
    }

    @Override
    public void drawBackground() {

    }

    @Override
    public void update(double x, double y) {
        super.update(x, y);
        if (!Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindsHotbar[player.inventory.currentItem].getKeyCode())) {
            //close animation
            //TODO
            //close Gui
            try {
                keyTyped('1', 1);
            } catch (IOException e) {
            }
        }
    }
}