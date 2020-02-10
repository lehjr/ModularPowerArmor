package com.github.lehjr.modularpowerarmor.client.render.entity;

import com.github.lehjr.modularpowerarmor.basemod.MPAObjects;
import com.github.lehjr.modularpowerarmor.block.BlockLuxCapacitor;
import com.github.lehjr.modularpowerarmor.client.event.ModelBakeEventHandler;
import com.github.lehjr.modularpowerarmor.client.model.block.ModelLuxCapacitor;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.mpalib.client.render.entity.MPALibEntityRenderer;
import com.github.lehjr.mpalib.math.Colour;
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

public class EntityRendererLuxCapacitorEntity extends MPALibEntityRenderer<LuxCapacitorEntity> {


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
            BlockState blockState = MPAObjects.INSTANCE.luxCapacitor.getDefaultState().
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