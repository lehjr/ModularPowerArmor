package com.github.lehjr.modularpowerarmor.client.gui.tinker.module;

import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableItem;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableTinkerSlider;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.github.lehjr.mpalib.nbt.propertymodifier.IPropertyModifier;
import com.github.lehjr.mpalib.nbt.propertymodifier.PropertyModifierLinearAdditive;
import com.github.lehjr.mpalib.network.MPALibPackets;
import com.github.lehjr.mpalib.network.packets.TweakRequestDoublePacket;
import com.github.lehjr.mpalib.string.StringUtils;
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
    protected ClickableTinkerSlider selectedSlider;

    public ModuleTweakFrame(
            Point2F topleft,
            Point2F bottomright,
            float zLevel,
            Colour borderColour,
            Colour insideColour,
            ItemSelectionFrame itemTarget,
            ModuleSelectionFrame moduleTarget) {
        super(topleft, bottomright, zLevel, borderColour, insideColour);
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
                    .map(pm-> pm.getUnit(key)).orElse("");
        }
        return "";
    }

    @Override
    public void init(float left, float top, float right, float bottom) {
        super.init(left, top, right, bottom);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (sliders != null) {
            super.render(mouseX, mouseY, partialTicks);
            Renderer.drawCenteredString("Tinker", (border.left() + border.right()) / 2, border.top() + 2);
            for (ClickableTinkerSlider slider : sliders) {
                slider.render(mouseX, mouseY, partialTicks, getzLevel());
            }
            int nexty = (int) (sliders.size() * 20 + border.top() + 23);

            for (Map.Entry<String, Double> property : propertyDoubleStrings.entrySet()) {
                String formattedValue = StringUtils.formatNumberFromUnits(property.getValue(), getUnit(property.getKey()));
                String name = property.getKey();
                double valueWidth = Renderer.getStringWidth(formattedValue);
                double allowedNameWidth = border.width() - valueWidth - margin * 2;

                List<String> namesList = StringUtils.wrapStringToVisualLength(
                        I18n.format(MPALIbConstants.MODULE_TRADEOFF_PREFIX + name), allowedNameWidth);
                for (int i = 0; i < namesList.size(); i++) {
                    Renderer.drawString(namesList.get(i), border.left() + margin, nexty + 9 * i);
                }
                Renderer.drawRightAlignedString(formattedValue, border.right() - margin, nexty + 9 * (namesList.size() - 1) / 2);
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
        CompoundNBT moduleTag = NBTUtils.getModuleTag(module);
        module.getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(pm->{

            Map<String, List<IPropertyModifier>> propertyModifiers = pm.getPropertyModifiers();
            for (Map.Entry<String, List<IPropertyModifier>> property : propertyModifiers.entrySet()) {
                double currValue = 0;
                for (IPropertyModifier modifier : property.getValue()) {
                    currValue = (double) modifier.applyModifier(moduleTag, currValue);
                    if (modifier instanceof PropertyModifierLinearAdditive) {
                        tweaks.add(((PropertyModifierLinearAdditive) modifier).getTradeoffName());
                    }
                }
                propertyDoubleStrings.put(property.getKey(), currValue);
            }
        });

        sliders = new LinkedList();
        int y = 0;
        for (String tweak : tweaks) {
            y += 20;
            Point2F center = new Point2F(border.centerx(), border.top() + y);
            ClickableTinkerSlider slider = new ClickableTinkerSlider(
                    center,
                    border.finalRight() - border.finalLeft() - 16,
                    moduleTag,
                    tweak, new TranslationTextComponent(MPALIbConstants.MODULE_TRADEOFF_PREFIX + tweak).getFormattedText());
            sliders.add(slider);
            if (selectedSlider != null && slider.hitBox(center.getX(), center.getY())) {
                selectedSlider = slider;
            }
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        boolean handled = false;
        if (button == 0) {
            if (sliders != null) {
                for (ClickableTinkerSlider slider : sliders) {
                    if (slider.hitBox((float) x, (float)y)) {
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
            MPALibPackets.CHANNEL_INSTANCE.sendToServer(
                    new TweakRequestDoublePacket(item.inventorySlot, module.getItem().getRegistryName(), selectedSlider.id(), selectedSlider.getValue()));
            handled = true;
        }
        if (button == 0) {
            selectedSlider = null;
            handled = true;
        }
        return handled;
    }
}