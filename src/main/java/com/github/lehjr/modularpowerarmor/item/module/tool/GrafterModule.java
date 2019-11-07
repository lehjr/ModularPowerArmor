package com.github.lehjr.modularpowerarmor.item.module.tool;

import com.github.lehjr.modularpowerarmor.config.MPAConfig;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.modularpowerarmor.item.module.IPowerModuleCapabilityProvider;
import com.github.lehjr.mpalib.capabilities.module.powermodule.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by User: Andrew
 * Date: 4/21/13
 * Time: 2:02 PM
 */
public class GrafterModule extends AbstractPowerModule {
    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new CapProvider(stack);
    }
    public GrafterModule(String regName) {
        super(regName);
    }

    public class CapProvider implements IPowerModuleCapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            moduleCap = new PowerModule(module, EnumModuleCategory.TOOL, EnumModuleTarget.TOOLONLY, MPAConfig.moduleConfig);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == PowerModuleCapability.POWER_MODULE) {
                return (T) moduleCap;
            }
            return null;
        }
    }
}