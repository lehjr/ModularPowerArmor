package com.github.machinemuse.powersuits.powermodule.movement;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.control.PlayerMovementInputWrapper;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.lehjr.mpalib.player.PlayerUtils;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class GliderModule extends PowerModuleBase implements IToggleableModule, IPlayerTickModule {
    public GliderModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.gliderWing, 2));
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.MOVEMENT;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_GLIDER__DATANAME;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack itemStack) {
        Vec3d playerHorzFacing = player.getLookVec();
        playerHorzFacing = new Vec3d(playerHorzFacing.x, 0, playerHorzFacing.z);
        playerHorzFacing.normalize();
        PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);

        PlayerUtils.resetFloatKickTicks(player);
        boolean hasParachute = ModuleManager.INSTANCE.itemHasActiveModule(itemStack, MPSModuleConstants.MODULE_PARACHUTE__DATANAME);
        if (playerInput.sneakKey && player.motionY < 0 && (!hasParachute || playerInput.moveForward > 0)) {
            if (player.motionY < -0.1) {
                float vol = (float) (player.motionX * player.motionX + player.motionZ * player.motionZ);
                double motionYchange = Math.min(0.08, -0.1 - player.motionY);
                player.motionY += motionYchange;
                player.motionX += playerHorzFacing.x * motionYchange;
                player.motionZ += playerHorzFacing.z * motionYchange;

                // sprinting speed
                player.jumpMovementFactor += 0.03f;
            }
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.glider;
    }
}