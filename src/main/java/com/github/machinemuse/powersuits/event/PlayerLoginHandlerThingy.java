package com.github.machinemuse.powersuits.event;

import com.github.machinemuse.powersuits.client.control.KeybindManager;
import com.github.machinemuse.powersuits.config.MPSSettings;
import com.github.machinemuse.powersuits.network.MPSPackets;
import com.github.machinemuse.powersuits.network.packets.ConfigPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 8:01 PM, 12/05/13
 * <p>
 * Ported to Java by lehjr on 10/24/16.
 */
public final class PlayerLoginHandlerThingy {
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        boolean isUsingBuiltInServer = FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer();

        // dedidated server or multiplayer game
        if (!isUsingBuiltInServer || (isUsingBuiltInServer && FMLCommonHandler.instance().getMinecraftServerInstance().getCurrentPlayerCount() > 1)) {
            // sync config settings between client and server
            MPSPackets.sendTo(new ConfigPacket(), (EntityPlayerMP) player);
        } else {
            MPSSettings.loadCustomInstallCosts();
        }
        if (player.world.isRemote)
            KeybindManager.readInKeybinds();
    }
}