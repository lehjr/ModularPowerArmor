package net.machinemuse.powersuits.network;

import net.machinemuse.powersuits.api.constants.MPSModConstants;
import net.machinemuse.powersuits.network.packets.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MPSPackets {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MPSModConstants.MODID);

    public static void registerMPSPackets() {
        int i = 0;

        INSTANCE.registerMessage(MPSPacketConfig.Handler.class, MPSPacketConfig.class, i++, Side.CLIENT);
        INSTANCE.registerMessage(MusePacketInstallModuleRequest.Handler.class, MusePacketInstallModuleRequest.class, i++, Side.SERVER);
        INSTANCE.registerMessage(MusePacketSalvageModuleRequest.Handler.class, MusePacketSalvageModuleRequest.class, i++, Side.SERVER);
        INSTANCE.registerMessage(MusePacketColourInfo.Handler.class, MusePacketColourInfo.class, i++, Side.SERVER);
        INSTANCE.registerMessage(MusePacketCosmeticInfo.Handler.class, MusePacketCosmeticInfo.class, i++, Side.SERVER);
        INSTANCE.registerMessage(MusePacketToggleRequest.Handler.class, MusePacketToggleRequest.class, i++, Side.SERVER);
        INSTANCE.registerMessage(MusePacketTweakRequestDouble.Handler.class, MusePacketTweakRequestDouble.class, i++, Side.SERVER);
        INSTANCE.registerMessage(MusePacketCosmeticPresetUpdate.Handler.class, MusePacketCosmeticPresetUpdate.class, i++, Side.SERVER);
        INSTANCE.registerMessage(MusePacketCosmeticPreset.Handler.class, MusePacketCosmeticPreset.class, i++, Side.SERVER);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        INSTANCE.sendTo(message, player);
    }

    public static void sendToAll(IMessage message) {
        INSTANCE.sendToAll(message);
    }

    public static void sendToAllAround(IMessage message, Entity entity, double d) {
        INSTANCE.sendToAllAround(message, new NetworkRegistry.TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, d));
    }

    public static void sendToDimension(IMessage message, int dim) {
        INSTANCE.sendToDimension(message, dim);
    }

    @SideOnly(Side.CLIENT)
    public static void sendToServer(IMessage message) {
        INSTANCE.sendToServer(message);
    }
}
