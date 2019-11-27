package com.github.machinemuse.powersuits.powermodule.energy_storage;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

/**
 * Created by leon on 7/3/16.
 */
public class UltimateBatteryModule extends PowerModuleBase {
    public UltimateBatteryModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.evcapacitor, 1));

        addBasePropertyDouble(MPALIbConstants.MAXIMUM_ENERGY, 100000000, "RF");
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.ENERGY_STORAGE;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_BATTERY_ULTIMATE__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.ultimateBattery;
    }
}