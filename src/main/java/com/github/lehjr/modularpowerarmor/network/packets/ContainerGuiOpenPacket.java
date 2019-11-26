package com.github.lehjr.modularpowerarmor.network.packets;

import com.github.lehjr.modularpowerarmor.basemod.ModularPowerArmor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author lehjr
 */
public class ContainerGuiOpenPacket implements IMessage {
    int id;

    public ContainerGuiOpenPacket() {

    }

    public ContainerGuiOpenPacket(int id) {
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
    }

    public static class Handler implements IMessageHandler<ContainerGuiOpenPacket, IMessage> {
        @Override
        public IMessage onMessage(ContainerGuiOpenPacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                final EntityPlayerMP player = ctx.getServerHandler().player;
                    player.getServerWorld().addScheduledTask(() -> {
                        player.openGui(ModularPowerArmor.getInstance(), message.id, player.world,
                                (int)player.posX, (int)player.posY, (int)player.posZ);
                    });
                }
            return null;
        }
    }
}