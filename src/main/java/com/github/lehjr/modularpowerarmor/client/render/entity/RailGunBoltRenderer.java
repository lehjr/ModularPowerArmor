package com.github.lehjr.modularpowerarmor.client.render.entity;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.mpalib.util.client.model.helper.ModelHelper;
import com.github.lehjr.mpalib.util.math.Colour;
import forge.OBJBakedCompositeModel;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.NonNullLazy;

import java.util.Random;

public class RailGunBoltRenderer {
    Colour colour = new Colour(161, 157, 148, 255);
    static final ResourceLocation modelLocation = new ResourceLocation(MPAConstants.MOD_ID, "models/entity/bolt.obj");
    // NonNullLazy doesn't init until called
    public static final NonNullLazy<OBJBakedCompositeModel> modelBolt = NonNullLazy.of(() -> ModelHelper.loadBakedModel(ModelRotation.X0_Y0, null, modelLocation));
    protected static final Random rand = new Random();







}
