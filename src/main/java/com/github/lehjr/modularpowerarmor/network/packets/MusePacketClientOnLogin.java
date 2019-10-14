package com.github.lehjr.modularpowerarmor.network.packets;

import com.github.lehjr.modularpowerarmor.event.PlayerLoginHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Workaround for login event being fired server side only now
 */
public class MusePacketClientOnLogin {
    public MusePacketClientOnLogin() {
    }

    public static void encode(MusePacketClientOnLogin msg, PacketBuffer packetBuffer) {
    }

    public static MusePacketClientOnLogin decode(PacketBuffer packetBuffer) {
        return new MusePacketClientOnLogin();
    }

    public static void handle(MusePacketClientOnLogin message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            System.out.println("reception side is client: " +

            ctx.get().getDirection().getReceptionSide().isClient());


            final ServerPlayerEntity player = ctx.get().getSender();
            PlayerLoginHandler.clientPlayerLogin(player);
        });
        ctx.get().setPacketHandled(true);
    }
}