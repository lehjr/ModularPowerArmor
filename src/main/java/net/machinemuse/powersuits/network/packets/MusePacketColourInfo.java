package net.machinemuse.powersuits.network.packets;

import net.machinemuse.numina.basemod.NuminaConstants;
import net.machinemuse.numina.capabilities.inventory.modechanging.ModeChangingCapability;
import net.machinemuse.numina.capabilities.inventory.modularitem.ModularItemCapability;
import net.machinemuse.numina.nbt.MuseNBTUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 12:28 PM, 5/6/13
 * <p>
 * Ported to Java by lehjr on 11/14/16.
 */
public class MusePacketColourInfo {
    protected int playerID;
    protected int itemSlot;
    protected int[] tagData;

    public MusePacketColourInfo() {

    }

    public MusePacketColourInfo(int playerID, int itemSlot, int[] tagData) {
        this.playerID = playerID;
        this.itemSlot = itemSlot;
        this.tagData = tagData;
    }

    public static void encode(MusePacketColourInfo msg, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.playerID);
        packetBuffer.writeInt(msg.itemSlot);
        packetBuffer.writeVarIntArray(msg.tagData);
    }

    public static MusePacketColourInfo decode(PacketBuffer packetBuffer) {
        return new MusePacketColourInfo(packetBuffer.readInt(), packetBuffer.readInt(), packetBuffer.readVarIntArray());
    }

    public static void handle(MusePacketColourInfo message, Supplier<NetworkEvent.Context> ctx) {
        final ServerPlayerEntity player = ctx.get().getSender();

        if (player == null || player.getServer() == null)
            return;

        final PlayerEntity actualPlayer;
        int playerID = message.playerID;
        int itemSlot = message.itemSlot;
        int[] tagData = message.tagData;
        Entity entity = player.world.getEntityByID(playerID);
        if (!(player.world.getEntityByID(playerID) instanceof PlayerEntity ))
            return;
        else
            actualPlayer = (PlayerEntity) player.world.getEntityByID(playerID);

        if (actualPlayer == null)
            return;

        ctx.get().enqueueWork(() -> {
            ItemStack stack = actualPlayer.inventory.getStackInSlot(itemSlot);
            if (stack.getCapability(ModularItemCapability.MODULAR_ITEM).isPresent() ||
                    stack.getCapability(ModeChangingCapability.MODE_CHANGING).isPresent()) {
                CompoundNBT itemTag = MuseNBTUtils.getMuseItemTag(stack);
                CompoundNBT renderTag = itemTag.getCompound(NuminaConstants.TAG_RENDER);
                if (renderTag == null) {
                    renderTag = new CompoundNBT();
                    itemTag.put(NuminaConstants.TAG_RENDER, renderTag);
                }
                if (renderTag != null)
                    renderTag.putIntArray(NuminaConstants.TAG_COLOURS, tagData);
            }
        });
    }
}