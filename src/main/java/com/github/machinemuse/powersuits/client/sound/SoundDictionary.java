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

package com.github.machinemuse.powersuits.client.sound;

import com.github.machinemuse.powersuits.api.constants.MPSModConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = MPSModConstants.MODID, value = Side.CLIENT)
public class SoundDictionary {
    private static final String SOUND_PREFIX = "powersuits:";
    public static SoundEvent SOUND_EVENT_GLIDER = registerSound("glider");
    public static SoundEvent SOUND_EVENT_GUI_INSTALL = registerSound("gui_install");
    public static SoundEvent SOUND_EVENT_GUI_SELECT = registerSound("gui_select");
    public static SoundEvent SOUND_EVENT_JETBOOTS = registerSound("jet_boots");
    public static SoundEvent SOUND_EVENT_JETPACK = registerSound("jetpack");
    public static SoundEvent SOUND_EVENT_JUMP_ASSIST = registerSound("jump_assist");
    public static SoundEvent SOUND_EVENT_MPS_BOOP = registerSound("mmmps_boop");
    public static SoundEvent SOUND_EVENT_SWIM_ASSIST = registerSound("swim_assist");
    public static SoundEvent SOUND_EVENT_ELECTROLYZER = registerSound("water_electrolyzer");

    static {
        new SoundDictionary();
    }

    @SubscribeEvent
    public static void registerSoundEvent(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                SOUND_EVENT_GLIDER,
                SOUND_EVENT_GUI_INSTALL,
                SOUND_EVENT_GUI_SELECT,
                SOUND_EVENT_JETBOOTS,
                SOUND_EVENT_JETPACK,
                SOUND_EVENT_JUMP_ASSIST,
                SOUND_EVENT_MPS_BOOP,
                SOUND_EVENT_SWIM_ASSIST,
                SOUND_EVENT_ELECTROLYZER);
    }

    private static SoundEvent registerSound(String soundName) {
        ResourceLocation location = new ResourceLocation(MPSModConstants.MODID, soundName);
        SoundEvent event = new SoundEvent(location).setRegistryName(location);
        return event;
    }
}
