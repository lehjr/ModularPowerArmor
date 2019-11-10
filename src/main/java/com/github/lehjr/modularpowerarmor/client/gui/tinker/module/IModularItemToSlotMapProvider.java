package com.github.lehjr.modularpowerarmor.client.gui.tinker.module;

import net.minecraft.inventory.Container;
import net.minecraftforge.items.SlotItemHandler;

import java.util.List;
import java.util.Map;

public interface IModularItemToSlotMapProvider<T extends Container> {
    Map<Integer, List<SlotItemHandler>> getModularItemToSlotMap();

    T getContainer();
}
