package com.github.lehjr.modularpowerarmor.client.event;

import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
import com.github.lehjr.modularpowerarmor.client.model.block.ModelLuxCapacitor;
import com.github.lehjr.modularpowerarmor.client.model.helper.MPSModelHelper;
import com.github.lehjr.modularpowerarmor.client.model.item.ModelPowerFist;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Ported to Java by lehjr on 12/22/16.
 */
@SideOnly(Side.CLIENT)
public enum ModelBakeEventHandler {
    INSTANCE;

    public static final ModelResourceLocation powerFistIconLocation = new ModelResourceLocation(new ResourceLocation(RegistryNames.ITEM__POWER_FIST__REGNAME), "inventory");
    public static IBakedModel powerFistIconModel;
    private static IRegistry<ModelResourceLocation, IBakedModel> modelRegistry;
    List<ModelResourceLocation> brokenModels = new ArrayList<>();



    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) throws IOException {
        modelRegistry = event.getModelRegistry();

        // New Lux Capacitor Model
        event.getModelRegistry().putObject(ModelLuxCapacitor.modelResourceLocation, new ModelLuxCapacitor());

        for (EnumFacing facing : EnumFacing.VALUES) {
            modelRegistry.putObject(ModelLuxCapacitor.getModelResourceLocation(facing), new ModelLuxCapacitor());
        }

        // Power Fist
        powerFistIconModel = modelRegistry.getObject(powerFistIconLocation);
        modelRegistry.putObject(powerFistIconLocation, new ModelPowerFist(powerFistIconModel));

        MPSModelHelper.loadArmorModels(null);
        for (ModelResourceLocation location : event.getModelRegistry().getKeys()) {
            if (event.getModelRegistry().getObject(location).equals(event.getModelManager().getMissingModel())) {
                brokenModels.add(location);
            }
        }
        for (ModelResourceLocation location: brokenModels) {
            System.out.println("missing model here: " + location);
        }
    }
}
