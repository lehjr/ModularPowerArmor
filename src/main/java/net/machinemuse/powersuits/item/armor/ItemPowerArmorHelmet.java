package net.machinemuse.powersuits.item.armor;

import net.minecraft.inventory.EquipmentSlotType;

public class ItemPowerArmorHelmet extends ItemPowerArmor {
    public ItemPowerArmorHelmet(String regName) {
        super(EquipmentSlotType.HEAD);
        setRegistryName(regName);
    }
}
