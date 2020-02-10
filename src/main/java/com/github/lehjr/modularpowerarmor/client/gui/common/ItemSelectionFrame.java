package com.github.lehjr.modularpowerarmor.client.gui.common;

import com.github.lehjr.modularpowerarmor.client.sound.SoundDictionary;
import com.github.lehjr.modularpowerarmor.containers.TinkerTableContainer;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableItem;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.FlyFromPointToPoint2D;
import com.github.lehjr.mpalib.client.gui.geometry.GradientAndArcCalculator;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.math.MathUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemSelectionFrame extends ScrollableFrame {
    protected ArrayList<ClickableItem> itemButtons = new ArrayList<>();
    protected int selectedItemStack = -1;
    protected PlayerEntity player;
    protected int lastItemSlot = -1;
    protected List<Point2D> itemPoints;
    protected List<Integer> indices;
    TinkerTableContainer container;

    public ItemSelectionFrame(
            @Nullable TinkerTableContainer container,
            Point2D topleft,
            Point2D bottomright,
            Colour borderColour,
            Colour insideColour,
            PlayerEntity player) {
        super(topleft, bottomright, borderColour, insideColour);
        this.container = container;
        this.player = player;

        if (container != null) {
            loadPoints(container.getModularItemToSlotMap().keySet().size());
        } else {
            loadIndices();
            if (indices != null && !indices.isEmpty()) {
                loadPoints(indices.size());
                loadItems();
            }
        }
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
        if (container != null) {
            loadPoints(container.getModularItemToSlotMap().keySet().size());
        } else {
            loadIndices();
            if (indices != null && !indices.isEmpty()) {
                loadPoints(indices.size());
                loadItems();
            }
        }
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
        List<Point2D> targetPoints = GradientAndArcCalculator.pointsInLine(num,
                new Point2D(centerx, border.top()),
                new Point2D(centerx, border.bottom()), 0, 18);
        for (Point2D point : targetPoints) {
            // Fly from middle over 200 ms
            itemPoints.add(new FlyFromPointToPoint2D(new Point2D(centerx, centery), point, 200));
        }
        totalsize = (targetPoints.size() + 1) * 18; // slot height of 16 + spacing of 2
    }

    private void loadIndices() {
        indices = new ArrayList<>();
        for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if(player.inventory.getStackInSlot(i).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(m-> m instanceof IModularItem).orElse(false)) {
                indices.add(i);
            }
        }
    }

    private void loadItems() {
        if (container != null) {
            itemButtons = new ArrayList<>();
            Iterator<Point2D> pointiterator = itemPoints.iterator();
            for (Integer slotIndex : container.getModularItemToSlotMap().keySet()) {
                Slot slot = container.getSlot(slotIndex);
                int index = slot.getSlotIndex();

                ClickableItem button = new ClickableItem(pointiterator.next(), index);
                button.containerIndex = slotIndex;
                itemButtons.add(button);
            }
        } else if (indices != null && !indices.isEmpty()) {
            itemButtons = new ArrayList<>();
            Iterator<Point2D> pointiterator = itemPoints.iterator();
            for (Integer index : indices) {
                itemButtons.add(new ClickableItem(pointiterator.next(), index));
            }
        }
    }

    @Override
    public void update(double mousex, double mousey) {
        loadItems();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
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
            this.currentscrollpixels = (int) MathUtils.clampDouble((double)(this.currentscrollpixels =
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
            Point2D pos = itemButtons.get(selectedItemStack).getPosition();
            if (pos.getY() > this.currentscrollpixels + border.top() + 4 && pos.getY() < this.currentscrollpixels + border.top() + border.height() - 4) {
                Renderer.drawCircleAround(pos.getX(), pos.getY(), 10);
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
        if (super.mouseClicked(x, y, button)) {
            return true;
        }

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