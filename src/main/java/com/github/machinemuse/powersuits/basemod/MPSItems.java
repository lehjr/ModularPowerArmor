/*
 * Copyright (c) ${DATE} MachineMuse, Lehjr
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

package com.github.machinemuse.powersuits.basemod;

import com.github.machinemuse.powersuits.api.constants.MPSModConstants;
import com.github.machinemuse.powersuits.block.BlockLuxCapacitor;
import com.github.machinemuse.powersuits.block.BlockTinkerTable;
import com.github.machinemuse.powersuits.fluid.BlockFluidLiquidNitrogen;
import com.github.machinemuse.powersuits.fluid.LiquidNitrogen;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorBoots;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorChestplate;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorHelmet;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorLeggings;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.item.tool.ItemPowerFist;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Claire Semple on 9/9/2014.
 * <p>
 * Ported to Java by lehjr on 10/22/16.
 */
@Mod.EventBusSubscriber(modid = MPSModConstants.MODID)
public enum MPSItems {
    INSTANCE;

    // Armor --------------------------------------------------------------------------------------
    public static final String powerArmorHelmetRegName = MPSModConstants.MODID + ":powerarmor_head";
    public static final String powerArmorChestPlateRegName = MPSModConstants.MODID + ":powerarmor_torso";
    public static final String powerArmorLeggingsRegName = MPSModConstants.MODID + ":powerarmor_legs";
    public static final String powerArmorBootsRegName = MPSModConstants.MODID + ":powerarmor_feet";

    @GameRegistry.ObjectHolder(powerArmorHelmetRegName)
    public static final ItemPowerArmorHelmet powerArmorHead = null;
    @GameRegistry.ObjectHolder(powerArmorChestPlateRegName)
    public static final ItemPowerArmorChestplate powerArmorTorso = null;
    @GameRegistry.ObjectHolder(powerArmorLeggingsRegName)
    public static final ItemPowerArmorLeggings powerArmorLegs = null;
    @GameRegistry.ObjectHolder(powerArmorBootsRegName)
    public static final ItemPowerArmorBoots powerArmorFeet = null;

    // HandHeld -----------------------------------------------------------------------------------
    public static final String powerFistRegName = MPSModConstants.MODID + ":power_fist";

    @GameRegistry.ObjectHolder(powerFistRegName)
    public static final ItemPowerFist powerFist = null;

    // Components ---------------------------------------------------------------------------------
    public static final String componentsRegname = MPSModConstants.MODID + ":powerarmorcomponent";

    @GameRegistry.ObjectHolder(componentsRegname)
    public static final ItemComponent components = null;

    // Blocks -------------------------------------------------------------------------------------
    public static final String tinkerTableRegName = MPSModConstants.MODID + ":tinkertable";
    public static final String luxCapaRegName = MPSModConstants.MODID + ":luxcapacitor";

    @GameRegistry.ObjectHolder(tinkerTableRegName)
    public static final BlockTinkerTable tinkerTable = null;

    @GameRegistry.ObjectHolder(luxCapaRegName)
    public static final BlockLuxCapacitor luxCapacitor = null;

    // Fluid --------------------------------------------------------------------------------------
    public static final LiquidNitrogen liquidNitrogen = new LiquidNitrogen();

    public static final String blockLiquidNitrogenName = MPSModConstants.MODID + ":liquid_nitrogen";
    @GameRegistry.ObjectHolder(blockLiquidNitrogenName)
    public static final BlockFluidLiquidNitrogen blockLiquidNitrogen = null;

    @SubscribeEvent
    public static void regigisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                // Armor --------------------------------------------------------------------------------------
                new ItemPowerArmorHelmet(powerArmorHelmetRegName),
                new ItemPowerArmorChestplate(powerArmorChestPlateRegName),
                new ItemPowerArmorLeggings(powerArmorLeggingsRegName),
                new ItemPowerArmorBoots(powerArmorBootsRegName),

                // HandHeld -----------------------------------------------------------------------------------
                new ItemPowerFist(powerFistRegName),

                // Components ---------------------------------------------------------------------------------
                new ItemComponent(componentsRegname),

//                // ItemBlocks ---------------------------------------------------------------------------------
                new ItemBlock(tinkerTable).setRegistryName(new ResourceLocation(tinkerTableRegName)),
                new ItemBlock(luxCapacitor).setRegistryName(new ResourceLocation(luxCapaRegName))
        );

        ItemComponent temp = (ItemComponent) event.getRegistry().getValue(new ResourceLocation(componentsRegname));
        if (temp != null)
            temp.registerOres();
    }

    @SubscribeEvent
    public static void initBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new BlockTinkerTable( new ResourceLocation(tinkerTableRegName)));
        event.getRegistry().register(new BlockLuxCapacitor(new ResourceLocation(luxCapaRegName)));

        event.getRegistry().register(new BlockFluidLiquidNitrogen(new ResourceLocation(blockLiquidNitrogenName)));
    }

    static boolean alreadyRegistered = true;
    public static void initFluids() {
        if (!FluidRegistry.isFluidRegistered("liquidnitrogen") && !FluidRegistry.isFluidRegistered("liquid_nitrogen")) {
            FluidRegistry.registerFluid(liquidNitrogen);
            FluidRegistry.addBucketForFluid(liquidNitrogen);
            alreadyRegistered = false;
        }
    }
}
