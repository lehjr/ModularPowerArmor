package net.machinemuse.powersuits.client.gui.tinker.common;

import net.machinemuse.numina.client.gui.clickable.ClickableButton;
import net.machinemuse.numina.client.gui.frame.IGuiFrame;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.geometry.MuseRect;
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 10/19/16.
 */
public class TabSelectFrame extends MuseRect implements IGuiFrame {
    PlayerEntity player;
    List<ClickableButton> buttons = new ArrayList<>();

    public TabSelectFrame(PlayerEntity player, int exclude) {
        super(0, 0, 0, 0);
        this.player = player;
        ClickableButton button;
        if (exclude != 0) {
            button = new ClickableButton(I18n.format("gui.powersuits.tab.tinker"), new MusePoint2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                System.out.println("launch tinker table module gui");

                // fixme: add onPressed code here
            });
            buttons.add(button);
        }

        if (exclude !=1) {
            button = new ClickableButton(I18n.format("gui.powersuits.tab.keybinds"), new MusePoint2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                System.out.println("launch tinker table keybind gui");
                // fixme: add onPressed code here
            });
            buttons.add(button);
        }

        if (exclude !=2) {
            button = new ClickableButton(I18n.format("gui.powersuits.tab.visual"), new MusePoint2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);

                System.out.println("launch tinker table cosmetic gui");

                // fixme: add onPressed code here
            });
            buttons.add(button);
        }

        if (exclude != 3) {
            button = new ClickableButton(I18n.format("container.crafting"), new MusePoint2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                System.out.println("launch crafting gui");

                // fixme: add onPressed code here
            });
            buttons.add(button);
        }

        for(ClickableButton b : buttons) {
            b.setVisible(true);
        }
    }

    private void init() {
        double totalButtonWidth = 0;
        for (ClickableButton button : buttons) {
            totalButtonWidth += (button.getRadius().getX() * 2);
        }
        // totalButtonWidth greater than width will produce a negative spacing value
        double spacing = (this.width() - totalButtonWidth) / (buttons.size() +1);

        double x = spacing; // first entry may be negative and will allow an oversized tab frame to be centered
        for (ClickableButton button : buttons) {
            button.setPosition(new MusePoint2D(this.left() + x + button.getRadius().getX(), this.top() -6));
            x += Math.abs(spacing) + button.getRadius().getX() * 2;
        }
    }

    @Override
    public MuseRect setLeft(double value) {
        super.setLeft(value);
        init();
        return this;
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        this.setTargetDimensions(left, top, right, bottom);
        this.init();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (button != 0)
            return false;

        for (ClickableButton b : buttons) {
            if (b.isEnabled() && b.hitBox(x, y)) {
                b.onPressed();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double v, double v1, int i) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double v, double v1, double v2) {
        return false;
    }

    @Override
    public void update(double v, double v1) {

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        for (ClickableButton b : buttons)
            b.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public List<ITextComponent> getToolTip(int i, int i1) {
        return null;
    }
}