package com.github.lehjr.modularpowerarmor.client.gui.modding.module;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.util.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.util.client.render.MPALibRenderer;
import com.github.lehjr.mpalib.util.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.util.item.ItemUtils;
import com.github.lehjr.mpalib.util.math.Colour;
import com.github.lehjr.mpalib.util.string.StringUtils;
import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;

public class DetailedSummaryFrame extends ScrollableFrame {
    protected PlayerEntity player;
    protected int energy;
    protected double armor;
    protected ItemSelectionFrame itemSelectionFrame;

    public DetailedSummaryFrame(
                                PlayerEntity player,
                                Point2D topleft,
                                Point2D bottomright,
                                float zLevel,
                                Colour borderColour,
                                Colour insideColour,
                                ItemSelectionFrame itemSelectionFrame) {
        super(topleft, bottomright, zLevel, borderColour, insideColour);
        this.player = player;
        this.itemSelectionFrame = itemSelectionFrame;
    }

    /*
    Todo: break down by item:
    Energy current/max
    Armor Points
    Occupied slots/total (break down by specialty slots)








     */





    @Override
    public void update(double mousex, double mousey) {
        energy = ElectricItemUtils.getPlayerEnergy(player);
        armor = 0;

        for (ItemStack stack : ItemUtils.getModularItemsEquipped(player)) {
            energy += ElectricItemUtils.getItemEnergy(stack);
            AtomicDouble atomicArmor = new AtomicDouble(0);
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iModularItem -> {
                if (iModularItem instanceof IModularItem) {
                    for (ItemStack module: ((IModularItem) iModularItem).getInstalledModules()) {
                        module.getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(iPowerModule -> {
                            // FIXME NPE Armor

                            atomicArmor.getAndAdd(iPowerModule.applyPropertyModifiers(MPAConstants.ARMOR_VALUE_PHYSICAL));
                            atomicArmor.getAndAdd(iPowerModule.applyPropertyModifiers(MPAConstants.ARMOR_VALUE_ENERGY));
                        });
                    }
                }
            });

            armor += atomicArmor.get();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)  {
        if (player != null) {
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            int margin = 4;
            int nexty = (int) border.top() + margin;
            MPALibRenderer.drawCenteredString(matrixStack, I18n.format("gui.modularpowerarmor.equippedTotals"), (border.left() + border.right()) / 2, nexty);
            nexty += 10;

            // Max Energy
            String formattedValue = StringUtils.formatNumberFromUnits(energy, "FE");
            String name = I18n.format("gui.modularpowerarmor.energyStorage");
            double valueWidth = MPALibRenderer.getStringWidth(formattedValue);
            double allowedNameWidth = border.width() - valueWidth - margin * 2;
            List<String> namesList = StringUtils.wrapStringToVisualLength(name, allowedNameWidth);
            for (int i = 0; i < namesList.size(); i++) {
                MPALibRenderer.drawString(matrixStack, namesList.get(i), border.left() + margin, nexty + 9 * i);
            }
            MPALibRenderer.drawRightAlignedString(matrixStack, formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
            nexty += 10 * namesList.size() + 1;

            // Armor points
            formattedValue = StringUtils.formatNumberFromUnits(armor, "pts");
            name = I18n.format("gui.modularpowerarmor.armor");
            valueWidth = MPALibRenderer.getStringWidth(formattedValue);
            allowedNameWidth = border.width() - valueWidth - margin * 2;
            namesList = StringUtils.wrapStringToVisualLength(name, allowedNameWidth);
            assert namesList != null;
            for (int i = 0; i < namesList.size(); i++) {
                MPALibRenderer.drawString(matrixStack, namesList.get(i), border.left() + margin, nexty + 9 * i);
            }
            MPALibRenderer.drawRightAlignedString(matrixStack, formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
        }
    }
}
