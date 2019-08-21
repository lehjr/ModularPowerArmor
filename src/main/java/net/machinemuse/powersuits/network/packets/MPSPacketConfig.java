//package net.machinemuse.powersuits.network.packets;
//
//import net.minecraft.network.PacketBuffer;
//import net.minecraftforge.fml.network.NetworkEvent;
//
//import java.util.function.Supplier;
//
///**
// * Sync settings between server and client
// */
//public class MPSPacketConfig {
//    public MPSPacketConfig() {
//
//    }
//
//    public static void encode(MPSPacketConfig msg, PacketBuffer packetBuffer) {
//
//    }
//
//    public static MPSPacketConfig decode(PacketBuffer packetBuffer) {
//        return null;
//    }
//
//    public static void handle(MPSPacketConfig message, Supplier<NetworkEvent.Context> ctx) {
//
//    }
//
//
////    // read settings from packet
////    @Override
////    public void fromBytes(ByteBuf buf) {
////        MPSServerSettings settings = new MPSServerSettings(buf);
////        CommonConfig.moduleConfig.setServerSettings(settings);
////    }
////
////    // write values to packet to send to the client
////    @Override
////    public void toBytes(ByteBuf buf) {
////        if (CommonConfig.moduleConfig.getServerSettings() == null)
////            CommonConfig.moduleConfig.setServerSettings(new MPSServerSettings());
////        CommonConfig.moduleConfig.getServerSettings().writeToBuffer(buf);
////    }
////
////    public static void handle(MPSPacketConfig message, Supplier<NetworkEvent.Context> ctx) {
////
////    }
//}