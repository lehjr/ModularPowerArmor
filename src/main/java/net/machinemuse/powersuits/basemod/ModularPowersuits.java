package net.machinemuse.powersuits.basemod;

import net.machinemuse.numina.basemod.MuseLogger;
import net.machinemuse.numina.client.model.obj.MuseOBJLoader;
import net.machinemuse.powersuits.client.event.ClientTickHandler;
import net.machinemuse.powersuits.client.event.ModelBakeEventHandler;
import net.machinemuse.powersuits.client.event.RenderEventHandler;
import net.machinemuse.powersuits.client.render.entity.EntityRendererLuxCapacitorEntity;
import net.machinemuse.powersuits.client.render.entity.EntityRendererPlasmaBolt;
import net.machinemuse.powersuits.client.render.entity.EntityRendererSpinningBlade;
import net.machinemuse.powersuits.entity.LuxCapacitorEntity;
import net.machinemuse.powersuits.entity.PlasmaBoltEntity;
import net.machinemuse.powersuits.entity.SpinningBladeEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, MPSConfig.INSTANCE.SERVER_SPEC, MPSConfig.INSTANCE.serverFile.getAbsolutePath());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, MPSConfig.INSTANCE.CLIENT_SPEC, MPSConfig.INSTANCE.clientFile.getAbsolutePath());

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the setupClient method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);

        // Register ourselves for server, registry and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addGenericListener(Block.class, MPSItems.INSTANCE::registerBlocks);
        modEventBus.addGenericListener(Item.class, MPSItems.INSTANCE::registerItems);
        modEventBus.addGenericListener(TileEntityType.class, MPSItems.INSTANCE::registerTileEntities);
        modEventBus.addGenericListener(EntityType.class, MPSItems.INSTANCE::registerEntities);
        modEventBus.addGenericListener(ContainerType.class, MPSItems.INSTANCE::registerContainerTypes);

        modEventBus.addListener(ModelBakeEventHandler.INSTANCE::onModelBake);
        modEventBus.addListener(RenderEventHandler.INSTANCE::preTextureStitch);

//        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> {
////            return (openContainer) -> {
////                ResourceLocation location = openContainer.getId();
//////                if (location.equals(some resource location here)) {
//////                    ClientPlayerEntity player = Minecraft.getInstance().player;
//////                        return new Gui with params;
//////                }
////                return null;
////            };
//        });
    }

    // preInit
    private void setup(final FMLCommonSetupEvent event) {
//        proxy.setup(event);
    }

    // client preInit
    private void setupClient(final FMLClientSetupEvent event) {
        MuseLogger.logger.info("doing something here: .... ");

        MuseOBJLoader.INSTANCE.addDomain(MPSConstants.MODID.toLowerCase());

//        OBJLoader.INSTANCE.addDomain(MPSConstants.MODID.toLowerCase());

//        MuseOBJLoader.INSTANCE.addDomain(MPSConstants.MODID.toLowerCase());
//
////        MinecraftForge.EVENT_BUS.register(ModelRegisterEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(RenderEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
//
//
        RenderingRegistry.registerEntityRenderingHandler(SpinningBladeEntity.class, EntityRendererSpinningBlade::new);
        RenderingRegistry.registerEntityRenderingHandler(PlasmaBoltEntity.class, EntityRendererPlasmaBolt::new);
        RenderingRegistry.registerEntityRenderingHandler(LuxCapacitorEntity.class, EntityRendererLuxCapacitorEntity::new);



//        proxy.setupClient(event);
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