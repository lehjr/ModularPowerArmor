package com.github.lehjr.modularpowerarmor.item.armor;

import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.client.render.ArmorModelSpecNBT;
import com.github.lehjr.modularpowerarmor.config.MPAConfig;
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
import ic2.api.item.IMetalArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Ported to Java by lehjr on 10/26/16.
 */
@Optional.InterfaceList({@Optional.Interface(iface = "ic2.api.item.IMetalArmor", modid = "IC2", striprefs = true)})
public class ItemPowerArmorBoots extends ItemPowerArmor implements IMetalArmor {
    public final EntityEquipmentSlot armorType;

    public ItemPowerArmorBoots(String regName) {
        super(regName, "powerArmorBoots",0, EntityEquipmentSlot.FEET);
        this.armorType = EntityEquipmentSlot.FEET;
    }

    public boolean isMetalArmor(final ItemStack itemStack, final EntityPlayer entityPlayer) {
        return true;
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
        AtomicDouble maxHeat;

        public PowerArmorCap(@Nonnull ItemStack armor) {
            this.armor = armor;
            maxHeat = new AtomicDouble(MPAConfig.INSTANCE.getBaseMaxHeatFeet());
            this.modularItemCap = new ModularArmorCap();
            this.energyStorage = java.util.Optional.ofNullable(this.modularItemCap.getStackInSlot(1).getCapability(CapabilityEnergy.ENERGY, null)).orElse(new EmptyEnergyWrapper());
            java.util.Optional.ofNullable(this.modularItemCap.getStackInSlot(0).getCapability(PowerModuleCapability.POWER_MODULE, null))
                    .ifPresent(m-> maxHeat.getAndAdd(m.applyPropertyModifiers(Constants.MAXIMUM_HEAT)));
            this.modelSpec = new ArmorModelSpecNBT(armor);
            this.heatStorage = new MuseHeatItemWrapper(armor, maxHeat.get());
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                return true;
            }
            if (capability == HeatCapability.HEAT) {
                return true;
            }
            if (capability == ModelSpecNBTCapability.RENDER) {
                return true;
            }
            if (capability == CapabilityEnergy.ENERGY) {
                return true;
            }
            return false;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                modularItemCap.updateFromNBT();
                return (T) modularItemCap;
            }
            if (capability == HeatCapability.HEAT) {
                heatStorage.updateFromNBT();
                return (T) heatStorage;
            }
            if (capability == ModelSpecNBTCapability.RENDER) {
                return (T) modelSpec;
            }

            if (capability == CapabilityEnergy.ENERGY) {
                return (T) energyStorage;
            }
            return null;
        }

        class ModularArmorCap extends ModularItem {
            public ModularArmorCap() {
                super(armor, 8);

                /*
                 * Limit only Armor, Energy Storage and Energy Generation
                 *
                 * This cuts down on overhead for accessing the most commonly used values
                 *
                 */
                Map<EnumModuleCategory, MPALibRangedWrapper> rangedWrapperMap = new HashMap<>();
                rangedWrapperMap.put(EnumModuleCategory.ARMOR,new MPALibRangedWrapper(this, 0, 1));
                rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE,new MPALibRangedWrapper(this, 1, 2));
                rangedWrapperMap.put(EnumModuleCategory.NONE,new MPALibRangedWrapper(this, 2, this.getSlots()-1));
                this.setRangedWrapperMap(rangedWrapperMap);
            }
        }

        class EmptyEnergyWrapper extends EnergyStorage {
            public EmptyEnergyWrapper() {
                super(0);
            }
        }
    }
}