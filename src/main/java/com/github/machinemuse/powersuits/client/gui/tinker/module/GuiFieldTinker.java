package com.github.machinemuse.powersuits.client.gui.tinker.module;

import com.github.lehjr.mpalib.client.gui.ContainerlessGui;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

/**
 * The gui class for the TinkerTable block.
 *
 * @author MachineMuse
 */
public class GuiFieldTinker extends ContainerlessGui {
    protected final EntityPlayerSP player;
    protected ItemSelectionFrame itemSelectFrame;

    /**
     * Constructor. Takes a player as an argument.
     *
     * @param player
     */
    public GuiFieldTinker(EntityPlayer player) {
        this.player = (EntityPlayerSP) player;
//        this.xSize = 256;
//        this.ySize = 200;
//        this.xSize = 400;
//        this.ySize = 244;

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
        itemSelectFrame = new ItemSelectionFrame(new Point2D(absX(-0.95F), absY(-0.95F)), new Point2D(absX(-0.78F), absY(0.95F)),
                Colour.LIGHTBLUE.withAlpha(0.8F), Colour.DARKBLUE.withAlpha(0.8F), player);
        frames.add(itemSelectFrame);

        DetailedSummaryFrame statsFrame = new DetailedSummaryFrame(player,
                new Point2D(absX(0f), absY(-0.9f)),
                new Point2D(absX(0.95f), absY(-0.3f)),
                Colour.LIGHTBLUE.withAlpha(0.8),
                Colour.DARKBLUE.withAlpha(0.8),
                itemSelectFrame);
        frames.add(statsFrame);

        ModuleSelectionFrame moduleSelectFrame = new ModuleSelectionFrame(
                new Point2D(absX(-0.75F), absY(-0.95f)),
                new Point2D(absX(-0.05F), absY(0.95f)),
                Colour.LIGHTBLUE.withAlpha(0.8),
                Colour.DARKBLUE.withAlpha(0.8),
                itemSelectFrame);
        frames.add(moduleSelectFrame);

        ModuleTweakFrame tweakFrame = new ModuleTweakFrame(player,
                new Point2D(absX(0f), absY(-0.25f)),
                new Point2D(absX(0.95f), absY(0.95f)),
                Colour.LIGHTBLUE.withAlpha(0.8),
                Colour.DARKBLUE.withAlpha(0.8),
                itemSelectFrame,
                moduleSelectFrame);
        frames.add(tweakFrame);
    }

    @Override
    public void drawScreen(int x, int y, float z) {
        super.drawScreen(x, y, z);
        if (itemSelectFrame.hasNoItems()) {
            double centerx = absX(0);
            double centery = absY(0);
            Renderer.drawCenteredString(I18n.format("gui.powersuits.noModulesFound.line1"), centerx, centery - 5);
            Renderer.drawCenteredString(I18n.format("gui.powersuits.noModulesFound.line2"), centerx, centery + 5);
        }
    }
}
