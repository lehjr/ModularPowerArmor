package net.machinemuse.powersuits.client.event;

import com.google.common.util.concurrent.AtomicDouble;
import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.client.gui.hud.meters.*;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.energy.ElectricItemUtils;
import net.machinemuse.numina.heat.MuseHeatUtils;
import net.machinemuse.numina.math.MuseMathUtils;
import net.machinemuse.numina.string.MuseStringUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSObjects;
import net.machinemuse.powersuits.basemod.MPSRegistryNames;
import net.machinemuse.powersuits.item.module.environmental.AutoFeederModule;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This handler is called before/after the game processes input events and
 * updates the gui state mainly. *independent of rendering, so don't do rendering here
 * -is also the parent class of KeyBindingHandleryBaseIcon
 *
 * @author MachineMuse
 */
public class ClientTickHandler {
    public ArrayList<String> modules;
    protected HeatMeter heat = null;
    protected HeatMeter energy = null;
    protected WaterMeter water = null;
    protected FluidMeter fluidMeter = null;
    protected PlasmaChargeMeter plasma = null;
    MPSObjects mpsi = MPSObjects.INSTANCE;
    static final ItemStack food = new ItemStack(Items.COOKED_BEEF);
    static final ResourceLocation autoFeederReg = new ResourceLocation(MPSRegistryNames.MODULE_AUTO_FEEDER__REGNAME);
    static final ResourceLocation clockReg = new ResourceLocation(MPSRegistryNames.MODULE_CLOCK__REGNAME);
    static final ResourceLocation compassReg = new ResourceLocation(MPSRegistryNames.MODULE_COMPASS__REGNAME);
    static final ResourceLocation plasmaCannon = new ResourceLocation(MPSRegistryNames.MODULE_PLASMA_CANNON__REGNAME);

    @SubscribeEvent
    public void onPreClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
//            for (ClickableKeybinding kb : KeybindManager.getKeybindings()) {
//                kb.doToggleTick();
//            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderTickEvent(TickEvent.RenderTickEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        int yOffsetString = 18;
        double yOffsetIcon = 16.0;
        double yBaseIcon;
        int yBaseString;
        if (MPSConfig.INSTANCE.HUD_USE_GRAPHICAL_METERS.get()) {
            yBaseIcon = 150.0;
            yBaseString = 155;
        } else {
            yBaseIcon = 26.0;
            yBaseString = 32;
        }

        if (event.phase == TickEvent.Phase.END) {
            PlayerEntity player = minecraft.player;
            modules = new ArrayList<>();
            if (player != null && minecraft.isGuiEnabled() && minecraft.currentScreen == null) {
                Minecraft mc = minecraft;
                MainWindow screen = mc.mainWindow;

                // Misc Overlay Items ---------------------------------------------------------------------------------
                AtomicInteger index = new AtomicInteger(0);

                // Helmet modules with overlay
                player.getItemStackFromSlot(EquipmentSlotType.HEAD).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                    if (!(h instanceof IModularItem))
                        return;

                    // AutoFeeder
                    ItemStack autoFeeder = ((IModularItem) h).getOnlineModuleOrEmpty(autoFeederReg);
                    if (!autoFeeder.isEmpty()) {
                        int foodLevel = (int) ((AutoFeederModule) autoFeeder.getItem()).getFoodLevel(autoFeeder);
                        String num = MuseStringUtils.formatNumberShort(foodLevel);
                        MuseRenderer.drawString(num, 17, yBaseString + (yOffsetString * index.get()));
                        MuseRenderer.drawItemAt(-1.0, yBaseIcon + (yOffsetIcon * index.get()), food);
                        index.addAndGet(1);
                    }

                    // Clock
                    ItemStack clock = ((IModularItem) h).getOnlineModuleOrEmpty(clockReg);
                    if (!clock.isEmpty()) {
                        String ampm;
                        long time = player.world.getGameTime();
                        long hour = ((time % 24000) / 1000);
                        if (MPSConfig.INSTANCE.HUD_USE_24_HOUR_CLOCK.get()) {
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

                            MuseRenderer.drawString(hour + ampm, 17, yBaseString + (yOffsetString * index.get()));
                            MuseRenderer.drawItemAt(-1.0, yBaseIcon + (yOffsetIcon * index.get()), clock);

                            index.addAndGet(1);
                        }
                    }

                    // Compass
                    ItemStack compass = ((IModularItem) h).getOnlineModuleOrEmpty(compassReg);
                    if (!compass.isEmpty()) {

                        MuseRenderer.drawItemAt(-1.0, yBaseIcon + (yOffsetIcon * index.get()), compass);
                        index.addAndGet(1);
                    }
                });

                // Meters ---------------------------------------------------------------------------------------------
                double top = (double) screen.getScaledHeight() / 2.0 - (double) 16;
//    	double left = screen.getScaledWidth() - 2;
                double left = screen.getScaledWidth() - 34;

                // energy
                double maxEnergy = ElectricItemUtils.getMaxPlayerEnergy(player);
                double currEnergy = ElectricItemUtils.getPlayerEnergy(player);
                String currEnergyStr = MuseStringUtils.formatNumberShort(currEnergy) + "RF";
                String maxEnergyStr = MuseStringUtils.formatNumberShort(maxEnergy);

