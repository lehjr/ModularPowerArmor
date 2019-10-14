package com.github.lehjr.modularpowerarmor.network;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.network.packets.*;
import com.github.lehjr.modularpowerarmor.network.packets.reworked_crafting_packets.CPlaceRecipePacket;
import com.github.lehjr.modularpowerarmor.network.packets.reworked_crafting_packets.SPlaceGhostRecipePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MPAPackets {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL_INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MPAConstants.MODID, "data"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMPSPackets() {
        int i = 0;
        CHANNEL_INSTANCE.registerMessage(
                i++,
                MusePacketColourInfo.class,
                MusePacketColourInfo::encode,
                MusePacketColourInfo::decode,
                MusePacketColourInfo::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                MusePacketCosmeticInfo.class,
                MusePacketCosmeticInfo::encode,
                MusePacketCosmeticInfo::decode,
                MusePacketCosmeticInfo::handle);

//        CHANNEL_INSTANCE.registerMessage(
//                i++,
//                MusePacketCosmeticPreset.class,
//                MusePacketCosmeticPreset::encode,
//                MusePacketCosmeticPreset::decode,
//                MusePacketCosmeticPreset::handle);

//        CHANNEL_INSTANCE.registerMessage(
//                i++,
//                MusePacketCosmeticPresetUpdate.class,
//                MusePacketCosmeticPresetUpdate::encode,
//                MusePacketCosmeticPresetUpdate::decode,
//                MusePacketCosmeticPresetUpdate::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                MusePacketCreativeInstallModuleRequest.class,
                MusePacketCreativeInstallModuleRequest::encode,
                MusePacketCreativeInstallModuleRequest::decode,
                MusePacketCreativeInstallModuleRequest::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                MusePacketModuleMoveFromSlotToSlot.class,
                MusePacketModuleMoveFromSlotToSlot::encode,
                MusePacketModuleMoveFromSlotToSlot::decode,
                MusePacketModuleMoveFromSlotToSlot::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                CPlaceRecipePacket.class,
                CPlaceRecipePacket::encode,
                CPlaceRecipePacket::decode,
                CPlaceRecipePacket::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                SPlaceGhostRecipePacket.class,
                SPlaceGhostRecipePacket::encode,
                SPlaceGhostRecipePacket::decode,
                SPlaceGhostRecipePacket::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                MusePacketClientOnLogin.class,
                MusePacketClientOnLogin::encode,
                MusePacketClientOnLogin::decode,
                MusePacketClientOnLogin::handle);

        CHANNEL_INSTANCE.registerMessage(
                i++,
                ContainerGuiOpenPacket.class,
                ContainerGuiOpenPacket::encode,
                ContainerGuiOpenPacket::decode,
                ContainerGuiOpenPacket::handle);

    }

    public SimpleChannel getWrapper() {
        return CHANNEL_INSTANCE;
    }
}