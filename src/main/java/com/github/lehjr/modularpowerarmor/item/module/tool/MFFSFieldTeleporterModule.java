package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.client.event.MuseIcon;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

/**
 * Created by User: Andrew2448
 * 7:21 PM 4/25/13
 */
public class MFFSFieldTeleporterModule extends AbstractPowerModule {
    public MFFSFieldTeleporterModule(EnumModuleTarget moduleTarget) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));

        addBasePropertyDouble(ModuleConstants.FIELD_TELEPORTER_ENERGY_CONSUMPTION, 200000, "RF");
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.TOOL;
    }

    @Override
    public String getDataName() {
        return ModuleConstants.MODULE_FIELD_TELEPORTER__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.mffsFieldTeleporter;
    }
}