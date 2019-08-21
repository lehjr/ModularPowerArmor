package net.machinemuse.powersuits.client.gui.tinker.keybind;

import net.machinemuse.numina.client.gui.MuseGui;
import net.machinemuse.powersuits.client.gui.tinker.common.TabSelectFrame;
import net.machinemuse.powersuits.containers.TinkerTableContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class TinkerKeybindGui extends MuseGui<TinkerTableContainer> {
    protected TabSelectFrame tabSelectFrame;

    public TinkerKeybindGui(TinkerTableContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }






}
