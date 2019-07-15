package net.machinemuse.powersuits.client.gui;

import net.machinemuse.numina.client.gui.clickable.ClickableModule;
import net.machinemuse.numina.client.gui.frame.IGuiFrame;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.math.geometry.MusePoint2D;
import net.machinemuse.numina.math.geometry.SpiralPointToPoint2D;
import net.machinemuse.powersuits.containers.ModeChangingContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RadialModeSelectionFrame implements IGuiFrame {
    protected final long spawnTime;
    protected List<ClickableModule> modeButtons = new ArrayList<>();
    protected int selectedModuleOriginal = -1;
    protected int selectedModuleNew = -1;
    ModeChangingContainer container;

    protected PlayerEntity player;
    protected MusePoint2D center;
    protected double radius;
    protected ItemStack stack;

    public RadialModeSelectionFrame(ModeChangingContainer container, MusePoint2D topleft, MusePoint2D bottomright, PlayerEntity player) {
        spawnTime = System.currentTimeMillis();
        this.player = player;
        this.center = bottomright.plus(topleft).times(0.5);
        this.radius = Math.min(center.minus(topleft).getX(), center.minus(topleft).getY());
        this.stack = player.inventory.getCurrentItem();
        this.container = container;

        loadItems();
        //Determine which mode is currently active
//        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iModeChanigng->{
//            if (iModeChanigng instanceof IModeChangingItem) {
//                if (!container.inventorySlots.isEmpty()) {
//                    selectedModuleOriginal = ((IModeChangingItem) iModeChanigng).getActiveMode();
//                }
//            }
//        });
    }

    private void loadItems() {
//        System.out.println("loading");
//        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iModeChanigng-> {
//            if (iModeChanigng instanceof IModeChangingItem) {
//
//                int modeNum = 0;
//                for (Slot slot : container.inventorySlots) {
//                    ClickableModule clickie = new ClickableModule(slot, new SpiralPointToPoint2D(center, radius, (3 * Math.PI / 2) - ((2 * Math.PI * modeNum) / container.inventorySlots.size()), 250));
//                    modeButtons.add(clickie);
//                    modeNum++;
//                }
//            }
//        });


//        int modeNum = 0;
//        for (Slot slot : container.inventorySlots) {
//            ClickableModule clickie = new ClickableModule(slot, new SpiralPointToPoint2D(center, radius, (3 * Math.PI / 2) - ((2 * Math.PI * modeNum) / container.inventorySlots.size()), 250));
//            modeButtons.add(clickie);
//            modeNum++;
//        }








    }

    @Override
    public boolean mouseClicked(double v, double v1, int i) {
        return false;
    }

    @Override
    public boolean mouseReleased(double v, double v1, int i) {
        return false;
    }

    @Override
    public boolean onMouseScrolled(double v, double v1, double v2) {
        return false;
    }

    @Override
    public void update(double mousex, double mousey) {
        //Update items
        loadItems();
        //Determine which mode is selected
        if (System.currentTimeMillis() - spawnTime > 250) {
            selectModule(mousex, mousey);
        }
//        //Switch to selected mode if mode changed
//        if (getSelectedModule() != null && selectedModuleOriginal != selectedModuleNew && !stack.isEmpty() && stack.getItem() instanceof IModeChangingItem) {
//            // update to detect mode changes
//            selectedModuleOriginal = selectedModuleNew;
////            ((IModeChangingItem) stack.getItem()).setActiveMode(stack, getSelectedModule().getModule().getDataName());
////            MPSPackets.sendToServer(new MusePacketModeChangeRequest(player, getSelectedModule().getModule().getDataName(), player.inventory.currentItem));
//        }
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
    public List<ITextComponent> getToolTip(int x, int y) {
        ClickableModule module = getSelectedModule();
        if (module != null) {
//            ItemStack selectedModule = module.getItemStack();
//            return Collections.singletonList(module.getLocalizedName(selectedModule));
        }
        return null;
    }

//    private boolean alreadyAdded(IRightClickModule module) {
////        for (ClickableModule clickie : modeButtons) {
////            if (clickie.getModule().getDataName().equals(module.getDataName())) {
////                return true;
////            }
////        }
//        return false;
//    }

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
}
