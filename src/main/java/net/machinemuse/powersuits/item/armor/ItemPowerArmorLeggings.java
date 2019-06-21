package net.machinemuse.powersuits.item.armor;

import net.minecraft.inventory.EquipmentSlotType;

public class ItemPowerArmorLeggings extends ItemPowerArmor {
    public ItemPowerArmorLeggings(String regName) {
        super(EquipmentSlotType.LEGS);
        setRegistryName(regName);
    }
}