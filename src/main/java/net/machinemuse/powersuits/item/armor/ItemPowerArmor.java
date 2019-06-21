package net.machinemuse.powersuits.item.armor;

import com.google.common.collect.Multimap;
import net.machinemuse.powersuits.basemod.MPSItems;
import net.machinemuse.powersuits.constants.MPSResourceConstants;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemPowerArmor extends ItemElectricArmor {
    public ItemPowerArmor(EquipmentSlotType slots) {
        super(slots, new Item.Properties().group(MPSItems.INSTANCE.creativeTab).maxStackSize(1).defaultMaxDamage(0));
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public IArmorMaterial getArmorMaterial() {
        return super.getArmorMaterial();
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack armor, Entity entity, EquipmentSlotType slot, String type) {
        if (type == "overlay")  // this is to allow a tint to be applied tot the armor
            return MPSResourceConstants.BLANK_ARMOR_MODEL_PATH;

        return slot == EquipmentSlotType.LEGS ? MPSResourceConstants.SEBK_AMROR_PANTS : MPSResourceConstants.SEBK_AMROR;
    }

    @Nullable
    @Override
    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel _default) {
//        // check if using 2d armor
//        if (!MPSNBTUtils.hasHighPolyModel(armor, armorSlot))
            return _default;
//
//        ModelBiped model = ArmorModelInstance.getInstance();
//        ((IArmorModel) model).setVisibleSection(armorSlot);
//
//        ItemStack chestPlate = armorSlot == EquipmentSlotType.CHEST ? armor : entityLiving.getItemStackFromSlot(EquipmentSlotType.CHEST);
//        if (chestPlate.getItem() instanceof ItemPowerArmorChestplate && ModuleManager.INSTANCE.itemHasActiveModule(chestPlate, MPSModuleConstants.MODULE_TRANSPARENT_ARMOR__DATANAME) ||
//                (armorSlot == EquipmentSlotType.CHEST && ModuleManager.INSTANCE.itemHasActiveModule(chestPlate, MPSModuleConstants.MODULE_ACTIVE_CAMOUFLAGE__DATANAME))) {
//            ((IArmorModel) model).setVisibleSection(null);
//        } else
//            ((IArmorModel) model).setRenderSpec(MPSNBTUtils.getMuseRenderTag(armor, armorSlot));
//        return model;
    }
}
