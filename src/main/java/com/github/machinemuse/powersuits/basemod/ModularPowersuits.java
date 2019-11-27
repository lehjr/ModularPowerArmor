package com.github.machinemuse.powersuits.basemod;

import com.github.machinemuse.powersuits.api.constants.MPSModConstants;
import com.github.machinemuse.powersuits.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;

/**
 * Modular Powersuits
 *
 * @author MachineMuse
 * <p>
 * Ported to Java by lehjr on 11/14/16.
 */
@Mod(modid = MPSModConstants.MODID, name = MPSModConstants.NAME, version = MPSModConstants.VERSION, dependencies = "required-after:mpalib@[@mpalib_version@,)")
public enum ModularPowersuits {
    INSTANCE;

    @SidedProxy(clientSide = "com.github.machinemuse.powersuits.proxy.ClientProxy", serverSide = "com.github.machinemuse.powersuits.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static Configuration config = null;

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @Nonnull
    @Mod.InstanceFactory
    public static ModularPowersuits getInstance() {
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
