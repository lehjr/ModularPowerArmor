/*
 * ModularPowersuits (Maintenance builds by lehjr)
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.client.gui.tinker.module;

import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.geometry.Rect;
import com.github.lehjr.mpalib.client.gui.geometry.RelativeRect;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.machinemuse.powersuits.client.gui.clickable.ClickableModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleSelectionSubFrame {
    protected List<ClickableModule> moduleButtons;
    protected RelativeRect border;
    protected String category;

    public ModuleSelectionSubFrame(String category, RelativeRect border) {
        this.category = category;
        this.border = border;
        this.moduleButtons = new ArrayList<>();
    }

    public ClickableModule addModule(IPowerModule module) {
        ClickableModule clickie = new ClickableModule(module, new Point2D(0, 0));
        this.moduleButtons.add(clickie);
        // refreshButtonPositions();
        return clickie;
    }

    public void drawPartial(int min, int max) {
        refreshButtonPositions();
        double top = border.top();
        Renderer.drawString(this.category, border.left(), top);
        for (ClickableModule clickie : moduleButtons) {
            clickie.drawPartial(border.left(), min, border.right(), max);
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

    public Rect getBorder() {
        return border;
    }
}
