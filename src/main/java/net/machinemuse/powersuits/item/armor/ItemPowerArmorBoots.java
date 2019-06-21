package net.machinemuse.powersuits.item.armor;

import net.minecraft.inventory.EquipmentSlotType;

public class ItemPowerArmorBoots extends ItemPowerArmor {
    public ItemPowerArmorBoots(String regName) {
        super(EquipmentSlotType.FEET);
        setRegistryName(regName);
    }
}