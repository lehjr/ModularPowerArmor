package com.github.lehjr.modularpowerarmor.network.packets;

import com.github.lehjr.mpalib.basemod.MPALIbConstants;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.legacy.item.IModularItem;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.github.lehjr.mpalib.network.MuseByteBufferUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
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
public class ColourInfoPacket implements IMessage {
    int itemSlot;
    int[] tagData;

    public ColourInfoPacket() {

    }

    public ColourInfoPacket(int itemSlot, int[] tagData) {
        this.itemSlot = itemSlot;
        this.tagData = tagData;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.itemSlot = buf.readInt();
        this.tagData = MuseByteBufferUtils.readIntArray(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(itemSlot);
        MuseByteBufferUtils.writeIntArray(buf, tagData);
    }

    public static class Handler implements IMessageHandler<ColourInfoPacket, IMessage> {
        @Override
        public IMessage onMessage(ColourInfoPacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                final EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    int itemSlot = message.itemSlot;
                    int[] tagData = message.tagData;
                    Optional.ofNullable(player.inventory.getStackInSlot(itemSlot).getCapability(ModelSpecNBTCapability.RENDER, null)).ifPresent(
                            render -> {
                                render.setColorArray(tagData);
                            });
                });
            }
            return null;
        }
    }
}