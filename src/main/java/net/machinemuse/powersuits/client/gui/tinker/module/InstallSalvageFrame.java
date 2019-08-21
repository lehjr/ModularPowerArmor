package net.machinemuse.powersuits.client.gui.tinker.module;

import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.client.gui.clickable.ClickableButton;
import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.machinemuse.numina.client.gui.slot.ClickableModuleSlot;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.scrollable.ScrollableFrame;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.client.gui.tinker.common.ItemSelectionFrame;
import net.machinemuse.powersuits.client.gui.tinker.common.ModuleSelectionFrame;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.network.MPSPackets;
import net.machinemuse.powersuits.network.packets.MusePacketInstallModuleRequest;
import net.machinemuse.powersuits.network.packets.MusePacketSalvageModuleRequest;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class InstallSalvageFrame extends ScrollableFrame {
    protected ItemSelectionFrame targetItem;
    protected ModuleSelectionFrame targetModule;
    protected ClickableButton installButton;
    protected ClickableButton salvageButton;
    protected PlayerEntity player;

    public InstallSalvageFrame(PlayerEntity player, MusePoint2D topleft,
                               MusePoint2D bottomright,
                               Colour borderColour, Colour insideColour,
                               ItemSelectionFrame targetItem, ModuleSelectionFrame targetModule) {
        super(topleft, bottomright, borderColour, insideColour);
        this.player = player;
        this.targetItem = targetItem;
        this.targetModule = targetModule;
        double sizex = border.right() - border.left();
        double sizey = border.bottom() - border.top();

        this.installButton = new ClickableButton(I18n.format("gui.powersuits.install"),
                new MusePoint2D(border.right() - sizex / 2.0, border.bottom() - sizey / 4.0),
                true);
        this.salvageButton = new ClickableButton(I18n.format("gui.powersuits.salvage"),
                new MusePoint2D(border.left() + sizex / 2.0, border.top() + sizey / 4.0),
                true);

    }

    @Override
    public void update(double x, double y) {
        super.update(x, y);


        double sizex = border.right() - border.left();
        double sizey = border.bottom() - border.top();
        this.installButton.setPosition(
                new MusePoint2D(border.right() - sizex / 2.0, border.bottom() - sizey / 4.0));
        this.installButton.setPosition(
                new MusePoint2D( border.right() - sizex / 2.0, border.bottom() - sizey / 4.0));
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null) {
            ClickableItem selectedItem = targetItem.getSelectedItem();
            ClickableModuleSlot selectedModule = targetModule.getSelectedModule();
            NonNullList<ItemStack> itemsToCheck = NonNullList.create(); // ModuleManager.INSTANCE.getInstallCost(module.getDataName());
            AtomicReference<Double> yoffset = new AtomicReference<>((double) 0);
            selectedItem.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                if (cap instanceof IModularItem) {
                    if(!((IModularItem) cap).isModuleInstalled(selectedModule.getStack().getItem().getRegistryName())) {
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
        }
    }

    private void drawBackground(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
    }

    private void drawItems(int mouseX, int mouseY, float partialTicks) {
        ClickableItem selectedItem = targetItem.getSelectedItem();
        ClickableModuleSlot selectedModule = targetModule.getSelectedModule();
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
        ClickableModuleSlot selectedModule = targetModule.getSelectedModule();
        selectedItem.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            if (cap instanceof IModularItem) {
                if(!((IModularItem) cap).isModuleInstalled(selectedModule.getStack().getItem().getRegistryName())) {



                    if (player.abilities.isCreativeMode)
                        installButton.setEnabled(true);

                    installButton.setVisible(true);
                    installButton.render(mouseX, mouseY, partialTicks);



                } else {
                    salvageButton.setVisible(true);

                    salvageButton.render(mouseX, mouseY, partialTicks);
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

//        if (border.containsPoint(x, y) && button == 0) {
//            installButton.setPosition(new MusePoint2D(x, y));
//            salvageButton.setPosition(new MusePoint2D(x, y));
//
//            double sizex = border.right() - border.left();
//            double sizey = border.bottom() - border.top();
//
//
//            System.out.println("clicked: " + new MusePoint2D(x, y));
//
//
//            System.out.println("target position: " +
//
//                    new MusePoint2D(border.right() - sizex / 2.0, border.bottom() - sizey / 4.0));
//
//
//        }





        ClickableItem selectedItem = targetItem.getSelectedItem();
        ClickableModuleSlot selectedModule = targetModule.getSelectedModule();
        if (selectedItem != null && !selectedItem.getStack().isEmpty()
                && selectedModule != null && !selectedModule.getStack().isEmpty()) {
            selectedItem.getStack().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                if (cap instanceof IModularItem) {
                    if(!((IModularItem) cap).isModuleInstalled(selectedModule.getStack().getItem().getRegistryName())) {
                        if (installButton.hitBox(x, y)) {
                            doInstall();
                        }
                    } else {
                        if (salvageButton.hitBox(x, y)) {
                            doSalvage();
                        }
                    }
                }
            });
        }
        return false;
    }

    private void doSalvage() {
        ClickableModuleSlot module = targetModule.getSelectedModule();

        if (!module.getStack().isEmpty())
            MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketSalvageModuleRequest(
                    player.getEntityId(),
                    targetItem.getSelectedItem().getSlotIndex(),
                    module.getStack().getItem().getRegistryName().toString()));
    }

    /**
     * Performs all the functions associated with the install button. This
     * requires communicating with the server.
     */
    private void doInstall() { // FIXME: no more install costs
        ItemStack module = targetModule.getSelectedModule().getStack();
        ItemStack modularItem = targetItem.getSelectedItem().getStack();
        if (!module.isEmpty() && !modularItem.isEmpty()) {
            modularItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap ->{
                if (cap instanceof IModularItem) {
                    ItemStack result = module.copy();
                    if (player.abilities.isCreativeMode) {
//                        result = ((IModularItem) cap).installModule(result);
                        if (result.isEmpty()) {
                            System.out.println("sending detect and send changes");
                            targetItem.container.detectAndSendChanges();
                        }
                    } else {

                    }
                }
            });





            if (player.abilities.isCreativeMode /*|| MuseItemUtils.hasInInventory(ModuleManager.INSTANCE.getInstallCost(module.getDataName()), player.inventory)*/) {
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_INSTALL, 1);
                // Now send request to server to make it legit
                MPSPackets.CHANNEL_INSTANCE.sendToServer(
                        new MusePacketInstallModuleRequest(
                                targetModule.getSelectedModule().getSlotIndex(),
                                module.getItem().getRegistryName().toString(),
                                targetItem.getSelectedItem().getSlotIndex()
                        ));
            }
        }
    }
}