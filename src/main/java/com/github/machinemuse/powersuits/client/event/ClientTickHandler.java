/*
 * ModularPowersuits (Maintenance builds by lehjr)
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.client.event;

import com.github.lehjr.mpalib.client.gui.hud.meters.*;
import com.github.lehjr.mpalib.client.render.Renderer;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.math.MathUtils;
import com.github.lehjr.mpalib.string.StringUtils;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.control.KeybindManager;
import com.github.machinemuse.powersuits.client.gui.clickable.ClickableKeybinding;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.config.MPSConfig;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorChestplate;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorHelmet;
import com.github.machinemuse.powersuits.item.tool.ItemPowerFist;
import com.github.machinemuse.powersuits.utils.modulehelpers.AutoFeederHelper;
import com.github.machinemuse.powersuits.utils.modulehelpers.FluidUtils;
import com.github.machinemuse.powersuits.utils.modulehelpers.PlasmaCannonHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Ported to Java by lehjr on 10/24/16.
 */
public class ClientTickHandler {
    /**
     * This handler is called before/after the game processes input events and
     * updates the gui state mainly. *independent of rendering, so don't do rendering here
     * -is also the parent class of KeyBindingHandleryBaseIcon
     *
     * @author MachineMuse
     */

    public ArrayList<String> modules;
    protected HeatMeter heat = null;
    protected HeatMeter energy = null;
    protected WaterMeter water = null;
    protected FluidMeter fluidMeter = null;
    protected PlasmaChargeMeter plasma = null;
    private FluidUtils waterUtils;
    private FluidUtils fluidUtils;

