package com.github.lehjr.modularpowerarmor.client.gui.tinker.cosmetic;

import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableItem;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.math.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 12:25 PM, 5/2/13
 * <p>
 * Ported to Java by lehjr on 11/2/16.
 */
public class PlayerModelViewFrame extends ScrollableFrame {
    Minecraft minecraft;
    ItemSelectionFrame itemSelector;
    double anchorx = 0;
    double anchory = 0;
    int dragging = -1;
    float lastdWheel = 0;
    double rotx = 0;
    double roty = 0;
    double offsetx = 0;
    double offsety = 29.0F;
    float zoom = 30;
    int mouseX = 0;
    int mouseY = 0;
    private double oldMouseX = 20D;
    private double oldMouseY = 20D;

    public PlayerModelViewFrame(ItemSelectionFrame itemSelector, Point2F topleft, Point2F bottomright, float zLevel, Colour backgroundColour, Colour borderColour) {
        super(topleft, bottomright, zLevel, backgroundColour, borderColour);
        this.itemSelector = itemSelector;
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    public void init(float left, float top, float right, float bottom) {
        border.setTargetDimensions(left, top, right, bottom);
    }

    ClickableItem getSelectedItem() {
        return itemSelector.getSelectedItem();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (border.containsPoint(x, y)) {
            dragging = button;
            anchorx = (float) x;
            anchory = (float) y;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (border.containsPoint(x, y)) {
            dragging = -1;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mousex, double mousey, double dWheel) {
        if (border.containsPoint(mousex, mousey)) { // broken
            zoom += dWheel * 2;
            return true;
        }
        return false;
    }

    @Override
    public void update(double mousex, double mousey) {
        if (this.mouseX != (float)mousex) {
            this.oldMouseX = this.mouseX;
        }
        this.mouseX = (int) mousex;

        if (this.mouseY != mousey) {
            this.oldMouseY = this.mouseY;
        }
        this.mouseY = (int) mousey;

        double dx = mousex - anchorx;
        double dy = mousey - anchory;
        switch (dragging) {
            case 0: {
                rotx = MathUtils.clampDouble(rotx + dy, -90, 90);
                roty = roty - dx;
                anchorx = mousex;
                anchory = mousey;
                break;
            }
            case 1: {
                offsetx = offsetx + dx;
                offsety = offsety + dy;
                anchorx = mousex;
                anchory = mousey;
                break;
            }
            default:
                break;
        }
    }


    public static void drawEntityOnScreen(float posX, float posY, int scale, float mouseX, float mouseY, LivingEntity p_228187_5_) {
        float f = (float)Math.atan(mouseX / 40.0F);
        float f1 = (float)Math.atan(mouseY / 40.0F);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(posX, posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale((float)scale, (float)scale, (float)scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.multiply(quaternion1);
        matrixstack.rotate(quaternion);
        float f2 = p_228187_5_.renderYawOffset;
        float f3 = p_228187_5_.rotationYaw;
        float f4 = p_228187_5_.rotationPitch;
        float f5 = p_228187_5_.prevRotationYawHead;
        float f6 = p_228187_5_.rotationYawHead;
        p_228187_5_.renderYawOffset = 180.0F + f * 20.0F;
        p_228187_5_.rotationYaw = 180.0F + f * 40.0F;
        p_228187_5_.rotationPitch = -f1 * 20.0F;
        p_228187_5_.rotationYawHead = p_228187_5_.rotationYaw;
        p_228187_5_.prevRotationYawHead = p_228187_5_.rotationYaw;
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        entityrenderermanager.renderEntityStatic(p_228187_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
        irendertypebuffer$impl.finish();
        entityrenderermanager.setRenderShadow(true);
        p_228187_5_.renderYawOffset = f2;
        p_228187_5_.rotationYaw = f3;
        p_228187_5_.rotationPitch = f4;
        p_228187_5_.prevRotationYawHead = f5;
        p_228187_5_.rotationYawHead = f6;
        RenderSystem.popMatrix();
    }

    @Override
    public void render(int mouseX_, int mouseY_, float partialTicks)  {
        border.draw(getzLevel());

        // int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity p_228187_5_)

        float mouse_x = (float) ((border.left() + 51) - this.oldMouseX);
        float mouse_y = (float) ((float) ((int) border.top() + 75 - 50) - this.oldMouseY);
        int i = (int) border.centerx();
        int j = (int) border.centery();


        drawEntityOnScreen(i, j, 30, mouse_x, mouse_y, this.minecraft.player);


        LivingEntity livingEntity = Minecraft.getInstance().player;



//        RenderSystem.pushMatrix();
////        RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
//        RenderSystem.translatef(border.centerx() + (float)offsetx, border.centery() + (float)offsety, 1050.0F);
//        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
//        MatrixStack matrixstack = new MatrixStack();
//        matrixstack.translate(0.0D, 0.0D, 1000.0D);
//
//        matrixstack.scale(-zoom, zoom, zoom);
//
//        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
//        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(mouse_y * 20.0F);
//        quaternion.multiply(quaternion1);
//        matrixstack.rotate(quaternion);
//
//
//        float f2 = livingEntity.renderYawOffset;
//        float f3 = livingEntity.rotationYaw;
//        float f4 = livingEntity.rotationPitch;
//        float f5 = livingEntity.prevRotationYawHead;
//        float f6 = livingEntity.rotationYawHead;
//
//        // XRotation with mouse look
//        RenderSystem.rotatef(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
//        RenderSystem.rotatef((float) rotx, 1, 0, 0);
//        RenderSystem.rotatef((float) roty, 0, 1, 0);
//
//        livingEntity.renderYawOffset = 180.0F + mouse_x * 20.0F;
//        livingEntity.rotationYaw = 180.0F + mouse_x * 40.0F;
//        livingEntity.rotationPitch = -mouse_y * 20.0F;
//        livingEntity.rotationYawHead = livingEntity.rotationYaw;
//        livingEntity.prevRotationYawHead = livingEntity.rotationYaw;
//        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
//        quaternion1.conjugate();
//        entityrenderermanager.setCameraOrientation(quaternion1);
//        entityrenderermanager.setRenderShadow(false);
//        IRenderTypeBuffer.Impl bufferSource = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
//        entityrenderermanager.renderEntityStatic(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, bufferSource, 15728880);
//        bufferSource.finish();
//        entityrenderermanager.setRenderShadow(true);
//        livingEntity.renderYawOffset = f2;
//        livingEntity.rotationYaw = f3;
//        livingEntity.rotationPitch = f4;
//        livingEntity.prevRotationYawHead = f5;
//        livingEntity.rotationYawHead = f6;
//        RenderSystem.popMatrix();
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        return null;
    }
}