package com.github.lehjr.modularpowerarmor.capabilities;

import com.github.lehjr.modularpowerarmor.client.render.PowerFistSpecNBT;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.mpalib.util.capabilities.heat.HeatCapability;
import com.github.lehjr.mpalib.util.capabilities.heat.HeatItemWrapper;
import com.github.lehjr.mpalib.util.capabilities.inventory.modechanging.ModeChangingModularItem;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.MPALibRangedWrapper;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PowerFistCap extends AbstractModularPowerCap {
    public PowerFistCap(@Nonnull ItemStack itemStackIn) {
        this.itemStack = itemStackIn;
        this.targetSlot = EquipmentSlotType.MAINHAND;

        this.modularItemCap = new ModeChangingModularItem(itemStack, 40)  {{
            Map<EnumModuleCategory, MPALibRangedWrapper> rangedWrapperMap = new HashMap<>();
            rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE, new MPALibRangedWrapper(this, 0, 1));
            rangedWrapperMap.put(EnumModuleCategory.NONE, new MPALibRangedWrapper(this, 1, this.getSlots() - 1));
            this.setRangedWrapperMap(rangedWrapperMap);
        }};
        this.modelSpec = new PowerFistSpecNBT(itemStack);
        this.heatStorage = new HeatItemWrapper(itemStack, MPASettings.getMaxHeatPowerFist());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == null) {
            return LazyOptional.empty();
        }

        if (cap == HeatCapability.HEAT) {
            heatStorage.updateFromNBT();
            return HeatCapability.HEAT.orEmpty(cap, LazyOptional.of(()->heatStorage));
        }

        return super.getCapability(cap, side);
    }
}
