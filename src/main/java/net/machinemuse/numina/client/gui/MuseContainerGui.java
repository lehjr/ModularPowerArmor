package net.machinemuse.numina.client.gui;

import net.machinemuse.numina.client.gui.clickable.IClickable;
import net.machinemuse.numina.client.gui.frame.IGuiFrame;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseRect;
import net.machinemuse.numina.math.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public class MuseContainerGui <T extends Container> extends MuseContainerScreen2<T> {
    protected long creationTime;
    protected DrawableMuseRect tooltipRect;
    protected List<IGuiFrame> frames;

    public MuseContainerGui(T container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        frames = new ArrayList();
        tooltipRect = new DrawableMuseRect(
                0, 0, 0, 0,
                false,
                Colour.BLACK.withAlpha(0.9),
                Colour.PURPLE);
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
    }

    @Override
    public void init() {
        super.init();
        minecraft.keyboardListener.enableRepeatEvents(true);
        creationTime = System.currentTimeMillis();
    }

    /**
     * Draws the gradient-rectangle background you see in the TinkerTable gui.
     */
    public void drawRectangularBackground() {

    }

    /**
     * Adds a frame to this gui's draw list.
     *
     * @param frame
     */
    public void addFrame(IGuiFrame frame) {
        frames.add(frame);
    }

    /**
     * Draws all clickables in a list
     */
    public void drawClickables(List<? extends IClickable> list, int mouseX, int mouseY, float partialTicks) {
        if (list == null) {
            return;
        }
        for (IClickable clickie : list) {
            clickie.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.drawRectangularBackground(); // The window rectangle
    }

    /**
     * Called every frame, draws the screen!
     */
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        update(mouseX, mouseY);
        renderFrames(mouseX, mouseY, partialTicks);
        super.render(mouseX, mouseY, partialTicks);
    }

    public void update(double x, double y) {
        for (IGuiFrame frame : frames) {
            frame.update(x, y);
        }
    }

    public void renderFrames(int mouseX, int mouseY, float partialTicks) {
        for (IGuiFrame frame : frames) {
            frame.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        for (IGuiFrame frame : frames) {
            if (frame.mouseScrolled(mouseX, mouseY, dWheel))
                return true;
        }
        if (super.mouseScrolled(mouseX, mouseY, dWheel))
            return true;
        return false;
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        for (IGuiFrame frame : frames) {
            if(frame.mouseClicked(x, y, button))
                return true;
        }
        if (super.mouseClicked(x, y, button))
            return true;
        return false;
    }

    /**
     * Called when the mouse is moved or a mouse button is released. Signature:
     * (mouseX, mouseY, which) which==-1 is mouseMove, which==0 or which==1 is
     * mouseUp
     */
    @Override
    public boolean mouseReleased(double x, double y, int which) {
        for (IGuiFrame frame : frames) {
            frame.mouseReleased(x, y, which);
        }
        if (super.mouseReleased(x, y, which))
            return true;
        return false;
    }

    public void drawToolTip(int mouseX, int mouseY) {
//        int mouseX = (int) (minecraft.mouseHelper.getMouseX() * this.width / this.minecraft.mainWindow.getWidth());
//        int mouseY = (int) (minecraft.mouseHelper.getMouseY() * this.height / (double) this.minecraft.mainWindow.getHeight());
        List<ITextComponent> tooltip = getToolTip(mouseX, mouseY);
        if (tooltip != null) {
            tooltip.forEach(ITextComponent::getFormattedText);
            List<String> toolTip2 = new ArrayList<>();
            for (ITextComponent component : tooltip) {
                toolTip2.add(component.getFormattedText());
            }
            renderTooltip(toolTip2, mouseX,mouseY);
        }
    }

    public List<ITextComponent> getToolTip(int x, int y) {
        List<ITextComponent> hitTip;
        for (IGuiFrame frame : frames) {
            hitTip = frame.getToolTip(x, y);
            if (hitTip != null) {
                return hitTip;
            }
        }
        return null;
    }
}