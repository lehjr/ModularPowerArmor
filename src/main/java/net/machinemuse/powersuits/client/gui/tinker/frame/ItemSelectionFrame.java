package net.machinemuse.powersuits.client.gui.tinker.frame;

import net.machinemuse.numina.client.gui.clickable.ClickableModularItem;
import net.machinemuse.numina.client.gui.scrollable.ScrollableFrame;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.math.geometry.FlyFromPointToPoint2D;
import net.machinemuse.numina.math.geometry.GradientAndArcCalculator;
import net.machinemuse.numina.math.geometry.MusePoint2D;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.containers.TinkerTableContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemSelectionFrame extends ScrollableFrame {
    protected List<ClickableModularItem> itemButtons = new ArrayList<>();
    TinkerTableContainer container;
    protected int selectedItemStack = -1;
    protected PlayerEntity player;
    protected int lastItemSlot = -1;
    protected List<MusePoint2D> itemPoints;
    int totalItems;


    /**
     * FIXME: although this is a scrollable frame, there is no scrolling code here ... there should be minimum borders for ClickableItems and they should not overlap
     *
     *
     * @param container
     * @param topleft
     * @param bottomright
     * @param borderColour
     * @param insideColour
     * @param player
     */
    public ItemSelectionFrame(TinkerTableContainer container, MusePoint2D topleft, MusePoint2D bottomright, Colour borderColour, Colour insideColour, PlayerEntity player) {
        super(topleft, bottomright, borderColour, insideColour);
        this.player = player;
        this.container = container;

        for (ClickableModularItem slot : container.getModularItemToSlotMap().keySet()) {
                itemButtons.add(slot);
        }
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
                new MusePoint2D(centerx, border.bottom()),
                new MusePoint2D(centerx, border.top()));
        for (MusePoint2D point : targetPoints) {
            // Fly from middle over 200 ms
            itemPoints.add(new FlyFromPointToPoint2D(
                    new MusePoint2D(centerx, centery),
                    point, 200));
        }
    }

    private void loadItems() {
        if (player != null) {
            itemButtons = new ArrayList<>();

            if (totalItems > 0) {
                Iterator<MusePoint2D> pointiterator = itemPoints.iterator();
                for (Slot slot : container.inventorySlots) {
                    if (slot instanceof ClickableModularItem) {
                        MusePoint2D point = pointiterator.next();
                        ((ClickableModularItem) slot).move(point.getX(), point.getY());
                        itemButtons.add((ClickableModularItem) slot);
                    }
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
        drawItems(mouseX, mouseY, partialTicks);
        drawSelection(mouseX, mouseY, partialTicks);
    }

    private void drawItems(int mouseX, int mouseY, float partialTicks) {
        for (ClickableModularItem item : itemButtons) {
            item.render(mouseX, mouseY, partialTicks);
        }
    }

    private void drawSelection(int mouseX, int mouseY, float partialTicks) {
        if (selectedItemStack != -1) {
            MuseRenderer.drawCircleAround(
                    Math.floor(itemButtons.get(selectedItemStack).getPosition().getX()),
                    Math.floor(itemButtons.get(selectedItemStack).getPosition().getY()),
                    10);
        }
    }

    public boolean hasNoItems() {
        return itemButtons.size() == 0;
    }

    public ClickableModularItem getPreviousSelectedItem() {
        if (itemButtons.size() > lastItemSlot && lastItemSlot != -1) {
            return itemButtons.get(lastItemSlot);
        } else {
            return null;
        }
    }

    public ClickableModularItem getSelectedItem() {
        if (itemButtons.size() > selectedItemStack && selectedItemStack != -1) {
            return itemButtons.get(selectedItemStack);
        } else {
            return null;
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        int i = 0;
        for (ClickableModularItem item : itemButtons) {
            if (item.hitBox(x, y)) {
                lastItemSlot = selectedItemStack;
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                selectedItemStack = i;
                return true;
            } else {
                i++;
            }
        }
        return false;
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        for (ClickableModularItem item : itemButtons) {
            if (item.hitBox(x, y)) {
                return item.getToolTip();
            }
        }
            return null;
    }
}
