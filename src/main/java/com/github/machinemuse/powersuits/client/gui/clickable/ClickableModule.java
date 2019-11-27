/*
 * Copyright (c) ${DATE} MachineMuse, Lehjr
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

package com.github.machinemuse.powersuits.client.gui.clickable;

import com.github.lehjr.mpalib.client.gui.GuiIcons;
import com.github.lehjr.mpalib.client.gui.clickable.Clickable;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.render.IconUtils;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.client.render.TextureUtils;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.string.StringUtils;
import net.minecraft.client.resources.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extends the Clickable class to make a clickable Augmentation; note that this
 * will not be an actual item.
 *
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 10/19/16.
 */
public class ClickableModule extends Clickable {
    final Colour checkmarkcolour = new Colour(0.0F, 0.667F, 0.0F, 1.0F);
    boolean allowed = true;
    boolean installed = false;
    IPowerModule module;

    public ClickableModule(IPowerModule module, Point2D position) {
        super(position);
        this.module = module;
    }

    @Override
    public List<String> getToolTip() {
        List<String> toolTipText = new ArrayList<>();
        toolTipText.add(getLocalizedName(getModule()));
        toolTipText.addAll(Arrays.asList(StringUtils.wordUtilsWrap(getLocalizedDescription(getModule()), 30)));
        return toolTipText;
    }

    public String getLocalizedName(IPowerModule m) {
        if (m == null)
            return "";

        return (m.getDataName() != null && !m.getDataName().isEmpty()) ? I18n.format(m.getUnlocalizedName() + ".name") : "broken translation for module name";
    }

    public String getLocalizedDescription(IPowerModule m) {
        if (m == null)
            return "";
        return (m.getDataName() != null && !m.getDataName().isEmpty()) ? I18n.format(m.getUnlocalizedName() + ".desc") : "broken translation for module description";
    }

    @Override
    public void render(int i, int i1, float v) {
        double k = Integer.MAX_VALUE;
        double left = getPosition().getX() - 8;
        double top = getPosition().getY() - 8;
        drawPartial(left, top, left + 16, top + 16);
    }

    // TODO: switch to models in order to handle icons overrriden with resource packs or items with no icons

    public void drawPartial(double xmino, double ymino, double xmaxo, double ymaxo) {
//         IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, world, null);

        double left = getPosition().getX() - 8;
        double top = getPosition().getY() - 8;

        TextureUtils.pushTexture(TextureUtils.TEXTURE_QUILT);
        IconUtils.drawIconAt(left, top, getModule().getIcon(null), Colour.WHITE);
        TextureUtils.popTexture();

        if (!allowed) {
            String string = StringUtils.wrapFormatTags("x", StringUtils.FormatCodes.DarkRed);
            Renderer.drawString(string, getPosition().getX() + 3, getPosition().getY() + 1);
        } else if (installed) {
            new GuiIcons.Checkmark(getPosition().getX() - 8 + 1, getPosition().getY() - 8 + 1, checkmarkcolour, null, null, null, null);
        }
    }

    @Override
    public boolean hitBox(double x, double y) {
        boolean hitx = Math.abs(x - getPosition().getX()) < 8;
        boolean hity = Math.abs(y - getPosition().getY()) < 8;
        return hitx && hity;
    }

    public IPowerModule getModule() {
        return module;
    }

    public boolean equals(ClickableModule other) {
        return this.module == other.getModule();
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }
}