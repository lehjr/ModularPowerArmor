package net.machinemuse.powersuits.client.gui.tinker;

import net.machinemuse.powersuits.client.gui.MuseGUI2;
import net.machinemuse.powersuits.client.gui.tinker.frame.ItemSelectionFrame;
import net.machinemuse.powersuits.containers.TinkerTableContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

/**
 * The gui class for the TinkerTable block.
 *
 * @author MachineMuse
 */
public class GuiTinkerTable extends MuseGUI2<TinkerTableContainer> {
    protected final PlayerEntity player;
    protected ItemSelectionFrame itemSelectFrame;

    public GuiTinkerTable(TinkerTableContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.player = playerInventory.player;
        this.xSize = Math.min(minecraft.mainWindow.getScaledWidth() - 50, 500);
        this.ySize = Math.min(minecraft.mainWindow.getScaledHeight() - 50, 300);
    }

    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        super.resize(p_resize_1_, p_resize_2_, p_resize_3_);
    }

    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void init() {
        super.init();
        /**
         * each frame will need it need its own container and container type
         *
         */



//        itemSelectFrame = new ItemSelectionFrame(container, new MusePoint2D(absX(-0.95F), absY(-0.95F)), new MusePoint2D(absX(-0.78F), absY(0.95F)),
//                Colour.LIGHTBLUE.withAlpha(0.8F), Colour.DARKBLUE.withAlpha(0.8F), player);
//        frames.add(itemSelectFrame);
//
//        DetailedSummaryFrame statsFrame = new DetailedSummaryFrame(player,
//                new MusePoint2D(absX(0f), absY(-0.9f)),
//                new MusePoint2D(absX(0.95f), absY(-0.3f)),
//                Colour.LIGHTBLUE.withAlpha(0.8), Colour.DARKBLUE.withAlpha(0.8), itemSelectFrame);
//        frames.add(statsFrame);
//
//        ModuleSelectionFrame moduleSelectFrame = new ModuleSelectionFrame(container, new MusePoint2D(absX(-0.75F), absY(-0.95f)), new MusePoint2D(absX(-0.05F),
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