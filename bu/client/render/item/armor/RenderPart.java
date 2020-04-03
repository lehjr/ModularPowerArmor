package com.github.lehjr.modularpowerarmor.client.render.item.armor;

import com.github.lehjr.modularpowerarmor.client.model.item.ArmorModelInstance;
import com.github.lehjr.modularpowerarmor.client.model.item.HighPolyArmor;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.client.render.RenderState;
import com.github.lehjr.mpalib.client.render.modelspec.ModelPartSpec;
import com.github.lehjr.mpalib.client.render.modelspec.ModelRegistry;
import com.github.lehjr.mpalib.client.render.modelspec.PartSpecBase;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.nbt.NBTTagAccessor;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 4:16 AM, 29/04/13
 * <p>
 * Ported to Java by lehjr on 11/6/16.
 */
@OnlyIn(Dist.CLIENT)
public class RenderPart extends RendererModel {
    RendererModel parent;

    public RenderPart(Model base, RendererModel parent) {
        super(base);
        this.parent = parent;
    }

    @Override
    public void render(float scale) {
        CompoundNBT renderSpec = ((HighPolyArmor) (ArmorModelInstance.getInstance())).getRenderSpec();
        if (renderSpec == null)
            return;

        int[] colours = renderSpec.getIntArray(MPALIbConstants.TAG_COLOURS);
        if (colours.length == 0)
            colours = new int[]{Colour.WHITE.getInt()};

        int partColor;
        for (CompoundNBT nbt : NBTTagAccessor.getValues(renderSpec)) {
             PartSpecBase part = ModelRegistry.getInstance().getPart(nbt);
            if (part != null && part instanceof ModelPartSpec) {
                if (part.getBinding().getSlot() == ((HighPolyArmor) (ArmorModelInstance.getInstance())).getVisibleSection()
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
                        GlStateManager.scalef(scale, scale, scale);
                        applyTransform();
                        Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
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
        GlStateManager.rotatef(180, 1.0F, 0.0F, 0.0F);
        GlStateManager.translatef(offsetX, offsetY - 26, offsetZ);
    }
}