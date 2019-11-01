package net.machinemuse.powersuits.client.gui.common;

import com.github.lehjr.mpalib.client.gui.clickable.ClickableButton;
import com.github.lehjr.mpalib.client.gui.frame.IGuiFrame;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.sound.Musique;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.common.ModularPowersuits;
import net.machinemuse.powersuits.network.MPSPackets;
import net.machinemuse.powersuits.network.packets.CraftingGuiServerSidePacket;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 10/19/16.
 */
public class TabSelectFrame extends Rect implements IGuiFrame {
    EntityPlayer player;

    int worldx;
    int worldy;
    int worldz;

    List<ClickableButton> buttons = new ArrayList<>();

    public TabSelectFrame(EntityPlayer player, int exclude, int worldx, int worldy, int worldz) {
        super(0, 0, 0, 0);
        this.player = player;

        this.worldx = worldx;
        this.worldy = worldy;
        this.worldz = worldz;

        BlockPos pos = new BlockPos(worldx, worldy, worldz);

        ClickableButton button;
        if (exclude != 0) {
            button = new ClickableButton(I18n.format("gui.powersuits.tab.tinker"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, pos);
                player.openGui(ModularPowersuits.getInstance(), 0, player.world, worldx, worldy, worldz);
            });
            buttons.add(button);
        }

        if (exclude !=1) {
            button = new ClickableButton(I18n.format("gui.powersuits.tab.keybinds"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, pos);
                player.openGui(ModularPowersuits.getInstance(), 1, player.world, worldx, worldy, worldz);
            });
            buttons.add(button);
        }

        if (exclude !=2) {
            button = new ClickableButton(I18n.format("gui.powersuits.tab.visual"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, pos);
                player.openGui(ModularPowersuits.getInstance(), 2, player.world, worldx, worldy, worldz);
            });
            buttons.add(button);
        }

        if (exclude != 3) {
            button = new ClickableButton(I18n.format("container.crafting"), new Point2D(0, 0), true);
            button.setOnPressed(onPressed->{
                MPSPackets.sendToServer(new CraftingGuiServerSidePacket());

                Musique.playClientSound(SoundDictionary.SOUND_EVENT_GUI_SELECT, SoundCategory.MASTER, 1, pos);
                player.openGui(ModularPowersuits.getInstance(), 3, player.world, worldx, worldy, worldz);
            });
            buttons.add(button);
        }

        for(ClickableButton b : buttons) {
            b.setVisible(true);
        }
    }

    @Override
    public void init(double left, double top, double right, double bottom) {
        this.setTargetDimensions(left, top, right, bottom);
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
    public void onMouseDown(double mouseX, double mouseY, int button) {
        if (button != 0)
            return;

        for (ClickableButton b : buttons) {
            if (b.isEnabled() && b.hitBox(mouseX, mouseY)) {
                b.onPressed();
                return;
            }
        }
    }

    @Override
    public void onMouseUp(double mouseX, double mouseY, int button) {
    }

    @Override
    public void update(double mousex, double mousey) {
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        for (ClickableButton b : buttons) {
            b.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        return null;
    }
}
