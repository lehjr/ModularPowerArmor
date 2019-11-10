package com.github.lehjr.modularpowerarmor.client.gui.tinker.module;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.string.StringUtils;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;
import java.util.Optional;

public class DetailedSummaryFrame extends ScrollableFrame {
    protected EntityPlayer player;
    protected int energy;
    protected double armor;
    protected ItemSelectionFrame itemSelectionFrame;

    public DetailedSummaryFrame(
            EntityPlayer player,
            Point2D topleft,
            Point2D bottomright,
            Colour borderColour,
            Colour insideColour,
            ItemSelectionFrame itemSelectionFrame) {
        super(topleft, bottomright, borderColour, insideColour);
        this.player = player;
        this.itemSelectionFrame = itemSelectionFrame;
    }

    @Override
    public void update(double mousex, double mousey) {
        energy = ElectricItemUtils.getPlayerEnergy(player);
        armor = 0;

        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            ItemStack stack = player.getItemStackFromSlot(slot);
            energy += ElectricItemUtils.getItemEnergy(stack);
            AtomicDouble atomicArmor = new AtomicDouble(0);
            Optional.ofNullable(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(iModularItem -> {
                if (iModularItem instanceof IModularItem) {
                    for (ItemStack module : ((IModularItem) iModularItem).getInstalledModules()) {
                        Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).ifPresent(iPowerModule -> {
                            // FIXME NPE Armor

                            atomicArmor.getAndAdd(iPowerModule.applyPropertyModifiers(Constants.ARMOR_VALUE_PHYSICAL));
                            atomicArmor.getAndAdd(iPowerModule.applyPropertyModifiers(Constants.ARMOR_VALUE_ENERGY));
                        });
                    }
                }
            });
            armor += atomicArmor.get();
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (player != null) {
            super.render(mouseX, mouseY, partialTicks);
            int margin = 4;
            int nexty = (int) border.top() + margin;
            Renderer.drawCenteredString(I18n.format("gui.modularpowerarmor.equippedTotals"), (border.left() + border.right()) / 2, nexty);
            nexty += 10;

            // Max Energy
            String formattedValue = StringUtils.formatNumberFromUnits(energy, "RF");
            String name = I18n.format("gui.modularpowerarmor.energyStorage");
            double valueWidth = Renderer.getStringWidth(formattedValue);
            double allowedNameWidth = border.width() - valueWidth - margin * 2;
            List<String> namesList = StringUtils.wrapStringToVisualLength(name, allowedNameWidth);
            for (int i = 0; i < namesList.size(); i++) {
                Renderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
            }
            Renderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
            nexty += 10 * namesList.size() + 1;

            // Armor points
            formattedValue = StringUtils.formatNumberFromUnits(armor, "pts");
            name = I18n.format("gui.modularpowerarmor.armor");
            valueWidth = Renderer.getStringWidth(formattedValue);
            allowedNameWidth = border.width() - valueWidth - margin * 2;
            namesList = StringUtils.wrapStringToVisualLength(name, allowedNameWidth);
            assert namesList != null;
            for (int i = 0; i < namesList.size(); i++) {
                Renderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
            }
            Renderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
        }
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        return null;
    }
}