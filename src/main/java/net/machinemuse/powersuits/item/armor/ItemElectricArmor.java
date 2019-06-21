package net.machinemuse.powersuits.item.armor;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;

/**
 * Power handling base class for armor
 */
public class ItemElectricArmor extends ArmorItem {
    public ItemElectricArmor(EquipmentSlotType slots, Properties builder) {
        super(MPSArmorMaterial.EMPTY_ARMOR, slots, builder);
    }

//    @Nullable
//    @Override
//    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
//        return new MPSCapProvider(stack);
//    }
}

