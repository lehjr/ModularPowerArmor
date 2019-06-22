package net.machinemuse.powersuits.client.event;

import net.machinemuse.numina.capabilities.inventory.modularitem.ModularItemCapability;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.client.gui.clickable.ClickableModule;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.client.render.MuseTextureUtils;
import net.machinemuse.numina.item.MuseItemUtils;
import net.machinemuse.numina.math.Colour;
import net.machinemuse.numina.math.geometry.DrawableMuseRect;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.MPSItems;
import net.machinemuse.powersuits.client.control.KeybindManager;
import net.machinemuse.powersuits.client.gui.tinker.clickable.ClickableKeybinding;
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

public class RenderEventHandler {
    private static final MPSConfig config = MPSConfig.INSTANCE;
    private static boolean ownFly;
    public static final ResourceLocation binoculars = new ResourceLocation(MPSItems.INSTANCE.BINOCULARS_MODULE__REGNAME);
    public static final ResourceLocation jetpack =  new ResourceLocation(MPSItems.INSTANCE.MODULE_JETPACK__REGNAME);
    public static final ResourceLocation glider = new ResourceLocation(MPSItems.INSTANCE.MODULE_GLIDER__REGNAME);
    public static final ResourceLocation jetBoots = new ResourceLocation(MPSItems.INSTANCE.MODULE_JETBOOTS__REGNAME);
    public static final ResourceLocation flightControl= new ResourceLocation(MPSItems.INSTANCE.MODULE_FLIGHT_CONTROL__REGNAME);


    private final DrawableMuseRect frame = new DrawableMuseRect(config.HUD_KEYBIND_HUD_X.get(), config.HUD_KEYBIND_HUD_Y.get(), config.HUD_KEYBIND_HUD_X.get() + (double) 16, config.HUD_KEYBIND_HUD_Y.get() + (double) 16, true, Colour.DARKGREEN.withAlpha(0.2), Colour.GREEN.withAlpha(0.2));

    public RenderEventHandler() {
        this.ownFly = false;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void preTextureStitch(TextureStitchEvent.Pre event) {
        MuseIcon.registerIcons(event);
        MPSModelHelper.loadArmorModels(event);
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

        player.getItemStackFromSlot(EquipmentSlotType.HEAD).getCapability(ModularItemCapability.MODULAR_ITEM)
                .map(iModularItem -> !iModularItem.itemGetActiveModuleOrEmpty(flightControl).isEmpty()).orElse(false) ||

                player.getItemStackFromSlot(EquipmentSlotType.CHEST).getCapability(ModularItemCapability.MODULAR_ITEM)
                        .map(iModularItem -> !iModularItem.itemGetActiveModuleOrEmpty(jetpack).isEmpty() ||
                                !iModularItem.itemGetActiveModuleOrEmpty(glider).isEmpty()).orElse(false) ||

                player.getItemStackFromSlot(EquipmentSlotType.FEET).getCapability(ModularItemCapability.MODULAR_ITEM)
                        .map(iModularItem -> !iModularItem.itemGetActiveModuleOrEmpty(jetBoots).isEmpty()).orElse(false);
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
        helmet.getCapability(ModularItemCapability.MODULAR_ITEM).ifPresent(h-> {
                    ItemStack binnoculars = h.itemGetActiveModuleOrEmpty(binoculars);
                    if (!binnoculars.isEmpty())
                        e.setNewfov((float) (e.getNewfov() / binnoculars.getCapability(PowerModuleCapability.POWER_MODULE)
                                                        .map(m->m.applyPropertyModifiers(MPSConstants.FOV)).orElse(1D)));
                }
        );
    }

    @OnlyIn(Dist.CLIENT)
    public void drawKeybindToggles() {
        if (config.HUD_DISPLAY_HUD.get()) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientPlayerEntity player = minecraft.player;
            frame.setLeft(config.HUD_KEYBIND_HUD_X.get());
            frame.setTop(config.HUD_KEYBIND_HUD_Y.get());
            frame.setBottom(frame.top() + 16);
            for (ClickableKeybinding kb : KeybindManager.getKeybindings()) {
                if (kb.displayOnHUD) {
                    double stringwidth = MuseRenderer.getStringWidth(kb.getLabel());
                    frame.setWidth(stringwidth + kb.getBoundModules().size() * 16);
                    frame.draw();
                    MuseRenderer.drawString(kb.getLabel(), frame.left() + 1, frame.top() + 3, (kb.toggleval) ? Colour.RED : Colour.GREEN);
                    double x = frame.left() + stringwidth;
                    for (ClickableModule module : kb.getBoundModules()) {
                        MuseTextureUtils.pushTexture(MuseTextureUtils.TEXTURE_QUILT);
                        boolean active = false;
                        for (ItemStack stack : MuseItemUtils.getModularItemsEquipped(player)) {
//                            if (!module.getModule().isEmpty())
//                                active = ModuleManager.INSTANCE.itemHasActiveModule(stack, module.getModule().getItem().getRegistryName());
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