package net.machinemuse.powersuits.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;

public class PortableCraftingContainer extends WorkbenchContainer {
    public PortableCraftingContainer(int id, PlayerInventory playerInventory) {
        super(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public PortableCraftingContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(id, playerInventory, worldPosCallable);
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }
}