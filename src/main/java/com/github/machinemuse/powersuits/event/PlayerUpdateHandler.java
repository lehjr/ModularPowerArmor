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

package com.github.machinemuse.powersuits.event;

import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.config.MPALibConfig;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPlayerTickModule;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.mpalib.math.MathUtils;
import com.github.machinemuse.powersuits.client.sound.SoundDictionary;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorBoots;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorChestplate;
import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorLeggings;
import com.github.machinemuse.powersuits.utils.MusePlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * Created by Claire Semple on 9/8/2014.
 * <p>
 * Ported to Java by lehjr on 10/24/16.
 */
public class PlayerUpdateHandler {
    @SuppressWarnings("unchecked")
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent e) {
        if (e.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) e.getEntity();

            // only MPS modular items in this list
            List<ItemStack> modularItemsEquipped = ItemUtils.getLegacyModularItemsEquipped(player);


//            // FIXME: is this really nescessary... apparently it is
//            for (ItemStack stack : modularItemsEquipped) {
//                // Temporary Advanced Rocketry hack Not the best way but meh.
//                NBTTagList tagList = stack.getEnchantmentTagList();
//                if (tagList != null && !tagList.isEmpty()) {
//                    if (tagList.tagCount() == 1) {
//                        if (!(tagList.getCompoundTagAt(0).getShort("id") == 128))
//                            stack.getTagCompound().removeTag("ench");
//                    } else {
//                        NBTTagCompound ar = null;
//                        for (int i = 0; i < tagList.tagCount(); i++) {
//                            NBTTagCompound nbtTag = tagList.getCompoundTagAt(i);
//                            if ((nbtTag.getShort("id") == 128)) {
//                                ar = nbtTag;
//                            }
//                        }
//                        stack.getTagCompound().removeTag("ench");
//                        if (ar != null) {
//                            stack.getTagCompound().setTag("ench", ar);
//                        }
//                    }
//                }
//            }

//            Enchantment.getEnchantmentID(AdvancedRocketryAPI.enchantmentSpaceProtection);

            for (ItemStack itemStack : modularItemsEquipped) {
                for (IPowerModule module : ModuleManager.INSTANCE.getModulesOfType(IPlayerTickModule.class)) {
                    if (ModuleManager.INSTANCE.isValidForItem(itemStack, module) && ModuleManager.INSTANCE.itemHasModule(itemStack, module.getDataName())) {
                        if (ModuleManager.INSTANCE.isModuleOnline(itemStack, module.getDataName()))
                            ((IPlayerTickModule) module).onPlayerTickActive(player, itemStack);
                        else
                            ((IPlayerTickModule) module).onPlayerTickInactive(player, itemStack);
                    }
                }
            }

            // Sound update
            if (player.world.isRemote && MPALibConfig.useSounds()) {
                if (modularItemsEquipped.size() > 0) {
                    double velsq2 = MathUtils.sumsq(player.motionX, player.motionY, player.motionZ) - 0.5;
                    if (player.isAirBorne && velsq2 > 0) {
                        Musique.playerSound(player, SoundDictionary.SOUND_EVENT_GLIDER, SoundCategory.PLAYERS, (float) (velsq2 / 3), 1.0f, true);
                    } else {
                        Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_GLIDER);
                    }
                } else {
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_GLIDER);
                }

                if (!(player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemPowerArmorBoots))
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS);

                if (!(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemPowerArmorChestplate))
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETPACK);

                if (!(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemPowerArmorLeggings))
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
            }

            // Done this way so players can let their stuff cool in their inventory without having to equip it.
            List<ItemStack> modularItemsInInventory = ItemUtils.getLegacyModularItemsInInventory(player);
            if (modularItemsInInventory.size() > 0) {
                // Heat update
                double currHeat = HeatUtils.getPlayerHeat(player);
                if (currHeat >= 0 && !player.world.isRemote) { // only apply serverside so change is not applied twice
                    double coolPlayerAmount = MusePlayerUtils.getPlayerCoolingBasedOnMaterial(player) * 0.55;  // cooling value adjustment. Too much or too little cooling makes the heat system useless.

                    // cooling value adjustment. Too much or too little cooling makes the heat system useless.

                    if (coolPlayerAmount > 0)
                        HeatUtils.coolPlayer(player, coolPlayerAmount);

                    double maxHeat = HeatUtils.getPlayerMaxHeat(player);

                    if (currHeat > maxHeat) {
                        player.attackEntityFrom(HeatUtils.overheatDamage, (float) (Math.sqrt(currHeat - maxHeat)/* was (int) */ / 4));
                        player.setFire(1);
                    } else {
                        player.extinguish();
                    }
                }
            }
        }
    }
}