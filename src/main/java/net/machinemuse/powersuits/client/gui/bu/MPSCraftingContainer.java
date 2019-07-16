//package net.machinemuse.powersuits.client.gui.bu;
//
//import net.machinemuse.numina.client.gui.clickable.ClickableItemSlot;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.inventory.CraftResultInventory;
//import net.minecraft.inventory.CraftingInventory;
//import net.minecraft.inventory.IInventory;
//import net.minecraft.inventory.container.CraftingResultSlot;
//import net.minecraft.inventory.container.MPSRecipeBookContainer;
//import net.minecraft.inventory.container.Slot;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.crafting.ICraftingRecipe;
//import net.minecraft.item.crafting.IRecipe;
//import net.minecraft.item.crafting.IRecipeType;
//import net.minecraft.item.crafting.RecipeItemHelper;
//import net.minecraft.network.play.server.SSetSlotPacket;
//import net.minecraft.util.IWorldPosCallable;
//import net.minecraft.world.World;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//import java.util.Optional;
//
//import static net.machinemuse.powersuits.basemod.MPSObjects.MPS_CRAFTING_CONTAINER_TYPE;
//
//public class MPSCraftingContainer extends MPSRecipeBookContainer<CraftingInventory> {
//    private final CraftingInventory craftingInventory = new CraftingInventory(this, 3, 3);
//    private final CraftResultInventory craftResultInventory = new CraftResultInventory();
//    private final IWorldPosCallable posCallable;
//    private final PlayerEntity player;
//
//    public MPSCraftingContainer(int windoId, PlayerInventory playerInventory) {
//        this(windoId, playerInventory, IWorldPosCallable.DUMMY);
//    }
//
//    public MPSCraftingContainer(int windowId, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
//        super(MPS_CRAFTING_CONTAINER_TYPE, windowId);
//        this.posCallable = worldPosCallable;
//        this.player = playerInventory.player;
//        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInventory, this.craftResultInventory, 0, 124, 35));
//
//        for (int i = 0; i < 3; ++i) {
//            for (int j = 0; j < 3; ++j) {
//                this.addSlot(new ClickableItemSlot(this.craftingInventory, j + i * 3, 30 + j * 18, 17 + i * 18));
//            }
//        }
//
//        for (int k = 0; k < 3; ++k) {
//            for (int i1 = 0; i1 < 9; ++i1) {
//                this.addSlot(new ClickableItemSlot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
//            }
//        }
//
//        for (int l = 0; l < 9; ++l) {
//            this.addSlot(new ClickableItemSlot(playerInventory, l, 8 + l * 18, 142));
//        }
//    }
//
//    protected static void func_217066_a(int windowId, World world, PlayerEntity playerEntity, CraftingInventory craftingInventory, CraftResultInventory craftResultInventory) {
//        if (!world.isRemote) {
//            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) playerEntity;
//            ItemStack itemstack = ItemStack.EMPTY;
//            Optional<ICraftingRecipe> optional = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftingInventory, world);
//            if (optional.isPresent()) {
//                ICraftingRecipe icraftingrecipe = optional.get();
//                if (craftResultInventory.canUseRecipe(world, serverplayerentity, icraftingrecipe)) {
//                    itemstack = icraftingrecipe.getCraftingResult(craftingInventory);
//                }
//            }
//
//            craftResultInventory.setInventorySlotContents(0, itemstack);
//            serverplayerentity.connection.sendPacket(new SSetSlotPacket(windowId, 0, itemstack));
//        }
//    }
//
//    /**
//     * Callback for when the crafting matrix is changed.
//     */
//    @Override
//    public void onCraftMatrixChanged(IInventory inventoryIn) {
//        this.posCallable.consume((world, blockPos) -> {
//            func_217066_a(this.windowId, world, this.player, this.craftingInventory, this.craftResultInventory);
//        });
//    }
//
//    @Override
//    public void func_201771_a(RecipeItemHelper p_201771_1_) {
//        this.craftingInventory.fillStackedContents(p_201771_1_);
//    }
//
//    @Override
//    public void clear() {
//        this.craftingInventory.clear();
//        this.craftResultInventory.clear();
//    }
//
//    @Override
//    public boolean matches(IRecipe<? super CraftingInventory> recipeIn) {
//        return recipeIn.matches(this.craftingInventory, this.player.world);
//    }
//
//    /**
//     * Called when the container is closed.
//     */
//    @Override
//    public void onContainerClosed(PlayerEntity playerIn) {
//        super.onContainerClosed(playerIn);
//        this.posCallable.consume((world, blockPos) -> {
//            this.clearContainer(playerIn, world, this.craftingInventory);
//        });
//    }
//
//    /**
//     * Determines whether supplied player can use this container
//     */
//    @Override
//    public boolean canInteractWith(PlayerEntity playerIn) {
//        return true;
////        return isWithinUsableDistance(this.posCallable, playerIn, Blocks.CRAFTING_TABLE);
//    }
//
//    /**
//     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
//     * inventory and the other inventory(s).
//     */
//    @Override
//    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
//        ItemStack itemstack = ItemStack.EMPTY;
//        Slot slot = this.inventorySlots.get(index);
//        if (slot != null && slot.getHasStack()) {
//            ItemStack itemstack1 = slot.getStack();
//            itemstack = itemstack1.copy();
//            if (index == 0) {
//                this.posCallable.consume((world, blockPos) -> {
//                    itemstack1.getItem().onCreated(itemstack1, world, playerIn);
//                });
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
//            if (index == 0) {
//                playerIn.dropItem(itemstack2, false);
//            }
//        }
//
//        return itemstack;
//    }
//
//    /**
//     * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
//     * null for the initial slot that was double-clicked.
//     */
//    @Override
//    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
//        return slotIn.inventory != this.craftResultInventory && super.canMergeSlot(stack, slotIn);
//    }
//
//    @Override
//    public int getOutputSlot() {
//        return 0;
//    }
//
//    @Override
//    public int getWidth() {
//        return this.craftingInventory.getWidth();
//    }
//
//    @Override
//    public int getHeight() {
//        return this.craftingInventory.getHeight();
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public int getSize() {
//        return 10;
//    }
//}
