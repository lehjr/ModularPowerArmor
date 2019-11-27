/*
 * Copyright (c) ${DATE} MachineMuse, Lehjr
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

package com.github.machinemuse.powersuits.recipe;

import com.github.lehjr.mpalib.misc.ModCompatibility;
import com.github.machinemuse.powersuits.config.MPSConfig;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

@SuppressWarnings("unused")
public class MPSRecipeConditionFactory implements IConditionFactory {
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        if (JsonUtils.hasField(json, "type")) {
            String key = JsonUtils.getString(json, "type");
            switch (key) {
                // Thermal Expansion
                case "powersuits:thermal_expansion_recipes_enabled":
                    return () -> MPSConfig.INSTANCE.useThermalExpansionRecipes();

                // EnderIO
                case "powersuits:enderio_recipes_enabled":
                    return () -> MPSConfig.INSTANCE.useEnderIORecipes();

                // Original recipe loading code set priority for TechReborn recipes instead of Gregtech or Industrialcraft
                // Tech Reborn
                case "powersuits:tech_reborn_recipes_enabled":
                    return () -> MPSConfig.INSTANCE.useTechRebornRecipes();

                // IC2
                case "powersuits:ic2_recipes_enabled":
                    return () -> (ModCompatibility.isIndustrialCraftExpLoaded() &&
                            /*!ModCompatibility.isGregTechLoaded() &&*/
                                    /* !ModCompatibility.isTechRebornLoaded()) */ MPSConfig.INSTANCE.useIC2Recipes());
                // IC2 Classic
                case "powersuits:ic2_classic_recipes_enabled":
                    return () -> (ModCompatibility.isIndustrialCraftClassicLoaded()&&
                            /*!ModCompatibility.isGregTechLoaded() &&*/
                            /* !ModCompatibility.isTechRebornLoaded()) */ MPSConfig.INSTANCE.useIC2Recipes());
                // Vanilla reciples only as fallback
                case "powersuits:vanilla_recipes_enabled":
                    return () -> (!(
                                    MPSConfig.INSTANCE.useThermalExpansionRecipes() ||
                                    MPSConfig.INSTANCE.useEnderIORecipes() ||
                                    (MPSConfig.INSTANCE.useIC2Recipes() && ModCompatibility.isIndustrialCraftLoaded()) ||
                                    MPSConfig.INSTANCE.useTechRebornRecipes()
                    ));
            }
        }
        return () -> false;
    }
}
