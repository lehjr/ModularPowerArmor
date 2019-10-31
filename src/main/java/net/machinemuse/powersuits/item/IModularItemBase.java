package net.machinemuse.powersuits.item;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.client.render.modelspec.ModelRegistry;
import com.github.lehjr.mpalib.client.render.modelspec.TexturePartSpec;
import com.github.lehjr.mpalib.legacy.energy.IElectricItem;
import com.github.lehjr.mpalib.legacy.item.IModularItem;
import com.github.lehjr.mpalib.legacy.module.IModuleManager;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.string.StringUtils;
import net.machinemuse.powersuits.common.ModuleManager;
import net.machinemuse.powersuits.common.config.MPSConfig;
import net.machinemuse.powersuits.utils.nbt.MPSNBTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 7:49 PM, 4/23/13
 * <p>
 * Ported to Java by lehjr on 11/4/16.
 */
public interface IModularItemBase extends IModularItem, IElectricItem {
    @Override
    default double getMaxBaseHeat(@Nonnull ItemStack itemStack) {
        return MPSConfig.INSTANCE.getBaseMaxHeat(itemStack);
    }

    default Colour getColorFromItemStack(@Nonnull ItemStack stack) {
        try {
            NBTTagCompound renderTag = MPSNBTUtils.getMuseRenderTag(stack);
            if (renderTag.hasKey(MPALIbConstants.NBT_TEXTURESPEC_TAG)) {
                TexturePartSpec partSpec = (TexturePartSpec) ModelRegistry.getInstance().getPart(renderTag.getCompoundTag(MPALIbConstants.NBT_TEXTURESPEC_TAG));
                NBTTagCompound specTag = renderTag.getCompoundTag(MPALIbConstants.NBT_TEXTURESPEC_TAG);
                int index = partSpec.getColourIndex(specTag);
                int[] colours = renderTag.getIntArray(MPALIbConstants.TAG_COLOURS);
                if (colours.length > index)
                    return new Colour(colours[index]);
            }
        } catch (Exception e) {
            MPALibLogger.logException("something failed here: ", e);
        }
        return Colour.WHITE;
    }

    default String formatInfo(String string, double value) {
        return string + '\t' + StringUtils.formatNumberShort(value);
    }

    @Override
    default IModuleManager getModuleManager() {
        return ModuleManager.INSTANCE;
    }

    default double getArmorDouble(EntityPlayer player, ItemStack stack) {
        return 0;
    }
}