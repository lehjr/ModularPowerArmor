package com.github.lehjr.modularpowerarmor.basemod;

import com.github.lehjr.modularpowerarmor.client.control.KeybindKeyHandler;
import com.github.lehjr.modularpowerarmor.client.event.ClientTickHandler;
import com.github.lehjr.modularpowerarmor.client.event.ModelBakeEventHandler;
import com.github.lehjr.modularpowerarmor.client.event.RenderEventHandler;
import com.github.lehjr.modularpowerarmor.client.gui.crafting.TinkerCraftingGUI;
import com.github.lehjr.modularpowerarmor.client.gui.tinker.module.TinkerTableGui;
import com.github.lehjr.modularpowerarmor.client.renderer.entity.LuxCapacitorEntityRenderer;
import com.github.lehjr.modularpowerarmor.client.renderer.entity.PlasmaBoltEntityRenderer;
import com.github.lehjr.modularpowerarmor.client.renderer.entity.SpinningBladeEntityRenderer;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.config.ModuleConfig;
import com.github.lehjr.modularpowerarmor.event.*;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.recipe.MPARecipeConditionFactory;
import com.github.lehjr.mpalib.config.MPALibSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
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

// The value here should match an entry in the META-INF/mods.toml file
@Mod("modularpowerarmor")
public class ModularPowerArmor {
    public ModularPowerArmor() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, MPASettings.CLIENT_SPEC, MPASettings.clientFile.getAbsolutePath());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MPASettings.COMMON_SPEC, MPALibSettings.setupConfigFile("modularpowerarmor-common.toml", MPAConstants.MOD_ID).getAbsolutePath());
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);
        modEventBus.register(RegisterStuff.INSTANCE);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.addListener(PlayerLoginHandler::onPlayerLogin);
        MinecraftForge.EVENT_BUS.addListener(EntityDamageEvent::handleEntityDamageEvent);
        MinecraftForge.EVENT_BUS.addListener(EntityDamageEvent::entityAttackEventHandler);
        MinecraftForge.EVENT_BUS.register(new PlayerUpdateHandler());
        MinecraftForge.EVENT_BUS.register(MovementManager.INSTANCE);

        MinecraftForge.EVENT_BUS.addListener(HarvestEventHandler::handleHarvestCheck);
        MinecraftForge.EVENT_BUS.addListener(HarvestEventHandler::handleBreakSpeed);

        modEventBus.addListener((ModConfig.ModConfigEvent event) -> {
            new RuntimeException("Got config " + event.getConfig() + " name " + event.getConfig().getModId() + ":" + event.getConfig().getFileName());
            final ModConfig config = event.getConfig();
//            if (config.getSpec() == MPASettings.CLIENT_SPEC) {
//
//            } else
            if (config.getSpec() == MPASettings.COMMON_SPEC) {
                ModuleConfig moduleConfig = new ModuleConfig(config);
                moduleConfig.setLoadingDone();
                moduleConfig.finishBuilder();
                CosmeticPresetSaveLoad.setConfigDirString(config.getFullPath().getParent().toString());
                CosmeticPresetSaveLoad.copyPresetsFromJar(config.getFullPath().getParent().toString());
                MPASettings.setModConfig(moduleConfig);
            }
        });
    }

    // preInit
    private void setup(final FMLCommonSetupEvent event) {
        MPAPackets.registerMPAPackets();
        CraftingHelper.register(MPARecipeConditionFactory.Serializer.INSTANCE);
    }

    private void setupClient(final FMLClientSetupEvent event) {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(ModelBakeEventHandler.INSTANCE::onModelBake);
        modEventBus.addListener(RenderEventHandler.INSTANCE::preTextureStitch);

        MinecraftForge.EVENT_BUS.register(RenderEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
        MinecraftForge.EVENT_BUS.register(new KeybindKeyHandler());


        System.out.println("FIXME!!");
//        RenderingRegistry.registerEntityRenderingHandler(BoltEntity.class, BoltEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MPAObjects.LUX_CAPACITOR_ENTITY_TYPE, LuxCapacitorEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MPAObjects.PLASMA_BOLT_ENTITY_TYPE, PlasmaBoltEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MPAObjects.SPINNING_BLADE_ENTITY_TYPE, SpinningBladeEntityRenderer::new);


//        ScreenManager.registerFactory(MPSObjects.MODULE_CONFIG_CONTAINER_TYPE, TinkerModuleGui::new);
        ScreenManager.registerFactory(MPAObjects.MPA_CRAFTING_CONTAINER_TYPE, TinkerCraftingGUI::new);
        ScreenManager.registerFactory(MPAObjects.TINKER_TABLE_CONTAINER_TYPE, TinkerTableGui::new);

/*

 <T extends TileEntity> void bindTileEntityRenderer(TileEntityType<T> tileEntityType,
            Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>> rendererFactory)

 */




    }
}
