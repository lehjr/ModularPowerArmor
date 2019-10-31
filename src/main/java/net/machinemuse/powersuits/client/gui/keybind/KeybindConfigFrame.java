package net.machinemuse.powersuits.client.gui.keybind;

import com.github.lehjr.mpalib.client.gui.ContainerlessGui;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableButton;
import com.github.lehjr.mpalib.client.gui.clickable.IClickable;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.GradientAndArcCalculator;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.render.RenderState;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.client.render.TextureUtils;
import com.github.lehjr.mpalib.control.KeyBindingHelper;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.lehjr.mpalib.math.Colour;
import net.machinemuse.powersuits.client.gui.clickable.ClickableKeybinding;
import net.machinemuse.powersuits.client.gui.clickable.ClickableModule;
import net.machinemuse.powersuits.common.ModuleManager;
import net.machinemuse.powersuits.common.config.MPSConfig;
import net.machinemuse.powersuits.client.control.KeybindKeyHandler;
import net.machinemuse.powersuits.client.control.KeybindManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

import java.util.*;

public class KeybindConfigFrame implements IGuiFrame {
    private static KeyBindingHelper keyBindingHelper = new KeyBindingHelper();
    protected Set<ClickableModule> modules;
    protected IClickable selectedClickie;
    protected ClickableKeybinding closestKeybind;
    protected EntityPlayer player;
    Rect rect;
    protected ContainerlessGui gui;
    protected boolean selecting;
    protected ClickableButton newKeybindButton;
    protected ClickableButton trashKeybindButton;
    protected long takenTime;

    public KeybindConfigFrame(ContainerlessGui gui, EntityPlayer player) {
        modules = new HashSet();
        for (ClickableKeybinding kb : KeybindManager.getKeybindings()) {
            modules.addAll(kb.getBoundModules());
        }
        this.gui = gui;
        rect = new Rect(0, 0, 0, 0);

        this.player = player;
//
        newKeybindButton = new ClickableButton(I18n.format("gui.powersuits.newKeybind"), new Point2D(0, 0), true);
        trashKeybindButton = new ClickableButton(I18n.format("gui.powersuits.trashKeybind"), new Point2D(0, 0), true);
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        rect.setTargetDimensions(left, top, right, bottom);
        newKeybindButton.move(rect.center().plus(new Point2D(0, -8)));
        trashKeybindButton.move(rect.center().plus(new Point2D(0, 8)));
    }

    @Override
    public void onMouseDown(double x, double y, int button) {
        if (button == 0) {
            if (selectedClickie == null) {
                for (ClickableModule module : modules) {
                    if (module.hitBox(x, y)) {
                        selectedClickie = module;
                        return;
                    }
                }
                for (ClickableKeybinding keybind : KeybindManager.getKeybindings()) {
                    if (keybind.hitBox(x, y)) {
                        selectedClickie = keybind;
                        return;
                    }
                }
            }
            if (newKeybindButton.hitBox(x, y)) {
                selecting = true;
            }
        } else if (button == 1) {
            for (ClickableKeybinding keybind : KeybindManager.getKeybindings()) {
                if (keybind.hitBox(x, y)) {
                    keybind.toggleHUDState();
                    return;
                }
            }
        } else if (button > 2) {
            int key = button - 100;
//            if (KeyBinding.HASH.containsItem(key)) {
            if (keyBindingHelper.keyBindingHasKey(key)) {
                takenTime = System.currentTimeMillis();
            }
//            if (!KeyBinding.HASH.containsItem(key)) {
            if (!keyBindingHelper.keyBindingHasKey(key)) {
                addKeybind(key, true);
            } else if (MPSConfig.INSTANCE.allowConflictingKeybinds()) {
                addKeybind(key, false);
            }
            selecting = false;
        }
    }

    public void refreshModules() {
        List<IPowerModule> installedModules = ModuleManager.INSTANCE.getPlayerInstalledModules(player);
        List<Point2D> points = GradientAndArcCalculator.pointsInLine(
                installedModules.size(),
                new Point2D(rect.finalLeft() + 10, rect.finalTop() + 10),
                new Point2D(rect.finalLeft() + 10, rect.finalBottom() - 10));
        Iterator<Point2D> pointIterator = points.iterator();
        for (IPowerModule module : installedModules) {
            if (module instanceof IToggleableModule && !alreadyAdded(module)) {
                ClickableModule clickie = new ClickableModule(module, pointIterator.next());
                modules.add(clickie);
            }
        }
    }

