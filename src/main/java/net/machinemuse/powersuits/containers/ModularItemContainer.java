package net.machinemuse.powersuits.containers;


import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.machinemuse.numina.client.gui.clickable.ClickableModularItem;
import net.machinemuse.numina.client.gui.slot.ClickableModuleSlot;
import net.machinemuse.numina.client.gui.slot.UniversalSlot;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.powersuits.basemod.MPSObjects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common container for all Modular Items.
 * Handles all modular items in player's inventory at once
 * Used for installing and removing modules
 */
public class ModularItemContainer extends Container implements IModularItemToSlotMapProvider {
    // A map of the slot that holds the modular item, and the set of slots in that modular item
    private Map<ClickableItem, List<Integer>> modularItemToSlotMap;

    private final CraftingInventory craftingInventory;

    public ModularItemContainer(int windowId, PlayerInventory playerInventory) {
        this(windowId, playerInventory, IWorldPosCallable.DUMMY);
    }

    /**
     *
            FIXME!!!
            need a way to find the source and target slot here

            craft and install:
            ------------------
            no source slot .. remove crafting ingredients
            target slot:
                    dependent on target modular item (certain categories have limited slots)
                    loop through and find acceptable inventory

            install (non-creative)
            --------------------
            source slot = first available slot with item to install
            target slot:
                 dependent on target modular item (certain categories have limited slots)
                 loop through and find acceptable inventory

            install (creative)
            --------------------
             source slot = none. Create new item to install
             target slot:
                 dependent on target modular item (certain categories have limited slots)
                 loop through and find acceptable inventory

            remove
            ----------------------
            source slot = slot of item to remove
            target slot:
                free spot in player inventory









            most likely way would be to add change the map to the



     */









    public ModularItemContainer(int windowId, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(MPSObjects.MODULAR_ITEM_CONTAINER_CONTAINER_TYPE, windowId);
        modularItemToSlotMap = new HashMap<>();
        this.craftingInventory = new CraftingInventory(this, 3, 3);


        //hotbar, slots 0-8
        for(int index = 0; index < 9; ++index) {
            this.addSlot(new UniversalSlot(playerInventory, index, 34 + index * 18, 163));
        }

        // main invenentory, slots 9-35
        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 9; ++col) {
                this.addSlot(new UniversalSlot(playerInventory, col + row * 9 + 9, 34 + col * 18, 83 + row * 18));
            }

        }

        // All inventory. creates a modularItem to modularItemInventorySlot map. Note does create duplicates but unavoidable.
        for (int index = 0; index < playerInventory.getSizeInventory(); index++) {

            // look for modular items and get all the modules from them.
            int finalIndex = index;
            playerInventory.getStackInSlot(index).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(itemHandler -> {
                if (itemHandler instanceof IModularItem) {
                    // add the slot to the container
                    ClickableItem  modularItemSlot = new ClickableItem(playerInventory.getStackInSlot(finalIndex), new MusePoint2D(0, 0), finalIndex);

                    List<Integer> indexList = new ArrayList<>();

                    for (int handlerIndex = 0; handlerIndex < itemHandler.getSlots(); handlerIndex ++) {
//                        moduleSet.add((ClickableModuleSlot)

                        ClickableModuleSlot slot = new ClickableModuleSlot(itemHandler, handlerIndex, -1000, -1000);
                        slot.setEnabled(false);
                        slot.setVisible(false);
                        slot.move(-1000, -1000);

                        this.addSlot(slot);
                        indexList.add(this.inventorySlots.size() -1); // track the index rather than the slot itself.
                    }

                    modularItemToSlotMap.put(modularItemSlot, indexList);
                }
            });
        }

        int row;
        int col;
        // crafting inventory: slot 1-9
        for(row = 0; row < 3; ++row) {
            for(col = 0; col < 3; ++col) {
                this.addSlot(new UniversalSlot(this.craftingInventory, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }
    }

    @Override
    public Map<ClickableItem, List<Integer>> getModularItemToSlotMap(){
        return modularItemToSlotMap;
    }

    @Override
    public Container getContainer() {
        return this;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public boolean canDragIntoSlot(Slot slotIn) {
//        return true;

        return !(slotIn instanceof ClickableModularItem);
    }

    /**
     *  FIXME: inserts into other modular items...
     * @param player
     * @param index
     * @return
     */

    @Override
    public ItemStack transferStackInSlot(final PlayerEntity player, final int index) {
        System.out.println("transfer stack from slot #: " + index);

        final Slot from = inventorySlots.get(index);
        if (from == null) {
            return ItemStack.EMPTY;
        }
        final ItemStack stack = from.getStack().copy();
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final boolean intoPlayerInventory = from.inventory != player.inventory;

        System.out.println("intoPlayerInventory: + " + intoPlayerInventory);

        final ItemStack fromStack = from.getStack();

        final int step, begin;
        if (intoPlayerInventory) {
            step = -1;
            begin = inventorySlots.size() - 1;
        } else {
            step = 1;
            begin = 0;
        }

        // Source
        if (fromStack.getMaxStackSize() > 1) {
            for (int i = begin; i >= 0 && i < inventorySlots.size(); i += step) {
                final Slot into = inventorySlots.get(i);
                if (into.inventory == from.inventory) {
                    continue;
                }

                final ItemStack intoStack = into.getStack();
                if (intoStack.isEmpty()) {
                    continue;
                }

                final boolean itemsAreEqual = fromStack.isItemEqual(intoStack) && ItemStack.areItemStackTagsEqual(fromStack, intoStack);
                if (!itemsAreEqual) {
                    continue;
                }

                final int maxSizeInSlot = Math.min(fromStack.getMaxStackSize(), into.getItemStackLimit(stack));
                final int spaceInSlot = maxSizeInSlot - intoStack.getCount();
                if (spaceInSlot <= 0) {
                    continue;
                }

                final int itemsMoved = Math.min(spaceInSlot, fromStack.getCount());
                if (itemsMoved <= 0) {
                    continue;
                }

                intoStack.grow(from.decrStackSize(itemsMoved).getCount());
                into.onSlotChanged();

                if (from.getStack().isEmpty()) {
                    break;
                }
            }
        }
        // Target
        for (int i = begin; i >= 0 && i < inventorySlots.size(); i += step) {
            System.out.println("checking slot: + " + i);

            if (from.getStack().isEmpty()) {
                break;
            }

            final Slot into = inventorySlots.get(i);

            if (into.inventory == from.inventory) {
                continue;
            }

            if (into.getHasStack()) {
                continue;
            }

            if (!into.isItemValid(fromStack)) {
                continue;
            }

            // prevent sending module into modular item not selected
            if (into instanceof ClickableModuleSlot) {
                if (!into.isEnabled() || !((ClickableModuleSlot) into).isVisible())
                    continue;
            }

            System.out.println(" into: " + i);

            final int maxSizeInSlot = Math.min(fromStack.getMaxStackSize(), into.getItemStackLimit(fromStack));
            final int itemsMoved = Math.min(maxSizeInSlot, fromStack.getCount());
            into.putStack(from.decrStackSize(itemsMoved));
        }




        return from.getStack().getCount() < stack.getCount() ? from.getStack() : ItemStack.EMPTY;
    }

    @Override
    public void putStackInSlot(int slotID, ItemStack stack) {
        super.putStackInSlot(slotID, stack);
    }
}
