package com.github.machinemuse.powersuits.powermodule.armor;

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

public class EnergyShieldModule extends PowerModuleBase {
    public EnergyShieldModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(this.getDataName(), ItemUtils.copyAndResize(ItemComponent.fieldEmitter, 2));

        addTradeoffPropertyDouble(MPSModuleConstants.MODULE_FIELD_STRENGTH, MPSModuleConstants.ARMOR_VALUE_ENERGY, 6,
                MPSModuleConstants.MODULE_TRADEOFF_PREFIX + MPSModuleConstants.ARMOR_POINTS);
        addTradeoffPropertyDouble(MPSModuleConstants.MODULE_FIELD_STRENGTH, MPSModuleConstants.ARMOR_ENERGY_CONSUMPTION, 5000, "RF");
        addTradeoffPropertyDouble(MPSModuleConstants.MODULE_FIELD_STRENGTH, MPALIbConstants.MAXIMUM_HEAT, 500, "");
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_ENERGY_SHIELD__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.energyShield;
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.ARMOR;
    }
}