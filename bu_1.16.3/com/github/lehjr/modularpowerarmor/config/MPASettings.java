package com.github.lehjr.modularpowerarmor.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.config.MPALibSettings;
import jdk.internal.jline.internal.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MPASettings {
    public static final ClientConfig CLIENT_CONFIG;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static File clientFile;

    public static final CommonConfig COMMON_CONFIG;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        // Client
        {
            final Pair<ClientConfig, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
            CLIENT_SPEC = clientSpecPair.getRight();
            CLIENT_CONFIG = clientSpecPair.getLeft();
            clientFile = MPALibSettings.setupConfigFile("modularpowerarmor-client-only.toml", MPAConstants.MOD_ID);

            final CommentedFileConfig configData = CommentedFileConfig.builder(clientFile)
                    .sync()
                    .autosave()
                    .writingMode(WritingMode.REPLACE)
                    .build();

            configData.load();
            CLIENT_SPEC.setConfig(configData);
        }
        // Common
        {
            final Pair<CommonConfig, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
            COMMON_SPEC = commonSpecPair.getRight();
            COMMON_CONFIG = commonSpecPair.getLeft();
        }
    }





    @Nullable
    static CommonConfig getCommonConfig() {
        return COMMON_CONFIG;
    }









    // Modules ------------------------------------------------------------------------------------
    static ModuleConfig moduleConfig = null;
    public static void setModConfig(ModuleConfig modConfigIn) {
        moduleConfig = modConfigIn;
    }

    public static Callable<IConfig> getModuleConfig() {
        if (moduleConfig == null) {
            moduleConfig = new ModuleConfig(null);
        }
        return new Callable<IConfig>() {
            @Override
            public IConfig call() throws Exception {
                return (IConfig) moduleConfig;
            }
        };
    }


}