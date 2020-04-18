package com.github.lehjr.modularpowerarmor.client.renderer;

import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.mpalib.client.render.entity.MPALibEntityRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class LuxCapacitorEntityRenderer extends MPALibEntityRenderer<LuxCapacitorEntity> {


    public LuxCapacitorEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    public ResourceLocation getEntityTexture(LuxCapacitorEntity luxCapacitorEntity) {
        return null;
    }

    @Override
    public void render(LuxCapacitorEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
//        if (ModelBakeEventHandler.INSTANCE.luxCapModel != null && ModelBakeEventHandler.INSTANCE.luxCapModel instanceof ModelLuxCapacitor) {
//            BlockState blockState = MPAObjects.INSTANCE.luxCapacitor.getDefaultState().
//                        with(DirectionalBlock.FACING, Direction.DOWN);
//                GL11.glPushMatrix();
//                GL11.glTranslated(x, y, z);
//                Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
//                Tessellator tess = Tessellator.getInstance();
//                BufferBuilder buffer = tess.getBuffer();
//                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
//                for (BakedQuad quad : ModelBakeEventHandler.INSTANCE.luxCapModel.getQuads(blockState, null, new Random(),
//                        new ModelDataMap.Builder().withInitial(BlockLuxCapacitor.COLOUR_PROP, entity.color.getInt()).build())) {
//                    buffer.addVertexData(quad.getVertexData());
//                    ForgeHooksClient.putQuadColor(buffer, quad, Colour.WHITE.getInt());
//                }
//                tess.draw();
//                GL11.glPopMatrix();
//        } else
//        System.out.println("not a model luxCapacitor");

    }
}