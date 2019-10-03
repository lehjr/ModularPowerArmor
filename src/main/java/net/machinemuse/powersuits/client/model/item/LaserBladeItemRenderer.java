//package net.machinemuse.powersuits.client.model.item;
//
//package com.github.iunius118.tolaserblade.client.renderer;
//
//import com.github.iunius118.tolaserblade.ToLaserBlade;
//import com.github.iunius118.tolaserblade.ToLaserBladeConfig;
//import com.github.iunius118.tolaserblade.client.model.LaserBladeModel;
//import com.github.iunius118.tolaserblade.item.LaserBlade;
//import com.mojang.blaze3d.platform.GLX;
//import com.mojang.blaze3d.platform.GlStateManager;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.BufferBuilder;
//import net.minecraft.client.renderer.ItemRenderer;
//import net.minecraft.client.renderer.RenderHelper;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.renderer.model.BakedQuad;
//import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
//import net.minecraft.client.renderer.texture.AtlasTexture;
//import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
//import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.HandSide;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.Util;
//import net.minecraft.util.math.Vec3i;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.client.model.pipeline.LightUtil;
//import org.lwjgl.BufferUtils;
//import org.lwjgl.opengl.GL11;
//import org.lwjgl.opengl.GL14;
//
//import java.nio.FloatBuffer;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@OnlyIn(Dist.CLIENT)
//public class LaserBladeItemRenderer extends ItemStackTileEntityRenderer {
//    public static final ResourceLocation LASER_BLADE_TEXTURE_LOCATION = new ResourceLocation(ToLaserBlade.MOD_ID, "textures/item/laser_blade.png");
//
//    /*
//    // Add to client setup
//
//    LaserBladeItem.properties = LaserBladeItem.properties.setTEISR(() -> LaserBladeItemRenderer::new);
//     */
//
//
//    @Override
//    public void renderByItem(ItemStack itemStackIn) {
//        Minecraft mc = Minecraft.getInstance();
//        IBakedModel model = mc.getItemRenderer().getItemModelMesher().getModelManager().getModel(ToLaserBlade.MRL_ITEM_LASER_BLADE);
//
//        if (model instanceof LaserBladeModel) {
//            LaserBladeModel laserBladeModel = (LaserBladeModel) model;
//            doRender(itemStackIn, laserBladeModel);
//        }
//    }
//
//    public void doRender(ItemStack itemStackIn, LaserBladeModel model) {
//        BufferBuilder renderer = Tessellator.getInstance().getBuffer();
//        LaserBlade laserBlade = LaserBlade.create(itemStackIn);
//        int colorCore = laserBlade.getCoreColor();
//        int colorHalo = laserBlade.getHaloColor();
//        boolean isSubColorCore = laserBlade.isCoreSubColor();
//        boolean isSubColorHalo = laserBlade.isHaloSubColor();
//
//        TransformType cameraTransformType = model.cameraTransformType;
//
//        // Transform by Blocking
//        boolean isBlocking = ToLaserBladeConfig.COMMON.isEnabledBlockingWithLaserBladeInServer.get()
//                && (cameraTransformType == TransformType.FIRST_PERSON_RIGHT_HAND || cameraTransformType == TransformType.FIRST_PERSON_LEFT_HAND)
//                && model.entity != null && model.entity.isHandActive();
//
//        // Transform by camera type
//        transform(cameraTransformType, isBlocking);
//
//        // Enable culling
//        boolean isEnableCull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
//
//        if (!isEnableCull) {
//            GlStateManager.enableCull();
//        }
//
//        // Start rendering LaserBlade
//
//        // Bind LaserBlade texture
//        Minecraft.getInstance().getTextureManager().bindTexture(LASER_BLADE_TEXTURE_LOCATION);
//
//        // Draw hilt
//        renderQuads(renderer, model.getQuadsByName("Hilt"), -1);
//
//        // Enable bright rendering
//        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
//        RenderHelper.disableStandardItemLighting();
//        float lastBrightnessX = GLX.lastBrightnessX;
//        float lastBrightnessY = GLX.lastBrightnessY;
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
//
//        // Draw bright part of hilt
//        renderQuads(renderer, model.getQuadsByName("Hilt_bright"), -1);
//
//        if (ToLaserBladeConfig.CLIENT.laserBladeRenderingMode.get() == 1) {
//            // Rendering Mode 1: Using only alpha blending
//
//            if (isSubColorCore) {
//                colorCore = ~colorCore | 0xFF000000;
//            }
//
//            if (isSubColorHalo) {
//                colorHalo = ~colorHalo | 0xFF000000;
//            }
//
//            // Draw blade parts
//            renderQuads(renderer, model.getQuadsByName("Blade_halo_2"), colorHalo);
//            renderQuads(renderer, model.getQuadsByName("Blade_halo_1"), colorHalo);
//            renderQuads(renderer, model.getQuadsByName("Blade_core"), colorCore);
//
//        } else {
//            // Rendering Mode 0: Default
//
//            // Enable Add-color
//            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
//            GL14.glBlendEquation(GL14.GL_FUNC_ADD);
//
//            // Draw blade core
//            if (isSubColorCore) {
//                // Draw core with Sub-color
//                GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
//            }
//
//            renderQuads(renderer, model.getQuadsByName("Blade_core"), colorCore);
//
//            // Draw blade halo
//            if (!isSubColorCore && isSubColorHalo) {
//                // Draw halo with Sub-color
//                GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
//            } else if (isSubColorCore && !isSubColorHalo) {
//                GL14.glBlendEquation(GL14.GL_FUNC_ADD);
//            }
//
//            renderQuads(renderer, model.getQuadsByName("Blade_halo_1"), colorHalo);
//            renderQuads(renderer, model.getQuadsByName("Blade_halo_2"), colorHalo);
//
//            if (isSubColorHalo) {
//                GL14.glBlendEquation(GL14.GL_FUNC_ADD);
//            }
//
//            // Disable Add-color
//            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        }
//
//        // Disable bright rendering
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, lastBrightnessX, lastBrightnessY);
//        RenderHelper.enableStandardItemLighting();
//        GL11.glPopAttrib();
//
//        // Restore texture
//        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
//
//        // Render Enchantment effect
//        if (itemStackIn.hasEffect()) {
//            renderEffect(model.getQuadsByName("Hilt"));
//            renderEffect(model.getQuadsByName("Hilt_bright"));
//        }
//
//        // Disable Culling
//        if (!isEnableCull) {
//            GlStateManager.disableCull();
//        }
//    }
//
//    // Prepare transform matrices
//    public static final Map<TransformType, float[]> transformMatrices;
//
//    static {
//        transformMatrices = new HashMap<>();
//        transformMatrices.put(TransformType.FIRST_PERSON_LEFT_HAND, new float[]{-3.090862E-8F, 3.090862E-8F, -1.0F, 0.0F, 0.8838835F, 0.8838835F, 0.0F, 0.0F, 0.70710677F, -0.70710677F, -4.371139E-8F, 0.0F, -0.030330122F, -0.030330122F, 0.5F, 1.0F});
//        transformMatrices.put(TransformType.FIRST_PERSON_RIGHT_HAND, new float[]{-3.090862E-8F, 3.090862E-8F, -1.0F, 0.0F, 0.8838835F, 0.8838835F, 0.0F, 0.0F, 0.70710677F, -0.70710677F, -4.371139E-8F, 0.0F, -0.030330122F, -0.030330122F, 0.5F, 1.0F});
//        transformMatrices.put(TransformType.THIRD_PERSON_LEFT_HAND, new float[]{-3.244294E-8F, 4.633332E-8F, -1.294F, 0.0F, 0.94637173F, 0.8825059F, 7.871984E-9F, 0.0F, 0.8825059F, -0.94637173F, -5.6012073E-8F, 0.0F, 0.035000555F, 0.030994587F, 0.5F, 1.0F});
//        transformMatrices.put(TransformType.THIRD_PERSON_RIGHT_HAND, new float[]{-3.244294E-8F, 4.633332E-8F, -1.294F, 0.0F, 0.94637173F, 0.8825059F, 7.871984E-9F, 0.0F, 0.8825059F, -0.94637173F, -5.6012073E-8F, 0.0F, 0.035000555F, 0.030994587F, 0.5F, 1.0F});
//        transformMatrices.put(TransformType.FIXED, new float[]{-5.0862745E-8F, -2.7817755E-8F, -0.9F, 0.0F, 0.63639605F, 0.63639605F, -5.5635514E-8F, 0.0F, 0.63639605F, -0.63639605F, -1.6295264E-8F, 0.0F, 0.022702962F, 0.022702962F, 0.52250004F, 1.0F});
//        transformMatrices.put(TransformType.NONE, new float[]{-2.7817755E-8F, 2.7817755E-8F, -0.9F, 0.0F, 0.63639605F, 0.63639605F, 0.0F, 0.0F, 0.63639605F, -0.63639605F, -3.934025E-8F, 0.0F, 0.022702962F, 0.022702962F, 0.5F, 1.0F});
//    }
//
//    public static final Map<TransformType, float[]> transformMatricesBlockingRight;
//
//    static {
//        transformMatricesBlockingRight = new HashMap<>();
//        transformMatricesBlockingRight.put(TransformType.FIRST_PERSON_LEFT_HAND, new float[]{-0.04950499F, -0.8617275F, -0.50495046F, 0.0F, 0.10771594F, 0.62499994F, -1.0771594F, 0.0F, 0.9950494F, -0.08617279F, 0.049504925F, 0.0F, 0.45283374F, 0.05398178F, 0.6716627F, 1.0F});
//        transformMatricesBlockingRight.put(TransformType.FIRST_PERSON_RIGHT_HAND, new float[]{-0.04950499F, 0.8617275F, -0.50495046F, 0.0F, -0.10771594F, 0.62499994F, 1.0771594F, 0.0F, 0.9950494F, 0.08617279F, 0.049504925F, 0.0F, 0.5471663F, 0.05398178F, 0.3283373F, 1.0F});
//    }
//
//    public static final Map<TransformType, float[]> transformMatricesBlockingLeft;
//
//    static {
//        transformMatricesBlockingLeft = new HashMap<>();
//        transformMatricesBlockingLeft.put(TransformType.FIRST_PERSON_LEFT_HAND, new float[]{0.049504902F, -0.8617275F, -0.50495046F, 0.0F, -0.10771594F, 0.62499994F, -1.0771594F, 0.0F, 0.9950494F, 0.086172715F, -0.04950497F, 0.0F, 0.5471663F, 0.05398178F, 0.6716627F, 1.0F});
//        transformMatricesBlockingLeft.put(TransformType.FIRST_PERSON_RIGHT_HAND, new float[]{0.049504902F, 0.8617275F, -0.50495046F, 0.0F, 0.10771594F, 0.62499994F, 1.0771594F, 0.0F, 0.9950494F, -0.086172715F, -0.04950497F, 0.0F, 0.45283374F, 0.05398178F, 0.3283373F, 1.0F});
//    }
//
//    private static final FloatBuffer matrixBuf = BufferUtils.createFloatBuffer(16);
//
//    public void transform(TransformType cameraTransformType, boolean isBlocking) {
//        matrixBuf.clear();
//
//        float[] matrix;
//
//        if (isBlocking) {
//            if (Minecraft.getInstance().gameSettings.mainHand == HandSide.RIGHT) {
//                matrix = transformMatricesBlockingRight.get(cameraTransformType);
//            } else {
//                matrix = transformMatricesBlockingLeft.get(cameraTransformType);
//            }
//        } else {
//            matrix = transformMatrices.get(cameraTransformType);
//        }
//
//        if (matrix == null) {
//            matrix = transformMatrices.get(TransformType.NONE);
//        }
//
//        matrixBuf.put(matrix);
//
///*
//		// Calculate transformation matrix (for debugging)
//		GlStateManager.pushMatrix();
//		GlStateManager.loadIdentity();
//		GlStateManager.translatef(0.5F, 0.5F, 0.5F);
//		switch (cameraTransformType)
//		{
//		case FIRST_PERSON_LEFT_HAND:
//		case FIRST_PERSON_RIGHT_HAND:
//		    GlStateManager.rotatef(45.0F, 0.0F, 0.0F, -1.0F);	// GlStateManager.rotatef(60.0F, 1.0F, 0.0F, 0.1F); // <- For Blocking with Right hand (Main)
//		    GlStateManager.scaled(1.0D, 1.25D, 1.0D);
//		    GlStateManager.translatef(0.0F, -0.6F, 0.0F);	// GlStateManager.translatef(0.0F, -0.3F, 0.3F); // <- For Blocking with Right hand (Main)
//		    GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
//		    break;
//		case THIRD_PERSON_LEFT_HAND:
//		case THIRD_PERSON_RIGHT_HAND:
//		    GlStateManager.rotatef(-55.0F, 0.0F, 0.0F, 1.0F);
//		    GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
//		    GlStateManager.rotatef(-8.0F, 1.0F, 0.0F, 0.0F);
//		    GlStateManager.scaled(1.294D, 1.294D, 1.294D);
//		    GlStateManager.translatef(0.0F, -0.45F, 0.0F);
//		    GlStateManager.translatef(0.0F, -0.06F, 0.02F);
//		    break;
//		case FIXED:
//		    GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
//		    GlStateManager.scaled(0.9D, 0.9D, 0.9D);
//		    GlStateManager.rotatef(45.0F, 0.0F, 0.0F, 1.0F);
//		    GlStateManager.translatef(0.0F, -0.75F, -0.025F);
//		    GlStateManager.rotatef(90.0F, 0.0F, -1.0F, 0.0F);
//		    break;
//		default:
//		    GlStateManager.scaled(0.9D, 0.9D, 0.9D);
//		    GlStateManager.rotatef(45.0F, 0.0F, 0.0F, -1.0F);
//		    GlStateManager.translatef(0.0F, -0.75F, 0.0F);
//		    GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
//		}
//		matrixBuf.clear();
//		GlStateManager.getFloatv(GL11.GL_MODELVIEW_MATRIX, matrixBuf);
//		GlStateManager.popMatrix();
//		matrix = new float[16];
//		matrixBuf.get(matrix);	// Put transformation matrix in matrix
//		// */
//
//        matrixBuf.flip();
//        GlStateManager.multMatrix(matrixBuf);
//    }
//
//    public void renderEffect(List<BakedQuad> quads) {
//        BufferBuilder renderer = Tessellator.getInstance().getBuffer();
//
//        // Render Enchantment effect for hilt
//        GlStateManager.depthMask(false);
//        GlStateManager.depthFunc(GL11.GL_EQUAL);
//        GlStateManager.disableLighting();
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
//        Minecraft.getInstance().getTextureManager().bindTexture(ItemRenderer.RES_ITEM_GLINT);
//        GlStateManager.matrixMode(GL11.GL_TEXTURE);
//        GlStateManager.pushMatrix();
//        GlStateManager.scalef(8.0F, 8.0F, 8.0F);
//        float f = (float) (Util.milliTime() % 3000L) / 3000.0F / 8.0F;
//        GlStateManager.translatef(f, 0.0F, 0.0F);
//        GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
//
//        renderQuads(renderer, quads, 0xFF8040CC);
//
//        GlStateManager.popMatrix();
//        GlStateManager.pushMatrix();
//        GlStateManager.scalef(8.0F, 8.0F, 8.0F);
//        float f1 = (float) (Util.milliTime() % 4873L) / 4873.0F / 8.0F;
//        GlStateManager.translatef(-f1, 0.0F, 0.0F);
//        GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
//
//        renderQuads(renderer, quads, 0xFF8040CC);
//
//        GlStateManager.popMatrix();
//        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//        GlStateManager.enableLighting();
//        GlStateManager.depthFunc(GL11.GL_LEQUAL);
//        GlStateManager.depthMask(true);
//        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
//    }
//
//    public void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color) {
//        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
//
//        // Render Quads
//        for (BakedQuad quad : quads) {
//            LightUtil.renderQuadColor(renderer, quad, color);
//            Vec3i vec3i = quad.getFace().getDirectionVec();
//            renderer.putNormal((float) vec3i.getX(), (float) vec3i.getY(), (float) vec3i.getZ());
//        }
//
//        Tessellator.getInstance().draw();
//    }
//}
