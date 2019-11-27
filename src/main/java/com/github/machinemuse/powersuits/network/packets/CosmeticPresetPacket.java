package com.github.machinemuse.powersuits.network.packets;

import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.network.MuseByteBufferUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;

public class CosmeticPresetPacket implements IMessage {
    int itemSlot;
    String presetName;

    public CosmeticPresetPacket() {
    }

    public CosmeticPresetPacket(int itemSlot, String presetName) {
        this.itemSlot = itemSlot;
        this.presetName = presetName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        itemSlot = buf.readInt();
        presetName = MuseByteBufferUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(itemSlot);
        MuseByteBufferUtils.writeUTF8String(buf, presetName);
    }

    public static class Handler implements IMessageHandler<CosmeticPresetPacket, IMessage> {
        @Override
        public IMessage onMessage(CosmeticPresetPacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                final EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    Optional.ofNullable(player.inventory.getStackInSlot(message.itemSlot)
                            .getCapability(ModelSpecNBTCapability.RENDER, null))
                            .ifPresent(spec->{
                                String presetName = message.presetName;
                                spec.setPresetTag(presetName);
                            });
                });
            }
            return null;
        }
    }
}