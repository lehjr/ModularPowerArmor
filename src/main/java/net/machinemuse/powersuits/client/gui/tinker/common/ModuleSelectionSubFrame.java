package net.machinemuse.powersuits.client.gui.tinker.common;

import net.machinemuse.numina.client.gui.slot.ClickableModuleSlot;
import net.machinemuse.numina.client.gui.geometry.MuseRect;
import net.machinemuse.numina.client.gui.geometry.MuseRelativeRect;
import net.machinemuse.numina.client.render.MuseRenderer;

import java.util.ArrayList;
import java.util.List;

public class ModuleSelectionSubFrame {
    protected List<ClickableModuleSlot> moduleButtons;
    protected MuseRelativeRect border;
    protected String category;

    public ModuleSelectionSubFrame(String category, MuseRelativeRect border) {
        this.category = category;
        this.border = border;
        this.moduleButtons = new ArrayList<>();
    }

    public ClickableModuleSlot addModule(ClickableModuleSlot clickie) {
        this.moduleButtons.add(clickie);
        // refreshButtonPositions();
        return clickie;
    }

    public void drawPartial(int min, int max, float partialTicks) {
        refreshButtonPositions();
        double top = border.top();
        MuseRenderer.drawString(this.category, border.left(), top);
        for (ClickableModuleSlot clickie : moduleButtons) {
            clickie.render(min, max, partialTicks);
        }
    }

    public void refreshButtonPositions() {
        int i = 0, j = 0;
        for (ClickableModuleSlot clickie : moduleButtons) {
            if (i > 4) {
                i = 0;
                j++;
            }
            double x = border.left() + 8 + 16 * i;
            double y = border.top() + 16 + 16 * j;
            clickie.move(x, y);
            i++;
        }
        border.setHeight(28 + 16 * j);
    }

    public MuseRect getBorder() {
        return border;
    }
}