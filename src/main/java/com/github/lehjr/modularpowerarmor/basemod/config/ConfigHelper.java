package com.github.lehjr.modularpowerarmor.basemod.config;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHelper {
    static File configFolder = null;

    @Nullable
    public static File getConfigFolder() {
        return configFolder;
    }

    public static File setupConfigFile(String fileName) {
        Path configFile = FMLPaths.CONFIGDIR.get().resolve("lehjr").resolve(MPAConstants.MOD_ID).resolve(fileName);
        try {
            Files.createDirectories(configFile.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        File cfgFile = configFile.toFile();

        if (configFolder == null) {
            configFolder = cfgFile.getParentFile();
        }

        return cfgFile;
    }
}