package com.github.lehjr.modularpowerarmor.basemod;

import com.github.lehjr.modularpowerarmor.common.MPSItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Ported to Java by lehjr on 11/3/16.
 */
public class CreativeTab extends CreativeTabs {
    public CreativeTab() {
        super("modularpowerarmor");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        return new ItemStack(MPSItems.powerArmorHead);
    }
}