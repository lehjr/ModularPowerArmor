package net.machinemuse.powersuits.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 8:01 PM, 12/05/13
 * <p>
 * Ported to Java by lehjr on 10/24/16.
 */
public final class PlayerLoginHandlerThingy {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();



//        boolean isUsingBuiltInServer = FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer();
//
//        // dedidated server or multiplayer game
//        if (!isUsingBuiltInServer || (isUsingBuiltInServer && FMLCommonHandler.instance().getMinecraftServerInstance().getCurrentPlayerCount() > 1)) {
//            // sync config settings between client and server
//            MPSPackets.sendTo(new MPSPacketConfig(), (EntityPlayerMP) player);
//        } else {
//            MPSSettings.loadCustomInstallCosts();
//        }
//        if (player.world.isRemote)
//            KeybindManager.readInKeybinds();
    }
}