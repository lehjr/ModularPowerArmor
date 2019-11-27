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

public class DiamondPlatingModule extends PowerModuleBase {
    public DiamondPlatingModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(this.getDataName(), ItemUtils.copyAndResize(ItemComponent.diamonddPlating, 1));

        addBasePropertyDouble(MPSModuleConstants.ARMOR_VALUE_PHYSICAL, 5,
                MPSModuleConstants.MODULE_TRADEOFF_PREFIX + MPSModuleConstants.ARMOR_POINTS);
        addBasePropertyDouble(MPALIbConstants.MAXIMUM_HEAT, 400);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.ARMOR;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_DIAMOND_PLATING__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.diamondPlating;
    }
}