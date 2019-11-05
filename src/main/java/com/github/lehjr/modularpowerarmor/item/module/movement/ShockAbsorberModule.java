package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.client.event.MuseIcon;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ShockAbsorberModule extends AbstractPowerModule implements IToggleableModule {
    public ShockAbsorberModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.servoMotor, 2));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), new ItemStack(Blocks.WOOL, 2));
        addBasePropertyDouble(ModuleConstants.SHOCK_ABSORB_ENERGY_CONSUMPTION, 0, "RF/m");
        addTradeoffPropertyDouble(ModuleConstants.POWER, ModuleConstants.SHOCK_ABSORB_ENERGY_CONSUMPTION, 100);
        addBasePropertyDouble(ModuleConstants.SHOCK_ABSORB_MULTIPLIER, 0, "%");
        addTradeoffPropertyDouble(ModuleConstants.POWER, ModuleConstants.SHOCK_ABSORB_MULTIPLIER, 10);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.MOVEMENT;
    }

    @Override
    public String getDataName() {
        return ModuleConstants.MODULE_SHOCK_ABSORBER__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.shockAbsorber;
    }
}