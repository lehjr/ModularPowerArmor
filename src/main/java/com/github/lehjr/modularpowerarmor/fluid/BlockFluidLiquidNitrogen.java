package com.github.lehjr.modularpowerarmor.fluid;

import com.github.lehjr.modularpowerarmor.common.MPSItems;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidFinite;

public class BlockFluidLiquidNitrogen extends BlockFluidFinite {
    public BlockFluidLiquidNitrogen(ResourceLocation regName) {
        super(MPSItems.liquidNitrogen, Material.WATER);
        setRegistryName(regName);
        setTranslationKey(new StringBuilder(Constants.MODID).append(".").append(LiquidNitrogen.name).toString());
    }
}