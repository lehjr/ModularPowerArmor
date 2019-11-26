package com.github.lehjr.modularpowerarmor.client.gui.tinker.module;


import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.network.packets.CreativeModuleInstallRequestPacket;
import com.github.lehjr.modularpowerarmor.network.packets.ModuleMoveFromSlotToSlotPacket;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class TinkerTableContainer extends ContainerWorkbench implements IModularItemToSlotMapProvider {
//    InventoryPlayer inventory;
//    EntityPlayer player;
//    World world;
//    int slotsIndex = -1;


//    /**
//     * The crafting matrix inventory (3x3).
//     */
//    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
//    public InventoryCraftResult craftResult = new InventoryCraftResult();
    private final World world;
//    /**
//     * Position of the workbench
//     */
    private final BlockPos pos;
    private final EntityPlayer player;

    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};


    /**
     * Note positions of slots are not important here. They will get moved where they need to go later
     *
     * @param playerInventory
     * @param worldIn
     * @param posIn
     */
    public TinkerTableContainer(InventoryPlayer playerInventory, World worldIn, BlockPos posIn) {
        super(playerInventory, worldIn, posIn);

        this.world = worldIn;
        this.pos = posIn;
        this.player = playerInventory.player;

        // clear out the inventory slots from the super class.
        this.inventorySlots.clear();

        modularItemToSlotMap = new HashMap<>();

        // crafting result: slot 0
        this.addSlotToContainer(new HideableResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, -1000, -1000));

        // crafting matrix
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlotToContainer(new HideableSlot(this.craftMatrix, col + row * 3, -1000, -1000));
            }
        }

        // Offhand
        this.addSlotToContainer(new HideableSlot(playerInventory, 40, 77, 62));

        // Entity Equipment Armor Slots
        for (int k = 0; k < 4; ++k) {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            System.out.println("36 + (3 - k): " + (39 - k));
            this.addSlotToContainer(new HideableSlot(playerInventory, 39 - k, 8, 8 + k * 18) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
                }

                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
                 * in the case of armor slots)
                 */
                public int getSlotStackLimit() {
                    return 1;
                }
            });
        }

        // main inventory and hotbar (positions are not important here)
        for(int index = 0; index < playerInventory.mainInventory.size(); index++) {
            this.addSlotToContainer(new HideableSlot(playerInventory, index,  -1000, -1000));
        }

        // add all modular item slots
        for (Slot slot :  new ArrayList<Slot>(this.inventorySlots)) {
            List<SlotItemHandler> slots = new ArrayList<>();

            Optional.ofNullable(slot.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(iItemHandler -> {
                if (iItemHandler instanceof IModularItem) {
                    for (int modularItemInvIndex = 0; modularItemInvIndex < iItemHandler.getSlots(); modularItemInvIndex ++) {
                        HideableSlotItemHandler slot1 =
                                (HideableSlotItemHandler) addSlotToContainer(new HideableSlotItemHandler(iItemHandler, inventorySlots.indexOf(slot), modularItemInvIndex, -1000, -1000));
                        slots.add(slot1);
                    }
                }
            });

            if (!slots.isEmpty()) {
                modularItemToSlotMap.put(slot.slotNumber, slots);
            }
        }

        for (Slot slot : this.inventorySlots) {
            if(slot instanceof IHideableSlot) {
                ((IHideableSlot) slot).disable();
            }
        }

        System.out.println("slots here: " + inventorySlots.size());

        player.openContainer = this; // make sure player's open container is set to this because it isn't happening automatically
    }

    static class HideableSlotItemHandler extends SlotItemHandler implements IHideableSlot {
        boolean isEnabled = false;
        protected int parentSlot;

        public HideableSlotItemHandler(IItemHandler itemHandler, int parent, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
            this.parentSlot = parent;
        }

        public int getParentSlot() {
            return parentSlot;
        }

        @Override
        public void enable() {
            this.isEnabled = true;
        }

        @Override
        public void disable() {
            this.isEnabled = false;
        }

        @Override
        public boolean isEnabled() {
            return isEnabled;
        }
    }

    static class HideableSlot extends Slot implements IHideableSlot {
        boolean isEnabled = false;

        public HideableSlot(IInventory iInventory, int slotIndex, int posX, int posY) {
            super(iInventory, slotIndex, posX, posY);
        }

        @Override
        public void enable() {
            this.isEnabled = true;
        }

        @Override
        public void disable() {
            this.isEnabled = false;
        }

        @Override
        public boolean isEnabled() {
            return isEnabled;
        }
    }

    static class HideableResultSlot extends SlotCrafting implements IHideableSlot {
        boolean isEnabled = false;

        public HideableResultSlot(EntityPlayer playerEntity, InventoryCrafting craftingInventory, IInventory inventory, int slotIndex, int posX, int posY) {
            super(playerEntity, craftingInventory, inventory, slotIndex, posX, posY);
        }

        @Override
        public void enable() {
            this.isEnabled = true;
        }

        @Override
        public void disable() {
            this.isEnabled = false;
        }

        @Override
        public boolean isEnabled() {
            return isEnabled;
        }

    }

    // A map of the slot that holds the modular item, and the set of slots in that modular item
    private Map<Integer, List<SlotItemHandler>> modularItemToSlotMap;

    @Override
    public Map<Integer, List<SlotItemHandler>> getModularItemToSlotMap() {
        return modularItemToSlotMap;
    }

    @Override
    public Container getContainer() {
        return this;
    }

