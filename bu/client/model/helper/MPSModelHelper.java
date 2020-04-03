package com.github.lehjr.modularpowerarmor.client.model.helper;

import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraftforge.client.event.TextureStitchEvent;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.ArrayList;

public class MPSModelHelper {
    // One pass just to register the textures called from texture stitch event
    // another to register the models called from model bake event (second run)
    public static void loadArmorModels(@Nullable TextureStitchEvent.Pre event, ModelBakery bakery) {
        ArrayList<String> resourceList = new ArrayList<String>() {{
            add("/assets/modularpowerarmor/modelspec/armor2.xml");
            add("/assets/modularpowerarmor/modelspec/default_armor.xml");
            add("/assets/modularpowerarmor/modelspec/default_armorskin.xml");
            add("/assets/modularpowerarmor/modelspec/armor_skin2.xml");
            add("/assets/modularpowerarmor/modelspec/default_powerfist.xml");
        }};

        for (String resourceString : resourceList) {
            parseSpecFile(resourceString, event, bakery);
        }
//
//        URL resource = MPSModelHelper.class.getResource("/assets/modularpowerarmor/models/item/armor/modelspec.xml");
//        ModelSpecXMLReader.INSTANCE.parseFile(resource, event);
//        URL otherResource = MPSModelHelper.class.getResource("/assets/modularpowerarmor/models/item/armor/armor2.xml");
//        ModelSpecXMLReader.INSTANCE.parseFile(otherResource, event);

//        ModelPowerFistHelper.INSTANCE.loadPowerFistModels(event);
    }

    public static void parseSpecFile(String resourceString, @Nullable TextureStitchEvent.Pre event, ModelBakery bakery) {
        URL resource = MPSModelHelper.class.getResource(resourceString);
        ModelSpecXMLReader.INSTANCE.parseFile(resource, event, bakery);
    }

//    public static boolean hasHighPolyModel(ItemStack stack, EquipmentSlotType slot) {
//        CompoundNBT renderTag = getRenderTag(stack, slot);
//
//        // any tag other than the colours or texSpec tag is a ModelPartSpec tag
//        for (String tagName : renderTag.keySet()) {
//            if (Objects.equals(tagName, MPALIbConstants.NBT_TEXTURESPEC_TAG) || Objects.equals(tagName, MPALIbConstants.TAG_COLOURS))
//                continue;
//            else
//                return true;
//        }
//        return false;
//    }

//    public static CompoundNBT getRenderTag(@Nonnull ItemStack stack, EquipmentSlotType armorSlot) {
//        CompoundNBT itemTag = NBTUtils.getMuseItemTag(stack);
//        CompoundNBT renderTag = new CompoundNBT();
////        if (itemTag.contains(MPALIbConstants.TAG_RENDER, Constants.NBT.TAG_COMPOUND))
////            renderTag = itemTag.getCompound(MPALIbConstants.TAG_RENDER);
////        else if (itemTag.contains(MPALIbConstants.TAG_COSMETIC_PRESET, Constants.NBT.TAG_STRING))
////            renderTag = CommonConfig.moduleConfig.getPresetNBTFor(stack, itemTag.getString(MPALIbConstants.TAG_COSMETIC_PRESET));
////
////        if (renderTag != null)
////            return renderTag;
////
////        // if cosmetic presets are to be used, or powerfist customization is not allowed set to default preset
////        if (!CommonConfig.moduleConfig.COSMETIC_USE_LEGACY_COSMETIC_SYSTEM.get() ||
////                (stack.getItem() instanceof ItemPowerFist && CommonConfig.moduleConfig.COSMETIC_ALLOW_POWER_FIST_CUSTOMIZATOIN.get())) {
////            itemTag.putString(MPALIbConstants.TAG_COSMETIC_PRESET, "Default");
////            return CommonConfig.moduleConfig.getPresetNBTFor(stack, "Default");
////        }
////
////        renderTag = DefaultModelSpec.makeModelPrefs(stack, armorSlot);
////        itemTag.put(MPALIbConstants.TAG_RENDER, renderTag);
//        return renderTag;
//    }

//    public static CompoundNBT getRenderTag(@Nonnull ItemStack stack) {
//        EquipmentSlotType slot = stack.getEquipmentSlot();
//
//        if (slot != null)
//            return getRenderTag(stack, slot);
//
//        if (!stack.isEmpty()) {
//            if (stack.getItem() instanceof ItemPowerArmor)
//                return getRenderTag(stack, ((ItemPowerArmor) stack.getItem()).getEquipmentSlot());
//            if (stack.getItem() instanceof ItemPowerFist)
//                return getRenderTag(stack, EquipmentSlotType.MAINHAND);
//        }
//        return new CompoundNBT();
//    }

//    public static String getArmorTexture(ItemStack stack, EquipmentSlotType slot) {
//        CompoundNBT renderTag = getRenderTag(stack, slot);
//        try {
//            TexturePartSpec partSpec = (TexturePartSpec) ModelRegistry.getInstance().getPart(renderTag.getCompound(MPALIbConstants.NBT_TEXTURESPEC_TAG));
//            return partSpec.getTextureLocation();
//        } catch (Exception ignored) {
//            return MPALIbConstants.BLANK_ARMOR_MODEL_PATH;
//        }
//    }
}