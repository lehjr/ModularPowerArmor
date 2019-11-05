package com.github.lehjr.modularpowerarmor.network.packets;

import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.item.ItemUtils;
import com.github.lehjr.mpalib.legacy.module.IPowerModule;
import com.github.lehjr.mpalib.nbt.NBTUtils;
import com.github.lehjr.mpalib.network.MuseByteBufferUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Packet for requesting to purchase an upgrade. Player-to-server. Server decides whether it is a valid upgrade or not and replies with an associated
 * inventoryrefresh packet.
 *
 * Author: MachineMuse (Claire Semple)
 * Created: 10:16 AM, 01/05/13
 *
 * Ported to Java by lehjr on 11/14/16.
 */
public class InstallModuleRequestPacket implements IMessage {
    int itemSlot;
    String moduleName;

    public InstallModuleRequestPacket() {
    }

    public InstallModuleRequestPacket(int itemSlot, String moduleName) {
        this.itemSlot = itemSlot;
        this.moduleName = moduleName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.itemSlot = buf.readInt();
        this.moduleName = MuseByteBufferUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(itemSlot);
        MuseByteBufferUtils.writeUTF8String(buf, moduleName);
    }

    public static class Handler implements IMessageHandler<InstallModuleRequestPacket, IMessage> {
        @Override
        public IMessage onMessage(InstallModuleRequestPacket message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    int itemSlot = message.itemSlot;
                    String moduleName = message.moduleName;
                    ItemStack stack = player.inventory.getStackInSlot(itemSlot);
                    if (moduleName != null) {
                        InventoryPlayer inventory = player.inventory;
                        IPowerModule moduleType = ModuleManager.INSTANCE.getModule(moduleName);
                        if (moduleType == null || !moduleType.isAllowed()) {
                            player.sendMessage(new TextComponentString("Server has disallowed this module. Sorry!"));
                        } else {
                            NonNullList<ItemStack> cost = ModuleManager.INSTANCE.getInstallCost(moduleName);
                            if ((!ModuleManager.INSTANCE.itemHasModule(stack, moduleName) && ItemUtils.hasInInventory(cost, player.inventory)) || player.capabilities.isCreativeMode) {
                                NBTUtils.removeMuseValuesTag(stack);
                                ModuleManager.INSTANCE.itemAddModule(stack, moduleType);
                                for (ItemStack stackInCost : cost) {
                                    ElectricItemUtils.givePlayerEnergy(player, MPSConfig.INSTANCE.rfValueOfComponent(stackInCost));
                                }

                                if (!player.capabilities.isCreativeMode) {
                                    ItemUtils.deleteFromInventory(cost, inventory);
                                }
                                // use builtin handler
                                player.inventoryContainer.detectAndSendChanges();
                            }
                        }
                    }
                });
            }
            return null;
        }
    }
}