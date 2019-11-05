package com.github.lehjr.modularpowerarmor.item.tool;

import com.github.lehjr.modularpowerarmor.client.misc.AdditionalInfo;
import com.github.lehjr.modularpowerarmor.event.RegisterStuff;
import net.minecraft.block.Block;
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
import java.util.Set;

public class AbstractElectricTool extends ToolItem {
    private final Set<Block> effectiveBlocks = new HashSet<>();

    public AbstractElectricTool() {
        super(0.0F,
                0.0F,
                MPSToolMaterial.EMPTY_TOOL,
                new HashSet<>(),
                new Item.Properties().group(RegisterStuff.INSTANCE.creativeTab).maxStackSize(1).defaultMaxDamage(0));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn != null) {
            AdditionalInfo.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }
}