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

package com.github.machinemuse.powersuits.utils.modulehelpers;

import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by leon on 4/9/17.
 */
public class PlasmaCannonHelper {
    public static int getPlayerPlasma(EntityPlayer player) {
        ItemStack powerfist = player.getHeldItemMainhand();
        int actualCount;

        if (!powerfist.isEmpty() && player.isHandActive()) {
            if (ModuleManager.INSTANCE.itemHasActiveModule(powerfist, MPSModuleConstants.MODULE_PLASMA_CANNON__DATANAME)) {
                actualCount = (72000 - player.getItemInUseCount());
                return (actualCount > 50 ? 50 : actualCount) * 2;
            } else if (ModuleManager.INSTANCE.itemHasActiveModule(powerfist, MPSModuleConstants.MODULE_ORE_SCANNER__DATANAME)) {
                actualCount = (72000 - player.getItemInUseCount());
                return (int) ((actualCount > 40 ? 40 : actualCount) * 2.5);
            }
        }
        return 0;
    }

    public static int getMaxPlasma(EntityPlayer player) {
        ItemStack powerfist = player.getHeldItemMainhand();
        if (!powerfist.isEmpty() && player.isHandActive() &&
                ((ModuleManager.INSTANCE.itemHasActiveModule(powerfist, MPSModuleConstants.MODULE_PLASMA_CANNON__DATANAME)) ||
                        (ModuleManager.INSTANCE.itemHasActiveModule(powerfist, MPSModuleConstants.MODULE_ORE_SCANNER__DATANAME)))) {
            return 100;
        }
        return 0;
    }
}