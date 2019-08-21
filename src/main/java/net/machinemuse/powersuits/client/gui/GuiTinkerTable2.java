package net.machinemuse.powersuits.client.gui;

import net.machinemuse.numina.client.gui.MuseContainerlessGui;
import net.machinemuse.powersuits.client.gui.tinker.common.ItemSelectionFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class GuiTinkerTable2 extends MuseContainerlessGui {
    protected final ClientPlayerEntity player;
    protected ItemSelectionFrame itemSelectFrame;

    /**
     * Constructor. Takes a player as an argument.
     *
     * @param player
     */
    public GuiTinkerTable2(PlayerEntity player) {
        super(new StringTextComponent("TinkerTable"));
        this.player = (ClientPlayerEntity) player;
        this.minecraft = Minecraft.getInstance();
        this.xSize = Math.min(minecraft.mainWindow.getScaledWidth() - 50, 500);
        this.ySize = Math.min(minecraft.mainWindow.getScaledHeight() - 50, 300);
    }

    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void init() {
        super.init();
//        itemSelectFrame = new ItemSelectionFrame(new MusePoint2D(absX(-0.95F), absY(-0.95F)), new MusePoint2D(absX(-0.78F), absY(0.95F)),
//                Colour.LIGHTBLUE.withAlpha(0.8F), Colour.DARKBLUE.withAlpha(0.8F), player);
//        frames.add(itemSelectFrame);

//        DetailedSummaryFrame statsFrame = new DetailedSummaryFrame(player,
//                new MusePoint2D(absX(0f), absY(-0.9f)),
//                new MusePoint2D(absX(0.95f), absY(-0.3f)),
//                Colour.LIGHTBLUE.withAlpha(0.8), Colour.DARKBLUE.withAlpha(0.8), itemSelectFrame);
//        frames.add(statsFrame);
//
//        ModuleSelectionFrame moduleSelectFrame = new ModuleSelectionFrame(new MusePoint2D(absX(-0.75F), absY(-0.95f)), new MusePoint2D(absX(-0.05F),
//                absY(0.55f)), Colour.LIGHTBLUE.withAlpha(0.8), Colour.DARKBLUE.withAlpha(0.8), itemSelectFrame);
//        frames.add(moduleSelectFrame);
//
//        InstallSalvageFrame installFrame = new InstallSalvageFrame(player, new MusePoint2D(absX(-0.75F), absY(0.6f)), new MusePoint2D(absX(-0.05F),
//                absY(0.95f)), Colour.LIGHTBLUE.withAlpha(0.8), Colour.DARKBLUE.withAlpha(0.8), itemSelectFrame, moduleSelectFrame);
//        frames.add(installFrame);
//
//        ModuleTweakFrame tweakFrame = new ModuleTweakFrame(player,
//                new MusePoint2D(absX(0f), absY(-0.25f)),
//                new MusePoint2D(absX(0.95f), absY(0.95f)),
//                Colour.LIGHTBLUE.withAlpha(0.8),
//                Colour.DARKBLUE.withAlpha(0.8),
//                itemSelectFrame,
//                moduleSelectFrame);
//        frames.add(tweakFrame);
//
//        TabSelectFrame tabFrame = new TabSelectFrame(player, new MusePoint2D(absX(-0.95F), absY(-1.05f)), new MusePoint2D(absX(0.95F), absY(-0.95f)), worldx, worldy, worldz);
//        frames.add(tabFrame);
    }

    /**
     * Draws the gradient-rectangle background you see in the TinkerTable gui.
     */
    @Override
    public void drawRectangularBackground() {
//        backgroundRect.draw(); // The giant green window rectangle
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
//        if (itemSelectFrame.hasNoItems()) {
//            double centerx = absX(0);
//            double centery = absY(0);
//            MuseRenderer.drawCenteredString(I18n.format("gui.powersuits.noModulesFound.line1"), centerx, centery - 5);
//            MuseRenderer.drawCenteredString(I18n.format("gui.powersuits.noModulesFound.line2"), centerx, centery + 5);
//        }
    }
}
