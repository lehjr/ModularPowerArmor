package net.machinemuse.powersuits.item.component;

import net.machinemuse.powersuits.event.RegisterStuff;
import net.minecraft.item.Item;

public class ItemComponent extends Item {
    public ItemComponent(String regName) {
        super(new Properties().group(RegisterStuff.INSTANCE.creativeTab));
        setRegistryName(regName);
    }
}