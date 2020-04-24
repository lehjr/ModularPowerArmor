package com.github.lehjr.modularpowerarmor.client.render.item.armor;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.modularpowerarmor.client.model.item.ArmorModelInstance;
import com.github.lehjr.modularpowerarmor.client.model.item.HighPolyArmor;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.render.modelspec.ModelPartSpec;
import com.github.lehjr.mpalib.capabilities.render.modelspec.ModelRegistry;
import com.github.lehjr.mpalib.capabilities.render.modelspec.PartSpecBase;
import com.github.lehjr.mpalib.client.model.helper.ModelHelper;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.nbt.NBTTagAccessor;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 4:16 AM, 29/04/13
 * <p>
 * Ported to Java by lehjr on 11/6/16.
 */
@OnlyIn(Dist.CLIENT)
public class RenderPart extends ModelRenderer {
    ModelRenderer parent;

    public RenderPart(Model base, ModelRenderer parent) {
        super(base);
        this.parent = parent;
    }

//    @Override
//    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn) {
//        System.out.println("doing something here");
//
//        super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
//    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        System.out.println("doing something here");

        if (this.showModel) {
            CompoundNBT renderSpec = ((HighPolyArmor) (ArmorModelInstance.getInstance())).getRenderSpec();
            if (renderSpec != null) {
                int[] colours = renderSpec.getIntArray(MPALIbConstants.TAG_COLOURS);

                if (colours.length == 0) {
                    colours = new int[]{Colour.WHITE.getInt()};
                }

                Colour partColor;
                for (CompoundNBT nbt : NBTTagAccessor.getValues(renderSpec)) {
                    PartSpecBase part = ModelRegistry.getInstance().getPart(nbt);
                    if (part != null && part instanceof ModelPartSpec) {
                        if (part.getBinding().getSlot() == ((HighPolyArmor) (ArmorModelInstance.getInstance())).getVisibleSection()
                                && part.getBinding().getTarget().apply(ArmorModelInstance.getInstance()) == parent) {
//                            IBakedModel modelPart = ((ModelPartSpec) part).getPart();

                            IBakedModel modelPart = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(new ItemStack(MPAObjects.luxCapacitor), Minecraft.getInstance().player.world, (LivingEntity) null);


//                            int ix = part.getColourIndex(nbt);
//                            // checks the range of the index to avoid errors OpenGL or crashing
//                            if (ix < colours.length && ix >= 0) {
//                                partColor = new Colour(colours[ix]);
//                            } else {
                                partColor = Colour.WHITE;
//                            }

                            matrixStackIn.push();
                            this.translateRotate(matrixStackIn);
                            renderModel(modelPart, packedLightIn, partColor, ((ModelPartSpec) part).getGlow(nbt), packedOverlayIn, matrixStackIn, bufferIn);
                            matrixStackIn.pop();

                        }
                    }
                }
            }
        }

//        System.out.println("showModel: " + this.showModel);
//        System.out.println("is cubelist empty? " + cubeList.isEmpty()); // super won't render with empty cube list
//        System.out.println("is childlist empty? " + childModels.isEmpty()); // super won't render with childlist cube list
//        renderer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

    }

    public void renderer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.push();
        this.translateRotate(matrixStackIn);
//        this.doRender(matrixStackIn.getLast(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

//        for(ModelRenderer modelrenderer : this.childModels) {
//            modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//        }

        matrixStackIn.pop();
    }













