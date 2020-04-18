package com.github.lehjr.modularpowerarmor.client.model.block;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.mpalib.client.model.helper.ModelHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class LuxCapaitorModelWrapper extends BakedModelWrapper {
    IBakedModel lens;

    



    public LuxCapaitorModelWrapper(IBakedModel originalModel) {
        super(originalModel);
        OBJModel lensOBJ = OBJLoader.INSTANCE.loadModel(new OBJModel.ModelSettings(new ResourceLocation(MPAConstants.MOD_ID, "models/block/luxcapacitor/luxcapacitor_lens.obj"), true, false, true, true, null));



//        lens =



    }




    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return super.getQuads(state, side, rand, extraData);
    }
}
