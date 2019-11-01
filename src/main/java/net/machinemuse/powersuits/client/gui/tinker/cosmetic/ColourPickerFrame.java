package net.machinemuse.powersuits.client.gui.tinker.cosmetic;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.client.gui.GuiIcons;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableLabel;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableSlider;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableLabel;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableRectangle;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableSlider;
import com.github.lehjr.mpalib.math.Colour;
import net.machinemuse.powersuits.api.constants.MPSModuleConstants;
import net.machinemuse.powersuits.client.gui.common.ItemSelectionFrame;
import net.machinemuse.powersuits.item.armor.ItemPowerArmor;
import net.machinemuse.powersuits.network.MPSPackets;
import net.machinemuse.powersuits.network.packets.ColourInfoPacket;
import net.machinemuse.powersuits.utils.nbt.MPSNBTUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 4:19 AM, 03/05/13
 * <p>
 * Ported to Java by lehjr on 11/2/16.
 */

// TODO: create base class and make this extend it for cosmetic preset friendly setup
public class ColourPickerFrame extends ScrollableFrame {
    public ItemSelectionFrame itemSelector;
    public ScrollableSlider rslider;
    public ScrollableSlider gslider;
    public ScrollableSlider bslider;
    public ScrollableSlider aslider;
    ScrollableColourBox colourBox;
    String COLOUR_PREFIX = I18n.format("gui.powersuits.colourPrefix");

    public ScrollableLabel colourLabel;

    public ScrollableSlider selectedSlider;
    public int selectedColour;
    public int decrAbove;
    ScrollableRectangle[] rectangles;

    public ColourPickerFrame(Point2D topleft, Point2D bottomright, Colour borderColour, Colour insideColour, ItemSelectionFrame itemSelector) {
        super(topleft, bottomright, borderColour, insideColour);
        this.itemSelector = itemSelector;
        this.rectangles = new ScrollableRectangle[6];
        this.totalsize = 120;

        // sliders boxes 0-3
        this.rslider = getScrollableSlider("red", null, 0);
        this.gslider = getScrollableSlider("green", rslider, 1);
        this.bslider = getScrollableSlider("blue", gslider, 2);
        this.aslider = getScrollableSlider("alpha", bslider, 3);

        // box 4 is for the color icon stuff
        this.colourBox = new ScrollableColourBox(new RelativeRect(border.left(), this.aslider.bottom(), this.border.right(), this.aslider.bottom()+ 25));
        this.colourBox.setMeBelow(aslider);
        rectangles[4] = colourBox;

        // box 5 is the label
        RelativeRect colourLabelBox = new RelativeRect(border.left(), this.colourBox.bottom(), this.border.right(), this.colourBox.bottom() + 20);
        this.colourLabel = new ScrollableLabel(
                new ClickableLabel(COLOUR_PREFIX, new Point2D(colourLabelBox.centerx(), colourLabelBox.centery())), colourLabelBox);

        colourLabel.setMeBelow(colourBox);
        rectangles[5] = this.colourLabel;

        this.selectedSlider = null;
        this.selectedColour = 0;
        this.decrAbove = -1;
        this.enable();
    }

    public ScrollableSlider getScrollableSlider(String id, ScrollableRectangle prev, int index) {
        RelativeRect newborder = new RelativeRect(border.left(), prev != null ? prev.bottom() : this.border.top(),
                this.border.right(), (prev != null ? prev.bottom() : this.border.top()) + 18);
        ClickableSlider slider =
                new ClickableSlider(new Point2D(newborder.centerx(), newborder.centery()), newborder.width() - 15, id,
                        I18n.format(MPSModuleConstants.MODULE_TRADEOFF_PREFIX + id));
        ScrollableSlider scrollableSlider = new ScrollableSlider(slider, newborder);
        scrollableSlider.setMeBelow((prev != null) ? prev : null);
        rectangles[index] = scrollableSlider;
        return scrollableSlider;
    }

    public int[] colours() {
        return (getOrCreateColourTag() != null) ? getOrCreateColourTag().getIntArray() : new int[0];
    }

