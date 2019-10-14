package com.github.lehjr.modularpowerarmor.client.event;


import com.google.common.collect.ImmutableMap;
import com.github.lehjr.mpalib.client.model.helper.MuseModelHelper;
import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.client.model.block.ModelLuxCapacitor;
import com.github.lehjr.modularpowerarmor.client.model.block.TinkerTableModel;
import com.github.lehjr.modularpowerarmor.client.model.helper.MPSModelHelper;
import com.github.lehjr.modularpowerarmor.client.model.item.ModelPowerFist;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
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

        // New Lux Capacitor Inventory Model
        IModel tinkertableUnbaked = MuseModelHelper.getModel(new ResourceLocation(MPAConstants.MODID,
                "models/block/powerarmor_workbench.obj"));

        // new Tinker Table Inventory Model
        IModel luxCapacitorBaseUnbaked = MuseModelHelper.getModel(new ResourceLocation(MPAConstants.MODID,
        "models/block/luxcapacitor/luxcapacitor_base.obj"));

        IModel luxcapacitorLenseUnbaked = MuseModelHelper.getModel(new ResourceLocation(MPAConstants.MODID,
        "models/block/luxcapacitor/luxcapacitor_lens.obj"));

        modelRegistry.put(
                new ModelResourceLocation(MPARegistryNames.TINKER_TABLE_REG_NAME, "inventory"),
                new TinkerTableModel(tinkertableUnbaked.bake(
                        event.getModelLoader(),
                        MuseModelHelper.defaultTextureGetter(),
                        ModelRotation.X0_Y0, DefaultVertexFormats.ITEM)));

        modelRegistry.put(
                new ModelResourceLocation(MPARegistryNames.LUX_CAPACITOR_REG_NAME, "inventory"),
                new ModelLuxCapacitor(luxCapacitorBaseUnbaked.bake(
                        event.getModelLoader(),
                        MuseModelHelper.defaultTextureGetter(),
                        ModelRotation.X0_Y0, DefaultVertexFormats.ITEM),
                        luxcapacitorLenseUnbaked.bake(
                                event.getModelLoader(),
                                MuseModelHelper.defaultTextureGetter(),
                                ModelRotation.X0_Y0, DefaultVertexFormats.ITEM)));

        luxCapModel = new ModelLuxCapacitor(luxCapacitorBaseUnbaked.bake(
                event.getModelLoader(),
                MuseModelHelper.defaultTextureGetter(),
                TRSRTransformation.getRotation(Direction.DOWN),
                DefaultVertexFormats.ITEM),
                luxcapacitorLenseUnbaked.bake(
                        event.getModelLoader(),
                        MuseModelHelper.defaultTextureGetter(),
                        TRSRTransformation.getRotation(Direction.DOWN), DefaultVertexFormats.ITEM));


        for (Direction facing : Direction.values()) {
            modelRegistry.put(
                    new ModelResourceLocation(MPARegistryNames.LUX_CAPACITOR_REG_NAME, "facing=" + facing.getName()),
                    new ModelLuxCapacitor(luxCapacitorBaseUnbaked.bake(
                            event.getModelLoader(),
                            MuseModelHelper.defaultTextureGetter(),
                            TRSRTransformation.getRotation(facing), DefaultVertexFormats.ITEM),
                            luxcapacitorLenseUnbaked.bake(
                                    event.getModelLoader(),
                                    MuseModelHelper.defaultTextureGetter(),
                                    TRSRTransformation.getRotation(facing), DefaultVertexFormats.ITEM)));

            if (facing.equals(Direction.DOWN) || facing.equals(Direction.UP))
                continue;

//            MPALibLogger.logger.info("MPS model location: " + new ModelResourceLocation(
//                    MPSRegistryNames.TINKER_TABLE_REG_NAME, "facing=" + facing.getName()).toString());

            modelRegistry.put(new ModelResourceLocation(MPARegistryNames.TINKER_TABLE_REG_NAME, "facing=" + facing.getName()),

                    new TinkerTableModel(tinkertableUnbaked.bake(
                            event.getModelLoader(),
                            MuseModelHelper.defaultTextureGetter(),
                            TRSRTransformation.getRotation(facing), DefaultVertexFormats.ITEM)));
        }
    }

    public IModelState getModelState() {
        ImmutableMap.Builder<IModelPart, TRSRTransformation> builder = ImmutableMap.builder();

        // first person and third person models rotated to so that the side away from the player is the same as when it is placed
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND,
                MuseModelHelper.get(0, 0, 0, 0, 135, 0, 0.4f));

        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
                MuseModelHelper.get(0, 0, 0, 0, 135, 0, 0.4f));

        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                MuseModelHelper.get(0, 2.5f, 0, 75, -135, 0, 0.375f));

        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                MuseModelHelper.get(0, 2.5f, 0, 75, -135, 0, 0.375f));

        builder.put(ItemCameraTransforms.TransformType.GUI,
                MuseModelHelper.get(-0.0625F, 0.25F, 0, 30, 225, 0, 0.625f));

        builder.put(ItemCameraTransforms.TransformType.GROUND,
                MuseModelHelper.get(0, 3, 0, 0, 0, 0, 0.25f));

        builder.put(ItemCameraTransforms.TransformType.FIXED,
                MuseModelHelper.get(0, 0, 0, 0, 0, 0, 0.5f));

        return new SimpleModelState(builder.build());
    }
}