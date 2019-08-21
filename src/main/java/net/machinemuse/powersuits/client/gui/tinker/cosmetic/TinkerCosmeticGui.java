package net.machinemuse.powersuits.client.gui.tinker.cosmetic;

import net.machinemuse.numina.client.gui.MuseGui;
import net.machinemuse.powersuits.client.gui.tinker.common.TabSelectFrame;
import net.machinemuse.powersuits.containers.TinkerTableContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class TinkerCosmeticGui extends MuseGui<TinkerTableContainer> {
    protected TabSelectFrame tabSelectFrame;

    public TinkerCosmeticGui(TinkerTableContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }




}