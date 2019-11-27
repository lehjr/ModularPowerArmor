package com.github.machinemuse.powersuits.powermodule.vision;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 1:08 AM, 4/24/13
 * <p>
 * Ported to Java by lehjr on 10/11/16.
 */
public class BinocularsModule extends PowerModuleBase implements IToggleableModule {
    public BinocularsModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.laserHologram, 1));
        addBasePropertyDouble(MPSModuleConstants.FOV, 0.5);
        addTradeoffPropertyDouble(MPSModuleConstants.FIELD_OF_VIEW, MPSModuleConstants.FOV, 9.5, "%");
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.VISION;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.BINOCULARS_MODULE__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.binoculars;
    }
}