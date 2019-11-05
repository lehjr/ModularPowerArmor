package com.github.lehjr.modularpowerarmor.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;

public class PlayerUtils {
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