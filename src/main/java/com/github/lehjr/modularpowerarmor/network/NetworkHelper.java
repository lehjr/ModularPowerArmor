package com.github.lehjr.modularpowerarmor.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public class NetworkHelper {
    public static PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
