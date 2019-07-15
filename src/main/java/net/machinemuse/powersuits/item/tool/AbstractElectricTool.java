package net.machinemuse.powersuits.item.tool;

import net.machinemuse.powersuits.basemod.MPSObjects;
import net.machinemuse.powersuits.event.RegisterStuff;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;

import java.util.HashSet;
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
}