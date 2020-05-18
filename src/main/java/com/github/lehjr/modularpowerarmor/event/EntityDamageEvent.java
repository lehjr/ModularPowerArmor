package com.github.lehjr.modularpowerarmor.event;

import com.github.lehjr.mpalib.capabilities.heat.HeatCapability;
import com.github.lehjr.mpalib.heat.HeatUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class EntityDamageEvent {
    // LivingAttackEvent


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

    /**
     * Use this instead of the above method.
     * * @param event
     */
    @SubscribeEvent
    public static void entityAttackEventHandler(LivingAttackEvent event) {

    }
}
