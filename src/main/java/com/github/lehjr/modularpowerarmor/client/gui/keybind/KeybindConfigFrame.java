package com.github.lehjr.modularpowerarmor.client.gui.keybind;

import com.github.lehjr.modularpowerarmor.basemod.config.ClientConfig;
import com.github.lehjr.modularpowerarmor.client.control.KeybindKeyHandler;
import com.github.lehjr.modularpowerarmor.client.control.KeybindManager;
import com.github.lehjr.modularpowerarmor.client.gui.clickable.ClickableKeybinding;
import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableButton;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.client.gui.clickable.IClickable;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.GradientAndArcCalculator;
import com.github.lehjr.mpalib.client.gui.geometry.IRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2F;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.control.KeyBindingHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.*;

import static com.github.lehjr.mpalib.math.MathUtils.clampFloat;

public class KeybindConfigFrame implements IGuiFrame {
    private static KeyBindingHelper keyBindingHelper = new KeyBindingHelper();
    protected Set<ClickableModule> modules;
    protected IClickable selectedClickie;
    protected ClickableKeybinding closestKeybind;
    protected PlayerEntity player;
    protected boolean selecting;
    protected ClickableButton newKeybindButton;
    protected ClickableButton trashKeybindButton;
    protected long takenTime;
    Rect rect;

    public KeybindConfigFrame(Rect backgroundRect, PlayerEntity player) {
        modules = new HashSet();
        for (ClickableKeybinding kb : KeybindManager.getKeybindings()) {
            modules.addAll(kb.getBoundModules());
        }
        this.rect = backgroundRect;
        this.player = player;
        Point2F center = rect.center();
        newKeybindButton = new ClickableButton(new TranslationTextComponent("gui.modularpowerarmor.newKeybind"), center.plus(new Point2F(0, -8)), true);
        trashKeybindButton = new ClickableButton(new TranslationTextComponent("gui.modularpowerarmor.trashKeybind"), center.plus(new Point2F(0, 8)), true);
    }

    @Override
    public void init(float left, float top, float right, float bottom) {
        newKeybindButton.move(rect.center().plus(new Point2F(0, -8)));
        trashKeybindButton.move(rect.center().plus(new Point2F(0, 8)));
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {
            if (selectedClickie == null) {
                for (ClickableModule module : modules) {
                    if (module.hitBox((float)x, (float)y)) {
                        selectedClickie = module;
                        return true;
                    }
                }
                for (ClickableKeybinding keybind : KeybindManager.getKeybindings()) {
                    if (keybind.hitBox((float) x, (float) y)) {
                        selectedClickie = keybind;
                        return true;
                    }
                }
            }
            if (newKeybindButton.hitBox((float)x, (float) y)) {
                selecting = true;
            }
        } else if (button == 1) {
            for (ClickableKeybinding keybind : KeybindManager.getKeybindings()) {
                if (keybind.hitBox((float) x, (float) y)) {
                    keybind.toggleHUDState();
                    return true;
                }
            }
        } else if (button > 2) {
            int key = button - 100;

            if (keyBindingHelper.keyBindingHasKey(key)) {
                takenTime = System.currentTimeMillis();
            }
            if (!keyBindingHelper.keyBindingHasKey(key)) {
                addKeybind(key, true);
            } else if (ClientConfig.GENERAL_ALLOW_CONFLICTING_KEYBINDS.get()) {
                addKeybind(key, false);
            }
            selecting = false;
        }
        return false;
    }

    public void refreshModules() {
        NonNullList<ItemStack> installedModules = NonNullList.create();

        for (EquipmentSlotType slot: EquipmentSlotType.values()) {
            switch (slot.getSlotType()) {
                case HAND:
                    player.getItemStackFromSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
                            iModeChanging -> {
                                if (iModeChanging instanceof IModeChangingItem)
                                    installedModules.addAll(((IModularItem) iModeChanging).getInstalledModulesOfType(IToggleableModule.class));
                            });
                    break;

                case ARMOR:
                    if (slot.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                        player.getItemStackFromSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(
                                iModularItem -> {
                                    if (iModularItem instanceof IModularItem)
                                        installedModules.addAll(((IModularItem) iModularItem).getInstalledModulesOfType(IToggleableModule.class));
                                });
                    }
            }
        }

