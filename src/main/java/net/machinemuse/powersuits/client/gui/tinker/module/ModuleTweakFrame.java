package net.machinemuse.powersuits.client.gui.tinker.module;

import net.machinemuse.numina.basemod.NuminaConstants;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.machinemuse.numina.client.gui.clickable.ClickableTinkerSlider;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.scrollable.ScrollableFrame;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.nbt.MuseNBTUtils;
import net.machinemuse.numina.nbt.propertymodifier.IPropertyModifier;
import net.machinemuse.numina.nbt.propertymodifier.IPropertyModifierDouble;
import net.machinemuse.numina.nbt.propertymodifier.IPropertyModifierInteger;
import net.machinemuse.numina.nbt.propertymodifier.PropertyModifierLinearAdditiveDouble;
import net.machinemuse.numina.network.NuminaPackets;
import net.machinemuse.numina.network.packets.MusePacketTweakRequestDouble;
import net.machinemuse.numina.string.MuseStringUtils;
import net.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.*;

public class ModuleTweakFrame extends ScrollableFrame {
    protected static int margin = 4;
    protected ItemSelectionFrame itemTarget;
    protected ModuleSelectionFrame moduleTarget;
    protected List<ClickableTinkerSlider> sliders;
    protected Map<String, Double> propertyDoubleStrings;
    protected Map<String, Integer> propertyIntStrings;

    protected ClickableTinkerSlider selectedSlider;

    public ModuleTweakFrame(
            MusePoint2D topleft,
            MusePoint2D bottomright,
            Colour borderColour,
            Colour insideColour,
            ItemSelectionFrame itemTarget,
            ModuleSelectionFrame moduleTarget) {
        super(topleft, bottomright, borderColour, insideColour);
        this.itemTarget = itemTarget;
        this.moduleTarget = moduleTarget;
    }

