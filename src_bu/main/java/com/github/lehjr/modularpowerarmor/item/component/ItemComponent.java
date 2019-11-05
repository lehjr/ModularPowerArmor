package com.github.lehjr.modularpowerarmor.item.component;

import com.github.lehjr.modularpowerarmor.event.RegisterStuff;
import net.minecraft.item.Item;

public class ItemComponent extends Item {
    public ItemComponent(String regName) {
        super(new Properties().group(RegisterStuff.INSTANCE.creativeTab));
        setRegistryName(regName);
    }
}