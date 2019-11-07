package com.github.lehjr.modularpowerarmor.item.armor;

import com.github.lehjr.modularpowerarmor.api.constants.ModuleConstants;
import com.github.lehjr.modularpowerarmor.basemod.Constants;
import com.github.lehjr.modularpowerarmor.basemod.RegistryNames;
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
import com.github.lehjr.mpalib.misc.ModCompatibility;
import com.google.common.util.concurrent.AtomicDouble;
import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.arboriculture.ArboricultureCapabilities;
import forestry.api.core.IArmorNaturalist;
import micdoodle8.mods.galacticraft.api.item.IBreathableArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Ported to Java by lehjr on 10/26/16.
 */
@Optional.InterfaceList({
        @Optional.Interface(iface = "thaumcraft.api.items.IGoggles", modid = "thaumcraft", striprefs = true),
        @Optional.Interface(iface = "thaumcraft.api.items.IRevealer", modid = "thaumcraft", striprefs = true),
        @Optional.Interface(iface = "thaumcraft.api.items.IVisDiscountGear", modid = "thaumcraft", striprefs = true),
        @Optional.Interface(iface = "micdoodle8.mods.galacticraft.api.item.IBreathableArmor", modid = "galacticraftcore", striprefs = true)
})
public class ItemPowerArmorHelmet extends ItemPowerArmor implements
        IVisDiscountGear,
        IRevealer,
        IGoggles,
        IBreathableArmor {
    public final EntityEquipmentSlot armorType;

    public ItemPowerArmorHelmet(String regName) {
        super(regName, "powerArmorHelmet", 0, EntityEquipmentSlot.HEAD);
        this.armorType = EntityEquipmentSlot.HEAD;
    }

    @Optional.Method(modid = "thaumcraft")
    public boolean showIngamePopups(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return java.util.Optional.ofNullable(itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
            if (iItemHandler instanceof IModularItem) {
                return ((IModularItem) iItemHandler).isModuleOnline(new ResourceLocation(RegistryNames.MODULE_THAUM_GOGGLES__REGNAME));
            }
            return false;
        }).orElse(false);
    }

    @Optional.Method(modid = "thaumcraft")
    public boolean showNodes(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return java.util.Optional.ofNullable(itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
            if (iItemHandler instanceof IModularItem) {
                return ((IModularItem) iItemHandler).isModuleOnline(new ResourceLocation(RegistryNames.MODULE_THAUM_GOGGLES__REGNAME));
            }
            return false;
        }).orElse(false);
    }

    @Optional.Method(modid = "thaumcraft")
    public int getVisDiscount(ItemStack itemStack, EntityPlayer entityPlayer) {
        return java.util.Optional.ofNullable(itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
            if (iItemHandler instanceof IModularItem) {
                return ((IModularItem) iItemHandler).isModuleOnline(new ResourceLocation(RegistryNames.MODULE_THAUM_GOGGLES__REGNAME));
            }
            return false;
        }).orElse(false) ? 5 : 0;
    }

    @Optional.Method(modid = "galacticraftcore")
    @Override
    public boolean handleGearType(EnumGearType enumGearType) {
        return enumGearType == EnumGearType.HELMET;
    }

    @Optional.Method(modid = "galacticraftcore")
    @Override
    public boolean canBreathe(ItemStack itemStack, EntityPlayer entityPlayer, EnumGearType enumGearType) {
        return java.util.Optional.ofNullable(itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)).map(iItemHandler -> {
            if (iItemHandler instanceof IModularItem) {
                return ((IModularItem) iItemHandler).isModuleOnline(new ResourceLocation(RegistryNames.MODULE_AIRTIGHT_SEAL__REGNAME));
            }
            return false;
        }).orElse(false);
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
            maxHeat = new AtomicDouble(MPAConfig.INSTANCE.getBaseMaxHeat(armor));

            this.modularItemCap = new ModularItem(armor, 18) {{
                /*
                 * Limit only Armor, Energy Storage and Energy Generation
                 *
                 * This cuts down on overhead for accessing the most commonly used values
                 */
                Map<EnumModuleCategory, MPALibRangedWrapper> rangedWrapperMap = new HashMap<>();
                int i = 0;

                rangedWrapperMap.put(EnumModuleCategory.ARMOR,new MPALibRangedWrapper(this, 0, 1));
                rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE,new MPALibRangedWrapper(this, 1, 2));
                rangedWrapperMap.put(EnumModuleCategory.ENERGY_GENERATION,new MPALibRangedWrapper(this, 2, 3));
                rangedWrapperMap.put(EnumModuleCategory.NONE,new MPALibRangedWrapper(this, 3, this.getSlots()-1));
                setRangedWrapperMap(rangedWrapperMap);
            }};

            this.energyStorage = java.util.Optional.ofNullable(this.modularItemCap.getStackInSlot(1).getCapability(CapabilityEnergy.ENERGY, null)).orElse(new EmptyEnergyWrapper());
            java.util.Optional.ofNullable(this.modularItemCap.getStackInSlot(0).getCapability(PowerModuleCapability.POWER_MODULE, null)).ifPresent(m-> maxHeat.getAndAdd(m.applyPropertyModifiers(Constants.MAXIMUM_HEAT)));
            this.modelSpec = new ArmorModelSpecNBT(armor);
            this.heatStorage = new MuseHeatItemWrapper(armor, maxHeat.get());
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (ModCompatibility.isForestryLoaded()) {
                if (capability == ArboricultureCapabilities.ARMOR_NATURALIST) {
                    return modularItemCap.isModuleInstalled(new ResourceLocation(RegistryNames.MODULE_APIARIST_ARMOR__REGNAME));
                }

                if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
                    return modularItemCap.isModuleInstalled(new ResourceLocation(RegistryNames.MODULE_APIARIST_ARMOR__REGNAME));
                }
            }

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
                return java.util.Optional.ofNullable(this.modularItemCap.getStackInSlot(1).getCapability(CapabilityEnergy.ENERGY, null)).isPresent();
            }
            return false;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (ModCompatibility.isForestryLoaded()) {
                if (capability == ArboricultureCapabilities.ARMOR_NATURALIST) {

                }

                if (capability == ApicultureCapabilities.ARMOR_APIARIST) {

                }
            }

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
                return (T) java.util.Optional.ofNullable(this.modularItemCap.getStackInSlot(1).getCapability(CapabilityEnergy.ENERGY, null)).orElse(null);
            }
            return null;
        }

        class EmptyEnergyWrapper extends EnergyStorage {
            public EmptyEnergyWrapper() {
                super(0);
            }
        }
    }
}