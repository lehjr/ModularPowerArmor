package net.machinemuse.powersuits.item.module.environmental;

import net.machinemuse.numina.capabilities.module.powermodule.*;
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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidTankModule extends AbstractPowerModule {
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
            this.fluidHandler = new FluidHandlerTank(module, (int) moduleCap.applyPropertyModifierBaseInt(MPSConstants.FLUID_TANK_SIZE));
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> fluidHandler));
            return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
        }

        class FluidHandlerTank implements IFluidHandlerItem {
            static final String FLUID_NBT_KEY = "Fluid";

            @Nonnull
            protected ItemStack container;
            protected int capacity;

            /**
             * @param container The container itemStack, data is stored on it directly as NBT.
             * @param capacity  The maximum capacity of this fluid tank.
             */
            public FluidHandlerTank(@Nonnull ItemStack container, int capacity) {
                this.container = container;
                this.capacity = capacity;
            }

            @Nonnull
            @Override
            public ItemStack getContainer() {
                return container;
            }

            @Nullable
            public FluidStack getFluid() {
                CompoundNBT tagCompound = container.getTag();
                if (tagCompound == null || !tagCompound.contains(FLUID_NBT_KEY)) {
                    return null;
                }
                return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound(FLUID_NBT_KEY));
            }

            protected void setFluid(FluidStack fluid) {
                if (!container.hasTag()) {
                    container.setTag(new CompoundNBT());
                }

                CompoundNBT fluidTag = new CompoundNBT();
                fluid.writeToNBT(fluidTag);
                container.getTag().put(FLUID_NBT_KEY, fluidTag);
            }

            @Override
            public IFluidTankProperties[] getTankProperties() {
                return new FluidTankProperties[]{new FluidTankProperties(getFluid(), capacity)};
            }

            @Override
            public int fill(FluidStack resource, boolean doFill) {
                if (container.getCount() != 1 || resource == null || resource.amount <= 0 || !canFillFluidType(resource)) {
                    return 0;
                }

                FluidStack contained = getFluid();
                if (contained == null) {
                    int fillAmount = Math.min(capacity, resource.amount);

                    if (doFill) {
                        FluidStack filled = resource.copy();
                        filled.amount = fillAmount;
                        setFluid(filled);
                    }

                    return fillAmount;
                } else {
                    if (contained.isFluidEqual(resource)) {
                        int fillAmount = Math.min(capacity - contained.amount, resource.amount);

                        if (doFill && fillAmount > 0) {
                            contained.amount += fillAmount;
                            setFluid(contained);
                        }

                        return fillAmount;
                    }
                    return 0;
                }
            }

            @Override
            public FluidStack drain(FluidStack resource, boolean doDrain) {
                if (container.getCount() != 1 || resource == null || resource.amount <= 0 || !resource.isFluidEqual(getFluid())) {
                    return null;
                }
                return drain(resource.amount, doDrain);
            }

            @Override
            public FluidStack drain(int maxDrain, boolean doDrain) {
                if (container.getCount() != 1 || maxDrain <= 0) {
                    return null;
                }

                FluidStack contained = getFluid();
                if (contained == null || contained.amount <= 0 || !canDrainFluidType(contained)) {
                    return null;
                }

                final int drainAmount = Math.min(contained.amount, maxDrain);

                FluidStack drained = contained.copy();
                drained.amount = drainAmount;

                if (doDrain) {
                    contained.amount -= drainAmount;
                    if (contained.amount == 0) {
                        setContainerToEmpty();
                    } else {
                        setFluid(contained);
                    }
                }

                return drained;
            }

            public boolean canFillFluidType(FluidStack fluid) {
                return true;
            }

            public boolean canDrainFluidType(FluidStack fluid) {
                return true;
            }

            /**
             * Override this method for special handling.
             * Can be used to swap out or destroy the container.
             */
            protected void setContainerToEmpty() {
                container.getTag().remove(FLUID_NBT_KEY);
            }

            /**
             * Destroys the container item when it's emptied.
             */
            public class Consumable extends FluidHandlerItemStack {
                public Consumable(ItemStack container, int capacity) {
                    super(container, capacity);
                }

                @Override
                protected void setContainerToEmpty() {
                    super.setContainerToEmpty();
                    container.shrink(1);
                }
            }

            /**
             * Swaps the container item for a different one when it's emptied.
             */
            public class SwapEmpty extends FluidHandlerItemStack {
                protected final ItemStack emptyContainer;

                public SwapEmpty(ItemStack container, ItemStack emptyContainer, int capacity) {
                    super(container, capacity);
                    this.emptyContainer = emptyContainer;
                }

                @Override
                protected void setContainerToEmpty() {
                    super.setContainerToEmpty();
                    container = emptyContainer;
                }
            }
        }
    }
}