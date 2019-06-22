package net.machinemuse.powersuits.network.packets;

import net.machinemuse.numina.basemod.MuseLogger;
import net.machinemuse.numina.basemod.NuminaConstants;
import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.numina.capabilities.inventory.modechanging.ModeChangingCapability;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.inventory.modularitem.ModularItemCapability;
import net.machinemuse.numina.nbt.MuseNBTUtils;
import net.machinemuse.numina.network.MuseByteBufferUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 12:28 PM, 5/6/13
 * <p>
 * Ported to Java by lehjr on 11/14/16.
 */
public class MusePacketCosmeticInfo {
    protected static int playerID;
    protected static int itemSlot;
    protected String tagName;
    protected CompoundNBT tagData;

    public MusePacketCosmeticInfo() {
    }

    public MusePacketCosmeticInfo(int playerID, int itemSlot, String tagName, CompoundNBT tagData) {
        this.playerID = playerID;
        this.itemSlot = itemSlot;
        this.tagName = tagName;
        this.tagData = tagData;
    }

    public static void encode(MusePacketCosmeticInfo msg, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.playerID);
        packetBuffer.writeInt(msg.itemSlot);
        packetBuffer.writeString(msg.tagName);
        MuseByteBufferUtils.writeCompressedNBT(packetBuffer, msg.tagData);
    }

    public static MusePacketCosmeticInfo decode(PacketBuffer packetBuffer) {
        return new MusePacketCosmeticInfo(
                packetBuffer.readInt(),
                packetBuffer.readInt(),
                packetBuffer.readString(500),
                MuseByteBufferUtils.readCompressedNBT(packetBuffer));
    }

    public static void handle(MusePacketCosmeticInfo message, Supplier<NetworkEvent.Context> ctx) {
        final ServerPlayerEntity player = ctx.get().getSender();

        if (player == null || player.getServer() == null)
            return;

        ctx.get().enqueueWork(() -> {
            final PlayerEntity actualPlayer;
            int playerID = message.playerID;
            int itemSlot = message.itemSlot;
            String tagName = message.tagName;
            CompoundNBT tagData = message.tagData;
            Entity entity = player.world.getEntityByID(playerID);
            if (!(player.world.getEntityByID(playerID) instanceof PlayerEntity ))
                return;
            else
                actualPlayer = (PlayerEntity) player.world.getEntityByID(playerID);

            if (actualPlayer == null)
                return;

            ItemStack itemStack = actualPlayer.inventory.getStackInSlot(itemSlot);

            LazyOptional<IModularItem> modularItemCap = itemStack.getCapability(ModularItemCapability.MODULAR_ITEM);
            LazyOptional<IModeChangingItem> modeChangingItemCap = itemStack.getCapability(ModeChangingCapability.MODE_CHANGING);

            if (tagName != null && (modularItemCap.isPresent()|| modeChangingItemCap.isPresent())) {
                CompoundNBT itemTag = MuseNBTUtils.getMuseItemTag(itemStack);
                itemTag.remove(NuminaConstants.TAG_COSMETIC_PRESET);

                if (Objects.equals(tagName, NuminaConstants.TAG_RENDER)) {
                    itemTag.remove(NuminaConstants.TAG_RENDER);
                    if (!tagData.isEmpty())
                        itemTag.put(NuminaConstants.TAG_RENDER, tagData);
                } else {
                    CompoundNBT renderTag;
                    if (!itemTag.contains(NuminaConstants.TAG_RENDER)) {
                        renderTag = new CompoundNBT();
                        itemTag.put(NuminaConstants.TAG_RENDER, renderTag);
                    } else {
                        renderTag = itemTag.getCompound(NuminaConstants.TAG_RENDER);
                    }
                    if (tagData.isEmpty()) {
                        MuseLogger.logger.debug("Removing tag " + tagName);
                        renderTag.remove(tagName);
                    } else {
                        MuseLogger.logger.debug("Adding tag " + tagName + " : " + tagData);
                        renderTag.put(tagName, tagData);
                    }
                }
            }
        });
    }
}