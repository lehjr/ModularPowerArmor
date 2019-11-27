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
import com.github.machinemuse.powersuits.client.render.ArmorModelSpecNBT;
import com.github.machinemuse.powersuits.basemod.ModuleManager;
import com.github.machinemuse.powersuits.config.MPSConfig;
import forestry.api.apiculture.ApicultureCapabilities;
import forestry.api.apiculture.IArmorApiarist;
import forestry.api.arboriculture.ArboricultureCapabilities;
import forestry.api.core.IArmorNaturalist;
import micdoodle8.mods.galacticraft.api.item.IBreathableArmor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        return ModuleManager.INSTANCE.itemHasModule(itemStack, MPSModuleConstants.MODULE_THAUM_GOGGLES__DATANAME);
    }

    @Optional.Method(modid = "thaumcraft")
    public boolean showNodes(ItemStack itemStack, EntityLivingBase entityLivingBase) {
        return ModuleManager.INSTANCE.itemHasModule(itemStack, MPSModuleConstants.MODULE_THAUM_GOGGLES__DATANAME);
    }

    @Optional.Method(modid = "thaumcraft")
    public int getVisDiscount(ItemStack itemStack, EntityPlayer entityPlayer) {
        return ModuleManager.INSTANCE.itemHasModule(itemStack, MPSModuleConstants.MODULE_THAUM_GOGGLES__DATANAME) ? 5 : 0;
    }

    @Optional.Method(modid = "galacticraftcore")
    @Override
    public boolean handleGearType(EnumGearType enumGearType) {
        return enumGearType == EnumGearType.HELMET;
    }

    @Optional.Method(modid = "galacticraftcore")
    @Override
    public boolean canBreathe(ItemStack itemStack, EntityPlayer entityPlayer, EnumGearType enumGearType) {
        return ModuleManager.INSTANCE.itemHasModule(itemStack, MPSModuleConstants.MODULE_AIRTIGHT_SEAL__DATANAME);
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
        double maxHeat;

        public PowerArmorCap(@Nonnull ItemStack armorIn) {
            this.armor = armorIn;
            maxHeat = MPSConfig.INSTANCE.getBaseMaxHeatHelmet();
            energyStorage = new ForgeEnergyItemWrapper(armor, ModuleManager.INSTANCE);
            modelSpec = new ArmorModelSpecNBT(armor);
            heatStorage = new MuseHeatItemWrapper(armor, maxHeat);
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            if (ModCompatibility.isForestryLoaded()) {
                if (capability == ArboricultureCapabilities.ARMOR_NATURALIST) {
                    return ModuleManager.INSTANCE.itemHasActiveModule(armor, MPSModuleConstants.MODULE_APIARIST_ARMOR__DATANAME);
                }

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
            return false;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (ModCompatibility.isForestryLoaded()) {
                if (capability == ArboricultureCapabilities.ARMOR_NATURALIST) {
                    return (T) new IArmorNaturalist() {
                        @Override
                        public boolean canSeePollination(EntityPlayer player, ItemStack armor, boolean doSee) {
                            return ModuleManager.INSTANCE.itemHasActiveModule(armor, MPSModuleConstants.MODULE_APIARIST_ARMOR__DATANAME);
                        }
                    };
                }

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