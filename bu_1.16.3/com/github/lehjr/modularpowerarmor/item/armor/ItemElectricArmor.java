package com.github.lehjr.modularpowerarmor.item.armor;

import com.github.lehjr.modularpowerarmor.client.misc.AdditionalInfo;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Power handling base class for armor
 */
public class ItemElectricArmor extends ArmorItem {
    public ItemElectricArmor(EquipmentSlotType slots, Properties builder) {
        super(MPAArmorMaterial.EMPTY_ARMOR, slots, builder);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn != null) {
            AdditionalInfo.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {

    }
}