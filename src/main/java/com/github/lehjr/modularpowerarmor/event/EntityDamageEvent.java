package com.github.lehjr.modularpowerarmor.event;

import com.google.common.eventbus.Subscribe;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class EntityDamageEvent {
    @Subscribe
    public static void handleEntityDamageEvent(LivingDamageEvent event) {
        // todo: control damage based on heat/max heat && whether or not player has full armor and is in lava
        // Note: can cancel here but damage animation/sound still happens. Only way to not have it is potion effects.


//        LivingEntity livingEntityy = event.getEntityLiving();
//        if (livingEntityy instanceof PlayerEntity) {
//            System.out.println("source: " + event.getSource().getDamageType());
//            if (event.getSource().isFireDamage()) {
//                event.setCanceled(true);
//            }
//        }
    }
}