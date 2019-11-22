package com.github.lehjr.modularpowerarmor.client.event;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.Objects;
import com.github.lehjr.modularpowerarmor.client.model.block.ModelLuxCapacitor;
import com.github.lehjr.modularpowerarmor.client.render.entity.EntityRendererLuxCapacitorEntity;
import com.github.lehjr.modularpowerarmor.client.render.entity.EntityRendererPlasmaBolt;
import com.github.lehjr.modularpowerarmor.client.render.entity.EntityRendererSpinningBlade;
import com.github.lehjr.modularpowerarmor.entity.LuxCapacitorEntity;
import com.github.lehjr.modularpowerarmor.entity.PlasmaBoltEntity;
import com.github.lehjr.modularpowerarmor.entity.SpinningBladeEntity;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Map;

public class EventRegisterRenderers {
    @SubscribeEvent
    public void registerRenderers(ModelRegistryEvent event) {
        Objects mpaItems = Objects.INSTANCE;

        // PowerFist
        regRenderer(mpaItems.powerFist);

        // Armor
        regRenderer(mpaItems.powerArmorHead);
        regRenderer(mpaItems.powerArmorTorso);
        regRenderer(mpaItems.powerArmorLegs);
        regRenderer(mpaItems.powerArmorFeet);

        // Tinker Table
        regRenderer(Item.getItemFromBlock(mpaItems.tinkerTable));

        // Lux Capacitor
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(mpaItems.luxCapacitor), 0, ModelLuxCapacitor.modelResourceLocation);

        // FIXME
        // Components
        Item components = mpaItems.component;
        if (components != null) {
            for (Integer meta : ((ItemComponent) components).names.keySet()) {
                String oredictName = ((ItemComponent) components).names.get(meta);
                ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Constants.COMPONENTS_PREFIX + oredictName, "inventory");
                ModelLoader.setCustomModelResourceLocation(components, meta, itemModelResourceLocation);
            }
        }

        for (Map.Entry<ResourceLocation, Item> entry: ForgeRegistries.ITEMS.getEntries()) {
            if (entry.getKey().getNamespace().equalsIgnoreCase(Constants.MODID) && entry.getValue().getTranslationKey().startsWith("item.module." + Constants.MODID)) {
                String location = Constants.RESOURCE_PREFIX + "module/" + entry.getKey().getPath();
                ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(location, "inventory");
                ModelLoader.setCustomModelResourceLocation(entry.getValue(), 0, itemModelResourceLocation);
            }
        }

        RenderingRegistry.registerEntityRenderingHandler(SpinningBladeEntity.class, EntityRendererSpinningBlade::new);
        RenderingRegistry.registerEntityRenderingHandler(PlasmaBoltEntity.class, EntityRendererPlasmaBolt::new);
        RenderingRegistry.registerEntityRenderingHandler(LuxCapacitorEntity.class, EntityRendererLuxCapacitorEntity::new);

        ModelResourceLocation liquid_nitrogen_location = new ModelResourceLocation(mpaItems.blockLiquidNitrogen.getRegistryName(), "normal");
        Item fluid = Item.getItemFromBlock(mpaItems.blockLiquidNitrogen);

        ModelBakery.registerItemVariants(fluid);
        ModelLoader.setCustomMeshDefinition(fluid, stack -> liquid_nitrogen_location);
        ModelLoader.setCustomStateMapper(mpaItems.blockLiquidNitrogen, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return liquid_nitrogen_location;
            }
        });
    }

//
//    ModelResourceLocation fluidLocation = new ModelResourceLocation(MODID.toLowerCase() + ":" + FiniteFluidBlock.id, "normal");
//
//    Item fluid = Item.getItemFromBlock(FiniteFluidBlock.instance);
//            ModelLoader.setCustomModelResourceLocation(EmptyFluidContainer.instance, 0, new ModelResourceLocation("forge:bucket", "inventory"));
//            ModelLoader.setBucketModelDefinition(FluidContainer.instance);
//    // no need to pass the locations here, since they'll be loaded by the block model logic.
//            ModelBakery.registerItemVariants(fluid);
//            ModelLoader.setCustomMeshDefinition(fluid, stack -> fluidLocation);
//            ModelLoader.setCustomStateMapper(MPSItems.INSANCE., new StateMapperBase() {
//        @Override
//        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
//            return fluidLocation;
//        }
//    });


    private void regRenderer(Item item) {
        ModelResourceLocation location = new ModelResourceLocation(item.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, location);
    }
}