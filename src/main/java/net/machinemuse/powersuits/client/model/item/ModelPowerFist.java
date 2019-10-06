package net.machinemuse.powersuits.client.model.item;

import com.google.common.collect.ImmutableList;
import net.machinemuse.numina.basemod.NuminaConstants;
import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.capabilities.render.IHandHeldModelSpecNBT;
import net.machinemuse.numina.capabilities.render.ModelSpecNBTCapability;
import net.machinemuse.numina.client.model.helper.MuseModelHelper;
import net.machinemuse.numina.client.render.modelspec.ModelPartSpec;
import net.machinemuse.numina.client.render.modelspec.ModelRegistry;
import net.machinemuse.numina.client.render.modelspec.ModelSpec;
import net.machinemuse.numina.client.render.modelspec.PartSpecBase;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.nbt.NBTTagAccessor;
import net.machinemuse.powersuits.client.event.ModelBakeEventHandler;
import net.machinemuse.powersuits.network.MPSPackets;
import net.machinemuse.powersuits.network.packets.MusePacketCosmeticInfo;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Random;

/**
 * Created by lehjr on 12/19/16.
 */
@OnlyIn(Dist.CLIENT)
public class ModelPowerFist implements IDynamicBakedModel {
    static ItemCameraTransforms.TransformType modelcameraTransformType;
    static ItemStack itemStack;
    static boolean isFiring = false;
    static IBakedModel iconModel;
    public ModelPowerFist(IBakedModel bakedModelIn) {
        this.iconModel = (bakedModelIn instanceof ModelPowerFist) ? ((ModelPowerFist) bakedModelIn).iconModel : bakedModelIn;
//        calibration = new ModelTransformCalibration();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        return getQuads(state, side, rand, null);
    }

    /**
     * We don't actually have any IModelData being passed here, so we can ignore the parameter.
     *
     * @param state
     * @param side
     * @param rand
     * @param extraData
     * @return
     */
    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (side != null)
            return ImmutableList.of();

        switch (modelcameraTransformType) {
            case GUI:
            case FIXED:
            case NONE:
                return iconModel.getQuads(state, side, rand);
        }
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        itemStack.getCapability(ModelSpecNBTCapability.RENDER).ifPresent(specNBTCap -> {
            if (specNBTCap instanceof IHandHeldModelSpecNBT) {
                CompoundNBT renderSpec = specNBTCap.getMuseRenderTag();

                // Set the tag on the item so this lookup isn't happening on every loop.
                if (renderSpec == null || renderSpec.isEmpty()) {
                    renderSpec = specNBTCap.getDefaultRenderTag();

                    // first person transform type insures THIS client's player is the one holding the item rather than this
                    // client's player seeing another player holding it
                    if (renderSpec != null && !renderSpec.isEmpty() &&
                            (modelcameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND ||
                                    (modelcameraTransformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND))) {
                        PlayerEntity player = Minecraft.getInstance().player;
                        int slot = -1;
                        if (player.getHeldItemMainhand().equals(itemStack)) {
                            slot = player.inventory.currentItem;
                        } else {
                            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                                if (player.inventory.getStackInSlot(i).equals(itemStack)) {
                                    slot = i;
                                    break;
                                }
                            }
                        }

                        if (slot != -1) {
                            specNBTCap.setMuseRenderTag(renderSpec, NuminaConstants.TAG_RENDER);
                            MPSPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketCosmeticInfo(slot, NuminaConstants.TAG_RENDER, renderSpec));
                        } else {
                            System.out.println(" stack not found");
                        }
                    }
                }

                if (renderSpec != null) {
                    int[] colours = renderSpec.getIntArray(NuminaConstants.TAG_COLOURS);
                    Colour partColor;
                    TRSRTransformation transform;

                    for (CompoundNBT nbt : NBTTagAccessor.getValues(renderSpec)) {
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
                                    builder.addAll(MuseModelHelper.getColouredQuadsWithGlowAndTransform(((ModelPartSpec) partSpec).getQuads(), partColor, transform, glow));
                            }
                        }
                    }
                }
            }
        });
        return builder.build();
    }

    /**
     * When dealing with possibly multiple specs and color lists, new list needs to be created, since there is only one list per item.
     */
    static List<Integer> addNewColourstoList(List<Integer> colours, List<Integer> coloursToAdd) {
        for (Integer i : coloursToAdd) {
            if (!colours.contains(i))
                colours.add(i);
        }
        return colours;
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
                return Pair.of(this, TRSRTransformation.blockCornerToCenter(TRSRTransformation.identity()).getMatrixVec());
            default:
                return iconModel.handlePerspective(cameraTransformType);
        }
    }

    @Override
    public boolean isGui3d() {
        if (iconModel == null)
            iconModel = ModelBakeEventHandler.INSTANCE.powerFistIconModel;
        return iconModel.isGui3d();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
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
    public ItemOverrideList getOverrides() {
        return new PowerFistItemOverrideList();
    }

    /**
     * Overrides are interesting. If you set them up both in the model and in the item's constructor,
     * the model being passed to the IBaked parameter here should change depending on that.
     */
    public class PowerFistItemOverrideList extends ItemOverrideList {
        @Nullable
        @Override
        public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack itemStackIn, @Nullable World world, @Nullable LivingEntity entityIn) {
            itemStack = itemStackIn;
            if (entityIn instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) entityIn;
                if (player.isHandActive()) {
                    player.getHeldItem(player.getActiveHand()).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(modechanging -> {
                        if (!(modechanging instanceof IModeChangingItem))
                            return;

                        ItemStack module = ((IModeChangingItem) modechanging).getActiveModule();
                        int actualCount = 0;

                        int maxDuration = ((IModeChangingItem) modechanging).getModularItemStack().getUseDuration();
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