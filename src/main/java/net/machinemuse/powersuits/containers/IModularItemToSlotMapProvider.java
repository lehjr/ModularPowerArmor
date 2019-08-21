package net.machinemuse.powersuits.containers;

import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.minecraft.inventory.container.Container;

import java.util.List;
import java.util.Map;

public interface IModularItemToSlotMapProvider<T extends Container> {
    Map<ClickableItem, List<Integer>> getModularItemToSlotMap();

    T getContainer();
}
