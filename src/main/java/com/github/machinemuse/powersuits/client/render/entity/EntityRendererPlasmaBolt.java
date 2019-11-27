/*
 * Copyright (c) ${DATE} MachineMuse, Lehjr
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

import com.github.lehjr.mpalib.client.gui.geometry.DrawableCircle;
import com.github.lehjr.mpalib.client.render.RenderState;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.github.machinemuse.powersuits.entity.EntityPlasmaBolt;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.DoubleBuffer;

import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;

public class EntityRendererPlasmaBolt extends MuseEntityRenderer<EntityPlasmaBolt> {
    public static DoubleBuffer unrotatebuffer;
    protected static DrawableCircle circle1;
    protected static DrawableCircle circle2;
    protected static DrawableCircle circle3;
    protected static DrawableCircle circle4;

    public EntityRendererPlasmaBolt(RenderManager renderManager) {
        super(renderManager);
        Colour c1 = new Colour(.3, .3, 1, 0.3);
        circle1 = new DrawableCircle(c1, c1);
        c1 = new Colour(.3, .3, 1, 0.6);
        circle2 = new DrawableCircle(c1, c1);
        c1 = new Colour(.3, .3, 1, 1);
        circle3 = new DrawableCircle(c1, c1);
        circle4 = new DrawableCircle(c1, new Colour(1, 1, 1, 1));
    }

    public static void doRender(double size) {
        GL11.glPushMatrix();
        Renderer.unRotate();
        double scale = size / 16.0;
        GL11.glScaled(scale, scale, scale);
        int millisPerCycle = 500;
        double timeScale = Math.cos((System.currentTimeMillis() % millisPerCycle) * 2.0 / millisPerCycle - 1.0);
        RenderState.glowOn();
        circle1.draw(4, 0, 0);
        GL11.glTranslated(0, 0, 0.001);
        circle2.draw(3 + timeScale / 2, 0, 0);
        GL11.glTranslated(0, 0, 0.001);
        circle3.draw(2 + timeScale, 0, 0);
        GL11.glTranslated(0, 0, 0.001);
        circle4.draw(1 + timeScale, 0, 0);
        for (int i = 0; i < 3; i++) {
            double angle1 = (Math.random() * 2 * Math.PI);
            double angle2 = (Math.random() * 2 * Math.PI);
            Renderer.drawLightning(Math.cos(angle1) * 0.5, Math.sin(angle1) * 0.5, 0, Math.cos(angle2) * 5, Math.sin(angle2) * 5, 1,
                    new Colour(1, 1, 1, 0.9));
        }
        RenderState.glowOff();
        GL11.glPopMatrix();
    }

    public static void doRender(double boltSizeIn, ItemCameraTransforms.TransformType cameraTransformTypeIn) {
        if (boltSizeIn != 0) {
            GL11.glPushMatrix();
            if (cameraTransformTypeIn == FIRST_PERSON_RIGHT_HAND || cameraTransformTypeIn == FIRST_PERSON_LEFT_HAND) {
                GL11.glScaled(0.0625f, 0.0625f, 0.0625f); // negative scale mirrors the model
                GL11.glRotatef(-182, 1, 0, 0);

            } else {
                GL11.glScaled(0.0625f, 0.0625f, 0.0625f);
                GL11.glTranslatef(0, 0, 20.3f);
//                GL11.glTranslatef(0, 0, 1.3f);
                GL11.glRotatef(-196, 1, 0, 0);
            }
            //---
            GL11.glTranslated(-1, 1, 16);
            GL11.glPushMatrix();
            EntityRendererPlasmaBolt.doRender(boltSizeIn);
            GL11.glPopMatrix();
            // ---
            GL11.glPopMatrix();
        }
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method,
     * always casting down its argument and then handing it off to a worker
     * function which does the actual work. In all probabilty, the class Render
     * is generic (Render<T extends Entity) and this method has signature public
     * void doRender(T entity, double d, double d1, double d2, float f, float
     * f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
    public void doRender(EntityPlasmaBolt entity, double x, double y, double z, float entityYaw, float partialTicks) {
        double size = (entity.size) / 10.0;
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        doRender(size);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
}