/*
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.client.model.block;

import com.github.lehjr.mpalib.client.model.helper.ModelHelper;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.block.BlockLuxCapacitor;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.client.model.helper.ColoredQuadHelperThingie;
import com.github.machinemuse.powersuits.client.model.helper.ModelLuxCapacitorHelper;
import com.github.machinemuse.powersuits.basemod.MPSItems;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static net.minecraft.block.BlockDirectional.FACING;

@SideOnly(Side.CLIENT)
public class ModelLuxCapacitor implements IBakedModel {
    public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(MPSItems.INSTANCE.luxCapacitor.getRegistryName().toString());
    final IModelState modelState;
    public IBakedModel wrapper;
    protected Function<ResourceLocation, TextureAtlasSprite> textureGetter;
    Colour colour;
    private LuxCapacitorItemOverrideList overrides;

    public ModelLuxCapacitor() {
        this.overrides = new LuxCapacitorItemOverrideList();
        this.wrapper = this;
        this.modelState = getModelState();
    }

    public static ModelResourceLocation getModelResourceLocation(EnumFacing facing) {
        return new ModelResourceLocation(MPSItems.INSTANCE.luxCapacitor.getRegistryName().toString(), "facing=" + facing.getName());
    }

    IModelState getModelState() {
        ImmutableMap.Builder<IModelPart, TRSRTransformation> builder = ImmutableMap.builder();
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND,
                ModelHelper.get(1.13F, 3.2F, 1.13F, -25F, -90F, 0F, 0.41F));

        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                ModelHelper.get(0F, 2F, 3F, 0F, 0F, 45F, 0.5F));

        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
                ModelHelper.get(1.13F, 3.2F, 1.13F, -25F, -90F, 0F, 0.41F));

        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                ModelHelper.get(0F, 2F, 3F, 0F, 0F, 45F, 0.5F));

        builder.put(ItemCameraTransforms.TransformType.GUI,
                ModelHelper.get(0F, 2.75F, 0F, -45F, 0F, 45F, 0.75F));

        builder.put(ItemCameraTransforms.TransformType.GROUND,
                ModelHelper.get(0F, 2F, 0F, -90F, -0F, 0F, 0.5F));

        builder.put(ItemCameraTransforms.TransformType.FIXED,
                ModelHelper.get(0F, 0F, -7.5F, 0F, 180F, 0F, 1F));
        return new SimpleModelState(builder.build());
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side != null)
            return Collections.emptyList();

        EnumFacing facing = EnumFacing.NORTH; // both NORTH and items use TRSRTransformation.Identity because I finally rotated the model
        colour = BlockLuxCapacitor.defaultColor;

        if (state != null) {
            facing = state.getValue(FACING);
            if (state instanceof IExtendedBlockState)
                if (((IExtendedBlockState) state).getUnlistedProperties().containsKey(BlockLuxCapacitor.COLOR))
                    colour = ((IExtendedBlockState) state).getValue(BlockLuxCapacitor.COLOR);
        }
        if (colour == null)
            colour = BlockLuxCapacitor.defaultColor;
        ColoredQuadHelperThingie helperThingie = new ColoredQuadHelperThingie(colour, facing);

        try {
            return ModelLuxCapacitorHelper.INSTANCE.luxCapColoredQuadMap.get(helperThingie);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
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
        return MuseIcon.luxCapacitorTexture;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.overrides;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        TRSRTransformation transform = modelState.apply(Optional.of(cameraTransformType)).orElse(TRSRTransformation.identity());
        if (transform != TRSRTransformation.identity())
            return Pair.of(this, transform.getMatrix());

        return Pair.of(this, transform.getMatrix());
    }

    private class LuxCapacitorItemOverrideList extends ItemOverrideList {
        private LuxCapacitorItemOverrideList() {
            super(ImmutableList.of());
        }

        @Nonnull
        public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            return ModelLuxCapacitor.this.wrapper;
        }
    }
}