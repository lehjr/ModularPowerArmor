package com.github.lehjr.modularpowerarmor.basemod;

import com.github.lehjr.forge.obj.MPALibOBJLoader;
import com.github.lehjr.modularpowerarmor.basemod.config.ClientConfig;
import com.github.lehjr.modularpowerarmor.basemod.config.CommonConfig;
import com.github.lehjr.modularpowerarmor.basemod.config.ConfigHelper;
import com.github.lehjr.modularpowerarmor.client.control.KeybindKeyHandler;
import com.github.lehjr.modularpowerarmor.client.event.ClientTickHandler;
import com.github.lehjr.modularpowerarmor.client.event.ModelBakeEventHandler;
import com.github.lehjr.modularpowerarmor.client.event.RenderEventHandler;
import com.github.lehjr.modularpowerarmor.client.gui.crafting.TinkerCraftingGUI;
import com.github.lehjr.modularpowerarmor.client.gui.tinker.module.TinkerTableGui;
import com.github.lehjr.modularpowerarmor.client.render.entity.BoltEntityRenderer;
import com.github.lehjr.modularpowerarmor.client.render.entity.LuxCapacitorEntityRenderer;
import com.github.lehjr.modularpowerarmor.client.render.entity.PlasmaBoltEntityRenderer;
import com.github.lehjr.modularpowerarmor.client.render.entity.SpinningBladeEntityRenderer;
import com.github.lehjr.modularpowerarmor.entity.BoltEntity;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.modularpowerarmor.entity.PlasmaBoltEntity;
import com.github.lehjr.modularpowerarmor.entity.SpinningBladeEntity;
import com.github.lehjr.modularpowerarmor.event.*;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.recipe.MPARecipeConditionFactory;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MPAConstants.MOD_ID)
public class ModularPowerArmor {
    public ModularPowerArmor() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_SPEC, ConfigHelper.setupConfigFile("modularpowerarmor-common.toml").getAbsolutePath());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_SPEC, ClientConfig.clientFile.getAbsolutePath());

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the setupClient method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);

        // Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);

        modEventBus.addGenericListener(Block.class, RegisterStuff.INSTANCE::registerBlocks);
        modEventBus.addGenericListener(Item.class, RegisterStuff.INSTANCE::registerItems);
        modEventBus.addGenericListener(TileEntityType.class, RegisterStuff.INSTANCE::registerTileEntities);
        modEventBus.addGenericListener(EntityType.class, RegisterStuff.INSTANCE::registerEntities);
        modEventBus.addGenericListener(ContainerType.class, RegisterStuff.INSTANCE::registerContainerTypes);

        MinecraftForge.EVENT_BUS.addListener(PlayerLoginHandler::onPlayerLogin);
        MinecraftForge.EVENT_BUS.addListener(EntityDamageEvent::handleEntityDamageEvent);

        MinecraftForge.EVENT_BUS.addListener(HarvestEventHandler::handleHarvestCheck);
        MinecraftForge.EVENT_BUS.addListener(HarvestEventHandler::handleBreakSpeed);
//        MinecraftForge.EVENT_BUS.addListener(HarvestEventHandler::handHarvestDrops);
//        MinecraftForge.EVENT_BUS.addListener(HarvestEventHandler::handleBlockBreak);


        modEventBus.addListener((ModConfig.ModConfigEvent event) -> {
            new RuntimeException("Got config " + event.getConfig() + " name " + event.getConfig().getModId() + ":" + event.getConfig().getFileName());
            final ModConfig config = event.getConfig();
            if (config.getSpec() == ClientConfig.CLIENT_SPEC) {
//
            } else if (config.getSpec() == CommonConfig.COMMON_SPEC) {
                CommonConfig.commonConfig = config;
                CommonConfig.setLoadingDone();
                CommonConfig.finishBuilder();
                CosmeticPresetSaveLoad.copyPresetsFromJar();
            }
        });
    }

    // preInit
    private void setup(final FMLCommonSetupEvent event) {
        MPAPackets.registerMPAPackets();
        CraftingHelper.register(MPARecipeConditionFactory.Serializer.INSTANCE);
    }

//    @SubscribeEvent
//    public void setupRecipeConditionHandler(RegistryEvent.Register<IRecipeSerializer<?>> event) {
////        CraftingHelper.register(MPARecipeConditionFactory.Serializer.INSTANCE);
//    }

    // client preInit
    private void setupClient(final FMLClientSetupEvent event) {
        MPALibOBJLoader.INSTANCE.addDomain(MPAConstants.MOD_ID.toLowerCase());
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(ModelBakeEventHandler.INSTANCE::onModelBake);
        modEventBus.addListener(RenderEventHandler.INSTANCE::preTextureStitch);

        MinecraftForge.EVENT_BUS.register(RenderEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new KeybindKeyHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerUpdateHandler());

        RenderingRegistry.registerEntityRenderingHandler(BoltEntity.class, BoltEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(LuxCapacitorEntity.class, LuxCapacitorEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(PlasmaBoltEntity.class, PlasmaBoltEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SpinningBladeEntity.class, SpinningBladeEntityRenderer::new);

//        ScreenManager.registerFactory(MPSObjects.MODULE_CONFIG_CONTAINER_TYPE, TinkerModuleGui::new);
        ScreenManager.registerFactory(MPAObjects.MPS_CRAFTING_CONTAINER_TYPE, TinkerCraftingGUI::new);
        ScreenManager.registerFactory(MPAObjects.TINKER_TABLE_CONTAINER_TYPE, TinkerTableGui::new);
    }

//    // You can use EventBusSubscriber to automatically subscribe events on the contained class
//    @Mod.EventBusSubscriber
//    public static class ServerEvents {
//        @SubscribeEvent
//        public static void onServerStarting(FMLServerStartingEvent event) {
//            // do something when the server starts
////            MPALibLogger.logInfo("HELLO from server starting");
//        }
//    }
}