    @SubscribeEvent
    public void onPreClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (ClickableKeybinding kb : KeybindManager.getKeybindings()) {
                kb.doToggleTick();
            }
        }
    }

    public void findInstalledModules(EntityPlayer player) {
        if (player != null) {
            ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (!helmet.isEmpty() && helmet.getItem() instanceof ItemPowerArmorHelmet) {
                if (ModuleManager.INSTANCE.itemHasActiveModule(helmet, MPSModuleConstants.MODULE_AUTO_FEEDER__DATANAME)) {
                    modules.add(MPSModuleConstants.MODULE_AUTO_FEEDER__DATANAME);
                }
                if (ModuleManager.INSTANCE.itemHasActiveModule(helmet, MPSModuleConstants.MODULE_CLOCK__DATANAME)) {
                    modules.add(MPSModuleConstants.MODULE_CLOCK__DATANAME);
                }
                if (ModuleManager.INSTANCE.itemHasActiveModule(helmet, MPSModuleConstants.MODULE_COMPASS__DATANAME)) {
                    modules.add(MPSModuleConstants.MODULE_COMPASS__DATANAME);
                }
            }

            ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (!chest.isEmpty() && chest.getItem() instanceof ItemPowerArmorChestplate) {
                if (ModuleManager.INSTANCE.itemHasActiveModule(chest, MPSModuleConstants.MODULE_BASIC_COOLING_SYSTEM__DATANAME)) {
                    modules.add(MPSModuleConstants.MODULE_BASIC_COOLING_SYSTEM__DATANAME);
                }

                if (ModuleManager.INSTANCE.itemHasActiveModule(chest, MPSModuleConstants.MODULE_ADVANCED_COOLING_SYSTEM__DATANAME)) {
                    modules.add(MPSModuleConstants.MODULE_ADVANCED_COOLING_SYSTEM__DATANAME);
                }
            }

            ItemStack powerfist = player.getHeldItemMainhand();
            if (!powerfist.isEmpty() && powerfist.getItem() instanceof ItemPowerFist) {
                if (ModuleManager.INSTANCE.itemHasActiveModule(powerfist, MPSModuleConstants.MODULE_PLASMA_CANNON__DATANAME))
                    modules.add(MPSModuleConstants.MODULE_PLASMA_CANNON__DATANAME);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderTickEvent(TickEvent.RenderTickEvent event) {
        ItemStack food = new ItemStack(Items.COOKED_BEEF);
        ItemStack clock = new ItemStack(Items.CLOCK);
        ItemStack compass = new ItemStack(Items.COMPASS);

        int yOffsetString = 18;
        double yOffsetIcon = 16.0;
        String ampm;

        double yBaseIcon;
        int yBaseString;
        if (MPSConfig.INSTANCE.useGraphicalMeters()) {
            yBaseIcon = 150.0;
            yBaseString = 155;
        } else {
            yBaseIcon = 26.0;
            yBaseString = 32;
        }

        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            modules = new ArrayList<>();
            findInstalledModules(player);
            if (player != null && Minecraft.getMinecraft().isGuiEnabled() && ItemUtils.getLegacyModularItemsEquipped(player).size() > 0 && Minecraft.getMinecraft().currentScreen == null) {
                Minecraft mc = Minecraft.getMinecraft();
                ScaledResolution screen = new ScaledResolution(mc);
                for (int i = 0; i < modules.size(); i++) {
                    if (Objects.equals(modules.get(i), MPSModuleConstants.MODULE_AUTO_FEEDER__DATANAME)) {
                        int foodLevel = (int) AutoFeederHelper.getFoodLevel(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
                        String num = StringUtils.formatNumberShort(foodLevel);
                        if (i == 0) {
                            Renderer.drawString(num, 17, yBaseString);
                            Renderer.drawItemAt(-1.0, yBaseIcon, food);
                        } else {
                            Renderer.drawString(num, 17, yBaseString + (yOffsetString * i));
                            Renderer.drawItemAt(-1.0, yBaseIcon + (yOffsetIcon * i), food);
                        }
                    } else if (Objects.equals(modules.get(i), MPSModuleConstants.MODULE_CLOCK__DATANAME)) {
                        long time = player.world.provider.getWorldTime();
                        long hour = ((time % 24000) / 1000);
                        if (MPSConfig.INSTANCE.use24hClock()) {
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
                        }
                        if (i == 0) {
                            Renderer.drawString(hour + ampm, 17, yBaseString);
                            Renderer.drawItemAt(-1.0, yBaseIcon, clock);
                        } else {
                            Renderer.drawString(hour + ampm, 17, yBaseString + (yOffsetString * i));
                            Renderer.drawItemAt(-1.0, yBaseIcon + (yOffsetIcon * i), clock);
                        }
                    } else if (Objects.equals(modules.get(i), MPSModuleConstants.MODULE_COMPASS__DATANAME)) {
                        if (i == 0) {
                            Renderer.drawItemAt(-1.0, yBaseIcon, compass);
                        } else {
                            Renderer.drawItemAt(-1.0, yBaseIcon + (yOffsetIcon * i), compass);
                        }
                    } else if (Objects.equals(modules.get(i), MPSModuleConstants.MODULE_BASIC_COOLING_SYSTEM__DATANAME)) {
                        waterUtils = new FluidUtils(player, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST), MPSModuleConstants.MODULE_BASIC_COOLING_SYSTEM__DATANAME);
                    } else if (Objects.equals(modules.get(i), MPSModuleConstants.MODULE_ADVANCED_COOLING_SYSTEM__DATANAME)) {
                        fluidUtils = new FluidUtils(player, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST), MPSModuleConstants.MODULE_ADVANCED_COOLING_SYSTEM__DATANAME);
                    }
                }
                drawMeters(player, screen);
            }
        }
    }

    private void drawMeters(EntityPlayer player, ScaledResolution screen) {
        double top = (double) screen.getScaledHeight() / 2.0 - (double) 16;
//    	double left = screen.getScaledWidth() - 2;
        double left = screen.getScaledWidth() - 34;

        // energy
        double maxEnergy = ElectricItemUtils.getMaxPlayerEnergy(player);
        double currEnergy = ElectricItemUtils.getPlayerEnergy(player);
        String currEnergyStr = StringUtils.formatNumberShort(currEnergy) + "RF";
        String maxEnergyStr = StringUtils.formatNumberShort(maxEnergy);

        // heat
        double maxHeat = HeatUtils.getPlayerMaxHeat(player);
        double currHeat = HeatUtils.getPlayerHeat(player);
        String currHeatStr = StringUtils.formatNumberShort(currHeat);
        String maxHeatStr = StringUtils.formatNumberShort(maxHeat);

        // Water
        double maxWater = 0;
        double currWater = 0;
        String currWaterStr = "";
        String maxWaterStr = "";

        if (waterUtils != null) {
            maxWater = waterUtils.getMaxFluidLevel();
            currWater = waterUtils.getFluidLevel();
            currWaterStr = StringUtils.formatNumberShort(currWater);
            maxWaterStr = StringUtils.formatNumberShort(maxWater);
        }

        // Fluid
        double maxFluid = 0;
        double currFluid = 0;
        String currFluidStr = "";
        String maxFluidStr = "";

        if (ModuleManager.INSTANCE.itemHasModule(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST), MPSModuleConstants.MODULE_ADVANCED_COOLING_SYSTEM__DATANAME)) {
            fluidUtils = new FluidUtils(player, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST), MPSModuleConstants.MODULE_ADVANCED_COOLING_SYSTEM__DATANAME);
            maxFluid = fluidUtils.getMaxFluidLevel();
            currFluid = fluidUtils.getFluidLevel();
            currFluidStr = StringUtils.formatNumberShort(currWater);
            maxFluidStr = StringUtils.formatNumberShort(maxWater);
        }

        // plasma
        double maxPlasma = PlasmaCannonHelper.getMaxPlasma(player);
        double currPlasma = PlasmaCannonHelper.getPlayerPlasma(player);
        String currPlasmaStr = StringUtils.formatNumberShort(currPlasma);
        String maxPlasmaStr = StringUtils.formatNumberShort(maxPlasma);

        if (MPSConfig.INSTANCE.useGraphicalMeters()) {
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

            if (maxWater > 0) {
                numMeters++;
                if (water == null) {
                    water = new WaterMeter();
                }
            } else water = null;

            if (maxFluid > 0) {
                numMeters++;
                if (fluidMeter == null) {
                    fluidMeter = fluidUtils.getFluidMeter();
                }
            }

            if (maxPlasma > 0 /* && drawPlasmaMeter */) {
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
                Renderer.drawRightAlignedString(currEnergyStr, stringX, top);
                numMeters--;
            }

            heat.draw(left, top + (totalMeters - numMeters) * 8, MathUtils.clampDouble(currHeat, 0, maxHeat) / maxHeat);
            Renderer.drawRightAlignedString(currHeatStr, stringX, top + (totalMeters - numMeters) * 8);
            numMeters--;

            if (water != null) {
                water.draw(left, top + (totalMeters - numMeters) * 8, currWater / maxWater);
                Renderer.drawRightAlignedString(currWaterStr, stringX, top + (totalMeters - numMeters) * 8);
                numMeters--;
            }

            if (fluidMeter != null) {
                fluidMeter.draw(left, top + (totalMeters - numMeters) * 8, currFluid / maxFluid);
                Renderer.drawRightAlignedString(currFluidStr, stringX, top + (totalMeters - numMeters) * 8);
                numMeters--;
            }

            if (plasma != null) {
                plasma.draw(left, top + (totalMeters - numMeters) * 8, currPlasma / maxPlasma);
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

            if (maxWater > 0) {
                Renderer.drawString(currWaterStr + '/' + maxWaterStr + " buckets", 2, 2 + (numReadouts * 9));
                numReadouts += 1;
            }

            if (maxFluid > 0) {
                Renderer.drawString(currFluidStr + '/' + maxFluidStr + " buckets", 2, 2 + (numReadouts * 9));
                numReadouts += 1;
            }

            if (maxPlasma > 0 /* && drawPlasmaMeter */) {
                Renderer.drawString(currPlasmaStr + '/' + maxPlasmaStr + "%", 2, 2 + (numReadouts * 9));
            }
        }
    }
}