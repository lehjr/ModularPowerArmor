package com.github.machinemuse.powersuits.item;

import com.github.lehjr.mpalib.legacy.energy.IElectricItem;
import com.github.lehjr.mpalib.legacy.item.IModularItem;
import com.github.lehjr.mpalib.legacy.module.IModuleManager;
import com.github.lehjr.mpalib.string.StringUtils;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 7:49 PM, 4/23/13
 * <p>
 * Ported to Java by lehjr on 11/4/16.
 */
public interface IModularItemBase extends IModularItem, IElectricItem {
    default String formatInfo(String string, double value) {
        return string + '\t' + StringUtils.formatNumberShort(value);
    }

    @Override
    default IModuleManager getModuleManager() {
        return ModuleManager.INSTANCE;
    }

    default double getArmorDouble(EntityPlayer player, ItemStack stack) {
        return 0;
    }
}