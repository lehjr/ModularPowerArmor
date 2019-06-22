package net.machinemuse.powersuits.network.packets;

import net.machinemuse.numina.basemod.NuminaConstants;
import net.machinemuse.numina.capabilities.inventory.modechanging.ModeChangingCapability;
import net.machinemuse.numina.capabilities.inventory.modularitem.ModularItemCapability;
import net.machinemuse.numina.nbt.MuseNBTUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MusePacketCosmeticPreset {
    protected int playerID;
    protected int itemSlot;
    protected String presetName;

    public MusePacketCosmeticPreset() {
    }

    public MusePacketCosmeticPreset(int playerID, int itemSlot, String presetName) {
        this.playerID = playerID;
        this.itemSlot = itemSlot;
        this.presetName = presetName;
    }

    public static void encode(MusePacketCosmeticPreset msg, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.playerID);
        packetBuffer.writeInt(msg.itemSlot);
        packetBuffer.writeString(msg.presetName);
    }

    public static MusePacketCosmeticPreset decode(PacketBuffer packetBuffer) {
        return new MusePacketCosmeticPreset(packetBuffer.readInt(), packetBuffer.readInt(), packetBuffer.readString(500));
    }

    public static void handle(MusePacketCosmeticPreset message, Supplier<NetworkEvent.Context> ctx) {
        final ServerPlayerEntity player = ctx.get().getSender();

        if (player == null || player.getServer() == null)
            return;

        final PlayerEntity actualPlayer;
        int playerID = message.playerID;
        int itemSlot = message.itemSlot;
        if (!(player.world.getEntityByID(playerID) instanceof PlayerEntity ))
            return;
        else
            actualPlayer = (PlayerEntity) player.world.getEntityByID(playerID);

        if (actualPlayer == null)
            return;

        ctx.get().enqueueWork(() -> {

            String presetName = message.presetName;
            ItemStack stack = actualPlayer.inventory.getStackInSlot(itemSlot);
            if (stack.getCapability(ModularItemCapability.MODULAR_ITEM).isPresent() ||
                    stack.getCapability(ModeChangingCapability.MODE_CHANGING).isPresent()) {
                CompoundNBT itemTag = MuseNBTUtils.getMuseItemTag(stack);
                itemTag.remove(NuminaConstants.TAG_RENDER);
                itemTag.putString(NuminaConstants.TAG_COSMETIC_PRESET, presetName);
            }
        });
    }
}