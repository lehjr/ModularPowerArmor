//package com.github.lehjr.modularpowerarmor.item.armor;
//
//import com.github.lehjr.mpalib.basemod.MPALibLogger;
//import com.github.lehjr.mpalib.basemod.MPALibConstants;
//import com.github.lehjr.mpalib.client.render.modelspec.ModelRegistry;
//import com.github.lehjr.mpalib.client.render.modelspec.TexturePartSpec;
//import com.github.lehjr.mpalib.math.Colour;
//import com.github.lehjr.mpalib.string.StringUtils;
//import com.github.lehjr.modularpowerarmor.client.model.helper.MPSModelHelper;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundNBT;
//
///**
// * Author: MachineMuse (Claire Semple)
// * Created: 7:49 PM, 4/23/13
// * <p>
// * Ported to Java by lehjr on 11/4/16.
// */
//public interface IModularItemBase {
//    default Colour getColorFromItemStack(ItemStack stack) {
//        try {
//            CompoundNBT renderTag = MPSModelHelper.getRenderTag(stack);
//            if (renderTag.contains(MPALibConstants.NBT_TEXTURESPEC_TAG)) {
//                TexturePartSpec partSpec = (TexturePartSpec) ModelRegistry.getInstance().getPart(renderTag.getCompound(MPALibConstants.NBT_TEXTURESPEC_TAG));
//                CompoundNBT specTag = renderTag.getCompound(MPALibConstants.NBT_TEXTURESPEC_TAG);
//                int index = partSpec.getColourIndex(specTag);
//                int[] colours = renderTag.getIntArray(MPALibConstants.TAG_COLOURS);
//                if (colours.length > index)
//                    return new Colour(colours[index]);
//            }
//        } catch (Exception e) {
//            MPALibLogger.logException("something failed here: ", e);
//        }
//        return Colour.WHITE;
//    }
//
//    default String formatInfo(String string, double value) {
//        return string + '\t' + StringUtils.formatNumberShort(value);
//    }
//
//    default double getArmorDouble(PlayerEntity player, ItemStack stack) {
//        return 0;
//    }
//}