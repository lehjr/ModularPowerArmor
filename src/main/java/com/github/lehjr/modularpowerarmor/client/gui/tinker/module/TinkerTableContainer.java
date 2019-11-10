package com.github.lehjr.modularpowerarmor.client.gui.tinker.module;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

import java.util.List;
import java.util.Map;

public class TinkerTableContainer extends ContainerWorkbench implements IModularItemToSlotMapProvider {
        public TinkerTableContainer(InventoryPlayer inventoryPlayer, World world, BlockPos pos) {
        super(inventoryPlayer, world, pos);
    }

    // A map of the slot that holds the modular item, and the set of slots in that modular item
    private Map<Integer, List<SlotItemHandler>> modularItemToSlotMap;


    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    @Override
    public Map<Integer, List<SlotItemHandler>> getModularItemToSlotMap() {
        return modularItemToSlotMap;
    }

    @Override
    public Container getContainer() {
        return this;
    }
}