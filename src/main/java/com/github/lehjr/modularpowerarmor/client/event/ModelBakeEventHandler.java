package com.github.lehjr.modularpowerarmor.client.event;


import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.client.model.block.LuxCapacitorModelWrapper;
import com.github.lehjr.modularpowerarmor.client.model.helper.MPSModelHelper;
import com.github.lehjr.modularpowerarmor.client.model.item.PowerFistModel;
import forge.OBJBakedCompositeModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;


public enum ModelBakeEventHandler {
    INSTANCE;

    ModelResourceLocation luxCapItemLocation = new ModelResourceLocation(MPARegistryNames.LUX_CAPACITOR_REG_NAME, "inventory");
    ModelResourceLocation luxCapModuleLocation = new ModelResourceLocation(MPARegistryNames.MODULE_LUX_CAPACITOR__REGNAME, "inventory");

    public static final ModelResourceLocation powerFistIconLocation = new ModelResourceLocation(MPARegistryNames.ITEM__POWER_FIST__REGNAME, "inventory");

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        /**
         * Notes: looks like all current models are "SimpleBakedModels"
         */



        boolean found = false;




        for (ResourceLocation location : event.getModelRegistry().keySet()) {
            if (location.getNamespace().equals(MPAConstants.MOD_ID)) {
//                System.out.println("location: " + location);
//                System.out.println("class: " + event.getModelRegistry().get(location).getClass());
//                System.out.println("texture location: " + event.getModelRegistry().get(location).getParticleTexture().toString());
//                System.out.println("atlas location: " + event.getModelRegistry().get(location).getParticleTexture().getAtlasTexture().getTextureLocation());

            }
        }

        // replace LuxCapacitor model with one that can generate the model data needed to color the lens for the item model
        IBakedModel luxCapItemModel = event.getModelRegistry().get(luxCapItemLocation);
        if (luxCapItemModel instanceof OBJBakedCompositeModel) {
            event.getModelRegistry().put(luxCapItemLocation, new LuxCapacitorModelWrapper((OBJBakedCompositeModel) luxCapItemModel));
        }

        IBakedModel luxCapModuleModel = event.getModelRegistry().get(luxCapModuleLocation);
        if (luxCapItemModel instanceof OBJBakedCompositeModel) {
            event.getModelRegistry().put(luxCapModuleLocation, new LuxCapacitorModelWrapper((OBJBakedCompositeModel) luxCapModuleModel));
        }

        IBakedModel powerFistIcon = event.getModelRegistry().get(powerFistIconLocation);
        if (luxCapItemModel instanceof OBJBakedCompositeModel) {
            event.getModelRegistry().put(powerFistIconLocation, new PowerFistModel(powerFistIcon));
        }

        MPSModelHelper.loadArmorModels(null, event.getModelLoader());
//        event.getModelLoader().defaultTextureGetter();




//        modelRegistry = event.getModelRegistry();
//        modelRegistry.put(powerFistIconLocation, new ModelPowerFist(modelRegistry.get(powerFistIconLocation)));
//
//
//
//        // Lux Capacitor Base Model
//        IModel luxCapacitorBaseUnbaked = ModelHelper.getModel(new ResourceLocation(MPAConstants.MOD_ID,
//        "models/block/luxcapacitor/luxcapacitor_base.obj"));
//
//        // Lux Capacitor Lens Model
//        IModel luxcapacitorLenseUnbaked = ModelHelper.getModel(new ResourceLocation(MPAConstants.MOD_ID,
//        "models/block/luxcapacitor/luxcapacitor_lens.obj"));
//
//
//        modelRegistry.put(
//                new ModelResourceLocation(MPARegistryNames.LUX_CAPACITOR_REG_NAME, "inventory"),
//                new ModelLuxCapacitor(luxCapacitorBaseUnbaked.bake(
//                        event.getModelLoader(),
//                        ModelHelper.defaultTextureGetter(),
//                        ModelRotation.X0_Y0, DefaultVertexFormats.ITEM),
//                        luxcapacitorLenseUnbaked.bake(
//                                event.getModelLoader(),
//                                ModelHelper.defaultTextureGetter(),
//                                ModelRotation.X0_Y0, DefaultVertexFormats.ITEM)));
//
//        modelRegistry.put(
//                new ModelResourceLocation(MPARegistryNames.MODULE_LUX_CAPACITOR__REGNAME, "inventory"),
//                new ModelLuxCapacitor(luxCapacitorBaseUnbaked.bake(
//                        event.getModelLoader(),
//                        ModelHelper.defaultTextureGetter(),
//                        ModelRotation.X0_Y0, DefaultVertexFormats.ITEM),
//                        luxcapacitorLenseUnbaked.bake(
//                                event.getModelLoader(),
//                                ModelHelper.defaultTextureGetter(),
//                                ModelRotation.X0_Y0, DefaultVertexFormats.ITEM)));
//
//        luxCapModel = new ModelLuxCapacitor(luxCapacitorBaseUnbaked.bake(
//                event.getModelLoader(),
//                ModelHelper.defaultTextureGetter(),
//                TransformationMatrix.getRotation(Direction.DOWN),
//                DefaultVertexFormats.ITEM),
//                luxcapacitorLenseUnbaked.bake(
//                        event.getModelLoader(),
//                        ModelHelper.defaultTextureGetter(),
//                        TransformationMatrix.getRotation(Direction.DOWN), DefaultVertexFormats.ITEM));
//
//
//        for (Direction facing : Direction.values()) {
//            modelRegistry.put(
//                    new ModelResourceLocation(MPARegistryNames.LUX_CAPACITOR_REG_NAME, "facing=" + facing.getName()),
//                    new ModelLuxCapacitor(luxCapacitorBaseUnbaked.bake(
//                            event.getModelLoader(),
//                            ModelHelper.defaultTextureGetter(),
//                            TransformationMatrix.getRotation(facing), DefaultVertexFormats.ITEM),
//                            luxcapacitorLenseUnbaked.bake(
//                                    event.getModelLoader(),
//                                    ModelHelper.defaultTextureGetter(),
//                                    TransformationMatrix.getRotation(facing), DefaultVertexFormats.ITEM)));
//        }
    }
}