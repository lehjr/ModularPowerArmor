package com.github.lehjr.modularpowerarmor.item.armor;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.mpalib.basemod.ModObjects;
import com.github.lehjr.mpalib.util.string.AdditionalInfo;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractElectricItemArmor extends ArmorItem {
    public AbstractElectricItemArmor(EquipmentSlotType slots, Properties builder) {
        super(MPAArmorMaterial.EMPTY_ARMOR, slots, builder);
    }

    public AbstractElectricItemArmor(EquipmentSlotType slots) {
        super(MPAArmorMaterial.EMPTY_ARMOR, slots, new Item.Properties()
                .maxStackSize(1)
                .group(MPAObjects.creativeTab)
                .defaultMaxDamage(-1)
                .setNoRepair());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn != null) {
            AdditionalInfo.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        return null;
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {

    }
}