/*
 * Copyright (c) ${DATE} MachineMuse, Lehjr
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

package com.github.machinemuse.powersuits.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;
import java.util.List;

public class MusePlayerUtils {

    public static RayTraceResult raytraceEntities(World world, EntityPlayer player, boolean collisionFlag, double reachDistance) {
        RayTraceResult pickedEntity = null;
        Vec3d playerPosition = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d playerLook = player.getLookVec();
        Vec3d playerViewOffset = new Vec3d(
                playerPosition.x + playerLook.x * reachDistance,
                playerPosition.y + playerLook.y * reachDistance,
                playerPosition.z + playerLook.z * reachDistance);

        AxisAlignedBB boxToScan =
                player.getEntityBoundingBox().expand(playerLook.x * reachDistance,
                        playerLook.y * reachDistance,
                        playerLook.z * reachDistance);

        List entitiesHit = world.getEntitiesWithinAABBExcludingEntity(player, boxToScan);
        double closestEntity = reachDistance;

        if (entitiesHit == null || entitiesHit.isEmpty()) {
            return null;
        }
        for (Entity entityHit : (Iterable<Entity>) entitiesHit) {
            if (entityHit != null && entityHit.canBeCollidedWith() && entityHit.getEntityBoundingBox() != null) {
                float border = entityHit.getCollisionBorderSize();
                AxisAlignedBB aabb = entityHit.getEntityBoundingBox().expand((double) border, (double) border, (double) border);
                RayTraceResult raytraceResult = aabb.calculateIntercept(playerPosition, playerViewOffset);
                if (raytraceResult != null) {
                    if (aabb.contains(playerPosition)) {
                        if (0.0D < closestEntity || closestEntity == 0.0D) {
                            pickedEntity = new RayTraceResult(entityHit);
                            if (pickedEntity != null) {
                                pickedEntity.hitVec = raytraceResult.hitVec;
                                closestEntity = 0.0D;
                            }
                        }
                    } else {
                        double distance = playerPosition.distanceTo(raytraceResult.hitVec);

                        if (distance < closestEntity || closestEntity == 0.0D) {
                            pickedEntity = new RayTraceResult(entityHit);
                            pickedEntity.hitVec = raytraceResult.hitVec;
                            closestEntity = distance;
                        }
                    }
                }
            }
        }
        return pickedEntity;
    }

    public static RayTraceResult raytraceBlocks(World world, EntityPlayer player, boolean collisionFlag, double reachDistance) {
        Vec3d playerPosition = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d playerLook = player.getLookVec();

        Vec3d playerViewOffset = new Vec3d(playerPosition.x + playerLook.x * reachDistance, playerPosition.y
                + playerLook.y * reachDistance, playerPosition.z + playerLook.z * reachDistance);
        return world.rayTraceBlocks(playerPosition, playerViewOffset);
    }

    public static RayTraceResult doCustomRayTrace(World world, EntityPlayer player, boolean collisionFlag, double reachDistance) {
        // Somehow this destroys the playerPosition vector -.-
        RayTraceResult pickedBlock = raytraceBlocks(world, player, collisionFlag, reachDistance);
        RayTraceResult pickedEntity = raytraceEntities(world, player, collisionFlag, reachDistance);
        if (pickedBlock == null) {
            return pickedEntity;
        } else if (pickedEntity == null) {
            return pickedBlock;
        } else {
            Vec3d playerPosition = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            double dBlock = pickedBlock.hitVec.distanceTo(playerPosition);
            double dEntity = pickedEntity.hitVec.distanceTo(playerPosition);
            if (dEntity < dBlock) {
                return pickedEntity;
            } else {
                return pickedBlock;
            }
        }
    }

    public static void teleportEntity(EntityPlayer entityPlayer, RayTraceResult rayTraceResult) {
        if (rayTraceResult != null && entityPlayer instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entityPlayer;
            if (player.connection.netManager.isChannelOpen()) {
                switch (rayTraceResult.typeOfHit) {
                    case ENTITY:
                        player.setPositionAndUpdate(rayTraceResult.hitVec.x, rayTraceResult.hitVec.y, rayTraceResult.hitVec.z);
                        break;
                    case BLOCK:
                        double hitx = rayTraceResult.hitVec.x;
                        double hity = rayTraceResult.hitVec.y;
                        double hitz = rayTraceResult.hitVec.z;
                        switch (rayTraceResult.sideHit) {
                            case DOWN: // Bottom
                                hity -= 2;
                                break;
                            case UP: // Top
                                // hity += 1;
                                break;
                            case NORTH: // North
                                hitx -= 0.5;
                                break;
                            case SOUTH: // South
                                hitx += 0.5;
                                break;
                            case WEST: // West
                                hitz += 0.5;
                                break;
                            case EAST: // East
                                hitz -= 0.5;
                                break;
                        }

                        player.setPositionAndUpdate(hitx, hity, hitz);
                        break;
                    default:
                        break;

                }
            }
        }
    }



    public static double getPlayerCoolingBasedOnMaterial(@Nonnull EntityPlayer player) {
        // cheaper method of checking if player is in lava. Described as "non-chunkloading copy of Entity.isInLava()"
//        if (ModCompatibility.isEnderCoreLoaded()) {
//            if (EnderCoreMethods.isInLavaSafe(player))
//                return 0;
//        } else {
            if (player.isInLava()) // not a cheap
                return 0;
//        }

        double cool = ((2.0 - getBiome(player).getTemperature(new BlockPos((int) player.posX, (int) player.posY, (int) player.posZ)) / 2)); // Algorithm that returns a getValue from 0.0 -> 1.0. Biome temperature is from 0.0 -> 2.0

        if (player.isInWater())
            cool += 0.5;

        // If high in the air, increase cooling
        if ((int) player.posY > 128)
            cool += 0.5;

        // If nighttime and in the desert, increase cooling
        if (!player.world.isDaytime() && getBiome(player) instanceof BiomeDesert) {
            cool += 0.8;
        }

        // check for rain and if player is in the rain
        // check if rain can happen in the biome the player is in
        if (player.world.getBiome(player.getPosition()).canRain()
                // check if raining in the world
                && player.world.isRaining()
                // check if the player can see the sky
                && player.world.canBlockSeeSky(player.getPosition().add(0, 1, 0))) {
            cool += 0.2;
        }

        return cool;
    }

    public static Biome getBiome(EntityPlayer player) {
        Chunk chunk = player.world.getChunk(player.getPosition());
        return chunk.getBiome(player.getPosition(), player.world.getBiomeProvider());
    }
}