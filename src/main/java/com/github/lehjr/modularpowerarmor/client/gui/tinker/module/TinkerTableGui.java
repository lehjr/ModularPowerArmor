package com.github.lehjr.modularpowerarmor.client.gui.tinker.module;

import com.github.lehjr.modularpowerarmor.client.gui.common.ItemSelectionFrame;
import com.github.lehjr.modularpowerarmor.client.gui.common.TabSelectFrame;
import com.github.lehjr.mpalib.client.gui.ContainerGui;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.gui.geometry.Point2D;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * The gui class for the TinkerTable block.
 *
 * @author MachineMuse
 */
public class TinkerTableGui extends ContainerGui {
    TinkerTableContainer container;
    protected ItemSelectionFrame itemSelectFrame;
    protected DetailedSummaryFrame summaryFrame;
    protected ModuleSelectionFrame moduleSelectFrame;
    protected InstallSalvageFrame installFrame;
    protected TabSelectFrame tabFrame;
    protected ModuleTweakFrame tweakFrame;
    DrawableRect backgroundRect;

    protected Point2D tweakFrameUL, tweakFrameBR;
    protected int worldx;
    protected int worldy;
    protected int worldz;

    EntityPlayer player;

//    /**
//     * Constructor. Takes a player as an argument.
//     *
//     * @param player
//     */
//    public TinkerTableGui(EntityPlayer player) {
//        this.player = (EntityPlayerSP) player;
//        ScaledResolution screen = new ScaledResolution(Minecraft.getMinecraft());
//        this.xSize = Math.min(screen.getScaledWidth() - 50, 500);
//        this.ySize = Math.min(screen.getScaledHeight() - 50, 300);
//    }


    public TinkerTableGui(InventoryPlayer playerInv, World worldIn) {
        this(playerInv, worldIn, BlockPos.ORIGIN);
    }

    public TinkerTableGui(InventoryPlayer playerInv, World worldIn, BlockPos blockPosition) {
        super(new TinkerTableContainer(playerInv, worldIn, blockPosition));
        this.mc = Minecraft.getMinecraft();
        this.player = mc.player;
        this.container = (TinkerTableContainer)this.inventorySlots;
        rescale();

        backgroundRect = new DrawableRect(absX(-1), absY(-1), absX(1), absY(1), true,
                new Colour(0.0F, 0.2F, 0.0F, 0.8F),
                new Colour(0.1F, 0.9F, 0.1F, 0.8F));


        // setup all frames here, since they are no longer recreated in the initGUI section

        itemSelectFrame = new ItemSelectionFrame(
                container,
                new Point2D(absX(-0.95F), absY(-0.95F)),
                new Point2D(absX(-0.78F), absY(0.95F)),
                Colour.DARKBLUE.withAlpha(0.8F),
                Colour.LIGHTBLUE.withAlpha(0.8F), player);
        frames.add(itemSelectFrame);

        itemSelectFrame.setDoOnNewSelect(doThis-> {
            moduleSelectFrame.loadModules(false);
            this.installFrame.ghostRecipe.clear();
            this.container.craftResult.clear();
            this.container.craftMatrix.clear();
        });

        summaryFrame = new DetailedSummaryFrame(player,
                new Point2D(absX(0f), absY(-0.9f)),
                new Point2D(absX(0.95f), absY(-0.3f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8),
                itemSelectFrame);
        frames.add(summaryFrame);

        moduleSelectFrame = new ModuleSelectionFrame(itemSelectFrame,
                new Point2D(absX(-0.75F), absY(-0.95f)),
                new Point2D(absX(-0.05F), absY(0.75f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8));
        frames.add(moduleSelectFrame);

        installFrame = new InstallSalvageFrame(
                container,
                player,
                new Point2D(absX(-0.75F),
                        absY(0.6f)),
                new Point2D(absX(-0.05F),
                        absY(0.95f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8),
                itemSelectFrame,
                moduleSelectFrame);
        frames.add(installFrame);

        tweakFrame = new ModuleTweakFrame(
                new Point2D(absX(0f), absY(-0.25f)),
                new Point2D(absX(0.95f), absY(0.95f)),
                Colour.DARKBLUE.withAlpha(0.8),
                Colour.LIGHTBLUE.withAlpha(0.8),
                itemSelectFrame,
                moduleSelectFrame);
        frames.add(tweakFrame);

        tabFrame = new TabSelectFrame(player, 0, worldx, worldy, worldz);
        frames.add(tabFrame);
    }

    public void rescale() {
        ScaledResolution screen = new ScaledResolution(Minecraft.getMinecraft());
        this.setXSize(Math.min(screen.getScaledWidth() - 50, 500));
        this.setYSize(Math.min(screen.getScaledHeight() - 50, 300));
    }

    @Override
    public void drawBackground() {
        super.drawDefaultBackground();
        backgroundRect.draw();
    }

    /**
     * Add the buttons (and other controls) to the screen.
     */
    @Override
    public void initGui() {
        super.initGui();
        rescale();
        backgroundRect.setTargetDimensions(absX(-1), absY(-1), absX(1), absY(1));
        itemSelectFrame.init(absX(-0.975F), absY(-0.95F), absX(-0.78F), absY(0.95F));
        summaryFrame.init(absX(-0.025F), absY(-0.95F), absX(0.975f), absY(-0.3f));
        installFrame.init(absX(-0.75F), absY(0.6f), absX(-0.05F), absY(0.95f));
        moduleSelectFrame.init(absX(-0.75F), absY(-0.95f), absX(-0.05F), absY(0.55f));
        moduleSelectFrame.loadModules(true);
        tabFrame.init(absX(-0.95F), absY(-1.05f), absX(0.95F), absY(-0.95f));
        tweakFrame.init(absX(-0.025F),  absY(-0.25f), absX(0.975f), absY(0.95f));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground();
        if (itemSelectFrame.hasNoItems()) {
            double centerx = absX(0);
            double centery = absY(0);
            Renderer.drawCenteredString(I18n.format("gui.modularpowerarmor.noModulesFound.line1"), centerx, centery - 5);
            Renderer.drawCenteredString(I18n.format("gui.modularpowerarmor.noModulesFound.line2"), centerx, centery + 5);
            tabFrame.render(mouseX, mouseY, partialTicks);
        } else {
            super.drawScreen(mouseX, mouseY, partialTicks);
            if (itemSelectFrame.getSelectedItem() != null && moduleSelectFrame.getSelectedModule() != null) {
                installFrame.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
                installFrame.renderGhostRecipeTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
            } else {
                installFrame.ghostRecipe.clear();
            }
            drawToolTip(mouseX, mouseY);
        }
    }
}