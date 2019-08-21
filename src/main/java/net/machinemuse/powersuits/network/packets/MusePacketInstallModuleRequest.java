package net.machinemuse.powersuits.network.packets;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet for requesting to purchase an upgrade. Player-to-server. Server decides whether it is a valid upgrade or not and replies with an associated
 * inventoryrefresh packet.
 *
 * Author: MachineMuse (Claire Semple)
 * Created: 10:16 AM, 01/05/13
 *
 * Ported to Java by lehjr on 11/14/16.
 */
public class MusePacketInstallModuleRequest {
    int slotSource;
    String moduleName;
    int slotTarget;

    public MusePacketInstallModuleRequest() {
    }

    public MusePacketInstallModuleRequest(int itemSlotSource, String moduleName, int slotTarget) {
        this.slotSource = itemSlotSource;
        this.moduleName = moduleName;
        this.slotTarget = slotTarget;
    }

    public static void encode(MusePacketInstallModuleRequest msg, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.slotSource);
        packetBuffer.writeString(msg.moduleName);
        packetBuffer.writeInt(msg.slotTarget);
    }

    public static MusePacketInstallModuleRequest decode(PacketBuffer packetBuffer) {
        return new MusePacketInstallModuleRequest(
                packetBuffer.readInt(),
                packetBuffer.readString(500),
                packetBuffer.readInt());
    }

    public static void handle(MusePacketInstallModuleRequest message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                int slotSource = message.slotSource;
                String moduleName = message.moduleName;
                int slotTarget = message.slotTarget;

//                if (player.container.

//                int itemSlot = message.itemSlot;
//                String moduleName = message.moduleName;
//                ItemStack stack = player.inventory.getStackInSlot(itemSlot);
//                if (moduleName != null) {
//                    InventoryPlayer inventory = player.inventory;
//                    IPowerModule moduleType = ModuleManager.INSTANCE.getModule(moduleName);
//                    if (moduleType == null || !moduleType.isAllowed()) {
//                        player.sendMessage(new TextComponentString("Server has disallowed this module. Sorry!"));
//                    } else {
//                        NonNullList<ItemStack> cost = ModuleManager.INSTANCE.getInstallCost(moduleName);
//                        if ((!ModuleManager.INSTANCE.itemHasModule(stack, moduleName) && MuseItemUtils.hasInInventory(cost, player.inventory)) || player.capabilities.isCreativeMode) {
//                            MuseNBTUtils.removeMuseValuesTag(stack);
//                            ModuleManager.INSTANCE.itemAddModule(stack, moduleType);
//                            for (ItemStack stackInCost : cost) {
//                                ElectricItemUtils.givePlayerEnergy(player, CommonConfig.moduleConfig.rfValueOfComponent(stackInCost));
//                            }
//
//                            if (!player.capabilities.isCreativeMode) {
//                                MuseItemUtils.deleteFromInventory(cost, inventory);
//                            }
//                            // use builtin handler
//                            player.inventoryContainer.detectAndSendChanges();
//                        }
//                    }
//                }
            });
            ctx.get().setPacketHandled(true);
    }
}