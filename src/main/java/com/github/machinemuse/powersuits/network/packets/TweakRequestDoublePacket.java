/*
 * Copyright (c) ${DATE} MachineMuse, Lehjr
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.machinemuse.powersuits.network.packets;

import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.github.lehjr.mpalib.network.MuseByteBufferUtils;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class TweakRequestDoublePacket implements IMessage {
    int itemSlot;
    String moduleName;
    String tweakName;
    double tweakValue;

    public TweakRequestDoublePacket() {
    }

    public TweakRequestDoublePacket(int itemSlot, String moduleName, String tweakName, double tweakValue) {
        this.itemSlot = itemSlot;
        this.moduleName = moduleName;
        this.tweakName = tweakName;
        this.tweakValue = tweakValue;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.itemSlot = buf.readInt();
        this.moduleName = MuseByteBufferUtils.readUTF8String(buf);
        this.tweakName = MuseByteBufferUtils.readUTF8String(buf);
        this.tweakValue =buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.itemSlot);
        MuseByteBufferUtils.writeUTF8String(buf, this.moduleName);
        MuseByteBufferUtils.writeUTF8String(buf, this.tweakName);
        buf.writeDouble(this.tweakValue);
    }

    public static class Handler implements IMessageHandler<TweakRequestDoublePacket, IMessage> {
        @Override
        public IMessage onMessage(TweakRequestDoublePacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                final EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    int itemSlot = message.itemSlot;
                    String moduleName = message.moduleName;
                    String tweakName = message.tweakName;
                    double tweakValue = message.tweakValue;
                    if (moduleName != null && tweakName != null) {
                        ItemStack stack = player.inventory.getStackInSlot(itemSlot);
                        NBTTagCompound itemTag = NBTUtils.getMuseItemTag(stack);

                        if (itemTag != null && ModuleManager.INSTANCE.tagHasModule(itemTag, moduleName)) {
                            NBTUtils.removeMuseValuesTag(stack);
                            NBTTagCompound moduleTag = itemTag.getCompoundTag(moduleName);
                            moduleTag.setDouble(tweakName, tweakValue);
                        }
                    }
                });
            }
            return null;
        }
    }
}