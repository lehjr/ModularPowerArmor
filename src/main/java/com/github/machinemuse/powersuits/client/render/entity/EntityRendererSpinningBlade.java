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

import com.github.lehjr.mpalib.client.render.TextureUtils;
import com.github.machinemuse.powersuits.api.constants.MPSModConstants;
import com.github.machinemuse.powersuits.api.constants.MPSResourceConstants;
import com.github.machinemuse.powersuits.entity.EntitySpinningBlade;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class EntityRendererSpinningBlade extends MuseEntityRenderer<EntitySpinningBlade> {
    public EntityRendererSpinningBlade(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySpinningBlade entity) {
        return new ResourceLocation(MPSModConstants.MODID, "modules/spinningblade.png");
    }

    @Override
    public void doRender(EntitySpinningBlade entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_CULL_FACE);
        TextureUtils.pushTexture(MPSResourceConstants.TEXTURE_PREFIX + "modules/spinningblade.png");
        GL11.glTranslated(x, y, z);
        double motionscale = Math.sqrt(entity.motionZ * entity.motionZ + entity.motionX * entity.motionX);
        GL11.glRotatef(90, 1, 0, 0);
        GL11.glRotatef(-entity.rotationPitch, (float) (entity.motionZ /
                motionscale), 0.0f, (float) (-entity.motionX / motionscale));
        int time = (int) System.currentTimeMillis() % 360;
        GL11.glRotatef(time / 2, 0, 0, 1);
        double scale = 0.5;
        GL11.glScaled(scale, scale, scale);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex3d(-1, -1, 0);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex3d(-1, 1, 0);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex3d(1, 1, 0);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex3d(1, -1, 0);

        GL11.glEnd();
        TextureUtils.popTexture();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}