//    @Override
//    public void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftingInventory, InventoryCraftResult resultInventory) {
//        if (!world.isRemote) {
//            EntityPlayerMP entityplayermp = (EntityPlayerMP) player;
//            ItemStack itemstack = ItemStack.EMPTY;
//            IRecipe irecipe = CraftingManager.findMatchingRecipe(craftingInventory, world);
//
//            if (irecipe != null && (irecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(irecipe))) {
//                resultInventory.setRecipeUsed(irecipe);
//                itemstack = irecipe.getCraftingResult(craftingInventory);
//            }
//
//            resultInventory.setInventorySlotContents(0, itemstack);
//            entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, itemstack));
//        }
//    }

    public void creativeInstall(int slot, @Nonnull ItemStack itemStack) {
        if (this.getSlot(slot).getItemStackLimit(itemStack) > 0) {
            putStackInSlot(slot, itemStack);
//            this.detectAndSendChanges();
            MPAPackets.INSTANCE.sendToServer(new CreativeModuleInstallRequestPacket(this.windowId, slot, itemStack));
        }
    }

    @Override
    public Slot getSlot(int slotId) {
        System.out.println("getting slot at index: " + slotId + " of " + inventorySlots.size());


        return inventorySlots.get(slotId);
    }

    public void move(int source, int target) {
        if (source == -1)
            return;
        if (target == -1)
            return;

        Slot sourceSlot = inventorySlots.get(source);
        Slot targetSlot = inventorySlots.get(target);

        ItemStack contents = sourceSlot.getStack();
        ItemStack stackCopy = contents.copy();

        if (sourceSlot.canTakeStack(player) && canMergeSlot(contents, targetSlot)) {
            if (source == getOutputSlot()) {
                if (this.mergeItemStack(stackCopy, target, target + 1, false)) {
                    sourceSlot.onTake(player, contents);
                    MPAPackets.INSTANCE.sendToServer(new ModuleMoveFromSlotToSlotPacket(this.windowId, source, target));
                }
            } else {
                MPAPackets.INSTANCE.sendToServer(new ModuleMoveFromSlotToSlotPacket(this.windowId, source, target));
                targetSlot.putStack(stackCopy);
                sourceSlot.putStack(ItemStack.EMPTY);
            }
            detectAndSendChanges();
        }
    }

    int getOutputSlot() {
        return 0;
    }


    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.slotChangedCraftingGrid(this.world, this.player, this.craftMatrix, this.craftResult);
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        if (!this.world.isRemote) {
            this.clearContainer(playerIn, this.world, this.craftMatrix);
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    /**
     * replace IWorldPosCallable.consume with something not position specific
     * since this will be used by a portable setup
     */
    public void consume(EntityPlayer playerIn) {
        this.craftResult.clear();
        if (!playerIn.world.isRemote) {
            this.clearContainer(playerIn, playerIn.world, this.craftMatrix);
        }
    }

//    /**
//     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
//     * inventory and the other inventory(s).
//     */
//    @Override
//    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
//        ItemStack itemstack = ItemStack.EMPTY;
//        Slot slot = this.inventorySlots.get(index);
//
//        if (slot != null && slot.getHasStack()) {
//            ItemStack itemstack1 = slot.getStack();
//            itemstack = itemstack1.copy();
//
//            if (index == 0) {
//                itemstack1.getItem().onCreated(itemstack1, this.world, playerIn);
//
//                if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
//                    return ItemStack.EMPTY;
//                }
//
//                slot.onSlotChange(itemstack1, itemstack);
//            } else if (index >= 10 && index < 37) {
//                if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
//                    return ItemStack.EMPTY;
//                }
//            } else if (index >= 37 && index < 46) {
//                if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
//                    return ItemStack.EMPTY;
//                }
//            } else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
//                return ItemStack.EMPTY;
//            }
//
//            if (itemstack1.isEmpty()) {
//                slot.putStack(ItemStack.EMPTY);
//            } else {
//                slot.onSlotChanged();
//            }
//
//            if (itemstack1.getCount() == itemstack.getCount()) {
//                return ItemStack.EMPTY;
//            }
//
//            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
//
//            if (index == 0) {
//                playerIn.dropItem(itemstack2, false);
//            }
//        }
//
//        return itemstack;
//    }
//


    /*

        / **

        // default version
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     * /
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        Slot slot = this.inventorySlots.get(index);
        return slot != null ? slot.getStack() : ItemStack.EMPTY;
    }

    // crafting version


        public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0)
            {
                itemstack1.getItem().onCreated(itemstack1, this.world, playerIn);

                if (!this.mergeItemStack(itemstack1, 10, 46, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 10 && index < 37)
            {
                if (!this.mergeItemStack(itemstack1, 37, 46, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (index >= 37 && index < 46)
            {
                if (!this.mergeItemStack(itemstack1, 10, 37, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);

            if (index == 0)
            {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }

     */


    @Override
    public boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        return super.mergeItemStack(stack, startIndex, endIndex, reverseDirection);
    }

    /**
     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in
     * is null for the initial slot that was double-clicked.
     */
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }
}