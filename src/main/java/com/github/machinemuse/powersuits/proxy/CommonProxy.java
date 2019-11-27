/*
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

package com.github.machinemuse.powersuits.proxy;

import com.github.machinemuse.powersuits.api.constants.MPSModConstants;
import com.github.machinemuse.powersuits.capabilities.CapabilityHandler;
import com.github.machinemuse.powersuits.basemod.MPSGuiHandler;
import com.github.machinemuse.powersuits.basemod.MPSItems;
import com.github.machinemuse.powersuits.basemod.MPSModules;
import com.github.machinemuse.powersuits.basemod.ModularPowersuits;
import com.github.machinemuse.powersuits.entity.EntityLuxCapacitor;
import com.github.machinemuse.powersuits.entity.EntityPlasmaBolt;
import com.github.machinemuse.powersuits.entity.EntitySpinningBlade;
import com.github.machinemuse.powersuits.event.HarvestEventHandler;
import com.github.machinemuse.powersuits.event.MovementManager;
import com.github.machinemuse.powersuits.event.PlayerLoginHandlerThingy;
import com.github.machinemuse.powersuits.event.PlayerUpdateHandler;
import com.github.machinemuse.powersuits.network.MPSPackets;
import com.github.machinemuse.powersuits.powermodule.tool.TerminalHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Common side of the proxy. Provides functions which
 * the ClientProxy and CommonProxy will override if the behaviour is different for client and
 * server, and keeps some common behaviour.
 *
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 11/14/16.
 */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        MPSItems.INSTANCE.initFluids();
        MPSPackets.registerMPSPackets();
    }

    public void init(FMLInitializationEvent event) {
        MPSModules.loadPowerModules();
        EntityRegistry.registerModEntity(new ResourceLocation(MPSModConstants.MODID, "entityPlasmaBolt"), EntityPlasmaBolt.class, "entityPlasmaBolt", 2477, ModularPowersuits.getInstance(), 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation(MPSModConstants.MODID, "entitySpinningBlade"), EntitySpinningBlade.class, "entitySpinningBlade", 2478, ModularPowersuits.getInstance(), 64, 20, true);
        EntityRegistry.registerModEntity(MPSItems.INSTANCE.luxCapacitor.getRegistryName(), EntityLuxCapacitor.class, "entityLuxCapacitor", 2479, ModularPowersuits.getInstance(), 64, 20, true);
        NetworkRegistry.INSTANCE.registerGuiHandler(ModularPowersuits.getInstance(), MPSGuiHandler.INSTANCE);
        TerminalHandler.registerHandler();
        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandlerThingy()); // doesn't seem to work if fired preinit

        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());



//        File newConfig = new File(event.getModConfigurationDirectory() + "/machinemuse/powersuits.cfg");
//        Config.init(new Configuration(newConfig));
//        Config.setConfigFolderBase(event.getModConfigurationDirectory());
//        Config.extractRecipes();
//        MPSItems.populateItems();
//        MPSItems.populateComponents();





    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new HarvestEventHandler());
        MinecraftForge.EVENT_BUS.register(new MovementManager());
        MinecraftForge.EVENT_BUS.register(new PlayerUpdateHandler());
    }

    public void registerRenderers() {
    }
}