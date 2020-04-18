package com.github.lehjr.modularpowerarmor.client.gui.modechanging;

import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.IRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.SpiralPointToPoint2F;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.network.MPALibPackets;
import com.github.lehjr.mpalib.network.packets.ModeChangeRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.List;

public class RadialModeSelectionFrame implements IGuiFrame {
    protected final long spawnTime;
    protected List<ClickableModule> modeButtons = new ArrayList<>();
    protected int selectedModuleOriginal = -1;
    protected int selectedModuleNew = -1;

    protected PlayerEntity player;
    protected Point2F center;
    protected float radius;
    protected ItemStack stack;
    Rect rect;

    public RadialModeSelectionFrame(Point2F topleft, Point2F bottomright, PlayerEntity player) {
        spawnTime = System.currentTimeMillis();
        this.player = player;
        rect = new Rect(topleft, bottomright);
        center = rect.center();
        this.radius = Math.min(rect.height(), rect.width());
        this.stack = player.inventory.getCurrentItem();

        loadItems();
    }

    @Override
    public boolean mouseClicked(double v, double v1, int i) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double v, double v1, double v2) {
        return false;
    }

    @Override
    public void init(float left, float top, float right, float bottom) {
        rect.setTargetDimensions(left, top, right, bottom);
        center = rect.center();
        this.radius = Math.min(rect.height(), rect.width());
        modeButtons.clear();
    }

    @Override
    public void update(double mousex, double mousey) {
        //Update items
        loadItems();
        //Determine which mode is selected
        if (System.currentTimeMillis() - spawnTime > 250) {
            selectModule((float) mousex, (float) mousey);
        }
        //Switch to selected mode if mode changed
        if (getSelectedModule() != null && selectedModuleOriginal != selectedModuleNew) {
            // update to detect mode changes
            selectedModuleOriginal = selectedModuleNew;
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler->{
                if (handler instanceof IModeChangingItem) {
                    ((IModeChangingItem) handler).setActiveMode(getSelectedModule().getInventorySlot());
                    MPALibPackets.CHANNEL_INSTANCE.sendToServer(new ModeChangeRequestPacket(getSelectedModule().getInventorySlot(), player.inventory.currentItem));
                }
            });
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        //Draw the installed power fist modes
        for (ClickableModule mode : modeButtons) {
            System.out.println("FIXMEEE!!!");

            mode.render(mouseX, mouseY, partialTicks, Minecraft.getInstance().currentScreen.getBlitOffset());
        }
        //Draw the selected mode indicator
        drawSelection();
    }

    public RadialModeSelectionFrame() {
        spawnTime = System.currentTimeMillis();
    }

    private void loadItems() {
        if (player != null && modeButtons.isEmpty()) {
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler->{
                if (handler instanceof IModeChangingItem) {
                    List<Integer> modes = ((IModeChangingItem) handler).getValidModes();
                    int activeMode = ((IModeChangingItem) handler).getActiveMode();
                    if (activeMode > 0)
                        selectedModuleOriginal = activeMode;
                    int modeNum = 0;
                    for (int mode : modes) {
                        ClickableModule clickie = new ClickableModule(handler.getStackInSlot(mode), new SpiralPointToPoint2F(center, radius, (float) ((3F * Math.PI / 2) - ((2F * Math.PI * modeNum) / modes.size())), 250F), mode, EnumModuleCategory.NONE);
                        modeButtons.add(clickie);
                        modeNum ++;
                    }
                }
            });
        }
    }

    private void selectModule(float x, float y) {
        if (modeButtons != null) {
            int i = 0;
            for (ClickableModule module : modeButtons) {
                if (module.hitBox(x, y)) {
                    selectedModuleNew = i;
                    break;
                }
                i++;
            }
        }
    }

    public ClickableModule getSelectedModule() {
        if (modeButtons.size() > selectedModuleNew && selectedModuleNew != -1) {
            return modeButtons.get(selectedModuleNew);
        } else {
            return null;
        }
    }

    public void drawSelection() {
        ClickableModule module = getSelectedModule();
        if (module != null) {
            Point2F pos = module.getPosition();
            System.out.println("FIXMEEEE");
            Renderer.drawCircleAround(pos.getX(), pos.getY(), 10, Minecraft.getInstance().currentScreen.getBlitOffset());
        }
    }

    @Override
    public boolean mouseReleased(double v, double v1, int i) {
        return false;
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        ClickableModule module = getSelectedModule();
        if (module != null) {
            return module.getToolTip();
        }
        return null;
    }

    @Override
    public IRect getBorder() {
        return rect;
    }

    boolean enabled = true;
    @Override
    public void setEnabled(boolean b) {
        enabled = b;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    boolean visible = true;
    @Override
    public void setVisible(boolean b) {
        visible = b;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public IRect setLeft(float v) {
        return rect.setLeft(v);
    }

    @Override
    public IRect setRight(float v) {
        return rect.setRight(v);
    }

    @Override
    public IRect setTop(float v) {
        return rect.setTop(v);
    }

    @Override
    public IRect setBottom(float v) {
        return rect.setBottom(v);
    }

    @Override
    public IRect setWidth(float v) {
        return rect.setWidth(v);
    }

    @Override
    public IRect setHeight(float v) {
        return rect.setHeight(v);
    }
}
