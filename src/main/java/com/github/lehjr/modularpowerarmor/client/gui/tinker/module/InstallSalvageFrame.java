package com.github.lehjr.modularpowerarmor.client.gui.tinker.module;

import com.github.lehjr.modularpowerarmor.client.gui.clickable.ClickableItem;
import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.modularpowerarmor.client.sound.SoundDictionary;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.network.packets.InstallModuleRequestPacket;
import com.github.lehjr.modularpowerarmor.network.packets.SalvageModuleRequestPacket;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableButton;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.string.StringUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class InstallSalvageFrame extends ScrollableFrame {
    protected ItemSelectionFrame targetItem;
    protected ModuleSelectionFrame targetModule;
    protected ClickableButton installButton;
    protected ClickableButton salvageButton;
    protected EntityPlayer player;

    public InstallSalvageFrame(EntityPlayer player,
                               Point2D topleft,
                               Point2D bottomright,
                               Colour backgroundColour, Colour borderColour,
                               ItemSelectionFrame targetItem, ModuleSelectionFrame targetModule) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.player = player;
        this.targetItem = targetItem;
        this.targetModule = targetModule;
        double sizex = border.right() - border.left();
        double sizey = border.bottom() - border.top();

        /** Install button -------------------------------------------------------------------------------------------- */
        this.installButton = new ClickableButton(I18n.format("gui.modularpowerarmor.install"),
                new Point2D(border.right() - sizex / 2.0, border.bottom() - sizey / 4.0),
                true);

        this.installButton.setOnPressed(press->{
            if (targetItem.getSelectedItem() == null || targetModule.getSelectedModule() == null)
                return;
            IPowerModule module = targetModule.getSelectedModule().getModule();
            if (player.capabilities.isCreativeMode || ItemUtils.hasInInventory(ModuleManager.INSTANCE.getInstallCost(module.getDataName()), player.inventory)) {
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_INSTALL, SoundCategory.BLOCKS, 1, null);
                // Now send request to server to make it legit
                MPAPackets.sendToServer(new InstallModuleRequestPacket(
                        targetItem.getSelectedItem().inventorySlot,
                        module.getDataName()));
            }
        });

        /** Salvage button -------------------------------------------------------------------------------------------- */
        this.salvageButton = new ClickableButton(I18n.format("gui.modularpowerarmor.salvage"),
                new Point2D(border.left() + sizex / 2.0, border.top() + sizey / 4.0),
                true);
        this.salvageButton.setOnPressed(pressed->{
            if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null) {

                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_INSTALL, SoundCategory.MASTER,  1, player.getPosition());
                IPowerModule module = targetModule.getSelectedModule().getModule();
                MPAPackets.sendToServer(new SalvageModuleRequestPacket(
                        targetItem.getSelectedItem().inventorySlot,
                        module.getDataName()));
            }
        });
    }

    @Override
    public void update(double x, double y) {
        super.update(x, y);

        double sizex = border.right() - border.left();
        double sizey = border.bottom() - border.top();

        this.installButton.setPosition(
                new Point2D(border.right() - sizex / 2.0, border.bottom() - sizey / 4.0));
        this.salvageButton.setPosition(
                new Point2D(border.left() + sizex / 2.0, border.top() + sizey / 4.0));

        if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null) {
            ItemStack stack = targetItem.getSelectedItem().getItem();
            IPowerModule module = targetModule.getSelectedModule().getModule();
            if (!ModuleManager.INSTANCE.itemHasModule(stack, module.getDataName())) {
                int installedModulesOfType = ModuleManager.INSTANCE.getNumberInstalledModulesOfType(stack, module.getCategory());
                installButton.show();
                installButton.setEnabled(player.capabilities.isCreativeMode ||
                        (ItemUtils.hasInInventory(ModuleManager.INSTANCE.getInstallCost(module.getDataName()), player.inventory) &&
                                installedModulesOfType < MPAConfig.INSTANCE.getMaxModulesOfType(module.getCategory())));
                salvageButton.disableAndHide();
            } else {
                salvageButton.enableAndShow();
                installButton.disableAndHide();
            }
        } else  {
            salvageButton.disableAndHide();
            installButton.disableAndHide();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null) {
            drawBackground(mouseX, mouseY, partialTicks);
            drawItems(mouseX, mouseY, partialTicks);
            drawButtons(mouseX, mouseY, partialTicks);
        }
    }

    private void drawItems(int mouseX, int mouseY, float partialTicks) {
        ItemStack stack = targetItem.getSelectedItem().getItem();
        IPowerModule module = targetModule.getSelectedModule().getModule();
        NonNullList<ItemStack> itemsToDraw = ModuleManager.INSTANCE.getInstallCost(module.getDataName());
        double yoffset;
        if (!ModuleManager.INSTANCE.itemHasModule(stack, module.getDataName())) {
            yoffset = border.top() + 4;
        } else {
            yoffset = border.bottom() - 20;
        }
        double xoffset = -8.0 * itemsToDraw.size()
                + (border.left() + border.right()) / 2;
        int i = 0;
        for (ItemStack costItem : itemsToDraw) {
            Renderer.drawItemAt(
                    16 * i++ + xoffset,
                    yoffset,
                    costItem);
        }
    }

    private void drawBackground(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onMouseDown(double x, double y, int button) {
        ClickableItem selItem = targetItem.getSelectedItem();
        ClickableModule selModule = targetModule.getSelectedModule();
        if (selItem != null && selModule != null) {
            ItemStack stack = selItem.getItem();
            IPowerModule module = selModule.getModule();

            if (installButton.hitBox(x, y)) {
                installButton.onPressed();
            }

            if (salvageButton.hitBox(x, y)) {
                salvageButton.onPressed();
            }
        }
    }

    private void drawButtons(int mouseX, int mouseY, float partialTicks) {
        salvageButton.render(mouseX, mouseY, partialTicks);
        installButton.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        if (targetItem.getSelectedItem() != null && targetModule.getSelectedModule() != null) {
            ItemStack stack = targetItem.getSelectedItem().getItem();
            IPowerModule module = targetModule.getSelectedModule().getModule();
            NonNullList<ItemStack> itemsToCheck = ModuleManager.INSTANCE.getInstallCost(module.getDataName());
            double yoffset;
            if (!ModuleManager.INSTANCE.itemHasModule(stack, module.getDataName())) {
                yoffset = border.top() + 4;
            } else {
                yoffset = border.bottom() - 20;
            }
            if (yoffset + 16 > y && yoffset < y) {
                double xoffset = -8.0 * itemsToCheck.size() + (border.left() + border.right()) / 2;
                if (xoffset + 16 * itemsToCheck.size() > x && xoffset < x) {
                    int index = (int) (x - xoffset) / 16;
                    return itemsToCheck.get(index).getTooltip(player, ITooltipFlag.TooltipFlags.NORMAL);
                }
            }
        }
        return null;
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        String ret = null;
        if (salvageButton.isVisible() && salvageButton.hitBox(x, y)) {
            ret = I18n.format("gui.modularpowerarmor.salvage.desc");
        }
        if (installButton.isVisible() && installButton.hitBox(x, y)) {
            if (installButton.isEnabled() && player.capabilities.isCreativeMode) {
                ret = I18n.format("gui.modularpowerarmor.install.creative.desc");
            } else if (installButton.isEnabled()) {
                Collections.singletonList(I18n.format("gui.modularpowerarmor.install.desc"));
            } else {
                // todo: tell user why disabled...
                ret = I18n.format("gui.modularpowerarmor.install.disabled.desc");
            }
        }

        if (craftAndInstallButton.isVisible() && craftAndInstallButton.hitBox(x, y)) {
            if (craftAndInstallButton.isEnabled()) {
                ret = I18n.format("gui.modularpowerarmor.craftAndInstall.desc");
            } else {
                ret = I18n.format("gui.modularpowerarmor.craftAndInstall.disabled.desc");
            }
        }

        if (ret != null) {
            return StringUtils.wrapITextComponentToLength(ret, 30);
        }
        return null;
    }


}