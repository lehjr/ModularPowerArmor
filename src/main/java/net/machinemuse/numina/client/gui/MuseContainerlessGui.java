package net.machinemuse.numina.client.gui;

import net.machinemuse.numina.client.gui.clickable.IClickable;
import net.machinemuse.numina.client.gui.frame.IGuiFrame;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseRect;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.math.Colour;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

public class MuseContainerlessGui extends Screen {
    protected long creationTime;
    protected int xSize;
    protected int ySize;
    protected DrawableMuseRect tooltipRect;

    protected List<IGuiFrame> frames;

    public MuseContainerlessGui(ITextComponent titleIn) {
        super(titleIn);
        frames = new ArrayList();
        tooltipRect = new DrawableMuseRect(0, 0, 0, 0, false, new Colour(0.2F, 0.6F, 0.9F, 0.7F), new Colour(0.1F, 0.3F, 0.4F, 0.7F));
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void init() {
        super.init();
        minecraft.keyboardListener.enableRepeatEvents(true);
        creationTime = System.currentTimeMillis();

        int xpadding = (width - getxSize()) / 2;
        int ypadding = (height - ySize) / 2;
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
    public void renderBackground() {
        super.renderBackground();
        this.drawRectangularBackground(); // The window rectangle
    }

    /**
     * Called every frame, draws the screen!
     */
    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        // item lighting code in super.render method screws up this lighting
//        super.render(mouseX, mouseY, partialTicks);
        update(mouseX, mouseY);
//        renderBackground();
        for (IGuiFrame frame : frames) {
            frame.render(mouseX, mouseY, partialTicks);
        }
        drawToolTip();
    }

    public void update(double x, double y) {
        //        double x = minecraft.mouseHelper.getMouseX() * this.width / (double) this.minecraft.mainWindow.getWidth();
//        double y = minecraft.mouseHelper.getMouseY() * this.height / (double) this.minecraft.mainWindow.getHeight();
        for (IGuiFrame frame : frames) {
            frame.update(x, y);
        }
    }

    /**
     * Returns the first ID in the list that is hit by a click
     *
     * @return
     */
    public int hitboxClickables(int x, int y, List<? extends IClickable> list) {
        if (list == null) {
            return -1;
        }
        IClickable clickie;
        for (int i = 0; i < list.size(); i++) {
            clickie = list.get(i);
            if (clickie.hitBox(x, y)) {
                // MuseLogger.logDebug("Hit!");
                return i;
            }
        }
        return -1;
    }

    /**
     * Whether or not this gui pauses the game in single player.
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * Returns absolute screen coordinates (int 0 to width) from a relative
     * coordinate (float -1.0F to +1.0F)
     *
     * @param relx Relative X coordinate
     * @return Absolute X coordinate
     */
    public int absX(double relx) {
        int absx = (int) ((relx + 1) * getxSize() / 2);
        int xpadding = (width - getxSize()) / 2;
        return absx + xpadding;
    }

    /**
     * Returns relative coordinate (float -1.0F to +1.0F) from absolute
     * coordinates (int 0 to width)
     */
    public int relX(double absx) {
        int padding = (width - getxSize()) / 2;
        return (int) ((absx - padding) * 2 / getxSize() - 1);
    }

    /**
     * Returns absolute screen coordinates (int 0 to width) from a relative
     * coordinate (float -1.0F to +1.0F)
     *
     * @param rely Relative Y coordinate
     * @return Absolute Y coordinate
     */
    public int absY(double rely) {
        int absy = (int) ((rely + 1) * ySize / 2);
        int ypadding = (height - ySize) / 2;
        return absy + ypadding;
    }

    /**
     * Returns relative coordinate (float -1.0F to +1.0F) from absolute
     * coordinates (int 0 to width)
     */
    public int relY(float absy) {
        int padding = (height - ySize) / 2;
        return (int) ((absy - padding) * 2 / ySize - 1);
    }

    /**
     * @return the xSize
     */
    public int getxSize() {
        return xSize;
    }

    /**
     * @param xSize the xSize to set
     */
    public void setxSize(int xSize) {
        this.xSize = xSize;
    }

    /**
     * @return the ySize
     */
    public int getySize() {
        return ySize;
    }

    /**
     * @param ySize the ySize to set
     */
    public void setySize(int ySize) {
        this.ySize = ySize;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        for (IGuiFrame frame : frames) {
            if (frame.mouseScrolled(mouseX, mouseY, dWheel))
                return true;
        }
        return false;
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    public boolean mouseClicked(double x, double y, int button) {
        for (IGuiFrame frame : frames) {
            frame.mouseClicked(x, y, button);
        }
        return true;
    }

    /**
     * Called when the mouse is moved or a mouse button is released. Signature:
     * (mouseX, mouseY, which) which==-1 is mouseMove, which==0 or which==1 is
     * mouseUp
     */

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int which) {
        for (IGuiFrame frame : frames) {
            if(frame.mouseReleased(mouseX, mouseY, which))
                return true;
        }
        return false;
    }

    protected void drawToolTip() {
        int x = (int) (minecraft.mouseHelper.getMouseX() * this.width / this.minecraft.mainWindow.getWidth());
        int y = (int) (minecraft.mouseHelper.getMouseY() * this.height / (double) this.minecraft.mainWindow.getHeight());
        List<ITextComponent> tooltip = getToolTip(x, y);
        if (tooltip != null) {
            double strwidth = 0;
            for (ITextComponent s : tooltip) {
                double currstrwidth = MuseRenderer.getStringWidth(s.getString());
                if (currstrwidth > strwidth) {
                    strwidth = currstrwidth;
                }
            }
            double top, bottom, left, right;
            if (y > this.height / 2) {
                top = y - 10 * tooltip.size() - 8;
                bottom = y;
                left = x;
                right = x + 8 + strwidth;
            } else {
                top = y;
                bottom = y + 10 * tooltip.size() + 8;

                left = x + 4;
                right = x + 12 + strwidth;
            }

            tooltipRect.setTop(top);
            tooltipRect.setLeft(left);
            tooltipRect.setRight(right);
            tooltipRect.setBottom(bottom);
            tooltipRect.draw();
            for (int i = 0; i < tooltip.size(); i++) {
                MuseRenderer.drawString(tooltip.get(i).getString(), left + 4, bottom - 10 * (tooltip.size() - i) - 4);
            }
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
