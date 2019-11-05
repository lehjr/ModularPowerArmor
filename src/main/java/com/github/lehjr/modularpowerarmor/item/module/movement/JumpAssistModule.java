package com.github.lehjr.modularpowerarmor.item.module.movement;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.control.PlayerMovementInputWrapper;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.lehjr.mpalib.player.PlayerUtils;
import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.client.event.MuseIcon;
import com.github.lehjr.modularpowerarmor.event.MovementManager;
import com.github.lehjr.modularpowerarmor.item.component.ItemComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class JumpAssistModule extends AbstractPowerModule implements IToggleableModule, IPlayerTickModule {
    public JumpAssistModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.servoMotor, 4));

        addBasePropertyDouble(ModuleConstants.JUMP_ENERGY_CONSUMPTION, 0, "RF");
        addTradeoffPropertyDouble(ModuleConstants.POWER, ModuleConstants.JUMP_ENERGY_CONSUMPTION, 250);
        addBasePropertyDouble(ModuleConstants.JUMP_MULTIPLIER, 1, "%");
        addTradeoffPropertyDouble(ModuleConstants.POWER, ModuleConstants.JUMP_MULTIPLIER, 4);

        addBasePropertyDouble(ModuleConstants.JUMP_ENERGY_CONSUMPTION, 0, "RF");
        addTradeoffPropertyDouble(ModuleConstants.COMPENSATION, ModuleConstants.JUMP_ENERGY_CONSUMPTION, 50);
        addBasePropertyDouble(ModuleConstants.JUMP_FOOD_COMPENSATION, 0, "%");
        addTradeoffPropertyDouble(ModuleConstants.COMPENSATION, ModuleConstants.JUMP_FOOD_COMPENSATION, 1);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.MOVEMENT;
    }

    @Override
    public String getDataName() {
        return ModuleConstants.MODULE_JUMP_ASSIST__DATANAME;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
        if (playerInput.jumpKey) {
            double multiplier = MovementManager.getPlayerJumpMultiplier(player);
            if (multiplier > 0) {
                player.motionY += 0.15 * Math.min(multiplier, 1);
                MovementManager.setPlayerJumpTicks(player, multiplier - 1);
            }
            player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
        } else {
            MovementManager.setPlayerJumpTicks(player, 0);
        }
        PlayerUtils.resetFloatKickTicks(player);
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.jumpAssist;
    }
}
