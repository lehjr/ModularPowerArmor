package com.github.lehjr.modularpowerarmor.client.gui.tinker.cosmetic;

import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.input.Mouse;

import java.util.List;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 12:25 PM, 5/2/13
 * <p>
 * Ported to Java by lehjr on 11/2/16.
 */
public class PlayerModelViewFrame implements IGuiFrame {
    Minecraft minecraft;

    ItemSelectionFrame itemSelector;
    DrawableRect border;
    double anchorx = 0;
    double anchory = 0;
    int dragging = -1;
    double lastdWheel = 0;
    double rotx = 0;
    double roty = 0;
    double offsetx = 0;
    double offsety = 29.0D;
    double zoom = 30;
    int mouseX = 0;
    int mouseY = 0;
    private float oldMouseX = 20F;
    private float oldMouseY = 20F;

    public PlayerModelViewFrame(ItemSelectionFrame itemSelector, Point2D topleft, Point2D bottomright, Colour borderColour, Colour insideColour) {
        this.itemSelector = itemSelector;
        this.border = new DrawableRect(topleft, bottomright, borderColour, insideColour);
        this.minecraft = Minecraft.getMinecraft();
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        border.setTargetDimensions(left, top, right, bottom);
    }

    @Override
    public boolean onMouseDown(double x, double y, int button) {
        if (border.containsPoint(x, y)) {
            dragging = button;
            anchorx = x;
            anchory = y;
            return true;
        }
        return false;
    }

    @Override
    public boolean onMouseUp(double x, double y, int button) {
        if (border.containsPoint(x, y)) {
            dragging = -1;
            return true;
        }
        return false;
    }

    @Override
    public void update(double mousex, double mousey) {
        if (this.mouseX != mousex)
            this.oldMouseX = this.mouseX;
        this.mouseX = (int) mousex;

        if (this.mouseY != mousey)
            this.oldMouseY = this.mouseY;
        this.mouseY = (int) mousey;


        if (border.containsPoint(mousex, mousey)) {
            double dscroll = (lastdWheel - Mouse.getDWheel()) / 120;
            zoom = zoom * Math.pow(1.1, dscroll);
            lastdWheel = Mouse.getDWheel();
        }
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

    @Override
    public void render(int mouseX_, int mouseY_, float partialTicks)  {
        border.draw();
        // set color to normal state
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        float mouseX = (float) (border.left() + 51) - this.oldMouseX;
        float mouseY = (float) ((int) border.top() + 75 - 50) - this.oldMouseY;
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(border.centerx() + offsetx, border.centery() + offsety, 50.0F);
        GlStateManager.scale((float) (-zoom), (float) zoom, (float) zoom);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F); // turn model right side up

        float f = minecraft.player.renderYawOffset;
        float f1 = minecraft.player.rotationYaw;
        float f2 = minecraft.player.rotationPitch;
        float f3 = minecraft.player.prevRotationYawHead;
        float f4 = minecraft.player.rotationYawHead;
        // XRotation with mouse look
        GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);

        GlStateManager.rotate((float) rotx, 1, 0, 0);
        GlStateManager.rotate((float) roty, 0, 1, 0);

        minecraft.player.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
        minecraft.player.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
        minecraft.player.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        minecraft.player.rotationYawHead = minecraft.player.rotationYaw;
        minecraft.player.prevRotationYawHead = minecraft.player.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(minecraft.player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        minecraft.player.renderYawOffset = f;
        minecraft.player.rotationYaw = f1;
        minecraft.player.rotationPitch = f2;
        minecraft.player.prevRotationYawHead = f3;
        minecraft.player.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        return null;
    }
}