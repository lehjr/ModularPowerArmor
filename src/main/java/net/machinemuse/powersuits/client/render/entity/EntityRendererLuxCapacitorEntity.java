package net.machinemuse.powersuits.client.render.entity;

import net.machinemuse.numina.client.render.entity.MuseEntityRenderer;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.basemod.MPSObjects;
import net.machinemuse.powersuits.block.BlockLuxCapacitor;
import net.machinemuse.powersuits.client.event.ModelBakeEventHandler;
import net.machinemuse.powersuits.client.model.block.ModelLuxCapacitor;
import net.machinemuse.powersuits.entity.LuxCapacitorEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.ModelDataMap;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.Random;

public class EntityRendererLuxCapacitorEntity extends MuseEntityRenderer<LuxCapacitorEntity> {


    public EntityRendererLuxCapacitorEntity(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(LuxCapacitorEntity luxCapacitorEntity) {
        return null;
    }

    @Override
    public void doRender(LuxCapacitorEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (ModelBakeEventHandler.INSTANCE.luxCapModel != null && ModelBakeEventHandler.INSTANCE.luxCapModel instanceof ModelLuxCapacitor) {
            BlockState blockState = MPSObjects.INSTANCE.luxCapacitor.getDefaultState().
                        with(DirectionalBlock.FACING, Direction.DOWN);
                GL11.glPushMatrix();
                GL11.glTranslated(x, y, z);
                Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                Tessellator tess = Tessellator.getInstance();
                BufferBuilder buffer = tess.getBuffer();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
                for (BakedQuad quad : ModelBakeEventHandler.INSTANCE.luxCapModel.getQuads(blockState, null, new Random(),
                        new ModelDataMap.Builder().withInitial(BlockLuxCapacitor.COLOUR_PROP, entity.color.getInt()).build())) {
                    buffer.addVertexData(quad.getVertexData());
                    ForgeHooksClient.putQuadColor(buffer, quad, Colour.WHITE.getInt());
                }
                tess.draw();
                GL11.glPopMatrix();
        } else
        System.out.println("not a model luxCapacitor");

    }
}