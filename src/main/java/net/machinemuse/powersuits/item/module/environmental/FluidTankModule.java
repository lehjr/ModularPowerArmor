package net.machinemuse.powersuits.item.module.environmental;

import net.machinemuse.numina.capabilities.module.powermodule.*;
import net.machinemuse.numina.nbt.MuseNBTUtils;
import net.machinemuse.powersuits.basemod.MPSConfig;
import net.machinemuse.powersuits.basemod.MPSConstants;
import net.machinemuse.powersuits.item.module.AbstractPowerModule;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidTankModule extends AbstractPowerModule {
    static final String FLUID_NBT_KEY = "Fluid";
    public FluidTankModule(String regName) {
        super(regName);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;
        IFluidHandlerItem fluidHandler;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.CATEGORY_ENVIRONMENTAL, EnumModuleTarget.TORSOONLY, MPSConfig.INSTANCE);
            this.moduleCap.addBasePropertyInteger(MPSConstants.FLUID_TANK_SIZE, 20000);
            this.fluidHandler = new ModuleTank(moduleCap.applyPropertyModifierBaseInt(MPSConstants.FLUID_TANK_SIZE));
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> fluidHandler));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class ModuleTank extends FluidTank implements IFluidTank, IFluidHandler, IFluidHandlerItem {
            public ModuleTank(int capacity) {
                super(capacity);
            }

            @Nullable
            public FluidStack getFluid() {
                CompoundNBT tagCompound = MuseNBTUtils.getMuseModuleTag(module);
                if (tagCompound == null || !tagCompound.contains(FLUID_NBT_KEY)) {
                    return null;
                }
                return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound(FLUID_NBT_KEY));
            }

            @Override
            protected void onContentsChanged() {
                MuseNBTUtils.getMuseModuleTag(module).put(FLUID_NBT_KEY, writeToNBT(new CompoundNBT()));
            }

            @Nonnull
            @Override
            public ItemStack getContainer() {
                return module;
            }
        }
    }
}