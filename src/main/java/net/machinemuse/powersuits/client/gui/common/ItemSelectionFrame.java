package net.machinemuse.powersuits.client.gui.common;

import com.github.lehjr.mpalib.client.gui.geometry.FlyFromPointToPoint2D;
import com.github.lehjr.mpalib.client.gui.geometry.GradientAndArcCalculator;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.math.Colour;
import net.machinemuse.powersuits.client.gui.clickable.ClickableItem;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemSelectionFrame extends ScrollableFrame {
    public List<ClickableItem> itemButtons;
    protected int selectedItemStack = -1;
    protected EntityPlayer player;
    protected List<Point2D> itemPoints;
    protected int lastItemSlot = -1;

    public ItemSelectionFrame(Point2D topleft, Point2D bottomright, Colour backgroundColour, Colour borderColour, EntityPlayer player) {
        super(topleft, bottomright, backgroundColour, borderColour);
        this.player = player;
        List<Integer> slots = ItemUtils.getLegacyModularItemSlotsInInventory(player);
        loadPoints(slots.size());
        loadItems();
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);

        List<Integer> slots = ItemUtils.getLegacyModularItemSlotsInInventory(player);
        loadPoints(slots.size());
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
        List<Point2D> targetPoints = GradientAndArcCalculator.pointsInLine(num,
                new Point2D(centerx, border.bottom()),
                new Point2D(centerx, border.top()));
        for (Point2D point : targetPoints) {
            // Fly from middle over 200 ms
            itemPoints.add(new FlyFromPointToPoint2D(
                    new Point2D(centerx, centery),
                    point, 200));
        }
    }

    private void loadItems() {
        if (player != null) {
            itemButtons = new ArrayList<>();
            List<Integer> slots = ItemUtils.getLegacyModularItemSlotsInInventory(player);
            if (slots.size() > itemPoints.size()) {
                loadPoints(slots.size());
            }
            if (slots.size() > 0) {
                Iterator<Point2D> pointiterator = itemPoints.iterator();

                for (int slot : slots) {
                    ClickableItem clickie = new ClickableItem(
                            player.inventory.getStackInSlot(slot),
                            pointiterator.next(), slot);
                    itemButtons.add(clickie);
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
        drawBackground(mouseX, mouseY, partialTicks);
        drawItems(mouseX, mouseY, partialTicks);
        drawSelection();
    }

    private void drawBackground(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
    }

    private void drawItems(int mouseX, int mouseY, float partialTicks) {
        for (ClickableItem item : itemButtons) {
            item.render(mouseX, mouseY, partialTicks);
        }
    }

    private void drawSelection() {
        if (selectedItemStack != -1) {
            Renderer.drawCircleAround(
                    Math.floor(itemButtons.get(selectedItemStack).getPosition().getX()),
                    Math.floor(itemButtons.get(selectedItemStack).getPosition().getY()),
                    10);
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
    public void onMouseDown(double x, double y, int button) {
        int i = 0;
        for (ClickableItem item : itemButtons) {
            if (item.hitBox(x, y)) {
                lastItemSlot = selectedItemStack;
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.BLOCKS, 1, null);
                selectedItemStack = i;
                onSelected();
                break;
            } else {
                i++;
            }
        }
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        int itemHover = -1;
        int i = 0;
        for (ClickableItem item : itemButtons) {
            if (item.hitBox(x, y)) {
                itemHover = i;
                break;
            } else {
                i++;
            }
        }
        if (itemHover > -1) {
            return itemButtons.get(itemHover).getToolTip();
        } else {
            return null;
        }
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
