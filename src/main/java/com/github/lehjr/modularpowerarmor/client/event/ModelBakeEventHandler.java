package com.github.lehjr.modularpowerarmor.client.event;


import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.client.model.block.ModelLuxCapacitor;
import com.github.lehjr.modularpowerarmor.client.model.block.TinkerTableModel;
import com.github.lehjr.modularpowerarmor.client.model.helper.MPSModelHelper;
import com.github.lehjr.modularpowerarmor.client.model.item.ModelPowerFist;
import com.github.lehjr.mpalib.client.model.helper.ModelHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;


public enum ModelBakeEventHandler {
    INSTANCE;

    public static final ModelResourceLocation powerFistIconLocation = new ModelResourceLocation(MPARegistryNames.ITEM__POWER_FIST__REGNAME, "inventory");
    public static IBakedModel powerFistIconModel;
    private static Map<ResourceLocation, IBakedModel> modelRegistry;
    public ModelLuxCapacitor luxCapModel;

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        modelRegistry = event.getModelRegistry();
        modelRegistry.put(powerFistIconLocation, new ModelPowerFist(modelRegistry.get(powerFistIconLocation)));

        MPSModelHelper.loadArmorModels(null, event.getModelLoader());

        // Tinker Table Inventory Model
        IModel tinkertableUnbaked = ModelHelper.getModel(new ResourceLocation(MPAConstants.MOD_ID,
                "models/block/powerarmor_workbench.obj"));

        // Lux Capacitor Base Model
        IModel luxCapacitorBaseUnbaked = ModelHelper.getModel(new ResourceLocation(MPAConstants.MOD_ID,
        "models/block/luxcapacitor/luxcapacitor_base.obj"));

        // Lux Capacitor Lens Model
        IModel luxcapacitorLenseUnbaked = ModelHelper.getModel(new ResourceLocation(MPAConstants.MOD_ID,
        "models/block/luxcapacitor/luxcapacitor_lens.obj"));


        modelRegistry.put(
                new ModelResourceLocation(MPARegistryNames.TINKER_TABLE_REG_NAME, "inventory"),
                new TinkerTableModel(tinkertableUnbaked.bake(
                        event.getModelLoader(),
                        ModelHelper.defaultTextureGetter(),
                        ModelRotation.X0_Y0, DefaultVertexFormats.ITEM)));

        modelRegistry.put(
                new ModelResourceLocation(MPARegistryNames.LUX_CAPACITOR_REG_NAME, "inventory"),
                new ModelLuxCapacitor(luxCapacitorBaseUnbaked.bake(
                        event.getModelLoader(),
                        ModelHelper.defaultTextureGetter(),
                        ModelRotation.X0_Y0, DefaultVertexFormats.ITEM),
                        luxcapacitorLenseUnbaked.bake(
                                event.getModelLoader(),
                                ModelHelper.defaultTextureGetter(),
                                ModelRotation.X0_Y0, DefaultVertexFormats.ITEM)));

        luxCapModel = new ModelLuxCapacitor(luxCapacitorBaseUnbaked.bake(
                event.getModelLoader(),
                ModelHelper.defaultTextureGetter(),
                TRSRTransformation.getRotation(Direction.DOWN),
                DefaultVertexFormats.ITEM),
                luxcapacitorLenseUnbaked.bake(
                        event.getModelLoader(),
                        ModelHelper.defaultTextureGetter(),
                        TRSRTransformation.getRotation(Direction.DOWN), DefaultVertexFormats.ITEM));


        for (Direction facing : Direction.values()) {
            modelRegistry.put(
                    new ModelResourceLocation(MPARegistryNames.LUX_CAPACITOR_REG_NAME, "facing=" + facing.getName()),
                    new ModelLuxCapacitor(luxCapacitorBaseUnbaked.bake(
                            event.getModelLoader(),
                            ModelHelper.defaultTextureGetter(),
                            TRSRTransformation.getRotation(facing), DefaultVertexFormats.ITEM),
                            luxcapacitorLenseUnbaked.bake(
                                    event.getModelLoader(),
                                    ModelHelper.defaultTextureGetter(),
                                    TRSRTransformation.getRotation(facing), DefaultVertexFormats.ITEM)));

            if (facing.equals(Direction.DOWN) || facing.equals(Direction.UP)) {
                continue;
            }

            modelRegistry.put(new ModelResourceLocation(MPARegistryNames.TINKER_TABLE_REG_NAME, "facing=" + facing.getName()),
                    new TinkerTableModel(tinkertableUnbaked.bake(
                            event.getModelLoader(),
                            ModelHelper.defaultTextureGetter(),
                            TRSRTransformation.getRotation(facing), DefaultVertexFormats.ITEM)));
        }
    }
}