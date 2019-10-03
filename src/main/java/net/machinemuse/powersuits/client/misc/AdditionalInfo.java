package net.machinemuse.powersuits.client.misc;

import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.energy.adapter.ElectricAdapter;
import net.machinemuse.numina.string.MuseStringUtils;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class AdditionalInfo {
    /**
     * Adds information to the item's tooltip when 'getting' it.
     *
     * @param stack            The itemstack to get the tooltip for
     * @param worldIn          The world (unused)
     * @param currentTipList   A list of strings containing the existing tooltip. When
     *                         passed, it will just contain the id of the item;
     *                         enchantments and lore are
     *                         appended afterwards.
     * @param advancedToolTips Whether or not the player has 'advanced tooltips' turned on in
     *                         their settings.
     */
    public static void addInformation(@Nonnull ItemStack stack, World worldIn, List currentTipList, ITooltipFlag advancedToolTips) {
        // TODO: remove enchantment labels.
        if (currentTipList.contains(I18n.format("silkTouch"))) {
            currentTipList.remove(I18n.format("silkTouch"));
        }

        // Mode changing item such as power fist
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
            if(iItemHandler instanceof IModeChangingItem) {
                ItemStack activeModule = ((IModeChangingItem) iItemHandler).getActiveModule();
                if (!activeModule.isEmpty()) {
                    ITextComponent localizedName = activeModule.getDisplayName();
                    currentTipList.add(
                            new TranslationTextComponent("tooltip.powersuits.mode").appendText(" ")
                            .appendSibling(localizedName.applyTextStyle(TextFormatting.RED)));
                } else {
                    currentTipList.add(new TranslationTextComponent("tooltip.powersuits.changeModes"));
                }
            }
        });

        ElectricAdapter adapter = ElectricAdapter.wrap(stack);
        if (adapter != null) {
            String energyinfo = I18n.format("tooltip.powersuits.energy") + " " +
                    MuseStringUtils.formatNumberShort(adapter.getEnergyStored()) + '/'
                    + MuseStringUtils.formatNumberShort(adapter.getMaxEnergyStored());
            currentTipList.add(new StringTextComponent(energyinfo).applyTextStyles(new TextFormatting[]{TextFormatting.AQUA, TextFormatting.ITALIC}));
        }

        if (doAdditionalInfo()) {
            stack.getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(pm->{
                if (pm.getCategory() == EnumModuleCategory.ARMOR) {
                    double pysArmor = pm.applyPropertyModifiers(MPSConstants.ARMOR_VALUE_PHYSICAL);
                    double energyArmor = pm.applyPropertyModifiers(MPSConstants.ARMOR_VALUE_ENERGY);
//                    double toughness = pm.applyPropertyModifiers(MPSConstants)
                    double knockbackResistance = pm.applyPropertyModifiers(MPSConstants.KNOCKBACK_RESISTANCE);





                }
            });





            // FIXME: fluids???
            // this is just some random info on the fluids installed
//            if (stack.getItem() instanceof ItemPowerArmorChestplate) {
//
//                // TODO: tooltip label for fluids if fluids found
//
//                // Water tank info
//                FluidUtils fluidUtils = new FluidUtils(player, stack, MPSModuleConstants.MODULE_BASIC_COOLING_SYSTEM__DATANAME);
//                List<String> fluidInfo = fluidUtils.getFluidDisplayString();
//                if (!fluidInfo.isEmpty())
//                    currentTipList.addAll(fluidInfo);
//
//                // advanced fluid tank info
//                fluidUtils = new FluidUtils(player, stack, MPSModuleConstants.MODULE_ADVANCED_COOLING_SYSTEM__DATANAME);
//                fluidInfo = fluidUtils.getFluidDisplayString();
//                if (!fluidInfo.isEmpty())
//                    currentTipList.addAll(fluidInfo);
//            }

            List<ITextComponent> installed = getItemInstalledModules(stack);
            if (installed.size() == 0) {
                String message = I18n.format("tooltip.powersuits.noModules");
                currentTipList.addAll(MuseStringUtils.wrapStringToLength(message, 30));
            } else {
                currentTipList.add(new TranslationTextComponent("tooltip.powersuits.installedModules"));
                currentTipList.addAll(installed);
            }




        } else {
            currentTipList.add(new TranslationTextComponent("tooltip.powersuits.pressShift")
                    .applyTextStyles(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}));
        }
    }

    public static String additionalInfoInstructions() {
        String message = I18n.format("tooltip.powersuits.pressShift");
        return MuseStringUtils.wrapMultipleFormatTags(message, MuseStringUtils.FormatCodes.Grey, MuseStringUtils.FormatCodes.Italic);
    }

    public static List<ITextComponent> getItemInstalledModules(@Nonnull ItemStack stack) {
        return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iItemHandler -> {
            List<ITextComponent> moduleNames = new ArrayList<>();

            if(iItemHandler instanceof IModularItem) {
                for (ItemStack module : ((IModularItem) iItemHandler).getInstalledModules()) {
                    moduleNames.add(module.getDisplayName().applyTextStyle(TextFormatting.LIGHT_PURPLE));
                }
            }
            return moduleNames;
        }).orElse(new ArrayList<>());
    }

    public static boolean doAdditionalInfo() {
        return InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);
    }
}