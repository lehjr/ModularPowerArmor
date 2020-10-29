package com.github.lehjr.modularpowerarmor.capabilities;

import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;
import com.github.lehjr.modularpowerarmor.client.render.ArmorModelSpecNBT;
import com.github.lehjr.modularpowerarmor.client.render.PowerFistSpecNBT;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.mpalib.util.capabilities.heat.HeatCapability;
import com.github.lehjr.mpalib.util.capabilities.heat.HeatItemWrapper;
import com.github.lehjr.mpalib.util.capabilities.heat.IHeatWrapper;
import com.github.lehjr.mpalib.util.capabilities.inventory.modechanging.ModeChangingModularItem;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.MPALibRangedWrapper;
import com.github.lehjr.mpalib.util.capabilities.inventory.modularitem.ModularItem;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.util.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.util.capabilities.render.IModelSpecNBT;
import com.github.lehjr.mpalib.util.capabilities.render.ModelSpecNBTCapability;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ModularPowerCap implements ICapabilityProvider {
    ItemStack itemStack;
    IModularItem modularItemCap;
    IHeatWrapper heatStorage;
    IModelSpecNBT modelSpec;
    EquipmentSlotType targetSlot;
    double maxHeat;

    class EmptyFluidHandler extends FluidHandlerItemStack {
        public EmptyFluidHandler() {
            super(ItemStack.EMPTY, 0);
        }
    }

    class EmptyEnergyWrapper extends EnergyStorage {
        public EmptyEnergyWrapper() {
            super(0);
        }
    }

    public ModularPowerCap(@Nonnull ItemStack itemStackIn, EquipmentSlotType slot) {
        this.itemStack = itemStackIn;
        this.targetSlot = slot;
        Map<EnumModuleCategory, MPALibRangedWrapper> rangedWrapperMap = new HashMap<>();

        switch(targetSlot) {
            case HEAD: {
                this.modularItemCap = new ModularItem(itemStack, 18) {{
                    rangedWrapperMap.put(EnumModuleCategory.ARMOR,new MPALibRangedWrapper(this, 0, 1));
                    rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE,new MPALibRangedWrapper(this, 1, 2));
                    rangedWrapperMap.put(EnumModuleCategory.ENERGY_GENERATION,new MPALibRangedWrapper(this, 2, 3));
                    rangedWrapperMap.put(EnumModuleCategory.NONE,new MPALibRangedWrapper(this, 3, this.getSlots() - 1));
                    setRangedWrapperMap(rangedWrapperMap);
                }};
                this.modelSpec = new ArmorModelSpecNBT(itemStack);
                this.maxHeat = MPASettings.getMaxHeatHelmet();
            }

            case CHEST: {
                this.modularItemCap = new ModularItem(itemStack, 18) {{
                    rangedWrapperMap.put(EnumModuleCategory.ARMOR,new MPALibRangedWrapper(this, 0, 1));
                    rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE,new MPALibRangedWrapper(this, 1, 2));
                    rangedWrapperMap.put(EnumModuleCategory.ENERGY_GENERATION,new MPALibRangedWrapper(this, 2, 3));
                    rangedWrapperMap.put(EnumModuleCategory.NONE,new MPALibRangedWrapper(this, 3, this.getSlots()-1));
                    this.setRangedWrapperMap(rangedWrapperMap);
                }};
                this.modelSpec = new ArmorModelSpecNBT(itemStack);
                this.maxHeat = MPASettings.getMaxHeatChestplate();
            }

            case LEGS: {
                this.modularItemCap = new ModularItem(itemStackIn, 10) {{
                    rangedWrapperMap.put(EnumModuleCategory.ARMOR,new MPALibRangedWrapper(this, 0, 1));
                    rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE,new MPALibRangedWrapper(this, 1, 2));
                    rangedWrapperMap.put(EnumModuleCategory.ENERGY_GENERATION,new MPALibRangedWrapper(this, 2, 3));
                    rangedWrapperMap.put(EnumModuleCategory.NONE,new MPALibRangedWrapper(this, 3, this.getSlots()-1));
                    this.setRangedWrapperMap(rangedWrapperMap);
                }};
                this.modelSpec = new ArmorModelSpecNBT(itemStack);
                this.maxHeat = MPASettings.getMaxHeatLegs();
            }

            case FEET: {
                this.modularItemCap = new ModularItem(itemStack, 8) {{
                    rangedWrapperMap.put(EnumModuleCategory.ARMOR,new MPALibRangedWrapper(this, 0, 1));
                    rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE,new MPALibRangedWrapper(this, 1, 2));
                    rangedWrapperMap.put(EnumModuleCategory.NONE,new MPALibRangedWrapper(this, 2, this.getSlots()-1));
                    this.setRangedWrapperMap(rangedWrapperMap);
                }};
                this.modelSpec = new ArmorModelSpecNBT(itemStack);
                this.maxHeat = MPASettings.getMaxHeatBoots();
            }

            // Fist
            default: {
                this.modularItemCap = new ModeChangingModularItem(itemStack, 40)  {{
                    Map<EnumModuleCategory, MPALibRangedWrapper> rangedWrapperMap = new HashMap<>();
                    rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE, new MPALibRangedWrapper(this, 0, 1));
                    rangedWrapperMap.put(EnumModuleCategory.NONE, new MPALibRangedWrapper(this, 1, this.getSlots() - 1));
                    this.setRangedWrapperMap(rangedWrapperMap);
                }};
                this.modelSpec = new PowerFistSpecNBT(itemStack);
                this.maxHeat = MPASettings.getMaxHeatPowerFist();
                this.heatStorage = new HeatItemWrapper(itemStack, maxHeat);
            }
        }
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
            if (targetSlot.getSlotType() == EquipmentSlotType.Group.ARMOR) {
                modularItemCap.updateFromNBT();
                // initialize heat storage with whatever value is retrieved
                this.heatStorage = new HeatItemWrapper(
                        itemStack, maxHeat, modularItemCap.getStackInSlot(0).getCapability(PowerModuleCapability.POWER_MODULE));
                // update heat storage to set current heat amount
                heatStorage.updateFromNBT();
            }
            return HeatCapability.HEAT.orEmpty(cap, LazyOptional.of(()-> heatStorage));
        }

        if (cap == HeatCapability.HEAT) {
            heatStorage.updateFromNBT();
            return HeatCapability.HEAT.orEmpty(cap, LazyOptional.of(()->heatStorage));
        }

        // currently chest only
        if (targetSlot == EquipmentSlotType.CHEST && cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
            modularItemCap.updateFromNBT();
            return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap,
                    LazyOptional.of(()->modularItemCap.getOnlineModuleOrEmpty(MPARegistryNames.FLUID_TANK_MODULE_REGNAME).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(new EmptyFluidHandler())));
        }

        if (cap == ModelSpecNBTCapability.RENDER) {
            return ModelSpecNBTCapability.RENDER.orEmpty(cap, LazyOptional.of(()->modelSpec));
        }

        // update item handler to gain access to the battery module if installed
        if (cap == CapabilityEnergy.ENERGY) {
            modularItemCap.updateFromNBT();
            // armor first slot is armor plating, second slot is energy
            return modularItemCap.getStackInSlot(targetSlot.getSlotType() == EquipmentSlotType.Group.ARMOR ? 1 : 0).getCapability(cap, side);
        }

        return LazyOptional.empty();
    }
}