        List<Point2F> points = GradientAndArcCalculator.pointsInLine(
                installedModules.size(),
                new Point2F(rect.finalLeft() + 10, rect.finalTop() + 10),
                new Point2F(rect.finalLeft() + 10, rect.finalBottom() - 10));
        Iterator<Point2F> pointIterator = points.iterator();
        for (ItemStack module : installedModules) {
            if (!alreadyAdded(module)) {
                ClickableModule clickie = new ClickableModule(module, pointIterator.next(), -1, EnumModuleCategory.NONE);
                modules.add(clickie);
            }
        }
    }

    public boolean alreadyAdded(@Nonnull ItemStack module) {
        if (module.isEmpty())
            return false;

        for (ClickableModule clickie : modules) {
            if (ItemStack.areItemsEqual(clickie.getModule(),module)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (button == 0) {
            if (selectedClickie != null && closestKeybind != null && selectedClickie instanceof ClickableModule) {
                closestKeybind.bindModule((ClickableModule) selectedClickie);
            } else if (selectedClickie != null && selectedClickie instanceof ClickableKeybinding && trashKeybindButton.hitBox((float) x, (float) y)) {
                KeyBinding binding = ((ClickableKeybinding) selectedClickie).getKeyBinding();
                keyBindingHelper.removeKey(binding);
//                KeyBinding.HASH.removeObject(binding.getKeyCode());
                keyBindingHelper.removeKey(binding);
                KeybindManager.getKeybindings().remove(selectedClickie);
            }
            selectedClickie = null;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        return false;
    }

    @Override
    public void update(double mousex, double mousey) {
        if (selecting) {
            return;
        }
        refreshModules();
        this.closestKeybind = null;
        double closestDistance = Double.MAX_VALUE;
        if (this.selectedClickie != null) {
            this.selectedClickie.move((float)mousex, (float)mousey);
            if (this.selectedClickie instanceof ClickableModule) {
                ClickableModule selectedModule = ((ClickableModule) this.selectedClickie);
                for (ClickableKeybinding keybind : KeybindManager.getKeybindings()) {
                    double distance = keybind.getPosition().minus(selectedModule.getPosition()).distance();
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        if (closestDistance < 32) {
                            this.closestKeybind = keybind;
                        }
                    }
                }
            }
        }
        for (ClickableKeybinding keybind : KeybindManager.getKeybindings()) {
            if (keybind != selectedClickie) {
                keybind.unbindFarModules();
            }
            keybind.attractBoundModules(selectedClickie);
        }
        for (IClickable module : modules) {
            if (module != selectedClickie) {
                repelOtherModules(module);
            }
        }
        for (IClickable keybind : KeybindManager.getKeybindings()) {
            if (keybind != selectedClickie) {
                repelOtherModules(keybind);
            }
        }
        for (IClickable module : modules) {
            clampClickiePosition(module);
        }
        for (IClickable keybind : KeybindManager.getKeybindings()) {
            clampClickiePosition(keybind);
        }
    }

    private void clampClickiePosition(IClickable clickie) {
        Point2F position = clickie.getPosition();
        position.setX(clampFloat(position.getX(), rect.finalLeft(), rect.finalRight()));
        position.setY(clampFloat(position.getY(), rect.finalTop(), rect.finalBottom()));
    }

    private double clampDouble(double x, double lower, double upper) {
        if (x < lower) {
            return lower;
        } else if (x > upper) {
            return upper;
        } else {
            return x;
        }
    }

    private void repelOtherModules(IClickable module) {
        Point2F modulePosition = module.getPosition();
        for (ClickableModule otherModule : modules) {
            if (otherModule != selectedClickie && otherModule != module && otherModule.getPosition().distanceTo(modulePosition) < 16) {
                Point2F euclideanDistance = otherModule.getPosition().minus(module.getPosition());
                Point2F directionVector = euclideanDistance.normalize();
                Point2F tangentTarget = directionVector.times(16).plus(module.getPosition());
                Point2F midpointTangent = otherModule.getPosition().midpoint(tangentTarget);
                if (midpointTangent.distanceTo(module.getPosition()) > 2) {
                    otherModule.move(midpointTangent.getX(), midpointTangent.getY());
                }
                // Point2F away = directionVector.times(0).plus(modulePosition);
                // module.move(away.getX(), away.getY());
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
//        Point2F center = rect.center();
//        RenderState.blendingOn();
//        RenderState.on2D();
//        if (selecting) {
//            Renderer.drawCenteredString(I18n.format("gui.modularpowerarmor.pressKey"), center.getX(), center.getY());
//            RenderState.off2D();
//            RenderState.blendingOff();
//            return;
//        }
//        newKeybindButton.render(mouseX, mouseY, partialTicks);
//        trashKeybindButton.render(mouseX, mouseY, partialTicks);
//        TextureUtils.pushTexture(TextureUtils.TEXTURE_QUILT);
//        Renderer.drawCenteredString(I18n.format("gui.modularpowerarmor.keybindInstructions1"), center.getX(), center.getY() + 40);
//        Renderer.drawCenteredString(I18n.format("gui.modularpowerarmor.keybindInstructions2"), center.getX(), center.getY() + 50);
//        Renderer.drawCenteredString(I18n.format("gui.modularpowerarmor.keybindInstructions3"), center.getX(), center.getY() + 60);
//        Renderer.drawCenteredString(I18n.format("gui.modularpowerarmor.keybindInstructions4"), center.getX(), center.getY() + 70);
//        if (takenTime + 1000 > System.currentTimeMillis()) {
//            Point2F pos = newKeybindButton.getPosition().plus(new Point2F(0, -20));
//            Renderer.drawCenteredString(I18n.format("gui.modularpowerarmor.keybindTaken"), pos.getX(), pos.getY());
//        }
//        for (ClickableModule module : modules) {
//            module.render(mouseX, mouseY, partialTicks);
//        }
//        for (ClickableKeybinding keybind : KeybindManager.getKeybindings()) {
//            keybind.render(mouseX, mouseY, partialTicks);
//        }
//        if (selectedClickie != null && closestKeybind != null) {
//            Renderer.drawLineBetween(selectedClickie, closestKeybind, Colour.YELLOW);
//        }
//        RenderState.off2D();
//        RenderState.blendingOff();
//        TextureUtils.popTexture();
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        for (ClickableModule module : modules) {
            if (module.hitBox(x, y)) {
                if (doAdditionalInfo()) {
                    return module.getToolTip();
                }
                return Collections.singletonList(module.getLocalizedName());
            }
        }
        return null;
    }

    public static boolean doAdditionalInfo() {
        return false; //InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT);
    }







    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {

        int key = p_keyPressed_1_; // no idea which one to use here!!

        if (selecting) {
//                if (KeyBinding.HASH.containsItem(key)) {
            if (keyBindingHelper.keyBindingHasKey(key)) {
                takenTime = System.currentTimeMillis();
            }
//                if (!KeyBinding.HASH.containsItem(key)) {
            if (!keyBindingHelper.keyBindingHasKey(key)) {
                addKeybind(key, true);
            } else if (ClientConfig.GENERAL_ALLOW_CONFLICTING_KEYBINDS.get()) {
                addKeybind(key, false);
            }
            selecting = false;
        }

        return true; // no idea what to return here!!!
    }

    private void addKeybind(int key, boolean free) {
        addKeybind(KeyBindingHelper.getInputByCode(key), free);
    }

    private void addKeybind(InputMappings.Input key, boolean free) {
        String name;
        try {
            name = key.getTranslationKey();
        } catch (Exception e) {
            name = "???";
        }
        KeyBinding keybind = new KeyBinding(name, key.getKeyCode(), KeybindKeyHandler.mps);
        ClickableKeybinding clickie = new ClickableKeybinding(keybind, newKeybindButton.getPosition().plus(new Point2F(0, -20)), free, false);
        KeybindManager.getKeybindings().add(clickie);
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
