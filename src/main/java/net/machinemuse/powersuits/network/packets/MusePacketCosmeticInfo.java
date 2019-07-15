package net.machinemuse.powersuits.network.packets;

import net.machinemuse.numina.capabilities.render.MuseRenderCapability;
import net.machinemuse.numina.network.MuseByteBufferUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
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
public class MusePacketCosmeticInfo {
    protected static int itemSlot;
    protected String tagName;
    protected CompoundNBT tagData;

    public MusePacketCosmeticInfo() {
    }

    public MusePacketCosmeticInfo(int itemSlot, String tagName, CompoundNBT tagData) {
        this.itemSlot = itemSlot;
        this.tagName = tagName;
        this.tagData = tagData;
    }

    public static void encode(MusePacketCosmeticInfo msg, PacketBuffer packetBuffer) {
        packetBuffer.writeInt(msg.itemSlot);
        packetBuffer.writeString(msg.tagName);
        MuseByteBufferUtils.writeCompressedNBT(packetBuffer, msg.tagData);
    }

    public static MusePacketCosmeticInfo decode(PacketBuffer packetBuffer) {
        return new MusePacketCosmeticInfo(
                packetBuffer.readInt(),
                packetBuffer.readString(500),
                MuseByteBufferUtils.readCompressedNBT(packetBuffer));
    }

    public static void handle(MusePacketCosmeticInfo message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity player = ctx.get().getSender();
            int itemSlot = message.itemSlot;
            String tagName = message.tagName;
            CompoundNBT tagData = message.tagData;

            player.inventory.getStackInSlot(itemSlot).getCapability(MuseRenderCapability.RENDER).ifPresent(render->{

                // fixme!! preset tags still a thing? I don't think anyone knows how to create or use them
//                if (tagName != null && (modularItemCap.isPresent()|| modeChangingItemCap.isPresent())) {
//                    CompoundNBT itemTag = MuseNBTUtils.getMuseItemTag(itemStack);
//                    itemTag.remove(NuminaConstants.TAG_COSMETIC_PRESET);
//
//                    if (Objects.equals(tagName, NuminaConstants.TAG_RENDER)) {
//                        itemTag.remove(NuminaConstants.TAG_RENDER);
//                        if (!tagData.isEmpty())
//                            itemTag.put(NuminaConstants.TAG_RENDER, tagData);
//                    } else {
//                        CompoundNBT renderTag;
//                        if (!itemTag.contains(NuminaConstants.TAG_RENDER)) {
//                            renderTag = new CompoundNBT();
//                            itemTag.put(NuminaConstants.TAG_RENDER, renderTag);
//                        } else {
//                            renderTag = itemTag.getCompound(NuminaConstants.TAG_RENDER);
//                        }
//                        if (tagData.isEmpty()) {
//                            MuseLogger.logger.debug("Removing tag " + tagName);
//                            renderTag.remove(tagName);
//                        } else {
//                            MuseLogger.logger.debug("Adding tag " + tagName + " : " + tagData);
//                            renderTag.put(tagName, tagData);
//                        }
//                    }
//                }


            });
        });
        ctx.get().setPacketHandled(true);
    }
}