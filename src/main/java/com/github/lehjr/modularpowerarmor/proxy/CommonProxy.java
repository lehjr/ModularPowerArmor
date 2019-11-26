package com.github.lehjr.modularpowerarmor.proxy;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.MPAGuiHandler;
import com.github.lehjr.modularpowerarmor.basemod.ModularPowerArmor;
import com.github.lehjr.modularpowerarmor.basemod.Objects;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.modularpowerarmor.entity.PlasmaBoltEntity;
import com.github.lehjr.modularpowerarmor.entity.SpinningBladeEntity;
import com.github.lehjr.modularpowerarmor.event.*;
import com.github.lehjr.modularpowerarmor.item.module.tool.TerminalHandler;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
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
        RegisterStuff.initFluids();
        MPAPackets.registerMPSPackets();
    }

    public void init(FMLInitializationEvent event) {
//        MPSModules.loadPowerModules(); // FIXME
        EntityRegistry.registerModEntity(new ResourceLocation(Constants.MODID, "entityPlasmaBolt"), PlasmaBoltEntity.class, "entityPlasmaBolt", 2477, ModularPowerArmor.getInstance(), 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation(Constants.MODID, "entitySpinningBlade"), SpinningBladeEntity.class, "entitySpinningBlade", 2478, ModularPowerArmor.getInstance(), 64, 20, true);
        EntityRegistry.registerModEntity(Objects.INSTANCE.luxCapacitor.getRegistryName(), LuxCapacitorEntity.class, "entityLuxCapacitor", 2479, ModularPowerArmor.getInstance(), 64, 20, true);
        NetworkRegistry.INSTANCE.registerGuiHandler(ModularPowerArmor.getInstance(), MPAGuiHandler.INSTANCE);
        TerminalHandler.registerHandler();
        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandlerThingy()); // doesn't seem to work if fired preinit

//        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());



//        File newConfig = new File(event.getModConfigurationDirectory() + "/machinemuse/modularpowerarmor.cfg");
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