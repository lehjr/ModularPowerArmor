package net.machinemuse.powersuits.client.render.entity;

import net.machinemuse.numina.client.render.MuseTextureUtils;
import net.machinemuse.numina.client.render.entity.MuseEntityRenderer;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.ModularPowersuits;
import net.machinemuse.powersuits.entity.SpinningBladeEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class EntityRendererSpinningBlade extends MuseEntityRenderer<SpinningBladeEntity> {
    public EntityRendererSpinningBlade(EntityRendererManager renderManager) {
        super(renderManager);
    }

    public static final ResourceLocation textureLocation = new ResourceLocation(MPSConstants.MODID, "modules/spinningblade.png");

    @Nullable
    @Override
    protected ResourceLocation func_110775_a(SpinningBladeEntity spinningBladeEntity) {
        return getEntityTexture(spinningBladeEntity);
    }

//    @Override
    protected ResourceLocation getEntityTexture(SpinningBladeEntity entity) {
        return textureLocation;
    }

    @Override
    public void /* doRender */ func_76986_a(SpinningBladeEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
        GL11.glDisable(GL11.GL_CULL_FACE);
        MuseTextureUtils.pushTexture(getEntityTexture(entity));
        GL11.glTranslated(x, y, z);
        double motionscale = Math.sqrt(entity.getMotion().z * entity.getMotion().z +entity.getMotion().x * entity.getMotion().x);
        GL11.glRotatef(90, 1, 0, 0);
        GL11.glRotatef(-entity.rotationPitch, (float) (entity.getMotion().z /
                motionscale), 0.0f, (float) (- entity.getMotion().x / motionscale));
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
        MuseTextureUtils.popTexture();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }
}