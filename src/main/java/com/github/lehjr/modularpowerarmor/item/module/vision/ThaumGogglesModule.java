package com.github.lehjr.modularpowerarmor.item.module.vision;

import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

public class ThaumGogglesModule extends AbstractPowerModule {
    ItemStack gogglesStack = ItemStack.EMPTY;

    public ThaumGogglesModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return null;
    }

//    public ThaumGogglesModule(EnumModuleTarget moduleTarget) {
//        super(moduleTarget);
//        if (ModCompatibility.isThaumCraftLoaded()) {
//            gogglesStack = new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("thaumcraft", "goggles")));
//
//            ModuleManager.INSTANCE.addInstallCost(getDataName(), ItemComponent.laserHologram.copy());
//            ModuleManager.INSTANCE.addInstallCost(getDataName(), gogglesStack);
//        }
//    }
//
//    @Override
//    public EnumModuleCategory getCategory() {
//        return EnumModuleCategory.VISION;
//    }
//
//    @Override
//    public String getDataName() {
//        return ModuleConstants.MODULE_THAUM_GOGGLES__REGNAME;
//    }
//
//    @Override
//    public TextureAtlasSprite getIcon(ItemStack item) {
//        return MuseIcon.aurameter;
//    }
//
//    @Nullable
//    @Override
//    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
//        return null;
//    }
}