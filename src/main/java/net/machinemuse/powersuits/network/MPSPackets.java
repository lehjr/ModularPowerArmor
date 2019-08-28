package net.machinemuse.powersuits.network;

import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.network.packets.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MPSPackets {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL_INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MPSConstants.MODID, "data"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMPSPackets() {
        int i = 0;
        CHANNEL_INSTANCE.registerMessage(
                i,
                MusePacketColourInfo.class,
                MusePacketColourInfo::encode,
                MusePacketColourInfo::decode,
                MusePacketColourInfo::handle);
        i ++;

        CHANNEL_INSTANCE.registerMessage(
                i,
                MusePacketCosmeticInfo.class,
                MusePacketCosmeticInfo::encode,
                MusePacketCosmeticInfo::decode,
                MusePacketCosmeticInfo::handle);
        i++;

        CHANNEL_INSTANCE.registerMessage(
                i,
                MusePacketCosmeticPreset.class,
                MusePacketCosmeticPreset::encode,
                MusePacketCosmeticPreset::decode,
                MusePacketCosmeticPreset::handle);
        i++;

        CHANNEL_INSTANCE.registerMessage(
                i,
                MusePacketCosmeticPresetUpdate.class,
                MusePacketCosmeticPresetUpdate::encode,
                MusePacketCosmeticPresetUpdate::decode,
                MusePacketCosmeticPresetUpdate::handle);
        i++;

        CHANNEL_INSTANCE.registerMessage(
                i,
                MusePacketInstallModuleRequest.class,
                MusePacketInstallModuleRequest::encode,
                MusePacketInstallModuleRequest::decode,
                MusePacketInstallModuleRequest::handle);
        i++;

        CHANNEL_INSTANCE.registerMessage(
                i,
                MusePacketSalvageModuleRequest.class,
                MusePacketSalvageModuleRequest::encode,
                MusePacketSalvageModuleRequest::decode,
                MusePacketSalvageModuleRequest::handle);
        i++;

        CHANNEL_INSTANCE.registerMessage(
                i,
                MusePacketTweakRequestDouble.class,
                MusePacketTweakRequestDouble::encode,
                MusePacketTweakRequestDouble::decode,
                MusePacketTweakRequestDouble::handle);
        i++;

        CHANNEL_INSTANCE.registerMessage(
                i,
                ModeChangingRequest.class,
                ModeChangingRequest::encode,
                ModeChangingRequest::decode,
                ModeChangingRequest::handle);
        i++;
    }

    public SimpleChannel getWrapper() {
        return CHANNEL_INSTANCE;
    }
}