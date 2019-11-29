/*
 * ModularPowersuits (Maintenance builds by lehjr)
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

package com.github.machinemuse.powersuits.client.render.modelspec;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.client.render.RenderState;
import com.github.lehjr.mpalib.client.render.modelspec.ModelPartSpec;
import com.github.lehjr.mpalib.client.render.modelspec.ModelRegistry;
import com.github.lehjr.mpalib.client.render.modelspec.PartSpecBase;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.nbt.NBTTagAccessor;
import com.github.machinemuse.powersuits.client.model.item.armor.ArmorModelInstance;
import com.github.machinemuse.powersuits.client.model.item.armor.IArmorModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 4:16 AM, 29/04/13
 * <p>
 * Ported to Java by lehjr on 11/6/16.
 */
@SideOnly(Side.CLIENT)
public class RenderPart extends ModelRenderer {
    ModelRenderer parent;

    public RenderPart(ModelBase base, ModelRenderer parent) {
        super(base);
        this.parent = parent;
    }

    @Override
    public void render(float scale) {
        NBTTagCompound renderSpec = ((IArmorModel) (ArmorModelInstance.getInstance())).getRenderSpec();
        if (renderSpec == null)
            return;

        int[] colours = renderSpec.getIntArray(MPALIbConstants.TAG_COLOURS);
        if (colours.length == 0)
            colours = new int[]{Colour.WHITE.getInt()};

        int partColor;
        for (NBTTagCompound nbt : NBTTagAccessor.getValues(renderSpec)) {
            PartSpecBase part = ModelRegistry.getInstance().getPart(nbt);
            if (part != null && part instanceof ModelPartSpec) {
                if (part.getBinding().getSlot() == ((IArmorModel) (ArmorModelInstance.getInstance())).getVisibleSection()
                        && part.getBinding().getTarget().apply(ArmorModelInstance.getInstance()) == parent) {
                    List<BakedQuad> quadList = ((ModelPartSpec) part).getQuads();
                    if (!quadList.isEmpty()) {
                        int ix = part.getColourIndex(nbt);

                        // checks the range of the index to avoid errors OpenGL or crashing
                        if (ix < colours.length && ix >= 0) partColor = colours[ix];
                        else partColor = Colour.WHITE.getInt();

                        // GLOW stuff on
                        if (((ModelPartSpec) part).getGlow(nbt))
                            RenderState.glowOn();

                        GlStateManager.pushMatrix();
                        GlStateManager.scale(scale, scale, scale);
                        applyTransform();
                        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                        Tessellator tess = Tessellator.getInstance();
                        BufferBuilder buffer = tess.getBuffer();
                        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);

                        for (BakedQuad quad : ((ModelPartSpec) part).getQuads()) {
                            buffer.addVertexData(quad.getVertexData());
                            ForgeHooksClient.putQuadColor(buffer, quad, partColor);
                        }
                        tess.draw();

                        GlStateManager.popMatrix();

                        //Glow stuff off
                        if (((ModelPartSpec) part).getGlow(nbt))
                            RenderState.glowOff();
                    }
                }
            }
        }
    }

    private void applyTransform() {
//        float degrad = (float) (180F / Math.PI);
//        GL11.glTranslatef(rotationPointX, rotationPointY, rotationPointZ);
//        GL11.glRotatef(rotateAngleZ * degrad, 0.0F, 0.0F, 1.0F);
//        GL11.glRotatef(rotateAngleY * degrad, 0.0F, 1.0F, 0.0F);
//        GL11.glRotatef(rotateAngleX * degrad, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(offsetX, offsetY - 26, offsetZ);
    }
}