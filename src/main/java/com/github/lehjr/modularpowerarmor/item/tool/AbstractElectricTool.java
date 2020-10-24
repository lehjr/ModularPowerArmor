package com.github.lehjr.modularpowerarmor.item.tool;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.mpalib.util.string.AdditionalInfo;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

public class AbstractElectricTool extends ToolItem {
    public AbstractElectricTool() {
        super(0.0F,
                0.0F,
                MPAToolMaterial.EMPTY_TOOL,
                new HashSet<>(),
                new Item.Properties().group(MPAObjects.creativeTab).maxStackSize(1).defaultMaxDamage(0));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn != null) {
            AdditionalInfo.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {

    }

    @Override
    public boolean isDamageable() {
        return false;
    }
}