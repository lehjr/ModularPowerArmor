package com.github.lehjr.modularpowerarmor.client.gui.common;

import com.github.lehjr.modularpowerarmor.client.gui.keybind.TinkerKeybindGui;
import com.github.lehjr.modularpowerarmor.client.gui.tinker.cosmetic.CosmeticGui;
import com.github.lehjr.modularpowerarmor.client.sound.SoundDictionary;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.network.packets.ContainerGuiOpenPacket;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableButton;
import com.github.lehjr.mpalib.client.gui.frame.ScrollableFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 10/19/16.
 */
public class TabSelectFrame extends ScrollableFrame {
    PlayerEntity player;
    List<ClickableButton> buttons = new ArrayList<>();

    public TabSelectFrame(PlayerEntity player, int exclude) {
        super(new Point2D(0, 0), new Point2D(0, 0), Colour.WHITE.withAlpha(0), Colour.WHITE.withAlpha(0));
        this.player = player;
        ClickableButton button;
        if (exclude != 0) {
            button = new ClickableButton(new TranslationTextComponent("gui.modularpowerarmor.tab.tinker"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                MPAPackets.CHANNEL_INSTANCE.sendToServer(new ContainerGuiOpenPacket(0));
            });
            buttons.add(button);
        }

        if (exclude !=1) {
            button = new ClickableButton(new TranslationTextComponent("gui.modularpowerarmor.tab.keybinds"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                Minecraft.getInstance().enqueue(() -> Minecraft.getInstance().displayGuiScreen(new TinkerKeybindGui(player.inventory, new TranslationTextComponent("gui.tinkertable"))));
            });
            buttons.add(button);
        }

        if (exclude !=2) {
            button = new ClickableButton(new TranslationTextComponent("gui.modularpowerarmor.tab.visual"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                Minecraft.getInstance().enqueue(() -> Minecraft.getInstance().displayGuiScreen(new CosmeticGui(player.inventory, new TranslationTextComponent("gui.tinkertable"))));
            });
            buttons.add(button);
        }

        if (exclude != 3) {
            button = new ClickableButton(new TranslationTextComponent("container.crafting"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, 1);
                MPAPackets.CHANNEL_INSTANCE.sendToServer(new ContainerGuiOpenPacket(1));
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
            button.setPosition(new Point2D(this.left() + x + button.getRadius().getX(), this.top() -6));
            x += Math.abs(spacing) + button.getRadius().getX() * 2;
        }
    }

    @Override
    public Rect setLeft(double value) {
        super.setLeft(value);
        init();
        return this.border;
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