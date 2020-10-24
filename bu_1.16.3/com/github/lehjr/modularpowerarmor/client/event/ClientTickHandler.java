package com.github.lehjr.modularpowerarmor.client.event;

import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.client.control.KeybindManager;
import com.github.lehjr.modularpowerarmor.client.gui.clickable.ClickableKeybinding;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.item.module.environmental.AutoFeederModule;
import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.client.gui.hud.meters.EnergyMeter;
import com.github.lehjr.mpalib.client.gui.hud.meters.HeatMeter;
import com.github.lehjr.mpalib.client.gui.hud.meters.PlasmaChargeMeter;
import com.github.lehjr.mpalib.client.gui.hud.meters.WaterMeter;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.util.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.math.MathUtils;
import com.github.lehjr.mpalib.string.StringUtils;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This handler is called before/after the game processes input events and
 * updates the gui state mainly. *independent of rendering, so don't do rendering here
 * -is also the parent class of KeyBindingHandleryBaseIcon
 *
 * @author MachineMuse
 */
public class ClientTickHandler {
    protected HeatMeter heatMeter = null;
    protected HeatMeter energyMeter = null;
    protected WaterMeter waterMeter = null;
    protected PlasmaChargeMeter plasmaMeter = null;
    static final ItemStack food = new ItemStack(Items.COOKED_BEEF);
    static final ResourceLocation autoFeederReg = new ResourceLocation(MPARegistryNames.MODULE_AUTO_FEEDER__REGNAME);
    static final ResourceLocation clockReg = new ResourceLocation(MPARegistryNames.MODULE_CLOCK__REGNAME);
    static final ResourceLocation compassReg = new ResourceLocation(MPARegistryNames.MODULE_COMPASS__REGNAME);
    static final ResourceLocation plasmaCannon = new ResourceLocation(MPARegistryNames.MODULE_PLASMA_CANNON__REGNAME);

