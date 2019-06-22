package net.machinemuse.powersuits.client.model.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Created by lehjr on 12/19/16.
 */
@OnlyIn(Dist.CLIENT)
public class ModelPowerFist implements IDynamicBakedModel {
    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return null;
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
        return null;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return null;
    }
//    static ItemCameraTransforms.TransformType modelcameraTransformType;
//    static ItemStack itemStack;
//    static Item item;
//    static Colour colour;
//    static World world;
//    static LivingEntity entity;
//    static boolean isFiring = false;
//    static IBakedModel iconModel;
//    CompoundNBT renderTag = new CompoundNBT();
////    ModelTransformCalibration calibration;
//    CompoundNBT renderSpec;
//
//    public ModelPowerFist(IBakedModel bakedModelIn) {
//        this.iconModel = (bakedModelIn instanceof ModelPowerFist) ? ((ModelPowerFist) bakedModelIn).iconModel : bakedModelIn;
////        calibration = new ModelTransformCalibration();
//    }
//
//    /**
//     * Since this is where the quads are actually
//     */
//    @Override
//    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable Direction side, Random rand) {
//        if (side != null)
//            return ImmutableList.of();
//
//        switch (modelcameraTransformType) {
//            case GUI:
//            case FIXED:
//            case NONE:
//                return iconModel.getQuads(state, side, rand);
//        }
//
//        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
//        int[] colours = renderSpec.getIntArray(NuminaConstants.TAG_COLOURS);
//        Colour partColor;
//        TRSRTransformation transform;
//
//        for (CompoundNBT nbt : NBTTagAccessor.getValues(renderSpec)) {
//            PartSpecBase partSpec = ModelRegistry.getInstance().getPart(nbt);
//            if (partSpec instanceof ModelPartSpec) {
//
//                // only process this part if it's for the correct hand
//                if (partSpec.getBinding().getTarget().name().toUpperCase().equals(
//                        modelcameraTransformType.equals(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) ||
//                                modelcameraTransformType.equals(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND) ?
//                                "LEFTHAND" : "RIGHTHAND")) {
//
//                    transform = ((ModelSpec) partSpec.spec).getTransform(modelcameraTransformType);
//                    String itemState = partSpec.getBinding().getItemState();
//
//                    int ix = partSpec.getColourIndex(nbt);
//                    if (ix < colours.length && ix >= 0)
//                        partColor = new Colour(colours[ix]);
//                    else
//                        partColor = Colour.WHITE;
//                    boolean glow = ((ModelPartSpec) partSpec).getGlow(nbt);
//
//                    if ((!isFiring && (itemState.equals("all") || itemState.equals("normal"))) ||
//                            (isFiring && (itemState.equals("all") || itemState.equals("firing"))))
//                        builder.addAll(MuseModelHelper.getColouredQuadsWithGlowAndTransform(((ModelPartSpec) partSpec).getQuads(), partColor, transform, glow));
//                }
//            }
//        }
//        return builder.build();
//    }
//
//
//
//    /**
//     * this is great for single models or those that share the exact same transforms for the different camera transform
//     * type. However, when dealing with quads from different models, it's useless.
//     */
//    @Override
//    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
//        modelcameraTransformType = cameraTransformType;
//        switch (cameraTransformType) {
//            case FIRST_PERSON_LEFT_HAND:
//            case THIRD_PERSON_LEFT_HAND:
//            case FIRST_PERSON_RIGHT_HAND:
//            case THIRD_PERSON_RIGHT_HAND:
//                return Pair.of(this, TRSRTransformation.blockCornerToCenter(TRSRTransformation.identity()).getMatrixVec());
//            default:
//                return iconModel.handlePerspective(cameraTransformType);
//        }
//    }
//
//    @Override
//    public boolean isAmbientOcclusion() {
//        return false;
//    }
//
//    @Override
//    public boolean isGui3d() {
//        if (iconModel == null)
//            iconModel = ModelBakeEventHandler.INSTANCE.powerFistIconModel;
//        return iconModel.isGui3d();
//    }
//
//    @Override
//    public boolean isBuiltInRenderer() {
//        return false;
//    }
//
//    @Override
//    public TextureAtlasSprite getParticleTexture() {
//        return iconModel.getParticleTexture();
//    }
//
//    @Override
//    public ItemOverrideList getOverrides() {
//        return new PowerFistItemOverrideList();
//    }
//
//    public class PowerFistItemOverrideList extends ItemOverrideList {
//        public PowerFistItemOverrideList() {
//            super(
//                    ModelLoaderRegistry.getMissingModel(),
//                    ModelLoader.defaultModelGetter(),
//                    MuseModelHelper.defaultTextureGetter(),
//                    ImmutableList.of());
//        }
//
//        @Nullable
//        @Override // used to be handleItemState
//        public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stackIn, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
//            itemStack = stackIn;
//            renderSpec = MPSModelHelper.getMuseRenderTag(stackIn);
//            world = worldIn;
//            entity = entityIn;
//            item = stackIn.getItem();
//            // Todo: eliminate
////            colour = ((IModularItemBase) item).getColorFromItemStack(stackin);
//
//            if (entityIn instanceof PlayerEntity) {
////                if (!stackIn.isEmpty() && stackIn == entityIn.getHeldItemMainhand() && entityIn.isHandActive()
////                        && ModuleManager.INSTANCE.itemHasActiveModule(stackin, MPSModuleConstants.MODULE_PLASMA_CANNON__DATANAME)) {
////                    isFiring = true;
////                } else
//                    isFiring = false;
//            } else isFiring = false;
//
//            return originalModel;
//        }
//    }
}
