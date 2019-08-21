package net.machinemuse.powersuits.client.gui.tinker.module_install;

import net.machinemuse.numina.client.gui.clickable.ClickableModule;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.gui.geometry.MuseRect;
import net.machinemuse.numina.client.gui.geometry.MuseRelativeRect;
import net.machinemuse.numina.client.gui.slot.ClickableModuleSlot;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModuleSelectionSubFrame2 {
    protected List<ClickableModule> moduleButtons;
    protected MuseRelativeRect border;
    protected String category;

    public ModuleSelectionSubFrame2(String category, MuseRelativeRect border) {
        this.category = category;
        this.border = border;
        this.moduleButtons = new ArrayList<>();
    }

    public ClickableModule addModule(ItemStack module, int index) {
        ClickableModule clickie = new ClickableModule(module, new MusePoint2D(0, 0), index);
        this.moduleButtons.add(clickie);
//         refreshButtonPositions();
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
        int col = 0, row = 0;
        for (ClickableModule clickie : moduleButtons) {
            if (col > 4) {
                col = 0;
                row++;
            }
            double x = border.left() + 8 + 16 * col;
            double y = border.top() + 16 + 16 * row;
            clickie.move(x, y);
            col++;
        }
        border.setHeight(28 + 16 * row);
    }

    public MuseRect getBorder() {
        return border;
    }
}