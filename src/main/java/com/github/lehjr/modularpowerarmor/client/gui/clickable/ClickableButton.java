///**
// *
// */
//package net.machinemuse.modularpowerarmor.client.gui.tinker.clickable;
//
//import com.github.lehjr.mpalib.client.gui.clickable.Clickable;
//import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
//import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
//import com.github.lehjr.mpalib.client.render.Renderer;
//import com.github.lehjr.mpalib.math.Colour;
//
//import java.util.List;
//
///**
// * @author MachineMuse
// */
//public class ClickableButton extends Clickable {
//    protected String label;
//    protected Point2D radius;
//    protected DrawableRect rect;
//    protected boolean enabled;
//    protected boolean visible = true;
//
//    public ClickableButton(String label, Point2D position, boolean enabled) {
//        this.label = label;
//        this.position = position;
//        this.radius = new Point2D(Renderer.getStringWidth(label) / 2 + 2, 6);
//        this.rect = new DrawableRect(
//                position.getX() - radius.getX(),
//                position.getY() - radius.getY(),
//                position.getX() + radius.getX(),
//                position.getY() + radius.getY(),
//                new Colour(0.5F, 0.6F, 0.8F, 1),
//                new Colour(0.3F, 0.3F, 0.3F, 1)
//        );
//        this.setEnabled(enabled);
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * machinemuse.modularpowerarmor.gui.Clickable#draw(net.minecraft.client.renderer
//     * .RenderEngine, machinemuse.modularpowerarmor.gui.MuseGui)
//     */
//
//    @Override
//    public void render(int i, int i1, float v) {
//        if (visible) {
//            Colour topcolour;
//            Colour bottomcolour;
//            if (isEnabled()) {
//                topcolour = new Colour(0.5F, 0.6F, 0.8F, 1);
//                bottomcolour = new Colour(0.3F, 0.3F, 0.3F, 1);
//            } else {
//                topcolour = new Colour(0.8F, 0.3F, 0.3F, 1);
//                bottomcolour = new Colour(0.8F, 0.6F, 0.6F, 1);
//            }
//            this.rect.setLeft(position.getX() - radius.getX());
//            this.rect.setTop(position.getY() - radius.getY());
//            this.rect.setRight(position.getX() + radius.getX());
//            this.rect.setBottom(position.getY() + radius.getY());
//            this.rect.setBorderColour(topcolour);
//            this.rect.setBackgroundColour(bottomcolour);
//            this.rect.draw();
//            Renderer.drawCenteredString(this.label, position.getX(),
//                    position.getY() - 4);
//        }
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see machinemuse.modularpowerarmor.gui.Clickable#hitBox(int, int,
//     * machinemuse.modularpowerarmor.gui.MuseGui)
//     */
//    @Override
//    public boolean hitBox(double x, double y) {
//        boolean hitx = Math.abs(position.getX() - x) < radius.getX();
//        boolean hity = Math.abs(position.getY() - y) < radius.getY();
//        return hitx && hity;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see machinemuse.modularpowerarmor.gui.Clickable#getToolTip()
//     */
//    @Override
//    public List<String> getToolTip() {
//        return null;
//    }
//
//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
//    }
//
//    public void enable() {
//        this.enabled = true;
//    }
//
//    public void buttonOn() {
//        this.enable();
//        this.show();
//    }
//
//    public void buttonOff() {
//        this.disable();
//        this.hide();
//    }
//
//    public void disable() {
//        this.enabled = false;
//    }
//
//    public void show() {
//        this.visible = true;
//    }
//
//    public void hide() {
//        this.visible = false;
//    }
//
//    public boolean isVisible() {
//        return visible;
//    }
//
//    public ClickableButton setLable(String label) {
//        this.label = label;
//        return this;
//    }
//
//    public String getLabel() {
//        return label;
//    }
//}