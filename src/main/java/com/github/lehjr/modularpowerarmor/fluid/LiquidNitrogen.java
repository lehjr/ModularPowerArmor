package com.github.lehjr.modularpowerarmor.fluid;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class LiquidNitrogen extends Fluid {
    public static final String name = "liquid_nitrogen";

    // Note: Naming convention seems a bit weird, but fluids are handled a bit differently.
    public LiquidNitrogen() {
        super(name, new ResourceLocation(Constants.MODID, "fluids/" + name + "_still"), new ResourceLocation(Constants.MODID, "fluids/" + name + "_flow"));
        setTemperature(70);
        setViscosity(200);
    }
}
