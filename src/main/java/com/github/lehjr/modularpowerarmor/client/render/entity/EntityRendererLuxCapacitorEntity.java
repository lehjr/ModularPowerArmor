package com.github.lehjr.modularpowerarmor.client.render.entity;

import com.github.lehjr.modularpowerarmor.client.model.block.ModelLuxCapacitor;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.modularpowerarmor.common.MPSItems;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import net.minecraft.block.BlockDirectional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.lwjgl.opengl.GL11;

import static com.github.lehjr.modularpowerarmor.block.BlockLuxCapacitor.COLOR;

public class EntityRendererLuxCapacitorEntity extends MuseEntityRenderer<LuxCapacitorEntity> {
    ModelLuxCapacitor luxCapacitorModel = new ModelLuxCapacitor();
    IExtendedBlockState blockState;
    public EntityRendererLuxCapacitorEntity(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(LuxCapacitorEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (luxCapacitorModel != null) {
            blockState = ((IExtendedBlockState) MPSItems.INSTANCE.luxCapacitor.getDefaultState().
                    withProperty(BlockDirectional.FACING, EnumFacing.DOWN)).withProperty(COLOR, entity.color);
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
            for (BakedQuad quad : luxCapacitorModel.getQuads(blockState, null, 0)) {
                buffer.addVertexData(quad.getVertexData());
                ForgeHooksClient.putQuadColor(buffer, quad, Colour.WHITE.getInt());
            }
            tess.draw();
            GL11.glPopMatrix();
        }
    }
}