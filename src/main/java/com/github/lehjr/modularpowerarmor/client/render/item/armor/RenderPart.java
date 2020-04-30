package com.github.lehjr.modularpowerarmor.client.render.item.armor;

import com.github.lehjr.modularpowerarmor.client.model.item.ArmorModelInstance;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.render.modelspec.ModelPartSpec;
import com.github.lehjr.mpalib.capabilities.render.modelspec.ModelRegistry;
import com.github.lehjr.mpalib.capabilities.render.modelspec.ModelSpec;
import com.github.lehjr.mpalib.capabilities.render.modelspec.PartSpecBase;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.nbt.NBTTagAccessor;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.model.TransformationHelper;
import org.lwjgl.system.MemoryStack;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 4:16 AM, 29/04/13
 * <p>
 * Ported to Java by lehjr on 11/6/16.
 */
@OnlyIn(Dist.CLIENT)
public class RenderPart extends ModelRenderer {
    // replace division operation with multiplication
    final float div255 = 0.003921569F;
    ModelRenderer parent;

    public RenderPart(Model base, ModelRenderer parent) {
        super(base);
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.showModel) {
            matrixStackIn.push();
            this.translateRotate(matrixStackIn);
            this.doRendering(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            matrixStackIn.pop();
        }
    }

    @Override
    public void translateRotate(MatrixStack matrixStackIn) {
        matrixStackIn.translate(
                this.rotationPointX * 0.0625F, // left/right??
                this.rotationPointY * 0.0625F, // up/down
                this.rotationPointZ * 0.0625F); // forward/backwards
        if (this.rotateAngleZ != 0.0F) {
            matrixStackIn.rotate(Vector3f.ZP.rotation(this.rotateAngleZ));
        }

        if (this.rotateAngleY != 0.0F) {
            matrixStackIn.rotate(Vector3f.YP.rotation(this.rotateAngleY));
        }

        if (this.rotateAngleX != 0.0F) {
            matrixStackIn.rotate(Vector3f.XP.rotation(this.rotateAngleX));
        }

        matrixStackIn.rotate(TransformationHelper.quatFromXYZ(new Vector3f(180, 0, 0), true));
    }

    private void doRendering(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        CompoundNBT renderSpec = ArmorModelInstance.getInstance().getRenderSpec();
        if (renderSpec != null) {
            MatrixStack.Entry entry = matrixStackIn.getLast();

            int[] colours = renderSpec.getIntArray(MPALIbConstants.TAG_COLOURS);

            if (colours.length == 0) {
                colours = new int[]{Colour.WHITE.getInt()};
            }

            int partColor;
            for (CompoundNBT nbt : NBTTagAccessor.getValues(renderSpec)) {
                PartSpecBase part = ModelRegistry.getInstance().getPart(nbt);
                if (part != null && part instanceof ModelPartSpec) {
                    if (part.getBinding().getSlot() == ArmorModelInstance.getInstance().getVisibleSection()
                            && part.getBinding().getTarget().apply(ArmorModelInstance.getInstance()) == parent) {
                        int ix = part.getColourIndex(nbt);
                        // checks the range of the index to avoid errors OpenGL or crashing
                        if (ix < colours.length && ix >= 0) {
                            partColor = colours[ix];
                        } else {
                            partColor = -1;
                        }

                        TransformationMatrix transform = ((ModelSpec) part.spec).getTransform(ItemCameraTransforms.TransformType.NONE);

                        // FIXME: not implemented yet
                        if (transform != TransformationMatrix.identity()) {
                            MatrixStack stack = new MatrixStack();
                            transform.push(stack);
                            // Apply the transformation to the real matrix stack
                            Matrix4f tMat = stack.getLast().getMatrix();
                            Matrix3f nMat = stack.getLast().getNormal();
                            matrixStackIn.getLast().getMatrix().mul(tMat);
                            matrixStackIn.getLast().getNormal().mul(nMat);
                        }

                        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                        Random random = new Random();
                        long i = 42L;
//                        Direction[] var7 = Direction.values();
//                        int var8 = var7.length;
//
//                        for (int var9 = 0; var9 < var8; ++var9) {
//                            Direction direction = var7[var9];
//                            random.setSeed(42L);
//                            builder.addAll(((ModelPartSpec) part).getPart().getQuads(null, direction, random));//, ItemCameraTransforms.TransformType.NONE, colour, glow));
//                        }

                        random.setSeed(i);
                        builder.addAll(((ModelPartSpec) part).getPart().getQuads(null, null, random));//, TransformType.NONE, colour, glow));

                        // TODO: optionally replaced packed light for glow

                        renderQuads(entry, bufferIn, builder.build(), packedLightIn, OverlayTexture.NO_OVERLAY /*packedOverlayIn*/, partColor);
                    }
                }
            }
        }
    }

    public void renderQuads(MatrixStack.Entry entry,
                            IVertexBuilder bufferIn,
                            List<BakedQuad> quadsIn,
                            int combinedLightIn,
                            int combinedOverlayIn, int colour) {
        float a = (float) (colour >> 24 & 255) * div255;
        float r = (float) (colour >> 16 & 255) * div255;
        float g = (float) (colour >> 8 & 255) * div255;
        float b = (float) (colour & 255) * div255;

        for (BakedQuad bakedquad : quadsIn) {
            addVertexData(bufferIn, entry, bakedquad, combinedLightIn, combinedOverlayIn, r, g, b, a);
        }
    }

    // Copy of addQuad with alpha support
    void addVertexData(IVertexBuilder bufferIn,
                       MatrixStack.Entry matrixEntry,
                       BakedQuad bakedQuad,
                       int lightmapCoordIn,
                       int overlayCoords, float red, float green, float blue, float alpha) {
        int[] aint = bakedQuad.getVertexData();
        Vec3i faceNormal = bakedQuad.getFace().getDirectionVec();
        Vector3f normal = new Vector3f((float) faceNormal.getX(), (float) faceNormal.getY(), (float) faceNormal.getZ());
        Matrix4f matrix4f = matrixEntry.getMatrix();// same as TexturedQuad renderer
        normal.transform(matrixEntry.getNormal()); // normals different here

        int intSize = DefaultVertexFormats.BLOCK.getIntegerSize();
        int vertexCount = aint.length / intSize;

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormats.BLOCK.getSize());
            IntBuffer intbuffer = bytebuffer.asIntBuffer();

            for (int v = 0; v < vertexCount; ++v) {
                ((Buffer) intbuffer).clear();
                intbuffer.put(aint, v * 8, 8);
                float f = bytebuffer.getFloat(0);
                float f1 = bytebuffer.getFloat(4);
                float f2 = bytebuffer.getFloat(8);
                int lightmapCoord = bufferIn.applyBakedLighting(lightmapCoordIn, bytebuffer);
                float f9 = bytebuffer.getFloat(16);
                float f10 = bytebuffer.getFloat(20);

                /** scaled like TexturedQuads, but using multiplication instead of division due to speed advantage.  */
                Vector4f pos = new Vector4f(f * 0.0625F, f1 * 0.0625F, f2 * 0.0625F, 1.0F); // scales to 1/16 like the TexturedQuads but with multiplication (faster than division)
                pos.transform(matrix4f);
                bufferIn.applyBakedNormals(normal, bytebuffer, matrixEntry.getNormal());
                bufferIn.addVertex(pos.getX(), pos.getY(), pos.getZ(), red, green, blue, alpha, f9, f10, overlayCoords, lightmapCoord, normal.getX(), normal.getY(), normal.getZ());
            }
        }
    }
}