package com.github.lehjr.modularpowerarmor.client.event;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.client.control.KeybindManager;
import com.github.lehjr.modularpowerarmor.client.gui.clickable.ClickableKeybinding;
import com.github.lehjr.modularpowerarmor.client.model.helper.MPSModelHelper;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.math.Colour;
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
    public static final ResourceLocation binoculars = new ResourceLocation(MPARegistryNames.BINOCULARS_MODULE__REGNAME);
    public static final ResourceLocation jetpack =  new ResourceLocation(MPARegistryNames.MODULE_JETPACK__REGNAME);
    public static final ResourceLocation glider = new ResourceLocation(MPARegistryNames.MODULE_GLIDER__REGNAME);
    public static final ResourceLocation jetBoots = new ResourceLocation(MPARegistryNames.MODULE_JETBOOTS__REGNAME);
    public static final ResourceLocation flightControl= new ResourceLocation(MPARegistryNames.MODULE_FLIGHT_CONTROL__REGNAME);
    private final DrawableRect frame = new DrawableRect(MPASettings.getHudKeybindX(), MPASettings.getHudKeybindY(), MPASettings.getHudKeybindX() + (float) 16, MPASettings.getHudKeybindY() +  16, true, Colour.DARKGREEN.withAlpha(0.2F), Colour.GREEN.withAlpha(0.2F));


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void preTextureStitch(TextureStitchEvent.Pre event) {
//        MuseIcon.registerIcons(event);
        MPSModelHelper.loadArmorModels(event, null);
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
            this.drawKeybindToggles();
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
        if (!event.getPlayer().abilities.isFlying && !event.getPlayer().onGround && this.playerHasFlightOn(event.getPlayer())) {
            event.getPlayer().abilities.isFlying = true;
            RenderEventHandler.ownFly = true;
        }
    }

    private boolean playerHasFlightOn(PlayerEntity player) {
        return

                player.getItemStackFromSlot(EquipmentSlotType.HEAD).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                        .map(iModularItem ->
                                (iModularItem instanceof IModularItem) && ((IModularItem) iModularItem).isModuleOnline(flightControl)).orElse(false) ||

                        player.getItemStackFromSlot(EquipmentSlotType.CHEST).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                                .map(iModularItem ->
                                        (iModularItem instanceof IModularItem) &&
                                                ((IModularItem) iModularItem).isModuleOnline(jetpack) ||
                                                ((IModularItem) iModularItem).isModuleOnline(glider)).orElse(false) ||

                        player.getItemStackFromSlot(EquipmentSlotType.FEET).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                                .map(iModularItem ->
                                        (iModularItem instanceof IModularItem) && ((IModularItem) iModularItem).isModuleOnline(jetBoots)).orElse(false);
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
                        ItemStack binnoculars = ((IModularItem) h).getOnlineModuleOrEmpty(binoculars);
                        if (!binnoculars.isEmpty())
                            e.setNewfov((float) (e.getNewfov() / binnoculars.getCapability(PowerModuleCapability.POWER_MODULE)
                                    .map(m->m.applyPropertyModifiers(MPAConstants.FOV)).orElse(1D)));
                    }
                }
        );
    }

    @OnlyIn(Dist.CLIENT)
    public void drawKeybindToggles() {
        float zLevel = Minecraft.getInstance().currentScreen != null ? Minecraft.getInstance().currentScreen.getBlitOffset() : 0;

        if (MPASettings.displayHud()) {

            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity player = minecraft.player;
            frame.setLeft(MPASettings.getHudKeybindX());
            frame.setTop(MPASettings.getHudKeybindY());
            frame.setBottom(frame.top() + 16);
            for (ClickableKeybinding kb : KeybindManager.INSTANCE.getKeybindings()) {
                if (kb.displayOnHUD) {
                    float stringwidth = (float) Renderer.getStringWidth(kb.getLabel().getFormattedText());
                    frame.setWidth(stringwidth + kb.getBoundModules().size() * 16);
                    frame.draw(zLevel);
                    Renderer.drawString(kb.getLabel().getFormattedText(), frame.left() + 1, frame.top() + 3, (kb.toggleval) ? Colour.RED : Colour.GREEN);
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

                        Renderer.drawModuleAt(x, frame.top(), module.getModule(), active);
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