package com.github.lehjr.modularpowerarmor.client.gui.tinker.cosmetic;

import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.modularpowerarmor.client.gui.obsolete.ScrollableLabel;
import com.github.lehjr.modularpowerarmor.client.gui.obsolete.ScrollableRectangle;
import com.github.lehjr.modularpowerarmor.client.gui.obsolete.ScrollableSlider;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.network.packets.ColourInfoPacket;
import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.client.gui.GuiIcon;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableSlider;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.render.IconUtils;
import com.github.lehjr.mpalib.math.Colour;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 4:19 AM, 03/05/13
 * <p>
 * Ported to Java by lehjr on 11/2/16.
 */
public class ColourPickerFrame extends ScrollableFrame {
    public ItemSelectionFrame itemSelector;
    public ScrollableSlider rslider;
    public ScrollableSlider gslider;
    public ScrollableSlider bslider;
    public ScrollableSlider aslider;
    ScrollableColourBox colourBox;
    String COLOUR_PREFIX = I18n.format("gui.modularpowerarmor.colourPrefix");

    public ScrollableLabel colourLabel;

    public ScrollableSlider selectedSlider;
    public int selectedColour;
    public int decrAbove;
    ScrollableRectangle[] rectangles;

    public ColourPickerFrame(Point2F topleft, Point2F bottomright, float zLevel, Colour borderColour, Colour insideColour, ItemSelectionFrame itemSelector) {
        super(topleft, bottomright, zLevel, borderColour, insideColour);
        this.itemSelector = itemSelector;
    }

    @Override
    public void init(float left, float top, float right, float bottom) {
        super.init(left, top, right, bottom);

        if (itemSelector.hasNoItems()) {
            this.disable();
        } else {
            this.enable();
        }

        this.rectangles = new ScrollableRectangle[6];
        this.totalsize = 120;

        // sliders boxes 0-3
        this.rslider = getScrollableSlider("red", null, 0);
        this.gslider = getScrollableSlider("green", rslider, 1);
        this.bslider = getScrollableSlider("blue", gslider, 2);
        this.aslider = getScrollableSlider("alpha", bslider, 3);

        // box 4 is for the color icon stuff
        this.colourBox = new ScrollableColourBox(new RelativeRect(border.left(), this.aslider.bottom(), this.border.right(), this.aslider.bottom() + 25));
        this.colourBox.setMeBelow(aslider);
        rectangles[4] = colourBox;

        // box 5 is the label
        RelativeRect colourLabelBox = new RelativeRect(border.left(), this.colourBox.bottom(), this.border.right(), this.colourBox.bottom() + 20);
        this.colourLabel = new ScrollableLabel(
                new ClickableLabel(COLOUR_PREFIX, new Point2F(colourLabelBox.centerx(), colourLabelBox.centery())), colourLabelBox);

        colourLabel.setMeBelow(colourBox);
        rectangles[5] = this.colourLabel;

        this.selectedSlider = null;
        this.selectedColour = 0;
        this.decrAbove = -1;
    }

    public ScrollableSlider getScrollableSlider(String id, ScrollableRectangle prev, int index) {
        RelativeRect newborder = new RelativeRect(border.left(), prev != null ? prev.bottom() : this.border.top(),
                this.border.right(), (prev != null ? prev.bottom() : this.border.top()) + 18);
        ClickableSlider slider =
                new ClickableSlider(new Point2F(newborder.centerx(), newborder.centery()), newborder.width() - 15, id,
                        I18n.format(MPALIbConstants.MODULE_TRADEOFF_PREFIX + id));
        ScrollableSlider scrollableSlider = new ScrollableSlider(slider, newborder);
        scrollableSlider.setMeBelow((prev != null) ? prev : null);
        rectangles[index] = scrollableSlider;
        return scrollableSlider;
    }

    public int[] colours() {
        return (getOrCreateColourTag() != null) ? getOrCreateColourTag().getIntArray() : new int[0];
    }

    public IntArrayNBT getOrCreateColourTag() {
        if (this.itemSelector.getSelectedItem() == null) {
            return null;
        }
        return itemSelector.getSelectedItem().getStack().getCapability(ModelSpecNBTCapability.RENDER).map(spec -> {
            CompoundNBT renderSpec = spec.getRenderTag();
            if (renderSpec != null && !renderSpec.isEmpty()) {
                return new IntArrayNBT(spec.getColorArray());
            }
            return new IntArrayNBT(new int[0]);
        }).orElse(new IntArrayNBT(new int[0]));
    }

