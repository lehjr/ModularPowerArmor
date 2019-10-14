package com.github.lehjr.modularpowerarmor.basemod;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public enum MPAModules {
    INSTANCE;

    protected List<ResourceLocation> moduleRegNames = new ArrayList<>();

    public void addModule(ResourceLocation regName) {
        moduleRegNames.add(regName);
    }

    public List<ResourceLocation> getModuleRegNames () {
        return moduleRegNames;
    }
}