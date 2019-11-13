package com.github.lehjr.modularpowerarmor.network;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.network.packets.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MPAPackets {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MODID);

    public static void registerMPSPackets() {
        int i = 0;

        INSTANCE.registerMessage(ConfigPacket.Handler.class, ConfigPacket.class, i++, Side.CLIENT);
        INSTANCE.registerMessage(CreativeModuleInstallRequestPacket.Handler.class, CreativeModuleInstallRequestPacket.class, i++, Side.SERVER);
        INSTANCE.registerMessage(ModuleMoveFromSlotToSlotPacket.Handler.class, ModuleMoveFromSlotToSlotPacket.class, i++, Side.SERVER);
        INSTANCE.registerMessage(ColourInfoPacket.Handler.class, ColourInfoPacket.class, i++, Side.SERVER);
        INSTANCE.registerMessage(CosmeticInfoPacket.Handler.class, CosmeticInfoPacket.class, i++, Side.SERVER);
        INSTANCE.registerMessage(CosmeticPresetUpdatePacket.Handler.class, CosmeticPresetUpdatePacket.class, i++, Side.SERVER);
        INSTANCE.registerMessage(CosmeticPresetPacket.Handler.class, CosmeticPresetPacket.class, i++, Side.SERVER);
        INSTANCE.registerMessage(CraftingGuiServerSidePacket.Handler.class, CraftingGuiServerSidePacket.class, i++, Side.SERVER);
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
