package net.machinemuse.powersuits.client.gui.tinker.module_new;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.client.gui.clickable.ClickableButton;
import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.machinemuse.numina.client.gui.clickable.ClickableModule;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseTile;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.scrollable.ScrollableFrame;
import net.machinemuse.numina.client.gui.slot.UniversalSlot;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.containers.ModularItemContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class InstallSalvageFrame extends ScrollableFrame {
    protected ItemSelectionFrame targetItem;
    protected ModuleSelectionFrame targetModule;
    protected ClickableButton craftAndInstallButton;
    protected ClickableButton installButton;
    protected ClickableButton salvageButton;
    //    protected InventoryFrame craftingGrid;
    ModularItemContainer container;
    List<Integer> craftingSlotIndices;
    protected PlayerEntity player;
    final int craftingGridSize = 54; // height and width same

    public InstallSalvageFrame(
            PlayerEntity player,
            MusePoint2D topleft,
            MusePoint2D bottomright,
            Colour backgroundColour,
            Colour borderColour,
            Colour gridColour,
            ItemSelectionFrame targetItem,
            ModuleSelectionFrame targetModule) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.player = player;
        this.targetItem = targetItem;
        this.targetModule = targetModule;
        double sizex = border.right() - border.left();
        double sizey = border.bottom() - border.top();

        this.craftAndInstallButton = new ClickableButton(I18n.format("gui.powersuits.craftAndInstall"),
                new MusePoint2D(border.right() - sizex / 2.0, border.bottom() - sizey / 4.0),
                true);

        this.installButton = new ClickableButton(I18n.format("gui.powersuits.install"),
                new MusePoint2D(border.right() - sizex / 2.0, border.bottom() - sizey / 4.0),
                true);

        this.salvageButton = new ClickableButton(I18n.format("gui.powersuits.salvage"),
                new MusePoint2D(border.left() + sizex / 2.0, border.top() + sizey / 4.0),
                true);
//        craftingSlotIndices = new ArrayList<Integer>(){{
//            IntStream.range(containerIn.inventorySlots.size() - 10, containerIn.inventorySlots.size()-1).forEach(this::add);
//        }};
//
//        this.craftingGrid = new InventoryFrame(container,
//                new MusePoint2D(0,0), new MusePoint2D(0, 0),        ItemStack module = targetModule.getSelectedModule().getModule();
//        ItemStack modularItem = targetItem.getSelectedItem().getStack();
//        if (!module.isEmpty() && !modularItem.isEmpty()) {
//            modularItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap ->{
//                if (cap instanceof IModularItem) {
//                    ItemStack result = module.copy();
//                    if (player.abilities.isCreativeMode) {
////                        result = ((IModularItem) cap).installModule(result);
//                        if (result.isEmpty()) {
//                            System.out.println("sending detect and send changes");
//                            targetItem.container.detectAndSendChanges();
//                        }
//                    } else {
//
//                    }
//                }
//            });




//
//            if (player.abilities.isCreativeMode /*|| MuseItemUtils.hasInInventory(ModuleManager.INSTANCE.getInstallCost(module.getDataName()), player.inventory)*/) {
//                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_INSTALL, 1);
//                // Now send request to server to make it legit
////                MPSPackets.CHANNEL_INSTANCE.sendToServer(
////                        new MusePacketInstallModuleRequest(
////                                targetModule.getSelectedModule().getSlotIndex(),
////                                module.getItem().getRegistryName().toString(),
////                                targetItem.getSelectedItem().getSlotIndex()
////                        ));
//            }
//        }
////                backgroundColour, borderColour, gridColour,
////                3, 3, craftingSlotIndices);
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);

//        this.craftingGrid.init(border.centerx() - craftingGridSize/2,
//                border.finalTop() + 5,
//                border.centerx() + craftingGridSize/2,
//                    border.finalTop() + 5  + craftingGridSize);


