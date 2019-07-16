//package net.machinemuse.numina.client.gui.clickable;
//
//import net.machinemuse.numina.math.Colour;
//import net.machinemuse.numina.math.geometry.DrawableMuseRect;
//import net.machinemuse.numina.math.geometry.MusePoint2D;
//import net.machinemuse.numina.math.geometry.MuseRect;
//import net.machinemuse.numina.math.geometry.MuseRelativeRect;
//import net.minecraft.client.gui.widget.button.Button;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//import java.util.List;
//
//public class TexturedButton extends DrawableMuseRect implements IClickable {
//    protected boolean enabled = true;
//
//    public TexturedButton(double left, double top, double right, double bottom, boolean growFromMiddle, Colour insideColour, Colour outsideColour) {
//        super(left, top, right, bottom, growFromMiddle, insideColour, outsideColour);
//    }
//
//    public TexturedButton(double left, double top, double right, double bottom, Colour insideColour, Colour outsideColour) {
//        super(left, top, right, bottom, insideColour, outsideColour);
//    }
//
//    public TexturedButton(MusePoint2D ul, MusePoint2D br, Colour insideColour, Colour outsideColour) {
//        super(ul, br, insideColour, outsideColour);
//    }
//
//    public TexturedButton(MuseRect ref, Colour insideColour, Colour outsideColour) {
//        super(ref, insideColour, outsideColour);
//    }
//
//    @Override
//    public void render(int i, int i1, float v) {
//
//    }
//
//    @Override
//    public void move(double x, double y) {
//        // set the center position
//        setLeft(x - width()/2);
//        setRight(y-height()/2);
//    }
//
//    @Override
//    public MusePoint2D getPosition() {
//        return new MusePoint2D(centerx(), centery());
//    }
//
//    @Override
//    public boolean hitBox(double x, double y) {
//        return (x >= this.left() && x <= this.right() &&
//                        y >= this.top() && y <= this.bottom());
//    }
//
//    @Override
//    public List<ITextComponent> getToolTip() {
//        return null;
//    }
//}
