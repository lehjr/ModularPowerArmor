//package net.machinemuse.powersuits.client.gui.tinker.module;
//
//import net.machinemuse.numina.client.gui.MuseGui;
//import net.machinemuse.numina.client.gui.geometry.DrawableMuseRect;
//import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
//import net.machinemuse.numina.client.render.MuseRenderer;
//import net.machinemuse.numina.math.Colour;
//import net.machinemuse.powersuits.client.gui.tinker.common.ItemSelectionFrame;
//import net.machinemuse.powersuits.client.gui.tinker.common.ModuleSelectionFrame;
//import net.machinemuse.powersuits.client.gui.tinker.common.TabSelectFrame;
//import net.machinemuse.powersuits.containers.TinkerTableContainer;
//import net.minecraft.client.resources.I18n;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.util.text.ITextComponent;
//
// * This will be the most complex GUI, requiring a super complex container.
// */
//public class TinkerModuleGui extends MuseGui<TinkerTableContainer> {
//    protected TabSelectFrame tabSelectFrame;
//    protected final PlayerEntity player;
//    protected DrawableMuseRect backgroundRect;
//    protected ItemSelectionFrame itemSelectFrame;
//    DetailedSummaryFrame statsFrame;
//    ModuleSelectionFrame moduleSelectFrame;
//    InstallSalvageFrame installFrame;
//
//    public TinkerModuleGui(TinkerTableContainer container, PlayerInventory playerInventory, ITextComponent title) {
//        super(container, playerInventory, title);
//        this.player = playerInventory.player;
//        this.xSize = Math.min(minecraft.mainWindow.getScaledWidth() - 50, 500);
//        this.ySize = Math.min(minecraft.mainWindow.getScaledHeight() - 50, 300);
//
//        backgroundRect = new DrawableMuseRect(absX(-1), absY(-1), absX(1), absY(1), true,
//                new Colour(0.0F, 0.2F, 0.0F, 0.8F),
//                new Colour(0.1F, 0.9F, 0.1F, 0.8F));
//
////        itemSelectFrame = new ItemSelectionFrame(container, new MusePoint2D(absX(-0.95F), absY(-0.95F)), new MusePoint2D(absX(-0.78F), absY(0.95F)),
////                Colour.DARKBLUE.withAlpha(0.8F),
////                Colour.LIGHTBLUE.withAlpha(0.8F), player);
////        frames.add(itemSelectFrame);
//
//        statsFrame = new DetailedSummaryFrame(player,
//                new MusePoint2D(absX(0f), absY(-0.9f)),
//                new MusePoint2D(absX(0.95f), absY(-0.3f)),
//                Colour.DARKBLUE.withAlpha(0.8),
//                Colour.LIGHTBLUE.withAlpha(0.8),
//                itemSelectFrame);
//        frames.add(statsFrame);
//
//        moduleSelectFrame = new ModuleSelectionFrame(container,
//                new MusePoint2D(absX(-0.75F), absY(-0.95f)), new MusePoint2D(absX(-0.05F), absY(0.55f)),
//                Colour.DARKBLUE.withAlpha(0.8),
//                Colour.LIGHTBLUE.withAlpha(0.8), itemSelectFrame);
//        frames.add(moduleSelectFrame);
//
//        installFrame = new InstallSalvageFrame(player,
//                new MusePoint2D(absX(-0.75F), absY(0.6f)), new MusePoint2D(absX(-0.05F), absY(0.95f)),
//                Colour.DARKBLUE.withAlpha(0.8),
//                Colour.LIGHTBLUE.withAlpha(0.8),
//                itemSelectFrame, moduleSelectFrame);
//        frames.add(installFrame);
//
//
//
////        ModuleTweakFrame tweakFrame = new ModuleTweakFrame(player,
////                new MusePoint2D(absX(0f), absY(-0.25f)),
////                new MusePoint2D(absX(0.95f), absY(0.95f)),
////                Colour.LIGHTBLUE.withAlpha(0.8),
////                Colour.DARKBLUE.withAlpha(0.8),
////                itemSelectFrame,
////                moduleSelectFrame);
////        frames.add(tweakFrame);
////
////        TabSelectFrame tabFrame = new TabSelectFrame(player, new MusePoint2D(absX(-0.95F), absY(-1.05f)), new MusePoint2D(absX(0.95F), absY(-0.95f)), worldx, worldy, worldz);
////        frames.add(tabFrame);
//
//    }
//
//    @Override
//    public void init() {
//        super.init();
////        minecraft.keyboardListener.enableRepeatEvents(true);
//        backgroundRect.setTargetDimensions(absX(-1), absY(-1), absX(1), absY(1));
//        itemSelectFrame.init(absX(-0.95F), absY(-0.95F), absX(-0.78F), absY(0.95F));
//        statsFrame.init(absX(0f), absY(-0.9f), absX(0.95f), absY(-0.3f));
//        moduleSelectFrame.init(absX(-0.75F), absY(-0.95f), absX(-0.05F), absY(0.55f));
//        installFrame.init(absX(-0.75F), absY(0.6f), absX(-0.05F), absY(0.95f));
//
//
//
//
//
//    }
//
//
//
//
//    /**
//     * Draws the gradient-rectangle background you see in the TinkerTable gui.
//     */
//    @Override
//    public void drawRectangularBackground() {
//        backgroundRect.draw(); // The giant green window rectangle
//    }
//
//
//    @Override
//    public void render(int mouseX, int mouseY, float partialTicks) {
//        super.render(mouseX, mouseY, partialTicks);
//        if (itemSelectFrame.hasNoItems()) {
//            double centerx = absX(0);
//            double centery = absY(0);
//            MuseRenderer.drawCenteredString(I18n.format("gui.powersuits.noModulesFound.line1"), centerx, centery - 5);
//            MuseRenderer.drawCenteredString(I18n.format("gui.powersuits.noModulesFound.line2"), centerx, centery + 5);
//        }
//    }
//}