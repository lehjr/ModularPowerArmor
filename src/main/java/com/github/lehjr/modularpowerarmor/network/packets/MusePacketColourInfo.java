package com.github.lehjr.modularpowerarmor.network.packets;

import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 12:28 PM, 5/6/13
 * <p>
 * Ported to Java by lehjr on 11/14/16.
 */
public class MusePacketColourInfo {
    protected int itemSlot;
    protected int[] tagData;

    public MusePacketColourInfo() {
    }

    public MusePacketColourInfo(int itemSlot, int[] tagData) {
        this.itemSlot = itemSlot;
        this.tagData = tagData;
    }

    public static void encode(MusePacketColourInfo msg, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.itemSlot);
        packetBuffer.writeVarIntArray(msg.tagData);
    }

    public static MusePacketColourInfo decode(PacketBuffer packetBuffer) {
        return new MusePacketColourInfo(packetBuffer.readInt(), packetBuffer.readVarIntArray());
    }

    public static void handle(MusePacketColourInfo message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity player = ctx.get().getSender();
            int itemSlot = message.itemSlot;
            int[] tagData = message.tagData;

            player.inventory.getStackInSlot(itemSlot).getCapability(ModelSpecNBTCapability.RENDER).ifPresent(
                    render -> {
                        render.setColorArray(tagData);
                    });

        });
        ctx.get().setPacketHandled(true);
    }
}