//    public void renderItem(boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn) {
//            matrixStackIn.push();
////            boolean flag = transformTypeIn == ItemCameraTransforms.TransformType.GUI;
////            boolean flag1 = flag || transformTypeIn == ItemCameraTransforms.TransformType.GROUND || transformTypeIn == ItemCameraTransforms.TransformType.FIXED;
//
//            modelIn = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStackIn, modelIn, transformTypeIn, leftHand);
//            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
//            if (!modelIn.isBuiltInRenderer() && (itemStackIn.getItem() != Items.TRIDENT || flag1)) {
//                RenderType rendertype = Atlases.getTranslucentBlockType();
//                RenderType rendertype1;
//                if (flag && Objects.equals(rendertype, Atlases.getTranslucentBlockType())) {
//                    rendertype1 = Atlases.getTranslucentCullBlockType();
//                } else {
//                    rendertype1 = rendertype;
//                }
//
//                IVertexBuilder ivertexbuilder = getBuffer(bufferIn, rendertype1, true, itemStackIn.hasEffect());
//                this.renderModel(modelIn, itemStackIn, combinedLightIn, combinedOverlayIn, matrixStackIn, ivertexbuilder);
//            }
//
//            matrixStackIn.pop();
//    }
//
//    private void renderModel(IBakedModel modelIn, ItemStack stack, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IVertexBuilder bufferIn) {
//        Random random = new Random();
//        long i = 42L;
//
//        for(Direction direction : Direction.values()) {
//            random.setSeed(42L);
//            this.renderQuads(matrixStackIn, bufferIn, modelIn.getQuads((BlockState)null, direction, random), stack, combinedLightIn, combinedOverlayIn);
//        }
//
//        random.setSeed(42L);
//        this.renderQuads(matrixStackIn, bufferIn, modelIn.getQuads((BlockState)null, (Direction)null, random), stack, combinedLightIn, combinedOverlayIn);
//    }
//
//    public void renderQuads(MatrixStack matrixStackIn, IVertexBuilder bufferIn, List<BakedQuad> quadsIn, ItemStack itemStackIn, int combinedLightIn, int combinedOverlayIn) {
//        boolean flag = !itemStackIn.isEmpty();
//        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
//
//        for(BakedQuad bakedquad : quadsIn) {
//            int i = -1;
//            if (flag && bakedquad.hasTintIndex()) {
//                i = this.itemColors.getColor(itemStackIn, bakedquad.getTintIndex());
//            }
//
//            float f = (float)(i >> 16 & 255) / 255.0F;
//            float f1 = (float)(i >> 8 & 255) / 255.0F;
//            float f2 = (float)(i & 255) / 255.0F;
//            bufferIn.addVertexData(matrixstack$entry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn, true);
//        }
//
//    }

















//    public void renderer(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
//        if (this.showModel) {
//            if (!this.cubeList.isEmpty() || !this.childModels.isEmpty()) {
//                matrixStackIn.push();
//                this.translateRotate(matrixStackIn);
//                this.doRender(matrixStackIn.getLast(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//
//                for(ModelRenderer modelrenderer : this.childModels) {
//                    modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
//                }
//
//                matrixStackIn.pop();
//            }
//        }
//    }



    private void renderModel(IBakedModel modelIn, int combinedLightIn, Colour color, boolean glow, int combinedOverlayIn, MatrixStack matrixStackIn, IVertexBuilder bufferIn) {
        Random random = new Random();
        long i = 42L;

        for(Direction direction : Direction.values()) {
            random.setSeed(42L);
            List<BakedQuad> quads = modelIn.getQuads((BlockState)null, direction, random);
            System.out.println("quads size (direction): " + quads.size());

            this.renderQuads(matrixStackIn, bufferIn, ModelHelper.getColoredQuadsWithGlow(quads, color, glow), Colour.WHITE.withAlpha(1), combinedLightIn, combinedOverlayIn);
        }
        random.setSeed(42L);
        List<BakedQuad> quads = modelIn.getQuads((BlockState)null, null, random);

        System.out.println("quads size (null direction): " + quads.size());

        this.renderQuads(matrixStackIn, bufferIn, quads, Colour.WHITE.withAlpha(1), combinedLightIn, combinedOverlayIn);
    }

    public void renderQuads(MatrixStack matrixStackIn, IVertexBuilder bufferIn, List<BakedQuad> quadsIn, Colour color, int combinedLightIn, int combinedOverlayIn) {
        MatrixStack.Entry matrixStack = matrixStackIn.getLast();






        for(BakedQuad bakedquad : quadsIn) {
            int i = -1;
//            if (flag && bakedquad.hasTintIndex()) {
//                i = this.itemColors.getColor(itemStackIn, bakedquad.getTintIndex());
//            }

            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;



            bufferIn.addVertexData(matrixStack, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn, true);
//


//            bufferIn.addVertexData(matrixStack, bakedquad, color.r, color.g, color.b, color.a, combinedLightIn, combinedOverlayIn, true);
        }
    }


    /*
        @Override
        public void render(float scale) {
            CompoundNBT renderSpec = ((HighPolyArmor) (ArmorModelInstance.getInstance())).getRenderSpec();
            if (renderSpec == null)
                return;






        }
    */
    private void applyTransform() {
        System.out.println("fixme!!!");


////        float degrad = (float) (180F / Math.PI);
////        GL11.glTranslatef(rotationPointX, rotationPointY, rotationPointZ);
////        GL11.glRotatef(rotateAngleZ * degrad, 0.0F, 0.0F, 1.0F);
////        GL11.glRotatef(rotateAngleY * degrad, 0.0F, 1.0F, 0.0F);
////        GL11.glRotatef(rotateAngleX * degrad, 1.0F, 0.0F, 0.0F);
//        GlStateManager.rotatef(180, 1.0F, 0.0F, 0.0F);
//        GlStateManager.translatef(offsetX, offsetY - 26, offsetZ);
    }
}