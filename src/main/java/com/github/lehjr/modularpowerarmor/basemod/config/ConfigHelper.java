package com.github.lehjr.modularpowerarmor.basemod.config;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigHelper {
    static File configFolder = null;

    @Nullable
    public static File getConfigFolder() {
        return configFolder;
    }

    public static File setupConfigFile(String fileName) {
        Path configFile = FMLPaths.CONFIGDIR.get().resolve("lehjr").resolve(MPAConstants.MODID).resolve(fileName);
        File cfgFile = configFile.toFile();
        try {
            if (!cfgFile.getParentFile().exists())
                cfgFile.getParentFile().mkdirs();
            if (!cfgFile.exists())
                cfgFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (configFolder == null)
            configFolder = cfgFile.getParentFile();

        return cfgFile;
    }
}