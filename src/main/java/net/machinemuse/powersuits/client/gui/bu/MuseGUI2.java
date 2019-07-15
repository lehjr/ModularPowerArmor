//package net.machinemuse.powersuits.client.gui.bu;
//
//import com.google.common.collect.Sets;
//import com.mojang.blaze3d.platform.GlStateManager;
//import net.machinemuse.numina.client.gui.clickable.IClickable;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.IHasContainer;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.util.InputMappings;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.inventory.container.ClickType;
//import net.minecraft.inventory.container.Container;
//import net.minecraft.inventory.container.Slot;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.Util;
//import net.minecraft.util.math.MathHelper;
//import net.minecraft.util.text.TextFormatting;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//import java.util.Set;
//
//@OnlyIn(Dist.CLIENT)
//public class MuseGUI2<T extends Container> extends Screen implements IHasContainer<T> {
//    /** The X size of the inventory window in pixels. */
//    public int xSize = 176;
//    /** The Y size of the inventory window in pixels. */
//    public int ySize = 166;
//    /** A list of the players inventory slots */
//    protected final T container;
//    /** Starting X position for the Gui. Inconsistent use for Gui backgrounds. */
//    public int guiLeft;
//    /** Starting Y position for the Gui. Inconsistent use for Gui backgrounds. */
//    public int guiTop;
//    /** holds the slot currently hovered */
//    public Slot hoveredSlot;
//    /** Used when touchscreen is enabled. */
//    public Slot clickedSlot;
//    /** Used when touchscreen is enabled. */
//    public boolean isRightMouseClick;
//    /** Used when touchscreen is enabled */
//    public ItemStack draggedStack = ItemStack.EMPTY;
//    public int touchUpX;
//    public int touchUpY;
//    public Slot returningStackDestSlot;
//    public long returningStackTime;
//    /** Used when touchscreen is enabled */
//    public ItemStack returningStack = ItemStack.EMPTY;
//    public Slot currentDragTargetSlot;
//    public long dragItemDropDelay;
//    public final Set<Slot> dragSplittingSlots = Sets.<Slot>newHashSet();
//    public boolean dragSplitting;
//    public int dragSplittingLimit;
//    public int dragSplittingButton;
//    public boolean ignoreMouseUp;
//    public int dragSplittingRemnant;
//    public long lastClickTime;
//    public Slot lastClickSlot;
//    public int lastClickButton;
//    public boolean doubleClick;
//    public ItemStack shiftClickedSlot = ItemStack.EMPTY;
//
//    protected final PlayerInventory playerInventory;
//
//
//
//
//
//
//    /*
//    public abstract class ContainerScreen<T extends Container> extends Screen implements IHasContainer<T> {
//   public static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation("textures/gui/container/inventory.png");
//
//
//    public ContainerScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn) {
//        super(titleIn);
//        this.container = screenContainer;
//        this.playerInventory = inv;
//        this.ignoreMouseUp = true;
//    }
//
//    protected void init() {
//        super.init();
//        this.guiLeft = (this.width - this.xSize) / 2;
//        this.guiTop = (this.height - this.ySize) / 2;
//    }
//
//    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
//        int i = this.guiLeft;
//        int j = this.guiTop;
//        this.drawGuiContainerBackgroundLayer(p_render_3_, p_render_1_, p_render_2_);
//        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawBackground(this, p_render_1_, p_render_2_));
//        GlStateManager.disableRescaleNormal();
//        RenderHelper.disableStandardItemLighting();
//        GlStateManager.disableLighting();
//        GlStateManager.disableDepthTest();
//        super.render(p_render_1_, p_render_2_, p_render_3_);
//        RenderHelper.enableGUIStandardItemLighting();
//        GlStateManager.pushMatrix();
//        GlStateManager.translatef((float)i, (float)j, 0.0F);
//        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//        GlStateManager.enableRescaleNormal();
//        this.hoveredSlot = null;
//        int k = 240;
//        int l = 240;
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
//        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//
//        for(int i1 = 0; i1 < this.container.inventorySlots.size(); ++i1) {
//            Slot slot = this.container.inventorySlots.get(i1);
//            if (slot.isEnabled()) {
//                this.drawSlot(slot);
//            }
//
//            if (this.isSlotSelected(slot, (double)p_render_1_, (double)p_render_2_) && slot.isEnabled()) {
//                this.hoveredSlot = slot;
//                GlStateManager.disableLighting();
//                GlStateManager.disableDepthTest();
//                int j1 = slot.xPos;
//                int k1 = slot.yPos;
//                GlStateManager.colorMask(true, true, true, false);
//                int slotColor = this.getSlotColor(i1);
//                this.fillGradient(j1, k1, j1 + 16, k1 + 16, slotColor, slotColor);
//                GlStateManager.colorMask(true, true, true, true);
//                GlStateManager.enableLighting();
//                GlStateManager.enableDepthTest();
//            }
//        }
//
//        RenderHelper.disableStandardItemLighting();
//        this.drawGuiContainerForegroundLayer(p_render_1_, p_render_2_);
//        RenderHelper.enableGUIStandardItemLighting();
//        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawForeground(this, p_render_1_, p_render_2_));
//        PlayerInventory playerinventory = this.minecraft.player.inventory;
//        ItemStack itemstack = this.draggedStack.isEmpty() ? playerinventory.getItemStack() : this.draggedStack;
//        if (!itemstack.isEmpty()) {
//            int j2 = 8;
//            int k2 = this.draggedStack.isEmpty() ? 8 : 16;
//            String s = null;
//            if (!this.draggedStack.isEmpty() && this.isRightMouseClick) {
//                itemstack = itemstack.copy();
//                itemstack.setCount(MathHelper.ceil((float)itemstack.getCount() / 2.0F));
//            } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
//                itemstack = itemstack.copy();
//                itemstack.setCount(this.dragSplittingRemnant);
//                if (itemstack.isEmpty()) {
//                    s = "" + TextFormatting.YELLOW + "0";
//                }
//            }
//
//            this.drawItemStack(itemstack, p_render_1_ - i - 8, p_render_2_ - j - k2, s);
//        }
//
//        if (!this.returningStack.isEmpty()) {
//            float f = (float)(Util.milliTime() - this.returningStackTime) / 100.0F;
//            if (f >= 1.0F) {
//                f = 1.0F;
//                this.returningStack = ItemStack.EMPTY;
//            }
//
//            int l2 = this.returningStackDestSlot.xPos - this.touchUpX;
//            int i3 = this.returningStackDestSlot.yPos - this.touchUpY;
//            int l1 = this.touchUpX + (int)((float)l2 * f);
//            int i2 = this.touchUpY + (int)((float)i3 * f);
//            this.drawItemStack(this.returningStack, l1, i2, (String)null);
//        }
//
//        GlStateManager.popMatrix();
//        GlStateManager.enableLighting();
//        GlStateManager.enableDepthTest();
//        RenderHelper.enableStandardItemLighting();
//    }
//
//    protected void renderHoveredToolTip(int p_191948_1_, int p_191948_2_) {
//        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
//            this.renderTooltip(this.hoveredSlot.getStack(), p_191948_1_, p_191948_2_);
//        }
//
//    }
//
//    /**
//     * Draws an ItemStack.
//     *
//     * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
//     */
//    private void drawItemStack(ItemStack stack, int x, int y, String altText) {
//        GlStateManager.translatef(0.0F, 0.0F, 32.0F);
//        this.blitOffset = 200;
//        this.itemRenderer.zLevel = 200.0F;
//        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
//        if (font == null) font = this.font;
//        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
//        this.itemRenderer.renderItemOverlayIntoGUI(font, stack, x, y - (this.draggedStack.isEmpty() ? 0 : 8), altText);
//        this.blitOffset = 0;
//        this.itemRenderer.zLevel = 0.0F;
//    }
//
//    / **
//            * Draw the foreground layer for the GuiContainer (everything in front of the items)
//     * /
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//    }
//
//    / **
//            * Draws the background layer of this container (behind the items).
//            */
//    protected abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);
//
//    /**
//     * Draws the given slot: any item in it, the slot's background, the hovered highlight, etc.
//     */
//    private void drawSlot(Slot slotIn) {
//        int i = slotIn.xPos;
//        int j = slotIn.yPos;
//        ItemStack itemstack = slotIn.getStack();
//        boolean flag = false;
//        boolean flag1 = slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && !this.isRightMouseClick;
//        ItemStack itemstack1 = this.minecraft.player.inventory.getItemStack();
//        String s = null;
//        if (slotIn == this.clickedSlot && !this.draggedStack.isEmpty() && this.isRightMouseClick && !itemstack.isEmpty()) {
//            itemstack = itemstack.copy();
//            itemstack.setCount(itemstack.getCount() / 2);
//        } else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && !itemstack1.isEmpty()) {
//            if (this.dragSplittingSlots.size() == 1) {
//                return;
//            }
//
//            if (Container.canAddItemToSlot(slotIn, itemstack1, true) && this.container.canDragIntoSlot(slotIn)) {
//                itemstack = itemstack1.copy();
//                flag = true;
//                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, slotIn.getStack().isEmpty() ? 0 : slotIn.getStack().getCount());
//                int k = Math.min(itemstack.getMaxStackSize(), slotIn.getItemStackLimit(itemstack));
//                if (itemstack.getCount() > k) {
//                    s = TextFormatting.YELLOW.toString() + k;
//                    itemstack.setCount(k);
//                }
//            } else {
//                this.dragSplittingSlots.remove(slotIn);
//                this.updateDragSplitting();
//            }
//        }
//
//        this.blitOffset = 100;
//        this.itemRenderer.zLevel = 100.0F;
//        if (itemstack.isEmpty() && slotIn.isEnabled()) {
//            TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();
//            if (textureatlassprite != null) {
//                GlStateManager.disableLighting();
//                this.minecraft.getTextureManager().bindTexture(slotIn.getBackgroundLocation());
//                blit(i, j, this.blitOffset, 16, 16, textureatlassprite);
//                GlStateManager.enableLighting();
//                flag1 = true;
//            }
//        }
//
//        if (!flag1) {
//            if (flag) {
//                fill(i, j, i + 16, j + 16, -2130706433);
//            }
//
//            GlStateManager.enableDepthTest();
//            this.itemRenderer.renderItemAndEffectIntoGUI(this.minecraft.player, itemstack, i, j);
//            this.itemRenderer.renderItemOverlayIntoGUI(this.font, itemstack, i, j, s);
//        }
//
//        this.itemRenderer.zLevel = 0.0F;
//        this.blitOffset = 0;
//    }
//
//    private void updateDragSplitting() {
//        ItemStack itemstack = this.minecraft.player.inventory.getItemStack();
//        if (!itemstack.isEmpty() && this.dragSplitting) {
//            if (this.dragSplittingLimit == 2) {
//                this.dragSplittingRemnant = itemstack.getMaxStackSize();
//            } else {
//                this.dragSplittingRemnant = itemstack.getCount();
//
//                for(Slot slot : this.dragSplittingSlots) {
//                    ItemStack itemstack1 = itemstack.copy();
//                    ItemStack itemstack2 = slot.getStack();
//                    int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
//                    Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);
//                    int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));
//                    if (itemstack1.getCount() > j) {
//                        itemstack1.setCount(j);
//                    }
//
//                    this.dragSplittingRemnant -= itemstack1.getCount() - i;
//                }
//
//            }
//        }
//    }
//
//    private Slot getSelectedSlot(double p_195360_1_, double p_195360_3_) {
//        for(int i = 0; i < this.container.inventorySlots.size(); ++i) {
//            Slot slot = this.container.inventorySlots.get(i);
//            if (this.isSlotSelected(slot, p_195360_1_, p_195360_3_) && slot.isEnabled()) {
//                return slot;
//            }
//        }
//
//        return null;
//    }
//
//    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
//        if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
//            return true;
//        } else {
//            InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrMakeInput(p_mouseClicked_5_);
//            boolean flag = this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey);
//            Slot slot = this.getSelectedSlot(p_mouseClicked_1_, p_mouseClicked_3_);
//            long i = Util.milliTime();
//            this.doubleClick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == p_mouseClicked_5_;
//            this.ignoreMouseUp = false;
//            if (p_mouseClicked_5_ == 0 || p_mouseClicked_5_ == 1 || flag) {
//                int j = this.guiLeft;
//                int k = this.guiTop;
//                boolean flag1 = this.hasClickedOutside(p_mouseClicked_1_, p_mouseClicked_3_, j, k, p_mouseClicked_5_);
//                if (slot != null) flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
//                int l = -1;
//                if (slot != null) {
//                    l = slot.slotNumber;
//                }
//
//                if (flag1) {
//                    l = -999;
//                }
//
//                if (this.minecraft.gameSettings.touchscreen && flag1 && this.minecraft.player.inventory.getItemStack().isEmpty()) {
//                    this.minecraft.displayGuiScreen((Screen)null);
//                    return true;
//                }
//
//                if (l != -1) {
//                    if (this.minecraft.gameSettings.touchscreen) {
//                        if (slot != null && slot.getHasStack()) {
//                            this.clickedSlot = slot;
//                            this.draggedStack = ItemStack.EMPTY;
//                            this.isRightMouseClick = p_mouseClicked_5_ == 1;
//                        } else {
//                            this.clickedSlot = null;
//                        }
//                    } else if (!this.dragSplitting) {
//                        if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
//                            if (this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
//                                this.handleMouseClick(slot, l, p_mouseClicked_5_, ClickType.CLONE);
//                            } else {
//                                boolean flag2 = l != -999 && (InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 344));
//                                ClickType clicktype = ClickType.PICKUP;
//                                if (flag2) {
//                                    this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
//                                    clicktype = ClickType.QUICK_MOVE;
//                                } else if (l == -999) {
//                                    clicktype = ClickType.THROW;
//                                }
//
//                                this.handleMouseClick(slot, l, p_mouseClicked_5_, clicktype);
//                            }
//
//                            this.ignoreMouseUp = true;
//                        } else {
//                            this.dragSplitting = true;
//                            this.dragSplittingButton = p_mouseClicked_5_;
//                            this.dragSplittingSlots.clear();
//                            if (p_mouseClicked_5_ == 0) {
//                                this.dragSplittingLimit = 0;
//                            } else if (p_mouseClicked_5_ == 1) {
//                                this.dragSplittingLimit = 1;
//                            } else if (this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
//                                this.dragSplittingLimit = 2;
//                            }
//                        }
//                    }
//                }
//            }
//
//            this.lastClickSlot = slot;
//            this.lastClickTime = i;
//            this.lastClickButton = p_mouseClicked_5_;
//            return true;
//        }
//    }
//
//    protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
//        return p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
//    }
//
//    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
//        Slot slot = this.getSelectedSlot(p_mouseDragged_1_, p_mouseDragged_3_);
//        ItemStack itemstack = this.minecraft.player.inventory.getItemStack();
//        if (this.clickedSlot != null && this.minecraft.gameSettings.touchscreen) {
//            if (p_mouseDragged_5_ == 0 || p_mouseDragged_5_ == 1) {
//                if (this.draggedStack.isEmpty()) {
//                    if (slot != this.clickedSlot && !this.clickedSlot.getStack().isEmpty()) {
//                        this.draggedStack = this.clickedSlot.getStack().copy();
//                    }
//                } else if (this.draggedStack.getCount() > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false)) {
//                    long i = Util.milliTime();
//                    if (this.currentDragTargetSlot == slot) {
//                        if (i - this.dragItemDropDelay > 500L) {
//                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
//                            this.handleMouseClick(slot, slot.slotNumber, 1, ClickType.PICKUP);
//                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
//                            this.dragItemDropDelay = i + 750L;
//                            this.draggedStack.shrink(1);
//                        }
//                    } else {
//                        this.currentDragTargetSlot = slot;
//                        this.dragItemDropDelay = i;
//                    }
//                }
//            }
//        } else if (this.dragSplitting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.dragSplittingSlots.size() || this.dragSplittingLimit == 2) && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && this.container.canDragIntoSlot(slot)) {
//            this.dragSplittingSlots.add(slot);
//            this.updateDragSplitting();
//        }
//
//        return true;
//    }
//
//    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
//        super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_); //Forge, Call parent to release buttons
//        Slot slot = this.getSelectedSlot(p_mouseReleased_1_, p_mouseReleased_3_);
//        int i = this.guiLeft;
//        int j = this.guiTop;
//        boolean flag = this.hasClickedOutside(p_mouseReleased_1_, p_mouseReleased_3_, i, j, p_mouseReleased_5_);
//        if (slot != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
//        InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrMakeInput(p_mouseReleased_5_);
//        int k = -1;
//        if (slot != null) {
//            k = slot.slotNumber;
//        }
//
//        if (flag) {
//            k = -999;
//        }
//
//        if (this.doubleClick && slot != null && p_mouseReleased_5_ == 0 && this.container.canMergeSlot(ItemStack.EMPTY, slot)) {
//            if (hasShiftDown()) {
//                if (!this.shiftClickedSlot.isEmpty()) {
//                    for(Slot slot2 : this.container.inventorySlots) {
//                        if (slot2 != null && slot2.canTakeStack(this.minecraft.player) && slot2.getHasStack() && slot2.isSameInventory(slot) && Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true)) {
//                            this.handleMouseClick(slot2, slot2.slotNumber, p_mouseReleased_5_, ClickType.QUICK_MOVE);
//                        }
//                    }
//                }
//            } else {
//                this.handleMouseClick(slot, k, p_mouseReleased_5_, ClickType.PICKUP_ALL);
//            }
//
//            this.doubleClick = false;
//            this.lastClickTime = 0L;
//        } else {
//            if (this.dragSplitting && this.dragSplittingButton != p_mouseReleased_5_) {
//                this.dragSplitting = false;
//                this.dragSplittingSlots.clear();
//                this.ignoreMouseUp = true;
//                return true;
//            }
//
//            if (this.ignoreMouseUp) {
//                this.ignoreMouseUp = false;
//                return true;
//            }
//
//            if (this.clickedSlot != null && this.minecraft.gameSettings.touchscreen) {
//                if (p_mouseReleased_5_ == 0 || p_mouseReleased_5_ == 1) {
//                    if (this.draggedStack.isEmpty() && slot != this.clickedSlot) {
//                        this.draggedStack = this.clickedSlot.getStack();
//                    }
//
//                    boolean flag2 = Container.canAddItemToSlot(slot, this.draggedStack, false);
//                    if (k != -1 && !this.draggedStack.isEmpty() && flag2) {
//                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, p_mouseReleased_5_, ClickType.PICKUP);
//                        this.handleMouseClick(slot, k, 0, ClickType.PICKUP);
//                        if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
//                            this.returningStack = ItemStack.EMPTY;
//                        } else {
//                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, p_mouseReleased_5_, ClickType.PICKUP);
//                            this.touchUpX = MathHelper.floor(p_mouseReleased_1_ - (double)i);
//                            this.touchUpY = MathHelper.floor(p_mouseReleased_3_ - (double)j);
//                            this.returningStackDestSlot = this.clickedSlot;
//                            this.returningStack = this.draggedStack;
//                            this.returningStackTime = Util.milliTime();
//                        }
//                    } else if (!this.draggedStack.isEmpty()) {
//                        this.touchUpX = MathHelper.floor(p_mouseReleased_1_ - (double)i);
//                        this.touchUpY = MathHelper.floor(p_mouseReleased_3_ - (double)j);
//                        this.returningStackDestSlot = this.clickedSlot;
//                        this.returningStack = this.draggedStack;
//                        this.returningStackTime = Util.milliTime();
//                    }
//
//                    this.draggedStack = ItemStack.EMPTY;
//                    this.clickedSlot = null;
//                }
//            } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
//                this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
//
//                for(Slot slot1 : this.dragSplittingSlots) {
//                    this.handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
//                }
//
//                this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
//            } else if (!this.minecraft.player.inventory.getItemStack().isEmpty()) {
//                if (this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
//                    this.handleMouseClick(slot, k, p_mouseReleased_5_, ClickType.CLONE);
//                } else {
//                    boolean flag1 = k != -999 && (InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 344));
//                    if (flag1) {
//                        this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
//                    }
//
//                    this.handleMouseClick(slot, k, p_mouseReleased_5_, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
//                }
//            }
//        }
//
//        if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
//            this.lastClickTime = 0L;
//        }
//
//        this.dragSplitting = false;
//        return true;
//    }
//
//    private boolean isSlotSelected(Slot p_195362_1_, double p_195362_2_, double p_195362_4_) {
//        return this.isPointInRegion(p_195362_1_.xPos, p_195362_1_.yPos, 16, 16, p_195362_2_, p_195362_4_);
//    }
//
//    protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {
//        int i = this.guiLeft;
//        int j = this.guiTop;
//        p_195359_5_ = p_195359_5_ - (double)i;
//        p_195359_7_ = p_195359_7_ - (double)j;
//        return p_195359_5_ >= (double)(p_195359_1_ - 1) && p_195359_5_ < (double)(p_195359_1_ + p_195359_3_ + 1) && p_195359_7_ >= (double)(p_195359_2_ - 1) && p_195359_7_ < (double)(p_195359_2_ + p_195359_4_ + 1);
//    }
//
//    /**
//     * Called when the mouse is clicked over a slot or outside the gui.
//     */
//    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
//        if (slotIn != null) {
//            slotId = slotIn.slotNumber;
//        }
//
//        this.minecraft.playerController.windowClick(this.container.windowId, slotId, mouseButton, type, this.minecraft.player);
//    }
//
//    public boolean shouldCloseOnEsc() {
//        return false;
//    }
//
//    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
//        if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
//            return true;
//        } else {
//            InputMappings.Input mouseKey = InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_);
//            if (p_keyPressed_1_ == 256 || this.minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey)) {
//                this.minecraft.player.closeScreen();
//                return true; // Forge MC-146650: Needs to return true when the key is handled.
//            }
//
//            if (this.func_195363_d(p_keyPressed_1_, p_keyPressed_2_))
//                return true; // Forge MC-146650: Needs to return true when the key is handled.
//            if (this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
//                if (this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
//                    this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 0, ClickType.CLONE);
//                    return true; // Forge MC-146650: Needs to return true when the key is handled.
//                } else if (this.minecraft.gameSettings.keyBindDrop.isActiveAndMatches(mouseKey)) {
//                    this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, hasControlDown() ? 1 : 0, ClickType.THROW);
//                    return true; // Forge MC-146650: Needs to return true when the key is handled.
//                }
//            }
//
//            return false; // Forge MC-146650: Needs to return false when the key is not handled.
//        }
//    }
//
//    protected boolean func_195363_d(int p_195363_1_, int p_195363_2_) {
//        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null) {
//            for(int i = 0; i < 9; ++i) {
//                if (this.minecraft.gameSettings.keyBindsHotbar[i].isActiveAndMatches(InputMappings.getInputByCode(p_195363_1_, p_195363_2_))) {
//                    this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, i, ClickType.SWAP);
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//
//    public void removed() {
//        if (this.minecraft.player != null) {
//            this.container.onContainerClosed(this.minecraft.player);
//        }
//    }
//
//    public boolean isPauseScreen() {
//        return false;
//    }
//
//    public void tick() {
//        super.tick();
//        if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
//            this.minecraft.player.closeScreen();
//        }
//
//    }
//
//    public T getContainer() {
//        return this.container;
//    }
//
//    @javax.annotation.Nullable
//    public Slot getSlotUnderMouse() { return this.hoveredSlot; }
//    public int getGuiLeft() { return guiLeft; }
//    public int getGuiTop() { return guiTop; }
//    public int getXSize() { return xSize; }
//    public int getYSize() { return ySize; }
//
//    protected int slotColor = -2130706433;
//    public int getSlotColor(int index) {
//        return slotColor;
//    }
//}
//     */
//
//
//
//
//
//
//
//
//
//// MuseGUI Edits ----------------------------------------------------------------------------------------------------
//protected long creationTime;
//
//protected DrawableMuseRect tooltipRect;
//protected List<IGuiFrame> frames;
////-------------------------------------------------------------------------------------------------------------------
//
//public MuseGUI2(T container, PlayerInventory playerInventory, ITextComponent title) {
//        super(title);
//        this.container = container;
//        this.playerInventory = playerInventory;
//        this.ignoreMouseUp = true;
//
//        //MuseGUI Edits--------------------------------------------------------------------------------------------------
//        frames = new ArrayList();
//        // this.minecraft shouldn't be null, but sometimes it is;
//        this.minecraft = Minecraft.getInstance();
//        this.guiLeft = (this.width - this.xSize) / 2;
//        this.guiTop = (this.height - this.ySize) / 2;
//
//
//        //---------------------------------------------------------------------------------------------------------------
//        }
//
///**
// * This is also called on resize, not just during initialization
// */
//@Override
//protected void init() {
//        super.init();
//        this.guiLeft = (this.width - this.xSize) / 2;
//        this.guiTop = (this.height - this.ySize) / 2;
//
//        //MuseGUI Edits--------------------------------------------------------------------------------------------------
//
////         this.controlList.clear();
//        minecraft.keyboardListener.enableRepeatEvents(true);
//        creationTime = System.currentTimeMillis();
//
//
//        tooltipRect = new DrawableMuseRect(0, 0, 0, 0, false, new Colour(0.2F, 0.6F, 0.9F, 0.7F), new Colour(0.1F, 0.3F, 0.4F, 0.7F));
//
//
//
//
//        //---------------------------------------------------------------------------------------------------------------
//        }
//
////MuseGUI Edits------------------------------------------------------------------------------------------------------
///**
// * Returns absolute screen coordinates (int 0 to width) from a relative
// * coordinate (float -1.0F to +1.0F)
// *
// * @param relx Relative X coordinate
// * @return Absolute X coordinate
// */
//public int absX(double relx) {
//        int absx = (int) ((relx + 1) * xSize / 2);
//        int xpadding = (width - xSize) / 2;
//        return absx + xpadding;
//        }
//
///**
// * Returns relative coordinate (float -1.0F to +1.0F) from absolute
// * coordinates (int 0 to width)
// */
//public int relX(double absx) {
//        int padding = (width - xSize) / 2;
//        return (int) ((absx - padding) * 2 / xSize - 1);
//        }
//
///**
// * Returns absolute screen coordinates (int 0 to width) from a relative
// * coordinate (float -1.0F to +1.0F)
// *
// * @param rely Relative Y coordinate
// * @return Absolute Y coordinate
// */
//public int absY(double rely) {
//        int absy = (int) ((rely + 1) * ySize / 2);
//        int ypadding = (height - ySize) / 2;
//        return absy + ypadding;
//        }
//
///**
// * Returns relative coordinate (float -1.0F to +1.0F) from absolute
// * coordinates (int 0 to width)
// */
//public int relY(float absy) {
//        int padding = (height - ySize) / 2;
//        return (int) ((absy - padding) * 2 / ySize - 1);
//        }
//
///**
// * Adds a frame to this gui's draw list.
// *
// * @param frame
// */
//public void addFrame(IGuiFrame frame) {
//        frames.add(frame);
//        }
//
///**
// * Draws all clickables in a list
// */
//public void drawClickables(List<? extends IClickable> list, int mouseX, int mouseY, float partialTicks) {
//        if (list == null) {
//        return;
//        }
//        for (IClickable clickie : list) {
//        clickie.render(mouseX, mouseY, partialTicks);
//        }
//        }
//
//@Override
//public void renderBackground() {
//        super.renderBackground();
//        }
//
//protected void drawToolTip(int mouseX, int mouseY) {
//        List<ITextComponent> tooltip = getToolTip(mouseX, mouseY);
//        if (tooltip != null) {
//        double strwidth = 0;
//        for (ITextComponent s : tooltip) {
//        double currstrwidth = MuseRenderer.getStringWidth(s.getString());
//        if (currstrwidth > strwidth) {
//        strwidth = currstrwidth;
//        }
//        }
//        double top, bottom, left, right;
//        if (mouseY > this.height / 2) {
//        top = mouseY - 10 * tooltip.size() - 8;
//        bottom = mouseY;
//        left = mouseX;
//        right = mouseX + 8 + strwidth;
//        } else {
//        top = mouseY;
//        bottom = mouseY + 10 * tooltip.size() + 8;
//
//        left = mouseX + 4;
//        right = mouseX + 12 + strwidth;
//        }
//
//        tooltipRect.setTop(top);
//        tooltipRect.setLeft(left);
//        tooltipRect.setRight(right);
//        tooltipRect.setBottom(bottom);
//        tooltipRect.draw();
//        for (int i = 0; i < tooltip.size(); i++) {
//        MuseRenderer.drawString(tooltip.get(i).getString(), left + 4, bottom - 10 * (tooltip.size() - i) - 4);
//        }
//        }
//        }
//
///**
// * @return
// */
//public List<ITextComponent> getToolTip(int x, int y) {
//        List<ITextComponent> hitTip;
//        for (IGuiFrame frame : frames) {
//        hitTip = frame.getToolTip(x, y);
//        if (hitTip != null) {
//        return hitTip;
//        }
//        }
//        return null;
//        }
//
////------------------------------------------------------------------------------------------------------------------
//
//@Override
//public void render(int mouseX, int mouseY, float partialTicks) {
//        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
//        GlStateManager.disableRescaleNormal();
//        RenderHelper.disableStandardItemLighting();
//        GlStateManager.disableLighting();
//        GlStateManager.disableDepthTest();
//        super.render(mouseX, mouseY, partialTicks);
//        RenderHelper.enableGUIStandardItemLighting();
//        GlStateManager.pushMatrix();
//        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//        GlStateManager.enableRescaleNormal();
//        this.hoveredSlot = null;
//        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
//        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//
//        // Fixme: copied from original museGUI, not sure if this is the best place for this or not
//        renderBackground();
//        for (IGuiFrame frame : frames) {
//        frame.render(mouseX, mouseY, partialTicks);
//        }
//        drawToolTip(mouseX, mouseY);
//        RenderHelper.disableStandardItemLighting();
//        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
//        RenderHelper.enableGUIStandardItemLighting();
//        GlStateManager.popMatrix();
//        GlStateManager.enableLighting();
//        GlStateManager.enableDepthTest();
//        RenderHelper.enableStandardItemLighting();
//        }
//
///**
// * Draw the foreground layer for the GuiContainer (everything in front of the items)
// */
//protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//
//        }
//
///**
// * Draws the background layer of this container (behind the items).
// */
//protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//
//        }
//
//private void updateDragSplitting() {
//        ItemStack itemstack = this.minecraft.player.inventory.getItemStack();
//        if (!itemstack.isEmpty() && this.dragSplitting) {
//        if (this.dragSplittingLimit == 2) {
//        this.dragSplittingRemnant = itemstack.getMaxStackSize();
//        } else {
//        this.dragSplittingRemnant = itemstack.getCount();
//
//        for(Slot slot : this.dragSplittingSlots) {
//        ItemStack itemstack1 = itemstack.copy();
//        ItemStack itemstack2 = slot.getStack();
//        int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
//        Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);
//        int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));
//        if (itemstack1.getCount() > j) {
//        itemstack1.setCount(j);
//        }
//
//        this.dragSplittingRemnant -= itemstack1.getCount() - i;
//        }
//
//        }
//        }
//        }
//
//private Slot getSelectedSlot(double p_195360_1_, double p_195360_3_) {
//        for(int i = 0; i < this.container.inventorySlots.size(); ++i) {
//        Slot slot = this.container.inventorySlots.get(i);
//        if (this.isSlotSelected(slot, p_195360_1_, p_195360_3_) && slot.isEnabled()) {
//        return slot;
//        }
//        }
//
//        return null;
//        }
//
//@Override
//public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
//        if (super.mouseScrolled(mouseX, mouseY, dWheel))
//        return true;
//        for (IGuiFrame frame : frames) {
//        if (frame.onMouseScrolled(mouseX, mouseY, dWheel))
//        return true;
//        }
//
//        return false;
//        }
//
//@Override
//public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        if (super.mouseClicked(mouseX, mouseY, button)) {
//        return true;
//        } else {
//        for (IGuiFrame frame : frames) {
//        if (frame.mouseClicked(mouseX, mouseY, button))
//        return true;
//        }
//        }
//
//
//
////            InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrMakeInput(button);
////            boolean flag = this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey);
////            Slot slot = this.getSelectedSlot(mouseX, mouseY);
////            long i = Util.milliTime();
////            this.doubleClick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == button;
////            this.ignoreMouseUp = false;
////            if (button == 0 || button == 1 || flag) {
////                int j = this.guiLeft;
////                int k = this.guiTop;
////                boolean flag1 = this.hasClickedOutside(mouseX, mouseY, j, k, button);
////                if (slot != null) flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
////                int l = -1;
////                if (slot != null) {
////                    l = slot.slotNumber;
////                }
////
////                if (flag1) {
////                    l = -999;
////                }
////
////                if (this.minecraft.gameSettings.touchscreen && flag1 && this.minecraft.player.inventory.getItemStack().isEmpty()) {
////                    this.minecraft.displayGuiScreen((Screen)null);
////                    return true;
////                }
////
////                if (l != -1) {
////                    if (this.minecraft.gameSettings.touchscreen) {
////                        if (slot != null && slot.getHasStack()) {
////                            this.clickedSlot = slot;
////                            this.draggedStack = ItemStack.EMPTY;
////                            this.isRightMouseClick = button == 1;
////                        } else {
////                            this.clickedSlot = null;
////                        }
////                    } else if (!this.dragSplitting) {
////                        if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
////                            if (this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
////                                this.handleMouseClick(slot, l, button, ClickType.CLONE);
////                            } else {
////                                boolean flag2 = l != -999 && (InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 344));
////                                ClickType clicktype = ClickType.PICKUP;
////                                if (flag2) {
////                                    this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
////                                    clicktype = ClickType.QUICK_MOVE;
////                                } else if (l == -999) {
////                                    clicktype = ClickType.THROW;
////                                }
////
////                                this.handleMouseClick(slot, l, button, clicktype);
////                            }
////
////                            this.ignoreMouseUp = true;
////                        } else {
////                            this.dragSplitting = true;
////                            this.dragSplittingButton = button;
////                            this.dragSplittingSlots.clear();
////                            if (button == 0) {
////                                this.dragSplittingLimit = 0;
////                            } else if (button == 1) {
////                                this.dragSplittingLimit = 1;
////                            } else if (this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
////                                this.dragSplittingLimit = 2;
////                            }
////                        }
////                    }
////                }
////            }
////
////            this.lastClickSlot = slot;
////            this.lastClickTime = i;
////            this.lastClickButton = button;
//        return true;
//        }
//
//protected boolean hasClickedOutside(double mouseX, double mouseY, int p_195361_5_, int p_195361_6_, int button) {
//        return mouseX < (double)p_195361_5_ || mouseY < (double)p_195361_6_ || mouseX >= (double)(p_195361_5_ + this.xSize) || mouseY >= (double)(p_195361_6_ + this.ySize);
//        }
//
//public boolean mouseDragged(double mouseX, double mouseY, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
//        Slot slot = this.getSelectedSlot(mouseX, mouseY);
//        ItemStack itemstack = this.minecraft.player.inventory.getItemStack();
//        if (this.clickedSlot != null && this.minecraft.gameSettings.touchscreen) {
//        if (p_mouseDragged_5_ == 0 || p_mouseDragged_5_ == 1) {
//        if (this.draggedStack.isEmpty()) {
//        if (slot != this.clickedSlot && !this.clickedSlot.getStack().isEmpty()) {
//        this.draggedStack = this.clickedSlot.getStack().copy();
//        }
//        } else if (this.draggedStack.getCount() > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false)) {
//        long i = Util.milliTime();
//        if (this.currentDragTargetSlot == slot) {
//        if (i - this.dragItemDropDelay > 500L) {
//        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
//        this.handleMouseClick(slot, slot.slotNumber, 1, ClickType.PICKUP);
//        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
//        this.dragItemDropDelay = i + 750L;
//        this.draggedStack.shrink(1);
//        }
//        } else {
//        this.currentDragTargetSlot = slot;
//        this.dragItemDropDelay = i;
//        }
//        }
//        }
//        } else if (this.dragSplitting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.dragSplittingSlots.size() || this.dragSplittingLimit == 2) && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && this.container.canDragIntoSlot(slot)) {
//        this.dragSplittingSlots.add(slot);
//        this.updateDragSplitting();
//        }
//
//        return true;
//        }
//
///**
// * Called when the mouse is moved or a mouse button is released. Signature:
// * (mouseX, mouseY, which) which==-1 is mouseMove, which==0 or which==1 is
// * mouseUp
// */
//public boolean mouseReleased(double mouseX, double mouseY, int which) {
//        if (super.mouseReleased(mouseX, mouseY, which)) //Forge, Call parent to release buttons
//        return true;
//        for (IGuiFrame frame : frames) {
//        frame.mouseReleased(mouseX, mouseY, which);
//        }
//        return true;
//
////        Slot slot = this.getSelectedSlot(mouseX, mouseY);
////        int i = this.guiLeft;
////        int j = this.guiTop;
////        boolean flag = this.hasClickedOutside(mouseX, mouseY, i, j, which);
////        if (slot != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
////        InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrMakeInput(which);
////        int k = -1;
////        if (slot != null) {
////            k = slot.slotNumber;
////        }
////
////        if (flag) {
////            k = -999;
////        }
////
////        if (this.doubleClick && slot != null && which == 0 && this.container.canMergeSlot(ItemStack.EMPTY, slot)) {
////            if (hasShiftDown()) {
////                if (!this.shiftClickedSlot.isEmpty()) {
////                    for(Slot slot2 : this.container.inventorySlots) {
////                        if (slot2 != null && slot2.canTakeStack(this.minecraft.player) && slot2.getHasStack() && slot2.isSameInventory(slot) && Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true)) {
////                            this.handleMouseClick(slot2, slot2.slotNumber, which, ClickType.QUICK_MOVE);
////                        }
////                    }
////                }
////            } else {
////                this.handleMouseClick(slot, k, which, ClickType.PICKUP_ALL);
////            }
////
////            this.doubleClick = false;
////            this.lastClickTime = 0L;
////        } else {
////            if (this.dragSplitting && this.dragSplittingButton != which) {
////                this.dragSplitting = false;
////                this.dragSplittingSlots.clear();
////                this.ignoreMouseUp = true;
////                return true;
////            }
////
////            if (this.ignoreMouseUp) {
////                this.ignoreMouseUp = false;
////                return true;
////            }
////
////            if (this.clickedSlot != null && this.minecraft.gameSettings.touchscreen) {
////                if (which == 0 || which == 1) {
////                    if (this.draggedStack.isEmpty() && slot != this.clickedSlot) {
////                        this.draggedStack = this.clickedSlot.getStack();
////                    }
////
////                    boolean flag2 = Container.canAddItemToSlot(slot, this.draggedStack, false);
////                    if (k != -1 && !this.draggedStack.isEmpty() && flag2) {
////                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, which, ClickType.PICKUP);
////                        this.handleMouseClick(slot, k, 0, ClickType.PICKUP);
////                        if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
////                            this.returningStack = ItemStack.EMPTY;
////                        } else {
////                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, which, ClickType.PICKUP);
////                            this.touchUpX = MathHelper.floor(mouseX - (double)i);
////                            this.touchUpY = MathHelper.floor(mouseY - (double)j);
////                            this.returningStackDestSlot = this.clickedSlot;
////                            this.returningStack = this.draggedStack;
////                            this.returningStackTime = Util.milliTime();
////                        }
////                    } else if (!this.draggedStack.isEmpty()) {
////                        this.touchUpX = MathHelper.floor(mouseX - (double)i);
////                        this.touchUpY = MathHelper.floor(mouseY - (double)j);
////                        this.returningStackDestSlot = this.clickedSlot;
////                        this.returningStack = this.draggedStack;
////                        this.returningStackTime = Util.milliTime();
////                    }
////
////                    this.draggedStack = ItemStack.EMPTY;
////                    this.clickedSlot = null;
////                }
////            } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
////                this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
////
////                for(Slot slot1 : this.dragSplittingSlots) {
////                    this.handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
////                }
////
////                this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
////            } else if (!this.minecraft.player.inventory.getItemStack().isEmpty()) {
////                if (this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
////                    this.handleMouseClick(slot, k, which, ClickType.CLONE);
////                } else {
////                    boolean flag1 = k != -999 && (InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), 344));
////                    if (flag1) {
////                        this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
////                    }
////
////                    this.handleMouseClick(slot, k, which, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
////                }
////            }
////        }
////
////        if (this.minecraft.player.inventory.getItemStack().isEmpty()) {
////            this.lastClickTime = 0L;
////        }
////
////        this.dragSplitting = false;
////        return true;
//        }
//
////
//private boolean isSlotSelected(Slot slot, double p_195362_2_, double p_195362_4_) {
//        return this.isPointInRegion(slot.xPos, slot.yPos, 16, 16, p_195362_2_, p_195362_4_);
//        }
//
//protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {
//        int i = this.guiLeft;
//        int j = this.guiTop;
//        p_195359_5_ = p_195359_5_ - (double)i;
//        p_195359_7_ = p_195359_7_ - (double)j;
//        return p_195359_5_ >= (double)(p_195359_1_ - 1) && p_195359_5_ < (double)(p_195359_1_ + p_195359_3_ + 1) && p_195359_7_ >= (double)(p_195359_2_ - 1) && p_195359_7_ < (double)(p_195359_2_ + p_195359_4_ + 1);
//        }
//
///**
// * Called when the mouse is clicked over a slot or outside the gui.
// */
//protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
//        System.out.println("doing something here");
//
//
//        if (slotIn != null) {
//        slotId = slotIn.slotNumber;
//        }
//
//        this.minecraft.playerController.windowClick(this.container.windowId, slotId, mouseButton, type, this.minecraft.player);
//        }
//
//@Override
//public boolean keyPressed(int keyCode, int scanCode, int modifier) {
//        if (super.keyPressed(keyCode, scanCode, modifier)) {
//        return true;
//        } else {
//        InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
//        if (keyCode == 256 || this.minecraft.gameSettings.keyBindInventory.isActiveAndMatches(mouseKey)) {
//        this.minecraft.player.closeScreen();
//        return true; // Forge MC-146650: Needs to return true when the key is handled.
//        }
//
//        if (this.func_195363_d(keyCode, scanCode))
//        return true; // Forge MC-146650: Needs to return true when the key is handled.
//        if (this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
//        if (this.minecraft.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseKey)) {
//        this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 0, ClickType.CLONE);
//        return true; // Forge MC-146650: Needs to return true when the key is handled.
//        } else if (this.minecraft.gameSettings.keyBindDrop.isActiveAndMatches(mouseKey)) {
//        this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, hasControlDown() ? 1 : 0, ClickType.THROW);
//        return true; // Forge MC-146650: Needs to return true when the key is handled.
//        }
//        }
//
//        return false; // Forge MC-146650: Needs to return false when the key is not handled.
//        }
//        }
//
//protected boolean func_195363_d(int keyCode, int scanCode) {
//        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null) {
//        for(int i = 0; i < 9; ++i) {
//        if (this.minecraft.gameSettings.keyBindsHotbar[i].isActiveAndMatches(InputMappings.getInputByCode(keyCode, scanCode))) {
//        this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, i, ClickType.SWAP);
//        return true;
//        }
//        }
//        }
//        return false;
//        }
//
//@Override
//public void removed() {
//        if (this.minecraft.player != null) {
//        this.container.onContainerClosed(this.minecraft.player);
//        }
//        }
//
//@Override
//public boolean isPauseScreen() {
//        return false;
//        }
//
//@Override
//public boolean shouldCloseOnEsc() {
//        return false;
//        }
//
//@Override
//public void tick() {
//        super.tick();
//        if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
//        this.minecraft.player.closeScreen();
//        }
//
//        double x = minecraft.mouseHelper.getMouseX() * this.width / (double) this.minecraft.mainWindow.getWidth();
//        double y = this.height - minecraft.mouseHelper.getMouseY() * this.height / (double) this.minecraft.mainWindow.getHeight() - 1;
//        for (IGuiFrame frame : frames) {
//        frame.update(x, y);
//        }
//        }
//
//@Override
//public T getContainer() {
//        return this.container;
//        }
//
//@javax.annotation.Nullable
//public Slot getSlotUnderMouse() { return this.hoveredSlot; }
//public int getGuiLeft() { return guiLeft; }
//public int getGuiTop() { return guiTop; }
//public int getXSize() { return xSize; }
//public int getYSize() { return ySize; }
//public int getGuiRight() { return guiLeft + xSize; }
//public int getGuibottom() { return guiTop + ySize; }
//        }