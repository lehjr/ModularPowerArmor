//package net.machinemuse.powersuits.client.gui.tinker;
//
//import net.machinemuse.numina.client.gui.MuseGui;
//import net.machinemuse.numina.math.Colour;
//import net.machinemuse.numina.math.geometry.MusePoint2D;
//import net.machinemuse.numina.math.geometry.MuseRect;
//import net.machinemuse.powersuits.basemod.MPSConfig;
//import net.machinemuse.powersuits.client.gui.tinker.frame.*;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.inventory.container.Container;
//import net.minecraft.inventory.container.PlayerContainer;
//import net.minecraft.server.management.OpEntry;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.text.ITextComponent;
//
///**
// * Author: MachineMuse (Claire Semple)
// * Created: 6:32 PM, 29/04/13
// * <p>
// * Ported to Java by lehjr on 10/19/16.
// */
//public class CosmeticGui extends MuseGui<PlayerContainer>  {
//    PlayerEntity player;
//    int worldx;
//    int worldy;
//    int worldz;
//    ItemSelectionFrame itemSelect;
//    LoadSaveResetSubFrame loadSaveResetSubFrame;
//
//    protected final boolean allowCosmeticPresetCreation;
//    protected final boolean usingCosmeticPresets;
//
//    public CosmeticGui(PlayerContainer container, PlayerInventory playerInventory, ITextComponent title) {
//        super(container, playerInventory, title);
//        this.player = playerInventory.player;
//        BlockPos pos = player.getPosition();
//        this.worldx = pos.getX();
//        this.worldy = pos.getY();
//        this.worldz = pos.getZ();
//        this.xSize = Math.min(minecraft.mainWindow.getScaledWidth() - 50, 500);
//        this.ySize = Math.min(minecraft.mainWindow.getScaledHeight() - 50, 300);
//
//        usingCosmeticPresets = !MPSConfig.INSTANCE.COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get();
//        if (usingCosmeticPresets) {
//            // check if player is the server owner
//            if (minecraft.isSingleplayer()) {
//                allowCosmeticPresetCreation = player.getName().equals(minecraft.getIntegratedServer().getServerOwner());
//            } else {
//                // check if player is top level op
//                OpEntry opEntry = minecraft.player.getServer().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
//                int opLevel = opEntry != null ? opEntry.getPermissionLevel() : 0;
//                allowCosmeticPresetCreation = opLevel == 4;
//            }
//        } else
//            allowCosmeticPresetCreation = false;
//    }
//
//
//    /**
//     * Add the buttons (and other controls) to the screen.
//     */
//    @Override
//    protected void init() {
//        super.init();
//
//        itemSelect = new ItemSelectionFrame(
//                new MusePoint2D(absX(-0.95F), absY(-0.95F)),
//                new MusePoint2D(absX(-0.78F), absY(-0.025F)),
//                Colour.LIGHTBLUE.withAlpha(0.8F),
//                Colour.DARKBLUE.withAlpha(0.8F), player);
//        frames.add(itemSelect);
//
////        PlayerModelViewFrame renderframe = new PlayerModelViewFrame(
////                itemSelect,
////                new MusePoint2D(absX(-0.75F), absY(-0.95f)),
////                new MusePoint2D(absX(0.15F), absY(-0.025f)),
////                Colour.LIGHTBLUE.withAlpha(0.8F),
////                Colour.DARKBLUE.withAlpha(0.8F));
////        frames.add(renderframe);
////
////        ColourPickerFrame colourpicker = new ColourPickerFrame(
////                new MusePoint2D(absX(0.18f),
////                        absY(-0.95f)),
////
////                new MusePoint2D(absX(0.95f),
////                        absY(-0.27f)),
////                Colour.LIGHTBLUE.withAlpha(0.8F),
////                Colour.DARKBLUE.withAlpha(0.8F),
////                itemSelect);
////        frames.add(colourpicker);
////
////        PartManipContainer partframe = new PartManipContainer(
////                itemSelect, colourpicker,
////                new MusePoint2D(absX(-0.95F), absY(0.025f)),
////                new MusePoint2D(absX(+0.95F), absY(0.95f)),
////                Colour.LIGHTBLUE.withAlpha(0.8F),
////                Colour.DARKBLUE.withAlpha(0.8F));
////        frames.add(partframe);
////
////        CosmeticPresetContainer cosmeticFrame = new CosmeticPresetContainer(
////                itemSelect, colourpicker,
////                new MusePoint2D(absX(-0.95F), absY(0.025f)),
////                new MusePoint2D(absX(+0.95F), absY(0.95f)),
////                Colour.LIGHTBLUE.withAlpha(0.8F),
////                Colour.DARKBLUE.withAlpha(0.8F));
////        frames.add(cosmeticFrame);
////
////        // if not using presets then only the reset button is displayed
////        loadSaveResetSubFrame = new LoadSaveResetSubFrame(
////                colourpicker,
////                player,
////                new MuseRect(
////                        absX(0.18f),
////                        absY(-0.23f),
////                        absX(0.95f),
////                        absY(-0.025f)),
////                Colour.LIGHTBLUE.withAlpha(0.8F),
////                Colour.DARKBLUE.withAlpha(0.8F),
////                itemSelect,
////                usingCosmeticPresets,
////                allowCosmeticPresetCreation,
////                partframe,
////                cosmeticFrame);
////        frames.add(loadSaveResetSubFrame);
////
////        TabSelectFrame tabFrame = new TabSelectFrame(
////                player,
////                new MusePoint2D(absX(-0.95F), absY(-1.05f)),
////                new MusePoint2D(absX(0.95F), absY(-0.95f)),
////                worldx, worldy, worldz);
////        frames.add(tabFrame);
//    }
//
//    @Override
//    public void onClose() {
//        super.onClose();
//        loadSaveResetSubFrame.onClose();
//    }
//    // FIXME!!! I have no idea what this return value should be based on... 0 documentation
//    @Override
//    public boolean charTyped(char typedChar, int keyCode) {
//        boolean ret = super.charTyped(typedChar, keyCode);
//        if (loadSaveResetSubFrame != null)
//            loadSaveResetSubFrame.charTyped(typedChar, keyCode);
//        return ret;
//    }
//}