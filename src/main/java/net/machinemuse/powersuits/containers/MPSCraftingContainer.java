package net.machinemuse.powersuits.containers;

import com.google.common.collect.Lists;
import net.machinemuse.powersuits.basemod.MPSObjects;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;

public class MPSCraftingContainer extends MPSRecipeBookContainer<CraftingInventory> {
    private final CraftingInventory craftingInventory;
    private final CraftResultInventory resultInventory;
    private final IWorldPosCallable worldPosCallable;
    private final PlayerEntity player;

    public MPSCraftingContainer(int windowId, PlayerInventory playerInventory) {
        this(windowId, playerInventory, IWorldPosCallable.DUMMY);
    }

    public MPSCraftingContainer(int windowId, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(MPSObjects.INSTANCE.MPS_CRAFTING_CONTAINER_TYPE, windowId);
        this.craftingInventory = new CraftingInventory(this, 3, 3);
        this.resultInventory = new CraftResultInventory();
        this.worldPosCallable = worldPosCallable;
        this.player = playerInventory.player;

        // crafting result: slot 0
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInventory, this.resultInventory, 0, 124, 35));

        int row;
        int col;
        // crafting inventory: slot 1-9
        for(row = 0; row < 3; ++row) {
            for(col = 0; col < 3; ++col) {
                this.addSlot(new Slot(this.craftingInventory, col + row * 3, 30 + col * 18, 17 + row * 18));
            }
        }

        // inventory slot 10-36
        for(row = 0; row < 3; ++row) {
            for(col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // hotbar slots 37-45
        for(col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
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

    @Override
    public void onCraftMatrixChanged(IInventory iInventory) {
        System.out.println("matrix changed");

        if (!player.world.isRemote) {
            setCraftingResultSlot(this.windowId, player.world, this.player, this.craftingInventory, this.resultInventory);
        }
    }

    @Override
    public void func_201771_a(RecipeItemHelper helper) {
        System.out.println("func_201771_a");
        this.craftingInventory.fillStackedContents(helper);
    }

    @Override
    public void clear() {
        System.out.println("clear");
        this.craftingInventory.clear();
        this.resultInventory.clear();
    }

    @Override
    public boolean matches(IRecipe recipeIn) {
        return recipeIn.matches(this.craftingInventory, this.player.world);
    }

    //    @Override
//    public boolean matches(IRecipe<? super CraftingInventory> recipeIn) {
//        System.out.println("matches");
//
//        return recipeIn.matches(this.craftingInventory, this.player.world);
//    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
//        this.clearContainer(playerIn, player.world, this.craftingInventory);
        this.worldPosCallable.consume((world, pos) -> this.clearContainer(playerIn, world, this.craftingInventory));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int index) {
        System.out.println("transferStackInSlot");


        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack = slot.getStack();
            stackCopy = itemStack.copy();

            // crafting result
            if (index == 0) {
                this.worldPosCallable.consume((world, pos) -> itemStack.getItem().onCreated(itemStack, world, playerEntity));
                if (!this.mergeItemStack(itemStack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemStack, stackCopy);

            // player inventory
            } else if (index >= 10 && index < 37) {
                if (!this.mergeItemStack(itemStack, 37, 46, false)) {
                    return ItemStack.EMPTY;
                }

            // hotbar
            } else if (index >= 37 && index < 46) {
                if (!this.mergeItemStack(itemStack, 10, 37, false)) {
                    return ItemStack.EMPTY;
                }


            } else if (!this.mergeItemStack(itemStack, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemStack.getCount() == stackCopy.getCount()) {
                return ItemStack.EMPTY;
            }

            ItemStack lvt_6_1_ = slot.onTake(playerEntity, itemStack);
            if (index == 0) {
                playerEntity.dropItem(lvt_6_1_, false);
            }
        }

        return stackCopy;
    }

    @Override
    public boolean canMergeSlot(ItemStack itemStack, Slot slot) {
        return slot.inventory != this.resultInventory && super.canMergeSlot(itemStack, slot);
    }

    @Override
    public int getOutputSlot() {
        return 0;
    }

    @Override
    public int getWidth() {
        return this.craftingInventory.getWidth();
    }

    @Override
    public int getHeight() {
        return this.craftingInventory.getHeight();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getSize() {
        return 10;
    }

    @Override
    public List<RecipeBookCategories> getRecipeBookCategories() {
        // needed since this isn't an an actual instance of WorkbenchContainer
        return Lists.newArrayList(new RecipeBookCategories[]{RecipeBookCategories.SEARCH, RecipeBookCategories.EQUIPMENT, RecipeBookCategories.BUILDING_BLOCKS, RecipeBookCategories.MISC, RecipeBookCategories.REDSTONE});
    }
}