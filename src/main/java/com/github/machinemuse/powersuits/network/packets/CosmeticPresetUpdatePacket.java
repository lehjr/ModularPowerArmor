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

import com.github.lehjr.mpalib.network.MuseByteBufferUtils;
import com.github.machinemuse.powersuits.config.CosmeticPresetSaveLoad;
import com.github.machinemuse.powersuits.config.MPSConfig;
import com.github.machinemuse.powersuits.config.MPSServerSettings;
import com.github.machinemuse.powersuits.config.MPSSettings;
import com.github.machinemuse.powersuits.network.MPSPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CosmeticPresetUpdatePacket implements IMessage {
    ResourceLocation registryName;
    String name;
    NBTTagCompound cosmeticSettings;

    public CosmeticPresetUpdatePacket() {

    }

    public CosmeticPresetUpdatePacket(ResourceLocation registryNameIn, String nameIn, NBTTagCompound cosmeticSettingsIn) {
        this.registryName = registryNameIn;
        this.name = nameIn;
        this.cosmeticSettings = cosmeticSettingsIn;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.registryName = new ResourceLocation(MuseByteBufferUtils.readUTF8String(buf));
        this.name = MuseByteBufferUtils.readUTF8String(buf);
        this.cosmeticSettings = MuseByteBufferUtils.readCompressedNBT(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        MuseByteBufferUtils.writeUTF8String(buf, registryName.toString());
        MuseByteBufferUtils.writeUTF8String(buf, name);
        MuseByteBufferUtils.writeCompressedNBT(buf, cosmeticSettings);
    }

    public static class Handler implements IMessageHandler<CosmeticPresetUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(CosmeticPresetUpdatePacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                boolean allowCosmeticPresetCreation;
                final EntityPlayerMP player = ctx.getServerHandler().player;
                // check if player is the server owner
                if (FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) {
                    allowCosmeticPresetCreation = player.getName().equals(FMLCommonHandler.instance().getMinecraftServerInstance().getServerOwner());
                } else {
                    // check if player is top level op
                    UserListOpsEntry opEntry = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());
                    int opLevel = opEntry != null ? opEntry.getPermissionLevel() : 0;
                    allowCosmeticPresetCreation = opLevel == 4;
                }
                if(allowCosmeticPresetCreation) {
                    player.getServerWorld().addScheduledTask(() -> {
                        ResourceLocation registryName = message.registryName;
                        String name = message.name;
                        NBTTagCompound cosmeticSettings = message.cosmeticSettings;
                        MPSServerSettings settings = MPSConfig.INSTANCE.getServerSettings();
                        if (settings != null) {
                            settings.updateCosmeticInfo(registryName, name, cosmeticSettings);
                            MPSPackets.sendToAll(new CosmeticPresetUpdatePacket(registryName, name, cosmeticSettings));
                        } else {
                            MPSSettings.cosmetics.updateCosmeticInfo(registryName, name, cosmeticSettings);
                        }
                        if (CosmeticPresetSaveLoad.savePreset(registryName, name, cosmeticSettings))
                            player.sendMessage(new TextComponentTranslation("gui.powersuits.savesuccessful"));
//                        else
//                            player.sendMessage(new TextComponentTranslation("gui.powersuits.fail"));
                    });
                }
            } else {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    ResourceLocation registryName = message.registryName;
                    String name = message.name;
                    NBTTagCompound cosmeticSettings = message.cosmeticSettings;
                    MPSServerSettings settings = MPSConfig.INSTANCE.getServerSettings();
                    settings.updateCosmeticInfo(registryName, name, cosmeticSettings);
                });
            }
            return null;
        }
    }
}