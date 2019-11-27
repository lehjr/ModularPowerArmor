package com.github.machinemuse.powersuits.fluid;

import com.github.machinemuse.powersuits.api.constants.MPSModConstants;
import com.github.machinemuse.powersuits.basemod.MPSItems;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidFinite;

public class BlockFluidLiquidNitrogen extends BlockFluidFinite {
    public BlockFluidLiquidNitrogen(ResourceLocation regName) {
        super(MPSItems.liquidNitrogen, Material.WATER);
        setRegistryName(regName);
        setTranslationKey(new StringBuilder(MPSModConstants.MODID).append(".").append(LiquidNitrogen.name).toString());
    }
}