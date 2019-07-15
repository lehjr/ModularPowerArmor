package net.machinemuse.powersuits.client.model.helper;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import net.machinemuse.numina.client.model.helper.MuseModelHelper;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.MPSObjects;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public enum ModelLuxCapacitorHelper {
    INSTANCE;

    private static final ResourceLocation baseModelLocation = new ResourceLocation(MPSConstants.RESOURCE_PREFIX + "models/block/luxcapacitor/luxcapacitor_base.obj");
    private static final ResourceLocation lensModelLocation = new ResourceLocation(MPSConstants.RESOURCE_PREFIX + "models/block/luxcapacitor/luxcapacitor_lens.obj");

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

                public IBakedModel getBase(@Nullable Direction facing) {
                    return MuseModelHelper.loadBakedModel(baseModelLocation, TRSRTransformation.from((facing != null) ? facing : Direction.NORTH));
                }

                public IBakedModel getLens(@Nullable Direction facing) {
                    return MuseModelHelper.loadBakedModel(lensModelLocation, TRSRTransformation.from((facing != null) ? facing : Direction.NORTH));
                }

                List<BakedQuad> getBaseQuads(@Nullable Direction facing) {
                    facing = (facing != null) ? facing : Direction.NORTH;

                    TRSRTransformation transform = TRSRTransformation.from(facing);
                    IBakedModel bakedModel = MuseModelHelper.loadBakedModel(baseModelLocation, transform);
                    return bakedModel.getQuads(MPSObjects.INSTANCE.luxCapacitor.getDefaultState().with(DirectionalBlock.FACING, facing), null, new Random());
                }

                List<BakedQuad> getLensColoredQuads(Colour color, @Nullable Direction facing) {
                    facing = (facing != null) ? facing : Direction.NORTH;
                    TRSRTransformation transform = TRSRTransformation.from(facing);
                    IBakedModel bakedModel = MuseModelHelper.loadBakedModel(lensModelLocation, transform);
                    List<BakedQuad> quads = bakedModel.getQuads(MPSObjects.INSTANCE.luxCapacitor.getDefaultState().with(DirectionalBlock.FACING, facing), null, new Random());
                    return MuseModelHelper.getColoredQuadsWithGlow(quads, color, true);
                }

                List<BakedQuad> getQuads(Colour color, @Nullable Direction facing) {
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