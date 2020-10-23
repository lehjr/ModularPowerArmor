package com.github.lehjr.modularpowerarmor.basemod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MPACreativeTab extends ItemGroup {
    public MPACreativeTab() {
        super(MPAConstants.MOD_ID);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack createIcon() {
        Item item = MPAObjects.POWER_ARMOR_HELMET.get();
        return new ItemStack(item);
    }
}