//        this.craftingGrid.init(border.finalRight() - 7 - craftingGridSize,
//                border.centery() - craftingGridSize/2,
//                border.finalRight() - 7,
//                border.centery() + craftingGridSize/2);


    }

    @Override
    public void update(double x, double y) {
        super.update(x, y);

        double sizex = border.right() - border.left();
        double sizey = border.bottom() - border.top();

        this.craftAndInstallButton.setPosition(
                new MusePoint2D(border.left() + (sizex - craftingGridSize -7)/ 2.0, border.bottom() - sizey / 2.0));
        this.installButton.setPosition(
                new MusePoint2D( border.left() + sizex / 2.0, border.bottom() - sizey / 2.0));
        this.salvageButton.setPosition(
                new MusePoint2D( border.left() + sizex / 2.0, border.bottom() - sizey / 2.0));

        if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null) {
            ClickableItem selectedItem = targetItem.getSelectedItem();
            ClickableModule selectedModule = targetModule.getSelectedModule();


            // fixme: show conflicts
            if (selectedModule.isInstalled()) {
                salvageButton.show();
                installButton.hide();
                craftAndInstallButton.hide();
            } else if (player.abilities.isCreativeMode
                // fixme: condition where player has the crafted item already

            ) {
                salvageButton.hide();
                installButton.show();
                craftAndInstallButton.hide();
            } else {
                salvageButton.hide();
                installButton.hide();
                craftAndInstallButton.show();
            }
        }
    }

    /*
        Looks like a RecipeList is a collection of recipes for a given ItemStack.
        As such, a RecipeList might only have one recipe.


     */

    void thing() {
        List<RecipeList> recipeCollection = Minecraft.getInstance().player.getRecipeBook().getRecipes();

        for (RecipeList recipesList: recipeCollection) {
            for (IRecipe<?> recipe : recipesList.getRecipes(true)) {


            }
        }


    }


    void craftingGridHide() {
        for (Integer index : craftingSlotIndices) {
            Slot slot = container.getSlot(index);
            if (slot instanceof UniversalSlot) {
                ((UniversalSlot) slot).hide();
            }
        }
    }

    void craftingGridShow() {
        for (Integer index : craftingSlotIndices) {
            Slot slot = container.getSlot(index);
            if (slot instanceof UniversalSlot) {
                ((UniversalSlot) slot).show();
            }
        }
    }














    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        // FIXME: switch install cost to recipe


        if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null) {
            ClickableItem selectedItem = targetItem.getSelectedItem();
            ClickableModule selectedModule = targetModule.getSelectedModule();
            NonNullList<ItemStack> itemsToCheck = NonNullList.create(); // ModuleManager.INSTANCE.getInstallCost(module.getDataName());
            AtomicReference<Double> yoffset = new AtomicReference<>((double) 0);
            selectedItem.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                if (cap instanceof IModularItem) {
                    if(!((IModularItem) cap).isModuleInstalled(selectedModule.getModule().getItem().getRegistryName())) {
                        yoffset.set(border.bottom() - 20);
                    } else {
                        yoffset.set(border.top() + 4);
                    }
                }
            });

            if (yoffset.get() + 16 > y && yoffset.get() < y) {
                double xoffset = -8.0 * itemsToCheck.size()
                        + (border.left() + border.right()) / 2;
                if (xoffset + 16 * itemsToCheck.size() > x && xoffset < x) {
                    int index = (int) (x - xoffset) / 16;
                    return itemsToCheck.get(index).getTooltip(player, ITooltipFlag.TooltipFlags.NORMAL);
                }
            }
        }
        return null;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null) {
            drawBackground(mouseX, mouseY, partialTicks);
            drawItems(mouseX, mouseY, partialTicks);
            drawButtons(mouseX, mouseY, partialTicks);

//            if (craftAndInstallButton.isEnabled() && craftAndInstallButton.isVisible()) {
//                craftingGrid.render(mouseX, mouseY, partialTicks);
//            }
        }
    }

    private void drawBackground(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
    }


    // fixme: switch to recipe?
    private void drawItems(int mouseX, int mouseY, float partialTicks) {
        ClickableItem selectedItem = targetItem.getSelectedItem();
        ClickableModule selectedModule = targetModule.getSelectedModule();
        NonNullList<ItemStack> itemsToDraw = NonNullList.create();//ModuleManager.INSTANCE.getInstallCost(module.getDataName()); // FIXME!!
        double yoffset;
//        if (!ModuleManager.INSTANCE.itemHasModule(stack, module)) {
//            yoffset = border.top() + 4;
//        } else {
        yoffset = border.bottom() - 20;
//        }
        double xoffset = -8.0 * itemsToDraw.size()
                + (border.left() + border.right()) / 2;
        int i = 0;
        for (ItemStack costItem : itemsToDraw) {
            MuseRenderer.drawItemAt(
                    16 * i++ + xoffset,
                    yoffset,
                    costItem);
        }
    }

    private void drawButtons(int mouseX, int mouseY, float partialTicks) {
        ClickableItem selectedItem = targetItem.getSelectedItem();
        ClickableModule selectedModule = targetModule.getSelectedModule();
        selectedItem.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            if (cap instanceof IModularItem) {
                // if module is installed enable salvage button
                if(((IModularItem) cap).isModuleInstalled(selectedModule.getModule().getItem().getRegistryName())) {
                    salvageButton.show();
                    salvageButton.render(mouseX, mouseY, partialTicks);
                } else {
                    installButton.show();
                    // check if player is in creative mode or has the module in inventory
                    if (player.abilities.isCreativeMode || player.inventory.hasItemStack(selectedModule.getModule())) {

                        boolean canInstall = false;
                        for (int i = 0; i < cap.getSlots(); i++) {
                            if(cap.insertItem(i, selectedModule.getModule(), true).isEmpty()) {
                                canInstall = true;
                                break;
                            }
                        }
                        installButton.setEnabled(canInstall);
                        installButton.render(mouseX, mouseY, partialTicks);
                    } else {
                        craftAndInstallButton.show();
                        // todo: recipe widget
                        craftAndInstallButton.render(mouseX, mouseY, partialTicks);

                    }



                    // fixme: condition does player have module in inventory

//
//                    installButton.setVisible(true);
//                    installButton.render(mouseX, mouseY, partialTicks);
                }
            }
        });


