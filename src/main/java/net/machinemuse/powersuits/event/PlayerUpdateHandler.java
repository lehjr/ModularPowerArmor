package net.machinemuse.powersuits.event;

import net.machinemuse.numina.basemod.NuminaConfig;
import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.client.sound.Musique;
import net.machinemuse.numina.heat.MuseHeatUtils;
import net.machinemuse.numina.item.MuseItemUtils;
import net.machinemuse.numina.math.MuseMathUtils;
import net.machinemuse.numina.player.NuminaPlayerUtils;
import net.machinemuse.powersuits.client.sound.SoundDictionary;
import net.machinemuse.powersuits.item.armor.ItemPowerArmorBoots;
import net.machinemuse.powersuits.item.armor.ItemPowerArmorChestplate;
import net.machinemuse.powersuits.item.armor.ItemPowerArmorLeggings;
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
    public void onPlayerUpdate(LivingEvent.LivingUpdateEvent e) {
        if (e.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) e.getEntity();

            // only MPS modular items in this list
//            List<ItemStack> modularItemsEquipped = MuseItemUtils.getModularItemsEquipped(player);


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

            AtomicInteger modularItemsEquipped = new AtomicInteger();

            for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                switch (slot.getSlotType()) {
                    case HAND:
                        player.getItemStackFromSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(i-> {
                            if (i instanceof IModeChangingItem) {
                                ((IModeChangingItem) i).tick(player);
                                modularItemsEquipped.addAndGet(1);
                            }
                        });
                        break;

                    case ARMOR:
                            player.getItemStackFromSlot(slot).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(i-> {
                                if (i instanceof IModularItem)
                                ((IModularItem) i).tick(player);
                                modularItemsEquipped.addAndGet(1);
                            });
                }
            }






//            System.out.println("player fall distance before: [ player: " + player.getName() + " ], [ distance : " + player.fallDistance + " ]"  );
//
                player.fallDistance = (float) MovementManager.computeFallHeightFromVelocity(MuseMathUtils.clampDouble(player.getMotion().y, -1000.0, 0.0));
//
//            System.out.println("player fall distance after: [ player: " + player.getName() + " ], [ distance : " + player.fallDistance + " ]"  );

            // Sound update
            if (player.world.isRemote && NuminaConfig.USE_SOUNDS.get()) {
                if (modularItemsEquipped.get() > 0) {
                    double velsq2 = MuseMathUtils.sumsq(player.getMotion().x, player.getMotion().y, player.getMotion().z) - 0.5;
                    if (player.isAirBorne && velsq2 > 0) {
                        Musique.playerSound(player, SoundDictionary.SOUND_EVENT_GLIDER, SoundCategory.PLAYERS, (float) (velsq2 / 3), 1.0f, true);
                    } else {
                        Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_GLIDER);
                    }
                } else {
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_GLIDER);
                }

                if (!(player.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() instanceof ItemPowerArmorBoots))
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETBOOTS);

                if (!(player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ItemPowerArmorChestplate))
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_JETPACK);

                if (!(player.getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() instanceof ItemPowerArmorLeggings))
                    Musique.stopPlayerSound(player, SoundDictionary.SOUND_EVENT_SWIM_ASSIST);
            }

            // Done this way so players can let their stuff cool in their inventory without having to equip it.
            List<ItemStack> modularItemsInInventory = MuseItemUtils.getModularItemsInInventory(player);
            if (modularItemsInInventory.size() > 0) {
                // Heat update
                double currHeat = MuseHeatUtils.getPlayerHeat(player);
                if (currHeat >= 0 && !player.world.isRemote) { // only apply serverside so change is not applied twice
                    double coolPlayerAmount = NuminaPlayerUtils.getPlayerCoolingBasedOnMaterial(player) * 0.55;  // cooling value adjustment. Too much or too little cooling makes the heat system useless.

                    // cooling value adjustment. Too much or too little cooling makes the heat system useless.

                    if (coolPlayerAmount > 0)
                        MuseHeatUtils.coolPlayer(player, coolPlayerAmount);

                    double maxHeat = MuseHeatUtils.getPlayerMaxHeat(player);

                    if (currHeat > maxHeat) {
                        player.attackEntityFrom(MuseHeatUtils.overheatDamage, (float) (Math.sqrt(currHeat - maxHeat)/* was (int) */ / 4));
                        player.setFire(1);
                    } else {
                        player.extinguish();
                    }
                }
            }
        }
    }
}