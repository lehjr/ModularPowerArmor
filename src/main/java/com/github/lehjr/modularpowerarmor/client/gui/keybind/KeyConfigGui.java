package com.github.lehjr.modularpowerarmor.client.gui.keybind;

import com.github.lehjr.mpalib.client.gui.ContainerlessGui;
import com.github.lehjr.modularpowerarmor.client.control.KeybindManager;
import com.github.lehjr.modularpowerarmor.client.gui.common.TabSelectFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class KeyConfigGui extends ContainerlessGui {
    protected KeybindConfigFrame keybindConfigFrame;
    protected TabSelectFrame tabFrame;
    protected int worldx;
    protected int worldy;
    protected int worldz;

    private EntityPlayer player;

    public KeyConfigGui(EntityPlayer player, int x, int y, int z) {
        super();
        KeybindManager.readInKeybinds();
        this.player = player;
        ScaledResolution screen = new ScaledResolution(Minecraft.getMinecraft());
        this.xSize = screen.getScaledWidth() - 50;
        this.ySize = screen.getScaledHeight() - 50;

        this.worldx = x;
        this.worldy = y;
        this.worldz = z;


        keybindConfigFrame = new KeybindConfigFrame(this, player);
        frames.add(keybindConfigFrame);

        tabFrame = new TabSelectFrame(player, 1, worldx, worldy, worldz);
        frames.add(tabFrame);
    }

    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void initGui() {
        super.initGui();
        keybindConfigFrame.init(absX(-0.95), absY(-0.95), absX(0.95), absY(0.95));
        tabFrame.init(absX(-0.95F), absY(-1.05f), absX(0.95F), absY(-0.95f));
    }

    @Override
    public void handleKeyboardInput() {
        try {
            super.handleKeyboardInput();
            keybindConfigFrame.handleKeyboard();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        KeybindManager.writeOutKeybinds();
    }
}