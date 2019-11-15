package com.github.lehjr.modularpowerarmor.client.event;

import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
import com.github.lehjr.modularpowerarmor.client.control.KeybindManager;
import com.github.lehjr.modularpowerarmor.client.gui.clickable.ClickableKeybinding;
import com.github.lehjr.modularpowerarmor.client.model.helper.MPSModelHelper;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.client.gui.clickable.ClickableModule;
import com.github.lehjr.mpalib.client.gui.geometry.DrawableRect;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.client.render.TextureUtils;
import com.github.lehjr.mpalib.math.Colour;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.Optional;

/**
 * Ported to Java by lehjr on 10/24/16.
 */
public class RenderEventHandler {
    private static final MPAConfig config = MPAConfig.INSTANCE;
    private static boolean ownFly;
    private final DrawableRect frame = new DrawableRect(config.keybindHUDx(), config.keybindHUDy(), config.keybindHUDx() + (double) 16, config.keybindHUDy() + (double) 16, true, Colour.DARKGREEN.withAlpha(0.2), Colour.GREEN.withAlpha(0.2));

    public RenderEventHandler() {
        this.ownFly = false;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void preTextureStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().equals( Minecraft.getMinecraft().getTextureMapBlocks())) {
            MuseIcon.registerIcons(event.getMap());
            MPSModelHelper.loadArmorModels(event.getMap());
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Post event) {

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution screen = new ScaledResolution(mc);
    }

    @SubscribeEvent
    public void onPreRenderPlayer(RenderPlayerEvent.Pre event) {
        if (!event.getEntityPlayer().capabilities.isFlying && !event.getEntityPlayer().onGround && this.playerHasFlightOn(event.getEntityPlayer())) {
            event.getEntityPlayer().capabilities.isFlying = true;
            RenderEventHandler.ownFly = true;
        }
    }

    private boolean playerHasFlightOn(EntityPlayer player) {
        return
                Optional.ofNullable(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)
                        .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                    if (iItemHandler instanceof IModularItem) {
                        return ((IModularItem) iItemHandler).isModuleOnline(new ResourceLocation(RegistryNames.MODULE_JETPACK__REGNAME)) ||
                                ((IModularItem) iItemHandler).isModuleOnline(new ResourceLocation(RegistryNames.MODULE_GLIDER__REGNAME));
                    }
                    return false;
                }).orElse(false) ||
                        Optional.ofNullable(player.getItemStackFromSlot(EntityEquipmentSlot.FEET)
                                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                            if (iItemHandler instanceof IModularItem) {
                                return ((IModularItem) iItemHandler).isModuleOnline(new ResourceLocation(RegistryNames.MODULE_JETBOOTS__REGNAME));
                            }
                            return false;
                        }).orElse(false) ||

                        Optional.ofNullable(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)
                                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                            if (iItemHandler instanceof IModularItem) {
                                return ((IModularItem) iItemHandler).isModuleOnline(new ResourceLocation(RegistryNames.MODULE_FLIGHT_CONTROL__REGNAME));
                            }
                            return false;
                        }).orElse(false);
    }

    @SubscribeEvent
    public void onPostRenderPlayer(RenderPlayerEvent.Post event) {
        if (RenderEventHandler.ownFly) {
            RenderEventHandler.ownFly = false;
            event.getEntityPlayer().capabilities.isFlying = false;
        }
    }

    @SubscribeEvent
    public void onFOVUpdate(FOVUpdateEvent e) {
        ItemStack helmet = e.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD);

        Optional.ofNullable(e.getEntity().getItemStackFromSlot(EntityEquipmentSlot.HEAD)
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent((iItemHandler -> {
            if (iItemHandler instanceof IModularItem) {
                ItemStack module = ((IModularItem) iItemHandler).getOnlineModuleOrEmpty(new ResourceLocation(RegistryNames.BINOCULARS_MODULE__REGNAME));
                Optional.ofNullable(module.getCapability(PowerModuleCapability.POWER_MODULE, null)).ifPresent(pm->
                        e.setNewfov(e.getNewfov() / (float) pm.applyPropertyModifiers(ModuleConstants.FOV)));
            }
        }));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onPostRenderGameOverlayEvent(RenderGameOverlayEvent.Post e) {
        RenderGameOverlayEvent.ElementType elementType = e.getType();
        if (RenderGameOverlayEvent.ElementType.HOTBAR.equals(elementType)) {
            this.drawKeybindToggles();
        }
    }

    @SideOnly(Side.CLIENT)
    public void drawKeybindToggles() {
        if (config.keybindHUDon()) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayerSP player = mc.player;
            ScaledResolution screen = new ScaledResolution(mc);
            frame.setLeft(config.keybindHUDx());
            frame.setTop(config.keybindHUDy());
            frame.setBottom(frame.top() + 16);
            for (ClickableKeybinding kb : KeybindManager.getKeybindings()) {
                if (kb.displayOnHUD) {
                    double stringwidth = Renderer.getStringWidth(kb.getLabel());
                    frame.setWidth(stringwidth + kb.getBoundModules().size() * 16);
                    frame.draw();
                    Renderer.drawString(kb.getLabel(), frame.left() + 1, frame.top() + 3, (kb.toggleval) ? Colour.RED : Colour.GREEN);
                    double x = frame.left() + stringwidth;
                    for (ClickableModule module : kb.getBoundModules()) {
                        TextureUtils.pushTexture(TextureUtils.TEXTURE_QUILT);
                        boolean active = false;
                        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                            active = Optional.ofNullable(player.getItemStackFromSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
                                if (iItemHandler instanceof IModularItem) {
                                    return ((IModularItem) iItemHandler).isModuleOnline(module.getModule().getItem().getRegistryName());
                                }
                                return false;
                            }).orElse(false);
                        }
//                        Renderer.drawModuleAt(x, frame.top(), module.getModule(), active); // FIXME
                        // FIXME
                        //IconUtils.drawIconAt(x, frame.top(), module.getModule().getIcon(null), (active) ? Colour.WHITE : Colour.DARKGREY.withAlpha(0.5));


                        TextureUtils.popTexture();
                        x += 16;
                    }
                    frame.setTop(frame.top() + 16);
                    frame.setBottom(frame.top() + 16);
                }
            }
        }
    }
}