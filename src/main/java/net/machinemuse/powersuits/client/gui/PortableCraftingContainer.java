package net.machinemuse.powersuits.client.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;

public class PortableCraftingContainer extends WorkbenchContainer {
    public PortableCraftingContainer(int p_i50089_1_, PlayerInventory playerInventory) {
        super(p_i50089_1_, playerInventory, IWorldPosCallable.DUMMY);
    }

    public PortableCraftingContainer(int p_i50090_1_, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(p_i50090_1_, playerInventory, worldPosCallable);
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }
}