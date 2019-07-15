package net.machinemuse.powersuits.client.gui.tinker.frame;

import net.machinemuse.numina.client.gui.clickable.ClickableButton;
import net.machinemuse.numina.client.gui.frame.IGuiFrame;
import net.machinemuse.numina.math.geometry.MusePoint2D;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 10/19/16.
 */
public class TabSelectFrame implements IGuiFrame {
    PlayerEntity p;
    MusePoint2D topleft;
    MusePoint2D bottomright;
    int worldx;
    int worldy;
    int worldz;
    Map<ClickableButton, Integer> buttons = new HashMap<>();
    List<String> toolTip = new ArrayList<>();

    public TabSelectFrame(PlayerEntity p, MusePoint2D topleft, MusePoint2D bottomright, int worldx, int worldy, int worldz) {
        this.p = p;
        this.topleft = topleft;
        this.bottomright = bottomright;
        this.worldx = worldx;
        this.worldy = worldy;
        this.worldz = worldz;

        this.buttons.put(new ClickableButton(I18n.format("gui.powersuits.tab.tinker"), topleft.midpoint(bottomright).minus(100, 0), worldy < 256 && worldy > 0), 0);
        this.buttons.put(new ClickableButton(I18n.format("gui.powersuits.tab.keybinds"), topleft.midpoint(bottomright), true), 1);
        this.buttons.put(new ClickableButton(I18n.format("gui.powersuits.tab.visual"), topleft.midpoint(bottomright).plus(100, 0), true), 3);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        for (ClickableButton b : buttons.keySet()) {
            if (b.isEnabled() && b.hitBox(x, y)) {
                // fixme: insert openGUI code... add an index value to simulate old behaviour?
//                p.displayGui();//.openGui(ModularPowersuits.getInstance(), buttons.get(b), p.world, worldx, worldy, worldz);
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double v, double v1, int i) {
        return false;
    }

    @Override
    public void update(double mousex, double mousey) {
    }

    @Override
    public boolean onMouseScrolled(double v, double v1, double v2) {
        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        for (ClickableButton b : buttons.keySet())
            b.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        return null;
    }
}
