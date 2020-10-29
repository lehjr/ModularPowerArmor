package com.github.lehjr.modularpowerarmor.client.event;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.client.control.KeybindManager;
import com.github.lehjr.modularpowerarmor.client.gui.clickable.ClickableKeybinding;
import com.github.lehjr.modularpowerarmor.client.model.helper.MPAModelHelper;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.util.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.util.client.render.MPALibRenderer;
import com.github.lehjr.mpalib.util.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

public enum RenderEventHandler {
    INSTANCE;
    private static boolean ownFly = false;
    private final DrawableRect frame = new DrawableRect(MPASettings.getHudKeybindX(), MPASettings.getHudKeybindY(), MPASettings.getHudKeybindX() + (float) 16, MPASettings.getHudKeybindY() +  16, true, Colour.DARK_GREEN.withAlpha(0.2F), Colour.GREEN.withAlpha(0.2F));


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void preTextureStitch(TextureStitchEvent.Pre event) {
//        MuseIcon.registerIcons(event);
        MPAModelHelper.loadArmorModels(event, null);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onPostRenderGameOverlayEvent(RenderGameOverlayEvent.Post e) {
        RenderGameOverlayEvent.ElementType elementType = e.getType();
        if (RenderGameOverlayEvent.ElementType.HOTBAR.equals(elementType)) {
            this.drawKeybindToggles(e.getMatrixStack());
        }
    }

//    @OnlyIn(Dist.CLIENT) // was this supposed to do something?!
//    @SubscribeEvent
//    public void renderLast(RenderWorldLastEvent event) {
//        Minecraft minecraft = Minecraft.getInstance();
//        MainWindow screen = minecraft.getMainWindow();
//    }

    @SubscribeEvent
    public void onPreRenderPlayer(RenderPlayerEvent.Pre event) {
        if (!event.getPlayer().abilities.isFlying && !event.getPlayer().isOnGround() && this.playerHasFlightOn(event.getPlayer())) {
            event.getPlayer().abilities.isFlying = true;
            RenderEventHandler.ownFly = true;
        }
    }

    private boolean playerHasFlightOn(PlayerEntity player) {
        return

                player.getItemStackFromSlot(EquipmentSlotType.HEAD).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                        .map(iModularItem ->
                                (iModularItem instanceof IModularItem) && ((IModularItem) iModularItem).isModuleOnline(MPARegistryNames.FLIGHT_CONTROL_MODULE_REGNAME)).orElse(false) ||

                        player.getItemStackFromSlot(EquipmentSlotType.CHEST).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                                .map(iModularItem ->
                                        (iModularItem instanceof IModularItem) &&
                                                ((IModularItem) iModularItem).isModuleOnline(MPARegistryNames.JETPACK_MODULE_REGNAME) ||
                                                ((IModularItem) iModularItem).isModuleOnline(MPARegistryNames.GLIDER_MODULE_REGNAME)).orElse(false) ||

                        player.getItemStackFromSlot(EquipmentSlotType.FEET).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                                .map(iModularItem ->
                                        (iModularItem instanceof IModularItem) && ((IModularItem) iModularItem).isModuleOnline(MPARegistryNames.JETBOOTS_MODULE_REGNAME)).orElse(false);
    }

    @SubscribeEvent
    public void onPostRenderPlayer(RenderPlayerEvent.Post event) {
        if (RenderEventHandler.ownFly) {
            RenderEventHandler.ownFly = false;
            event.getPlayer().abilities.isFlying = false;
        }
    }

    @SubscribeEvent
    public void onFOVUpdate(FOVUpdateEvent e) {
        ItemStack helmet = e.getEntity().getItemStackFromSlot(EquipmentSlotType.HEAD);
        helmet.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h-> {
                    if (h instanceof IModularItem) {
                        ItemStack binnoculars = ((IModularItem) h).getOnlineModuleOrEmpty(MPARegistryNames.BINOCULARS_MODULE_REGNAME);
                        if (!binnoculars.isEmpty())
                            e.setNewfov((float) (e.getNewfov() / binnoculars.getCapability(PowerModuleCapability.POWER_MODULE)
                                    .map(m->m.applyPropertyModifiers(MPAConstants.FOV)).orElse(1D)));
                    }
                }
        );
    }

    @OnlyIn(Dist.CLIENT)
    public void drawKeybindToggles(MatrixStack matrixStack) {
        float zLevel = Minecraft.getInstance().currentScreen != null ? Minecraft.getInstance().currentScreen.getBlitOffset() : 0;

        if (MPASettings.displayHud()) {

            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity player = minecraft.player;
            frame.setLeft(MPASettings.getHudKeybindX());
            frame.setTop(MPASettings.getHudKeybindY());
            frame.setBottom(frame.top() + 16);
            for (ClickableKeybinding kb : KeybindManager.INSTANCE.getKeybindings()) {
                if (kb.displayOnHUD) {
                    float stringwidth = (float) MPALibRenderer.getFontRenderer().getStringPropertyWidth(kb.getLabel());
                    frame.setWidth(stringwidth + kb.getBoundModules().size() * 16);
                    frame.draw(matrixStack, zLevel);
                    MPALibRenderer.drawCenteredText(matrixStack, kb.getLabel(), (float) frame.left() + 1, (float) frame.top() + 3, (kb.toggleval) ? Colour.RED : Colour.GREEN);

                    double x = frame.left() + stringwidth;
                    for (ClickableModule module : kb.getBoundModules()) {
//                        TextureUtils.pushTexture(TextureUtils.TEXTURE_QUILT);
                        boolean active = false;
                        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                            ItemStack stack = player.getItemStackFromSlot(slot);
                            active = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iItemHandler -> {
                                if (iItemHandler instanceof IModularItem) {
                                    return ((IModularItem) iItemHandler).isModuleOnline(module.getModule().getItem().getRegistryName());
                                }
                                return false;
                            }).orElse(false);
                        }

                        MPALibRenderer.drawModuleAt(matrixStack, x, frame.top(), module.getModule(), active);
//                        TextureUtils.popTexture();
                        x += 16;
                    }
                    frame.setTop(frame.top() + 16);
                    frame.setBottom(frame.top() + 16);
                }
            }
        }
    }
}