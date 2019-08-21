package net.machinemuse.powersuits.network.packets;

import net.machinemuse.numina.capabilities.inventory.modechanging.IModeChangingItem;
import net.machinemuse.powersuits.basemod.MPSObjects;
import net.machinemuse.powersuits.containers.providers.RadialModeContainerProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.function.Supplier;

public class ModeChangingRequest {
    public ModeChangingRequest() {

    }

    public static void encode(ModeChangingRequest msg, PacketBuffer packetBuffer) {

    }

    public static ModeChangingRequest decode(PacketBuffer packetBuffer) {
        return new ModeChangingRequest();
    }

    public static void handle(ModeChangingRequest message, Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity player = ctx.get().getSender();

            player.getHeldItemMainhand().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(ih -> {
                if (ih instanceof IModeChangingItem) {
                    if (ih.getStackInSlot(0).isEmpty())
                        ((IModeChangingItem) ih).setStackInSlot(0, new ItemStack(MPSObjects.INSTANCE.moduleBatteryUltimate));
                    else
                        ih.getStackInSlot(0)
                                .getCapability(CapabilityEnergy.ENERGY)
                                .ifPresent(energy -> energy.receiveEnergy(energy.getMaxEnergyStored(), false));

                    if (ih.getStackInSlot(1).isEmpty())
                        ((IModeChangingItem) ih).setStackInSlot(1, new ItemStack(MPSObjects.INSTANCE.leafBlower));

                    if (ih.getStackInSlot(2).isEmpty())
                        ((IModeChangingItem) ih).setStackInSlot(2, new ItemStack(MPSObjects.INSTANCE.hoe));

                    if (ih.getStackInSlot(3).isEmpty())
                        ((IModeChangingItem) ih).setStackInSlot(3, new ItemStack(MPSObjects.INSTANCE.shears));


                }
            });
            System.out.println(player.getHeldItemMainhand().serializeNBT());



/*

chest
            {
                x:
                245, y:63, z:-237, Items:[{
                Slot:
                0b, id:"powersuits:battery.ultimate", Count:1 b, tag:{
                    MMModModule:
                    {
                    }
                }
            }],id:
            "minecraft:chest"
            }



// FIST note: recursive tag here:
{
  id: "powersuits:powerfist",
  Count: 1b,
  tag: {
    MMModItem: {

      Modules: {
        mode: 0,
        Modules: [
          {
            Slot: 0,
            id: "powersuits:battery.ultimate",
            Count: 1b,
            tag: {
              MMModModule: {}
            }
          },
          {
            Slot: 1,
            id: "powersuits:leaf_blower",
            Count: 1b
          },
          {
            Slot: 2,
            id: "powersuits:hoe",
            Count: 1b
          },
          {
            Slot: 3,
            id: "powersuits:shears",
            Count: 1b
          }
        ],
        Size: 40
      }
    }
  }
}


 */


            NetworkHooks.openGui((ServerPlayerEntity) player, new RadialModeContainerProvider());
        });
        ctx.get().setPacketHandled(true);
    }
}
