package com.github.lehjr.modularpowerarmor.event;

import com.github.lehjr.modularpowerarmor.client.sound.MPASoundDictionary;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorBoots;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorChestplate;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorLeggings;
import com.github.lehjr.mpalib.basemod.MPALibConfig;
import com.github.lehjr.mpalib.basemod.MPALibLogger;
import com.github.lehjr.mpalib.capabilities.inventory.modechanging.IModeChangingItem;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.math.MathUtils;
import com.github.lehjr.mpalib.player.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Claire Semple on 9/8/2014.
 * <p>
 * Ported to Java by lehjr on 10/24/16.
 */
public class PlayerUpdateHandler {
    @SuppressWarnings("unchecked")
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();

            // only MPS modular items in this list
//            List<ItemStack> modularItemsEquipped = ItemUtils.getModularItemsEquipped(player);


//            // FIXME: is this really nescessary... apparently it is
//            for (ItemStack stack : modularItemsEquipped) {
//                // Temporary Advanced Rocketry hack Not the best way but meh.
//                ListNBT tagList = stack.getEnchantmentTagList();
//                if (tagList != null && !tagList.isEmpty()) {
//                    if (tagList.tagCount() == 1) {
//                        if (!(tagList.getCompoundTagAt(0).getShort("id") == 128))
//                            stack.getTagCompound().removeTag("ench");
//                    } else {
//                        CompoundNBT ar = null;
//                        for (int i = 0; i < tagList.tagCount(); i++) {
//                            CompoundNBT nbtTag = tagList.getCompoundTagAt(i);
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

            // FIXME ... ticking modules not done

            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                if(player.getItemStackFromSlot(slot).isEmpty())
                    continue;

                switch (slot.getSlotType()) {
                    case HAND:
                        player.getItemStackFromSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(i-> {
                            if (i instanceof IModeChangingItem) {
                                ((IModeChangingItem) i).tick(player);
                                modularItemsEquipped.getAndAdd(1);
                            }
                        });
                        break;

                    case ARMOR:

                        try {
                            player.getItemStackFromSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(i-> {
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

            // pretty sure the whole point of this was to reduce fall distance, not increase it.
            float fallDistance = (float) MovementManager.computeFallHeightFromVelocity(MathUtils.clampDouble(player.getMotion().y, -1000.0, 0.0));
            if (fallDistance < player.fallDistance) {
                player.fallDistance = fallDistance;
            }

            // Sound update
            if (player.world.isRemote && MPALibConfig.USE_SOUNDS.get()) {
                if (modularItemsEquipped.get() > 0) {
                    double velsq2 = MathUtils.sumsq(player.getMotion().x, player.getMotion().y, player.getMotion().z) - 0.5;
                    if (player.isAirBorne && velsq2 > 0) {
                        Musique.playerSound(player, MPASoundDictionary.SOUND_EVENT_GLIDER, SoundCategory.PLAYERS, (float) (velsq2 / 3), 1.0f, true);
                    } else {
                        Musique.stopPlayerSound(player, MPASoundDictionary.SOUND_EVENT_GLIDER);
                    }
                } else {
                    Musique.stopPlayerSound(player, MPASoundDictionary.SOUND_EVENT_GLIDER);
                }

                if (!(player.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() instanceof ItemPowerArmorBoots))
                    Musique.stopPlayerSound(player, MPASoundDictionary.SOUND_EVENT_JETBOOTS);

                if (!(player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ItemPowerArmorChestplate))
                    Musique.stopPlayerSound(player, MPASoundDictionary.SOUND_EVENT_JETPACK);

                if (!(player.getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() instanceof ItemPowerArmorLeggings))
                    Musique.stopPlayerSound(player, MPASoundDictionary.SOUND_EVENT_SWIM_ASSIST);
            }

            //  Done this way so players can let their stuff cool in their inventory without having to equip it,
            // allowing it to cool off enough to not take damage
            List<ItemStack> modularItemsInInventory = ItemUtils.getModularItemsInInventory(player);
            if (modularItemsInInventory.size() > 0) {
                // Heat update
                double currHeat = HeatUtils.getPlayerHeat(player);

//                System.out.println("currHeat: " + currHeat);

                if (currHeat >= 0 && !player.world.isRemote) { // only apply serverside so change is not applied twice

                    // cooling value adjustment. Too much or too little cooling makes the heat system useless.
                    double coolPlayerAmount = PlayerUtils.getPlayerCoolingBasedOnMaterial(player) * 0.55;  // cooling value adjustment. Too much or too little cooling makes the heat system useless.


//                    System.out.println("coolPlayerAmount: " + coolPlayerAmount);


                    if (coolPlayerAmount > 0) {
                        HeatUtils.coolPlayer(player, coolPlayerAmount);
                    }

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