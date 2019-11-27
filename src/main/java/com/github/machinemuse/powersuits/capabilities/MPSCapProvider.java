//package com.github.machinemuse.powersuits.capabilities;
//
//import com.github.lehjr.mpalib.capabilities.heat.HeatCapability;
//import com.github.machinemuse.powersuits.basemod.ModuleManager;
//import com.github.machinemuse.powersuits.config.MPSConfig;
//import com.github.machinemuse.powersuits.item.armor.ItemPowerArmorChestplate;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.EnumFacing;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.ICapabilityProvider;
//import net.minecraftforge.energy.CapabilityEnergy;
//import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//
//public final class MPSCapProvider implements ICapabilityProvider {
//    private final ItemStack container;
//    MuseHeatItemWrapper heatWrapper;
//    ForgeEnergyItemWrapper energyContainerWrapper;
//    ItemHandlerPowerFist powerFistItemHandler;
//    MPSChestPlateFluidHandler chestPlateFluidHandler;
//
//    public MPSCapProvider(@Nonnull final ItemStack containerIn) {
//        this.container = containerIn;
//
//        // Forge Energy
//        energyContainerWrapper = new ForgeEnergyItemWrapper(containerIn, ModuleManager.INSTANCE);
//
//        // Heat
//        heatWrapper = new MuseHeatItemWrapper(containerIn, MPSConfig.INSTANCE.getBaseMaxHeat(containerIn), ModuleManager.INSTANCE);
//
//        if (container.getItem() instanceof ItemPowerArmorChestplate)
//            chestPlateFluidHandler = new MPSChestPlateFluidHandler(container, ModuleManager.INSTANCE);
//
//
//        // todo: fluid handlers for cooling system modules
//
//
//    }
//
//
//    @Override
//    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
//        if (/*capability == CapabilityHeat.HEAT ||*/ capability == CapabilityEnergy.ENERGY)
//            return true;
//
//        if (capability == HeatCapability.HEAT)
//            if (heatWrapper != null)
//                return true;
//
//        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
//            return chestPlateFluidHandler != null;
//
//
//        return false;
//    }
//
//    @Nullable
//    @Override
//    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
//        if (capability == CapabilityEnergy.ENERGY) {
//            if (energyContainerWrapper != null) {
//                energyContainerWrapper.updateFromNBT();
//                return (T) energyContainerWrapper;
//            }
//        }
//
//        if (capability == HeatCapability.HEAT) {
//            if (heatWrapper != null) {
//                heatWrapper.updateFromNBT();
//                return (T) heatWrapper;
//            }
//        }
//
//        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
//            if (chestPlateFluidHandler != null) {
//                chestPlateFluidHandler.updateFromNBT();
//                return (T) chestPlateFluidHandler;
//            }
//        }
//        return null;
//    }
//}
