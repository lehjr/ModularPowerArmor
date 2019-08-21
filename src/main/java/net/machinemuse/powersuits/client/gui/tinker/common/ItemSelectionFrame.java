package net.machinemuse.powersuits.client.gui.tinker.common;

import net.machinemuse.numina.client.gui.clickable.ClickableItem;
import net.machinemuse.numina.client.gui.geometry.FlyFromPointToPoint2D;
import net.machinemuse.numina.client.gui.geometry.GradientAndArcCalculator;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.scrollable.ScrollableFrame;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.math.MuseMathUtils;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.containers.IModularItemToSlotMapProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemSelectionFrame<T extends IModularItemToSlotMapProvider> extends ScrollableFrame {
    protected ArrayList<ClickableItem> itemButtons = new ArrayList<>();
    public Container container;
    protected int selectedItemStack = -1;
    protected PlayerEntity player;
    protected int lastItemSlot = -1;
    protected List<MusePoint2D> itemPoints;
    int totalItems;

    /**
     * @param slotProvider
     * @param topleft
     * @param bottomright
     * @param borderColour
     * @param insideColour
     * @param player
     */
    public ItemSelectionFrame(T slotProvider, MusePoint2D topleft, MusePoint2D bottomright, Colour borderColour, Colour insideColour, PlayerEntity player) {
        super(topleft, bottomright, borderColour, insideColour);
        this.player = player;
        this.container = slotProvider.getContainer();
        totalItems = ((IModularItemToSlotMapProvider) container).getModularItemToSlotMap().keySet().size();
        loadPoints(totalItems);
        loadItems();
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);

        totalItems = itemButtons.size();
        loadPoints(totalItems);
        loadItems();
    }

    public int getLastItemSlot() {
        return lastItemSlot;
    }

    public int getSelectedItemSlot() {
        return selectedItemStack;
    }

    private void loadPoints(int num) {
        double centerx = (border.left() + border.right()) / 2;
        double centery = (border.top() + border.bottom()) / 2;

        itemPoints = new ArrayList();
        List<MusePoint2D> targetPoints = GradientAndArcCalculator.pointsInLine(num,
                new MusePoint2D(centerx, border.top()),
                new MusePoint2D(centerx, border.bottom()), 0, 18);
        for (MusePoint2D point : targetPoints) {
            // Fly from middle over 200 ms
            itemPoints.add(new FlyFromPointToPoint2D(new MusePoint2D(centerx, centery), point, 200));
        }
        totalsize = (targetPoints.size() + 1) * 18; // slot height of 16 + spacing of 2
    }

    private void loadItems() {
        if (player != null) {
            if (totalItems > 0) {
                itemButtons = new ArrayList<ClickableItem>(((IModularItemToSlotMapProvider) container).getModularItemToSlotMap().keySet());
                Iterator<MusePoint2D> pointiterator = itemPoints.iterator();

                for (ClickableItem slot : itemButtons) {
                    MusePoint2D point = pointiterator.next();
                    slot.move(point.getX(), point.getY());
                }
            }
        }
    }

    @Override
    public void update(double mousex, double mousey) {
        loadItems();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        this.currentscrollpixels = Math.min(currentscrollpixels, getMaxScrollPixels());
        super.preRender(mouseX, mouseY, partialTicks);
        GL11.glPushMatrix();
        GL11.glTranslatef(0, -currentscrollpixels, 0);
        drawItems(mouseX, mouseY, partialTicks);
        drawSelection(mouseX, mouseY, partialTicks);
        GL11.glPopMatrix();
        super.postRender(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        if (this.border.containsPoint(mouseX, mouseY)) {
            this.currentscrollpixels = (int) MuseMathUtils.clampDouble((double)(this.currentscrollpixels =
                    (int)((double)this.currentscrollpixels - dWheel * this.getScrollAmount())), 0.0D, this.getMaxScrollPixels());
            return true;
        } else {
            return false;
        }
    }

    private void drawItems(int mouseX, int mouseY, float partialTicks) {
        for (ClickableItem item : itemButtons) {
            item.render(mouseX, mouseY, partialTicks);
        }
    }

    private void drawSelection(int mouseX, int mouseY, float partialTicks) {
        if (selectedItemStack != -1) {
            MusePoint2D pos = itemButtons.get(selectedItemStack).getPosition();
            if (pos.getY() > this.currentscrollpixels + border.top() + 4 && pos.getY() < this.currentscrollpixels + border.top() + border.height() - 4) {
                MuseRenderer.drawCircleAround(pos.getX(), pos.getY(), 10);
            }
        }
    }

    public boolean hasNoItems() {
        return itemButtons.size() == 0;
    }

    public ClickableItem getPreviousSelectedItem() {
        if (itemButtons.size() > lastItemSlot && lastItemSlot != -1) {
            return itemButtons.get(lastItemSlot);
        } else {
            return null;
        }
    }

    public ClickableItem getSelectedItem() {
        if (itemButtons.size() > selectedItemStack && selectedItemStack != -1) {
            return itemButtons.get(selectedItemStack);
        } else {
            return null;
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (super.mouseClicked(x, y, button))
            return true;

        if (border.containsPoint(x, y)) {
            y += currentscrollpixels;
            int i = 0;
            for (ClickableItem item : itemButtons) {
                if (item.hitBox(x, y)) {
                    lastItemSlot = selectedItemStack;
                    Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                    selectedItemStack = i;
                    if(getSelectedItem() != getPreviousSelectedItem())
                        onSelected();
                    return true;
                } else {
                    i++;
                }
            }
        }
        return false;
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        if (border.containsPoint(x, y)) {
            y += currentscrollpixels;
            if (itemButtons != null) {
                for (ClickableItem item : itemButtons) {
                    if (item.hitBox(x, y)) {
                        return item.getToolTip();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Sets code to be executed when a new item is selected
     * @param doThisIn
     */
    OnSelectNewItem doThis;
    public void setDoOnNewSelect(OnSelectNewItem doThisIn) {
        doThis = doThisIn;
    }

    /**
     * runs preset code when new item is selected
     */
    void onSelected() {
        if(this.doThis != null) {
            this.doThis.onSelected(this);
        }
    }

    public interface OnSelectNewItem {
        void onSelected(ItemSelectionFrame doThis);
    }
}