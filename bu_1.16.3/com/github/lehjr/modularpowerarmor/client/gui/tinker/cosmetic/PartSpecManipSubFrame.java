package com.github.lehjr.modularpowerarmor.client.gui.tinker.cosmetic;

import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.network.packets.CosmeticInfoPacket;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.capabilities.render.IArmorModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.IHandHeldModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.capabilities.render.modelspec.*;
import com.github.lehjr.mpalib.client.gui.GuiIcon;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableItem;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.render.IconUtils;
import com.github.lehjr.mpalib.client.render.RenderState;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.math.MathUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 1:46 AM, 30/04/13
 * <p>
 * Ported to Java by lehjr on 11/2/16.
 */
public class PartSpecManipSubFrame {
    public SpecBase model;
    public ColourPickerFrame colourframe;
    public ItemSelectionFrame itemSelector;
    public RelativeRect border;
    public List<PartSpecBase> partSpecs;
    public boolean open;
    Minecraft minecraft;
    float zLevel;

    public PartSpecManipSubFrame(SpecBase model,
                                 ColourPickerFrame colourframe,
                                 ItemSelectionFrame itemSelector,
                                 RelativeRect border,
                                 float zLevel) {
        this.model = model;
        this.colourframe = colourframe;
        this.itemSelector = itemSelector;
        this.border = border;
        this.partSpecs = this.getPartSpecs();
        this.open = true;
        this.zLevel = zLevel;
        minecraft = Minecraft.getInstance();
    }

    /**
     * get all valid parts of model for the equipment itemSlot
     * Don't bother converting to Java stream with filter, the results are several times slower
     */
    private List<PartSpecBase> getPartSpecs() {
        List<PartSpecBase> specsArray = new ArrayList<>();
        Iterator<PartSpecBase> specIt = model.getPartSpecs().iterator();

        if (getSelectedItem() != null) {
            getSelectedItem().getStack().getCapability(ModelSpecNBTCapability.RENDER).ifPresent(specNBT ->{
                PartSpecBase spec;

                while (specIt.hasNext()) {
                    spec = specIt.next();

                    // this COULD fail here if the wrong capability is applied, otherwise should be fine.
                    if (specNBT instanceof IArmorModelSpecNBT) {
                        EquipmentSlotType slot = MobEntity.getSlotForItemStack(getSelectedItem().getStack());
                        if (spec.getBinding().getSlot() == slot) {
                            specsArray.add(spec);
                        }
                    } else if (specNBT instanceof IHandHeldModelSpecNBT) {
                        if (spec.getBinding().getSlot().getSlotType().equals(EquipmentSlotType.Group.HAND)) {
                            specsArray.add(spec);
                        }
                    }
                }
            });
        }
        return specsArray;
    }

    public ClickableItem getSelectedItem() {
        return this.itemSelector.getSelectedItem();
    }

    @Nullable
    public CompoundNBT getOrDontGetSpecTag(PartSpecBase partSpec) {
        if (this.getSelectedItem() == null) {
            return null;
        }

        AtomicReference<CompoundNBT> specTag = new AtomicReference<>(null);

        getSelectedItem().getStack().getCapability(ModelSpecNBTCapability.RENDER).ifPresent(specNBT->{
            CompoundNBT renderTag = specNBT.getRenderTag();

            if (renderTag != null && !renderTag.isEmpty()) {
                // there can be many ModelPartSpecs
                if (partSpec instanceof ModelPartSpec) {
                    String name = ModelRegistry.getInstance().makeName(partSpec);
                    specTag.set(renderTag.contains(name) ? renderTag.getCompound(name) : null);
                }
                // Only one TexturePartSpec is allowed at a time, so figure out if this one is enabled
                if (partSpec instanceof TexturePartSpec && renderTag.contains(MPALIbConstants.NBT_TEXTURESPEC_TAG)) {
                    CompoundNBT texSpecTag = renderTag.getCompound(MPALIbConstants.NBT_TEXTURESPEC_TAG);
                    if (partSpec.spec.getOwnName().equals(texSpecTag.getString(MPALIbConstants.TAG_MODEL))) {
                        specTag.set(renderTag.getCompound(MPALIbConstants.NBT_TEXTURESPEC_TAG));
                    }
                }
            }
        });
        return specTag.get();
    }

    public CompoundNBT getSpecTag(PartSpecBase partSpec) {
        CompoundNBT nbt = getOrDontGetSpecTag(partSpec);
        return nbt != null ? nbt : new CompoundNBT();
    }

