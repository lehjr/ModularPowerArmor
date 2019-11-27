package com.github.machinemuse.powersuits.network.packets;

import com.github.lehjr.mpalib.network.MuseByteBufferUtils;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class ToggleRequestPacket implements IMessage {
    String module;
    Boolean active;

    public ToggleRequestPacket() {
    }

    public ToggleRequestPacket(String module, Boolean active) {
        this.module = module;
        this.active = active;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.module = MuseByteBufferUtils.readUTF8String(buf);
        this.active = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        MuseByteBufferUtils.writeUTF8String(buf, module);
        buf.writeBoolean(active);
    }

    public static class Handler implements IMessageHandler<ToggleRequestPacket,IMessage> {
        @Override
        public IMessage onMessage(ToggleRequestPacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                final EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    String module = message.module;
                    Boolean active = message.active;
                    ModuleManager.INSTANCE.toggleModuleForPlayer(player, module, active);
                });
            }
            return null;
        }
    }
}
