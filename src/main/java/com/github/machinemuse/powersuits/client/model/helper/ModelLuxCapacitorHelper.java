/*
 * ModularPowersuits (Maintenance builds by lehjr)
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

package com.github.machinemuse.powersuits.client.model.helper;


import com.github.lehjr.mpalib.client.model.helper.ModelHelper;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.api.constants.MPSResourceConstants;
import com.github.machinemuse.powersuits.basemod.MPSItems;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockDirectional;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT)
public enum ModelLuxCapacitorHelper {
    INSTANCE;

    private static final ResourceLocation baseModelLocation = new ResourceLocation(MPSResourceConstants.RESOURCE_PREFIX + "block/luxcapacitor/luxcapacitor_base.obj");
    private static final ResourceLocation lensModelLocation = new ResourceLocation(MPSResourceConstants.RESOURCE_PREFIX + "block/luxcapacitor/luxcapacitor_lens.obj");

    /*
     * Guava chache for the list of baked quads.
     * The "ColoredQuadHelperThingie" is just easier and cleaner than using multi level maps.
     */
    public static LoadingCache<ColoredQuadHelperThingie, List<BakedQuad>> luxCapColoredQuadMap = CacheBuilder.newBuilder()
            .maximumSize(40)
            .build(new CacheLoader<ColoredQuadHelperThingie, List<BakedQuad>>() {
                @Override
                public List<BakedQuad> load(ColoredQuadHelperThingie key) throws Exception {
                    return getQuads(key.getColour(), key.getFacing());
                }

                public IBakedModel getBase(@Nullable EnumFacing facing) {
                    return ModelHelper.getBakedModel(baseModelLocation, TRSRTransformation.from((facing != null) ? facing : EnumFacing.NORTH));
                }

                public IBakedModel getLens(@Nullable EnumFacing facing) {
                    return ModelHelper.getBakedModel(lensModelLocation, TRSRTransformation.from((facing != null) ? facing : EnumFacing.NORTH));
                }

                List<BakedQuad> getBaseQuads(@Nullable EnumFacing facing) {
                    facing = (facing != null) ? facing : EnumFacing.NORTH;

                    TRSRTransformation transform = TRSRTransformation.from(facing);
                    IBakedModel bakedModel = ModelHelper.getBakedModel(baseModelLocation, transform);
                    return bakedModel.getQuads(MPSItems.INSTANCE.luxCapacitor.getDefaultState().withProperty(BlockDirectional.FACING, facing), null, 0);
                }

                List<BakedQuad> getLensColoredQuads(Colour color, @Nullable EnumFacing facing) {
                    facing = (facing != null) ? facing : EnumFacing.NORTH;
                    TRSRTransformation transform = TRSRTransformation.from(facing);
                    IBakedModel bakedModel = ModelHelper.getBakedModel(lensModelLocation, transform);
                    List<BakedQuad> quads = bakedModel.getQuads(MPSItems.INSTANCE.luxCapacitor.getDefaultState().withProperty(BlockDirectional.FACING, facing), null, 0);
                    return ModelHelper.getColoredQuadsWithGlow(quads, color, true);
                }

                List<BakedQuad> getQuads(Colour color, @Nullable EnumFacing facing) {
                    List<BakedQuad> frameList = getBaseQuads(facing);
                    List<BakedQuad> lensList = getLensColoredQuads(color, facing);

                    ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                    for (BakedQuad quad : frameList)
                        builder.add(quad);
                    for (BakedQuad quad : lensList)
                        builder.add(quad);
                    return builder.build();
                }
            });
}