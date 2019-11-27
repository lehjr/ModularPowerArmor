package com.github.machinemuse.powersuits.item.armor;

import com.github.lehjr.mpalib.capabilities.heat.HeatCapability;
import com.github.lehjr.mpalib.capabilities.heat.IHeatWrapper;
import com.github.lehjr.mpalib.capabilities.heat.MuseHeatItemWrapper;
import com.github.lehjr.mpalib.capabilities.render.IArmorModelSpecNBT;
import com.github.lehjr.mpalib.capabilities.render.ModelSpecNBTCapability;
import com.github.lehjr.mpalib.energy.ElectricItemUtils;
import com.github.lehjr.mpalib.misc.ModCompatibility;
import com.github.machinemuse.powersuits.api.constants.MPSModuleConstants;
import com.github.machinemuse.powersuits.capabilities.ForgeEnergyItemWrapper;
import com.github.machinemuse.powersuits.capabilities.MPSChestPlateFluidHandler;
import com.github.machinemuse.powersuits.client.render.ArmorModelSpecNBT;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.config.MPSConfig;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Ported to Java by lehjr on 10/26/16.
 */
public class ItemPowerArmorChestplate extends ItemPowerArmor {
    public final EntityEquipmentSlot armorType;

    public ItemPowerArmorChestplate(String regName) {
        super(regName, "powerArmorChestplate", 0, EntityEquipmentSlot.CHEST);
        this.armorType = EntityEquipmentSlot.CHEST;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new PowerArmorCap(stack);
    }

    static class PowerArmorCap implements ICapabilityProvider {
        ItemStack armor;
        ForgeEnergyItemWrapper energyStorage;
        IHeatWrapper heatStorage;
        IArmorModelSpecNBT modelSpec;
        MPSChestPlateFluidHandler chestPlateFluidHandler;

        public PowerArmorCap(@Nonnull ItemStack armorIn) {
            armor = armorIn;
            energyStorage = new ForgeEnergyItemWrapper(armor, ModuleManager.INSTANCE);
            modelSpec = new ArmorModelSpecNBT(armor);
            heatStorage = new MuseHeatItemWrapper(armor, MPSConfig.INSTANCE.getBaseMaxHeatChest());
            chestPlateFluidHandler = new MPSChestPlateFluidHandler(armorIn, ModuleManager.INSTANCE);
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (ModCompatibility.isForestryLoaded()) {
                if (capability == ApicultureCapabilities.ARMOR_APIARIST) {
                    return ModuleManager.INSTANCE.itemHasActiveModule(armor, MPSModuleConstants.MODULE_APIARIST_ARMOR__DATANAME);
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
                return true;
            }
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
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

            if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
                chestPlateFluidHandler.updateFromNBT();
                return (T) chestPlateFluidHandler;
            }

            return null;
        }
    }
}