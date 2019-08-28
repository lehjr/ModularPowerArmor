package net.machinemuse.powersuits.basemod;

import net.machinemuse.numina.basemod.MuseLogger;
import net.machinemuse.numina.client.model.obj.MuseOBJLoader;
import net.machinemuse.powersuits.basemod.config.ClientConfig;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.basemod.config.ConfigHelper;
import net.machinemuse.powersuits.client.control.KeybindKeyHandler;
import net.machinemuse.powersuits.client.event.ClientTickHandler;
import net.machinemuse.powersuits.client.event.ModelBakeEventHandler;
import net.machinemuse.powersuits.client.event.RenderEventHandler;
import net.machinemuse.powersuits.client.gui.tinker.crafting.TinkerCraftingGUI;
import net.machinemuse.powersuits.client.gui.tinker.module.ModuleInstallRemoveGui;
import net.machinemuse.powersuits.client.render.entity.EntityRendererLuxCapacitorEntity;
import net.machinemuse.powersuits.client.render.entity.EntityRendererPlasmaBolt;
import net.machinemuse.powersuits.client.render.entity.EntityRendererSpinningBlade;
import net.machinemuse.powersuits.entity.LuxCapacitorEntity;
import net.machinemuse.powersuits.entity.PlasmaBoltEntity;
import net.machinemuse.powersuits.entity.SpinningBladeEntity;
import net.machinemuse.powersuits.event.PlayerUpdateHandler;
import net.machinemuse.powersuits.event.RegisterStuff;
import net.machinemuse.powersuits.network.MPSPackets;
import net.machinemuse.powersuits.recipe.MPSRecipeConditionFactory;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MPSConstants.MODID)
public class ModularPowersuits {
    public ModularPowersuits() {
        // TODO: revisit and see if a server config is needed too (not that server config only initializes with a server running and is stored with the saved world

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_SPEC, ConfigHelper.setupConfigFile("powersuits-common.toml").getAbsolutePath());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_SPEC, ClientConfig.clientFile.getAbsolutePath());

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the setupClient method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);

        // Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addGenericListener(Block.class, RegisterStuff.INSTANCE::registerBlocks);
        modEventBus.addGenericListener(Item.class, RegisterStuff.INSTANCE::registerItems);
        modEventBus.addGenericListener(TileEntityType.class, RegisterStuff.INSTANCE::registerTileEntities);
        modEventBus.addGenericListener(EntityType.class, RegisterStuff.INSTANCE::registerEntities);
        modEventBus.addGenericListener(ContainerType.class, RegisterStuff.INSTANCE::registerContainerTypes);

        modEventBus.addListener(ModelBakeEventHandler.INSTANCE::onModelBake);
        modEventBus.addListener(RenderEventHandler.INSTANCE::preTextureStitch);

        modEventBus.addListener((ModConfig.ModConfigEvent event) -> {
            new RuntimeException("Got config " + event.getConfig() + " name " + event.getConfig().getModId() + ":" + event.getConfig().getFileName());
            final ModConfig config = event.getConfig();
                if (config.getSpec() == ClientConfig.CLIENT_SPEC) {
//
                } else if (config.getSpec() == CommonConfig.COMMON_SPEC) {
                    CommonConfig.commonConfig = config;
                    CommonConfig.setLoadingDone();
                    CommonConfig.finishBuilder();
                }
        });
    }

    // preInit
    private void setup(final FMLCommonSetupEvent event) {
        MPSPackets.registerMPSPackets();

        registerCraftingCondition("thermal_expansion_recipes_enabled");
        registerCraftingCondition("enderio_recipes_enabled");
        registerCraftingCondition("tech_reborn_recipes_enabled");
        registerCraftingCondition("ic2_recipes_enabled");
        registerCraftingCondition("vanilla_recipes_enabled");
    }

    private void registerCraftingCondition(String conditionName) {
        ResourceLocation res = new ResourceLocation(MPSConstants.MODID, conditionName);
        CraftingHelper.register(res, new MPSRecipeConditionFactory(res));
    }


    // client preInit
    private void setupClient(final FMLClientSetupEvent event) {
        MuseLogger.logger.info("doing something here: .... ");

        MuseOBJLoader.INSTANCE.addDomain(MPSConstants.MODID.toLowerCase());
////        MinecraftForge.EVENT_BUS.register(ModelRegisterEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(RenderEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new KeybindKeyHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerUpdateHandler());


        RenderingRegistry.registerEntityRenderingHandler(SpinningBladeEntity.class, EntityRendererSpinningBlade::new);
        RenderingRegistry.registerEntityRenderingHandler(PlasmaBoltEntity.class, EntityRendererPlasmaBolt::new);
        RenderingRegistry.registerEntityRenderingHandler(LuxCapacitorEntity.class, EntityRendererLuxCapacitorEntity::new);

//        ScreenManager.registerFactory(MPSObjects.MODULE_CONFIG_CONTAINER_TYPE, TinkerModuleGui::new);
        ScreenManager.registerFactory(MPSObjects.MPS_CRAFTING_CONTAINER_TYPE, TinkerCraftingGUI::new);
        ScreenManager.registerFactory(MPSObjects.MODULAR_ITEM_CONTAINER_CONTAINER_TYPE, ModuleInstallRemoveGui::new);


    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class
    @Mod.EventBusSubscriber
    public static class ServerEvents {
        @SubscribeEvent
        public static void onServerStarting(FMLServerStartingEvent event) {
            // do something when the server starts
//            MuseLogger.logInfo("HELLO from server starting");
        }
    }
}