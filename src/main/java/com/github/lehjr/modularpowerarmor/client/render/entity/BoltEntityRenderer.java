//package com.github.lehjr.modularpowerarmor.client.render.entity;
//
//import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
//import com.github.lehjr.modularpowerarmor.entity.BoltEntity;
//import com.mojang.blaze3d.platform.GlStateManager;
//import net.minecraft.client.renderer.BufferBuilder;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.renderer.entity.EntityRenderer;
//import net.minecraft.client.renderer.entity.EntityRendererManager;
//import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.MathHelper;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//@OnlyIn(Dist.CLIENT)
//public class BoltEntityRenderer<T extends BoltEntity> extends EntityRenderer<T> {
//    public static final ResourceLocation BOLT_TEXTURE = new ResourceLocation(MPAConstants.MOD_ID, "textures/models/bolt.png");
//
//    public BoltEntityRenderer(EntityRendererManager renderManagerIn) {
//        super(renderManagerIn);
//    }
//
//    @Override
//    public ResourceLocation getEntityTexture(BoltEntity entity) {
//        return BOLT_TEXTURE;
//    }
//
////    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
////        this.bindEntityTexture(entity);
////        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
////        GlStateManager.pushMatrix();
////        GlStateManager.disableLighting();
////        GlStateManager.translatef((float)x, (float)y, (float)z);
////        GlStateManager.rotatef(MathHelper.lerp(partialTicks, entity.prevRotationYaw, entity.rotationYaw) - 90.0F, 0.0F, 1.0F, 0.0F);
////        GlStateManager.rotatef(MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch), 0.0F, 0.0F, 1.0F);
////        Tessellator tessellator = Tessellator.getInstance();
////        BufferBuilder bufferbuilder = tessellator.getBuffer();
////        int i = 0;
////        float f = 0.0F;
////        float f1 = 0.5F;
////        float f2 = 0.0F;
////        float f3 = 0.15625F;
////        float f4 = 0.0F;
////        float f5 = 0.15625F;
////        float f6 = 0.15625F;
////        float f7 = 0.3125F;
////        float f8 = 0.05625F;
////        GlStateManager.enableRescaleNormal();
////
////        GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
////        GlStateManager.scalef(0.05625F, 0.05625F, 0.05625F);
////        GlStateManager.translatef(-4.0F, 0.0F, 0.0F);
////        if (this.renderOutlines) {
////            GlStateManager.enableColorMaterial();
////            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
////        }
////
////        GlStateManager.normal3f(0.05625F, 0.0F, 0.0F);
////        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
////        bufferbuilder.pos(-7.0D, -2.0D, -2.0D).tex(0.0F, 0.15625F).endVertex();
////        bufferbuilder.pos(-7.0D, -2.0D, 2.0D).tex(0.15625F, 0.15625F).endVertex();
////        bufferbuilder.pos(-7.0D, 2.0D, 2.0D).tex(0.15625F, 0.3125F).endVertex();
////        bufferbuilder.pos(-7.0D, 2.0D, -2.0D).tex(0.0F, 0.3125F).endVertex();
////        tessellator.draw();
////
////        GlStateManager.normal3f(-0.05625F, 0.0F, 0.0F);
////        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
////        bufferbuilder.pos(-7.0D, 2.0D, -2.0D).tex(0.0F, 0.15625F).endVertex();
////        bufferbuilder.pos(-7.0D, 2.0D, 2.0D).tex(0.15625F, 0.15625F).endVertex();
////        bufferbuilder.pos(-7.0D, -2.0D, 2.0D).tex(0.15625F, 0.3125F).endVertex();
////        bufferbuilder.pos(-7.0D, -2.0D, -2.0D).tex(0.0F, 0.3125F).endVertex();
////        tessellator.draw();
////
////        // arrow body
////        for(int j = 0; j < 4; ++j) {
////            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
////            GlStateManager.normal3f(0.0F, 0.0F, 0.05625F);
////            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
////            bufferbuilder.pos(-8.0D, -2.0D, 0.0D).tex(0.0D, 0.0D).endVertex();
////            bufferbuilder.pos(8.0D, -2.0D, 0.0D).tex(0.5D, 0.0D).endVertex();
////            bufferbuilder.pos(8.0D, 2.0D, 0.0D).tex(0.5D, 0.15625D).endVertex();
////            bufferbuilder.pos(-8.0D, 2.0D, 0.0D).tex(0.0D, 0.15625D).endVertex();
////            tessellator.draw();
////        }
////
////        if (this.renderOutlines) {
////            GlStateManager.tearDownSolidRenderingTextureCombine();
////            GlStateManager.disableColorMaterial();
////        }
////
////        GlStateManager.disableRescaleNormal();
////        GlStateManager.enableLighting();
////        GlStateManager.popMatrix();
////        super.doRender(entity, x, y, z, entityYaw, partialTicks);
////    }
//}