    @SubscribeEvent
    public void onPreClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().player == null) {
            return;
        }

        if (event.phase == TickEvent.Phase.START) {
            for (ClickableKeybinding kb : KeybindManager.INSTANCE.getKeybindings()) {
                kb.doToggleTick();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderTickEvent(TickEvent.RenderTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        int yOffsetString = 18;
        float yOffsetIcon = 16.0F;
        float yBaseIcon;
        int yBaseString;
        if (MPASettings.useGraphicalMeters()) {
            yBaseIcon = 150.0F;
            yBaseString = 155;
        } else {
            yBaseIcon = 26.0F;
            yBaseString = 32;
        }

        if (event.phase == TickEvent.Phase.END) {
            PlayerEntity player = minecraft.player;
            if (player != null && minecraft.isGuiEnabled() && minecraft.currentScreen == null) {
                Minecraft mc = minecraft;
                MainWindow screen = mc.getMainWindow();

                // Misc Overlay Items ---------------------------------------------------------------------------------
                AtomicInteger index = new AtomicInteger(0);

                // Helmet modules with overlay
                player.getItemStackFromSlot(EquipmentSlotType.HEAD).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                    if (!(h instanceof IModularItem)) {
                        return;
                    }

                    // AutoFeeder
                    ItemStack autoFeeder = ((IModularItem) h).getOnlineModuleOrEmpty(autoFeederReg);
                    if (!autoFeeder.isEmpty()) {
                        int foodLevel = (int) ((AutoFeederModule) autoFeeder.getItem()).getFoodLevel(autoFeeder);
                        String num = StringUtils.formatNumberShort(foodLevel);
                        Renderer.drawString(num, 17, yBaseString + (yOffsetString * index.get()));
                        Renderer.drawItemAt(-1.0, yBaseIcon + (yOffsetIcon * index.get()), food);
                        index.addAndGet(1);
                    }


                    // Clock
                    ItemStack clock = ((IModularItem) h).getOnlineModuleOrEmpty(clockReg);
                    if (!clock.isEmpty()) {
                        String ampm;
                        long time = player.world.getDayTime();
                        long hour = ((time % 24000) / 1000);
                        if (MPASettings.use24HourClock()) {
                            if (hour < 19) {
                                hour += 6;
                            } else {
                                hour -= 18;
                            }
                            ampm = "h";
                        } else {
                            if (hour < 6) {
                                hour += 6;
                                ampm = " AM";
                            } else if (hour == 6) {
                                hour = 12;
                                ampm = " PM";
                            } else if (hour > 6 && hour < 18) {
                                hour -= 6;
                                ampm = " PM";
                            } else if (hour == 18) {
                                hour = 12;
                                ampm = " AM";
                            } else {
                                hour -= 18;
                                ampm = " AM";
                            }

                            Renderer.drawString(hour + ampm, 17, yBaseString + (yOffsetString * index.get()));
                            Renderer.drawItemAt(-1.0, yBaseIcon + (yOffsetIcon * index.get()), clock);

                            index.addAndGet(1);
                        }
                    }

                    // Compass
                    ItemStack compass = ((IModularItem) h).getOnlineModuleOrEmpty(compassReg);
                    if (!compass.isEmpty()) {
                        Renderer.drawItemAt(-1.0, yBaseIcon + (yOffsetIcon * index.get()), compass);
                        index.addAndGet(1);
                    }
                });

                // Meters ---------------------------------------------------------------------------------------------
                float top = (float) screen.getScaledHeight() / 2.0F - 16F;
                float left = screen.getScaledWidth() - 34;

                // energy
                float maxEnergy = ElectricItemUtils.getMaxPlayerEnergy(player);
                float currEnergy = ElectricItemUtils.getPlayerEnergy(player);
                String currEnergyStr = StringUtils.formatNumberShort(currEnergy) + "RF";
                String maxEnergyStr = StringUtils.formatNumberShort(maxEnergy);

                // heat
                float maxHeat = (float) HeatUtils.getPlayerMaxHeat(player);
                float currHeat = (float) HeatUtils.getPlayerHeat(player);

                String currHeatStr = StringUtils.formatNumberShort(currHeat);
                String maxHeatStr = StringUtils.formatNumberShort(maxHeat);

                // Water
                AtomicReference<Float> currWater = new AtomicReference<Float>(0F);
                AtomicReference<Float> maxWater = new AtomicReference<Float>(0F);
                AtomicReference<String> currWaterStr = new AtomicReference<>("");
                AtomicReference<String> maxWaterStr = new AtomicReference<>("");

                player.getItemStackFromSlot(EquipmentSlotType.CHEST).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fh -> {
                    for (int i = 0; i < fh.getTanks(); i++) {
                        maxWater.set(maxWater.get() + fh.getTankCapacity(i));
                        if (maxWater.get() > 0) {
                            FluidStack fluidStack = fh.getFluidInTank(i);
                            currWater.set(currWater.get() + fluidStack.getAmount());
                            waterMeter = new WaterMeter();
                            currWaterStr.set(StringUtils.formatNumberShort(currWater.get()));
                            maxWaterStr.set(StringUtils.formatNumberShort(maxWater.get()));
                        }
                    }
                });

                // Plasma
                AtomicReference<Float> currentPlasma = new AtomicReference<Float>(0F);
                AtomicReference<Float> maxPlasma = new AtomicReference<Float>(0F);
                if (player.isHandActive()) {
                    player.getHeldItem(player.getActiveHand()).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(modechanging -> {
                        if (!(modechanging instanceof IModeChangingItem)) {
                            return;
                        }

                        ItemStack module = ((IModeChangingItem) modechanging).getActiveModule();
                        int actualCount = 0;

                        int maxDuration = ((IModeChangingItem) modechanging).getModularItemStack().getUseDuration();
                        if (!module.isEmpty()) {
                            // Plasma Cannon
                            if (module.getItem().getRegistryName().equals(plasmaCannon)) {
                                actualCount = (maxDuration - player.getItemInUseCount());
                                currentPlasma.set(
                                        currentPlasma.get() + (actualCount > 50 ? 50 : actualCount) * 2);
                                maxPlasma.set(maxPlasma.get() + 100F);

                                // Ore Scanner or whatever
                            } else {
                                actualCount = (maxDuration - player.getItemInUseCount());
                                currentPlasma.set(
                                        currentPlasma.get() + (actualCount > 40 ? 40 : actualCount) * 2.5F);
                                maxPlasma.set(maxPlasma.get() + 100F);
                            }
                        }
                    });
                }

                float val = currentPlasma.get();
                String currPlasmaStr = StringUtils.formatNumberShort((int)val) + "%";
                String maxPlasmaStr = StringUtils.formatNumberShort(maxPlasma.get());

                if (MPASettings.useGraphicalMeters()) {
                    int numMeters = 0;

                    if (maxEnergy > 0) {
                        numMeters++;
                        if (energyMeter == null) {
                            energyMeter = new EnergyMeter();
                        }
                    } else energyMeter = null;

                    if (maxHeat > 0) {
                        numMeters++;
                        if (heatMeter == null)
                            heatMeter = new HeatMeter();
                    } else heatMeter = null;

                    if (maxWater.get() > 0 && waterMeter != null) {
                        numMeters++;
                    }

                    if (maxPlasma.get() > 0 /* && drawPlasmaMeter */) {
                        numMeters++;
                        if (plasmaMeter == null) {
                            plasmaMeter = new PlasmaChargeMeter();
                        }
                    } else plasmaMeter = null;

                    double stringX = left - 2;
                    final int totalMeters = numMeters;
                    //"(totalMeters-numMeters) * 8" = 0 for whichever of these is first,
                    //but including it won't hurt and this makes it easier to swap them around.

                    if (energyMeter != null) {
                        energyMeter.draw(left, top + (totalMeters - numMeters) * 8, (float) (currEnergy / maxEnergy));
                        Renderer.drawRightAlignedString(currEnergyStr, stringX, top);
                        numMeters--;
                    }

                    if (heatMeter != null) {
                        heatMeter.draw(left, top + (totalMeters - numMeters) * 8, MathUtils.clampFloat(currHeat, 0, maxHeat) / maxHeat);
                        Renderer.drawRightAlignedString(currHeatStr, stringX, top + (totalMeters - numMeters) * 8);
                        numMeters--;
                    }

                    if (waterMeter != null) {
                        waterMeter.draw(left, top + (totalMeters - numMeters) * 8, MathUtils.clampFloat(currWater.get(), 0, maxWater.get()) / maxWater.get());
                        Renderer.drawRightAlignedString(currWaterStr.get(), stringX, top + (totalMeters - numMeters) * 8);
                        numMeters--;
                    }

                    if (plasmaMeter != null) {
                        plasmaMeter.draw(left, top + (totalMeters - numMeters) * 8, currentPlasma.get() / maxPlasma.get());
                        Renderer.drawRightAlignedString(currPlasmaStr, stringX, top + (totalMeters - numMeters) * 8);
                    }

                } else {
                    int numReadouts = 0;
                    if (maxEnergy > 0) {
                        Renderer.drawString(currEnergyStr + '/' + maxEnergyStr + " \u1D60", 2, 2);
                        numReadouts += 1;
                    }

                    Renderer.drawString(currHeatStr + '/' + maxHeatStr + " C", 2, 2 + (numReadouts * 9));
                    numReadouts += 1;

                    if (maxWater.get() > 0) {
                        Renderer.drawString(currWaterStr.get() + '/' + maxWaterStr.get() + " buckets", 2, 2 + (numReadouts * 9));
                        numReadouts += 1;
                    }

                    if (maxPlasma.get() > 0 /* && drawPlasmaMeter */) {
                        Renderer.drawString(currPlasmaStr + '/' + maxPlasmaStr + "%", 2, 2 + (numReadouts * 9));
                    }
                }
            }
        }
    }
}
