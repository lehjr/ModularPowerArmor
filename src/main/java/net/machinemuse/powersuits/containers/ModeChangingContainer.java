package net.machinemuse.powersuits.containers;

import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.powersuits.basemod.MPSObjects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * This weird container abomination is just for the mode changing item handling to select the active module
 * The item slots are spawned in the center and then should spiral outward with the help of the associated container screen
 */
public class ModeChangingContainer extends Container {
    final Hand hand;
    final PlayerInventory playerInventory;
    PlayerEntity player;
    int xCenter;
    int yCenter;

    public ModeChangingContainer(int windowId, PlayerInventory playerInventory, Hand handIn) {
        super(MPSObjects.INSTANCE.MODE_CHANGING_CONTAINER_CONTAINER_TYPE, windowId);
        this.playerInventory = playerInventory;
        this.hand = handIn;
        player = playerInventory.player;
        this.xCenter = xCenter;
        this.yCenter = yCenter;

        player.getHeldItem(hand).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iModeChanging ->{
            if (iModeChanging instanceof IModeChangingItem) {
                    for (int slot = 0; slot < iModeChanging.getSlots(); ++slot) {
                        this.addSlot(new SlotItemHandler(iModeChanging, slot, 62 + slot * 18, 20));
                    }

                    for (int row = 0; row < 3; ++row) {
                        for (int col = 0; col < 9; ++col) {
                            this.addSlot(new Slot(player.inventory, col + row * 9 + 9, 8 + col * 18, row * 18 + 77));
                        }
                    }

                    for (int slot = 0; slot < 9; ++slot) {
                        this.addSlot(new Slot(player.inventory, slot, 8 + slot * 18, 135));
                    }

//                // We only want the valid modes
//                List<Integer> validModes = ((IModeChangingItem) iModeChanging).getValidModes();
//                // this should put all of the items in the center.
//                for(int index : validModes) {
//                    // position doesn't matter because they will get moved in the GUI
//                    this.addSlot(new SlotItemHandler(iModeChanging, index, 0, 0));
//                }
            }
        });
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        System.out.println("slot was clicked");


//        player.getHeldItem(hand).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iModeChanging ->{
//            if (iModeChanging instanceof IModeChangingItem) {
//                int actualMode = (Integer) ((HashBiMap)validModesAndSlots).inverse().get(slotId);
//                ((IModeChangingItem) iModeChanging).setActiveMode(actualMode);
//            }
//        });
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return player.getHeldItem(hand).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(handler -> handler instanceof IModeChangingItem).orElse(false);
    }
}