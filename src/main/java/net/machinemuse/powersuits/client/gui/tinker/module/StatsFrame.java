package net.machinemuse.powersuits.client.gui.tinker.module;

import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.gui.scrollable.ScrollableFrame;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import com.github.lehjr.mpalib.nbt.NBTTagAccessor;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Set;

public class StatsFrame extends ScrollableFrame {
    protected NBTTagCompound properties;
    protected ItemStack stack;
    protected Set<String> propertiesToList;

    public StatsFrame(Point2D topleft, Point2D bottomright,
                      Colour borderColour, Colour insideColour, ItemStack stack) {
        super(topleft, bottomright, borderColour, insideColour);
        this.stack = stack;
        this.properties = NBTUtils.getMuseItemTag(stack);
        this.propertiesToList = NBTTagAccessor.getMap(properties).keySet();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushMatrix();
        super.render(mouseX, mouseY, partialTicks);
        int xoffset = 8;
        int yoffset = 8;
        int i = 0;
        for (String propName : propertiesToList) {
            double propValue = NBTUtils.getDoubleOrZero(properties, propName);
            String propValueString = String.format("%.2f", propValue);
            double strlen = Renderer.getStringWidth(propValueString);
            Renderer.drawString(propName, border.left() + xoffset, border.top() + yoffset + i * 10);
            Renderer.drawString(propValueString, border.bottom() - xoffset - strlen - 40, border.top() + yoffset + i * 10);
            i++;
        }
        GL11.glPopMatrix();
    }

    @Override
    public void onMouseDown(double x, double y, int button) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onMouseUp(double x, double y, int button) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<String> getToolTip(int x, int y) {
        // TODO Auto-generated method stub
        return null;
    }
}
