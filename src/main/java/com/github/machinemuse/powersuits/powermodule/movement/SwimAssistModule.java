/*
 * ModularPowersuits (Maintenance builds by lehjr)
 * Copyright (c) 2019 MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import com.github.machinemuse.powersuits.client.event.MPSIcon;
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
        return MPSIcon.swimAssist;
    }
}