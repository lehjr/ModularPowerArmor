package net.machinemuse.powersuits.network.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * creates a new itemstack on both client and server side
 */
public class MusePacketCreativeInstallModuleRequest {
    int windowId;
    int slotId;
    ItemStack itemStack;

    public MusePacketCreativeInstallModuleRequest(int windowIdIn, int slotIdIn, @Nonnull ItemStack itemStackIn) {
        windowId = windowIdIn;
        slotId = slotIdIn;
        itemStack = itemStackIn;
    }

    public static void encode(MusePacketCreativeInstallModuleRequest msg, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.windowId);
        packetBuffer.writeInt(msg.slotId);
        packetBuffer.writeItemStack(msg.itemStack);
    }

    public static MusePacketCreativeInstallModuleRequest decode(PacketBuffer packetBuffer) {
        return new MusePacketCreativeInstallModuleRequest(
                packetBuffer.readInt(),
                packetBuffer.readInt(),
                packetBuffer.readItemStack());
    }


    public static void handle(MusePacketCreativeInstallModuleRequest message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player.openContainer != null && player.openContainer.windowId == message.windowId) {
                player.openContainer.putStackInSlot(message.slotId, message.itemStack);
//                player.openContainer.detectAndSendChanges();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}