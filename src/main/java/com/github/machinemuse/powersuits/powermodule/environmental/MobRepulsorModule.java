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

package com.github.machinemuse.powersuits.powermodule.environmental;


import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleTarget;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IToggleableModule;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.client.event.MPSIcon;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.component.ItemComponent;
import com.github.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

/**
 * Created by User: Andrew2448
 * 8:26 PM 4/25/13
 */
public class MobRepulsorModule extends PowerModuleBase implements IPlayerTickModule, IToggleableModule {
    public MobRepulsorModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.magnet, 1));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemUtils.copyAndResize(ItemComponent.controlCircuit, 1));

        addBasePropertyDouble(MPSModuleConstants.MOB_REPULSOR_ENERGY_CONSUMPTION, 2500, "RF");
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.ENVIRONMENTAL;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_MOB_REPULSOR__DATANAME;
    }

    @Override
    public void onPlayerTickActive(EntityPlayer player, ItemStack item) {
        if (ElectricItemUtils.getPlayerEnergy(player) > ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, MPSModuleConstants.MOB_REPULSOR_ENERGY_CONSUMPTION)) {
            if (player.world.getTotalWorldTime() % 20 == 0) {
                ElectricItemUtils.drainPlayerEnergy(player, (int) Math.round(ModuleManager.INSTANCE.getOrSetModularPropertyDouble(item, MPSModuleConstants.MOB_REPULSOR_ENERGY_CONSUMPTION)));
            }
            repulse(player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
        }
    }

    @Override
    public void onPlayerTickInactive(EntityPlayer player, ItemStack item) {
    }

    public void repulse(World world, int i, int j, int k) {
        float distance = 5.0F;
        Entity entity;
        Iterator iterator;
        List list = world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
        for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
            entity = (Entity) iterator.next();
        }
        list = world.getEntitiesWithinAABB(EntityArrow.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
        for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
            entity = (Entity) iterator.next();
        }
        list = world.getEntitiesWithinAABB(EntityFireball.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
        for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
            entity = (Entity) iterator.next();
        }
        list = world.getEntitiesWithinAABB(EntityPotion.class, new AxisAlignedBB(i - distance, j - distance, k - distance, i + distance, j + distance, k + distance));
        for (iterator = list.iterator(); iterator.hasNext(); push(entity, i, j, k)) {
            entity = (Entity) iterator.next();
        }
    }

    private void push(Entity entity, int i, int j, int k) {
        if (!(entity instanceof EntityPlayer) && !(entity instanceof EntityDragon)) {
            double d = i - entity.posX;
            double d1 = j - entity.posY;
            double d2 = k - entity.posZ;
            double d4 = d * d + d1 * d1 + d2 * d2;
            d4 *= d4;
            if (d4 <= Math.pow(6.0D, 4.0D)) {
                double d5 = -(d * 0.01999999955296516D / d4) * Math.pow(6.0D, 3.0D);
                double d6 = -(d1 * 0.01999999955296516D / d4) * Math.pow(6.0D, 3.0D);
                double d7 = -(d2 * 0.01999999955296516D / d4) * Math.pow(6.0D, 3.0D);
                if (d5 > 0.0D) {
                    d5 = 0.22D;
                } else if (d5 < 0.0D) {
                    d5 = -0.22D;
                }
                if (d6 > 0.2D) {
                    d6 = 0.12D;
                } else if (d6 < -0.1D) {
                    d6 = 0.12D;
                }
                if (d7 > 0.0D) {
                    d7 = 0.22D;
                } else if (d7 < 0.0D) {
                    d7 = -0.22D;
                }
                entity.motionX += d5;
                entity.motionY += d6;
                entity.motionZ += d7;
            }
        }
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MPSIcon.mobRepulsor;
    }
}