    @Override
    public void update(double mousex, double mousey) {
        if (itemTarget.getSelectedItem() != null && moduleTarget.getSelectedModule() != null) {
            ItemStack stack = itemTarget.getSelectedItem().getStack();
            ItemStack module = moduleTarget.getSelectedModule().getModule();
            if (stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iItemHandler -> {
                if (iItemHandler instanceof IModularItem) {
                    return ((IModularItem) iItemHandler).isModuleInstalled(module.getItem().getRegistryName());
                }
                return false;
            }).orElse(false)) {
                loadTweaks(module);
            } else {
                sliders = null;
                propertyDoubleStrings = null;
            }
        } else {
            sliders = null;
            propertyDoubleStrings = null;
        }
        if (selectedSlider != null) {
            selectedSlider.setValueByX(mousex);
        }
    }

    String getUnit(String key) {
        if (moduleTarget.getSelectedModule() != null) {
            return moduleTarget.getSelectedModule().getModule().getCapability(PowerModuleCapability.POWER_MODULE)
                    .map(pm->{
                        return pm.getUnit(key);
                    }).orElse("");
        }
        return "";
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (sliders != null) {
            super.render(mouseX, mouseY, partialTicks);
            MuseRenderer.drawCenteredString("Tinker", (border.left() + border.right()) / 2, border.top() + 2);
            for (ClickableTinkerSlider slider : sliders) {
                slider.render(mouseX, mouseY, partialTicks);
            }
            int nexty = (int) (sliders.size() * 20 + border.top() + 23);
            for (Map.Entry<String, Double> property : propertyDoubleStrings.entrySet()) {
                String formattedValue = MuseStringUtils.formatNumberFromUnits(property.getValue(), getUnit(property.getKey()));
                String name = property.getKey();
                double valueWidth = MuseRenderer.getStringWidth(formattedValue);
                double allowedNameWidth = border.width() - valueWidth - margin * 2;

                List<String> namesList = MuseStringUtils.wrapStringToVisualLength(
                        I18n.format(NuminaConstants.MODULE_TRADEOFF_PREFIX + name), allowedNameWidth);
                for (int i = 0; i < namesList.size(); i++) {
                    MuseRenderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
                }
                MuseRenderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
                nexty += 9 * namesList.size() + 1;
            }

            for (Map.Entry<String, Integer> property: propertyIntStrings.entrySet()) {
                String formattedValue = MuseStringUtils.formatNumberFromUnits(property.getValue(), getUnit(property.getKey()));
                String name = property.getKey();
                double valueWidth = MuseRenderer.getStringWidth(formattedValue);
                double allowedNameWidth = border.width() - valueWidth - margin * 2;

                List<String> namesList = MuseStringUtils.wrapStringToVisualLength(
                        I18n.format(NuminaConstants.MODULE_TRADEOFF_PREFIX + name), allowedNameWidth);
                for (int i = 0; i < namesList.size(); i++) {
                    MuseRenderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
                }
                MuseRenderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
                nexty += 9 * namesList.size() + 1;
            }
        }
    }

    /**
     * Loads values that can be adjusted through the sliders
     * Also loads permanently set values for display
     *
     * @param module
     */
    private void loadTweaks(@Nonnull ItemStack module) {
        propertyDoubleStrings = new HashMap();
        Set<String> tweaks = new HashSet<String>();
        CompoundNBT moduleTag = MuseNBTUtils.getMuseModuleTag(module);
        module.getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(pm->{

            Map<String, List<IPropertyModifierDouble>> propertyModifiers = pm.getPropertyModifiers();
            for (Map.Entry<String, List<IPropertyModifierDouble>> property : propertyModifiers.entrySet()) {
                double currValue = 0;
                for (IPropertyModifier modifier : property.getValue()) {
                    currValue = (double) modifier.applyModifier(moduleTag, currValue);
                    if (modifier instanceof PropertyModifierLinearAdditiveDouble) {
                        tweaks.add(((PropertyModifierLinearAdditiveDouble) modifier).getTradeoffName());
                    }
                }
                propertyDoubleStrings.put(property.getKey(), currValue);
            }
        });

        sliders = new LinkedList();
        int y = 0;
        for (String tweak : tweaks) {
            y += 20;
            MusePoint2D center = new MusePoint2D(border.centerx(), border.top() + y);
            ClickableTinkerSlider slider = new ClickableTinkerSlider(
                    center,
                    border.finalRight() - border.finalLeft() - 16,
                    moduleTag,
                    tweak, new TranslationTextComponent(NuminaConstants.MODULE_TRADEOFF_PREFIX + tweak).getFormattedText());
            sliders.add(slider);
            if (selectedSlider != null && slider.hitBox(center.getX(), center.getY())) {
                selectedSlider = slider;
            }
        }

        /**
         * Loads values for display only. These values cannot be changed.
         * @param module
         */
        propertyIntStrings = new HashMap();
        module.getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(pm->{
            Map<String, List<IPropertyModifierInteger>> propertyModifiers = pm.getPropertyModifierBaseInt();
            for (Map.Entry<String, List<IPropertyModifierInteger>> property : propertyModifiers.entrySet()) {
                int currValue = 0;
                for (IPropertyModifier modifier : property.getValue()) {
                    currValue = (int) modifier.applyModifier(moduleTag, currValue);
                }
                propertyIntStrings.put(property.getKey(), currValue);
            }
        });
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean handled = false;
        if (button == 0) {
            if (sliders != null) {
                for (ClickableTinkerSlider slider : sliders) {
                    if (slider.hitBox(x, y)) {
                        selectedSlider = slider;
                        handled = true;
                        break;
                    }
                }
            }
        }
        return handled;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        boolean handled = false;
        if (selectedSlider != null && itemTarget.getSelectedItem() != null && moduleTarget.getSelectedModule() != null) {
            ClickableItem item = itemTarget.getSelectedItem();
            ItemStack module = moduleTarget.getSelectedModule().getModule();
            NuminaPackets.CHANNEL_INSTANCE.sendToServer(
                    new MusePacketTweakRequestDouble(item.inventorySlot, module.getItem().getRegistryName(), selectedSlider.id(), selectedSlider.getValue()));
            handled = true;
        }
        if (button == 0) {
            selectedSlider = null;
            handled = true;
        }
        return handled;
    }
}