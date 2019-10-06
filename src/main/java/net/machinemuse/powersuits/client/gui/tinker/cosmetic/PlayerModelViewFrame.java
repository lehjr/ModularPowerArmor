package net.machinemuse.powersuits.client.gui.tinker.cosmetic;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.machinemuse.numina.capabilities.render.IArmorModelSpecNBT;
import net.machinemuse.numina.capabilities.render.ModelSpecNBTCapability;
import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.machinemuse.numina.client.gui.frame.IGuiFrame;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseRect;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.math.MuseMathUtils;
import net.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import net.machinemuse.powersuits.client.model.item.ArmorModelInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

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
    DrawableMuseRect border;
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

    public PlayerModelViewFrame(ItemSelectionFrame itemSelector, MusePoint2D topleft, MusePoint2D bottomright, Colour borderColour, Colour insideColour) {
        this.itemSelector = itemSelector;
        this.border = new DrawableMuseRect(topleft, bottomright, borderColour, insideColour);
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        border.setTargetDimensions(left, top, right, bottom);
    }

    ClickableItem getSelectedItem() {
        return itemSelector.getSelectedItem();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (border.containsPoint(x, y)) {
            dragging = button;
            anchorx = x;
            anchory = y;
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
        if (this.mouseX != mousex)
            this.oldMouseX = this.mouseX;
        this.mouseX = (int) mousex;

        if (this.mouseY != mousey)
            this.oldMouseY = this.mouseY;
        this.mouseY = (int) mousey;

        double dx = mousex - anchorx;
        double dy = mousey - anchory;
        switch (dragging) {
            case 0: {
                rotx = MuseMathUtils.clampDouble(rotx + dy, -90, 90);
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
        if (itemSelector.getSelectedItem() == null) {
            return;
        }

        getSelectedItem().getStack().getCapability(ModelSpecNBTCapability.RENDER).ifPresent(specie->{
            if (specie instanceof IArmorModelSpecNBT) {
                CompoundNBT renderTag = specie.getMuseRenderTag();
                ArmorModelInstance.getInstance().setRenderSpec(renderTag);

                EquipmentSlotType slot = specie.getItemStack().getEquipmentSlot();
                if (slot == null) {
                    slot = MobEntity.getSlotForItemStack(specie.getItemStack());
                }
                ArmorModelInstance.getInstance().setVisibleSection(slot);

            }
        });


        // FIXME --- capabilities






        // set color to normal state
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        float mouseX = (float) (border.left() + 51) - this.oldMouseX;
        float mouseY = (float) ((int) border.top() + 75 - 50) - this.oldMouseY;
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translated(border.centerx() + offsetx, border.centery() + offsety, 50.0F);
        GlStateManager.scalef((float) (-zoom), (float) zoom, (float) zoom);
        GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F); // turn model right side up

        float f = minecraft.player.renderYawOffset;
        float f1 = minecraft.player.rotationYaw;
        float f2 = minecraft.player.rotationPitch;
        float f3 = minecraft.player.prevRotationYawHead;
        float f4 = minecraft.player.rotationYawHead;
        // XRotation with mouse look
        GlStateManager.rotatef(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);

        GlStateManager.rotatef((float) rotx, 1, 0, 0);
        GlStateManager.rotatef((float) roty, 0, 1, 0);

        minecraft.player.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
        minecraft.player.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
        minecraft.player.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        minecraft.player.rotationYawHead = minecraft.player.rotationYaw;
        minecraft.player.prevRotationYawHead = minecraft.player.rotationYaw;
        GlStateManager.translatef(0.0F, 0.0F, 0.0F);
        EntityRendererManager rendermanager = Minecraft.getInstance().getRenderManager();
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
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        return null;
    }
}