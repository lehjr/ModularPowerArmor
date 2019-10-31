package net.machinemuse.powersuits.utils;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.energy.ElectricAdapterManager;
import com.github.lehjr.mpalib.energy.adapter.IElectricAdapter;
import com.github.lehjr.mpalib.legacy.item.IModeChangingItem;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.github.lehjr.mpalib.string.StringUtils;
import net.machinemuse.powersuits.api.constants.MPSModuleConstants;
import net.machinemuse.powersuits.common.ModuleManager;
import net.machinemuse.powersuits.common.config.MPSConfig;
import net.machinemuse.powersuits.item.armor.ItemPowerArmorChestplate;
import net.machinemuse.powersuits.utils.modulehelpers.FluidUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public abstract class MuseCommonStrings {
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
        if (worldIn == null || stack.isEmpty()) {
            return;
        }

        EntityPlayer player = Minecraft.getMinecraft().player;

        // TODO: remove enchantment labels.

        if (currentTipList.contains(I18n.format("silkTouch")))
            currentTipList.remove(I18n.format("silkTouch"));

        // Mode changing item such as power fist
        if (stack.getItem() instanceof IModeChangingItem) {
            String moduleDataName = NBTUtils.getStringOrNull(stack, MPALIbConstants.TAG_MODE);
            if (moduleDataName != null) {
                String localizedName = I18n.format("module.powersuits." + moduleDataName + ".name");
                currentTipList.add(I18n.format("tooltip.powersuits.mode") + " " + StringUtils.wrapFormatTags(localizedName, StringUtils.FormatCodes.Red));
            } else
                currentTipList.add(I18n.format("tooltip.powersuits.changeModes"));
        }

        IElectricAdapter adapter = ElectricAdapterManager.INSTANCE.wrap(stack, true);
        if (adapter != null) {
            String energyinfo = I18n.format("tooltip.powersuits.energy") + " " + StringUtils.formatNumberShort(adapter.getEnergyStored()) + '/'
                    + StringUtils.formatNumberShort(adapter.getMaxEnergyStored());
            currentTipList.add(StringUtils.wrapMultipleFormatTags(energyinfo, StringUtils.FormatCodes.Italic.character,
                    StringUtils.FormatCodes.Aqua));
        }
        if (MPSConfig.INSTANCE.doAdditionalInfo()) {
            // this is just some random info on the fluids installed
            if (stack.getItem() instanceof ItemPowerArmorChestplate) {

                // TODO: tooltip label for fluids if fluids found

                // Water tank info
                FluidUtils fluidUtils = new FluidUtils(player, stack, MPSModuleConstants.MODULE_BASIC_COOLING_SYSTEM__DATANAME);
                List<String> fluidInfo = fluidUtils.getFluidDisplayString();
                if (!fluidInfo.isEmpty())
                    currentTipList.addAll(fluidInfo);

                // advanced fluid tank info
                fluidUtils = new FluidUtils(player, stack, MPSModuleConstants.MODULE_ADVANCED_COOLING_SYSTEM__DATANAME);
                fluidInfo = fluidUtils.getFluidDisplayString();
                if (!fluidInfo.isEmpty())
                    currentTipList.addAll(fluidInfo);
            }

            List<String> installed = getItemInstalledModules(player, stack);
            if (installed.size() == 0) {
                String message = I18n.format("tooltip.powersuits.noModules");
                currentTipList.addAll(StringUtils.wrapStringToLength(message, 30));
            } else {
                currentTipList.add(I18n.format("tooltip.powersuits.installedModules"));
                for (String moduleName : installed) {
                    currentTipList.add(StringUtils.wrapFormatTags(moduleName, StringUtils.FormatCodes.Indigo));
                }
//                currentTipList.addAll(installed);
            }
        } else {
            currentTipList.add(additionalInfoInstructions());
        }
    }


    @SideOnly(Side.CLIENT)
    public static String additionalInfoInstructions() {
        String message = I18n.format("tooltip.powersuits.pressShift");
        return StringUtils.wrapMultipleFormatTags(message, StringUtils.FormatCodes.Grey, StringUtils.FormatCodes.Italic);
    }

    // //////////////////////// //
    // --- OTHER PROPERTIES --- //
    // //////////////////////// //
    public static double getOrSetModuleProperty(NBTTagCompound moduleTag, String propertyName, double defaultValue) {
        if (!moduleTag.hasKey(propertyName)) {
            moduleTag.setDouble(propertyName, defaultValue);
        }
        return moduleTag.getDouble(propertyName);
    }

    public static List<String> getItemInstalledModules(EntityPlayer player, ItemStack stack) {
        NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);
        List<String> modules = new LinkedList();
        for (IPowerModule module : ModuleManager.INSTANCE.getValidModulesForItem(stack)) {
            if (ModuleManager.INSTANCE.tagHasModule(itemTag, module.getDataName())) {
                modules.add(I18n.format(module.getUnlocalizedName() + ".name"));
            }
        }
        return modules;
    }
}
