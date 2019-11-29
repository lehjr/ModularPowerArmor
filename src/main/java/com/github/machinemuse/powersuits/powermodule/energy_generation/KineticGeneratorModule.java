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

package com.github.machinemuse.powersuits.powermodule.energy_generation;

import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MPSIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.event.MovementManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class KineticGeneratorModule extends PowerModuleBase implements IPlayerTickModule, IToggleableModule {
    public KineticGeneratorModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.servoMotor, 2));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));

        addBasePropertyDouble(MPSModuleConstants.KINETIC_ENERGY_GENERATION, 2000);
        addTradeoffPropertyDouble(MPSModuleConstants.ENERGY_GENERATED, MPSModuleConstants.KINETIC_ENERGY_GENERATION, 6000, "RF");
        addBasePropertyDouble(MPSModuleConstants.KINETIC_ENERGY_MOVEMENT_RESISTANCE, 0.01);
        addTradeoffPropertyDouble(MPSModuleConstants.ENERGY_GENERATED, MPSModuleConstants.KINETIC_ENERGY_MOVEMENT_RESISTANCE, 0.49, "%");
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.ENERGY_GENERATION;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_KINETIC_GENERATOR__DATANAME;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack itemStack) {
        if (player.capabilities.isFlying || player.isRiding() || player.isElytraFlying() || !player.onGround)
            onPlayerTickInactive(player, itemStack);

        // really hate running this check on every tick but needed for player speed adjustments
        if (ElectricItemUtils.getPlayerEnergy(player) < ElectricItemUtils.getMaxPlayerEnergy(player)) {            // only fires if the sprint assist module isn't installed and active
            if (!ModuleManager.INSTANCE.itemHasActiveModule(itemStack, MPSModuleConstants.MODULE_SPRINT_ASSIST__DATANAME)) {
                MovementManager.setMovementModifier(itemStack, 0, player);
            }

            // server side
            if (!player.world.isRemote &&
                    // every 20 ticks
                    (player.world.getTotalWorldTime() % 20) == 0 &&
                    // player not jumping, flying, or riding
                    player.onGround) {
                double distance = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                ElectricItemUtils.givePlayerEnergy(player, (int) (distance * 10 *  ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.KINETIC_ENERGY_GENERATION)));
            }
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
        // only fire if sprint assist module not installed.
        if (!ModuleManager.INSTANCE.itemHasModule(item, MPSModuleConstants.MODULE_SPRINT_ASSIST__DATANAME)) {
            MovementManager.setMovementModifier(item, 0, player);
        }
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MPSIcon.kineticGenerator;
    }
}