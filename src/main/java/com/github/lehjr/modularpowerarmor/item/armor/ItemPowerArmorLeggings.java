package com.github.lehjr.modularpowerarmor.item.armor;

import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.render.ArmorModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.heat.HeatCapability;
import com.github.lehjr.mpalib.capabilities.heat.HeatItemWrapper;
import com.github.lehjr.mpalib.capabilities.heat.IHeatWrapper;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.MPALibRangedWrapper;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.ModularItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.render.IArmorModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ItemPowerArmorLeggings extends ItemPowerArmor {
    public ItemPowerArmorLeggings(String regName) {
        super(EquipmentSlotType.LEGS);
        setRegistryName(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new PowerArmorCap(stack);
    }

    class PowerArmorCap implements ICapabilityProvider {
        ItemStack armor;
        IModularItem modularItemCap;
        IHeatWrapper heatStorage;
        IArmorModelSpecNBT modelSpec;
        double maxHeat = MPASettings.getMaxHeatLegs();

        public PowerArmorCap(@Nonnull ItemStack armor) {
            this.armor = armor;
            this.modularItemCap = new ModularItem(armor, 10) {{
                /*
                 * Limit only Armor, Energy Storage and Energy Generation
                 *
                 * This cuts down on overhead for accessing the most commonly used values
                 *
                 */
                Map<EnumModuleCategory, MPALibRangedWrapper> rangedWrapperMap = new HashMap<>();
                rangedWrapperMap.put(EnumModuleCategory.ARMOR,new MPALibRangedWrapper(this, 0, 1));
                rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE,new MPALibRangedWrapper(this, 1, 2));
                rangedWrapperMap.put(EnumModuleCategory.ENERGY_GENERATION,new MPALibRangedWrapper(this, 2, 3));
                rangedWrapperMap.put(EnumModuleCategory.NONE,new MPALibRangedWrapper(this, 3, this.getSlots()-1));
                this.setRangedWrapperMap(rangedWrapperMap);
            }};
            this.modelSpec = new ArmorModelSpecNBT(armor);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == null) {
                return LazyOptional.empty();
            }
            if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                modularItemCap.updateFromNBT();
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(()->modularItemCap));
            }

            // update item handler to gain access to the armor module if installed
            if (cap == HeatCapability.HEAT) {
                modularItemCap.updateFromNBT();
                // initialize heat storage with whatever value is retrieved
                this.heatStorage = new HeatItemWrapper(
                        armor, maxHeat, modularItemCap.getStackInSlot(0).getCapability(PowerModuleCapability.POWER_MODULE));
                // update heat storage to set current heat amount
                heatStorage.updateFromNBT();
                return HeatCapability.HEAT.orEmpty(cap, LazyOptional.of(()-> heatStorage));
            }

            if (cap == ModelSpecNBTCapability.RENDER) {
                return ModelSpecNBTCapability.RENDER.orEmpty(cap, LazyOptional.of(()->modelSpec));
            }

            // update item handler to gain access to the battery module if installed
            if (cap == CapabilityEnergy.ENERGY) {
                modularItemCap.updateFromNBT();
                return modularItemCap.getStackInSlot(1).getCapability(cap, side);
            }
            return LazyOptional.empty();
        }
    }
}