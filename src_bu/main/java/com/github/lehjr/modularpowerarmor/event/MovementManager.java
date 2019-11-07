package com.github.lehjr.modularpowerarmor.event;

import com.google.common.util.concurrent.AtomicDouble;
import com.github.lehjr.mpalib.basemod.MPALibConfig;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.client.sound.Musique;
import com.github.lehjr.mpalib.control.PlayerMovementInputWrapper;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.math.MathUtils;
import com.github.lehjr.mpalib.player.PlayerUtils;
import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;

import com.github.lehjr.modularpowerarmor.client.event.RenderEventHandler;
import com.github.lehjr.modularpowerarmor.client.sound.SoundDictionary;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MovementManager {
    static final double root2 = Math.sqrt(2);
    public static final Map<UUID, Double> playerJumpMultipliers = new HashMap();
    /**
     * Gravity, in meters per tick per tick.
     */
    public static final double DEFAULT_GRAVITY = -0.0784000015258789;

    public static double getPlayerJumpMultiplier(PlayerEntity player) {
        if (playerJumpMultipliers.containsKey(player.getUniqueID())) {
            return playerJumpMultipliers.get(player.getUniqueID());
        } else {
            return 0;
        }
    }

    public static void setPlayerJumpTicks(PlayerEntity player, double number) {
        playerJumpMultipliers.put(player.getUniqueID(), number);
    }

    public static double computeFallHeightFromVelocity(double velocity) {
        double ticks = velocity / DEFAULT_GRAVITY;
        return -0.5 * DEFAULT_GRAVITY * ticks * ticks;
    }

    static final ResourceLocation kineticGen = new ResourceLocation(MPARegistryNames.MODULE_KINETIC_GENERATOR__REGNAME);
    // moved here so it is still accessible if sprint assist module isn't installed.
    public static void setMovementModifier(ItemStack itemStack, double multiplier, PlayerEntity player) {
        // reduce player speed according to Kinetic Energy Generator setting
        AtomicDouble movementResistance = new AtomicDouble(0);
        itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iModularItem -> {
            if (iModularItem instanceof IModularItem)
                ((IModularItem) iModularItem).getOnlineModuleOrEmpty(kineticGen).getCapability(PowerModuleCapability.POWER_MODULE)
                        .ifPresent(kin->{
                            movementResistance.set(kin.applyPropertyModifiers(Constants.MOVEMENT_RESISTANCE));
                        });
        });
        multiplier -= movementResistance.get();
        // player walking speed: 0.10000000149011612
        // player sprintint speed: 0.13000001
        double additive = multiplier * (player.isSprinting() ? 0.13 : 0.1)/2;
        NBTTagCompound itemNBT = itemStack.getOrCreateTag();
        boolean hasAttribute = false;

        if (itemNBT.contains("AttributeModifiers", Constants.NBT.TAG_LIST)) {
            ListNBT listnbt = itemNBT.getList("AttributeModifiers", Constants.NBT.TAG_COMPOUND);
            int remove = -1;

            for (int i = 0; i < listnbt.size(); ++i) {
                NBTTagCompound attributeTag = listnbt.getCompound(i);
                AttributeModifier attributemodifier = SharedMonsterAttributes.readAttributeModifier(attributeTag);
                if (attributemodifier != null && attributemodifier.getName().equals(SharedMonsterAttributes.MOVEMENT_SPEED.getName())) {
                    // adjust the tag
                    if (additive != 0) {
                        attributeTag.putDouble("Amount", additive);
                        hasAttribute = true;
                        break;
                    } else {
                        // discard the tag
                        remove = i;
                        break;
                    }
                }
            }
            if (hasAttribute && remove != -1) {
                listnbt.remove(remove);
            }
        }
        if (!hasAttribute && additive != 0) {
            itemStack.addAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), additive, AttributeModifier.Operation.ADDITION), EquipmentSlotType.LEGS);
        }
    }

    public static double thrust(PlayerEntity player, double thrust, boolean flightControl) {
        PlayerMovementInputWrapper.PlayerMovementInput playerInput = PlayerMovementInputWrapper.get(player);
        double thrustUsed = 0;
        if (flightControl) {
            Vec3d desiredDirection = player.getLookVec().normalize();
            double strafeX = desiredDirection.z;
            double strafeZ = -desiredDirection.x;
            double flightVerticality = 0;
            ItemStack helm = player.getItemStackFromSlot(EquipmentSlotType.HEAD);
            flightVerticality = helm.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(iModularItem -> {
                if (iModularItem instanceof IModularItem)
                    return ((IModularItem) iModularItem)
                            .getOnlineModuleOrEmpty(RenderEventHandler.flightControl)
                            .getCapability(PowerModuleCapability.POWER_MODULE)
                            .map(pm->pm.applyPropertyModifiers(Constants.FLIGHT_VERTICALITY)).orElse(0D);
                else
                    return 0D;
            }).orElse(0D);

            desiredDirection = new Vec3d(
                    (desiredDirection.x * Math.signum(playerInput.moveForward) + strafeX * Math.signum(playerInput.moveStrafe)),
                    (flightVerticality * desiredDirection.y * Math.signum(playerInput.moveForward) + (playerInput.jumpKey ? 1 : 0) - (playerInput.downKey ? 1 : 0)),
                    (desiredDirection.z * Math.signum(playerInput.moveForward) + strafeZ * Math.signum(playerInput.moveStrafe)));

            desiredDirection = desiredDirection.normalize();

            // Brakes
            if (player.getMotion().y < 0 && desiredDirection.y >= 0) {
                if (-player.getMotion().y > thrust) {
                    player.setMotion(player.getMotion().add(0, thrust,0));
                    thrustUsed += thrust;
                    thrust = 0;
                } else {
                    thrust -= player.getMotion().y;
                    thrustUsed += player.getMotion().y;
                    player.setMotion(player.getMotion().x, 0, player.getMotion().z);
                }
            }
            if (player.getMotion().y < -1) {
                thrust += 1 + player.getMotion().y;
                thrustUsed -= 1 + player.getMotion().y;
                player.setMotion(player.getMotion().x, -1, player.getMotion().z);
            }
            if (Math.abs(player.getMotion().x) > 0 && desiredDirection.length() == 0) {
                if (Math.abs(player.getMotion().x) > thrust) {
                    player.setMotion(player.getMotion().add(
                            - Math.signum(player.getMotion().x) * thrust, 0, 0));
                    thrustUsed += thrust;
                    thrust = 0;
                } else {
                    thrust -= Math.abs(player.getMotion().x);
                    thrustUsed += Math.abs(player.getMotion().x);
                    player.setMotion(0, player.getMotion().y, player.getMotion().z);
                }
            }
            if (Math.abs(player.getMotion().z) > 0 && desiredDirection.length() == 0) {
                if (Math.abs(player.getMotion().z) > thrust) {
                    player.setMotion(
                            player.getMotion().add(
                                    0, 0, Math.signum(player.getMotion().z) * thrust
                            )

                    );
                    thrustUsed += thrust;
                    thrust = 0;
                } else {
                    thrustUsed += Math.abs(player.getMotion().z);
                    thrust -= Math.abs(player.getMotion().z);
                    player.setMotion(player.getMotion().x, player.getMotion().y, 0);
                }
            }

            // Thrusting, finally :V
            player.setMotion(player.getMotion().add(
                    thrust * desiredDirection.x,
                    thrust * desiredDirection.y,
                    thrust * desiredDirection.z
            ));
            thrustUsed += thrust;

        } else {
            Vec3d playerHorzFacing = player.getLookVec();
            playerHorzFacing = new Vec3d(playerHorzFacing.x, 0, playerHorzFacing.z);
            playerHorzFacing.normalize();
            if (playerInput.moveForward == 0) {
                player.setMotion(player.getMotion().add(0, thrust, 0));
            } else {
                player.setMotion(player.getMotion().add(
                        playerHorzFacing.x * thrust / root2 * Math.signum(playerInput.moveForward),
                        thrust / root2,
                        playerHorzFacing.z * thrust / root2 * Math.signum(playerInput.moveForward)
                ));
            }
            thrustUsed += thrust;
        }

        // Slow the player if they are going too fast
        double horzm2 = player.getMotion().x * player.getMotion().x + player.getMotion().z * player.getMotion().z;

        // currently comes out to 0.0625
        double horizontalLimit = MPAConfig.GENERAL_MAX_FLYING_SPEED.get() * MPAConfig.GENERAL_MAX_FLYING_SPEED.get() / 400;

//        double playerVelocity = Math.abs(player.getMotion().x) + Math.abs(player.getMotion().y) + Math.abs(player.getMotion().z);

        if (playerInput.sneakKey && horizontalLimit > 0.05) {
            horizontalLimit = 0.05;
        }

        if (horzm2 > horizontalLimit) {
            double ratio = Math.sqrt(horizontalLimit / horzm2);
            player.setMotion(
                    player.getMotion().x * ratio,
                    player.getMotion().y,
                    player.getMotion().z * ratio);
        }
        PlayerUtils.resetFloatKickTicks(player);
        return thrustUsed;
    }

    public static double computePlayerVelocity(PlayerEntity player) {
        return MathUtils.pythag(player.getMotion().x, player.getMotion().y, player.getMotion().z);
    }


    static final ResourceLocation jumpAssist = new ResourceLocation(MPARegistryNames.MODULE_JUMP_ASSIST__REGNAME);
    @SubscribeEvent
    public void handleLivingJumpEvent(LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            player.getItemStackFromSlot(EquipmentSlotType.LEGS).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iModularItem -> {
                if (!(iModularItem instanceof IModularItem))
                    return;

                ((IModularItem) iModularItem).getOnlineModuleOrEmpty(jumpAssist).getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(jumper -> {
                    double jumpAssist = jumper.applyPropertyModifiers(Constants.MULTIPLIER) * 2;
                    double drain = jumper.applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
                    int avail = ElectricItemUtils.getPlayerEnergy(player);
                    if ((player.world.isRemote()) && MPALibConfig.useSounds()) {
                        Musique.playerSound(player, SoundDictionary.SOUND_EVENT_JUMP_ASSIST, SoundCategory.PLAYERS, (float) (jumpAssist / 8.0), (float) 1, false);
                    }

                    if (drain < avail) {
                        ElectricItemUtils.drainPlayerEnergy(player, (int) drain);
                        setPlayerJumpTicks(player, jumpAssist);
                        double jumpCompensationRatio = jumper.applyPropertyModifiers(Constants.FOOD_COMPENSATION);
                        if (player.isSprinting()) {
                            player.getFoodStats().addExhaustion((float) (-0.2F * jumpCompensationRatio));
                        } else {
                            player.getFoodStats().addExhaustion((float) (-0.05F * jumpCompensationRatio));
                        }
                    }
                });
            });
        }
    }

    private static final ResourceLocation shockAbsorbersReg = new ResourceLocation(MPARegistryNames.MODULE_SHOCK_ABSORBER__REGNAME);

    @SubscribeEvent
    public void handleFallEvent(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity && event.getDistance() > 3.0) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            ItemStack boots = player.getItemStackFromSlot(EquipmentSlotType.FEET);
            boots.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iModularItem -> {
                if (!(iModularItem instanceof IModularItem))
                    return;

                ItemStack shockAbsorbers = ((IModularItem) iModularItem).getOnlineModuleOrEmpty(shockAbsorbersReg);
                shockAbsorbers.getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(sa -> {
                    double distanceAbsorb = event.getDistance() * sa.applyPropertyModifiers(Constants.MULTIPLIER);
                    if (player.world.isRemote && MPALibConfig.useSounds()) {
                        Musique.playerSound(player, SoundDictionary.SOUND_EVENT_GUI_INSTALL, SoundCategory.PLAYERS, (float) (distanceAbsorb), (float) 1, false);
                    }
                    double drain = distanceAbsorb * sa.applyPropertyModifiers(Constants.ENERGY_CONSUMPTION);
                    int avail = ElectricItemUtils.getPlayerEnergy(player);
                    if (drain < avail) {
                        ElectricItemUtils.drainPlayerEnergy(player, (int) drain);
                        event.setDistance((float) (event.getDistance() - distanceAbsorb));
//                        event.getEntityLiving().sendMessage(new TextComponentString("modified fall settings: [ damage : " + event.getDamageMultiplier() + " ], [ distance : " + event.getDistance() + " ]"));
                    }
                });
            });
        }
    }
}
