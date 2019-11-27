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

package com.github.machinemuse.powersuits.client.model.item;

import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.client.model.helper.ModelHelper;
import com.github.lehjr.mpalib.client.model.helper.ModelTransformCalibration;
import com.github.lehjr.mpalib.client.render.modelspec.ModelPartSpec;
import com.github.lehjr.mpalib.client.render.modelspec.ModelRegistry;
import com.github.lehjr.mpalib.client.render.modelspec.ModelSpec;
import com.github.lehjr.mpalib.client.render.modelspec.PartSpecBase;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.nbt.NBTTagAccessor;
import com.github.machinemuse.powersuits.client.event.ModelBakeEventHandler;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by lehjr on 12/19/16.
 */
@SideOnly(Side.CLIENT)
public class ModelPowerFist implements IBakedModel {
    static ItemCameraTransforms.TransformType modelcameraTransformType;
    static ItemStack itemStack;
    static boolean isFiring = false;
    static IBakedModel iconModel;
    ModelTransformCalibration calibration;

    public ModelPowerFist(IBakedModel bakedModelIn) {
        this.iconModel = (bakedModelIn instanceof ModelPowerFist) ? ((ModelPowerFist) bakedModelIn).iconModel : bakedModelIn;
        calibration = new ModelTransformCalibration();
    }

    /**
     * this is great for single models or those that share the exact same transforms for the different camera transform
     * type. However, when dealing with quads from different models, it's useless.
     */
    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        modelcameraTransformType = cameraTransformType;
        switch (cameraTransformType) {
            case FIRST_PERSON_LEFT_HAND:
            case THIRD_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
                return Pair.of(this, TRSRTransformation.blockCornerToCenter(TRSRTransformation.identity()).getMatrix());
            default:
                return iconModel.handlePerspective(cameraTransformType);
        }
    }

    @Override
    public boolean isAmbientOcclusion() {
        return iconModel.isAmbientOcclusion();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return iconModel.getParticleTexture();
    }

    @Override
    public boolean isGui3d() {
        if (iconModel == null)
            iconModel = ModelBakeEventHandler.INSTANCE.powerFistIconModel;
        return iconModel.isGui3d();
    }

    /**
     * Since this is where the quads are actually
     */
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (side != null)
            return ImmutableList.of();

        switch (modelcameraTransformType) {
            case GUI:
            case FIXED:
            case NONE:
                return iconModel.getQuads(state, side, rand);
        }

        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        Optional.ofNullable(itemStack.getCapability(ModelSpecNBTCapability.RENDER, null)).ifPresent(iModelSpecNBT -> {
            int[] colours = iModelSpecNBT.getColorArray();

            Colour partColor;
            TRSRTransformation transform;
            NBTTagCompound renderSpec = iModelSpecNBT.getRenderTag();

            if (renderSpec != null) {
                for (NBTTagCompound nbt : NBTTagAccessor.getValues(renderSpec)) {
                    PartSpecBase partSpec = ModelRegistry.getInstance().getPart(nbt);
                    if (partSpec instanceof ModelPartSpec) {

                        // only process this part if it's for the correct hand
                        if (partSpec.getBinding().getTarget().name().toUpperCase().equals(
                                modelcameraTransformType.equals(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) ||
                                        modelcameraTransformType.equals(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) ?
                                        "LEFTHAND" : "RIGHTHAND")) {

                            transform = ((ModelSpec) partSpec.spec).getTransform(modelcameraTransformType);
                            String itemState = partSpec.getBinding().getItemState();

                            int ix = partSpec.getColourIndex(nbt);
                            if (ix < colours.length && ix >= 0)
                                partColor = new Colour(colours[ix]);
                            else
                                partColor = Colour.WHITE;
                            boolean glow = ((ModelPartSpec) partSpec).getGlow(nbt);

                            if ((!isFiring && (itemState.equals("all") || itemState.equals("normal"))) ||
                                    (isFiring && (itemState.equals("all") || itemState.equals("firing"))))
                                builder.addAll(ModelHelper.getColouredQuadsWithGlowAndTransform(((ModelPartSpec) partSpec).getQuads(), partColor, transform, glow));
                        }
                    }
                }

            }
        });
        return builder.build();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new PowerFistItemOverrideList();
    }

    public static class PowerFistItemOverrideList extends ItemOverrideList {
        public PowerFistItemOverrideList() {
            super(Collections.EMPTY_LIST);
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stackIn, World worldIn, EntityLivingBase entityIn) {
            itemStack = stackIn;
            if (entityIn instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entityIn;
                if (player.isHandActive()) {
                    Optional.ofNullable(player.getHeldItem(player.getActiveHand()).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(modechanging -> {
                        if (!(modechanging instanceof IModeChangingItem))
                            return;

                        ItemStack module = ((IModeChangingItem) modechanging).getActiveModule();
                        int actualCount = 0;

                        int maxDuration = ((IModeChangingItem) modechanging).getModularItemStack().getMaxItemUseDuration();
                        if (!module.isEmpty()) {
                            actualCount = (maxDuration - player.getItemInUseCount());
                        }
                        isFiring = actualCount > 0;
                    });
                } else {
                    isFiring = false;
                }
            }
            return originalModel;
        }
    }
}