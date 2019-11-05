package com.github.lehjr.modularpowerarmor.network.packets;

import com.github.lehjr.modularpowerarmor.basemod.ModularPowerArmor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CraftingGuiServerSidePacket implements IMessage {
    public CraftingGuiServerSidePacket() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<CraftingGuiServerSidePacket, IMessage> {
        @Override
        public IMessage onMessage(CraftingGuiServerSidePacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                final EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    player.openGui(ModularPowerArmor.getInstance(), 3, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
                });
            }
            return null;
        }
    }
}
