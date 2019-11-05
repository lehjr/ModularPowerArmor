package com.github.lehjr.modularpowerarmor.client.gui.modeselection;

import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.SpiralPointToPoint2D;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.legacy.item.IModeChangingItem;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.mpalib.legacy.module.IRightClickModule;
import com.github.lehjr.mpalib.legacy.network.LegacyModeChangeRequestPacket;
import com.github.lehjr.modularpowerarmor.network.MPSPackets;
import com.github.lehjr.modularpowerarmor.client.gui.clickable.ClickableModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RadialModeSelectionFrame implements IGuiFrame {
    protected final long spawnTime;
    protected List<ClickableModule> modeButtons = new ArrayList<>();
    protected int selectedModuleOriginal = -1;
    protected int selectedModuleNew = -1;


    protected EntityPlayer player;
    protected Point2D center;
    protected double radius;
    protected ItemStack stack;

    public RadialModeSelectionFrame(Point2D topleft, Point2D bottomright, EntityPlayer player) {
        spawnTime = System.currentTimeMillis();
        this.player = player;
        this.center = bottomright.plus(topleft).times(0.5);
        this.radius = Math.min(center.minus(topleft).getX(), center.minus(topleft).getY());
        this.stack = player.inventory.getCurrentItem();
        loadItems();
        //Determine which mode is currently active
        if (!stack.isEmpty() && stack.getItem() instanceof IModeChangingItem) {
            if (modeButtons != null) {
                int i = 0;
                for (ClickableModule mode : modeButtons) {
                    if (mode.getModule().getDataName().equals(((IModeChangingItem) stack.getItem()).getActiveMode(stack))) {
                        selectedModuleOriginal = i;
                        break;
                    }
                    i++;
                }
            }
        }
    }

    @Override
    public void init(double v, double v1, double v2, double v3) {

    }

    public RadialModeSelectionFrame() {
        spawnTime = System.currentTimeMillis();
    }

    private boolean alreadyAdded(IRightClickModule module) {
        for (ClickableModule clickie : modeButtons) {
            if (clickie.getModule().getDataName().equals(module.getDataName())) {
                return true;
            }
        }
        return false;
    }

    private void loadItems() {
        if (player != null) {
            List<IRightClickModule> modes = new ArrayList<>();
            for (IPowerModule module : ModuleManager.INSTANCE.getModulesOfType(IRightClickModule.class)) {
                if (ModuleManager.INSTANCE.isValidForItem(stack, module))
                    if (ModuleManager.INSTANCE.itemHasModule(stack, module.getDataName()))
                        modes.add((IRightClickModule) module);
            }

            int modeNum = 0;
            for (IRightClickModule module : modes) {
                if (!alreadyAdded(module)) {
                    ClickableModule clickie = new ClickableModule(module, new SpiralPointToPoint2D(center, radius, (3 * Math.PI / 2) - ((2 * Math.PI * modeNum) / modes.size()), 250));
                    modeButtons.add(clickie);
                    modeNum++;
                }
            }
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
        if (getSelectedModule() != null && selectedModuleOriginal != selectedModuleNew && !stack.isEmpty() && stack.getItem() instanceof IModeChangingItem) {
            // update to detect mode changes
            selectedModuleOriginal = selectedModuleNew;
            ((IModeChangingItem) stack.getItem()).setActiveMode(stack, getSelectedModule().getModule().getDataName());
            MPSPackets.sendToServer(new LegacyModeChangeRequestPacket(getSelectedModule().getModule().getDataName(), player.inventory.currentItem));
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
            IPowerModule selectedModule = module.getModule();
            return Collections.singletonList(module.getLocalizedName(selectedModule));
        }
        return null;
    }
}
