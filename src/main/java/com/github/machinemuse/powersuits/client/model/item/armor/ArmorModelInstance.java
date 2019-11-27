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

package com.github.machinemuse.powersuits.client.model.item.armor;

import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import net.minecraft.client.model.ModelBiped;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 10:01 PM, 11/07/13
 */
public class ArmorModelInstance {
    private static ModelBiped instance = null;

    public static ModelBiped getInstance() {
        if (instance == null) {
            if (ModCompatibility.isRenderPlayerAPILoaded()) {
                try {
                    MPALibLogger.logger.info("Attempting to load SmartMoving armor model.");
                    instance = Class.forName("com.github.machinemuse.powersuits.client.model.item.armor.SMovingArmorModel").asSubclass(ModelBiped.class).newInstance();
                    MPALibLogger.logger.info("SmartMoving armor model loaded successfully!");
                } catch (Exception e) {
                    MPALibLogger.logger.info("Smart Moving armor model did not loadButton successfully. Either Smart Moving is not installed, or there was another problem.");
                    instance = new HighPolyArmor();
                }
            }
            else {
                instance = new HighPolyArmor();
            }
        }
        return instance;
    }
}