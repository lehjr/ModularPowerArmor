package com.github.lehjr.modularpowerarmor.client.event;


import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.client.model.block.LuxCapacitorModelWrapper;
import com.github.lehjr.modularpowerarmor.client.model.helper.MPAModelHelper;
import com.github.lehjr.modularpowerarmor.client.model.item.PowerFistModel;
import forge.OBJBakedCompositeModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public enum ModelBakeEventHandler {
    INSTANCE;

    ModelResourceLocation luxCapItemLocation = new ModelResourceLocation(new ResourceLocation(MPAConstants.MOD_ID, MPARegistryNames.LUX_CAPACITOR), "inventory");
    ModelResourceLocation luxCapModuleLocation = new ModelResourceLocation(new ResourceLocation(MPAConstants.MOD_ID, MPARegistryNames.LUX_CAPACITOR_MODULE), "inventory");

    public static final ModelResourceLocation powerFistIconLocation = new ModelResourceLocation(new ResourceLocation(MPAConstants.MOD_ID, MPARegistryNames.POWER_FIST), "inventory");

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        /**
         * Notes: looks like all current models are "SimpleBakedModels"
         */
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

        MPAModelHelper.loadArmorModels(null, event.getModelLoader());
    }
}