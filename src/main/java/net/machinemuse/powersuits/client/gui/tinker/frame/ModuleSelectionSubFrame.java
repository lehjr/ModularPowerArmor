package net.machinemuse.powersuits.client.gui.tinker.frame;

import net.machinemuse.numina.client.gui.clickable.ClickableModule;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.math.geometry.MuseRect;
import net.machinemuse.numina.math.geometry.MuseRelativeRect;

import java.util.ArrayList;
import java.util.List;

public class ModuleSelectionSubFrame {
    protected List<ClickableModule> moduleButtons;
    protected MuseRelativeRect border;
    protected String category;

    public ModuleSelectionSubFrame(String category, MuseRelativeRect border) {
        this.category = category;
        this.border = border;
        this.moduleButtons = new ArrayList<>();
    }

    public ClickableModule addModule(ClickableModule clickie) {
        this.moduleButtons.add(clickie);
        // refreshButtonPositions();
        return clickie;
    }

    public void drawPartial(int min, int max, float partialTicks) {
        refreshButtonPositions();
        double top = border.top();
        MuseRenderer.drawString(this.category, border.left(), top);
        for (ClickableModule clickie : moduleButtons) {
            clickie.render(min, max, partialTicks);
        }
    }

    public void refreshButtonPositions() {
        int i = 0, j = 0;
        for (ClickableModule clickie : moduleButtons) {
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