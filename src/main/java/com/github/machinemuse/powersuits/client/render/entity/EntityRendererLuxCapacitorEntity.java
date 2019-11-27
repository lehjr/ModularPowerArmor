/*
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.client.render.entity;

import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.client.model.block.ModelLuxCapacitor;
import com.github.machinemuse.powersuits.basemod.MPSItems;
import com.github.machinemuse.powersuits.entity.EntityLuxCapacitor;
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

import static com.github.machinemuse.powersuits.block.BlockLuxCapacitor.COLOR;

public class EntityRendererLuxCapacitorEntity extends MuseEntityRenderer<EntityLuxCapacitor> {
    ModelLuxCapacitor luxCapacitorModel = new ModelLuxCapacitor();
    IExtendedBlockState blockState;
    public EntityRendererLuxCapacitorEntity(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityLuxCapacitor entity, double x, double y, double z, float entityYaw, float partialTicks) {
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