    public NBTTagIntArray getOrCreateColourTag() {
        if (this.itemSelector.getSelectedItem() == null) {
            return null;
        }

        NBTTagCompound renderSpec = MPSNBTUtils.getMuseRenderTag(this.itemSelector.getSelectedItem().getItem());
        if (renderSpec.hasKey(MPALIbConstants.TAG_COLOURS) && renderSpec.getTag(MPALIbConstants.TAG_COLOURS) instanceof NBTTagIntArray) {
            return (NBTTagIntArray) renderSpec.getTag(MPALIbConstants.TAG_COLOURS);
        } else {
            Item item = this.itemSelector.getSelectedItem().getItem().getItem();
            if (item instanceof ItemPowerArmor) {
                ItemPowerArmor itemPowerArmor = (ItemPowerArmor) item;
                int[] intArray = {itemPowerArmor.getColorFromItemStack(this.itemSelector.getSelectedItem().getItem()).getInt()};
                renderSpec.setIntArray(MPALIbConstants.TAG_COLOURS, intArray);
            } else {
                int[] intArray2 = new int[]{Colour.WHITE.getInt()};
                renderSpec.setIntArray(MPALIbConstants.TAG_COLOURS, intArray2);
            }
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player.world.isRemote) {
                MPSPackets.sendToServer(new ColourInfoPacket(this.itemSelector.getSelectedItem().inventorySlot, this.colours()));
            }
            return (NBTTagIntArray) renderSpec.getTag(MPALIbConstants.TAG_COLOURS);
        }
    }

    public NBTTagIntArray setColourTagMaybe(List<Integer> intList) {
        if (this.itemSelector.getSelectedItem() == null) {
            return null;
        }
        NBTTagCompound renderSpec = MPSNBTUtils.getMuseRenderTag(this.itemSelector.getSelectedItem().getItem());
        renderSpec.setTag(MPALIbConstants.TAG_COLOURS, new NBTTagIntArray(intList));
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player.world.isRemote) {
            MPSPackets.sendToServer(new ColourInfoPacket(this.itemSelector.getSelectedItem().inventorySlot, this.colours()));
        }
        return (NBTTagIntArray) renderSpec.getTag(MPALIbConstants.TAG_COLOURS);
    }

    public ArrayList<Integer> importColours() {
        return new ArrayList<Integer>();
    }

    public void refreshColours() {
        //    getOrCreateColourTag.map(coloursTag => {
        //      val colourints: Array[Int] = coloursTag.intArray
        //      val colourset: HashSet[Int] = HashSet.empty ++ colours ++ colourints
        //      val colourarray = colourset.toArray
        //      coloursTag.intArray = colourarray
        //    })
    }

    @Override
    public void onMouseUp(double x, double y, int button) {
        if (this.isEnabled())
            this.selectedSlider = null;
    }

    public DrawableRect getBorder(){
        return this.border;
    }


    @Override
    public void update(double mousex, double mousey) {
        super.update(mousex, mousey);
        if (this.isEnabled()) {
            if (this.selectedSlider != null) {
                this.selectedSlider.getSlider().setValueByX(mousex);
                if (colours().length > selectedColour) {
                    colours()[selectedColour] = Colour.getInt(rslider.getValue(), gslider.getValue(), bslider.getValue(), aslider.getValue());

                    EntityPlayerSP player = Minecraft.getMinecraft().player;
                    if (player.world.isRemote)
                        MPSPackets.sendToServer(new ColourInfoPacket(itemSelector.getSelectedItem().inventorySlot, colours()));
                }
                // this just sets up the sliders on selecting an item
            } else if (itemSelector.getSelectedItem() != null && colours().length > 0) {
                if (selectedColour <= colours().length -1)
                    onSelectColour(selectedColour);
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.isVisibile()) {
            this.currentscrollpixels = Math.min(currentscrollpixels, getMaxScrollPixels());

            if (colours().length > selectedColour) {
                colourLabel.setText(COLOUR_PREFIX + " 0X" + new Colour(colours()[selectedColour]).hexColour());
            }

            super.preRender(mouseX, mouseY, partialTicks);
            GL11.glPushMatrix();
            GL11.glTranslatef(0, -currentscrollpixels, 0);
            for (ScrollableRectangle f : rectangles) {
                f.render(mouseX, mouseY, partialTicks);
            }
            GL11.glPopMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public List<String> getToolTip(int x, int y) {
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
    public void onMouseDown(double x, double y, int button) {
        if (this.isEnabled()) {
            y = y + currentscrollpixels;

            if (this.rslider.hitBox(x, y))
                this.selectedSlider = this.rslider;
            else if (this.gslider.hitBox(x, y))
                this.selectedSlider = this.gslider;
            else if (this.bslider.hitBox(x, y))
                this.selectedSlider = this.bslider;
            else if (this.aslider.hitBox(x, y))
                this.selectedSlider = this.aslider;
            else
                this.selectedSlider = null;

            colourBox.addColour(x, y);
            colourBox.removeColour(x, y);

            if (colourLabel.hitbox(x, y) && colours().length > selectedColour) {
                // todo: insert chat to player...
//            System.out.println("copying to clipboard: " + "0x" + new Colour(selectedColour).hexColour());
                StringSelection selection = new StringSelection(new Colour(selectedColour).hexColour());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        }
    }

    public int[] getIntArray(NBTTagIntArray e) {
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
                    MPALibLogger.logDebug("Adding");
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
                NBTTagIntArray nbtTagIntArray = getOrCreateColourTag();
                List<Integer> intList = Arrays.stream(getIntArray(nbtTagIntArray)).boxed().collect(Collectors.toList());

                if (intList.size() > 1 && selectedColour <= intList.size() -1) {
                    intList.remove(selectedColour); // with integer list, will default to index rather than getValue

                    setColourTagMaybe(intList);

                    decrAbove = selectedColour;
                    if (selectedColour == getIntArray(nbtTagIntArray).length) {
                        selectedColour = selectedColour - 1;
                    }

                    EntityPlayerSP player = Minecraft.getMinecraft().player;
                    if (player.world.isRemote)
                        MPSPackets.sendToServer(new ColourInfoPacket(itemSelector.getSelectedItem().inventorySlot, nbtTagIntArray.getIntArray()));
                }
                return true;
            }
            return false;
        }

        public void draw() {
            // colours
            for (int i=0; i < colours().length; i++) {
                new GuiIcons.ArmourColourPatch(this.left() + 8 + i * 8, this.centery() + 8 , new Colour(colours()[i]), null, null, null, null);
            }

            new GuiIcons.ArmourColourPatch(this.left() + 8 + colours().length * 8, this.centery() + 8, Colour.WHITE, null, null, null, null);
            new GuiIcons.SelectedArmorOverlay(this.left() + 8 + selectedColour * 8, this.centery() + 8, Colour.WHITE, null, null, null, null);
            new GuiIcons.MinusSign(this.left() + 8 + selectedColour * 8, this.centery(), Colour.RED, null, null, null, null);
            new GuiIcons.PlusSign(this.left() + 8 + colours().length * 8, this.centery() + 8, Colour.GREEN, null, null, null, null);
        }
    }
}
