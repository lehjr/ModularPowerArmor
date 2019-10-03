package net.machinemuse.powersuits.item.armor;

import com.google.common.util.concurrent.AtomicDouble;
import net.machinemuse.numina.capabilities.heat.HeatCapability;
import net.machinemuse.numina.capabilities.heat.IHeatWrapper;
import net.machinemuse.numina.capabilities.heat.MuseHeatItemWrapper;
import net.machinemuse.numina.capabilities.inventory.modularitem.IModularItem;
import net.machinemuse.numina.capabilities.inventory.modularitem.ModularItem;
import net.machinemuse.numina.capabilities.inventory.modularitem.MuseRangedWrapper;
import net.machinemuse.numina.capabilities.module.powermodule.EnumModuleCategory;
import net.machinemuse.numina.capabilities.module.powermodule.PowerModuleCapability;
import net.machinemuse.numina.capabilities.render.IArmorModelSpecNBT;
import net.machinemuse.numina.capabilities.render.ModelSpecNBTCapability;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.basemod.config.CommonConfig;
import net.machinemuse.powersuits.capabilities.render.ArmorModelSpecNBT;
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
        IEnergyStorage energyStorage;
        IHeatWrapper heatStorage;
        IArmorModelSpecNBT modelSpec;
        AtomicDouble maxHeat = new AtomicDouble(CommonConfig.baseMaxHeatLegs());

        public PowerArmorCap(@Nonnull ItemStack armor) {
            this.armor = armor;
            this.modularItemCap = new ModularArmorCap();
            this.energyStorage = this.modularItemCap.getStackInSlot(1).getCapability(CapabilityEnergy.ENERGY).orElse(new EmptyEnergyWrapper());
            this.modularItemCap.getStackInSlot(0).getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(m-> maxHeat.getAndAdd(m.applyPropertyModifiers(MPSConstants.MAXIMUM_HEAT)));
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
            return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(()-> energyStorage));
        }

        class ModularArmorCap extends ModularItem {
            public ModularArmorCap() {
                super(armor, 10);

                /*
                 * Limit only Armor, Energy Storage and Energy Generation
                 *
                 * This cuts down on overhead for accessing the most commonly used values
                 *
                 */
                Map<EnumModuleCategory, MuseRangedWrapper> rangedWrapperMap = new HashMap<>();
                rangedWrapperMap.put(EnumModuleCategory.ARMOR,new MuseRangedWrapper(this, 0, 1));
                rangedWrapperMap.put(EnumModuleCategory.ENERGY_STORAGE,new MuseRangedWrapper(this, 1, 2));
                rangedWrapperMap.put(EnumModuleCategory.ENERGY_GENERATION,new MuseRangedWrapper(this, 2, 3));
                rangedWrapperMap.put(EnumModuleCategory.NONE,new MuseRangedWrapper(this, 3, this.getSlots()-1));
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