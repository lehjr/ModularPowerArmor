package net.machinemuse.powersuits.client.control;

import net.machinemuse.numina.capabilities.inventory.modechanging.ModeChangingCapability;
import net.machinemuse.numina.capabilities.player.CapabilityPlayerKeyStates;
import net.machinemuse.numina.capabilities.player.IPlayerKeyStates;
import net.machinemuse.numina.network.NuminaPackets;
import net.machinemuse.numina.network.packets.MusePacketPlayerUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import static java.util.Optional.empty;

@OnlyIn(Dist.CLIENT)
public class KeybindKeyHandler {
    Minecraft minecraft;

    public static final String mps = "Modular ModularPowersuits";
    public static final KeyBinding openKeybindGUI = new KeyBinding("Open MPS Keybind GUI", GLFW.GLFW_KEY_UNKNOWN, mps);
    public static final KeyBinding goDownKey = new KeyBinding("Go Down (MPS Flight Control)", GLFW.GLFW_KEY_Z, mps);
    public static final KeyBinding cycleToolBackward = new KeyBinding("Cycle Tool Backward (MPS)", GLFW.GLFW_KEY_UNKNOWN, mps);
    public static final KeyBinding cycleToolForward = new KeyBinding("Cycle Tool Forward (MPS)", GLFW.GLFW_KEY_UNKNOWN, mps);
    public static final KeyBinding openCosmeticGUI = new KeyBinding("Cosmetic (MPS)", GLFW.GLFW_KEY_UNKNOWN, mps);
    public static final KeyBinding[] keybindArray = new KeyBinding[]{openKeybindGUI, goDownKey, cycleToolBackward, cycleToolForward, openCosmeticGUI};

    public KeybindKeyHandler() {
        minecraft = Minecraft.getInstance();
        for (KeyBinding key : keybindArray) {
            ClientRegistry.registerKeyBinding(key);
        }
    }

    void updatePlayerValues(ClientPlayerEntity clientPlayer) {
        boolean markForSync = false;
        boolean downKeyState = goDownKey.isKeyDown();
        boolean jumpKeyState = minecraft.gameSettings.keyBindJump.isKeyDown();

        LazyOptional<IPlayerKeyStates> playerCap = clientPlayer.getCapability(CapabilityPlayerKeyStates.PLAYER_KEYSTATES, null);
        if (playerCap.isPresent()) {
            if (playerCap.map(m -> m.getDownKeyState() != downKeyState).orElse(false)) {
                playerCap.map(m -> {
                    m.setDownKeyState(downKeyState);
                    return empty();
                });
                markForSync = true;
            }

            if (playerCap.map(m -> m.getJumpKeyState() != jumpKeyState).orElse(false)) {
                playerCap.map(m -> {
                    m.setJumpKeyState(jumpKeyState);
                    return empty();
                });
                markForSync = true;
            }

            if (markForSync) {
                NuminaPackets.sendToServer(new MusePacketPlayerUpdate(clientPlayer.getEntityId(), downKeyState, jumpKeyState));
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
//
//    public void checkPlayerKeys() {
        ClientPlayerEntity player = minecraft.player;
        KeyBinding[] hotbarKeys = minecraft.gameSettings.keyBindsHotbar;
        updatePlayerValues(player);

//        if (openKeybindGUI.isKeyDown() && minecraft.isGameFocused())
//            minecraft.displayGuiScreen(new KeyConfigGui(player));

//        if (openCosmeticGUI.isKeyDown() && minecraft.isGameFocused())
//            minecraft.displayGuiScreen(new CosmeticGui(player));
//
//        if (hotbarKeys[player.inventory.currentItem].isKeyDown() && minecraft.isGameFocused())
//            minecraft.displayGuiScreen(new GuiModeSelector(player));

        /* cycleToolBackward/cycleToolForward */
        if (cycleToolBackward.isKeyDown()) {
            minecraft.playerController.tick();
            player.inventory.getStackInSlot(player.inventory.currentItem).getCapability(ModeChangingCapability.MODE_CHANGING)
                    .ifPresent(iModeChangingItem -> iModeChangingItem.cycleMode(player, 1));
        }

        if (cycleToolForward.isKeyDown()) {
            minecraft.playerController.tick();
            player.inventory.getStackInSlot(player.inventory.currentItem).getCapability(ModeChangingCapability.MODE_CHANGING)
                    .ifPresent(iModeChangingItem -> iModeChangingItem.cycleMode(player, -1));
        }
    }
}