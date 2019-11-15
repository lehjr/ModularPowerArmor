package com.github.lehjr.modularpowerarmor.event;

import com.github.lehjr.modularpowerarmor.client.sound.SoundDictionary;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorBoots;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorChestplate;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorLeggings;
import com.github.lehjr.modularpowerarmor.utils.PlayerUtils;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.config.MPALibConfig;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.math.MathUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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

            AtomicInteger modularItemsEquipped = new AtomicInteger(0);
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                if(player.getItemStackFromSlot(slot).isEmpty()) {
                    continue;
                }

                switch (slot.getSlotType()) {
                    case HAND:
                        Optional.ofNullable(player.getItemStackFromSlot(slot)
                                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
                                .ifPresent(i-> {
                            if (i instanceof IModeChangingItem) {
                                ((IModeChangingItem) i).tick(player);
                                modularItemsEquipped.getAndAdd(1);
                            }
                        });
                        break;

                    case ARMOR:
                        try {
                            Optional.ofNullable(player.getItemStackFromSlot(slot)
                                    .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).ifPresent(i-> {
                                if (i instanceof IModularItem) {
                                    ((IModularItem) i).tick(player);
                                    modularItemsEquipped.getAndAdd(1);
                                }
                            });
                        } catch (Exception exception) {
                            MPALibLogger.logException(player.getItemStackFromSlot(slot).toString(), exception);
                        }
                        break;
                }
            }

            // Sound update
            if (player.world.isRemote && MPALibConfig.useSounds()) {
                if (modularItemsEquipped.get() > 0) {
                    double velsq2 = MathUtils.sumsq(player.motionX, player.motionY, player.motionZ) - 0.5;
                    if (player.isAirBorne && velsq2 > 0) {
                        Musique.playerSound(player, SoundDictionary.SOUND_EVENT_GLIDER, SoundCategory.PLAYERS, (float) (velsq2 / 3), 1.0f, true);
                    } else {
                        Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_GLIDER);
                    }
                } else {
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_GLIDER);
                }

                // fixme remove item reference and use capability references
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
                    double coolPlayerAmount = PlayerUtils.getPlayerCoolingBasedOnMaterial(player) * 0.55;  // cooling value adjustment. Too much or too little cooling makes the heat system useless.

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