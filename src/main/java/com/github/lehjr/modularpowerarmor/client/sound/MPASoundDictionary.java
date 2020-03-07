package com.github.lehjr.modularpowerarmor.client.sound;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MPAConstants.MOD_ID, value = Dist.CLIENT)
public class MPASoundDictionary {
    public static SoundEvent SOUND_EVENT_GLIDER = initSound("glider");
    public static SoundEvent SOUND_EVENT_JETBOOTS = initSound("jet_boots");
    public static SoundEvent SOUND_EVENT_JETPACK = initSound("jetpack");
    public static SoundEvent SOUND_EVENT_JUMP_ASSIST = initSound("jump_assist");
    public static SoundEvent SOUND_EVENT_SWIM_ASSIST = initSound("swim_assist");
    public static SoundEvent SOUND_EVENT_ELECTROLYZER = initSound("water_electrolyzer");

    static {
        new MPASoundDictionary();
    }

    @SubscribeEvent
    public static void registerSoundEvent(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                SOUND_EVENT_GLIDER,
                SOUND_EVENT_JETBOOTS,
                SOUND_EVENT_JETPACK,
                SOUND_EVENT_JUMP_ASSIST,
                SOUND_EVENT_SWIM_ASSIST,
                SOUND_EVENT_ELECTROLYZER);
    }

    private static SoundEvent initSound(String soundName) {
        ResourceLocation location = new ResourceLocation(MPAConstants.MOD_ID, soundName);
        SoundEvent event = new SoundEvent(location).setRegistryName(location);
        return event;
    }
}
