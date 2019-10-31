package net.machinemuse.powersuits.utils.nbt;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.client.render.modelspec.ModelRegistry;
import com.github.lehjr.mpalib.client.render.modelspec.TexturePartSpec;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.machinemuse.powersuits.api.constants.MPSResourceConstants;
import net.machinemuse.powersuits.client.render.modelspec.DefaultModelSpec;
import net.machinemuse.powersuits.common.config.MPSConfig;
import net.machinemuse.powersuits.item.armor.ItemPowerArmor;
import net.machinemuse.powersuits.item.tool.ItemPowerFist;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.Objects;



public class MPSNBTUtils {
    public static NBTTagCompound getMuseRenderTag(@Nonnull ItemStack stack, EntityEquipmentSlot armorSlot) {
        NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);
        NBTTagCompound renderTag = new NBTTagCompound();
        if (itemTag.hasKey(MPALIbConstants.TAG_RENDER, Constants.NBT.TAG_COMPOUND))
            renderTag = itemTag.getCompoundTag(MPALIbConstants.TAG_RENDER);
        else if (itemTag.hasKey(MPALIbConstants.TAG_COSMETIC_PRESET, Constants.NBT.TAG_STRING))
            renderTag = MPSConfig.INSTANCE.getPresetNBTFor(stack, itemTag.getString(MPALIbConstants.TAG_COSMETIC_PRESET));

        if (renderTag != null && !renderTag.isEmpty())
            return renderTag;

        // if cosmetic presets are to be used, or powerfist customization is not allowed set to default preset
        if (!MPSConfig.INSTANCE.useLegacyCosmeticSystem() ||
                (stack.getItem() instanceof ItemPowerFist && MPSConfig.INSTANCE.allowPowerFistCustomization())) {
            itemTag.setString(MPALIbConstants.TAG_COSMETIC_PRESET, "Default");
            return MPSConfig.INSTANCE.getPresetNBTFor(stack, "Default");
        }

        renderTag = DefaultModelSpec.makeModelPrefs(stack, armorSlot);
        itemTag.setTag(MPALIbConstants.TAG_RENDER, renderTag);
        return renderTag;
    }

    public static NBTTagCompound getMuseRenderTag(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ItemPowerArmor)
                return getMuseRenderTag(stack, ((ItemPowerArmor) stack.getItem()).armorType);
            if (stack.getItem() instanceof ItemPowerFist)
                return getMuseRenderTag(stack, EntityEquipmentSlot.MAINHAND);
        }
        return new NBTTagCompound();
    }

    public static String getArmorTexture(ItemStack stack, EntityEquipmentSlot slot) {
        NBTTagCompound renderTag = getMuseRenderTag(stack, slot);
        try {
            TexturePartSpec partSpec = (TexturePartSpec) ModelRegistry.getInstance().getPart(renderTag.getCompoundTag(MPALIbConstants.NBT_TEXTURESPEC_TAG));
            return partSpec.getTextureLocation();
        } catch (Exception ignored) {
            return MPSResourceConstants.BLANK_ARMOR_MODEL_PATH;
        }
    }

    public static boolean hasHighPolyModel(ItemStack stack, EntityEquipmentSlot slot) {
        NBTTagCompound renderTag = getMuseRenderTag(stack, slot);

        // any tag other than the colours or texSpec tag is a ModelPartSpec tag
        for (String tagName : renderTag.getKeySet()) {
            if (Objects.equals(tagName, MPALIbConstants.NBT_TEXTURESPEC_TAG) || Objects.equals(tagName, MPALIbConstants.TAG_COLOURS))
                continue;
            else
                return true;
        }
        return false;
    }
}