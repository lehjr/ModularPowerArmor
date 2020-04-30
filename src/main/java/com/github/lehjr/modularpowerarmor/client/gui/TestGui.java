package com.github.lehjr.modularpowerarmor.client.gui;

import com.github.lehjr.mpalib.client.gui.ContainerlessGui;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableArrow;
import com.github.lehjr.mpalib.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestGui extends ContainerlessGui {
    DrawableArrow testArrow = null;

    public TestGui(ITextComponent titleIn) {
        super(titleIn);
    }




    @Override
    public void init() {
        super.init();

        System.out.print("init");

        if (testArrow == null) {
            testArrow = new DrawableArrow(
                    this.guiLeft + this.width/4F,
                    this.guiTop + this.height * 0.25F,
                    this.guiLeft + this.width * 0.75F,
                    this.guiTop + this.height * 0.75F,
//                    new Colour(0.545F, 0.545F, 0.545F, 1),
                    new Colour(0xF0100010).withAlpha(1),
//                    new Colour(0x505000FF).withAlpha(1)
                    new Colour(0,191,255)
            );
        }

        testArrow.setDirection(DrawableArrow.ArrowDirection.DOWN);
        testArrow.setDrawShaft(false);
    }

    @Override
    protected void renderTooltip(ItemStack p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
        super.renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_);
    }

    @Override
    public void renderTooltip(String p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
        this.renderTooltip(Arrays.asList(p_renderTooltip_1_), p_renderTooltip_2_, p_renderTooltip_3_);
    }

    @Override
    public void renderTooltip(List<String> p_renderTooltip_1_, int p_renderTooltip_2_, int p_renderTooltip_3_) {
        renderTooltip(p_renderTooltip_1_, p_renderTooltip_2_, p_renderTooltip_3_, font);
    }

//    private int blitOffset;
//
//    @Override
//    public void setBlitOffset(int offset) {
//        this.blitOffset = offset;
//    }
//
//    @Override
//    public int getBlitOffset() {
//        return super.getBlitOffset();
//    }


    DrawableArrow.ArrowDirection direction = DrawableArrow.ArrowDirection.RIGHT;
    boolean drawShaft = false;

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (testArrow != null) {
            DrawableArrow.ArrowDirection[] values = DrawableArrow.ArrowDirection.values().clone();
            if (direction.ordinal() == values.length -1) {
                direction = values[0];
                drawShaft = !drawShaft;
                testArrow.setDrawShaft(drawShaft);
            } else {
                direction = values[direction.ordinal() + 1];
            }
            testArrow.setDirection(direction);
        }

        Colour borderColorStart = new Colour(0x505000FF);
        Colour borderColorEnd = new Colour(((borderColorStart.getInt() & 0xFEFEFE) >> 1 | borderColorStart.getInt() & 0xFF000000));

        System.out.println("borderColourStart: " + "Colour{r=" + borderColorStart.r * 255 + ", g=" + borderColorStart.g * 255 + ", b=" + borderColorStart.b * 255 + ", a=" + borderColorStart.a * 255 + '}');
        System.out.println("borderColourEnd: " + "Colour{r=" + borderColorEnd.r * 255 + ", g=" + borderColorEnd.g * 255 + ", b=" + borderColorEnd.b * 255 + ", a=" + borderColorEnd.a * 255 + '}');
        return super.mouseClicked(x, y, button);
    }

    public static void drawHoveringText(List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        drawHoveringText(ItemStack.EMPTY, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
    }

    /**
     * Use this version if calling from somewhere where ItemStack context is available.
     *
     * @see #drawHoveringText(List, int, int, int, int, int, FontRenderer)
     */
    public static void drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        if (!textLines.isEmpty()) {
            RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
            if (MinecraftForge.EVENT_BUS.post(event))
                return;
            mouseX = event.getX();
            mouseY = event.getY();
            screenWidth = event.getScreenWidth();
            screenHeight = event.getScreenHeight();
            maxTextWidth = event.getMaxWidth();
            font = event.getFontRenderer();

            RenderSystem.disableRescaleNormal();
            RenderSystem.disableDepthTest();
            int tooltipTextWidth = 0;

            for (String textLine : textLines) {
                int textLineWidth = font.getStringWidth(textLine);
                if (textLineWidth > tooltipTextWidth)
                    tooltipTextWidth = textLineWidth;
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (mouseX > screenWidth / 2)
                        tooltipTextWidth = mouseX - 12 - 8;
                    else
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap) {
                int wrappedTooltipWidth = 0;
                List<String> wrappedTextLines = new ArrayList<String>();
                for (int i = 0; i < textLines.size(); i++) {
                    String textLine = textLines.get(i);
                    List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                    if (i == 0)
                        titleLinesCount = wrappedLine.size();

                    for (String line : wrappedLine) {
                        int lineWidth = font.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth)
                            wrappedTooltipWidth = lineWidth;
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2)
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                else
                    tooltipX = mouseX + 12;
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount)
                    tooltipHeight += 2; // gap between title lines and next lines
            }

            if (tooltipY < 4)
                tooltipY = 4;
            else if (tooltipY + tooltipHeight + 4 > screenHeight)
                tooltipY = screenHeight - tooltipHeight - 4;

            final int zLevel = 300;
            Colour backgroundColor = new Colour(0xF0100010);
            Colour borderColorStart = new Colour(0x505000FF);
            Colour borderColorEnd = new Colour(((borderColorStart.getInt() & 0xFEFEFE) >> 1 | borderColorStart.getInt() & 0xFF000000));

//            RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, textLines, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
//            MinecraftForge.EVENT_BUS.post(colorEvent);
//            backgroundColor = colorEvent.getBackground();
//            borderColorStart = colorEvent.getBorderStart();
//            borderColorEnd = colorEvent.getBorderEnd();

            /*

            line 1:
                left: 97, top: 72, right: 172, bottom: 73 (horiz line)
            line 2:
                 left: 97, top: 87, right: 172, bottom: 88 (horiz line)
            line 3:
                left: 97, top: 73, right: 172, bottom: 87 (filled box)
            line 4:
                left: 96, top: 73, right: 97, bottom: 87 (vert line)
            line 5:
                left: 172, top: 73, right: 173, bottom: 87 (vert line)

            ==========================================================
            // B O R D E R    C O D E
            ==========================================================
            line 6:
                left: 97, top: 74, right: 98, bottom: 86 (vert line)
            line 7:
                left: 171, top: 74, right: 172, bottom: 86 (vert line)
            line 8:
                left: 97, top: 73, right: 172, bottom: 74 (horiz line)
            line 9:
                left: 97, top: 86, right: 172, bottom: 87 (horiz line)
            */


            // background
            // top line
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);

            // bottom line
            drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);

            //filled box
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);