                // heat
                double maxHeat = MuseHeatUtils.getPlayerMaxHeat(player);
                double currHeat = MuseHeatUtils.getPlayerHeat(player);
                String currHeatStr = MuseStringUtils.formatNumberShort(currHeat);
                String maxHeatStr = MuseStringUtils.formatNumberShort(maxHeat);

                // Fluid
                AtomicDouble currFluid = new AtomicDouble(0);
                AtomicDouble maxFluid = new AtomicDouble(0);
                String currFluidStr = "";
                String maxFluidStr = "";

                player.getItemStackFromSlot(EquipmentSlotType.CHEST).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(fh -> {
                              for (IFluidTankProperties prop : fh.getTankProperties()) {
                                  FluidStack stack = prop.getContents();
                                  if (stack!= null) {
                                      fluidMeter = new FluidMeter(stack.getFluid());
                                      maxFluid.getAndAdd(prop.getCapacity());
                                      currFluid.addAndGet(stack.amount);
                                  }
                              }
                });

                // Plasma
                AtomicDouble currentPlasma = new AtomicDouble(0);
                AtomicDouble maxPlasma = new AtomicDouble(0);
                if (player.isHandActive())
                    player.getHeldItem(player.getActiveHand()).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(modechanging -> {
                        if (!(modechanging instanceof IModeChangingItem))
                            return;

                        ItemStack module = ((IModeChangingItem) modechanging).getActiveModule();
                        int actualCount = 0;

                        if (!module.isEmpty()) {
                            // Plasma Cannon
                            if (module.getItem().getRegistryName().equals(plasmaCannon)) {
                                actualCount = (72000 - player.getItemInUseCount());
                                currentPlasma.getAndAdd((actualCount > 50 ? 50 : actualCount) * 2);
                                maxPlasma.getAndAdd(100);

                                // Ore Scanner
                            } else if (false) {
                                actualCount = (72000 - player.getItemInUseCount());
                                currentPlasma.getAndAdd((actualCount > 40 ? 40 : actualCount) * 2.5);
                                maxPlasma.getAndAdd(100);
                            }
                        }
                    });
                String currPlasmaStr = MuseStringUtils.formatNumberShort(currentPlasma.get());
                String maxPlasmaStr = MuseStringUtils.formatNumberShort(maxPlasma.get());


                if (MPSConfig.INSTANCE.HUD_USE_GRAPHICAL_METERS.get()) {
                    int numMeters = 0;

                    if (maxEnergy > 0) {
                        numMeters++;
                        if (energy == null) {
                            energy = new EnergyMeter();
                        }
                    } else energy = null;

                    if (maxHeat > 0) {
                        numMeters++;
                        if (heat == null)
                            heat = new HeatMeter();
                    } else heat = null;

                    if (maxFluid.get() > 0 && fluidMeter != null) {
                        numMeters++;
                     }

                    if (maxPlasma.get() > 0 /* && drawPlasmaMeter */) {
                        numMeters++;
                        if (plasma == null) {
                            plasma = new PlasmaChargeMeter();
                        }
                    } else plasma = null;

                    double stringX = left - 2;
                    final int totalMeters = numMeters;
                    //"(totalMeters-numMeters) * 8" = 0 for whichever of these is first,
                    //but including it won't hurt and this makes it easier to swap them around.

                    if (energy != null) {
                        energy.draw(left, top + (totalMeters - numMeters) * 8, currEnergy / maxEnergy);
                        MuseRenderer.drawRightAlignedString(currEnergyStr, stringX, top);
                        numMeters--;
                    }

                    if (heat != null) {
                        heat.draw(left, top + (totalMeters - numMeters) * 8, MuseMathUtils.clampDouble(currHeat, 0, maxHeat) / maxHeat);
                        MuseRenderer.drawRightAlignedString(currHeatStr, stringX, top + (totalMeters - numMeters) * 8);
                        numMeters--;
                    }

                    if (fluidMeter != null) {
                        fluidMeter.draw(left, top + (totalMeters - numMeters) * 8, currFluid.get() / maxFluid.get());
                        MuseRenderer.drawRightAlignedString(currFluidStr, stringX, top + (totalMeters - numMeters) * 8);
                        numMeters--;
                    }

                    if (plasma != null) {
                        plasma.draw(left, top + (totalMeters - numMeters) * 8, currentPlasma.get() / maxPlasma.get());
                        MuseRenderer.drawRightAlignedString(currPlasmaStr, stringX, top + (totalMeters - numMeters) * 8);
                    }

                } else {
                    int numReadouts = 0;
                    if (maxEnergy > 0) {
                        MuseRenderer.drawString(currEnergyStr + '/' + maxEnergyStr + " \u1D60", 2, 2);
                        numReadouts += 1;
                    }

                    MuseRenderer.drawString(currHeatStr + '/' + maxHeatStr + " C", 2, 2 + (numReadouts * 9));
                    numReadouts += 1;

                    if (maxFluid.get() > 0) {
                        MuseRenderer.drawString(currFluidStr + '/' + maxFluidStr + " buckets", 2, 2 + (numReadouts * 9));
                        numReadouts += 1;
                    }

                    if (maxPlasma.get() > 0 /* && drawPlasmaMeter */) {
                        MuseRenderer.drawString(currPlasmaStr + '/' + maxPlasmaStr + "%", 2, 2 + (numReadouts * 9));
                    }
                }
            }
        }
    }
}
