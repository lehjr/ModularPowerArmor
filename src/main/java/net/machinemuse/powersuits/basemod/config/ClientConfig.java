package net.machinemuse.powersuits.basemod.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

/**
 * Client only settings
 */
public class ClientConfig {
    public static final ClientSettings CLIENT_CONFIG;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static File clientFile;

    static {
        final Pair<ClientSettings, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(ClientSettings::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT_CONFIG = clientSpecPair.getLeft();
        clientFile = ConfigHelper.setupConfigFile("powersuits-client-only.toml");
        CLIENT_SPEC.setConfig(CommentedFileConfig.of(clientFile));
    }

    /** HUD ---------------------------------------------------------------------------------------*/
    public static ForgeConfigSpec.BooleanValue
            HUD_USE_GRAPHICAL_METERS,
            HUD_TOGGLE_MODULE_SPAM,
            HUD_DISPLAY_HUD,
            HUD_USE_24_HOUR_CLOCK;

    /** General ----------------------------------------------------------------------------------- */
    public static ForgeConfigSpec.BooleanValue
            GENERAL_ALLOW_CONFLICTING_KEYBINDS;

    public static ForgeConfigSpec.DoubleValue
            HUD_KEYBIND_HUD_X,
            HUD_KEYBIND_HUD_Y;

    public static class ClientSettings {
        ClientSettings(ForgeConfigSpec.Builder builder) {
            // HUD ------------------------------------------------------------------------------------
            builder.comment("HUD settings").push("HUD");
            HUD_USE_GRAPHICAL_METERS = builder
                    .comment("Use Graphical Meters")
                    .translation(MPSConstants.CONFIG_HUD_USE_GRAPHICAL_METERS)
                    .define("useGraphicalMeters", true);

            HUD_TOGGLE_MODULE_SPAM = builder
                    .comment("Chat message when toggling module")
                    .translation(MPSConstants.CONFIG_HUD_TOGGLE_MODULE_SPAM)
                    .define("toggleModuleSpam", false);

            HUD_DISPLAY_HUD = builder
                    .comment("Display HUD")
                    .translation(MPSConstants.CONFIG_HUD_DISPLAY_HUD)
                    .define("keybind_HUD_on", true);

            HUD_KEYBIND_HUD_X = builder
                    .comment("x position")
                    .translation(MPSConstants.CONFIG_HUD_KEYBIND_HUD_X)
                    .defineInRange("keybindHUDx", 8.0, 0, Double.MAX_VALUE);

            HUD_KEYBIND_HUD_Y = builder
                    .comment("x position")
                    .translation(MPSConstants.CONFIG_HUD_KEYBIND_HUD_Y)
                    .defineInRange("keybindHUDy", 32.0, 0, Double.MAX_VALUE);

            HUD_USE_24_HOUR_CLOCK = builder
                    .comment("Use a 24h clock instead of 12h")
                    .translation(MPSConstants.CONFIG_HUD_USE_24_HOUR_CLOCK)
                    .define("use24hClock", false);
            builder.pop();

            builder.comment("General settings").push("General");
            GENERAL_ALLOW_CONFLICTING_KEYBINDS = builder
                    .comment("Allow Conflicting Keybinds")
                    .translation(MPSConstants.CONFIG_GENERAL_ALLOW_CONFLICTING_KEYBINDS)
                    .define("allowConflictingKeybinds", true);
        }
    }
}