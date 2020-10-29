package com.github.lehjr.modularpowerarmor.basemod;

import com.github.lehjr.mpalib.basemod.MPALibConstants;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public enum MPAModules {
    INSTANCE;

    protected List<ResourceLocation> moduleRegNames = new ArrayList<ResourceLocation>() {{
        add(new ResourceLocation(MPALibConstants.MOD_ID, MPALibConstants.MODULE_BATTERY_BASIC__REGNAME));
        add(new ResourceLocation(MPALibConstants.MOD_ID, MPALibConstants.MODULE_BATTERY_ADVANCED__REGNAME));
        add(new ResourceLocation(MPALibConstants.MOD_ID, MPALibConstants.MODULE_BATTERY_ELITE__REGNAME));
        add(new ResourceLocation(MPALibConstants.MOD_ID, MPALibConstants.MODULE_BATTERY_ULTIMATE__REGNAME));
    }};

    public void addModule(ResourceLocation regName) {
        moduleRegNames.add(regName);
    }

    public List<ResourceLocation> getModuleRegNames () {
        return moduleRegNames;
    }
}