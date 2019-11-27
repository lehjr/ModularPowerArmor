package com.github.machinemuse.powersuits.item.armor;

import com.github.lehjr.mpalib.capabilities.heat.HeatCapability;
import com.github.lehjr.mpalib.capabilities.heat.MuseHeatItemWrapper;
import com.github.lehjr.mpalib.capabilities.render.IArmorModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.capabilities.ForgeEnergyItemWrapper;
import com.github.machinemuse.powersuits.client.render.ArmorModelSpecNBT;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.config.MPSConfig;
import com.google.common.util.concurrent.AtomicDouble;
import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.apiculture.IArmorApiarist;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Ported to Java by lehjr on 10/26/16.
 */
public class ItemPowerArmorLeggings extends ItemPowerArmor {
    public final EntityEquipmentSlot armorType;

    public ItemPowerArmorLeggings(String regName) {
        super(regName, "powerArmorLeggings",0, EntityEquipmentSlot.LEGS);
        this.armorType = EntityEquipmentSlot.LEGS;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new PowerArmorCap(stack);
    }

    static class PowerArmorCap implements ICapabilityProvider {
        ItemStack armor;
        ForgeEnergyItemWrapper energyStorage;
        MuseHeatItemWrapper heatStorage;
        IArmorModelSpecNBT modelSpec;
        AtomicDouble maxHeat;

        public PowerArmorCap(@Nonnull ItemStack armorIn) {
            this.armor = armorIn;
            maxHeat = new AtomicDouble(MPSConfig.INSTANCE.getBaseMaxHeatLegs());
            energyStorage = new ForgeEnergyItemWrapper(armor, ModuleManager.INSTANCE);
            modelSpec = new ArmorModelSpecNBT(armor);
            heatStorage = new MuseHeatItemWrapper(armor, maxHeat.get());
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (ModCompatibility.isForestryLoaded()) {
                if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
                    return ModuleManager.INSTANCE.itemHasActiveModule(armor, MPSModuleConstants.MODULE_APIARIST_ARMOR__DATANAME);
                }
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
            if (ModCompatibility.isForestryLoaded()) {
                if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
                    return (T) new IArmorApiarist() {
                        @Override
                        public boolean protectEntity(EntityLivingBase entity, ItemStack armor, @Nullable String cause, boolean doProtect) {
                            if (ModuleManager.INSTANCE.itemHasActiveModule(armor, MPSModuleConstants.MODULE_APIARIST_ARMOR__DATANAME)) {
                                ElectricItemUtils.drainPlayerEnergy((EntityPlayer) entity, (int) ModuleManager.INSTANCE.getOrSetModularPropertyDouble(armor, MPSModuleConstants.APIARIST_ARMOR_ENERGY_CONSUMPTION));
                                return true;
                            }
                            return false;
                        }
                    };
                }
            }

            if (capability == HeatCapability.HEAT) {
                heatStorage.updateFromNBT();
                return (T) heatStorage;
            }

            if (capability == ModelSpecNBTCapability.RENDER) {
                return (T) modelSpec;
            }

            if (capability == CapabilityEnergy.ENERGY) {
                energyStorage.updateFromNBT();
                return (T) energyStorage;
            }

            return null;
        }
    }
}