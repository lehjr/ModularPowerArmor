/*
 * ModularPowersuits (Maintenance builds by lehjr)
 * Copyright (c) 2019 MachineMuse, Lehjr
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

import com.github.machinemuse.powersuits.config.MPSConfig;
import com.github.machinemuse.powersuits.config.MPSServerSettings;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Sync settings between server and client
 */
public class ConfigPacket implements IMessage {
    public ConfigPacket() {

    }

    // read settings from packet
    @Override
    public void fromBytes(ByteBuf buf) {
        MPSServerSettings settings = new MPSServerSettings(buf);
        MPSConfig.INSTANCE.setServerSettings(settings);
    }

    // write values to packet to send to the client
    @Override
    public void toBytes(ByteBuf buf) {
        if (MPSConfig.INSTANCE.getServerSettings() == null)
            MPSConfig.INSTANCE.setServerSettings(new MPSServerSettings());
        MPSConfig.INSTANCE.getServerSettings().writeToBuffer(buf);
    }

    public static class Handler implements IMessageHandler<ConfigPacket, IMessage> {
        @Override
        public IMessage onMessage(ConfigPacket message, MessageContext ctx) {
            return null;
        }
    }
}