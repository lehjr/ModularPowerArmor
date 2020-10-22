package com.github.lehjr.modularpowerarmor.client.renderer.entity;

import com.github.lehjr.modularpowerarmor.entity.PlasmaBoltEntity;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableCircle;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.client.render.entity.MPALibEntityRenderer;
import com.github.lehjr.mpalib.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.nio.DoubleBuffer;

public class PlasmaBoltEntityRenderer extends MPALibEntityRenderer<PlasmaBoltEntity> {
    public static DoubleBuffer unrotatebuffer;
    protected static DrawableCircle circle1;
    protected static DrawableCircle circle2;
    protected static DrawableCircle circle3;
    protected static DrawableCircle circle4;

    public PlasmaBoltEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        Colour c1 = new Colour(0.3F, 0.3F, 1, 0.3F);
        circle1 = new DrawableCircle(c1, c1);
        c1 = new Colour(0.3F, 0.3F, 1, 0.6F);
        circle2 = new DrawableCircle(c1, c1);
        c1 = new Colour(0.3F, 0.3F, 1, 1);
        circle3 = new DrawableCircle(c1, c1);
        circle4 = new DrawableCircle(c1, new Colour(1, 1, 1, 1));
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(PlasmaBoltEntity plasmaBoltEntity) {
        return null;
    }

    public static void doRender(float size, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        // TODO: check if possible to use old render method but with each render loop as scheduled task. Also text performance




        matrixStackIn.push();
//        RenderSystem.popMatrix();// ???


        Renderer.unRotate();
        float scale = size / 16.0F;
        matrixStackIn.scale(scale, scale, scale);
        int millisPerCycle = 500;
        double timeScale = Math.cos((System.currentTimeMillis() % millisPerCycle) * 2.0 / millisPerCycle - 1.0);
//        RenderState.glowOn();
        circle1.draw(4, 0, 0, 0, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.translate(0, 0, 0.001);
        circle2.draw((float) (3 + timeScale / 2), 0, 0, 0, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.translate(0, 0, 0.001);
        circle3.draw((float) (2 + timeScale), 0, 0, 0, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.translate(0, 0, 0.001);
        circle4.draw((float) (1 + timeScale), 0, 0, 0, matrixStackIn, bufferIn, packedLightIn);
        for (int i = 0; i < 3; i++) {
            double angle1 = (Math.random() * 2 * Math.PI);
            double angle2 = (Math.random() * 2 * Math.PI);
            Renderer.drawLightning(
                    Math.cos(angle1) * 0.5,
                    Math.sin(angle1) * 0.5, 0,
                    Math.cos(angle2) * 5,
                    Math.sin(angle2) * 5, 1,
                    new Colour(1, 1, 1, 0.9F));
        }
//        RenderState.glowOff();
        matrixStackIn.pop();
    }




//    public static void doRender(double boltSizeIn, ItemCameraTransforms.TransformType cameraTransformTypeIn) {
//        if (boltSizeIn != 0) {
//            RenderSystem.pushMatrix();
//            if (cameraTransformTypeIn == FIRST_PERSON_RIGHT_HAND || cameraTransformTypeIn == FIRST_PERSON_LEFT_HAND) {
//                RenderSystem.scaled(0.0625f, 0.0625f, 0.0625f); // negative scale mirrors the model
//                RenderSystem.rotatef(-182, 1, 0, 0);
//
//            } else {
//                RenderSystem.scaled(0.0625f, 0.0625f, 0.0625f);
//                RenderSystem.translatef(0, 0, 20.3f);
////                GL11.glTranslatef(0, 0, 1.3f);
//                RenderSystem.rotatef(-196, 1, 0, 0);
//            }
//            //---
//            RenderSystem.translated(-1, 1, 16);
//            RenderSystem.pushMatrix();
//            PlasmaBoltEntityRenderer.doRender(boltSizeIn);
//            RenderSystem.popMatrix();;
//            // ---
//            RenderSystem.popMatrix();
//        }
//    }



    /**
     * Actually renders the given argument. This is a synthetic bridge method,
     * always casting down its argument and then handing it off to a worker
     * function which does the actual work. In all probabilty, the class Render
     * is generic (Render<T extends Entity) and this method has signature public
     * void doRender(T entity, double d, double d1, double d2, float f, float
     * f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
    public void render(PlasmaBoltEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        float size = (float)(entityIn.size) / 10.0F;
        matrixStackIn.push();
        matrixStackIn.translate(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ());
        matrixStackIn.scale(0.5F, 0.5F, 0.5F);
        doRender(size, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.pop();
    }
}