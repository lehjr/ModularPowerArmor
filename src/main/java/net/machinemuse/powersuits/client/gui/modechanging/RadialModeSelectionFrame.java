package net.machinemuse.powersuits.client.gui.modechanging;

import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.client.gui.clickable.ClickableModule;
import net.machinemuse.numina.client.gui.frame.IGuiFrame;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.geometry.MuseRect;
import net.machinemuse.numina.client.gui.geometry.SpiralPointToPoint2D;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.network.NuminaPackets;
import net.machinemuse.numina.network.packets.MusePacketModeChangeRequest;
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
    protected MusePoint2D center;
    protected double radius;
    protected ItemStack stack;
    MuseRect rect;

    public RadialModeSelectionFrame(MusePoint2D topleft, MusePoint2D bottomright, PlayerEntity player) {
        spawnTime = System.currentTimeMillis();
        this.player = player;
        rect = new MuseRect(topleft, bottomright);
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
    public void init(double left, double top, double right, double bottom) {
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
            selectModule(mousex, mousey);
        }
        //Switch to selected mode if mode changed
        if (getSelectedModule() != null && selectedModuleOriginal != selectedModuleNew) {
            // update to detect mode changes
            selectedModuleOriginal = selectedModuleNew;
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler->{
                if (handler instanceof IModeChangingItem) {
                    ((IModeChangingItem) handler).setActiveMode(getSelectedModule().getInventorySlot());
                    NuminaPackets.CHANNEL_INSTANCE.sendToServer(new MusePacketModeChangeRequest(getSelectedModule().getInventorySlot(), player.inventory.currentItem));
                }
            });
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
                        ClickableModule clickie = new ClickableModule(handler.getStackInSlot(mode), new SpiralPointToPoint2D(center, radius, (3 * Math.PI / 2) - ((2 * Math.PI * modeNum) / modes.size()), 250), mode);
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

    public void drawSelection() {
        ClickableModule module = getSelectedModule();
        if (module != null) {
            MusePoint2D pos = module.getPosition();
            MuseRenderer.drawCircleAround(pos.getX(), pos.getY(), 10);
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
}
