package com.github.lehjr.modularpowerarmor.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

/**
 * creates a new itemstack on both client and server side
 */
public class CreativeModuleInstallRequestPacket implements IMessage {
    int windowId;
    int slotId;
    ItemStack itemStack;

    public CreativeModuleInstallRequestPacket() {
    }

    public CreativeModuleInstallRequestPacket(int windowIdIn, int slotIdIn, @Nonnull ItemStack itemStackIn) {
        windowId = windowIdIn;
        slotId = slotIdIn;
        itemStack = itemStackIn;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeInt(slotId);
        ByteBufUtils.writeItemStack(buf, itemStack);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        windowId = buf.readInt();
        slotId = buf.readInt();
        itemStack = ByteBufUtils.readItemStack(buf);
    }

    public static class Handler implements IMessageHandler<CreativeModuleInstallRequestPacket, IMessage> {
        @Override
        public IMessage onMessage(CreativeModuleInstallRequestPacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    if (player.openContainer != null && player.openContainer.windowId == message.windowId) {
                        player.openContainer.putStackInSlot(message.slotId, message.itemStack);
//                player.openContainer.detectAndSendChanges();
                    }
                });
            }
            return null;
        }
    }
}