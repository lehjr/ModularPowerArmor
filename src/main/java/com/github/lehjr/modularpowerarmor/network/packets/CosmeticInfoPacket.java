package com.github.lehjr.modularpowerarmor.network.packets;

import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.network.MuseByteBufferUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 12:28 PM, 5/6/13
 * <p>
 * Ported to Java by lehjr on 11/14/16.
 */
public class CosmeticInfoPacket implements IMessage {
    int itemSlot;
    String tagName;
    NBTTagCompound tagData;

    public CosmeticInfoPacket(){
    }

    public CosmeticInfoPacket(int itemSlot, String tagName, NBTTagCompound tagData) {
        this.itemSlot = itemSlot;
        this.tagName = tagName;
        this.tagData = tagData;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.itemSlot = buf.readInt();
        this.tagName = MuseByteBufferUtils.readUTF8String(buf);
        this.tagData = MuseByteBufferUtils.readCompressedNBT(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(itemSlot);
        MuseByteBufferUtils.writeUTF8String(buf, tagName);
        MuseByteBufferUtils.writeCompressedNBT(buf, tagData);
    }

    public static class Handler implements IMessageHandler<CosmeticInfoPacket, IMessage> {
        @Override
        public IMessage onMessage(CosmeticInfoPacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                final EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    int itemSlot = message.itemSlot;
                    String tagName = message.tagName;
                    NBTTagCompound tagData = message.tagData;
                    Optional.ofNullable(player.inventory.getStackInSlot(itemSlot).getCapability(ModelSpecNBTCapability.RENDER, null)).ifPresent(render->{
                        render.setMuseRenderTag(tagData, tagName);
                    });
                });
            }
            return null;
        }
    }
}