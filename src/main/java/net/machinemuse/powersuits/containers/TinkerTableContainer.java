package net.machinemuse.powersuits.containers;

import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.client.gui.clickable.ClickableModularItem;
import net.machinemuse.numina.client.gui.slot.ClickableModuleSlot;
import net.machinemuse.powersuits.basemod.MPSModules;
import net.machinemuse.powersuits.containers.providers.ContainerTypePicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Looks like slots have to be populated in the container's constructor.
 * This means that only equiped
 *
 */
public class TinkerTableContainer extends Container {
    /*
    types of slots to deal with
    =============================

    Player inventory:
    ----------------------------
    slots with modular items
    slots with modules
    slots with junk -> ignore completely?
    slots with nothing -> ignore or use for targets for uninstalling items

    modular item inventory
    ----------------------------
    slots with modules
    slots with nothing

    fake module inventory
    ----------------------------
    slots with modules -> used to show modules not installed
     */

    // A fake inventory to hold a copy of every module
    private static final IItemHandler allModules = new ItemStackHandler(MPSModules.INSTANCE.getModuleRegNames().size());

    // A map of the slot that holds the modular item, and the set of slots in that modular item
    private Map<ClickableModularItem, Set<ClickableModuleSlot>> modularItemToSlotMap;

    // modules in the player's inventory not installed
    private Set<ClickableModuleSlot> modulesInPlayerInventory;

    // a set of all known modules
    private Set<ClickableModuleSlot> allPossibleModules;




    protected TinkerTableContainer(@Nullable ContainerType<?> type, int windowId) {
        super(type, windowId);

    }

    public TinkerTableContainer(int id, PlayerInventory playerInventory, int typeIndex) {
        this(ContainerTypePicker.getContainerType(typeIndex), id);

        PlayerEntity player = playerInventory.player;
        modularItemToSlotMap = new HashMap<>();
        modulesInPlayerInventory = new HashSet<>();
        allPossibleModules = new HashSet<>();

        for (int index = 0; index < playerInventory.getSizeInventory(); index++) {

            // look for modular items and get all the modules from them.
            int finalIndex = index;
            if(playerInventory.getStackInSlot(index).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(itemHandler -> {
                if (itemHandler instanceof IModularItem) {
                    // add the slot to the container
                    ClickableModularItem modularItemSlot = (ClickableModularItem)
                            this.addSlot(new ClickableModularItem(playerInventory, finalIndex,0, 0));

                    Set<ClickableModuleSlot> moduleSet = new HashSet<>();
                    for (int handlerIndex = 0; handlerIndex < itemHandler.getSlots(); handlerIndex ++) {
                        moduleSet.add((ClickableModuleSlot)
                                this.addSlot(new ClickableModuleSlot(itemHandler, handlerIndex, 0, 0)));
                    }
                    modularItemToSlotMap.put(modularItemSlot, moduleSet);
                    return true;
                }
                return false;
            }).orElse(false)) {

                // really not sure what to do here. Modules in the person's inventory are fine, but what about other stuff?
            }
        }

        List<ResourceLocation> regNames = MPSModules.INSTANCE.getModuleRegNames();

        // A list if modules for display. Maybe use it to open the recipe book or JEI?
        if(!this.inventorySlots.isEmpty()) {
            for (int index = 0; index < allModules.getSlots(); index ++) {
                allModules.insertItem(index, new ItemStack(ForgeRegistries.ITEMS.getValue(regNames.get(index))), false);
                allPossibleModules.add((ClickableModuleSlot)
                        this.addSlot(new ClickableModuleSlot(allModules, index, 0, 0)));
            }
        }
    }

    public Set<ClickableModuleSlot> getAllModules() {
        return allPossibleModules;
    }

    public Map<ClickableModularItem, Set<ClickableModuleSlot>> getModularItemToSlotMap() {
        return modularItemToSlotMap;
    }

    public Set<ClickableModuleSlot> getModulesInPlayerInventory() {
        return modulesInPlayerInventory;
    }

    /**
     *
     intention here is to differentiate slots with modular items installed and slots with everything else
     every modular item/mode changing item needs to go into a special set of slots... their inventory needs to go into another set of slots...
     there needs to be a way to call those so when a modular item is selected, that inventory can be shown
     player inventory should also have slots mapped.. and those can either contain available modules or can be targets of
     removed modules... trick is figuring out how to show modules not in player inventory and not installed... maybe a detached inventory of some sort?




     */




//        armor_Head_Slot_Set = new ArrayList<>();
//        armor_Chest_Slot_Set = new ArrayList<>();
//        armor_Legs_Slot_Set = new ArrayList<>();
//        armor_Feet_Slot_Set;
//        left_Hand_Slot_Set;
//        right_Hand_Slot_Set;













    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}