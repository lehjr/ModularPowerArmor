package com.github.lehjr.modularpowerarmor.config;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.IConfig;
import com.github.lehjr.mpalib.config.MPALibSettings;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModuleConfig implements IConfig {
    ModConfig commonConfig;

    static boolean isDevMode = false;
    public boolean isLoading = true;

    public ModuleConfig(ModConfig commonConfig) {
        outputMap = new HashMap<>();
    }

    public void setLoadingDone() {
        this.isLoading = false;
    }

    /**
     * FIXME: might be better with categories as keys with a value as a map of module name and ArrayList<String> of entries
     *  this way these don't get duplicate catagory entries in the output file
     */
    Map<String, Map<String, ArrayList<String>>> outputMap;
    void addtoMap(String category, String moduleName, String entry) {

        Map<String, ArrayList<String>> modulesForCategory;
        ArrayList<String> moduleSettings;

        // check if the category is already in the map
        if (outputMap.containsKey(category)) {
            modulesForCategory = outputMap.get(category);
            if(modulesForCategory.containsKey(moduleName)) {
                moduleSettings = modulesForCategory.get(moduleName);
                if (moduleSettings.contains(entry)) {
                    return;
                }
            } else {
                moduleSettings = new ArrayList<>();
            }
        } else {
            modulesForCategory = new HashMap<>();
            moduleSettings = new ArrayList<>();
        }

        moduleSettings.add(entry);
        modulesForCategory.put(moduleName, moduleSettings);

        outputMap.put(category, modulesForCategory);
    }

    // once the builder has been built, it cannot be changed.
    public void finishBuilder() {
        if (isLoading)
            return;
        System.out.println("MODULE MAP SET SIZE: " + outputMap.size());
        StringBuilder outString = new StringBuilder("builder.push(\"Modules\");\n");

        for (Map.Entry<String, Map<String, ArrayList<String>>> categoryMapEntry : outputMap.entrySet()) {
            String moduleCategory = categoryMapEntry.getKey();
            outString.append("builder.push(\"").append(moduleCategory).append("\");\n");

            Map<String, ArrayList<String>> moduleMapEntry = categoryMapEntry.getValue();

            for (Map.Entry<String, ArrayList<String>> entry: moduleMapEntry.entrySet()) {
                String moduleName = entry.getKey();
                ArrayList<String> moduleSettings = entry.getValue();

                outString.append("builder.push(\"").append(moduleName).append("\");\n");
                for (String moduleLine : moduleSettings) {
                    outString.append(moduleLine);
                }
                outString.append("builder.pop();\n");
            }
            outString.append("builder.pop();\n");

        }
        outString.append("builder.pop();\n");
        try {
            FileUtils.writeStringToFile(MPALibSettings.setupConfigFile("missingConfigs.txt", MPAConstants.MOD_ID), outString.toString(), Charset.defaultCharset(), false);
        } catch (Exception e) {

        }
    }

    @Override
    public double getBasePropertyDoubleOrDefault (
            EnumModuleCategory category,
            @Nonnull ItemStack module,
            String propertyName, double baseVal) {

        String moduleName = itemTranslationKeyToConfigKey(module.getTranslationKey());
        String entry = "base_" + propertyName;
        if (isDevMode) {
            addtoMap(category.getName(),
                    moduleName,
                    new StringBuilder("builder.defineInRange(\"")
                            .append(entry).append("\", ")
                            .append(baseVal).append("D, ")
                            .append(0).append(", ")
                            .append(Double.MAX_VALUE)
                            .append(");\n").toString());
            boolean isAllowed = isModuleAllowed(category, module);
        } else {
            ArrayList<String> key = new ArrayList<String>() {{
                add("Modules");
                add(category.getName());
                add(moduleName);
                add(entry);
            }};

            if (commonConfig != null && commonConfig.getConfigData().contains(key)) {
                double val = commonConfig.getConfigData().get(key);
//                    System.out.println("common config value: " + commonConfig.getConfigData().get(key));
                return (double)val;
            }
        }
        return baseVal;
    }

    @Override
    public double getTradeoffPropertyDoubleOrDefault(
            EnumModuleCategory category,
            @Nonnull ItemStack module,
            String tradeoffName,
            String propertyName,
            double multiplier) {

        String moduleName = itemTranslationKeyToConfigKey(module.getTranslationKey());
        String entry = propertyName + "_" + tradeoffName + "_multiplier";

        if (isDevMode) {
            addtoMap(category.getName(),
                    moduleName,
                    new StringBuilder("builder.defineInRange(\"")
                            .append(entry).append("\", ")
                            .append(multiplier).append("D, ")
                            .append(0).append(", ")
                            .append(Double.MAX_VALUE)
                            .append(");\n").toString());
            boolean isAllowed = isModuleAllowed(category, module);
        } else {
            ArrayList<String> key = new ArrayList<String>() {{
                add("Modules");
                add(category.getName().replace(" ", "_"));
                add(moduleName);
                add(entry);
            }};
            if (commonConfig != null && commonConfig.getConfigData().contains(key)) {
                double val = commonConfig.getConfigData().get(key);
//                    System.out.println("common config value: " + commonConfig.getConfigData().get(key));
                return val;
            }
        }

        return multiplier;
    }

    @Override
    public int getBasePropertIntegerOrDefault(EnumModuleCategory category, @Nonnull ItemStack module, String propertyName, int baseVal) {
        String moduleName = itemTranslationKeyToConfigKey(module.getTranslationKey());
        String entry = "base_" + propertyName;

        if (isDevMode) {
            addtoMap(category.getName(),
                    moduleName,
                    new StringBuilder("builder.defineInRange(\"")
                            .append(entry).append("\", ")
                            .append(baseVal).append(", ")
                            .append(0).append(", ")
                            .append(Integer.MAX_VALUE)
                            .append(");\n").toString());
            boolean isAllowed = isModuleAllowed(category, module);
        } else {
            ArrayList<String> key = new ArrayList<String>() {{
                add("Modules");
                add(category.getName());
                add(moduleName);
                add(entry);
            }};
            if (commonConfig != null && commonConfig.getConfigData().contains(key)) {
//                    System.out.println("common config value: " + commonConfig.getConfigData().get(key));
                return commonConfig.getConfigData().get(key);
            }
        }
        return baseVal;
    }

    @Override
    public int getTradeoffPropertyIntegerOrDefault(EnumModuleCategory category, @Nonnull ItemStack module, String tradeoffName, String propertyName, int multiplier) {
        String moduleName = itemTranslationKeyToConfigKey(module.getTranslationKey());
        String entry = propertyName + "_" + tradeoffName + "_multiplier";


        if (isDevMode) {
            addtoMap(category.getName(),
                    moduleName,
                    new StringBuilder("builder.defineInRange(\"")
                            .append(entry).append("\", ")
                            .append(multiplier).append(", ")
                            .append(0).append(", ")
                            .append(Integer.MAX_VALUE)
                            .append(");\n").toString());
            boolean isAllowed = isModuleAllowed(category, module);
        } else {
            ArrayList<String> key = new ArrayList<String>() {{
                add("Modules");
                add(category.getName());
                add(moduleName);
                add(entry);
            }};
            if (commonConfig != null && commonConfig.getConfigData().contains(key)) {
//                    System.out.println("common config value: " + commonConfig.getConfigData().get(key));
                return commonConfig.getConfigData().get(key);
            }
        }
        return multiplier;
    }

    @Override
    public boolean isModuleAllowed(EnumModuleCategory category, @Nonnull ItemStack module) {
        String moduleName = itemTranslationKeyToConfigKey(module.getTranslationKey());
        String entry = "isAllowed";

        if (isDevMode) {
            addtoMap(category.getName(), moduleName, new StringBuilder("builder.define(\"").append(entry).append("\", true);\n").toString());
        } else {
            ArrayList<String> key = new ArrayList<String>() {{
                add("Modules");
                add(category.getName());
                add(moduleName);
                add(entry);
            }};

            if (commonConfig != null && commonConfig.getConfigData().contains(key)) {
//                    System.out.println("common config value: " + commonConfig.getConfigData().get(key));
                return commonConfig.getConfigData().get(key);
            }
        }
        return true;
    }

    // drop the prefix for MPS modules and replace "dots" with underscores
    final String itemPrefix = "item." + MPAConstants.MOD_ID + ".";
    String itemTranslationKeyToConfigKey(String translationKey) {
        if (translationKey.startsWith(itemPrefix )){
            translationKey = translationKey.substring(itemPrefix .length());
        }
        return translationKey.replace(".", "_");
    }
}
