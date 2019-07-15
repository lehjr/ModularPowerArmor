package net.machinemuse.powersuits.client.gui;

import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.math.geometry.DrawableMuseRect;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;


/**
 * Base class for all Tinker Table GUI's
 */
public class TinkerGUIBase extends MuseGUI2 {

    protected DrawableMuseRect backgroundRect;
    public TinkerGUIBase(Container container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    public void init() {
        super.init();
//        // This will resize the background rectangle any time
//        backgroundRect = new DrawableMuseRect(absX(-1), absY(-1), absX(1), absY(1),
//                true, new Colour(0.1F, 0.9F, 0.1F, 0.8F), new Colour(0.0F, 0.2F,
//                0.0F, 0.8F));
    }

    @Override
    public void renderBackground() {
        super.renderBackground();
        backgroundRect.draw(); // The giant green window rectangle
    }
}
