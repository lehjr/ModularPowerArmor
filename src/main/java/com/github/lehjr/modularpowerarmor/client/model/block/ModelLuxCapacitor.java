package com.github.lehjr.modularpowerarmor.client.model.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.github.lehjr.mpalib.client.model.helper.MuseModelHelper;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.block.BlockLuxCapacitor;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ModelLuxCapacitor implements IDynamicBakedModel {
    final IModelState modelState;
    public IDynamicBakedModel wrapper;
    Colour colour;
    private LuxCapacitorItemOverrideList overrides;
    TextureAtlasSprite particleTexture = null;
    IBakedModel baseModel;
    IBakedModel lensModel;


    public ModelLuxCapacitor(IBakedModel baseModel, IBakedModel lensModel) {
        this.overrides = new LuxCapacitorItemOverrideList();
        this.wrapper = this;
        this.modelState = getModelState();
        this.baseModel = baseModel;
        this.lensModel = lensModel;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        builder.addAll(baseModel.getQuads(state, side, rand, extraData));

        colour = extraData.hasProperty(BlockLuxCapacitor.COLOUR_PROP) ? new Colour(extraData.getData(BlockLuxCapacitor.COLOUR_PROP)) : BlockLuxCapacitor.defaultColor;

        builder.addAll(MuseModelHelper.getColoredQuadsWithGlow(lensModel.getQuads(state, side, rand, extraData), colour, true));
        return builder.build();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return particleTexture != null ? particleTexture : MissingTextureSprite.func_217790_a();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return overrides;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        TRSRTransformation transform = modelState.apply(Optional.of(cameraTransformType)).orElse(TRSRTransformation.identity());
        if (transform != TRSRTransformation.identity())
            return Pair.of(this, transform.getMatrixVec());

        return Pair.of(this, transform.getMatrixVec());
    }

    public static ModelResourceLocation getModelResourceLocation(Direction facing) {
        return new ModelResourceLocation(MPARegistryNames.LUX_CAPACITOR_REG_NAME, "facing=" + facing.getName());
    }

    public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(MPARegistryNames.LUX_CAPACITOR_REG_NAME, "inventory");

    public static final IModelState getModelState() {
        ImmutableMap.Builder<IModelPart, TRSRTransformation> builder = ImmutableMap.builder();
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND,
                MuseModelHelper.get(1.13F, 3.2F, 1.13F, -25F, -90F, 0F, 0.41F));

        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                MuseModelHelper.get(0F, 2F, 3F, 0F, 0F, 45F, 0.5F));

        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
                MuseModelHelper.get(1.13F, 3.2F, 1.13F, -25F, -90F, 0F, 0.41F));

        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                MuseModelHelper.get(0F, 2F, 3F, 0F, 0F, 45F, 0.5F));

        builder.put(ItemCameraTransforms.TransformType.GUI,
                MuseModelHelper.get(0F, 2.75F, 0F, -45F, 0F, 45F, 0.75F));

        builder.put(ItemCameraTransforms.TransformType.GROUND,
                MuseModelHelper.get(0F, 2F, 0F, -90F, -0F, 0F, 0.5F));

        builder.put(ItemCameraTransforms.TransformType.FIXED,
                MuseModelHelper.get(0F, 0F, -7.5F, 0F, 180F, 0F, 1F));
        return new SimpleModelState(builder.build());
    }

    private class LuxCapacitorItemOverrideList extends ItemOverrideList {
        @Nullable
        @Override
        public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
            return ModelLuxCapacitor.this.wrapper;
        }
    }
}