    public CompoundNBT getOrMakeSpecTag(PartSpecBase partSpec) {
        String name;
        CompoundNBT nbt = getSpecTag(partSpec);
        if (nbt.isEmpty()) {
            if (partSpec instanceof ModelPartSpec) {
                name = ModelRegistry.getInstance().makeName(partSpec);
                ((ModelPartSpec) partSpec).multiSet(nbt, null, null);
            } else {
                name = MPALIbConstants.NBT_TEXTURESPEC_TAG;
                partSpec.multiSet(nbt, null);
            }

            // update the render tag client side. The server side update is called below.
            if (getSelectedItem() != null) {
                this.getSelectedItem().getStack().getCapability(ModelSpecNBTCapability.RENDER).ifPresent(specNBT->{
                    CompoundNBT renderTag  = specNBT.getRenderTag();
                    if (renderTag != null && !renderTag.isEmpty()) {
                        renderTag.put(name, nbt);
                        specNBT.setRenderTag(renderTag, MPALIbConstants.TAG_RENDER);
                    }
                });
            }
        }
        return nbt;
    }

    public void updateItems() {
        this.partSpecs = getPartSpecs();
        this.border.setHeight((partSpecs.size() > 0) ? (partSpecs.size() * 8 + 10) : 0);
    }

    public void drawPartial(double min, double max) {
        if (partSpecs.size() > 0) {
            Renderer.drawString(model.getDisaplayName(), border.left() + 8, border.top());
            drawOpenArrow(min, max);
            if (open) {
                int y = (int) (border.top() + 8);
                for (PartSpecBase spec : partSpecs) {
                    drawSpecPartial(border.left(), y, spec);
                    y += 8;
                }
            }
        }
    }

    public void decrAbove(int index) {
        for (PartSpecBase spec : partSpecs) {
            String tagname = ModelRegistry.getInstance().makeName(spec);
            ClientPlayerEntity player = minecraft.player;
            CompoundNBT tagdata = getOrDontGetSpecTag(spec);

            if (tagdata != null) {
                int oldindex = spec.getColourIndex(tagdata);
                if (oldindex >= index && oldindex > 0) {
                    spec.setColourIndex(tagdata, oldindex - 1);
                    if (player.world.isRemote) {
                        MPAPackets.CHANNEL_INSTANCE.sendToServer(new CosmeticInfoPacket(getSelectedItem().inventorySlot, tagname, tagdata));
                    }
                }
            }
        }
    }

    public void drawSpecPartial(float x, float y, PartSpecBase partSpec) {
        GuiIcon icon = IconUtils.getIcon();
        CompoundNBT tag = this.getSpecTag(partSpec);
        int selcomp = tag.isEmpty() ? 0 : (partSpec instanceof ModelPartSpec && ((ModelPartSpec) partSpec).getGlow(tag) ? 2 : 1);
        int selcolour = partSpec.getColourIndex(tag);

        icon.transparentArmor.draw(x, y, Colour.WHITE);

        icon.normalArmor.draw(x+8, y, Colour.WHITE);

        if (partSpec instanceof ModelPartSpec) {
            icon.glowArmor.draw(x + 16, y, Colour.WHITE);
        }

        icon.selectedArmorOverlay.draw(x + selcomp * 8, y, Colour.WHITE);

        float acc = (x + 28);
        for (int colour : colourframe.colours()) {
            icon.armorColourPatch.draw(acc, y, new Colour(colour));
            acc += 8;
        }

        double textstartx = acc;
        if (selcomp > 0) {
            icon.selectedArmorOverlay.draw(x + 28 + selcolour * 8, y, Colour.WHITE);
        }
        Renderer.drawString(partSpec.getDisaplayName().getFormattedText(), textstartx + 4, y);
    }

    public void drawOpenArrow(double min, double max) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderState.glowOn();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        if (this.open) {
            buffer.pos(this.border.left() + 3, MathUtils.clampDouble(this.border.top() + 3, min, max), zLevel).color(Colour.LIGHTBLUE.r, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.a).endVertex();
            buffer.pos(this.border.left() + 5, MathUtils.clampDouble(this.border.top() + 7, min, max), zLevel).color(Colour.LIGHTBLUE.r, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.a).endVertex();
            buffer.pos(this.border.left() + 7, MathUtils.clampDouble(this.border.top() + 3, min, max), zLevel).color(Colour.LIGHTBLUE.r, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.a).endVertex();
        } else {
            buffer.pos(this.border.left() + 3, MathUtils.clampDouble(this.border.top() + 3, min, max), zLevel).color(Colour.LIGHTBLUE.r, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.a).endVertex();
            buffer.pos(this.border.left() + 3, MathUtils.clampDouble(this.border.top() + 7, min, max), zLevel).color(Colour.LIGHTBLUE.r, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.a).endVertex();
            buffer.pos(this.border.left() + 7, MathUtils.clampDouble(this.border.top() + 5, min, max), zLevel).color(Colour.LIGHTBLUE.r, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.b, Colour.LIGHTBLUE.a).endVertex();
        }
        tessellator.draw();

