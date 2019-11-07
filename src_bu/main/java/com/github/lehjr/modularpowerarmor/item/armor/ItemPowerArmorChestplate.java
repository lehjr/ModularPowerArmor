package com.github.lehjr.modularpowerarmor.item.armor;

import com.github.lehjr.mpalib.capabilities.heat.HeatCapability;
import com.github.lehjr.mpalib.capabilities.heat.IHeatWrapper;
import com.github.lehjr.mpalib.capabilities.heat.MuseHeatItemWrapper;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.IModularItem;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.MPALibRangedWrapper;
import com.github.lehjr.mpalib.capabilities.inventory.modularitem.ModularItem;
import com.github.lehjr.mpalib.capabilities.module.powermodule.EnumModuleCategory;
import com.github.lehjr.mpalib.capabilities.module.powermodule.PowerModuleCapability;
import com.github.lehjr.mpalib.capabilities.render.IArmorModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.google.common.util.concurrent.AtomicDouble;
import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.MPARegistryNames;

import com.github.lehjr.modularpowerarmor.render.ArmorModelSpecNBT;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ItemPowerArmorChestplate extends ItemPowerArmor {
    static final ResourceLocation fluidTank = new ResourceLocation(MPARegistryNames.MODULE_FLUID_TANK__REGNAME);

    public ItemPowerArmorChestplate(String regName) {
        super(EquipmentSlotType.CHEST);
        setRegistryName(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new PowerArmorCap(stack);
    }

    class PowerArmorCap implements ICapabilityProvider {
        ItemStack armor;
        IModularItem modularItemCap;
        IEnergyStorage energyStorage;
        IHeatWrapper heatStorage;
        IArmorModelSpecNBT modelSpec;
        IFluidHandlerItem fluidHandler;
        AtomicDouble maxHeat = new AtomicDouble(MPAConfig.baseMaxHeatChest());

        public PowerArmorCap(@Nonnull ItemStack armor) {
            this.armor = armor;
            this.modularItemCap = new ModularArmorCap();
            this.energyStorage = this.modularItemCap.getStackInSlot(1).getCapability(CapabilityEnergy.ENERGY).orElse(new EmptyEnergyWrapper());
            this.modularItemCap.getStackInSlot(0).getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(m-> maxHeat.getAndAdd(m.applyPropertyModifiers(Constants.MAXIMUM_HEAT)));
            this.fluidHandler = modularItemCap.getOnlineModuleOrEmpty(fluidTank).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).orElse(new EmptyFluidHandler());
            this.modelSpec = new ArmorModelSpecNBT(armor);
            this.heatStorage = new MuseHeatItemWrapper(armor, maxHeat.get());
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
            if (cap == HeatCapability.HEAT) {
                heatStorage.updateFromNBT();
                return HeatCapability.HEAT.orEmpty(cap, LazyOptional.of(()-> heatStorage));
            }

            if (cap == ModelSpecNBTCapability.RENDER) {
                return ModelSpecNBTCapability.RENDER.orEmpty(cap, LazyOptional.of(()->modelSpec));
            }

            if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
                return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, LazyOptional.of(()->fluidHandler));
            }
            return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(()-> energyStorage));
        }

        class ModularArmorCap extends ModularItem {
            public ModularArmorCap() {
                super(armor, 18);
                /*
                 * Limit only Armor, Energy Storage and Energy Generation
                 *
                 * This cuts down on overhead for accessing the most commonly used values
                */
                Map<EnumModuleCategory, MPALibRangedWrapper> rangedWrapperMap = new HashMap<>();
                rangedWrapperMap.put(EnumModuleCategory.ARMOR,new MPALibRangedWrapper(this, 0, 1));
                rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE,new MPALibRangedWrapper(this, 1, 2));
                rangedWrapperMap.put(EnumModuleCategory.ENERGY_GENERATION,new MPALibRangedWrapper(this, 2, 3));
                rangedWrapperMap.put(EnumModuleCategory.NONE,new MPALibRangedWrapper(this, 3, this.getSlots()-1));
                this.setRangedWrapperMap(rangedWrapperMap);
            }
        }

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
    }
}