package net.machinemuse.powersuits.client.gui.keybind;

import net.machinemuse.numina.client.gui.MuseContainerlessGui;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseRect;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.client.control.KeybindManager;
import net.machinemuse.powersuits.client.gui.common.TabSelectFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;

public class TinkerKeybindGui extends MuseContainerlessGui {
    protected KeybindConfigFrame kbFrame;
    protected DrawableMuseRect backgroundRect;
    private PlayerEntity player;
    TabSelectFrame tabSelectFrame;

    public TinkerKeybindGui(PlayerInventory playerInventory, ITextComponent title) {
        super(title);
        KeybindManager.readInKeybinds();
        this.player = playerInventory.player;
        this.minecraft = Minecraft.getInstance();
        rescale();
        backgroundRect = new DrawableMuseRect(absX(-1), absY(-1), absX(1), absY(1), true,
                new Colour(0.0F, 0.2F, 0.0F, 0.8F),
                new Colour(0.1F, 0.9F, 0.1F, 0.8F));

        kbFrame = new KeybindConfigFrame(backgroundRect, player);
        addFrame(kbFrame);

        tabSelectFrame = new TabSelectFrame(player, 1);
        addFrame(tabSelectFrame);
    }

    public void rescale() {
        this.setXSize((Math.min(minecraft.mainWindow.getScaledWidth()- 50, 500)));
        this.setYSize((Math.min(minecraft.mainWindow.getScaledHeight() - 50, 300)));
    }

    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void init() {
        super.init();
        rescale();
        backgroundRect.setTargetDimensions(getGuiLeft(), getGuiTop(), getGuiLeft() + getXSize(), getGuiTop() + getYSize());
        kbFrame.init(absX(-0.95), absY(-0.95), absX(0.95), absY(0.95));
        tabSelectFrame.init(getGuiLeft(), getGuiTop(), getGuiLeft() + getXSize(), getGuiTop() + getYSize());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_) {
//        System.out.println("keyCode: " + keyCode);
//        System.out.println("scanCode: " + scanCode);
//        System.out.println("p_keyPressed_3_: "+ p_keyPressed_3_);
        InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || this.minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey)) {
            this.minecraft.player.closeScreen();
            return true; // Forge MC-146650: Needs to return true when the key is handled.
        }

        if ( kbFrame.keyPressed(keyCode, scanCode, p_keyPressed_3_)) {
            return true;
        }

        if (super.keyPressed(keyCode, scanCode, p_keyPressed_3_)) {
            return true;
        }
        return false;
    }

    @Override
    public void renderBackground() {
        super.renderBackground();
        this.backgroundRect.draw();
    }

    @Override
    public void onClose() {
        super.onClose();
        KeybindManager.writeOutKeybinds();
    }
}