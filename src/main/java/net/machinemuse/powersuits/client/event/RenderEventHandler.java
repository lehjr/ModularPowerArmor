package net.machinemuse.powersuits.client.event;

import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.client.gui.clickable.ClickableModule;
import net.machinemuse.numina.client.gui.geometry.DrawableMuseRect;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.client.render.MuseTextureUtils;
import net.machinemuse.numina.item.MuseItemUtils;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.MPSRegistryNames;
import net.machinemuse.powersuits.basemod.config.ClientConfig;
import net.machinemuse.powersuits.client.control.KeybindManager;
import net.machinemuse.powersuits.client.gui.clickable.ClickableKeybinding;
import net.machinemuse.powersuits.client.model.helper.MPSModelHelper;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

public enum RenderEventHandler {
    INSTANCE;
    private static boolean ownFly = false;
    public static final ResourceLocation binoculars = new ResourceLocation(MPSRegistryNames.BINOCULARS_MODULE__REGNAME);
    public static final ResourceLocation jetpack =  new ResourceLocation(MPSRegistryNames.MODULE_JETPACK__REGNAME);
    public static final ResourceLocation glider = new ResourceLocation(MPSRegistryNames.MODULE_GLIDER__REGNAME);
    public static final ResourceLocation jetBoots = new ResourceLocation(MPSRegistryNames.MODULE_JETBOOTS__REGNAME);
    public static final ResourceLocation flightControl= new ResourceLocation(MPSRegistryNames.MODULE_FLIGHT_CONTROL__REGNAME);

    private final DrawableMuseRect frame = new DrawableMuseRect(ClientConfig.HUD_KEYBIND_HUD_X.get(), ClientConfig.HUD_KEYBIND_HUD_Y.get(), ClientConfig.HUD_KEYBIND_HUD_X.get() + (double) 16, ClientConfig.HUD_KEYBIND_HUD_Y.get() + (double) 16, true, Colour.DARKGREEN.withAlpha(0.2), Colour.GREEN.withAlpha(0.2));


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void preTextureStitch(TextureStitchEvent.Pre event) {
        MuseIcon.registerIcons(event);
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

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void renderLast(RenderWorldLastEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        MainWindow screen = minecraft.mainWindow;
    }

    @SubscribeEvent
    public void onPreRenderPlayer(RenderPlayerEvent.Pre event) {
        if (!event.getEntityPlayer().abilities.isFlying && !event.getEntityPlayer().onGround && this.playerHasFlightOn(event.getEntityPlayer())) {
            event.getEntityPlayer().abilities.isFlying = true;
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
            event.getEntityPlayer().abilities.isFlying = false;
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
                                    .map(m->m.applyPropertyModifiers(MPSConstants.FOV)).orElse(1D)));
                    }
                }
        );
    }

    @OnlyIn(Dist.CLIENT)
    public void drawKeybindToggles() {
        if (ClientConfig.HUD_DISPLAY_HUD.get()) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity player = minecraft.player;
            frame.setLeft(ClientConfig.HUD_KEYBIND_HUD_X.get());
            frame.setTop(ClientConfig.HUD_KEYBIND_HUD_Y.get());
            frame.setBottom(frame.top() + 16);
            for (ClickableKeybinding kb : KeybindManager.INSTANCE.getKeybindings()) {
                if (kb.displayOnHUD) {
                    double stringwidth = MuseRenderer.getStringWidth(kb.getLabel().getFormattedText());
                    frame.setWidth(stringwidth + kb.getBoundModules().size() * 16);
                    frame.draw();
                    MuseRenderer.drawString(kb.getLabel().getFormattedText(), frame.left() + 1, frame.top() + 3, (kb.toggleval) ? Colour.RED : Colour.GREEN);
                    double x = frame.left() + stringwidth;
                    for (ClickableModule module : kb.getBoundModules()) {
                        MuseTextureUtils.pushTexture(MuseTextureUtils.TEXTURE_QUILT);
                        boolean active = false;
                        for (ItemStack stack : MuseItemUtils.getModularItemsEquipped(player)) {
                            active = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iItemHandler -> {
                                if (iItemHandler instanceof IModularItem) {
                                    return ((IModularItem) iItemHandler).isModuleOnline(module.getModule().getItem().getRegistryName());
                                }
                                return false;
                            }).orElse(false);
                        }
                        MuseRenderer.drawModuleAt(x, frame.top(), module.getModule(), active);
                        MuseTextureUtils.popTexture();
                        x += 16;
                    }
                    frame.setTop(frame.top() + 16);
                    frame.setBottom(frame.top() + 16);
                }
            }
        }
    }
}