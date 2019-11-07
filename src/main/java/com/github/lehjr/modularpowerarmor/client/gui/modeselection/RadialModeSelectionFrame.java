package com.github.lehjr.modularpowerarmor.client.gui.modeselection;

import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.SpiralPointToPoint2D;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.network.MPALibPackets;
import com.github.lehjr.mpalib.network.packets.ModeChangeRequestPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RadialModeSelectionFrame implements IGuiFrame {
    protected final long spawnTime;
    protected List<ClickableModule> modeButtons = new ArrayList<>();
    protected int selectedModuleOriginal = -1;
    protected int selectedModuleNew = -1;
    protected EntityPlayer player;
    protected Point2D center;
    protected double radius;
    protected ItemStack stack;

    Rect rect;

    public RadialModeSelectionFrame(Point2D topleft, Point2D bottomright, EntityPlayer player) {
        spawnTime = System.currentTimeMillis();
        this.player = player;
        rect = new Rect(topleft, bottomright);
        center = rect.center();
        this.radius = Math.min(rect.height(), rect.width());
        this.stack = player.inventory.getCurrentItem();

        loadItems();
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        rect.setTargetDimensions(left, top, right, bottom);
        center = rect.center();
        this.radius = Math.min(rect.height(), rect.width());
        modeButtons.clear();
    }

    public RadialModeSelectionFrame() {
        spawnTime = System.currentTimeMillis();
    }

    private void loadItems() {
        if (player != null && modeButtons.isEmpty()) {
            Optional.ofNullable(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(handler->{
                if (handler instanceof com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem) {
                    List<Integer> modes = ((com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem) handler).getValidModes();
                    int activeMode = ((com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem) handler).getActiveMode();
                    if (activeMode > 0)
                        selectedModuleOriginal = activeMode;
                    int modeNum = 0;
                    for (int mode : modes) {
                        ClickableModule clickie = new ClickableModule(handler.getStackInSlot(mode), new SpiralPointToPoint2D(center, radius, (3 * Math.PI / 2) - ((2 * Math.PI * modeNum) / modes.size()), 250), mode, EnumModuleCategory.NONE);
                        modeButtons.add(clickie);
                        modeNum ++;
                    }
                }
            });
        }
    }


    private void selectModule(double x, double y) {
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

    @Override
    public void update(double mousex, double mousey) {
        //Update items
        loadItems();
        //Determine which mode is selected
        if (System.currentTimeMillis() - spawnTime > 250) {
            selectModule(mousex, mousey);
        }
        //Switch to selected mode if mode changed
        if (getSelectedModule() != null && selectedModuleOriginal != selectedModuleNew) {
            // update to detect mode changes
            selectedModuleOriginal = selectedModuleNew;
            Optional.ofNullable(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(handler->{
                if (handler instanceof IModeChangingItem) {
                    ((IModeChangingItem) handler).setActiveMode(getSelectedModule().getInventorySlot());
                    MPALibPackets.INSTANCE.sendToServer(new ModeChangeRequestPacket(getSelectedModule().getInventorySlot(), player.inventory.currentItem));
                }
            });
        }
    }

    public void drawSelection() {
        ClickableModule module = getSelectedModule();
        if (module != null) {
            Point2D pos = module.getPosition();
            Renderer.drawCircleAround(pos.getX(), pos.getY(), 10);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        //Draw the installed power fist modes
        for (ClickableModule mode : modeButtons) {
            mode.render(mouseX, mouseY, partialTicks);
        }
        //Draw the selected mode indicator
        drawSelection();
    }

    @Override
    public void onMouseDown(double x, double y, int button) {

    }

    @Override
    public void onMouseUp(double x, double y, int button) {

    }

    @Override
    public List<String> getToolTip(int x, int y) {
        ClickableModule module = getSelectedModule();
        if (module != null) {
            return module.getToolTip();
        }
        return null;
    }
}
