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