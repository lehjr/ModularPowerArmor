package net.machinemuse.powersuits.client.render.entity;

import net.machinemuse.numina.client.render.entity.MuseEntityRenderer;
import net.machinemuse.powersuits.client.model.block.ModelLuxCapacitor;
import net.machinemuse.powersuits.entity.LuxCapacitorEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class EntityRendererLuxCapacitorEntity extends MuseEntityRenderer<LuxCapacitorEntity> {
//    ModelLuxCapacitor luxCapacitorModel = new ModelLuxCapacitor(tinkertableUnbaked.bake(event.getModelLoader(), MuseModelHelper.defaultTextureGetter(), ModelRotation.X0_Y0, DefaultVertexFormats.ITEM));


    public EntityRendererLuxCapacitorEntity(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation func_110775_a(LuxCapacitorEntity luxCapacitorEntity) {
        return null;
    }


    @Override
    public void /* doRender */ func_76986_a(LuxCapacitorEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
//        if (luxCapacitorModel != null) {
//                blockState = (IExtendedBlockState) ((IExtendedBlockState) MPSItems.INSTANCE.luxCapacitor.getDefaultState().
//                        with(DirectionalBlock.FACING, Direction.DOWN)).withProperty(COLOR, entity.color);
//                GL11.glPushMatrix();
//                GL11.glTranslated(x, y, z);
//                Minecraft.getInstance().textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//                Tessellator tess = Tessellator.getInstance();
//                BufferBuilder buffer = tess.getBuffer();
//                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
//                for (BakedQuad quad : luxCapacitorModel.getQuads(blockState, null, new Random())) {
//                    buffer.addVertexData(quad.getVertexData());
//                    ForgeHooksClient.putQuadColor(buffer, quad, Colour.WHITE.getInt());
//                }
//                tess.draw();
//                GL11.glPopMatrix();
//        }
    }
}