package net.machinemuse.powersuits.containers;

import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.client.gui.slot.ClickableModuleSlot;
import net.machinemuse.powersuits.basemod.MPSObjects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;

/**
 * This weird container abomination is just for the mode changing item handling to select the active module
 * The item slots are spawned in the center and then should spiral outward with the help of the associated container screen
 */
public class ModeChangingContainer extends Container {
    final PlayerInventory playerInventory;
    PlayerEntity player;

    private static IInventory fakeInventory = new Inventory(1);

    public ModeChangingContainer(int windowId, PlayerInventory playerInventory) {
        super(MPSObjects.INSTANCE.MODE_CHANGING_CONTAINER_TYPE, windowId);
        this.playerInventory = playerInventory;
        player = playerInventory.player;

        player.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iModeChanging -> {
            if (iModeChanging instanceof IModeChangingItem) {
                // We only want the valid modes
                List<Integer> validModes = ((IModeChangingItem) iModeChanging).getValidModes();
                if (!validModes.isEmpty()) {
                    // this should put all of the items in the center.
                    for (int index : validModes) {
                        // position doesn't matter because they will get moved in the GUI
                        this.addSlot(new ClickableModuleSlot(iModeChanging, index, 0, 0));
//                        validModesAndSlots.put(index);
                    }
                } else {
                    this.addSlot(new Slot(fakeInventory, 0, 0, 0));
                }
            }
        });
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        System.out.println("slot <" + slotId + "> was clicked");


        player.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iModeChanging -> {
            if (iModeChanging instanceof IModeChangingItem) {
                int actualMode = this.inventorySlots.get(slotId).getSlotIndex();
                ((IModeChangingItem) iModeChanging).setActiveMode(actualMode);
            }
        });
        return ItemStack.EMPTY;

//        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return player.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(handler -> handler instanceof IModeChangingItem).orElse(false);
    }


    //-----------------------------------------------------------------------------------------

//    @Override
//    public void onContainerClosed(PlayerEntity playerIn) {
//        System.out.println("onContainerClosed");
//
//
//        super.onContainerClosed(playerIn);
//
//
//    }
//
//    @Override
//    public void removeListener(IContainerListener listener) {
//        System.out.println("removeListener");
//
//        super.removeListener(listener);
//    }
//
//    @Override
//    public ContainerType<?> getType() {
//        return super.getType();
//    }
//
//    @Override
//    protected Slot addSlot(Slot slotIn) {
//        return super.addSlot(slotIn);
//    }
//
//    @Override
//    protected IntReferenceHolder trackInt(IntReferenceHolder intIn) {
//        System.out.println("trackInt");
//
//        return super.trackInt(intIn);
//    }
//
//    @Override
//    protected void trackIntArray(IIntArray arrayIn) {
//        System.out.println("trackIntArray(");
//
//        super.trackIntArray(arrayIn);
//    }
//
//
//
//    @Override
//    public void addListener(IContainerListener listener) {
//        System.out.println("addListener");
//
//        super.addListener(listener);
//    }
//
//    @Override
//    public NonNullList<ItemStack> getInventory() {
//        System.out.println("getInventory");
//
//        return super.getInventory();
//    }
//
//    @Override
//    public void detectAndSendChanges() {
//        System.out.println("detectAndSendChanges");
//
//
//        super.detectAndSendChanges();
//    }
//
//    @Override
//    public boolean enchantItem(PlayerEntity playerIn, int id) {
//        System.out.println("cenchantItem");
//
//
//        return super.enchantItem(playerIn, id);
//    }
//
//    @Override
//    public Slot getSlot(int slotId) {
//        System.out.println("getSlot");
//
//        return super.getSlot(slotId);
//    }
//
//    @Override
//    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
//        return super.transferStackInSlot(playerIn, index);
//    }
//
//    @Override
//    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
//        System.out.println("canMergeSlot");
//
//
//        return super.canMergeSlot(stack, slotIn);
//    }
//
//    @Override
//    protected void clearContainer(PlayerEntity playerIn, World worldIn, IInventory inventoryIn) {
//        System.out.println("clearContainer");
//
//        super.clearContainer(playerIn, worldIn, inventoryIn);
//    }
//
//    @Override
//    public void onCraftMatrixChanged(IInventory inventoryIn) {
//        super.onCraftMatrixChanged(inventoryIn);
//    }
//
//    @Override
//    public void putStackInSlot(int slotID, ItemStack stack) {
//        System.out.println("putStackInSlot");
//
//        super.putStackInSlot(slotID, stack);
//    }
//
//    @Override
//    public void setAll(List<ItemStack> p_190896_1_) {
//        System.out.println("setAll");
//
//        super.setAll(p_190896_1_);
//    }
//
//    @Override
//    public void updateProgressBar(int id, int data) {
//        super.updateProgressBar(id, data);
//    }
//
//    @Override
//    public short getNextTransactionID(PlayerInventory invPlayer) {
//
//        System.out.println("getNextTransactionID");
//
//        return super.getNextTransactionID(invPlayer);
//    }
//
//    @Override
//    public boolean getCanCraft(PlayerEntity player) {
//        return super.getCanCraft(player);
//    }
//
//    @Override
//    public void setCanCraft(PlayerEntity player, boolean canCraft) {
//        super.setCanCraft(player, canCraft);
//    }
//
//    @Override
//    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
//        return super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
//    }
//
//    @Override
//    protected void resetDrag() {
//        super.resetDrag();
//    }
//
//    @Override
//    public boolean canDragIntoSlot(Slot slotIn) {
//        return super.canDragIntoSlot(slotIn);
//    }
}