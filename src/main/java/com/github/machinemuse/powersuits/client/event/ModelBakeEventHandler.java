/*
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.client.event;

import com.github.machinemuse.powersuits.client.model.block.ModelLuxCapacitor;
import com.github.machinemuse.powersuits.client.model.helper.MPSModelHelper;
import com.github.machinemuse.powersuits.client.model.item.ModelPowerFist;
import com.github.machinemuse.powersuits.basemod.MPSItems;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;


/**
 * Ported to Java by lehjr on 12/22/16.
 */
@SideOnly(Side.CLIENT)
public enum ModelBakeEventHandler {
    INSTANCE;

    public static final ModelResourceLocation powerFistIconLocation = new ModelResourceLocation(MPSItems.INSTANCE.powerFistRegName, "inventory");
    public static IBakedModel powerFistIconModel;
    private static IRegistry<ModelResourceLocation, IBakedModel> modelRegistry;

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
    }
}
