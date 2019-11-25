package com.github.lehjr.modularpowerarmor.network.packets;

import com.github.lehjr.modularpowerarmor.client.gui.tinker.module.TinkerTableContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ModuleMoveFromSlotToSlotPacket implements IMessage {
    int windowId;
    int sourceSlot;
    int targetSlot;

    public ModuleMoveFromSlotToSlotPacket() {
    }

    public ModuleMoveFromSlotToSlotPacket(int windowIdIn, int sourceSlotIn, int targetSlotIn) {
        this.windowId = windowIdIn;
        this.sourceSlot = sourceSlotIn;
        this.targetSlot = targetSlotIn;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.windowId = buf.readInt();
        this.sourceSlot = buf.readInt();
        this.targetSlot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeInt(sourceSlot);
        buf.writeInt(targetSlot);
    }

    public static class Handler implements IMessageHandler<ModuleMoveFromSlotToSlotPacket, IMessage> {
        @Override
        public IMessage onMessage(ModuleMoveFromSlotToSlotPacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    if (player.openContainer != null && player.openContainer.windowId == message.windowId && player.openContainer instanceof TinkerTableContainer) {
                        TinkerTableContainer container = (TinkerTableContainer) player.openContainer;

                        Slot source = container.getSlot(message.sourceSlot);
                        Slot target = container.getSlot(message.targetSlot);

                        ItemStack itemStack = source.getStack();
                        ItemStack stackCopy = itemStack.copy();
                        // fixme: no idea if this will work with target set as range
                        if (source instanceof SlotCrafting && source.getHasStack()) {
                            container.consume(player);
                            if (container.mergeItemStack(itemStack, message.targetSlot, message.targetSlot + 1, false)) {
//                        source.onSlotChange(itemStack, stackCopy);
                                source.onTake(player, itemStack);
                            }
                        } else {
                            target.putStack(stackCopy);
                            source.decrStackSize(1);
                        }
                        player.openContainer.detectAndSendChanges();
                    }
                });
            }
            return null;
        }
    }
}