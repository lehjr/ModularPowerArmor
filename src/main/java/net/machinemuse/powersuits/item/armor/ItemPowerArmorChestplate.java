package net.machinemuse.powersuits.item.armor;

import net.minecraft.inventory.EquipmentSlotType;

public class ItemPowerArmorChestplate extends ItemPowerArmor {
    public ItemPowerArmorChestplate(String regName) {
        super(EquipmentSlotType.CHEST);
        setRegistryName(regName);
    }
}