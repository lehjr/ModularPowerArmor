package com.github.lehjr.modularpowerarmor.fluid;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.Objects;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidFinite;

public class BlockFluidLiquidNitrogen extends BlockFluidFinite {
    public BlockFluidLiquidNitrogen(ResourceLocation regName) {
        super(Objects.INSTANCE.liquidNitrogen, Material.WATER);
        setRegistryName(regName);
        setTranslationKey(new StringBuilder(Constants.MODID).append(".").append(LiquidNitrogen.name).toString());
    }
}