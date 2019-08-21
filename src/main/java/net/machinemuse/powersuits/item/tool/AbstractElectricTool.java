package net.machinemuse.powersuits.item.tool;

import net.machinemuse.powersuits.client.misc.AdditionalInfo;
import net.machinemuse.powersuits.event.RegisterStuff;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

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

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn != null) {
            AdditionalInfo.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }
}