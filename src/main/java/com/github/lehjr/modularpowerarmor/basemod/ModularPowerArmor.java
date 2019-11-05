package com.github.lehjr.modularpowerarmor.basemod;

import com.github.lehjr.modularpowerarmor.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;

/**
 * Modular modularpowerarmor
 *
 * @author MachineMuse, Lehjr
 */
@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION, dependencies = "required-after:mpalib@[@mpalib_version@,)")
public enum ModularPowerArmor {
    INSTANCE;

    @SidedProxy(clientSide = "net.machinemuse.modularpowerarmor.common.proxy.ClientProxy", serverSide = "net.machinemuse.modularpowerarmor.common.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static Configuration config = null;

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @Nonnull
    @Mod.InstanceFactory
    public static ModularPowerArmor getInstance() {
        return INSTANCE;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        proxy.registerEvents();
        proxy.registerRenderers();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}