        RenderState.glowOff();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    public Rect getBorder() {
        if (this.open) {
            border.setHeight(9 + 9 * partSpecs.size());
        } else {
            this.border.setHeight(9.0F);
        }
        return this.border;
    }

    public boolean tryMouseClick(double x, double y) {
        ClientPlayerEntity player = minecraft.player;
        CompoundNBT tagdata;
        String tagname;

        // player clicked outside the area
        if (x < this.border.left() || x > this.border.right() || y < this.border.top() || y > this.border.bottom())
            return false;

            // opens the list of partSpecs
        else if (x > this.border.left() + 2 && x < this.border.left() + 8 && y > this.border.top() + 2 && y < this.border.top() + 8) {
            this.open = !this.open;
            this.getBorder();
            return true;

            // player clicked one of the icons for off/on/glowOn
        } else if (x < this.border.left() + 24 && y > this.border.top() + 8) {
            int lineNumber = (int) ((y - this.border.top() - 8) / 8);
            int columnNumber = (int) ((x - this.border.left()) / 8);
            PartSpecBase spec = partSpecs.get(Math.max(Math.min(lineNumber, partSpecs.size() - 1), 0));
            MPALibLogger.logger.debug("Line " + lineNumber + " Column " + columnNumber);

            switch (columnNumber) {
                // removes the associated tag from the render tag making the part not isEnabled
                case 0: {
                    tagname = spec instanceof TexturePartSpec ? MPALIbConstants.NBT_TEXTURESPEC_TAG : ModelRegistry.getInstance().makeName(spec);
                    MPAPackets.CHANNEL_INSTANCE.sendToServer(new CosmeticInfoPacket(this.getSelectedItem().inventorySlot, tagname, new CompoundNBT()));

                    this.updateItems();
                    return true;
                }

                // set part to isEnabled
                case 1: {
                    tagname = spec instanceof TexturePartSpec ? MPALIbConstants.NBT_TEXTURESPEC_TAG : ModelRegistry.getInstance().makeName(spec);
                    tagdata = this.getOrMakeSpecTag(spec);
                    if (spec instanceof ModelPartSpec) {
                        ((ModelPartSpec) spec).setGlow(tagdata, false);
                    }
                    MPAPackets.CHANNEL_INSTANCE.sendToServer(new CosmeticInfoPacket(this.getSelectedItem().inventorySlot, tagname, tagdata));

                    this.updateItems();
                    return true;
                }

                // glow on
                case 2: {
                    if (spec instanceof ModelPartSpec) {
                        tagname = ModelRegistry.getInstance().makeName(spec);
                        tagdata = this.getOrMakeSpecTag(spec);
                        ((ModelPartSpec) spec).setGlow(tagdata, true);
                        MPAPackets.CHANNEL_INSTANCE.sendToServer(new CosmeticInfoPacket(this.getSelectedItem().inventorySlot, tagname, tagdata));
                        this.updateItems();
                        return true;
                    }
                    return false;
                }
                default:
                    return false;
            }
        }
        // player clicked a color icon
        else if (x > this.border.left() + 28 && x < this.border.left() + 28 + this.colourframe.colours().length * 8) {
            int lineNumber = (int) ((y - this.border.top() - 8) / 8);
            int columnNumber = (int) ((x - this.border.left() - 28) / 8);
            PartSpecBase spec = partSpecs.get(Math.max(Math.min(lineNumber, partSpecs.size() - 1), 0));
            tagname = spec instanceof TexturePartSpec ? MPALIbConstants.NBT_TEXTURESPEC_TAG : ModelRegistry.getInstance().makeName(spec);
            tagdata = this.getOrMakeSpecTag(spec);
            spec.setColourIndex(tagdata, columnNumber);
            MPAPackets.CHANNEL_INSTANCE.sendToServer(new CosmeticInfoPacket(this.getSelectedItem().inventorySlot, tagname, tagdata));
            return true;
        }
        return false;
    }
}