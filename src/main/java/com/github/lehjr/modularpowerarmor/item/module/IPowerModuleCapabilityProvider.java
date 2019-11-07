package com.github.lehjr.modularpowerarmor.item.module;

import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author lehjr
 */
public interface IPowerModuleCapabilityProvider extends ICapabilityProvider {

    @Override
    default boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == PowerModuleCapability.POWER_MODULE;
    }
}
