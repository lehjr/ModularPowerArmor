package net.machinemuse.powersuits.item.armor;

import com.google.common.collect.Multimap;
import net.machinemuse.powersuits.basemod.MPSItems;
import net.machinemuse.powersuits.constants.MPSResourceConstants;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

public class ItemPowerArmor extends ItemElectricArmor {
    int damageReduceAmount = 0;
    float toughness = 0;

    public ItemPowerArmor(EquipmentSlotType slots) {
        super(slots, new Item.Properties().group(MPSItems.INSTANCE.creativeTab).maxStackSize(1).defaultMaxDamage(0));
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
        if (equipmentSlot == this.slot) {
            multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", (double)getDamageReduceAmount(), AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", (double)getToughness(), AttributeModifier.Operation.ADDITION));
        }
        return multimap;
    }

    public int getDamageReduceAmount() {
        return this.damageReduceAmount;
    }

    public void setDamageReduceAmount(int damageReduceAmount) {
        this.damageReduceAmount = damageReduceAmount;
    }

    public float getToughness() {
        return this.toughness;
    }

    public void setToughness(float toughness) {
        this.toughness = toughness;
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
