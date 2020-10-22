package com.github.lehjr.modularpowerarmor.event;

import com.github.lehjr.modularpowerarmor.client.sound.MPASoundDictionary;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorBoots;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorChestplate;
import com.github.lehjr.modularpowerarmor.item.armor.ItemPowerArmorLeggings;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.config.MPALibSettings;
import com.github.lehjr.mpalib.math.MathUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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

            // pretty sure the whole point of this was to reduce fall distance, not increase it.
            float fallDistance = (float) MovementManager.INSTANCE.computeFallHeightFromVelocity(MathUtils.clampDouble(player.getMotion().y, -1000.0, 0.0));
            if (fallDistance < player.fallDistance) {
                player.fallDistance = fallDistance;
            }

            // Sound update
            if (player.world.isRemote && MPALibSettings.useSounds()) {
                if ((player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ItemPowerArmorChestplate)) {
                    double velsq2 = MathUtils.sumsq(player.getMotion().x, player.getMotion().y, player.getMotion().z) - 0.5;
                    if (player.isAirBorne && velsq2 > 0) {
                        Musique.playerSound(player, MPASoundDictionary.SOUND_EVENT_GLIDER, SoundCategory.PLAYERS, (float) (velsq2 / 3), 1.0f, true);
                    } else {
                        Musique.stopPlayerSound(player, MPASoundDictionary.SOUND_EVENT_GLIDER);
                    }
                } else {
                    Musique.stopPlayerSound(player, MPASoundDictionary.SOUND_EVENT_GLIDER);
                }

                if (!(player.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() instanceof ItemPowerArmorBoots)) {
                    Musique.stopPlayerSound(player, MPASoundDictionary.SOUND_EVENT_JETBOOTS);
                }

                if (!(player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() instanceof ItemPowerArmorChestplate)) {
                    Musique.stopPlayerSound(player, MPASoundDictionary.SOUND_EVENT_JETPACK);
                }

                if (!(player.getItemStackFromSlot(EquipmentSlotType.LEGS).getItem() instanceof ItemPowerArmorLeggings)) {
                    Musique.stopPlayerSound(player, MPASoundDictionary.SOUND_EVENT_SWIM_ASSIST);
                }
            }
        }
    }
}