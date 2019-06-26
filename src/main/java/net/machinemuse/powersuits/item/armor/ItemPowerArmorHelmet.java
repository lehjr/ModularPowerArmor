package net.machinemuse.powersuits.item.armor;

import com.google.common.util.concurrent.AtomicDouble;
import net.machinemuse.numina.capabilities.heat.CapabilityHeat;
import net.machinemuse.numina.capabilities.heat.IHeatStorage;
import net.machinemuse.numina.capabilities.heat.MuseHeatItemWrapper;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.inventory.modularitem.ModularItem;
import net.machinemuse.numina.capabilities.inventory.modularitem.ModularItemCapability;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ItemPowerArmorHelmet extends ItemPowerArmor {
    public ItemPowerArmorHelmet(String regName) {
        super(EquipmentSlotType.HEAD);
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
        IEnergyStorage energyStorage;
        IHeatStorage heatStorage;
        AtomicDouble maxHeat = new AtomicDouble(MPSConfig.GENERAL_BASE_MAX_HEAT_HELMET != null ? MPSConfig.GENERAL_BASE_MAX_HEAT_HELMET.get() : 5.0);

        public PowerArmorCap(@Nonnull ItemStack armor) {
            this.armor = armor;
            this.modularItemCap = new ModularArmorCap();
            this.energyStorage = this.modularItemCap.getStackInSlot(1).getCapability(CapabilityEnergy.ENERGY).orElse(new EmptyEnergyWrapper());
            this.modularItemCap.getStackInSlot(0).getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(m-> maxHeat.getAndAdd(m.applyPropertyModifiers(MPSConstants.MAXIMUM_HEAT)));
            this.heatStorage = new MuseHeatItemWrapper(armor, maxHeat.get());
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == ModularItemCapability.MODULAR_ITEM)
                return ModularItemCapability.MODULAR_ITEM.orEmpty(cap, LazyOptional.of(()->modularItemCap));
            if (cap == CapabilityEnergy.ENERGY)
                return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(()-> energyStorage));
            return CapabilityHeat.HEAT.orEmpty(cap, LazyOptional.of(()-> heatStorage));
        }

        class ModularArmorCap extends ModularItem {
            public ModularArmorCap() {
                super(armor, 18);

                /*
                 * Limit only Armor, Energy Storage and Energy Generation
                 *
                 * This cuts down on overhead for accessing the most commonly used values
                 */
                Map<EnumModuleCategory, RangedWrapper> rangedWrapperMap = new HashMap<>();
                rangedWrapperMap.put(EnumModuleCategory.CATEGORY_ARMOR,new RangedWrapper(this, 0, 1));
                rangedWrapperMap.put(EnumModuleCategory.CATEGORY_ENERGY_STORAGE,new RangedWrapper(this, 1, 2));
                rangedWrapperMap.put(EnumModuleCategory.CATEGORY_ENERGY_GENERATION,new RangedWrapper(this, 2, 3));
                rangedWrapperMap.put(EnumModuleCategory.CATEGORY_NONE,new RangedWrapper(this, 3, this.getSlots()-1));
                this.setRangedWrapperMap(rangedWrapperMap);
            }

            // Armor attributes might have to be set through a ticking event like the sprint/walking assist module

//            @Override
//            public double getArmorDouble() {
//                AtomicDouble armorDouble = new AtomicDouble(0);
//                getStackInSlot(0).getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(m->{
//                   armorDouble.getAndAdd(m.applyPropertyModifiers());
//                    double totalArmor = 0.0;
//                    double energy = ElectricItemUtils.getPlayerEnergy(player);
//                    double physArmor = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(stack, MPSModuleConstants.ARMOR_VALUE_PHYSICAL);
//                    double enerArmor = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(stack, MPSModuleConstants.ARMOR_VALUE_ENERGY);
//                    double enerConsum = ModuleManager.INSTANCE.getOrSetModularPropertyDouble(stack, MPSModuleConstants.ARMOR_ENERGY_CONSUMPTION);
//
//                    totalArmor += physArmor;
//                    if (energy > enerConsum) {
//                        totalArmor += enerArmor;
//                    }
//                    totalArmor = Math.min(MPSConfig.INSTANCE.getMaximumArmorPerPiece(), totalArmor);
//
//                    return totalArmor;
//                });
//                return armorDouble.get();
//            }
        }

        class EmptyEnergyWrapper extends EnergyStorage {
            public EmptyEnergyWrapper() {
                super(0);
            }
        }
    }
}