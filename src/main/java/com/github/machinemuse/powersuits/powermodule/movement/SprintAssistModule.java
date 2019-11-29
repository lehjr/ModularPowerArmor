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
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MPSIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.event.MovementManager;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorLeggings;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * Ported by leon on 10/18/16.
 */
public class SprintAssistModule extends PowerModuleBase implements IToggleableModule, IPlayerTickModule {
    public SprintAssistModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.servoMotor, 4));

        addBasePropertyDouble(MPSModuleConstants.SPRINT_ENERGY_CONSUMPTION, 0, "RF");
        addTradeoffPropertyDouble(MPSModuleConstants.SPRINT_ASSIST, MPSModuleConstants.SPRINT_ENERGY_CONSUMPTION, 100);
        addBasePropertyDouble(MPSModuleConstants.SPRINT_SPEED_MULTIPLIER, .01, "%");
        addTradeoffPropertyDouble(MPSModuleConstants.SPRINT_ASSIST, MPSModuleConstants.SPRINT_SPEED_MULTIPLIER, 2.49);

        addBasePropertyDouble(MPSModuleConstants.SPRINT_ENERGY_CONSUMPTION, 0, "RF");
        addTradeoffPropertyDouble(MPSModuleConstants.COMPENSATION, MPSModuleConstants.SPRINT_ENERGY_CONSUMPTION, 20);
        addBasePropertyDouble(MPSModuleConstants.SPRINT_FOOD_COMPENSATION, 0, "%");
        addTradeoffPropertyDouble(MPSModuleConstants.COMPENSATION, MPSModuleConstants.SPRINT_FOOD_COMPENSATION, 1);

        addBasePropertyDouble(MPSModuleConstants.WALKING_ENERGY_CONSUMPTION, 0, "RF");
        addTradeoffPropertyDouble(MPSModuleConstants.WALKING_ASSISTANCE, MPSModuleConstants.WALKING_ENERGY_CONSUMPTION, 100);
        addBasePropertyDouble(MPSModuleConstants.WALKING_SPEED_MULTIPLIER, 0.01, "%");
        addTradeoffPropertyDouble(MPSModuleConstants.WALKING_ASSISTANCE, MPSModuleConstants.WALKING_SPEED_MULTIPLIER, 1.99);
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack itemStack) {
        if (player.capabilities.isFlying || player.isRiding() || player.isElytraFlying())
            onPlayerTickInactive(player, itemStack);

        ItemStack armorLeggings = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        // now you actually have to wear these to get the speed boost
        if (!armorLeggings.isEmpty() && armorLeggings.getItem() instanceof ItemPowerArmorLeggings) {
            double horzMovement = player.distanceWalkedModified - player.prevDistanceWalkedModified;
            double totalEnergy = ElectricItemUtils.getPlayerEnergy(player);
            if (horzMovement > 0) { // stop doing drain calculations when player hasn't moved
                if (player.isSprinting()) {
                    double exhaustion =  Math.round(horzMovement) * 0.1F;
                    double sprintCost = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.SPRINT_ENERGY_CONSUMPTION);
                    if (sprintCost < totalEnergy) {
                        double sprintMultiplier = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.SPRINT_SPEED_MULTIPLIER);
                        double exhaustionComp = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.SPRINT_FOOD_COMPENSATION);
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (sprintCost * horzMovement * 5));
                        MovementManager.setMovementModifier(itemStack, sprintMultiplier, player);
                        player.getFoodStats().addExhaustion((float) (-1 * exhaustion * exhaustionComp));
                        player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                    }
                } else {
                    double cost = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.WALKING_ENERGY_CONSUMPTION);
                    if (cost < totalEnergy) {
                        double walkMultiplier = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(itemStack, MPSModuleConstants.WALKING_SPEED_MULTIPLIER);
                        ElectricItemUtils.drainPlayerEnergy(player, (int) (cost * horzMovement * 5));
                        MovementManager.setMovementModifier(itemStack, walkMultiplier, player);
                        player.jumpMovementFactor = player.getAIMoveSpeed() * .2f;
                    }
                }
            }
        } else
            onPlayerTickInactive(player, itemStack);
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack itemStack) {
        MovementManager.setMovementModifier(itemStack, 0, player);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.MOVEMENT;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_SPRINT_ASSIST__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MPSIcon.sprintAssist;
    }
}