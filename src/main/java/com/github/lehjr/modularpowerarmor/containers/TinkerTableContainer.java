package com.github.lehjr.modularpowerarmor.containers;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.network.packets.MusePacketCreativeInstallModuleRequest;
import com.github.lehjr.modularpowerarmor.network.packets.MusePacketModuleMoveFromSlotToSlot;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.client.gui.slot.ClickableModuleSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Looks like slots have to be populated in the container's constructor.
 * This means that only equipped ... fixme: ...what?
 *
 */
public class TinkerTableContainer extends MPARecipeBookContainer<CraftingInventory> implements IModularItemToSlotMapProvider {
    private final CraftingInventory craftingInventory;
    private final CraftResultInventory resultInventory;
    private final PlayerEntity player;

    // A map of the slot that holds the modular item, and the set of slots in that modular item
    private Map<Integer, List<SlotItemHandler>> modularItemToSlotMap;

    // a set of all known modules
    private Set<ClickableModuleSlot> allPossibleModules;

    public TinkerTableContainer(int windowId, PlayerInventory playerInventory) {
        super(MPAObjects.INSTANCE.TINKER_TABLE_CONTAINER_TYPE, windowId);
        this.craftingInventory = new CraftingInventory(this, 3, 3);
        this.resultInventory = new CraftResultInventory();
        this.player = playerInventory.player;

        modularItemToSlotMap = new HashMap<>();

        // crafting result: slot 0
        this.addSlot(new HideableResultSlot(playerInventory.player, this.craftingInventory, this.resultInventory, 0, -1000, -1000));

        int row;
        int col;
        // crafting inventory: slot 1-9
        for(row = 0; row < 3; ++row) {
            for(col = 0; col < 3; ++col) {
                this.addSlot(new HideableSlot(this.craftingInventory, col + row * 3, -1000, -1000));
            }
        }

        // add all player inventory slots
        for (int index = 0; index < playerInventory.getSizeInventory(); index ++) {
            this.addSlot(new HideableSlot(playerInventory, index, 0, 0));
        }

        // add all modular item slots
        for (Slot slot :  new ArrayList<Slot>(this.inventorySlots)) {
            List<SlotItemHandler> slots = new ArrayList<>();

            slot.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                if (iItemHandler instanceof IModularItem) {
                    for (int modularItemInvIndex = 0; modularItemInvIndex < iItemHandler.getSlots(); modularItemInvIndex ++) {
                        HideableSlotItemHandler slot1 =
                                (HideableSlotItemHandler) addSlot(new HideableSlotItemHandler(iItemHandler, inventorySlots.indexOf(slot), modularItemInvIndex, -1000, -1000));
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
    }

    class HideableSlotItemHandler extends SlotItemHandler implements IHideableSlot {
        boolean isEnabled = false;
        protected int parentSlot = -1;

        public HideableSlotItemHandler(IItemHandler itemHandler, int parent, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
            this.parentSlot = parent;
        }

        public int getParentSlot(){
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

    class HideableSlot extends Slot implements IHideableSlot {
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

    class HideableResultSlot extends CraftingResultSlot implements IHideableSlot {
        boolean isEnabled = false;
        public HideableResultSlot(PlayerEntity playerEntity, CraftingInventory craftingInventory, IInventory inventory, int slotIndex, int posX, int posY) {
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

    public Map<Integer, List<SlotItemHandler>> getModularItemToSlotMap() {
        return modularItemToSlotMap;
    }

    @Override
    public boolean canMergeSlot(ItemStack itemStack, Slot slot) {
        if (slot instanceof SlotItemHandler) {
            return ((SlotItemHandler) slot).getItemHandler().isItemValid(slot.getSlotIndex(), itemStack);
        }
        return slot.inventory != this.resultInventory && super.canMergeSlot(itemStack, slot);
    }

    @Override
    public boolean canDragIntoSlot(Slot slotIn) {
        return false;
    }

    /**
     * Merges provided ItemStack with the first avaliable one in the container/player inventor between minIndex
     * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. /!\ the Container
     * implementation do not check if the item is valid for the slot
     */
    @Override
    public boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();
                if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getItemStackLimit(stack)/*.getSlotStackLimit()*/, stack.getMaxStackSize());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.onSlotChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.onSlotChanged();
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
                    if (stack.getCount() > slot1.getItemStackLimit(stack)/*.getSlotStackLimit()*/) {
                        slot1.putStack(stack.split(slot1.getSlotStackLimit()));
                    } else {
                        slot1.putStack(stack.split(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    @Override
    public Container getContainer() {
        return this;
    }

    public void creativeInstall(int slot, @Nonnull ItemStack itemStack) {
        if(this.getSlot(slot).getItemStackLimit(itemStack) > 0) {
            putStackInSlot(slot, itemStack);
//            this.detectAndSendChanges();
            MPAPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketCreativeInstallModuleRequest(this.windowId, slot, itemStack));
        }
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

        if(sourceSlot.canTakeStack(player) && canMergeSlot(contents, targetSlot)) {
            if (source == getOutputSlot()) {
                if (this.mergeItemStack(stackCopy, target, target+ 1, false)) {
                    sourceSlot.onTake(player, contents);
                    MPAPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketModuleMoveFromSlotToSlot(this.windowId, source, target));
                }
            } else {
                MPAPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketModuleMoveFromSlotToSlot(this.windowId, source, target));
                targetSlot.putStack(stackCopy);
                sourceSlot.putStack(ItemStack.EMPTY);
            }
            detectAndSendChanges();
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory iInventory) {
        if (!player.world.isRemote) {
            setCraftingResultSlot(this.windowId, player.world, this.player, this.craftingInventory, this.resultInventory);
        }
    }

    protected static void setCraftingResultSlot(int windowId, World world, PlayerEntity playerIn, CraftingInventory craftingInventory, CraftResultInventory resultInventory) {
        if (!world.isRemote) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)playerIn;
            ItemStack itemStack = ItemStack.EMPTY;
            Optional<ICraftingRecipe> optionalRecipe = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftingInventory, world);
            if (optionalRecipe.isPresent()) {
                ICraftingRecipe recipe = (ICraftingRecipe)optionalRecipe.get();
                if (resultInventory.canUseRecipe(world, serverPlayer, recipe)) {
                    itemStack = recipe.getCraftingResult(craftingInventory);
                }
            }
            // set result slot on server side then send packet to set same on client
            resultInventory.setInventorySlotContents(0, itemStack);
            serverPlayer.connection.sendPacket(new SSetSlotPacket(windowId, 0, itemStack));
        }
    }

    /**
     * replace IWorldPosCallable.consume with something not position specific
     * since this will be used by a portable setup
     */
    public void consume(PlayerEntity playerIn) {
        this.resultInventory.clear();
        if (!playerIn.world.isRemote) {
            this.clearContainer(playerIn, playerIn.world, this.craftingInventory);
        }
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        consume(playerIn);
    }

    @Override
    public void func_201771_a(RecipeItemHelper helper) {
        this.craftingInventory.fillStackedContents(helper);
    }

    @Override
    public void clear() {
        this.craftingInventory.clear();
        this.resultInventory.clear();
    }

    @Override
    public boolean matches(IRecipe recipeIn) {
        return recipeIn.matches(this.craftingInventory, this.player.world);
    }

    @Override
    public int getOutputSlot() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public int getSize() {
        return getHeight() * getWidth() + 1;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}