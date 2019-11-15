package com.github.lehjr.modularpowerarmor.item.armor;

import com.github.lehjr.modularpowerarmor.utils.AdditionalInfo;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Ported to Java by lehjr on 10/26/16.
 */
public abstract class ItemElectricArmor extends ItemArmor implements ISpecialArmor {
    public ItemElectricArmor(ItemArmor.ArmorMaterial material, int renderIndexIn, EntityEquipmentSlot slot) {
        super(material, renderIndexIn, slot);
    }

    @Override
    public boolean hasColor(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public int getColor(@Nonnull ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(ModelSpecNBTCapability.RENDER, null))
                .map(ms->ms.getColorFromItemStack().getInt()).orElse(Colour.WHITE.getInt());
    }

    @Override
    public boolean hasOverlay(@Nonnull ItemStack stack) {
        return super.hasOverlay(stack);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> currentTipList, ITooltipFlag flagIn) {
        AdditionalInfo.addInformation(stack, worldIn, currentTipList, flagIn);
    }
}