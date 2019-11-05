package com.github.lehjr.modularpowerarmor.utils.modulehelpers;

import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
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
            if (ModuleManager.INSTANCE.itemHasActiveModule(powerfist, ModuleConstants.MODULE_PLASMA_CANNON__DATANAME)) {
                actualCount = (72000 - player.getItemInUseCount());
                return (actualCount > 50 ? 50 : actualCount) * 2;
            } else if (ModuleManager.INSTANCE.itemHasActiveModule(powerfist, ModuleConstants.MODULE_ORE_SCANNER__DATANAME)) {
                actualCount = (72000 - player.getItemInUseCount());
                return (int) ((actualCount > 40 ? 40 : actualCount) * 2.5);
            }
        }
        return 0;
    }

    public static int getMaxPlasma(EntityPlayer player) {
        ItemStack powerfist = player.getHeldItemMainhand();
        if (!powerfist.isEmpty() && player.isHandActive() &&
                ((ModuleManager.INSTANCE.itemHasActiveModule(powerfist, ModuleConstants.MODULE_PLASMA_CANNON__DATANAME)) ||
                        (ModuleManager.INSTANCE.itemHasActiveModule(powerfist, ModuleConstants.MODULE_ORE_SCANNER__DATANAME)))) {
            return 100;
        }
        return 0;
    }
}