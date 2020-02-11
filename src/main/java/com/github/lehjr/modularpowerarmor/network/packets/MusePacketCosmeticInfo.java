package com.github.lehjr.modularpowerarmor.network.packets;

import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 12:28 PM, 5/6/13
 * <p>
 * Ported to Java by lehjr on 11/14/16.
 */
public class MusePacketCosmeticInfo {
    protected static int itemSlot;
    protected String tagName;
    protected CompoundNBT tagData;

    public MusePacketCosmeticInfo() {
    }

    public MusePacketCosmeticInfo(int itemSlot, String tagName, CompoundNBT tagData) {
        this.itemSlot = itemSlot;
        this.tagName = tagName;
        this.tagData = tagData;
    }

    public static void encode(MusePacketCosmeticInfo msg, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.itemSlot);
        packetBuffer.writeString(msg.tagName);
        packetBuffer.writeCompoundTag(msg.tagData);
    }

    public static MusePacketCosmeticInfo decode(PacketBuffer packetBuffer) {
        return new MusePacketCosmeticInfo(
                packetBuffer.readInt(),
                packetBuffer.readString(500),
                packetBuffer.readCompoundTag());
    }

    public static void handle(MusePacketCosmeticInfo message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity player = ctx.get().getSender();
            int itemSlot = message.itemSlot;
            String tagName = message.tagName;
            CompoundNBT tagData = message.tagData;
            player.inventory.getStackInSlot(itemSlot).getCapability(ModelSpecNBTCapability.RENDER).ifPresent(render->{
                render.setRenderTag(tagData, tagName);
            });
        });
        ctx.get().setPacketHandled(true);
    }
}