//        if (!ModuleManager.INSTANCE.itemHasModule(stack, module.getItem().getRegistryName())) {
//            int installedModulesOfType = ModuleManager.INSTANCE.getNumberInstalledModulesOfType(stack, ((IPowerModule)module.getItem()).getCategory());
//            installButton.setEnabled(true); // fixme!!!!
////                    player.abilities.isCreativeMode ||
////                    (MuseItemUtils.hasInInventory(ModuleManager.INSTANCE.getInstallCost(module.getDataName()), player.inventory)
////                            && installedModulesOfType < CommonConfig.moduleConfig.getMaxModulesOfType(module.getCategory())));
//
//        } else {
//
//        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        ClickableItem selectedItem = targetItem.getSelectedItem();
        ClickableModule selectedModule = targetModule.getSelectedModule();
        AtomicBoolean handled = new AtomicBoolean(false);
        if (selectedItem != null && !selectedItem.getStack().isEmpty() && selectedModule != null && !selectedModule.getModule().isEmpty()) {
            selectedItem.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                if (cap instanceof IModularItem) {
                    if (((IModularItem) cap).isModuleInstalled(selectedModule.getModule().getItem().getRegistryName())) {
                        if (salvageButton.isEnabled() && salvageButton.hitBox(x, y)) {
                            doSalvage();
                            handled.set(true);
                        }
                    } else {
                        if (craftAndInstallButton.isEnabled() && craftAndInstallButton.hitBox(x, y)) {
                            doCraftAndInstall();
                        } else if (installButton.isEnabled() && installButton.hitBox(x, y)) {
                            doInstall();
                       }
                        handled.set(true);
                    }
                }
            });
        }
        return handled.get();
    }

    private void doCraftAndInstall() {


    }


    private void doSalvage() {
        ClickableModule module = targetModule.getSelectedModule();

//        if (!module.getModule().isEmpty()) {
//            ItemHandlerHelper.giveItemToPlayer(player, targetModule.getSelectedModule().getModule());
//
//            container.inventorySlots.indexOf();
//
//
//
//            container.getSlot(targetModule.getSelectedModule().getInventorySlot())


//            container.transferStackInSlot(player, module.getInventorySlot());
//            module.setInstalled(false);
//            container.detectAndSendChanges();




//            MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketSalvageModuleRequest(
//                    player.getEntityId(),
//                    targetItem.getSelectedItem().getSlotIndex(),
//                    module.getModule().getItem().getRegistryName().toString()));
//        }

    }

    /**
     * Performs all the functions associated with the install button. This
     * requires communicating with the server.
     */
    private void doInstall() {

        // TODO: packet move item from player inventory slot to modularItem inventory slot





        // FIXME: no more install costs


//        ItemStack module = targetModule.getSelectedModule().getModule();
//        ItemStack modularItem = targetItem.getSelectedItem().getStack();
//        if (!module.isEmpty() && !modularItem.isEmpty()) {
//            modularItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap ->{
//                if (cap instanceof IModularItem) {
//                    ItemStack result = module.copy();
//                    if (player.abilities.isCreativeMode) {
////                        result = ((IModularItem) cap).installModule(result);
//                        if (result.isEmpty()) {
//                            System.out.println("sending detect and send changes");
//                            targetItem.container.detectAndSendChanges();
//                        }
//                    } else {
//
//                    }
//                }
//            });
//
//
//
//
//
//            if (player.abilities.isCreativeMode /*|| MuseItemUtils.hasInInventory(ModuleManager.INSTANCE.getInstallCost(module.getDataName()), player.inventory)*/) {
//                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_INSTALL, 1);
//                // Now send request to server to make it legit
////                MPSPackets.CHANNEL_INSTANCE.sendToServer(
////                        new MusePacketInstallModuleRequest(
////                                targetModule.getSelectedModule().getSlotIndex(),
////                                module.getItem().getRegistryName().toString(),
////                                targetItem.getSelectedItem().getSlotIndex()
////                        ));
//            }
//        }
    }
}