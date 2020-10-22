package com.github.lehjr.modularpowerarmor.item.module.energy.storage;

import com.github.lehjr.modularpowerarmor.basemod.MPAConstants;
import com.github.lehjr.modularpowerarmor.config.MPASettings;
import com.github.lehjr.modularpowerarmor.item.module.AbstractPowerModule;
import com.github.lehjr.mpalib.capabilities.energy.ForgeEnergyModuleWrapper;
import com.github.lehjr.mpalib.capabilities.energy.IEnergyWrapper;
import com.github.lehjr.mpalib.capabilities.module.powermodule.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyStorageModule extends AbstractPowerModule {
    protected final int maxEnergy;
    protected final int maxTransfer;

    public EnergyStorageModule(String regName, int maxEnergy, int maxTransfer) {
        super(regName);
        this.maxEnergy = maxEnergy;
        this.maxTransfer = maxTransfer;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapProvider(stack);
    }

    public class CapProvider implements ICapabilityProvider {
        ItemStack module;
        IPowerModule moduleCap;
        IEnergyWrapper energyStorage;

        public CapProvider(@Nonnull ItemStack module) {
            this.module = module;
            this.moduleCap = new PowerModule(module, EnumModuleCategory.ENERGY_STORAGE, EnumModuleTarget.ALLITEMS, MPASettings.getModuleConfig());
            this.moduleCap.addBaseProperty(MPAConstants.MAX_ENERGY, maxEnergy, "RF");
            this.moduleCap.addBaseProperty(MPAConstants.MAX_TRAMSFER, maxTransfer, "RF");
            this.energyStorage = new ForgeEnergyModuleWrapper(
                    module,
                    (int)moduleCap.applyPropertyModifiers(MPAConstants.MAX_ENERGY),
                    (int)moduleCap.applyPropertyModifiers(MPAConstants.MAX_TRAMSFER)
            );
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap == CapabilityEnergy.ENERGY) {
                energyStorage.updateFromNBT();
                return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(() -> energyStorage));
            }
            if (cap == PowerModuleCapability.POWER_MODULE) {
                return PowerModuleCapability.POWER_MODULE.orEmpty(cap, LazyOptional.of(() -> moduleCap));
            }
            return LazyOptional.empty();
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        System.out.println(playerIn.getHeldItem(handIn).serializeNBT());


        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

        @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        super.fillItemGroup(group, items);
        if (isInGroup(group)) {
            ItemStack out = new ItemStack(this);
            ForgeEnergyModuleWrapper energyStorage = new ForgeEnergyModuleWrapper(out, maxEnergy, maxTransfer);
            energyStorage.receiveEnergy(maxEnergy, false);
            items.add(out);
        }
    }


    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> energyCap.getMaxEnergyStored() > 0).orElse(false);
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> 1 - energyCap.getEnergyStored() / (double) energyCap.getMaxEnergyStored()).orElse(1D);
    }
}