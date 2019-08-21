package net.machinemuse.powersuits.item.armor;

import net.machinemuse.powersuits.client.misc.AdditionalInfo;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Power handling base class for armor
 */
public class ItemElectricArmor extends ArmorItem {
    public ItemElectricArmor(EquipmentSlotType slots, Properties builder) {
        super(MPSArmorMaterial.EMPTY_ARMOR, slots, builder);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn != null)
            AdditionalInfo.addInformation(stack, worldIn, tooltip, flagIn);
    }
}