//            // left line
            drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);


            // right line
            drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);


//            //border
            // left
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);

            // right
            drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);

            // top
            drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);

            // bottom
            drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);


            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));

            IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            MatrixStack textStack = new MatrixStack();
            textStack.translate(0.0D, 0.0D, (double) zLevel);
            Matrix4f textLocation = textStack.getLast().getMatrix();

            int tooltipTop = tooltipY;

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                String line = textLines.get(lineNumber);
                if (line != null)
                    font.renderString(line, (float) tooltipX, (float) tooltipY, -1, true, textLocation, renderType, false, 0, 15728880);

                if (lineNumber + 1 == titleLinesCount)
                    tooltipY += 2;

                tooltipY += 10;
            }

            renderType.finish();

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

            RenderSystem.enableDepthTest();
            RenderSystem.enableRescaleNormal();
        }
    }




    @Override
    public void renderTooltip(List<String> stringList, int posX, int posY, FontRenderer font) {

        // follow this line here!!! the rest of the code does not run due to "if(false...
        drawHoveringText(stringList, posX, posY, width, height, -1, font);
    }

    public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, Colour startColor, Colour endColor) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right, top, zLevel).color(startColor.r, startColor.g, startColor.b, startColor.a).endVertex();
        buffer.pos(left, top, zLevel).color(startColor.r, startColor.g, startColor.b, startColor.a).endVertex();
        buffer.pos(left, bottom, zLevel).color(endColor.r, endColor.g, endColor.b, endColor.a).endVertex();
        buffer.pos(right, bottom, zLevel).color(endColor.r, endColor.g, endColor.b, endColor.a).endVertex();
        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        if(testArrow != null) {
//            testArrow.setDrawShaft(false);
            testArrow.draw(this.getBlitOffset() + 10);
        }

//        renderTooltip(new ArrayList<String>(){{
//            add("I farted hard.");
//            add("I farted hard.");
//            add("I farted hard.");
//            add("I sharted hard.");
//        }}, this.guiLeft + this.xSize / 2, this.guiTop + this.xSize / 2);

    }

}