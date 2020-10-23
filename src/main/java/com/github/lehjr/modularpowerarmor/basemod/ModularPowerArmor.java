package com.github.lehjr.modularpowerarmor.basemod;

import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.recipe.MPARecipeConditionFactory;
import com.github.lehjr.mpalib.config.ConfigHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MPAConstants.MOD_ID)
public class ModularPowerArmor {
    public ModularPowerArmor() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, MPASettings.CLIENT_SPEC, ConfigHelper.setupConfigFile("mpa-client-only.toml", MPAConstants.MOD_ID).getAbsolutePath());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, MPASettings.SERVER_SPEC); // note config file location for dedicated server is stored in the world config

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);






        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MPAObjects.ITEMS.register(modEventBus);
        MPAObjects.BLOCKS.register(modEventBus);
        MPAObjects.TILE_TYPES.register(modEventBus);
        MPAObjects.ENTITY_TYPES.register(modEventBus);
//        MPAObjects.CONTAINER_TYPES.register(modEventBus);
    }


    // preInit
    private void setup(final FMLCommonSetupEvent event) {
//        MPAPackets.registerMPAPackets();
        CraftingHelper.register(MPARecipeConditionFactory.Serializer.INSTANCE);
    }

}
