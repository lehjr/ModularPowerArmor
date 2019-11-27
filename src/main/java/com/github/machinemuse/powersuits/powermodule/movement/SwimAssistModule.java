package com.github.machinemuse.powersuits.powermodule.movement;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.config.MPALibConfig;
import com.github.lehjr.mpalib.control.PlayerMovementInputWrapper;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MuseIcon;
import com.github.machinemuse.powersuits.client.sound.SoundDictionary;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.event.MovementManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public class SwimAssistModule extends PowerModuleBase implements IToggleableModule, IPlayerTickModule {
    public SwimAssistModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.ionThruster, 1));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.solenoid, 2));
        addTradeoffPropertyDouble(MPSModuleConstants.THRUST, MPSModuleConstants.SWIM_BOOST_ENERGY_CONSUMPTION, 1000, "RF");
        addTradeoffPropertyDouble(MPSModuleConstants.THRUST, MPSModuleConstants.SWIM_BOOST_AMOUNT, 1, "m/s");
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.MOVEMENT;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_SWIM_BOOST__DATANAME;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        if (player.isInWater() && !(player.isRiding())) {
            PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
            if (playerInput.moveForward != 0 || playerInput.moveStrafe != 0 || playerInput.jumpKey || playerInput.sneakKey) {
                double moveRatio = 0;
                if (playerInput.moveForward != 0) {
                    moveRatio += playerInput.moveForward * playerInput.moveForward;
                }
                if (playerInput.moveStrafe != 0) {
                    moveRatio += playerInput.moveStrafe * playerInput.moveStrafe;
                }
                if (playerInput.jumpKey || playerInput.sneakKey) {
                    moveRatio += 0.2 * 0.2;
                }
                double swimAssistRate = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, MPSModuleConstants.SWIM_BOOST_AMOUNT) * 0.05 * moveRatio;
                double swimEnergyConsumption = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, MPSModuleConstants.SWIM_BOOST_ENERGY_CONSUMPTION);
                if (swimEnergyConsumption < ElectricItemUtils.getPlayerEnergy(player)) {
                    if (player.world.isRemote && MPALibConfig.useSounds()) {
                        Musique.playerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST, SoundCategory.PLAYERS, 1.0f, 1.0f, true);
                    }
                    MovementManager.thrust(player, swimAssistRate, true);
                } else {
                    if (player.world.isRemote && MPALibConfig.useSounds()) {
                        Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
                    }
                }
            } else {
                if (player.world.isRemote && MPALibConfig.useSounds()) {
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
                }
            }
        } else {
            if (player.world.isRemote && MPALibConfig.useSounds()) {
                Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
            }
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
        if (player.world.isRemote && MPALibConfig.useSounds()) {
            Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
        }
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.swimAssist;
    }
}