    public boolean alreadyAdded(IPowerModule module) {
        for (ClickableModule clickie : modules) {
            if (clickie.getModule().getDataName().equals(module.getDataName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMouseUp(double x, double y, int button) {
        if (button == 0) {
            if (selectedClickie != null && closestKeybind != null && selectedClickie instanceof ClickableModule) {
                closestKeybind.bindModule((ClickableModule) selectedClickie);
            } else if (selectedClickie != null && selectedClickie instanceof ClickableKeybinding && trashKeybindButton.hitBox(x, y)) {
                KeyBinding binding = ((ClickableKeybinding) selectedClickie).getKeyBinding();
                keyBindingHelper.removeKey(binding);
//                KeyBinding.HASH.removeObject(binding.getKeyCode());
                keyBindingHelper.removeKey(binding.getKeyCode());
                KeybindManager.getKeybindings().remove(selectedClickie);
            }
            selectedClickie = null;
        }

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
            this.selectedClickie.move(mousex, mousey);
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
        Point2D position = clickie.getPosition();
        position.setX(clampDouble(position.getX(), rect.finalLeft(), rect.finalRight()));
        position.setY(clampDouble(position.getY(), rect.finalTop(), rect.finalBottom()));
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
        Point2D modulePosition = module.getPosition();
        for (ClickableModule otherModule : modules) {
            if (otherModule != selectedClickie && otherModule != module && otherModule.getPosition().distanceTo(modulePosition) < 16) {
                Point2D euclideanDistance = otherModule.getPosition().minus(module.getPosition());
                Point2D directionVector = euclideanDistance.normalize();
                Point2D tangentTarget = directionVector.times(16).plus(module.getPosition());
                Point2D midpointTangent = otherModule.getPosition().midpoint(tangentTarget);
                if (midpointTangent.distanceTo(module.getPosition()) > 2) {
                    otherModule.move(midpointTangent.getX(), midpointTangent.getY());
                }
                // Point2D away = directionVector.times(0).plus(modulePosition);
                // module.move(away.getX(), away.getY());
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY1, float partialTicks) {
        Point2D center = rect.center();
        RenderState.blendingOn();
        RenderState.on2D();
        if (selecting) {
            Renderer.drawCenteredString(I18n.format("gui.powersuits.pressKey"), center.getX(), center.getY());
            RenderState.off2D();
            RenderState.blendingOff();
            return;
        }
        newKeybindButton.render(mouseX, mouseY1, partialTicks);
        trashKeybindButton.render(mouseX, mouseY1, partialTicks);
        TextureUtils.pushTexture(TextureUtils.TEXTURE_QUILT);
        Renderer.drawCenteredString(I18n.format("gui.powersuits.keybindInstructions1"), center.getX(), center.getY() + 40);
        Renderer.drawCenteredString(I18n.format("gui.powersuits.keybindInstructions2"), center.getX(), center.getY() + 50);
        Renderer.drawCenteredString(I18n.format("gui.powersuits.keybindInstructions3"), center.getX(), center.getY() + 60);
        Renderer.drawCenteredString(I18n.format("gui.powersuits.keybindInstructions4"), center.getX(), center.getY() + 70);
        if (takenTime + 1000 > System.currentTimeMillis()) {
            Point2D pos = newKeybindButton.getPosition().plus(new Point2D(0, -20));
            Renderer.drawCenteredString(I18n.format("gui.powersuits.keybindTaken"), pos.getX(), pos.getY());
        }
        for (ClickableModule module : modules) {
            module.render(mouseX, mouseY1, partialTicks);
        }
        for (ClickableKeybinding keybind : KeybindManager.getKeybindings()) {
            keybind.render(mouseX, mouseY1, partialTicks);
        }
        if (selectedClickie != null && closestKeybind != null) {
            Renderer.drawLineBetween(selectedClickie, closestKeybind, Colour.YELLOW);
        }
        RenderState.off2D();
        RenderState.blendingOff();
        TextureUtils.popTexture();
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        for (ClickableModule module : modules) {
            if (module.hitBox(x, y)) {
                if (MPSConfig.INSTANCE.doAdditionalInfo()) {
                    return module.getToolTip();
                }
                return Collections.singletonList(module.getLocalizedName(module.getModule()));
            }
        }
        return null;
    }

    public void handleKeyboard() {
        if (selecting) {
            if (Keyboard.getEventKeyState()) {
                int key = Keyboard.getEventKey();
//                if (KeyBinding.HASH.containsItem(key)) {
                if (keyBindingHelper.keyBindingHasKey(key)) {
                    takenTime = System.currentTimeMillis();
                }
//                if (!KeyBinding.HASH.containsItem(key)) {
                if (!keyBindingHelper.keyBindingHasKey(key)) {
                    addKeybind(key, true);
                } else if (MPSConfig.INSTANCE.allowConflictingKeybinds()) {
                    addKeybind(key, false);
                }
                selecting = false;
            }
        }
    }

    private void addKeybind(int key, boolean free) {
        String name;
        try {
            name = Keyboard.getKeyName(key);
        } catch (Exception e) {
            name = "???";
        }
        KeyBinding keybind = new KeyBinding(name, key, KeybindKeyHandler.mps);
        ClickableKeybinding clickie = new ClickableKeybinding(keybind, newKeybindButton.getPosition().plus(new Point2D(0, -20)), free, false);
        KeybindManager.getKeybindings().add(clickie);
    }
}