    public IntArrayNBT setColourTagMaybe(List<Integer> intList) {
        if (this.itemSelector.getSelectedItem() == null) {
            return null;
        }
        return itemSelector.getSelectedItem().getStack().getCapability(ModelSpecNBTCapability.RENDER).map(spec -> {
            CompoundNBT renderSpec = spec.getRenderTag();
            renderSpec.put(MPALIbConstants.TAG_COLOURS, new IntArrayNBT(intList));
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player.world.isRemote) {
                MPAPackets.CHANNEL_INSTANCE.sendToServer(new ColourInfoPacket(this.itemSelector.getSelectedItem().getSlotIndex(), this.colours()));
            }
            return (IntArrayNBT) renderSpec.get(MPALIbConstants.TAG_COLOURS);
        }).orElse(new IntArrayNBT(new int[0]));
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (this.isEnabled())
            this.selectedSlider = null;
        return false;
    }

    public DrawableRect getBorder() {
        return this.border;
    }

    @Override
    public void update(double mousex, double mousey) {
        super.update(mousex, mousey);
        if (this.isEnabled()) {
            if (this.selectedSlider != null) {
                this.selectedSlider.getSlider().setValueByX((float) mousex);
                if (colours().length > selectedColour) {
                    colours()[selectedColour] = Colour.getInt(rslider.getValue(), gslider.getValue(), bslider.getValue(), aslider.getValue());

                    ClientPlayerEntity player = Minecraft.getInstance().player;
                    if (player.world.isRemote)
                        MPAPackets.CHANNEL_INSTANCE.sendToServer(new ColourInfoPacket(itemSelector.getSelectedItem().inventorySlot, colours()));
                }
                // this just sets up the sliders on selecting an item
            } else if (itemSelector.getSelectedItem() != null && colours().length > 0) {
                if (selectedColour <= colours().length - 1)
                    onSelectColour(selectedColour);
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.isVisible()) {
            this.currentscrollpixels = Math.min(currentscrollpixels, getMaxScrollPixels());

            if (colours().length > selectedColour) {
                colourLabel.setText(COLOUR_PREFIX + " 0X" + new Colour(colours()[selectedColour]).hexColour());
            }

            super.preRender(mouseX, mouseY, partialTicks);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0, -currentscrollpixels, 0);
            for (ScrollableRectangle f : rectangles) {
                f.render(mouseX, mouseY, partialTicks, zLevel);
            }
            RenderSystem.popMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        return null;
    }

    public void onSelectColour(int i) {
        Colour c = new Colour(this.colours()[i]);
        this.rslider.setValue(c.r);
        this.gslider.setValue(c.g);
        this.bslider.setValue(c.b);
        this.aslider.setValue(c.a);
        this.selectedColour = i;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (this.isEnabled()) {
            y = y + currentscrollpixels;

            if (this.rslider.hitBox((float) x, (float) y)) {
                this.selectedSlider = this.rslider;
            } else if (this.gslider.hitBox((float) x, (float) y)) {
                this.selectedSlider = this.gslider;
            } else if (this.bslider.hitBox((float) x, (float) y)) {
                this.selectedSlider = this.bslider;
            } else if(this.aslider.hitBox((float)x,(float) y)) {
                this.selectedSlider =this.aslider;
            } else{
                this.selectedSlider=null;
            }

            colourBox.addColour(x, y);
            colourBox.removeColour(x, y);

            if (colourLabel.hitbox((float) x, (float) y) && colours().length > selectedColour) {
                // todo: insert chat to player...
//            System.out.println("copying to clipboard: " + "0x" + new Colour(selectedColour).hexColour());
                StringSelection selection = new StringSelection(new Colour(selectedColour).hexColour());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        }
        return false;
    }

    public int[] getIntArray(IntArrayNBT e) {
        if (e == null) // null when no armor item selected
            return new int[0];
        return e.getIntArray();
    }

    class ScrollableColourBox extends ScrollableRectangle {
        public ScrollableColourBox(RelativeRect relativeRect) {
            super(relativeRect);
        }

        boolean addColour(double x, double y) {
            if (y > this.centery() + 8.5 && y < this.centery() + 16.5 ) {
                int colourCol = (int) (x - left() - 8.0) / 8;
                if (colourCol >= 0 && colourCol < colours().length) {
                    onSelectColour(colourCol);
                } else if (colourCol == colours().length) {
                    MPALibLogger.logger.debug("Adding");
                    List<Integer> intList = Arrays.stream(getIntArray(getOrCreateColourTag())).boxed().collect(Collectors.toList());
                    intList.add(Colour.WHITE.getInt());
                    setColourTagMaybe(intList);
                }
                return true;
            }
            return false;
        }

        boolean removeColour(double x, double y) {
            if (y > this.centery() + 0.5 && y < this.centery() + 8.5 && x > left() + 8 + selectedColour * 8 && x < left() + 16 + selectedColour * 8) {
                IntArrayNBT IntArrayNBT = getOrCreateColourTag();
                List<Integer> intList = Arrays.stream(getIntArray(IntArrayNBT)).boxed().collect(Collectors.toList());

                if (intList.size() > 1 && selectedColour <= intList.size() -1) {
                    intList.remove(selectedColour); // with integer list, will default to index rather than getValue

                    setColourTagMaybe(intList);

                    decrAbove = selectedColour;
                    if (selectedColour == getIntArray(IntArrayNBT).length) {
                        selectedColour = selectedColour - 1;
                    }

                    ClientPlayerEntity player = Minecraft.getInstance().player;
                    if (player.world.isRemote)
                        MPAPackets.CHANNEL_INSTANCE.sendToServer(new ColourInfoPacket(itemSelector.getSelectedItem().getSlotIndex(), IntArrayNBT.getIntArray()));
                }
                return true;
            }
            return false;
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTicks, float zLevel) {
            GuiIcon icon = IconUtils.getIcon();

            // colours
            for (int i=0; i < colours().length; i++) {
                icon.armorColourPatch.draw(this.left() + 8 + i * 8, this.centery() + 8 , new Colour(colours()[i]));
            }

            icon.armorColourPatch.draw(this.left() + 8 + colours().length * 8, this.centery() + 8, Colour.WHITE);
            icon.selectedArmorOverlay.draw(this.left() + 8 + selectedColour * 8, this.centery() + 8, Colour.WHITE);
            icon.minusSign.draw(this.left() + 8 + selectedColour * 8, this.centery(), Colour.RED);
            icon.plusSign.draw(this.left() + 8 + colours().length * 8, this.centery() + 8, Colour.GREEN);
        }
    }
}
