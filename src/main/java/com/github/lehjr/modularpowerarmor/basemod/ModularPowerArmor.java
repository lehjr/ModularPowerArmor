package com.github.lehjr.modularpowerarmor.basemod;

import com.github.lehjr.modularpowerarmor.client.control.KeybindKeyHandler;
import com.github.lehjr.modularpowerarmor.client.event.ClientTickHandler;
import com.github.lehjr.modularpowerarmor.client.event.ModelBakeEventHandler;
import com.github.lehjr.modularpowerarmor.client.event.RenderEventHandler;
import com.github.lehjr.modularpowerarmor.client.gui.modding.module.MPAWorkbenchGui;
import com.github.lehjr.modularpowerarmor.client.render.entity.LuxCapacitorEntityRenderer;
import com.github.lehjr.modularpowerarmor.client.render.entity.PlasmaBoltEntityRenderer;
import com.github.lehjr.modularpowerarmor.client.render.entity.SpinningBladeEntityRenderer;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.event.HarvestEventHandler;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.event.PlayerUpdateHandler;
import com.github.lehjr.modularpowerarmor.network.MPAPackets;
import com.github.lehjr.modularpowerarmor.recipe.MPARecipeConditionFactory;
import com.github.lehjr.mpalib.config.ConfigHelper;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.module.toggleable.IToggleableModule;
import com.github.lehjr.mpalib.util.capabilities.module.toggleable.ToggleableModule;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(MPAConstants.MOD_ID)
public class ModularPowerArmor {
    public ModularPowerArmor() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, MPASettings.CLIENT_SPEC, ConfigHelper.setupConfigFile("mpa-client-only.toml", MPAConstants.MOD_ID).getAbsolutePath());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, MPASettings.SERVER_SPEC); // note config file location for dedicated server is stored in the world config

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
//        modEventBus.register(this);

        // Register the setup method for modloading
        modEventBus.addListener(this::setup);

        // Register the doClientStuff method for modloading
        modEventBus.addListener(this::setupClient);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

//        MinecraftForge.EVENT_BUS.addListener(PlayerLoginHandler::onPlayerLogin);
//        MinecraftForge.EVENT_BUS.addListener(EntityDamageEvent::handleEntityDamageEvent);
//        MinecraftForge.EVENT_BUS.addListener(EntityDamageEvent::entityAttackEventHandler);
        MinecraftForge.EVENT_BUS.register(new PlayerUpdateHandler());
        MinecraftForge.EVENT_BUS.register(MovementManager.INSTANCE);

        MinecraftForge.EVENT_BUS.addListener(HarvestEventHandler::handleHarvestCheck);
        MinecraftForge.EVENT_BUS.addListener(HarvestEventHandler::handleBreakSpeed);

        MPAObjects.ITEMS.register(modEventBus);
        MPAObjects.BLOCKS.register(modEventBus);
        MPAObjects.TILE_TYPES.register(modEventBus);
        MPAObjects.ENTITY_TYPES.register(modEventBus);
        MPAObjects.CONTAINER_TYPES.register(modEventBus);

        // handles loading and reloading event
        modEventBus.addListener((ModConfig.ModConfigEvent event) -> {
            new RuntimeException("Got config " + event.getConfig() + " name " + event.getConfig().getModId() + ":" + event.getConfig().getFileName());

            final ModConfig config = event.getConfig();
            if (config.getSpec() == MPASettings.SERVER_SPEC) {
                MPASettings.getModuleConfig().setServerConfig(config);
                CosmeticPresetSaveLoad.setConfigDirString(config.getFullPath().getParent().toString());
                CosmeticPresetSaveLoad.copyPresetsFromJar(config.getFullPath().getParent().toString());
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


        //        RenderingRegistry.registerEntityRenderingHandler(BoltEntity.class, BoltEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MPAObjects.LUX_CAPACITOR_ENTITY_TYPE.get(), LuxCapacitorEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MPAObjects.PLASMA_BOLT_ENTITY_TYPE.get(), PlasmaBoltEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MPAObjects.SPINNING_BLADE_ENTITY_TYPE.get(), SpinningBladeEntityRenderer::new);

//        ScreenManager.registerFactory(MPSObjects.MODULE_CONFIG_CONTAINER_TYPE, TinkerModuleGui::new);
//        ScreenManager.registerFactory(MPAObjects.MPA_CRAFTING_CONTAINER_TYPE.get(), TinkerCraftingGUI::new);
        ScreenManager.registerFactory(MPAObjects.MPA_WORKBENCH_CONTAINER_TYPE.get(), MPAWorkbenchGui::new);

/*
 <T extends TileEntity> void bindTileEntityRenderer(TileEntityType<T> tileEntityType,
            Function<? super TileEntityRendererDispatcher, ? extends TileEntityRenderer<? super T>> rendererFactory)

 */
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        if (!event.getCapabilities().containsKey(MPARegistryNames.CLOCK_MODULE_REG) &&
                event.getObject().getItem().equals(Items.CLOCK)) {
            final ItemStack stack = event.getObject();

            IToggleableModule clock = new ToggleableModule(stack, EnumModuleCategory.SPECIAL, EnumModuleTarget.HEADONLY, MPASettings::getModuleConfig, true);
            event.addCapability(MPARegistryNames.CLOCK_MODULE_REG, new ICapabilityProvider() {
                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    if (cap == PowerModuleCapability.POWER_MODULE) {
                        return LazyOptional.of(()->(T)clock);
                    }
                    return LazyOptional.empty();
                }
            });
        } else if (!event.getCapabilities().containsKey(MPARegistryNames.COMPASS_MODULE_REG) &&
                event.getObject().getItem().equals(Items.COMPASS)) {
            final ItemStack stack = event.getObject();
            IToggleableModule compass = new ToggleableModule(stack, EnumModuleCategory.SPECIAL, EnumModuleTarget.HEADONLY, MPASettings::getModuleConfig, true);

            event.addCapability(MPARegistryNames.COMPASS_MODULE_REG, new ICapabilityProvider() {
                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    if (cap == PowerModuleCapability.POWER_MODULE) {
                        return LazyOptional.of(()->(T)compass);
                    }
                    return LazyOptional.empty();
                }
            });
        }
    }
}